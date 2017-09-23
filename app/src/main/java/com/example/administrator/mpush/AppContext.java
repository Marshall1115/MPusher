package com.example.administrator.mpush;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.administrator.mpush.global.Contacts;
import com.example.pusher.pusher.PusherEnv;

public class AppContext extends Application {
    private static AppContext instance;
    private static Context context;
    private static Handler mHandler;

    public static Context getContext() {
        return context;
    }

    public static Handler getHandler() {
        return mHandler;
    }

    @Override
    public void onCreate() {
        instance = this;
        this.context = getApplicationContext();
        super.onCreate();
        mHandler = new Handler();
        PusherEnv.init(getApplicationContext(), Contacts.RTMP_URL);
    }

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static Application instance() {
        return instance;
    }
}
