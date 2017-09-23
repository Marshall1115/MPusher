package com.example.pusher.pusher;

import com.example.pusher.params.Params;
import com.example.pusher.pusher.impl.Pusher;

/**
 * Created by Administrator on 2017/9/21.
 */

public abstract class AbstractPusher<T extends Params> implements Pusher {
    protected boolean isPushing;
    protected T params;

    @Override
    public void startPushStream() {
        isPushing = true;
        onStartPushStream();
    }

    @Override
    public void stopPushStream() {
        isPushing = false;
        onStopPushStream();
    }

    protected void onStartPushStream() {
    }

    protected void onStopPushStream() {
    }

    public void appay(T params) {
        this.params = params;
    }
}
