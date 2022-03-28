package com.cskin.hook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

public abstract class MyLayoutInflater extends LayoutInflater {
    protected MyLayoutInflater(Context context) {
        super(context);
    }

    protected MyLayoutInflater(LayoutInflater original, Context newContext) {
        super(original, newContext);
    }

    @Override
    public View inflate(int resource, @Nullable ViewGroup root) {
        return super.inflate(resource, root);
    }
}
