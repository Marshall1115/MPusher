package com.example.pusher.pusher;//Source file: D:\\360Downloads\\PusherEngine.java


import com.example.pusher.config.PusherConfig;
import com.example.pusher.params.AudioParams;
import com.example.pusher.params.VideoParams;
import com.example.pusher.pusher.impl.Pusher;

public class PusherEngine implements Pusher {
    private boolean isPushing;
    private VideoPusher vp = new VideoPusher();
    private AudioPusher ap = new AudioPusher();
    //单例对象
    private static PusherEngine mInstance;
    private PusherConfig config;

    public Boolean isPushing() {
        return isPushing;
    }

    private PusherEngine(PusherConfig config) {
        this.config = config;
        init(config);
    }

    private void init(PusherConfig config) {

        VideoParams videoParams = config.getVideoParams();
        AudioParams audioParams = config.getAudioParams();
        vp.appay(videoParams);
        ap.appay(audioParams);
    }

    public PusherConfig getConfig() {
        return config;
    }

    public void setConfig(PusherConfig config) {
        this.config = config;
    }

    /**
     * 获取单例方法
     * 第一次调用
     *
     * @param config
     * @return
     */
    public static PusherEngine getInstance(PusherConfig config) {
        if (mInstance == null) {
            synchronized (PusherEngine.class) {
                if (mInstance == null) {
                    mInstance = new PusherEngine(config);
                }
            }
        }
        return mInstance;
    }

    /**
     * 第二次获取单例
     *
     * @return
     */
    public static PusherEngine getInstance() {
        if (mInstance == null) {
            throw new UnsupportedOperationException("请初始化PusherConfig");
        }
        return mInstance;
    }

    public void prepare() {
        vp.prepare();
        ap.prepare();
    }

    /**
     * @roseuid 59C35F9A0344
     */
    public void startPushStream() {
        isPushing =true;
        vp.startPushStream();
        ap.startPushStream();
    }

    /**
     * @roseuid 59C35F9A0356
     */
    public void stopPushStream() {
        isPushing =false;
        ap.stopPushStream();
        vp.stopPushStream();
    }
}
