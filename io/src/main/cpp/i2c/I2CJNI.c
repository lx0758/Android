#include <jni.h>
#include <linux/i2c.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jobject JNICALL
Java_com_liux_android_io_i2c_I2C__1open(JNIEnv *env, jclass jclazz, jstring path) {
    // TODO: implement _open()
}

JNIEXPORT jint JNICALL
Java_com_liux_android_io_i2c_I2C__1read(JNIEnv *env, jclass jclazz, jint i2c_adr,
                                        jbyteArray buffer) {
    // TODO: implement _read()
}

JNIEXPORT jint JNICALL
Java_com_liux_android_io_i2c_I2C__1write(JNIEnv *env, jclass jclazz, jint i2c_adr, jint sub_adr,
                                         jbyteArray data) {
    // TODO: implement _write()
}

JNIEXPORT void JNICALL
Java_com_liux_android_io_i2c_I2C__1close(JNIEnv *env, jclass jclazz, jobject fd) {
    // TODO: implement _close()
}

#ifdef __cplusplus
}
#endif
