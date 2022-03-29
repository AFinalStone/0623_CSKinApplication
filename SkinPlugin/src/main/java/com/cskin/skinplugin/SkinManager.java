package com.cskin.skinplugin;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;

import com.cskin.skinplugin.core.SkinLayoutFactory;
import com.cskin.skinplugin.model.SkinCache;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public final class SkinManager {
    private static SkinManager INSTANCE;

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
    private String mSkinPackageName; // 皮肤包资源所在包名（注：皮肤包不在app内，也不限包名）
    private HashMap<String, SkinCache> mResourcesCache;
    private HashMap<Activity, SkinLayoutFactory> mActivitySkinLayouts;
    private boolean isDefaultSkin = true; // 应用默认皮肤（app内置）
    private final String Add_Asset_Method = "addAssetPath";

    private SkinManager(Application application) {
        mApplication = application;
        mDefaultResource = application.getResources();
        mResourcesCache = new HashMap<>();
        mActivitySkinLayouts = new HashMap<>();
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                try {
                    hookLayoutInflater(activity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                mActivitySkinLayouts.remove(activity);
            }
        });
    }

    public static SkinManager getInstance() {
        return INSTANCE;
    }

    /**
     * Hook页面的LayoutInflater
     *
     * @param activity
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public void hookLayoutInflater(Activity activity) throws NoSuchFieldException, IllegalAccessException {
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        Field mFactorySet = LayoutInflater.class.getDeclaredField("mFactorySet");
        mFactorySet.setAccessible(true);
        mFactorySet.set(layoutInflater, false);
        SkinLayoutFactory skinLayoutFactory = new SkinLayoutFactory(activity);
        layoutInflater.setFactory2(skinLayoutFactory);
        mActivitySkinLayouts.put(activity, skinLayoutFactory);
    }

    /**
     * 加载皮肤资源
     *
     * @param skinPath
     */
    public void loadSkinResource(String skinPath) {
        // 优化：如果没有皮肤包或者没做换肤动作，方法不执行直接返回！
        if (TextUtils.isEmpty(skinPath)) {
            isDefaultSkin = true;
            return;
        }
        SkinCache skinCache = mResourcesCache.get(skinPath);
        if (skinCache != null) {
            isDefaultSkin = false;
            mSkinResource = skinCache.getSkinResource();
            mSkinPackageName = skinCache.getPackageName();
            return;
        }
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method method = AssetManager.class.getDeclaredMethod(Add_Asset_Method, String.class);
            method.setAccessible(true);
            method.invoke(assetManager, skinPath);
            Resources resources = mApplication.getResources();
            mSkinResource = new Resources(assetManager, resources.getDisplayMetrics(), resources.getConfiguration());
            // 根据apk文件路径获取该应用的包名。兼容5.0 - 12.0
            mSkinPackageName = mApplication.getPackageManager().getPackageArchiveInfo(skinPath, PackageManager.GET_ACTIVITIES).packageName;
            // 无法获取皮肤包应用的包名，则加载app内置资源
            isDefaultSkin = TextUtils.isEmpty(mSkinPackageName);
            if (!isDefaultSkin) {
                skinCache = new SkinCache();
                skinCache.setSkinResource(mSkinResource);
                skinCache.setPackageName(mSkinPackageName);
                mResourcesCache.put(skinPath, skinCache);
            }

        } catch (Exception e) {
            e.printStackTrace();
            isDefaultSkin = true;
        }
    }

    /**
     * 使用外部资源及时刷新当前页面的UI皮肤
     */
    public void applyActivitySkin(Activity activity) {
        SkinLayoutFactory skinLayoutFactory = mActivitySkinLayouts.get(activity);
        if (skinLayoutFactory != null) {
            skinLayoutFactory.getSkinAttribute().applySkin();
        }
    }

    /**
     * 通过app的资源id获取外部资源包的资源id
     *
     * @param resourceId
     * @return
     */
    public int getSkinResourceIds(int resourceId) {
        // 如果没有皮肤包或者没做换肤动作，直接返回app内置资源！
        if (isDefaultSkin) return resourceId;
        // 使用app内置资源加载，是因为内置资源与皮肤包资源一一对应（“launch_bg”, “drawable”）
        String resourceName = mDefaultResource.getResourceEntryName(resourceId);
        String resourceType = mDefaultResource.getResourceTypeName(resourceId);
        int skinResId = mSkinResource.getIdentifier(resourceName, resourceType, mSkinPackageName);
        isDefaultSkin = skinResId == 0;
        return isDefaultSkin ? resourceId : skinResId;
    }


    public int getColor(int resourceId) {
        int ids = getSkinResourceIds(resourceId);
        return isDefaultSkin ? mDefaultResource.getColor(ids) : mSkinResource.getColor(ids);
    }

    public ColorStateList getColorStateList(int resourceId) {
        int ids = getSkinResourceIds(resourceId);
        return isDefaultSkin ? mDefaultResource.getColorStateList(ids) : mSkinResource.getColorStateList(ids);
    }

    // mipmap和drawable统一用法
    public Drawable getDrawableOrMipMap(int resourceId) {
        int ids = getSkinResourceIds(resourceId);
        return isDefaultSkin ? mDefaultResource.getDrawable(ids) : mSkinResource.getDrawable(ids);
    }

    public String getString(int resourceId) {
        int ids = getSkinResourceIds(resourceId);
        return isDefaultSkin ? mDefaultResource.getString(ids) : mSkinResource.getString(ids);
    }

    // 返回值特殊情况：可能是color / drawable / mipmap
    public Object getBackgroundOrSrc(int resourceId) {
        // 需要获取当前属性的类型名Resources.getResourceTypeName(resourceId)再判断
        String resourceTypeName = mDefaultResource.getResourceTypeName(resourceId);
        switch (resourceTypeName) {
            case "color":
                return getColor(resourceId);

            case "mipmap": // drawable / mipmap
            case "drawable":
                return getDrawableOrMipMap(resourceId);
        }
        return null;
    }

    // 获得字体
    public Typeface getTypeface(int resourceId) {
        // 通过资源ID获取资源path，参考：resources.arsc资源映射表
        String skinTypefacePath = getString(resourceId);
        // 路径为空，使用系统默认字体
        if (TextUtils.isEmpty(skinTypefacePath)) return Typeface.DEFAULT;
        return isDefaultSkin ? Typeface.createFromAsset(mDefaultResource.getAssets(), skinTypefacePath)
                : Typeface.createFromAsset(mSkinResource.getAssets(), skinTypefacePath);
    }

}
