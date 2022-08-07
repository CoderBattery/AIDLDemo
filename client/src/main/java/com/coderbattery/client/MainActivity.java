package com.coderbattery.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.coderbattery.aidllib.ITestAidlInterface;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private ITestAidlInterface testAidlInterface;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: "+name+", "+service);

            testAidlInterface = ITestAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: "+name.toString());

        }
    };

    public void bind(View view) {

        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.coderbattery.service", "com.coderbattery.service.TestAidlService"));
        intent.setAction("com.coderbattery.service.TestAidlService");
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "bind: ");

    }

    public void add(View view) {
        try {
            int sum = testAidlInterface.add(1, 2);
            Log.d(TAG, "add: "+sum);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}
