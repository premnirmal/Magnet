package com.premnirmal.Magnet.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.premnirmal.Magnet.IconCallback;
import com.premnirmal.Magnet.Magnet;

/**
 * Created by prem on 7/20/14.
 * Desc: Example on how to use {@link com.premnirmal.Magnet.Magnet} in a service
 */
public class MyService extends Service implements IconCallback {

    private static final String TAG = "Magnet";
    private Magnet mMagnet;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ImageView iconView = new ImageView(this);
        iconView.setImageResource(R.drawable.ic_launcher);
        mMagnet = new Magnet.Builder(this)
                .setIconView(iconView)
                .setIconCallback(this)
                .build();
        mMagnet.show();
    }

    @Override
    public void onFlingAway() {
        Log.i(TAG, "onFlingAway");
    }

    @Override
    public void onMove(float x, float y) {
        Log.i(TAG, "onMove(" + x + "," + y + ")");
    }

    @Override
    public void onIconClick(View icon, float iconXPose, float iconYPose) {
        Log.i(TAG, "onIconClick(..)");
    }

    @Override
    public void onIconDestroyed() {
        Log.i(TAG, "onIconDestroyed()");
    }
}
