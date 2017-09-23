package com.example.administrator.mpush.utils;

import com.facebook.soloader.SoLoader;

/**
 * Created by Administrator on 2017/5/14.
 */

public class PicutureNativeUtils {
    static {
        SoLoader.loadLibrary ("native-lib");
    }

    public native static void pushData(byte[] data);

    public native static void prepare(int width, int height);
}
