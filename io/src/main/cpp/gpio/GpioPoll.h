#include <jni.h>
#include <poll.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include "android/log.h"

static const char *TAG = "[GpioPoll]";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

const static int TYPE_ERROR = -1;
const static int TYPE_EVENT = 1;

class GpioPoll {
public:
    GpioPoll(int number);
    virtual ~GpioPoll();

    int number = 0;
    int pollFd = 0;
    int pollRuning = false;
    jobject pollObject = NULL;
    jmethodID pollMethodID = NULL;
    pthread_t pollPthread = NULL;

    int start(JNIEnv *env, jobject thiz);
    int stop(JNIEnv *env, jobject thiz);
};