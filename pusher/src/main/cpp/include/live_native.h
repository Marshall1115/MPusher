#include <jni.h>
#include <string>
#include <x264.h>
#include <jni.h>
#include "stdio.h"
#include "stdlib.h"
#include "../pusher.h"
#include <android/log.h>

void init(JNIEnv *env,jobject obj,jobject context, jstring _rtmp_rul);

void video_prepare(JNIEnv *env,jobject obj,jint width, jint height,jint bps);

void audio_prepare(JNIEnv *env,jobject obj,jint sampleRate,
                   jint channels);

void pushVideoData(JNIEnv *env,jobject obj,jbyteArray frame_datas_);

void pushAudioData(JNIEnv *env,jobject obj,jbyteArray frame_datas_,
                   jint dataLen);