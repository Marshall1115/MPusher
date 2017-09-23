package com.example.pusher.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/21.
 */

public class LiveHandler {

    private MyHandler mHandler;
    public List<OnHandleMessageListener> listeners = new ArrayList<>();
    private HandlerThread mHandlerThread;

    public LiveHandler() {
        mHandlerThread = new HandlerThread("liveHandlerThread");
        mHandlerThread.start();
        mHandler = new MyHandler(mHandlerThread.getLooper());
    }

    public void sendMessage(Message message) {
        mHandler.sendMessage(message);
    }

    public void setHandleMessageListener(OnHandleMessageListener listener) {
        listeners.add(listener);
    }

    public void release() {
        mHandlerThread.quit();
        mHandler.removeCallbacksAndMessages(null);
    }

    private class MyHandler extends Handler {

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            for (OnHandleMessageListener listener : listeners) {
                if (listener != null) {
                    listener.handleMessage(msg);
                }
            }
        }
    }

    public interface OnHandleMessageListener {
        void handleMessage(Message msg);
    }

}
