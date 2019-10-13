#include "Gpio.h"

//Gpio::Gpio(int number) {
//    this->number = number;
//}

int Gpio::gpio_export() {
    char buffer[64];
    int len = snprintf(buffer, sizeof(buffer), "%d", number);
    int result = write_file("/sys/class/gpio/export", buffer, len);
    if (result != 0) return -1;
    return 0;
}

int Gpio::gpio_unexport() {
    char buffer[64];
    int len = snprintf(buffer, sizeof(buffer), "%d", number);
    int result = write_file("/sys/class/gpio/unexport", buffer, len);
    if (result != 0) return -1;
    return 0;
}

int Gpio::gpio_direction(char direction) {
    char path[64];
    snprintf(path, sizeof(path), "/sys/class/gpio/gpio%d/direction", number);
    int result = write_file(path, &direction, sizeof(direction));
    if (result != 0) return -1;
    return 0;
}

int Gpio::gpio_edge(char edge) {
    char path[64];
    snprintf(path, sizeof(path), "/sys/class/gpio/gpio%d/edge", number);
    int result = write_file(path, &edge, sizeof(edge));
    if (result != 0) return -1;
    return 0;
}

int Gpio::gpio_write(int value) {
    char path[64];
    snprintf(path, sizeof(path), "/sys/class/gpio/gpio%d/value", number);
    char buffer[1];
    int len = snprintf(buffer, sizeof(buffer), "%d", value);
    int result = write_file(path, buffer, len);
    if (result != 0) return -1;
    return 0;
}

int Gpio::gpio_read() {
    char path[64];
    snprintf(path, sizeof(path), "/sys/class/gpio/gpio%d/value", number);
    char buffer[64];
    int result = read_file(path, buffer, 64);
    if (result != 0) return -1;
    return atoi(buffer);
}

int Gpio::gpio_start_poll(JavaVM *vm, JNIEnv *env, jobject thiz) {
    gpio_stop_poll(env, thiz);
    jclass clazz = env->GetObjectClass(thiz);

    pollVM = vm;
    pollRuning = true;
    pollObject = env->NewGlobalRef(thiz);
    pollMethodID = env->GetMethodID(clazz, "_onCallback", "(II)V");
    pthread_create(&pollPthread, NULL, pollRun, this);
    return 0;
}

int Gpio::gpio_stop_poll(JNIEnv *env, jobject thiz) {
    pollRuning = false;
    if (pollObject != NULL) {
        env->DeleteGlobalRef(pollObject);
        pollObject = NULL;
    }
    return 0;
}

int Gpio::read_file(const char *path, char *buffer, int len) {
    int fd = open(path, O_RDONLY);
    if (fd < 0) return -1;
    if (read(fd, buffer, len) < 0) return -1;
    close(fd);
    return 0;
}

int Gpio::write_file(const char *path, char *buffer, int len) {
    int fd = open(path, O_WRONLY);
    if (fd < 0) return -1;
    if (write(fd, buffer, len) < 0) return -1;
    close(fd);
    return 0;
}

void Gpio::pollCallback(const int type, int value) {
    JNIEnv *env;
    pollVM->AttachCurrentThread(&env, NULL);
    env->CallVoidMethod(pollObject, pollMethodID, type, value);
    pollVM->DetachCurrentThread();
}

void *pollRun(void *data) {
    Gpio *gpio = (Gpio*) data;
    char path[64];
    snprintf(path, sizeof(path), "/sys/class/gpio/gpio%d/value", gpio->number);
    int fd = open(path, O_RDONLY);
    if (fd <= 0) {
        gpio->pollCallback(TYPE_ERROR, -1);
        close(fd);
        pthread_exit(&gpio->pollPthread);
    }

    int result = 0;
    int first = true;
    struct pollfd fds[1];
    fds[0].fd = fd;
    fds[0].events = POLLPRI;
    while (gpio->pollRuning) {
        result = poll(fds, 1, 1000);
        if (result < 0) {
            if (gpio->pollRuning) gpio->pollCallback(TYPE_ERROR, -1);
            break;
        }
        if (result == 0) continue;
        if (fds[0].revents & POLLPRI) {
            usleep(10 * 1000);
            char buffer[16];
            if (lseek(fd, 0, SEEK_SET) == -1 || read(fd, buffer, sizeof(buffer)) == -1) {
                if (gpio->pollRuning) gpio->pollCallback(TYPE_ERROR, -1);
                break;
            }
            if (first) {
                first = false;
                continue;
            }
            if (gpio->pollRuning) gpio->pollCallback(TYPE_EVENT, atoi(buffer));
        }
    }

    close(fd);
    pthread_exit(&gpio->pollPthread);
}