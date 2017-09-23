package com.example.administrator.mpush.utils;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.administrator.mpush.AppContext;

/**
 * user: marshall
 * date: 2016/4/21.
 * fixme
 * <p>
 */
public class UiUtils {

    public static Context getContext() {
        return AppContext.getContext ();
    }

    public static View inflate(int resId) {
        return View.inflate (AppContext.getContext (), resId, null);
    }

    public static View inflate(int resId, ViewGroup parent) {
        return LayoutInflater.from (parent.getContext ())
                .inflate (resId, parent, false);
    }

    public static void showToast(final String msg) {
        HandlerUtils.postTaskSafely (new Runnable () {
                                         @Override
                                         public void run() {
                                             Toast.makeText (getContext (), msg, Toast.LENGTH_SHORT).show ();
                                         }
                                     }
        );
    }

    public static String[] getStringArray(int id) {
        String[] str = getResources ().getStringArray (id);
        return str;
    }

    public static Resources getResources() {return getContext ().getResources ();}

    public static int getDimensionPixelSize(int id) {
        int size = getResources ().getDimensionPixelSize (id);
        return size;
    }

    public static String getString(int id) {
        return getResources ().getString (id);
    }

    public static int getColor(int color) {
        return getResources ().getColor (color);
    }



}
