package com.cskin.change;

import android.app.Application;

import com.cskin.skinplugin.SkinManager;

public class CKApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.init(this);
    }
}
