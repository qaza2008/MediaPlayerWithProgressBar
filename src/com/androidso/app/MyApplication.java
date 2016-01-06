package com.androidso.app;

import android.app.Application;
import android.content.Intent;
import com.androidso.app.service.MediaPlayService;

/**
 * Created by mac on 16/1/6.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        startService();
    }

    public void startService() {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), MediaPlayService.class);
        startService(intent);
    }

}
