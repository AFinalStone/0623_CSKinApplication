package com.cskin.skinplugin.core;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.cskin.skinplugin.SkinAttribute;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public class SkinLayoutFactory implements LayoutInflater.Factory2 {

    private String[] mSDKViewPrevName = {
            "android.view.",
            "android.webkit.",
            "android.widget."
    };
    static final Class<?>[] mConstructorSignature = new Class[]{
            Context.class, AttributeSet.class};
    private static final HashMap<String, Constructor<? extends View>> mConstructorMap =
            new HashMap<String, Constructor<? extends View>>();

    private SkinAttribute mSkinAttribute;
    private Activity mActivity;

    public SkinLayoutFactory(Activity activity) {
        mActivity = activity;
        this.mSkinAttribute = new SkinAttribute();
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view;
        if (-1 == name.indexOf('.')) {
            view = createSDkView(name, context, attrs);
        } else {
            view = createView(name, null, attrs);
        }
        if (view != null && view instanceof ViewsNoSupportChangeSkin) {
            mSkinAttribute.look(view, attrs);
        }
        return view;
    }


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }

    private View createSDkView(String name, Context context, AttributeSet attrs) {
        View view = null;
        for (String prevName : mSDKViewPrevName) {
            name = prevName + name;
            view = createView(name, context, attrs);
            if (null != view) {
                break;
            }
        }
        return view;
    }

    private View createView(String name, Context context, AttributeSet attrs) {
        Constructor<? extends View> constructor = findConstructor(name, context);
        View view = null;
        try {
            view = constructor.newInstance(context, attrs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private Constructor<? extends View> findConstructor(String name, Context context) {
        Constructor<? extends View> constructor = mConstructorMap.get(name);
        if (constructor == null) {
            try {
                Class<? extends View> clazz = null;
                clazz = context.getClassLoader().loadClass(name).asSubclass(View.class);
                constructor = clazz.getConstructor(mConstructorSignature);
                constructor.setAccessible(true);
                mConstructorMap.put(name, constructor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return constructor;
    }
}
