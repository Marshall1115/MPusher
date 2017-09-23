package com.example.administrator.mpush.view;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Administrator on 2017/5/14.
 */

public class CameraView extends SurfaceView {
    private Camera mCamera;

    public CameraView(Context context) {
        super(context,null,0);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context,attrs,0);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // deprecated setting, but required on Android versions prior to 3.0
        this.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
}
