package com.premnirmal.Magnet.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;

/**
 * Created by prem on 7/20/14.
 * <p/>
 * Activity used to launch the service
 */
public class ParanormalActivity extends Activity implements ServiceConnection {

  static final int REQUEST_CODE = 842;
  MyService.IconService iconService;
  boolean isBound;

  @Override public void onServiceConnected(ComponentName className, IBinder binder) {
    iconService = (MyService.IconService) binder;
  }

  @Override public void onServiceDisconnected(ComponentName className) {
    iconService = null;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    checkAndLaunch();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (isBound) {
      unbindService(this);
    }
  }

  void checkAndLaunch() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (!Settings.canDrawOverlays(this)) {
        final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE);
      } else {
        launchService();
      }
    } else {
      launchService();
    }
  }

  void launchService() {
    final Intent intent = new Intent(this, MyService.class);
    startService(intent);
    bindService(intent, this, Context.BIND_AUTO_CREATE);
    isBound = true;
    setContentView(R.layout.activity_main);
    findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        iconService.startMagnet();
      }
    });
    findViewById(R.id.stop_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        iconService.stopMagnet();
      }
    });
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (Settings.canDrawOverlays(this)) {
          launchService();
        } else {
          new AlertDialog.Builder(this).setTitle(R.string.permission)
              .setMessage(R.string.need_permission)
              .setCancelable(false)
              .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialogInterface, int i) {
                  dialogInterface.dismiss();
                  finish();
                }
              })
              .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialogInterface, int i) {
                  checkAndLaunch();
                }
              })
              .show();
        }
      }
    }
  }
}
