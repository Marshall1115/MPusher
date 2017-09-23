package com.example.pusher.params;

import android.hardware.Camera;

/**
 * Created by Administrator on 2017/5/14.
 */

public class VideoParams implements Params {
    public int height = 480;
    public int width = 800;
    public int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;//前置摄像头;
    public int bps =  700000;

    public VideoParams() {
    }

    public VideoParams(int height, int width, int cameraId) {
        this.height = height;
        this.width = width;
        this.cameraId = cameraId;
    }
}
