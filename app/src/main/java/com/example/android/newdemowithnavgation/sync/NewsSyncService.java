package com.example.android.newdemowithnavgation.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by cheny on 2017/2/11.
 */

public class NewsSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static NewsSyncAdapter sNewsSyncAdapter = null;


    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sNewsSyncAdapter == null) {
                sNewsSyncAdapter = new NewsSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sNewsSyncAdapter.getSyncAdapterBinder();
    }
}
