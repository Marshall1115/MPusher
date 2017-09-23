package com.example.pusher.utils;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import java.io.IOException;

/**
 * Created by Administrator on 2017/9/21.
 */

public class CameraHelper implements SurfaceHolder.Callback {

    private Context context;
    private Camera mCamera;
    private int cameraId;
    private int width;
    private int height;
    private Camera.PreviewCallback cb;

    public CameraHelper(Context context, int cameraId) {
        this.context = context;
        this.cameraId = cameraId;
        mCamera = getCameraInstance(cameraId);
    }

    private static Camera getCameraInstance(int cameraId) {
        Camera c = null;
        try {
            c = Camera.open(cameraId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(" camera open failure");
        }
        return c;
    }


    public void startPreview(SurfaceHolder previewHolder, int width, int height, Camera.PreviewCallback cb) throws IOException {
        this.width = width;
        this.height = height;
        this.cb = cb;
        previewHolder.addCallback(this);
    }

    private void adjustCameraOrientation() {
        Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, mCameraInfo);
        int degrees = getDeviceRotationDegree(context);
        int result;
        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (mCameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (mCameraInfo.orientation - degrees + 360) % 360;
        }

        mCamera.setDisplayOrientation(result);
    }

    private void stopPreview() {
        if (null == mCamera)
            return;
        try {
            mCamera.stopPreview();
            mCamera.setPreviewDisplay(null);
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.release();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        mCamera = null;
    }

    public void release() {
        stopPreview();
    }

    public int getDisplayDefaultRotation(Context ctx) {
        WindowManager windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getRotation();
    }

    public int getDeviceRotationDegree(Context ctx) {
        switch (getDisplayDefaultRotation(ctx)) {
            // normal portrait
            case Surface.ROTATION_0:
                return 0;
            // expected landscape
            case Surface.ROTATION_90:
                return 90;
            // upside down portrait
            case Surface.ROTATION_180:
                return 180;
            // "upside down" landscape
            case Surface.ROTATION_270:
                return 270;
        }
        return 0;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        initCamera(surfaceHolder);
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        adjustCameraOrientation();
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(width , height);
        parameters.setPreviewFormat(ImageFormat.NV21);
        mCamera.setParameters(parameters);
        mCamera.addCallbackBuffer(new byte[height * width * 4]);
        mCamera.setPreviewCallbackWithBuffer(cb);
        mCamera.addCallbackBuffer(new byte[height * width * 4]);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        mCamera.startPreview ();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        stopPreview();
    }
}
