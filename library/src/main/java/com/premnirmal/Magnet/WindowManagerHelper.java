package com.premnirmal.Magnet;

import android.os.Build;
import android.view.WindowManager;

/**
 * Created by nishant.patel on 18/12/17.
 * Class with some helper methods to assist with WindowManager
 */

public class WindowManagerHelper {

  public static int getOverlayFlag() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
    } else {
      return WindowManager.LayoutParams.TYPE_PHONE;
    }
  }
}
