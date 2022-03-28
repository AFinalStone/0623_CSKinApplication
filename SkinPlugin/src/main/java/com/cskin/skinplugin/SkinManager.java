package com.cskin.skinplugin;

import android.app.Application;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.util.HashMap;

public final class SkinManager {
    private static SkinManager INSTANCE;

    /**
     * 单例方法，目的是初始化app内置资源（越早越好，用户的操作可能是：换肤后的第2次冷启动）
     */
    public static void init(Application application) {
        if (INSTANCE == null) {
            synchronized (SkinManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SkinManager(application);
                }
            }
        }
    }

    private Application mApplication;
    private Resources mDefaultResource;
    private Resources mSkinResource;
    private HashMap<String, Resources> mResourcesCache;

    private SkinManager(Application application) {
        mApplication = application;
        mDefaultResource = application.getResources();
        mResourcesCache = new HashMap<>();
    }

    public static SkinManager getInstance() {
        return INSTANCE;
    }

    public void loadSkinResource(String pathName) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
