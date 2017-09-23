//
// Created by Administrator on 2017/6/6.
//

#include "pusher.h"

int Pusher::init_rtmp() {
    rtmp = RTMP_Alloc();
    RTMP_Init(rtmp);
    //set connection timeout,default 30s
    rtmp->Link.timeout = TIMEOUT;
    return 1;
}

int Pusher::connect_rtmp_server(string url) {
    if (!RTMP_SetupURL(rtmp, (char *) url.c_str())) {
        LOGE("error:%s", "rtmp SetupURL Err");
        release();
        return -1;
    }
    RTMP_EnableWrite(rtmp);
    if (!RTMP_Connect(rtmp, NULL)) {
        LOGE("error:%s", "rtmp connect Err");
        release();
        return -2;
    }

    if (!RTMP_ConnectStream(rtmp, 0)) {
        LOGE("error:%s", "rtmp ConnectStream Err\n");
        release();
        RTMP_Close(rtmp);
        return -3;
    }
    return 1;
}

void Pusher::release() {
    RTMP_Free(rtmp);
}

/**
 */
void Pusher::initRtmpPacket(RTMPPacket **packet, uint32_t bodySoze) {
    *packet = (RTMPPacket *) malloc(sizeof(RTMPPacket));
    RTMPPacket_Alloc(*packet, bodySoze);
    RTMPPacket_Reset(*packet);
}

int Pusher::connect_rtmp() {
    if (init_rtmp()) {
        return -1;
    }
    if (connect_rtmp_server(url) < 0) {
        return -1;
    }
}

void *send_packets(void *args) {
    Pusher *pusher = (Pusher *) args;
    if (pusher->connect_rtmp() == -1) {
        LOGE("连接rmtp服务器失败");
        return NULL;
    }
    pusher->start_time = RTMP_GetTime();
    LOGI("开启线程 发送rtmp包数据");
    //瞬间就能把所有的数据发送出去，流媒体服务器是接受不了的  生产一条数据发送一条数据
    while (true) {
        pthread_mutex_lock(&pusher->mut);
        pthread_cond_wait(&pusher->has_product, &pusher->mut);

        RTMPPacket *rtmpPacket = pusher->packets2.front();
        if (rtmpPacket) {
            LOGI("RTMP_SendPacket ：m_nTimeStamp:%d  ;type:%d , isKeyFrame:%x",
                 rtmpPacket->m_nTimeStamp,
                 rtmpPacket->m_packetType, rtmpPacket->m_body[0]);
            if (!RTMP_SendPacket(pusher->rtmp, rtmpPacket, TRUE)) {
                LOGI("error:%s", "rtmp Send Error");
                RTMP_SendPacket(pusher->rtmp, rtmpPacket, TRUE);
            }
            RTMPPacket_Free(rtmpPacket);
        }
        pusher->packets2.pop();
        pthread_mutex_unlock(&pusher->mut);
    }
    return NULL;
}

int Pusher::init() {
    //初始化互斥锁
    pthread_mutex_init(&mut, NULL);
    pthread_cond_init(&has_product, NULL);
    //开启线程
    pthread_t tid = 0;
    pthread_create(&tid, NULL, send_packets, this);
    pthread_detach(tid);
    return 1;
}


void Pusher::add_packet_sequence(RTMPPacket *packet) {

    if (!RTMP_IsConnected(this->rtmp)) {
        LOGE("rtmp 连接失败");
        return;
    }
//    shared_ptr<RTMPPacket> shared_stuff(packet, free);

    LOGI("add_packet_sequence ：m_nTimeStamp:%d  ;type:%d , isKeyFrame:%x",
         packet->m_nTimeStamp,
         packet->m_packetType, packet->m_body[0]);
    pthread_mutex_lock(&mut);
    packets2.push(packet);
    pthread_cond_signal(&has_product);
    pthread_mutex_unlock(&mut);
}

