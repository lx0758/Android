package com.liux.android.example.service;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.liux.android.example.service.api.IBusinessInterface;
import com.liux.android.sm.api.ModuleService;

public class BusinessService extends ModuleService {
    private static final String TAG = "BusinessService";

    private BusinessInterfaceImpl mBusinessInterfaceImpl;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        mBusinessInterfaceImpl = new BusinessInterfaceImpl();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return mBusinessInterfaceImpl;
    }

    @Override
    public void onModuleServiceReady() {
        super.onModuleServiceReady();
        Log.i(TAG, "onModuleServiceReady");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    public static class BusinessInterfaceImpl extends IBusinessInterface.Stub {
        @Override
        public String call(String param) throws RemoteException {
            Log.i(TAG, "call, param:" + param);
            return param + " World!";
        }
    }
}
