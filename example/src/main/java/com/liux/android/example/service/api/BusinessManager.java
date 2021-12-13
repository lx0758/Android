package com.liux.android.example.service.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;

import com.liux.android.service.api.AbstractProxy;

public class BusinessManager extends AbstractProxy<IBusinessInterface> {

    @SuppressLint("StaticFieldLeak")
    private volatile static BusinessManager mInstance;
    public static BusinessManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (BusinessManager.class) {
                if (mInstance == null) {
                    mInstance = new BusinessManager(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    private BusinessManager(Context context) {
        super(context, "com.liux.android.example.action.SERVICE_BUSINESS");
    }

    @Override
    public IBusinessInterface getInterface() {
        IBinder iBinder = getService();
        if (iBinder != null) {
            return IBusinessInterface.Stub.asInterface(iBinder);
        }
        return null;
    }

    public String call(String param) {
        IBusinessInterface iBusinessInterface = getInterface();
        if (iBusinessInterface == null) {
            return null;
        }

        try {
            return iBusinessInterface.call(param);
        } catch (RemoteException ignored) {
            return null;
        }
    }
}