void Pusher::send_video_sps_pps(uint8_t *sps, int spsLen, uint8_t *pps, int ppsLen) {
//    RTMPPacket *packet;
//    RTMPPacket *packet = NULL;
    int bodySize = spsLen + ppsLen +
                   16;//VideoTagHeader +AVCDecoderConfigurationRecord+当前nal占用的字节数长度（4个字节描述） =16
//    initRtmpPacket(&packet, bodySize);

    RTMPPacket *packet = (RTMPPacket *) malloc(sizeof(RTMPPacket));
    if (!packet) {
        return;
    }
    RTMPPacket_Reset(packet);
    if (!RTMPPacket_Alloc(packet, (uint32_t) bodySize)) {
        return;
    }

    char *body = packet->m_body;
    //配置VideoTagHeader
    int index = 0;
    body[index++] = 0x17;//Frame Type CodecID
    body[index++] = 0x00;//AVCPacketType
    //CompositionTime
    body[index++] = 0x00;
    body[index++] = 0x00;
    body[index++] = 0x00;
    //AVCDecoderConfigurationRecord
    body[index++] = 0x01;//configurationVersion
    body[index++] = sps[1];//AVCProfileIndication
    body[index++] = sps[2];//profile_compatibility
    body[index++] = sps[3];//AVCLevelIndication
    body[index++] = (char) 0xFF;// reserved 和lengthSizeMinusOne
//    body[index++]= (spsLen+sps )& 0x11;//reserved和numOfSequenceParameterSets 1111
//    body[index++]= 0x04;//
//    body[index++]= 0x04;//
    //reserved  numOfSequenceParameterSets(sequenceParameterSetLength+sequenceParameterSetNALUnit)


    // sps nums
    body[index++] = (char) 0xE1; //&0x1f  SPS 的个数，numOfSequenceParameterSets & 0x1F，实际测试时发现总为E1，计算结果为1
    // sps data length
    body[index++] = (char) (spsLen >> 8) & 0xff; //SPS 的长度，2个字节，计算结果49
    //todo 这是原来的代码
//    body[index++] = spsLen >> 8; //SPS 的长度，2个字节，计算结果49
    body[index++] = (char) spsLen & 0xff;
    //sequenceParameterSetNALUnits
    memcpy(&body[index], sps, spsLen);
    index += spsLen;

    // pps nums
    body[index++] = 0x01; //&0x1f
    // pps data length
    body[index++] = (char) (ppsLen >> 8) & 0xff;
    ////todo 这是原来的代码
//    body[index++] = ppsLen >> 8;
    body[index++] = (char) ppsLen & 0xff;
    // sps data
    memcpy(&body[index], pps, ppsLen);
    index += ppsLen;


    //配置nal占用的字节数长度
    //发送sps pps 不需要设置
//    body[index++] = len>>24;
//    body[index++] = len>>16;
//    body[index++] = len>>8;
//    body[index++] = len&0xff;;


    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nBodySize = bodySize;
    packet->m_hasAbsTimestamp = 0;//是否使用绝对时间戳，一般定义为0。

    /**
        #define STREAM_CHANNEL_METADATA  0x03
        #define STREAM_CHANNEL_VIDEO     0x04
        #define STREAM_CHANNEL_AUDIO     0x05
     */
    packet->m_nChannel = 0x04;//音视频通道号码，音视频不要写错

    packet->m_headerType = RTMP_PACKET_SIZE_MEDIUM;
    //一般视频时间戳可以从0开始计算，每帧时间戳 + 1000/fps (25fps每帧递增25；30fps递增33)
    //音频时间戳也可以从0开始计算，48K采样每帧递增21；44.1K采样每帧递增23。
    packet->m_nTimeStamp = 0;
//    packet->m_body = body;
//    packet->m_nInfoField2 = rtmp->m_stream_id;


    add_packet_sequence(packet);
}

