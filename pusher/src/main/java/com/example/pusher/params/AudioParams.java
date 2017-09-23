package com.example.pusher.params;

/**
 * Created by Administrator on 2017/5/14.
 */

public class AudioParams implements Params {
    public int sampleRate = 16000;//todo音频降噪必须使用采样率16000 ,44100会产生杂音
    public int channels = 1;

    public AudioParams(int sampleRate, int channels) {
        this.sampleRate = sampleRate;
        this.channels = channels;
    }

    public AudioParams() {
    }
}
