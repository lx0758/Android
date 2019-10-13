#include <poll.h>
#include <fcntl.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <pthread.h>
#include <stdbool.h>
#include <jni.h>

#include "android/log.h"

static const char *TAG = "[Gpio]";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

#ifndef ANDROID_DEMO_GPIO_H
#define ANDROID_DEMO_GPIO_H

const static char DIRECTION_IN = *"in";
const static char DIRECTION_OUT = *"out";
const static char EDGE_NONE = *"none";
const static char EDGE_RISING = *"resing";
const static char EDGE_FALLING = *"falling";
const static char EDGE_BOTH = *"both";
const static int TYPE_ERROR = -1;
const static int TYPE_EVENT = 1;

#endif //ANDROID_DEMO_GPIO_H

void *pollRun(void *data);

class Gpio {
public:
    int number = 0;

    //Gpio(int number);
    int gpio_export();
    int gpio_unexport();
    int gpio_direction(char direction);
    int gpio_edge(char edge);
    int gpio_write(int value);
    int gpio_read();
    int gpio_start_poll(JavaVM *vm, JNIEnv *env, jobject thiz);
    int gpio_stop_poll(JNIEnv *env, jobject thiz);

    int read_file(const char *path, char *buffer, int len);
    int write_file(const char *path, char *buffer, int len);

    int pollRuning = false;
    JavaVM *pollVM = NULL;
    jobject pollObject = NULL;
    jmethodID pollMethodID = NULL;
    pthread_t pollPthread = NULL;
    void pollCallback(const int type, int value);
};