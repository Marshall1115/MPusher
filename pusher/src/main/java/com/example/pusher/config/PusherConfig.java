package com.example.pusher.config;


import android.view.SurfaceHolder;

import com.example.pusher.params.AudioParams;
import com.example.pusher.params.VideoParams;
import com.example.pusher.pusher.PusherEngine;

public class PusherConfig {
    private VideoParams videoParams = new VideoParams();
    private AudioParams audioParams = new AudioParams();
    private SurfaceHolder previewHolder;

    /**
     * 返回一个带有默认设置的VideoParams
     *
     * @return
     */
    public VideoParams getVideoParams() {
        return videoParams;
    }

    /**
     * 返回一个带有默认设置的AudioParams
     *
     * @return
     */
    public AudioParams getAudioParams() {
        return audioParams;
    }

    public SurfaceHolder getPreviewHolder() {
        return previewHolder;
    }

    public static class Builder {
        PusherConfig config;

        /**
         * @roseuid 59C35F9A0300
         */
        public Builder() {
            config = new PusherConfig();
        }

        public Builder setAudioParams(AudioParams params) {
            config.audioParams = params;
            return this;
        }

        public Builder setVideoParams(VideoParams params) {
            config.videoParams = params;
            return this;
        }

        /**
         * @return PusherEngine
         * @roseuid 59C27A470242
         */
        public PusherEngine build() {
            return PusherEngine.getInstance(config);
        }

        /**
         * @return PusherConfig
         * @roseuid 59C27B4A032D
         */
        public Builder setDispalyViewHolder(SurfaceHolder holder) {
            config.previewHolder = holder;
            return this;
        }
    }
}
