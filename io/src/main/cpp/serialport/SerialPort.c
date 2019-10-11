/*
 * Copyright 2009-2011 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>
#include <strings.h>

#include "android/log.h"

static const char *TAG = "[SerialPort]";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

static speed_t getBaudrate(jint baudrate) {
    switch (baudrate) {
        case 0:
            return B0;
        case 50:
            return B50;
        case 75:
            return B75;
        case 110:
            return B110;
        case 134:
            return B134;
        case 150:
            return B150;
        case 200:
            return B200;
        case 300:
            return B300;
        case 600:
            return B600;
        case 1200:
            return B1200;
        case 1800:
            return B1800;
        case 2400:
            return B2400;
        case 4800:
            return B4800;
        case 9600:
            return B9600;
        case 19200:
            return B19200;
        case 38400:
            return B38400;
        case 57600:
            return B57600;
        case 115200:
            return B115200;
        case 230400:
            return B230400;
        case 460800:
            return B460800;
        case 500000:
            return B500000;
        case 576000:
            return B576000;
        case 921600:
            return B921600;
        case 1000000:
            return B1000000;
        case 1152000:
            return B1152000;
        case 1500000:
            return B1500000;
        case 2000000:
            return B2000000;
        case 2500000:
            return B2500000;
        case 3000000:
            return B3000000;
        case 3500000:
            return B3500000;
        case 4000000:
            return B4000000;
        default:
            return -1;
    }
}

int set_opt(struct termios attr, jint databit, jint stopbit, jchar checkbit) {
    LOGD("set_opt: databit=%d, stopbit=%d, checkbit=%c", databit, stopbit, checkbit);

    bzero(&attr, sizeof(attr));

    // c_cflag标志可以定义CLOCAL和CREAD，这将确保该程序不被其他端口控制和信号干扰
    // 同时串口驱动将读取进入的数据。CLOCAL和CREAD通常总是被是能的
    attr.c_cflag |= CLOCAL | CREAD;

    // 设置数据位数
    switch (databit){
        case 5:
            attr.c_cflag &= ~CSIZE;
            attr.c_cflag |= CS5;
            LOGD("set data bit 5");
            break;
        case 6:
            attr.c_cflag &= ~CSIZE;
            attr.c_cflag |= CS6;
            LOGD("set data bit 6");
            break;
        case 7:
            attr.c_cflag &= ~CSIZE;
            attr.c_cflag |= CS7;
            LOGD("set data bit 7");
            break;
        case 8:
            attr.c_cflag &= ~CSIZE;
            attr.c_cflag |= CS8;
            LOGD("set data bit 8");
            break;
        default:
            attr.c_cflag &= ~CSIZE;
            attr.c_cflag |= CS8;
            LOGD("set data bit 8 [default]");
            break;
    }

    // 设置停止位
    switch (stopbit){
        case 1:
            attr.c_cflag &= ~CSTOPB;
            LOGD("set stop bit 1");
            break;
        case 2:
            attr.c_cflag |= CSTOPB;
            LOGD("set stop bit 2");
            break;
        default:
            attr.c_cflag &= ~CSTOPB;
            LOGD("set stop bit 1 [default]");
            break;
    }

    // 设置校验位
    switch (checkbit){
        case 'o':
        case 'O':
            attr.c_cflag |= (PARODD | PARENB);
            attr.c_iflag |= INPCK;
            LOGD("set check digit O");
            break;
        case 'e':
        case 'E':
            attr.c_cflag |= PARENB;
            attr.c_cflag &= ~PARODD;
            attr.c_iflag |= INPCK;
            LOGD("set check digit E");
            break;
        case 'n':
        case 'N':
            attr.c_cflag &= ~PARENB;
            LOGD("set check digit N");
            break;
        case 's':
        case 'S':
            LOGD("set check digit S");
            break;
        default:
            LOGD("set check digit S [default]");
            break;
    }

    attr.c_cc[VTIME] = 1;//设置等待时间(百毫秒)
    attr.c_cc[VMIN] = 0;//设置最小接收字符
}

JNIEXPORT jobject JNICALL Java_com_liux_android_io_serialport_SerialPort__1open
        (JNIEnv *env, jclass thiz, jstring path, jint baudrate, jint databit, jint stopbit,
         jchar checkbit) {
    int fd;
    speed_t speed;
    jobject mFileDescriptor;

    /* Check arguments */
    {
        speed = getBaudrate(baudrate);
        if (speed == -1) {
            /* TODO: throw an exception */
            LOGE("Invalid baudrate");
            return NULL;
        }
    }

    /* Opening device */
    {
        jint flags = 0;
        jboolean iscopy;
        const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
        LOGD("Opening serial port %s with flags 0x%x", path_utf, O_RDWR | flags);
        fd = open(path_utf, O_RDWR | flags);
        LOGD("open() fd = %d", fd);
        (*env)->ReleaseStringUTFChars(env, path, path_utf);
        if (fd == -1) {
            /* Throw an exception */
            LOGE("Cannot open port");
            /* TODO: throw an exception */
            return NULL;
        }
    }

    /* Configure device */
    {
        struct termios cfg;
        LOGD("Configuring serial port");
        if (tcgetattr(fd, &cfg)) {
            LOGE("tcgetattr() failed");
            close(fd);
            /* TODO: throw an exception */
            return NULL;
        }

        set_opt(cfg, databit, stopbit, checkbit);
        tcflush(fd, TCIFLUSH);

        cfmakeraw(&cfg);
        cfsetispeed(&cfg, speed);
        cfsetospeed(&cfg, speed);

        if (tcsetattr(fd, TCSANOW, &cfg)) {
            LOGE("tcsetattr() failed");
            close(fd);
            /* TODO: throw an exception */
            return NULL;
        }
    }

    /* Create a corresponding file descriptor */
    {
        jclass cFileDescriptor = (*env)->FindClass(env, "java/io/FileDescriptor");
        jmethodID iFileDescriptor = (*env)->GetMethodID(env, cFileDescriptor, "<init>", "()V");
        jfieldID descriptorID = (*env)->GetFieldID(env, cFileDescriptor, "descriptor", "I");
        mFileDescriptor = (*env)->NewObject(env, cFileDescriptor, iFileDescriptor);
        (*env)->SetIntField(env, mFileDescriptor, descriptorID, (jint) fd);
    }

    return mFileDescriptor;
}

//JNIEXPORT void JNICALL Java_com_liux_android_io_serialport_SerialPort__1close
//        (JNIEnv *env, jobject thiz) {
//    jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
//    jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");
//
//    jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd", "Ljava/io/FileDescriptor;");
//    jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass, "descriptor", "I");
//
//    jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
//    jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);
//
//    LOGD("close(fd = %d)", descriptor);
//    close(descriptor);
//}

JNIEXPORT void JNICALL
Java_com_liux_android_io_serialport_SerialPort__1close(JNIEnv *env, jobject thiz, jobject fd) {
    jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");
    jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass, "descriptor", "I");
    jint descriptor = (*env)->GetIntField(env, fd, descriptorID);
    LOGD("close(fd = %d)", descriptor);
    close(descriptor);
}