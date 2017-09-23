package com.example.administrator.mpush.utils;

import android.content.Context;

import com.facebook.soloader.SoLoader;

/**
 * Created by Administrator on 2017/9/21.
 */

public class NativeUtils {
    static {
        SoLoader.loadLibrary("native-lib");
    }

    public static native void init(Context context);

    public static native void video_prepare(int width, int height);

    public static native void audio_prepare(int sampleRate, int channels);

    public static native void pushVideoData(byte[] frame_datas);

    public static native void pushAudioData(byte[] frame_datas, int dataLen);
}
