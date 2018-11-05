package com.example.tworld.myapplication;

import android.app.Application;
import android.os.Handler;


/**
 */

public class BaseApplication extends Application {

    private static BaseApplication instance;
    private static Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        handler = new Handler();
    }

    public static BaseApplication getInstance() {
        return instance;
    }

    public static Handler getHandler(){
        return handler;
    }

    /**
     * 完全退出程序
     */
    public void exit(){
        instance = null;
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
