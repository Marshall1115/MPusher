package com.example.pusher.pusher;


import android.os.Bundle;
import android.os.Message;

import com.example.pusher.params.AudioParams;
import com.example.pusher.utils.AudioHelper;
import com.example.pusher.utils.LiveHandler;
import com.example.pusher.utils.NativeUtils;

public class AudioPusher extends AbstractPusher<AudioParams> {
    private static final int AUDIO_DATA_MESSAGE = 2;
    private AudioHelper audioHelper;
    private LiveHandler liveHandler;

    @Override
    public void prepare() {
        audioHelper = new AudioHelper(params.sampleRate, params.channels);
        liveHandler = new LiveHandler();
        liveHandler.setHandleMessageListener(new LiveHandler.OnHandleMessageListener() {
            @Override
            public void handleMessage(Message msg) {
                int what = msg.what;
                if (what == AUDIO_DATA_MESSAGE) {
                    if (isPushing){
                        Bundle msgData = msg.getData();
                        NativeUtils.pushAudioData(msgData.getByteArray("frame_data"),msgData.getInt("dataLen"));
                    }
                }
            }
        });
        audioHelper.setOnCollectAudioDataListener(new AudioHelper.OnCollectAudioDataListener() {
            @Override
            public void onCollectAudioData(byte[] data, int len) {
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putByteArray("frame_data", data);
                bundle.putInt("dataLen", len);
                msg.what = AUDIO_DATA_MESSAGE;
                msg.setData(bundle);
                liveHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public void onStartPushStream() {
        audioHelper.startRecord();


    }

    @Override
    public void onStopPushStream() {
        audioHelper.release();
        liveHandler.release();
    }

    public void appay(AudioParams params) {
        super.appay(params);
        NativeUtils.audio_prepare(params.sampleRate, params.channels);
    }
}
