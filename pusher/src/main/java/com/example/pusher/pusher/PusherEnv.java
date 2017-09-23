package com.example.pusher.pusher;


import android.content.Context;

import com.example.pusher.utils.NativeUtils;
import com.facebook.soloader.SoLoader;

public class PusherEnv {

    private static Context applicationContext;

    public static Context getApplicationContext() {
        return applicationContext;
    }

    public static void init(Context context, String rtmpUrl) {
        applicationContext = context;
        SoLoader.init(context, false);
        NativeUtils.init(context,rtmpUrl);
    }
}
