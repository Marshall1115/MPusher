package com.example.pusher.pusher;


import android.hardware.Camera;
import android.os.Bundle;
import android.os.Message;

import com.example.pusher.config.PusherConfig;
import com.example.pusher.params.VideoParams;
import com.example.pusher.utils.CameraHelper;
import com.example.pusher.utils.LiveHandler;
import com.example.pusher.utils.NativeUtils;

import java.io.IOException;

public class VideoPusher extends AbstractPusher<VideoParams> implements Camera.PreviewCallback {
    private LiveHandler liveHandler;
    private CameraHelper cameraHelper;
    private static final int VIDEO_DATA_MESSAGE = 1;

    public void appay(VideoParams params) {
        super.appay(params);
        NativeUtils.video_prepare(params.width, params.height,params.bps);
    }

    /**
     * @roseuid 59C35F9A03A2
     */
    public void prepare() {
        cameraHelper = new CameraHelper(PusherEnv.getApplicationContext(), params.cameraId);
        try {
            PusherConfig config = PusherEngine.getInstance().getConfig();
            cameraHelper.startPreview(config.getPreviewHolder(), params.width, params.height, this);
        } catch (IOException e) {
            e.printStackTrace();
            new RuntimeException();
        }
        liveHandler = new LiveHandler();
        liveHandler.setHandleMessageListener(new LiveHandler.OnHandleMessageListener() {
            @Override
            public void handleMessage(Message msg) {
                    int what = msg.what;
                    if (what == VIDEO_DATA_MESSAGE&&isPushing) {
                        NativeUtils.pushVideoData(msg.getData().getByteArray("frame_data"));
                    }
            }
        });
    }

    @Override
    protected void onStopPushStream() {
        cameraHelper.release();
        liveHandler.release();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        camera.addCallbackBuffer (data);
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putByteArray("frame_data", data);
        msg.what = VIDEO_DATA_MESSAGE;
        msg.setData(bundle);
        liveHandler.sendMessage(msg);
    }


}
