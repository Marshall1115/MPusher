//
// Created by Administrator on 2017/9/22.
//
#include <JNILoad.h>
#include "JNIUtil.h"
#include "live_native.h"

typedef int (*RegisterFunc)(JNIEnv *env);

int RegisterAllNativeMethods(JNIEnv *env);
int RegisterMethods(JNIEnv *env);
RegisterFunc g_funcs[] = {RegisterMethods};
const char RegisterClassName[] = "com/example/pusher/utils/NativeUtils";
static JNINativeMethod RegisterNativeMethods[] =
        {
                {"init",          "(Landroid/content/Context;Ljava/lang/String;)V", (void *) init},
                {"video_prepare", "(III)V",                        (void *) video_prepare},
                {"audio_prepare", "(II)V",                        (void *) audio_prepare},
                {"pushVideoData", "([B)V",                        (void *) pushVideoData},
                {"pushAudioData", "([BI)V",                       (void *) pushAudioData}};

int RegisterMethods(JNIEnv *env) {
    return jniRegisterNativeMethods(env, RegisterClassName, RegisterNativeMethods,
                                    sizeof(RegisterNativeMethods) / sizeof(JNINativeMethod));
}

int RegisterAllNativeMethods(JNIEnv *env) {
    //注册所有类的native函数
    int nRet = JNI_ERR;
    for (int i = 0; i < sizeof(g_funcs) / sizeof(RegisterFunc); i++) {
        nRet = g_funcs[i](env);
        if (nRet != JNI_OK)
            break;
    }
    return nRet;
}
