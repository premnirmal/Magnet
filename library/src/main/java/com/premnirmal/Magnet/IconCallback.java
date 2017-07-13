package com.premnirmal.Magnet;

import android.view.View;

/**
 * Created by prem on 7/20/14.
 * Interface that gives the user callbacks for when the MagnetIcon has been interacted with.
 */
public interface IconCallback {

  /**
   * Callback for when the icon has been flung away
   */
  void onFlingAway();

  /**
   * Callback for when the icon has been dragged by the user
   *
   * @param x x coordinate on the screen in pixels
   * @param y y coordinate on the screen in pixels
   */
  void onMove(float x, float y);

  /**
   * Callback for when the icon has been clicked
   *
   * @param icon the view holding the icon. Get context from this view
   * @param iconXPose the x coordinate of the icon in pixels
   * @param iconYPose the y coordinate of the icon in pixels
   */
  void onIconClick(View icon, float iconXPose, float iconYPose);

  /**
   * Callback for when the icon has been destroyed
   */
  void onIconDestroyed();
}
