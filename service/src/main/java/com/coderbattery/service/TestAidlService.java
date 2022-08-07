package com.coderbattery.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.coderbattery.aidllib.ITestAidlInterface;


public class TestAidlService extends Service {

    private static final String TAG = "TestAidlService";

    private IBinder iBinder = new ITestAidlInterface.Stub(){

        @Override
        public int add(int num1, int num2) throws RemoteException {
            int sum = num1 + num2;
            Log.d(TAG, "add: num1 = "+num1 + ", num2 = "+ num2 + ", sum = "+sum);
            return sum;
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: "+intent.toString());
        return iBinder;
    }
}
