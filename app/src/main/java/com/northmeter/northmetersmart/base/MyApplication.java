package com.northmeter.northmetersmart.base;

import android.app.Application;

import com.mob.MobSDK;

/**
 * Created by dyd on 2018/11/30.
 */

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        MobSDK.init(this);
    }
}
