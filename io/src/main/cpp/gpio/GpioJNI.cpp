#include "Gpio.h"
#include "../ClassBind.h"

#ifdef __cplusplus
extern "C" {
#endif

JavaVM *jvm;

Gpio * getGpio(JNIEnv *env, jobject thiz);
void setGpio(JNIEnv *env, jobject thiz, Gpio *gpio);

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    jvm = vm;
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) return JNI_ERR;
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL Java_com_liux_android_io_gpio_Gpio__1create(JNIEnv *env, jobject thiz) {
    SET(Gpio);
    GET(Gpio, gpio);
    jfieldID numberfid = env->GetFieldID(clazz, "number", "I");
    gpio->number = env->GetIntField(thiz, numberfid);
}

JNIEXPORT void JNICALL Java_com_liux_android_io_gpio_Gpio__1startPoll(JNIEnv *env, jobject thiz) {
    GET(Gpio, gpio);
    if (gpio != NULL) {
        gpio->gpio_start_poll(jvm, env, thiz);
    }
}

JNIEXPORT void JNICALL Java_com_liux_android_io_gpio_Gpio__1stopPoll(JNIEnv *env, jobject thiz) {
    GET(Gpio, gpio);
    if (gpio != NULL) {
        gpio->gpio_stop_poll(env, thiz);
    }
}

JNIEXPORT void JNICALL Java_com_liux_android_io_gpio_Gpio__1destroy(JNIEnv *env, jobject thiz) {
    GET(Gpio, gpio);
    if (gpio != NULL) {
        //do nothing
    }
    CLEAR(Gpio);
}

#ifdef __cplusplus
}
#endif