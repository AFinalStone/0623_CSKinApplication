package com.cskin.skinplugin;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SkinView {

    List<SkinPair> skinPairs;
    View view;

    public List<SkinPair> getSkinPairs() {
        return skinPairs;
    }

    public void setSkinPairs(List<SkinPair> skinPairs) {
        this.skinPairs = skinPairs;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void applySkin() {
        for (SkinPair skinPair : skinPairs) {
            switch (skinPair.getAttributeName()) {
                case "src":
                    Object src = SkinManager.getInstance().getBackgroundOrSrc(skinPair.getResourceId());
                    if (src instanceof Integer) {
                        ((ImageView) src).setImageDrawable(new ColorDrawable((Integer) src));
                    } else {
                        ((ImageView) src).setImageDrawable((Drawable) src);
                    }
                    break;
                case "background":
                    Object background = SkinManager.getInstance().getBackgroundOrSrc(skinPair.getResourceId());
                    if (background instanceof Integer) {
                        view.setBackgroundColor((Integer) background);
                    } else {
                        view.setBackground((Drawable) background);
                    }
                    break;
                case "text":
                    ((TextView) view).setText(SkinManager.getInstance().getString(skinPair.getResourceId()));
                    break;
                case "textColor":
                    ((TextView) view).setTextColor(SkinManager.getInstance().getColor(skinPair.getResourceId()));
                    break;
            }
        }
    }
}
