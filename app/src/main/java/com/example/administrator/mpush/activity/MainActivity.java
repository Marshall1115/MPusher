package com.example.administrator.mpush.activity;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.example.administrator.mpush.R;
import com.example.administrator.mpush.view.CameraView;
import com.example.administrator.mpush.view.HeartSurfaceView;
import com.example.pusher.config.PusherConfig;
import com.example.pusher.pusher.PusherEngine;

public class MainActivity extends BaseActivity {
    private CameraView cv;
    private HeartSurfaceView heartView;
    private Button button;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            heartView.addHeart();
            handler.sendEmptyMessageDelayed(0, 400);
        }
    };
    private PusherEngine pusherEngine;

    private void assignViews() {
        cv = (CameraView) findViewById(R.id.cv);
        heartView = (HeartSurfaceView) findViewById(R.id.diplayView);
        button = (Button) findViewById(R.id.btn_startPush);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        assignViews();
    }

    @Override
    protected void initData() {
        heartView.start();
        handler.sendEmptyMessage(0);
        pusherEngine = new PusherConfig.Builder().setDispalyViewHolder(cv.getHolder()).build();
        pusherEngine.prepare();
    }

    @Override
    protected void initListener() {
    }

    public void pushStream(View view) {

        if (!pusherEngine.isPushing()) {
            pusherEngine.startPushStream();
            button.setText("推流中");
        } else {
            pusherEngine.stopPushStream();
            button.setText("开始推流");
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (pusherEngine.isPushing()) {
            pusherEngine.stopPushStream();
        }
        super.onDestroy();
    }
}
