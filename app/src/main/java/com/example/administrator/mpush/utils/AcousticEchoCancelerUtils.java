package com.example.administrator.mpush.utils;

import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AudioEffect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

/**
 * Created by Administrator on 2017/5/17.
 */

public class AcousticEchoCancelerUtils {
    public static final String TAG = "marshall";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isDeviceSupport() {
        return AcousticEchoCanceler.isAvailable ();
    }

    private static AcousticEchoCanceler canceler;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static boolean initAEC(int audioSession) {
        if (canceler != null) {
            return false;
        }
        canceler = AcousticEchoCanceler.create (audioSession);
        canceler.setEnabled (false);
        return canceler.getEnabled ();
    }

    public static boolean setAECEnabled(boolean enable) {
        if (null == canceler) {
            return false;
        }
        int ret = canceler.setEnabled (enable);
        if (ret != AudioEffect.SUCCESS) {
            Log.e (TAG, "AcousticEchoCanceler.setEnabled failed");
            return false;
        }
        if (enable) {
            Log.d (TAG, "Aec On");
        } else {
            Log.d (TAG, "Aec Off");
        }
        return canceler.getEnabled ();
    }

    public static boolean release() {
        if (null == canceler) {
            return false;
        }
        canceler.setEnabled (false);
        canceler.release ();
        canceler = null;
        return true;
    }
}