package com.premnirmal.windowicon;

import android.view.View;

/**
 * Created by prem on 7/20/14.
 */
public interface IconCallback {

    public void onFlingAway();

    public void onMove(float x, float y);

    public void onIconClick(View icon, float iconXPose, float iconYPose);

}
