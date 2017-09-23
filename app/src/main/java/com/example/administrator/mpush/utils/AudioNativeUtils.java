package com.example.administrator.mpush.utils;

import com.facebook.soloader.SoLoader;

/**
 * Created by Administrator on 2017/5/14.
 */

public class AudioNativeUtils {
    static {
        SoLoader.loadLibrary ("native-lib");
    }

    public static native void prepare(int sampleRate, int channels);

    public static native void pushData(byte[] buffer, int length);

    public static native void release() ;
}
