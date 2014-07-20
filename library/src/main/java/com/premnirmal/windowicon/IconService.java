package com.premnirmal.windowicon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by prem on 7/20/14.
 */
public abstract class IconService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
