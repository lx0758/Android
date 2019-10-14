#include "GpioPoll.h"

void *pollRun(void *data);
void pollCallback(GpioPoll *gpio, const int type, int value);

GpioPoll::GpioPoll(int number) {
    this->number = number;
}

GpioPoll::~GpioPoll() {
    pollRuning = false;
    extern JavaVM *jvm;
    JNIEnv *env;
    jvm->AttachCurrentThread(&env, NULL);
    if (pollObject != NULL) {
        env->DeleteGlobalRef(pollObject);
        pollObject = NULL;
    }
    jvm->DetachCurrentThread();
    if (pollFd != 0) {
        close(pollFd);
        pollFd = 0;
    }
}

int GpioPoll::start(JNIEnv *env, jobject thiz) {
    stop(env, thiz);
    jclass clazz = env->GetObjectClass(thiz);

    pollRuning = true;
    pollObject = env->NewGlobalRef(thiz);
    pollMethodID = env->GetMethodID(clazz, "jniPollCallback", "(II)V");
    pthread_create(&pollPthread, NULL, pollRun, this);
    return 0;
}

int GpioPoll::stop(JNIEnv *env, jobject thiz) {
    pollRuning = false;
    return 0;
}

void *pollRun(void *data) {
    GpioPoll *gpioPoll = (GpioPoll*) data;
    char path[64];
    snprintf(path, sizeof(path), "/sys/class/gpio/gpio%d/value", gpioPoll->number);
    int fd = open(path, O_RDONLY);
    if (fd <= 0) {
        if (gpioPoll->pollRuning) pollCallback(gpioPoll, TYPE_ERROR, -1);
        close(fd);
        pthread_exit(&gpioPoll->pollPthread);
    }
    gpioPoll->pollFd = fd;

    int result = 0;
    int first = true;
    struct pollfd fds[1];
    fds[0].fd = gpioPoll->pollFd;
    fds[0].events = POLLPRI;
    while (gpioPoll->pollRuning) {
        result = poll(fds, 1, 1000);
        if (result < 0) {
            if (gpioPoll->pollRuning) pollCallback(gpioPoll, TYPE_ERROR, -1);
            break;
        }
        if (result == 0) continue;
        if (fds[0].revents & POLLPRI) {
            usleep(10 * 1000);
            char buffer[16];
            if (lseek(fd, 0, SEEK_SET) == -1 || read(fd, buffer, sizeof(buffer)) == -1) {
                if (gpioPoll->pollRuning) pollCallback(gpioPoll, TYPE_ERROR, -1);
                break;
            }
            if (first) {
                first = false;
                continue;
            }
            if (gpioPoll->pollRuning) pollCallback(gpioPoll, TYPE_EVENT, atoi(buffer));
        }
    }

    delete gpioPoll;

    pthread_exit(NULL);
}

void pollCallback(GpioPoll *gpio, const int type, int value) {
    if (gpio->pollObject != NULL) {
        extern JavaVM *jvm;
        JNIEnv *env;
        jvm->AttachCurrentThread(&env, NULL);
        if (gpio->pollObject != NULL) {
            env->CallVoidMethod(gpio->pollObject, gpio->pollMethodID, type, value);
        }
        jvm->DetachCurrentThread();
    }
}