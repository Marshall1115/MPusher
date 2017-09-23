#include "stdio.h"

extern "C" {
#include <faac/faac.h>
}

unsigned long nMaxOutputBytes = 0;//得到每次调用编码时生成的AAC数据的最大长度  调用faacEncEncode（）后输出缓冲区。
unsigned long nInputSamples = 0;//得到每次调用编码时所应接收的原始数据长度
int initFaac(int sampleRate, int channels, faacEncHandle *aacHandler) {
    // 打开faac编码器引擎
    *aacHandler = faacEncOpen(sampleRate, channels, &nInputSamples, &nMaxOutputBytes);
    if (aacHandler == NULL) {
        LOGI("%s", "打开faac编码器引擎失败!\n");
        return -1;
    }

    // 获取当前编码器信息
    faacEncConfigurationPtr pConfiguration = {0};
    pConfiguration = faacEncGetCurrentConfiguration(*aacHandler);

    // 设置编码配置信息
    /*
        PCM Sample Input Format
        0   FAAC_INPUT_NULL         invalid, signifies a misconfigured config
        1   FAAC_INPUT_16BIT        native endian 16bit
        2   FAAC_INPUT_24BIT        native endian 24bit in 24 bits      (not implemented)
        3   FAAC_INPUT_32BIT        native endian 24bit in 32 bits      (DEFAULT)
        4   FAAC_INPUT_FLOAT        32bit floating point
    */
    pConfiguration->inputFormat = FAAC_INPUT_16BIT;

    // 0 = Raw; 1 = ADTS todo
    pConfiguration->outputFormat = 0;

    // AAC object types
    //#define MAIN 1  增益控制 音质好
    //#define LOW  2  LC 没有了增益控制 提高了编码效
    //#define SSR  3 增益控制
    //#define LTP  4 增益控制
    pConfiguration->aacObjectType = LOW;
    pConfiguration->useLfe = 0;
    pConfiguration->bitRate = 0;
    pConfiguration->bandWidth = 0; //频宽 取值：0， 32000，64000都可以，暂时不清楚参数作用
    pConfiguration->useTns = 1;//抗噪
//    pConfiguration->shortctl
    pConfiguration->allowMidside = 0;// 是否使用mid/side编码 midside midside为立体声调整方式 重建一個人耳聽到實際樂器的感覺.
    // 其他的参数不知道怎么配置，毕竟对音频不熟
    // 不过当前的设置可以实现转换，不过声音好像有一丢丢怪异
    // 这一块的配置信息很重要，错了会导致转码失败，然后你以为代码其他地方错了

    // 重置编码器的配置信息
    faacEncSetConfiguration(*aacHandler, pConfiguration);

    LOGI("%s", "初始|化faac编码器引擎成功!");
    return 1;
}

