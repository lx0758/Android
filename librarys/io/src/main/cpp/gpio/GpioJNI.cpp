#include <string>
#include "GpioPoll.h"

using namespace std;
template<typename T> void set(JNIEnv *env, jobject thiz, T *t, string name){
    jclass clazz = env->GetObjectClass(thiz);
    jfieldID fid = env->GetFieldID(clazz, name.c_str(), "J");
    env->SetLongField(thiz, fid, (jlong) t);
}
template<typename T> T * get(JNIEnv *env, jobject thiz, string name){
    jclass clazz = env->GetObjectClass(thiz);
    jfieldID fid = env->GetFieldID(clazz, name.c_str(), "J");
    return (T *) env->GetLongField(thiz, fid);
}

#ifdef __cplusplus
extern "C" {
#endif

JavaVM *jvm;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    jvm = vm;
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) return JNI_ERR;
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL
Java_com_liux_android_io_gpio_Gpio_jniPollStart(JNIEnv *env, jobject thiz) {
    jclass clazz = env->GetObjectClass(thiz);
    jfieldID numberfid = env->GetFieldID(clazz, "number", "I");
    int number = env->GetIntField(thiz, numberfid);
    GpioPoll *gpioPoll = new GpioPoll(number);
    gpioPoll->start(env, thiz);
    set<GpioPoll>(env, thiz, gpioPoll, "pollHandler");
}

JNIEXPORT void JNICALL
Java_com_liux_android_io_gpio_Gpio_jniPollStop(JNIEnv *env, jobject thiz) {
    GpioPoll *gpioPoll = get<GpioPoll>(env, thiz, "pollHandler");
    if (gpioPoll != NULL) {
        gpioPoll->stop(env, thiz);
        set<GpioPoll>(env, thiz, NULL, "pollHandler");
    }
}

#ifdef __cplusplus
}
#endif