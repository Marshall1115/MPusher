#include <jni.h>
#include <string>
#include <jni.h>
#include "stdio.h"
#include "stdlib.h"
#include "pusher.h"
#include <android/log.h>
#include "live_native.h"

#define LOG_TAG "JNI_LOG"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

#include "pusher.h"
#include "x264Utils.cpp"
#include "FaacUtils.cpp"

extern "C" {
#include <x264.h>
#include <sys/time.h>
#include <sys/resource.h>
#include <faac/faac.h>
#include <faac/faaccfg.h>
#include "libyuv.h"
}

class Context {
public:
    shared_ptr<Pusher> pusher;
    x264_param_t param;
    x264_t *x264_handler;
    faacEncHandle faac_handler;
} context;


void init(JNIEnv *env, jobject obj, jobject _context, jstring _rtmp_rul) {
    const char *rtmpUrl = env->GetStringUTFChars(_rtmp_rul, 0);
    context.pusher = make_shared<Pusher>(rtmpUrl);
    env->ReleaseStringUTFChars(_rtmp_rul, rtmpUrl);
}

void video_prepare(JNIEnv *env, jobject obj, jint width, jint height, jint bps) {
    if (!initX264(width, height, bps, &context.param, &context.x264_handler) < 0) {
        LOGE("打开x264编码器失败");
    }else{
        LOGI("打开x264编码器成功");
    }
}

void audio_prepare(JNIEnv *env, jobject obj, jint sampleRate,
                   jint channels) {
    if (initFaac(sampleRate, channels, &context.faac_handler) < 0) {
        LOGE("打开faac编码器失败");
    } else {
        LOGI("打开faac编码器成功");
    }
    //发送音频头
}

void pushVideoData(JNIEnv *env, jobject obj, jbyteArray frame_datas_) {
}

void pushAudioData(JNIEnv *env, jobject obj, jbyteArray frame_datas_,
                   jint dataLen) {
}