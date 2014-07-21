package com.premnirmal.Magnet;

import android.view.View;

/**
 * Created by prem on 7/20/14.
 * Desc: Interface that gives the user callbacks for when the MagnetIcon has been interacted with.
 */
public interface IconCallback {

    /**
     * Insert code for what to do when the icon has been flung away
     */
    public void onFlingAway();

    /**
     * Callback for when the icon has been dragged by the user
     * @param x x coordiante on the screen in pixels
     * @param y y coordinate on the screen in pixels
     */
    public void onMove(float x, float y);

    /**
     * Callback for when the icon has been clicked. Perform any action such as launch your app,
     * or show a menu, etc.
     * @param icon the view holding the icon. Get context from this view.
     * @param iconXPose the x coordinate of the icon in pixels
     * @param iconYPose the y coordiante of the icon in pixels
     */
    public void onIconClick(View icon, float iconXPose, float iconYPose);


    /**
     * Callback for when the icon has been destroyed. Usually you should stop your service in this.
     */
    public void onIconDestroyed();
}
