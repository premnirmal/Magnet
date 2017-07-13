package com.premnirmal.Magnet.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.premnirmal.Magnet.IconCallback;
import com.premnirmal.Magnet.Magnet;

/**
 * Created by prem on 7/20/14.
 * Desc: Example on how to use {@link com.premnirmal.Magnet.Magnet} in a service
 */
public class MyService extends Service {

  private ServiceBinder serviceBinder;

  @Override public IBinder onBind(Intent intent) {
    if (serviceBinder == null) {
      serviceBinder = new ServiceBinder(this);
    }
    return serviceBinder;
  }

  @Override public void onDestroy() {
    serviceBinder.destroy();
    serviceBinder = null;
    super.onDestroy();
  }

  interface IconService {
    void startMagnet();

    void stopMagnet();
  }

  private static class ServiceBinder extends Binder implements IconCallback, IconService {

    private static final String TAG = "Magnet";
    private Magnet magnet;
    private final Context context;

    ServiceBinder(Context context) {
      this.context = context;
    }

    void destroy() {
      if (magnet != null) {
        magnet.destroy();
      }
    }

    @Override public void startMagnet() {
      final ImageView iconView = new ImageView(context);
      iconView.setImageResource(R.drawable.ic_launcher);
      if (magnet == null) {
        magnet = Magnet.newBuilder(context)
            .setIconView(iconView)
            .setIconCallback(this)
            .setRemoveIconResId(R.drawable.ic_close)
            .setRemoveIconShadow(R.drawable.bottom_shadow)
            .setShouldFlingAway(true)
            .setShouldStickToXWall(true)
            .setShouldStickToYWall(false)
            .setRemoveIconShouldBeResponsive(true)
            .setInitialPosition(100, 200)
            .build();
        magnet.show();
        iconView.postDelayed(new Runnable() {
          @Override public void run() {
            if (magnet != null) {
              magnet.setPosition(500, 800);
            }
          }
        }, 2000);
      }
    }

    @Override public void stopMagnet() {
      if (magnet != null) {
        magnet.destroy();
        magnet = null;
      }
    }

    @Override public void onFlingAway() {
      Log.i(TAG, "onFlingAway");
      if (magnet != null) {
        magnet.destroy();
        magnet = null;
      }
    }

    @Override public void onMove(float x, float y) {
      Log.i(TAG, "onMove(" + x + "," + y + ")");
    }

    @Override public void onIconClick(View icon, float iconXPose, float iconYPose) {
      Log.i(TAG, "onIconClick(..)");
      Toast.makeText(context, R.string.click, Toast.LENGTH_SHORT).show();
    }

    @Override public void onIconDestroyed() {
      Log.i(TAG, "onIconDestroyed()");
    }
  }
}
