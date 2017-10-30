package com.demon.yzy.beanmusic.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by 易镇艺 on 2017/8/14.
 */

public class BaseApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }


    public static Context getContext() {
        return context;
    }
}
