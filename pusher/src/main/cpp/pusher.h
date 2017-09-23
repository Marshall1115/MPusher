//
// Created by Administrator on 2017/6/6.
//
#pragma once

#include "stdio.h"
#include "stdlib.h"
#include "iostream"
#include <string>
#include <android/log.h>
#include <queue>
#include <memory>

#define LOG_TAG "marshall"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" {
#include "librtmp/rtmp.h"
#include "librtmp/rtmp_sys.h"
#include <faac/faac.h>
#include <faac/faaccfg.h>
}

#include <iostream>

using namespace std;
#define TIMEOUT 5

class Pusher {
private:
    RTMP *rtmp;
    string url;
//
    queue<shared_ptr<RTMPPacket>> packets;

    pthread_mutex_t mut;
    pthread_cond_t has_product;

    int start_time;

    int init_rtmp();

    int connect_rtmp_server(string url);

public:
    queue<RTMPPacket *> packets2;

    Pusher(string url) {
        this->url = url;
        init();
    }

    friend void *send_packets(void *args);
    int connect_rtmp();
    ~Pusher() {
        release();
    }

    void initRtmpPacket(RTMPPacket **packet, uint32_t bodySoze);

    int init();

    void add_packet_sequence(RTMPPacket *packet);

    void send_video_data(uint8_t *data, int len, bool isKeyFrame);

    void send_video_sps_pps(uint8_t *sps, int spsLen, uint8_t *pps, int ppsLen);

    void release();


    void send_aac_header(faacEncHandle handler, int simpleRage, int channels);

    void send_aac_body(faacEncHandle handler, unsigned char *data, size_t len);

    faacEncHandle aacHandler;

};

