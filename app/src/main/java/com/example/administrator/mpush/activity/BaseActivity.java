package com.example.administrator.mpush.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.administrator.mpush.AppManager;


/**
 * 1,规范代码结构
 * 2,提供公用方法，精简代码
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        AppManager.getAppManager ().addActivity (this);
        initView();
        initListener();
        initData();

    }

    /**
     * 返回当前Activity使用的布局ID
     */
    protected abstract int getLayoutId();

    /**
     * 只执行findViewById操作
     */
    protected abstract void initView();

    /**
     * 注册监听器，适配器
     */
    protected abstract void initListener();

    /**
     * 获取数据，填充界面
     */
    protected abstract void initData();

    /**
     * 显示一个内容为msg的吐司
     */
    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示一个内容为msgId引用的String的吐司
     */
    public void toast(int msgId) {
        Toast.makeText(this, msgId, Toast.LENGTH_SHORT).show();
    }

    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager ().finishActivity ();
    }
}

