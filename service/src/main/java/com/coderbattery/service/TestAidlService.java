package com.coderbattery.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.coderbattery.aidllib.Book;
import com.coderbattery.aidllib.BookManager;
import com.coderbattery.aidllib.IBookUpdateListener;
import com.coderbattery.aidllib.ITestAidlInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class TestAidlService extends Service {

    private static final String TAG = "TestAidlService";

    private IBinder iBinder = new ITestAidlInterface.Stub() {

        @Override
        public int add(int num1, int num2) throws RemoteException {
            int sum = num1 + num2;
            Log.d(TAG, "add: num1 = " + num1 + ", num2 = " + num2 + ", sum = " + sum);
            return sum;
        }
    };

    private IBinder bookManager = new BookManager.Stub() {

        /**
         * 支持并发的读写，这里我们使用它来进行自动的线程同步
         */
        private CopyOnWriteArrayList<Book> books = new CopyOnWriteArrayList<>();

        /**
         * RemoteCallBackList：系统专门提供的用于删除跨进程listener的接口
         * 工作原理：它的内部有一个Map结构专门用来保存所有AIDL回调ArrayMap<IBinder, Callback> mCallback = new ArrayMap<IBinder, Callback>();
         *          当客户端注册listener时，会把listener的信息注册到mCallBack中,
         *          其中key和value通过下面方式获得：IBinder key = listener.asBinder();Callback value = new Callback(listener, cookie)
         *          对象是不能跨进程传输的，对象的跨进程传输过程实际是反序列化的过程，这是我们Book类为什么要实现Parcelable接口的原因。
         *          在跨进程传输中，Binder会把客户端传递的对象重新转化并生成另一对象，当我们注册和解注册的过程中使用的是同一个客户端对象，
         *          但是通过Binder传递到服务端却生成了两个不同的对象。
         *          而RemoteCallBackList就是用来解决这个问题的，虽然所多次跨进程传输客户端的同一个对象会在服务端生成不同的对象，
         *          但在这些新生成的对象都有一个共同点，那就是他们底层的Binder对象是同一个，利用这个，就可以实现上面无法实现的功能。
         *          当客户端解注册时，我们只要遍历所有的listener，找出那个和解注册listener具有相同Binder对象服务器listener并把他删除掉即可，
         *          这就是RemoteCallbackList为我们做的事情
         */
        private RemoteCallbackList<IBookUpdateListener> mRemoteCallbackList = new RemoteCallbackList<>();


        @Override
        public boolean addBook(Book book) throws RemoteException {
            Log.d(TAG, "addBook: " + book.toString());

            boolean add = books.add(book);
            // 增加广播回调
            if (mRemoteCallbackList != null) {
                int N = mRemoteCallbackList.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    IBookUpdateListener broadcastItem = mRemoteCallbackList.getBroadcastItem(i);
                    if (broadcastItem != null) {
                        try {
                            broadcastItem.OnBookUpdate(book);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mRemoteCallbackList.finishBroadcast();
            }

            return add;
        }

        @Override
        public boolean removeBook(Book book) throws RemoteException {
            Log.d(TAG, "removeBook: " + book.toString());
            return books.remove(book);
        }

        @Override
        public void registerListener(IBookUpdateListener listener) throws RemoteException {
            Log.d(TAG, "registerListener: " + mRemoteCallbackList.getRegisteredCallbackCount());
            mRemoteCallbackList.register(listener);

        }

        @Override
        public void unregisterListener(IBookUpdateListener listener) throws RemoteException {
            Log.d(TAG, "unregisterListener: " + mRemoteCallbackList.getRegisteredCallbackCount());
            mRemoteCallbackList.unregister(listener);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: " + intent.toString());
        return bookManager;
    }
}
