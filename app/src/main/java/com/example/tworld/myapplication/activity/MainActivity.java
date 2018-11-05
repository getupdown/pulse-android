package com.example.tworld.myapplication.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tworld.myapplication.BaseApplication;
import com.example.tworld.myapplication.R;

public class MainActivity extends AppCompatActivity {
    private TextView tv_bluetooth;
    private TextView tv_history;
    private long exitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//主界面
        init();
    }

    //初始化函数
    public void init() {
        tv_bluetooth = findViewById(R.id.tv_bluetooth);//蓝牙接收数据模块
        tv_history = findViewById(R.id.tv_history);//历史数据模块

        tv_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FindDeviceActivity.class);//跳转到打开蓝牙界面
                startActivity(intent);
            }
        });

        tv_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到历史数据界面，也是一个listView
                Intent intent = new Intent(MainActivity.this, GetHistoryActivity.class);//跳转到历史列表界面
                startActivity(intent);
            }
        });
    }


    //返回键退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            //如果点击的是后退键  首先判断webView是否能够后退
            // 当按下BACK键时，会被onKeyDown捕获，判断是BACK键，则执行exit方法。
            //  判断用户两次按键的时间差是否在一个预期值之内，是的话直接直接退出，不是的话提示用户再按一次后退键退出。
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_LONG).show();
                exitTime = System.currentTimeMillis();
            } else {
                BaseApplication.getInstance().exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
