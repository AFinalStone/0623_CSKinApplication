package com.cskin.skinplugin;

import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class SkinAttribute {

    private ArrayList<String> mAttributes;
    private ArrayList<SkinView> mSkinViews;


    public SkinAttribute() {
        mAttributes = new ArrayList<>();
        mAttributes.add("text");
        mAttributes.add("textColor");
        mAttributes.add("src");
        mAttributes.add("background");
        mAttributes.add("drawable");
        mSkinViews = new ArrayList<>();
    }

    public void look(View view, AttributeSet attrs) {
        List<SkinPair> skinPars = new ArrayList<>();
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            String attributeName = attrs.getAttributeName(i);
            if (mAttributes.contains(attributeName)) {
                String attributeValue = attrs.getAttributeValue(i);
                if (attributeValue.startsWith("#") || attributeValue.startsWith("?")) {
                    continue;
                }
                int resId = Integer.parseInt(attributeValue.substring(1));
                SkinPair skinPair = new SkinPair();
                skinPair.setAttributeName(attributeName);
                skinPair.setResourceId(resId);
                skinPars.add(skinPair);
            }
        }
        if (skinPars.size() > 0) {
            SkinView skinView = new SkinView();
            skinView.setView(view);
            skinView.setSkinPairs(skinPars);
            skinView.applySkin();
            mSkinViews.add(skinView);
        }
    }

    /**
     * 使用外部资源及时刷新当前页面的UI皮肤
     */
    public void applySkin() {
        for (SkinView skinview : mSkinViews) {
            skinview.applySkin();
        }
    }


}
