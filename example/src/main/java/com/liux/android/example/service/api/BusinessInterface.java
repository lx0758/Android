package com.liux.android.example.service.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;

import com.liux.android.sm.api.ModuleInterface;

public class BusinessInterface extends ModuleInterface<IBusinessInterface> {

    @SuppressLint("StaticFieldLeak")
    private volatile static BusinessInterface mInstance;
    public static BusinessInterface getInstance(Context context) {
        if (mInstance == null) {
            synchronized (BusinessInterface.class) {
                if (mInstance == null) {
                    mInstance = new BusinessInterface(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    private BusinessInterface(Context context) {
        super(context, context.getPackageName() + ".action.BUSINESS_SERVICE");
    }

    @Override
    public IBusinessInterface onBinderToInterface(IBinder iBinder) {
        return IBusinessInterface.Stub.asInterface(iBinder);
    }

    public String call(String param) {
        if (!isModuleAvailable()) {
            return null;
        }

        try {
            return getModuleInterface().call(param);
        } catch (RemoteException ignored) {
            return null;
        }
    }
}
