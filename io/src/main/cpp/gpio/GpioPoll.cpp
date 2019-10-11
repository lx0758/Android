#include <jni.h>
#include "Gpio.h"

#ifdef __cplusplus
extern "C" {
#endif

void callback(int type, int value);

Gpio *gpio;
JavaVM *jvm;
jobject object;
jmethodID methodID;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    jvm = vm;
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) return JNI_ERR;
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL Java_com_liux_android_io_gpio_Gpio_startPoll
        (JNIEnv *env, jobject jo, jint ji) {
    if (gpio != NULL) {
        gpio->gpio_stop_poll();
        gpio = NULL;
    }
    object = env->NewGlobalRef(jo);
    jclass clazz = env->GetObjectClass(object);
    methodID = env->GetMethodID(clazz, "onCallback", "(II)V");
    gpio = new Gpio(ji);
    gpio->gpio_start_poll(&callback);
}

JNIEXPORT void JNICALL Java_com_liux_android_io_gpio_Gpio_stopPoll
        (JNIEnv *env, jobject jo) {
    if (gpio != NULL) {
        gpio->gpio_stop_poll();
        gpio = NULL;
    }
    if (object != NULL) {
        env->DeleteGlobalRef(object);
        object = NULL;
    }
}

void callback(int type, int value) {
    JNIEnv *env;
    jvm->AttachCurrentThread(&env, NULL);
    env->CallVoidMethod(object, methodID, type, value);
    jvm->DetachCurrentThread();
}

#ifdef __cplusplus
}
#endif