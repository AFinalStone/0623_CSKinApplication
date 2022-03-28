package com.cskin.skinplugin.bean;

import android.content.res.Resources;

public class SkinCache {
    private String skinName;
    private Resources skinResource;

    public String getSkinName() {
        return skinName;
    }

    public void setSkinName(String skinName) {
        this.skinName = skinName;
    }

    public Resources getSkinResource() {
        return skinResource;
    }

    public void setSkinResource(Resources skinResource) {
        this.skinResource = skinResource;
    }
}
