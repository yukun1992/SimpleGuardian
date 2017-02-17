package com.example.android.newdemowithnavgation.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by cheny on 2017/2/11.
 */

public class NewsAuthenticatorService extends Service {
    private NewsAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new NewsAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
