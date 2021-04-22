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

#ifdef __cplusplus
extern "C" {
#endif

static const char *TAG = "[SerialPort]";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

static speed_t getBaudRate(jint baudRate) {
    switch (baudRate) {
        case 0: return B0;
        case 50: return B50;
        case 75: return B75;
        case 110: return B110;
        case 134: return B134;
        case 150: return B150;
        case 200: return B200;
        case 300: return B300;
        case 600: return B600;
        case 1200: return B1200;
        case 1800: return B1800;
        case 2400: return B2400;
        case 4800: return B4800;
        case 9600: return B9600;
        case 19200: return B19200;
        case 38400: return B38400;
        case 57600: return B57600;
        case 115200: return B115200;
        case 230400: return B230400;
        case 460800: return B460800;
        case 500000: return B500000;
        case 576000: return B576000;
        case 921600: return B921600;
        case 1000000: return B1000000;
        case 1152000: return B1152000;
        case 1500000: return B1500000;
        case 2000000: return B2000000;
        case 2500000: return B2500000;
        case 3000000: return B3000000;
        case 3500000: return B3500000;
        case 4000000: return B4000000;
        default: return -1;
    }
}

JNIEXPORT jobject JNICALL Java_com_liux_android_io_serialport_SerialPort_jniOpen
        (JNIEnv *env, jclass jclazz, jstring path, jint baudRate, jint dataBit, jint stopBit, jchar parity) {
    int fd;
    speed_t speed;
    jobject fileDescriptor;

    /* Check arguments */
    {
        speed = getBaudRate(baudRate);
        if (speed == -1) {
            LOGE("Invalid baudRate");
            return NULL;
        }
        if (dataBit < 5 || dataBit > 8) {
            LOGE("Invalid dataBit");
            return NULL;
        }
        if (stopBit < 1 || stopBit > 2) {
            LOGE("Invalid stopBit");
            return NULL;
        }
    }

    /* Opening device */
    {
        jboolean isCopy;
        const char *localPath = (*env)->GetStringUTFChars(env, path, &isCopy);
        int flags = O_RDWR | O_NOCTTY;
        LOGD("Opening serial port %s with flags 0x%x", localPath, flags);
        fd = open(localPath, flags);
        LOGD("open() fd = %d", fd);
        (*env)->ReleaseStringUTFChars(env, path, localPath);
        if (fd == -1) {
            LOGE("Cannot open port");
            return NULL;
        }
    }

    /* Configure device */
    {
        struct termios option;

        LOGD("Configuring serial port");
        if (tcgetattr(fd, &option)) {
            LOGE("tcgetattr() failed");
            close(fd);
            return NULL;
        }

        // cfmakeraw(&option);
        option.c_iflag &= ~(IGNBRK|BRKINT|PARMRK|ISTRIP|INLCR|IGNCR|ICRNL|IXON);
        option.c_oflag &= ~OPOST;
        option.c_lflag &= ~(ECHO|ECHONL|ICANON|ISIG|IEXTEN);
        option.c_cflag &= ~(CSIZE|PARENB);

        cfsetispeed(&option, speed);
        cfsetospeed(&option, speed);

        // 设置数据位数
        switch (dataBit) {
            case 5:
                option.c_cflag |= CS5;
                LOGD("set dataBit is 5");
                break;
            case 6:
                option.c_cflag |= CS6;
                LOGD("set dataBit is 6");
                break;
            case 7:
                option.c_cflag |= CS7;
                LOGD("set dataBit is 7");
                break;
            case 8:
                option.c_cflag |= CS8;
                LOGD("set dataBit is 8");
                break;
            default:
                option.c_cflag |= CS8;
                LOGD("set dataBit is 8 [default]");
                break;
        }
        // 设置停止位
        switch (stopBit) {
            case 1:
                option.c_cflag &= ~CSTOPB;
                LOGD("set stopBit is 1");
                break;
            case 2:
                option.c_cflag |= CSTOPB;
                LOGD("set stopBit is 2");
                break;
            default:
                option.c_cflag &= ~CSTOPB;
                LOGD("set stopBit is 1 [default]");
                break;
        }
        // 设置校验位
        switch (parity) {
            case 'o':
            case 'O':
                option.c_cflag |= PARENB;
                option.c_cflag |= PARODD;
                LOGD("set parity is O");
                break;
            case 'e':
            case 'E':
                option.c_cflag |= PARENB;
                option.c_cflag &= ~PARODD;
                LOGD("set parity is E");
                break;
            case 'n':
            case 'N':
                option.c_cflag &= ~PARENB;
                LOGD("set parity is N");
                break;
            default:
                option.c_cflag &= ~PARENB;
                LOGD("set parity is N [default]");
                break;
        }
        // 超时设置
        option.c_cc[VTIME] = 0;
        option.c_cc[VMIN] = 0;

        if (tcsetattr(fd, TCSANOW, &option)) {
            LOGE("tcsetattr() failed");
            close(fd);
            return NULL;
        }
    }

    /* Create a corresponding file descriptor */
    {
        jclass cFileDescriptor = (*env)->FindClass(env, "java/io/FileDescriptor");
        jmethodID iFileDescriptor = (*env)->GetMethodID(env, cFileDescriptor, "<init>", "()V");
        jfieldID descriptorID = (*env)->GetFieldID(env, cFileDescriptor, "descriptor", "I");
        fileDescriptor = (*env)->NewObject(env, cFileDescriptor, iFileDescriptor);
        (*env)->SetIntField(env, fileDescriptor, descriptorID, (jint) fd);
    }

    return fileDescriptor;
}

JNIEXPORT void JNICALL
Java_com_liux_android_io_serialport_SerialPort_jniClose(JNIEnv *env, jclass jclazz, jobject fd) {
    jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");
    jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass, "descriptor", "I");
    jint descriptor = (*env)->GetIntField(env, fd, descriptorID);
    LOGD("close(fd = %d)", descriptor);
    close(descriptor);
}

#ifdef __cplusplus
}
#endif