void Pusher::send_video_data(uint8_t *data, int len, bool isKeyFrame) {
////    int type = data[0] & 0x05; //type = 5 i帧
////    LOGI("type:%x:", type);
////    RTMPPacket *packet;
    int bodySize = len + 9;
//    initRtmpPacket(&packet, bodySize);
    RTMPPacket *packet = (RTMPPacket *) malloc(sizeof(RTMPPacket));
    if (!packet) {
        return;
    }
    RTMPPacket_Reset(packet);
    if (!RTMPPacket_Alloc(packet, (uint32_t) bodySize)) {
        return;
    }

    char *body = packet->m_body;
    //配置VideoTagHeader
    int index = 0;
    body[index++] = isKeyFrame ? 0x17 : 0x27;//Frame Type CodecID

    body[index++] = 0x01;//AVCPacketType
    //CompositionTime
    body[index++] = 0x00;
    body[index++] = 0x00;
    body[index++] = 0x00;

    //todo  不加括号有问题， 无画面
//    body[index++] = (char) len >> 24 & 0xff;
//    body[index++] = (char) len >> 16 & 0xff;
//    body[index++] = (char) len >> 8 & 0xff;
//    body[index++] = (char) len & 0xff;
    //四个字节描述当前nal的长度 举例 0001 0000 0011 0000 0111 0000 0000 1111
    //提示 0xff 为一个字节 1111 1111
    body[index++] = (char) ((len >> 24) & 0xff);//得到0001 0000
    body[index++] = (char) ((len >> 16) & 0xff);//得到0011 0000
    body[index++] = (char) ((len >> 8) & 0xff);//得到0111 0000
    body[index++] = (char) (len & 0xff);//0000 1111

    //todo  memcpy(（void *）body[index++], data, len);
    memcpy(&body[index++], data, len);
    //第一个nal size （除去第一帧的界定符所以帧做为一个整体调用发送函数，它们的类型是由第一帧类型决定。）


    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nChannel = 0x04;//音视频通道号码，音视频不要写错
    packet->m_hasAbsTimestamp = 0;//是否使用绝对时间戳，一般定义为0。
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;//chunk type  fmt字段表示不同的类型包头长度不同
    //一般视频时间戳可以从0开始计算，每帧时间戳 + 1000/fps (25fps每帧递增25；30fps递增33)
    //音频时间戳也可以从0开始计算，48K采样每帧递增21；44.1K采样每帧递增23。
    packet->m_nTimeStamp = RTMP_GetTime() - start_time;
//    packet->m_nInfoField2 = rtmp->m_stream_id; //如果注释打开就会出现wireshark抓包：packet   Unknow（0x0）：
    packet->m_nBodySize = bodySize;
    add_packet_sequence(packet);
}

void Pusher::send_aac_header(faacEncHandle handler, int simpleRage, int channels) {
    RTMPPacket *packet;
    unsigned char *ppBuffer;//拼接好的AAC sequence header 的信息
    unsigned long pSizeOfDecoderSpecificInfo;//长度
    faacEncGetDecoderSpecificInfo(handler, &ppBuffer, &pSizeOfDecoderSpecificInfo);
    int bodySize = pSizeOfDecoderSpecificInfo + 2;
    initRtmpPacket(&packet, bodySize);
    char *body = packet->m_body;

    //这里的值是固定的0xAF、0×00，主要看后面AAC sequence header 的信息
//    if(channels ==2){//双声道
//        body[0] = 0xa7;
//    }else{
//        body[0] = 0xa6;
//    }

    body[0] = 0xAF;
    body[1] = 0x00;
    memcpy(&body[2], ppBuffer, pSizeOfDecoderSpecificInfo);
    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    packet->m_hasAbsTimestamp = 0;//是否使用绝对时间戳，一般定义为0。

    /**
        #define STREAM_CHANNEL_METADATA  0x03
        #define STREAM_CHANNEL_VIDEO     0x04
        #define STREAM_CHANNEL_AUDIO     0x05
     */
    packet->m_nChannel = 0x04;//音视频通道号码，音视频不要写错

    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    //一般视频时间戳可以从0开始计算，每帧时间戳 + 1000/fps (25fps每帧递增25；30fps递增33)
    //音频时间戳也可以从0开始计算，48K采样每帧递增21；44.1K采样每帧递增23。
    packet->m_nTimeStamp = 0;
    packet->m_nBodySize = bodySize;


    add_packet_sequence(packet);
}

void Pusher::send_aac_body(faacEncHandle handler, unsigned char *data, size_t data_len) {
    RTMPPacket *packet;
    int bodySize = data_len + 2;
    initRtmpPacket(&packet, bodySize);
    char *body = packet->m_body;
    body[0] = 0xAF;
    body[1] = 0x01;
    memcpy(&body[2], data, data_len);

    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    packet->m_hasAbsTimestamp = 0;//是否使用绝对时间戳，一般定义为0。
    packet->m_nChannel = 0x04;//音视频通道号码，音视频不要写错
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    //一般视频时间戳可以从0开始计算，每帧时间戳 + 1000/fps (25fps每帧递增25；30fps递增33)
    //音频时间戳也可以从0开始计算，48K采样每帧递增21；44.1K采样每帧递增23。
    packet->m_nTimeStamp = RTMP_GetTime() - start_time;
    packet->m_nBodySize = bodySize;
    add_packet_sequence(packet);
}


