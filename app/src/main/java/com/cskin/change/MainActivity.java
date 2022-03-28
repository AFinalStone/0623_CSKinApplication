package com.cskin.change;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    static final Class<?>[] mConstructorSignature = new Class[]{
            Context.class, AttributeSet.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater layoutInflater = getLayoutInflater();
        layoutInflater.setFactory2(new LayoutInflater.Factory2() {
            @Nullable
            @Override
            public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
                try {
                    Class<? extends View> clazz = Class.forName(name, false,
                            context.getClassLoader()).asSubclass(View.class);
                    View view = null;
//                    if (-1 == name.indexOf('.')) {
//                        view = createView(name, "android.view.", attrs);
//                    } else {
//                        view = createView(context, name, null, attrs);
//                    }
                    return view;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Nullable
            @Override
            public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
                return null;
            }
        });
        setContentView(R.layout.activity_main);
    }
}