package com.zhj.my;

import android.app.Application;
import android.content.Context;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

public class MyApplication extends Application {
    static Context context;


    public static Context getContext() {
        return MyApplication.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();


        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5da81900");



    }
}
