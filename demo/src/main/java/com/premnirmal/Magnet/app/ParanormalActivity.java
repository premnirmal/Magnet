package com.premnirmal.Magnet.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

/**
 * Created by prem on 7/20/14.
 * <p/>
 * Description:
 * A stub activity used to launch the service
 */
public class ParanormalActivity extends Activity {

  private static final int REQUEST_CODE = 842;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    checkDrawOverlayPermission();
  }

  private void checkDrawOverlayPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (!Settings.canDrawOverlays(this)) {
        final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE);
      } else {
        launchMagnet();
      }
    } else {
      launchMagnet();
    }
  }

  private void launchMagnet() {
    startService(new Intent(this, MyService.class));
    finish();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (Settings.canDrawOverlays(this)) {
          launchMagnet();
        }
      }
    }
  }
}
