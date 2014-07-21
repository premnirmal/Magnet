package com.premnirmal.Magnet;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by prem on 7/20/14.
 */
class FlingListener extends GestureDetector.SimpleOnGestureListener {

    private static final float FLING_THRESHOLD_VELOCITY = 50f;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return (Math.abs(e2.getX() - e1.getX()) < FLING_THRESHOLD_VELOCITY && e2.getY() - e1.getY() > FLING_THRESHOLD_VELOCITY);
    }

}