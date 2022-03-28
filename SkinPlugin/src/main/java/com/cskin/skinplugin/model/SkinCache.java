package com.cskin.skinplugin.model;

import android.content.res.Resources;

public class SkinCache {
    private String packageName;
    private Resources skinResource;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Resources getSkinResource() {
        return skinResource;
    }

    public void setSkinResource(Resources skinResource) {
        this.skinResource = skinResource;
    }
}
