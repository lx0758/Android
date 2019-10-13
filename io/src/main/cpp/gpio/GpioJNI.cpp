#include "Gpio.h"

#ifdef __cplusplus
extern "C" {
#endif

JavaVM *jvm;

Gpio * getGpio(JNIEnv *env, jobject thiz);
void setGpio(JNIEnv *env, jobject thiz, struct Gpio *gpio);

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    jvm = vm;
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) return JNI_ERR;
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL Java_com_liux_android_io_gpio_Gpio__1create(JNIEnv *env, jobject thiz, jint number) {
    Gpio *gpio = new Gpio(number);
    setGpio(env, thiz, gpio);
}

JNIEXPORT void JNICALL Java_com_liux_android_io_gpio_Gpio__1startPoll(JNIEnv *env, jobject thiz) {
    Gpio *gpio = getGpio(env, thiz);
    if (gpio != NULL) {
        gpio->gpio_start_poll(jvm, env, thiz);
    }
}

JNIEXPORT void JNICALL Java_com_liux_android_io_gpio_Gpio__1stopPoll(JNIEnv *env, jobject thiz) {
    Gpio *gpio = getGpio(env, thiz);
    if (gpio != NULL) {
        gpio->gpio_stop_poll(env, thiz);
    }
}

JNIEXPORT void JNICALL Java_com_liux_android_io_gpio_Gpio__1destroy(JNIEnv *env, jobject thiz) {
    Gpio *gpio = getGpio(env, thiz);
    if (gpio != NULL) {
        //do nothing
    }
}

Gpio * getGpio(JNIEnv *env, jobject thiz) {
    jclass gpioClass = env->GetObjectClass(thiz);
    jfieldID handlerFieldID = env->GetFieldID(gpioClass, "handler", "J");
    long handler = (long) env->GetIntField(thiz, handlerFieldID);
    if (handler == 0) return NULL;
    return (Gpio *) handler;
}

void setGpio(JNIEnv *env, jobject thiz, Gpio *gpio) {
    jclass gpioClass = env->GetObjectClass(thiz);
    jfieldID handlerFieldID = env->GetFieldID(gpioClass, "handler", "J");
    env->SetIntField(thiz, handlerFieldID, (long) gpio);
}

#ifdef __cplusplus
}
#endif