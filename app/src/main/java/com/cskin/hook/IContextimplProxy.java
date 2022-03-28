package com.cskin.hook;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class IContextimplProxy implements InvocationHandler {

    private Object mContextimplProxy;
    private static final String TAG = "IContextimplProxy";

    public IContextimplProxy(Object contextimplProxy) {
        this.mContextimplProxy = contextimplProxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.d(TAG, "invoke方法被执行了");
        if ("getSystemService".equals(method.getName())) {
           
        }
        return method.invoke(mContextimplProxy, args);
    }
}
