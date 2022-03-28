package com.cskin.skinplugin;

import android.app.Application;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.cskin.skinplugin.model.SkinCache;

import java.lang.reflect.Method;
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
    private String mSkinPackageName; // 皮肤包资源所在包名（注：皮肤包不在app内，也不限包名）
    private HashMap<String, SkinCache> mResourcesCache;
    private boolean isDefaultSkin = true; // 应用默认皮肤（app内置）
    private final String Add_Asset_Method = "addAssetPath";

    private SkinManager(Application application) {
        mApplication = application;
        mDefaultResource = application.getResources();
        mResourcesCache = new HashMap<>();
    }

    public static SkinManager getInstance() {
        return INSTANCE;
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
            Method method = AssetManager.class.getMethod(Add_Asset_Method);
            method.setAccessible(true);
            method.invoke(assetManager, skinPath);
            Resources resources = mApplication.getResources();
            mSkinResource = new Resources(assetManager, resources.getDisplayMetrics(), resources.getConfiguration());
            // 根据apk文件路径（皮肤包也是apk文件），获取该应用的包名。兼容5.0 - 9.0（亲测）
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
