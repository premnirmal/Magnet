package com.premnirmal.Magnet;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by prem on 7/20/14.
 * Desc: Class holding the Magnet Icon, and performing touchEvents on the view.
 */
public class Magnet implements View.OnTouchListener {

    private static final int TOUCH_TIME_THRESHOLD = 200;

    private View mIconView;
    private RemoveView mRemoveView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private Context mContext;
    private GestureDetector mGestureDetector;
    private boolean shouldStickToWall = true;
    private boolean shouldFlingAway = true;
    private IconCallback mListener;
    private MoveAnimator mAnimator;

    private long lastTouchDown;
    private float lastXPose, lastYPose;
    private boolean isBeingDragged = false;
    private int mWidth, mHeight;

    Magnet(Context context, IconCallback callback, MagnetRequirements icon) {
        mContext = context;
        mIconView = icon.getIconView(context);
        mIconView.setOnTouchListener(this);
        mListener = callback;
        shouldStickToWall = icon.shouldStickToWall();
        shouldFlingAway = icon.shouldFlingAway();
        mGestureDetector = new GestureDetector(context, new FlingListener());
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mAnimator = new MoveAnimator();
        if (shouldFlingAway) {
            mRemoveView = new RemoveView(context, icon.getRemoveIconResID(),
                    icon.getShadowBackgroundResID(), icon.removeIconShouldBeResponsive());
        }
        addToWindow(mIconView);
        updateSize();
        goToWall();
    }

    private void addToWindow(View icon) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
        );
        mWindowManager.addView(icon, mLayoutParams = params);
    }

    private void updateSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        mWidth = (metrics.widthPixels - mIconView.getWidth()) / 2;
        mHeight = (metrics.heightPixels - mIconView.getHeight()) / 2;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        boolean eaten = false;
        if (shouldFlingAway) {
            eaten = mGestureDetector.onTouchEvent(event);
        }
        if (eaten) {
            flingAway();
        } else {
            float x = event.getRawX();
            float y = event.getRawY();
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                showRemoveView();
                lastTouchDown = System.currentTimeMillis();
                mAnimator.stop();
                updateSize();
                isBeingDragged = true;
            } else if (action == MotionEvent.ACTION_UP) {
                if (System.currentTimeMillis() - lastTouchDown < TOUCH_TIME_THRESHOLD) {
                    mListener.onIconClick(mIconView, x, y);
                }
                hideRemoveView();
                isBeingDragged = false;
                eaten = false;
                goToWall();
            } else if (action == MotionEvent.ACTION_MOVE) {
                if (isBeingDragged) {
                    move(x - lastXPose, y - lastYPose);
                }
            }

            lastXPose = x;
            lastYPose = y;
        }
        return eaten;
    }

    private void flingAway() {
        if (shouldFlingAway) {
            int y = mContext.getResources().getDisplayMetrics().heightPixels / 2;
            int x = 0;
            mAnimator.start(x, y);
            mListener.onFlingAway();
            destroy();
        }
    }

    private void showRemoveView() {
        if (mRemoveView != null) {
            mRemoveView.show();
        }
    }

    private void hideRemoveView() {
        if (mRemoveView != null) {
            mRemoveView.hide();
        }
    }

    private void goToWall() {
        if (shouldStickToWall) {
            float nearestXWall = mLayoutParams.x > 0 ? mWidth : -mWidth;
            float nearestYWall = mLayoutParams.y > 0 ? mHeight : -mHeight;
            if (Math.abs(mLayoutParams.x - nearestXWall) < Math.abs(mLayoutParams.y - nearestYWall)) {
                mAnimator.start(nearestXWall, mLayoutParams.y);
            } else {
                mAnimator.start(mLayoutParams.x, nearestYWall);
            }
        }
    }

    private void move(float deltaX, float deltaY) {
        mLayoutParams.x += deltaX;
        mLayoutParams.y += deltaY;
        if (mRemoveView != null) {
            mRemoveView.onMove(mLayoutParams.x, mLayoutParams.y);
        }
        mWindowManager.updateViewLayout(mIconView, mLayoutParams);
        if (!isBeingDragged && Math.abs(mLayoutParams.x) < 50 && Math.abs(mLayoutParams.y
                - (mContext.getResources().getDisplayMetrics().heightPixels / 2)) < 250) {
            mListener.onFlingAway();
        }
    }

    void destroy() {
        mWindowManager.removeView(mIconView);
        if(mRemoveView != null) {
            mRemoveView.destroy();
        }
        mListener.onIconDestroyed();
        mContext = null;
    }

    private class MoveAnimator implements Runnable {

        private Handler handler = new Handler(Looper.getMainLooper());
        private float destinationX;
        private float destinationY;
        private long startingTime;

        private void start(float x, float y) {
            this.destinationX = x;
            this.destinationY = y;
            startingTime = System.currentTimeMillis();
            handler.post(this);
        }

        @Override
        public void run() {
            if (mIconView != null && mIconView.getParent() != null) {
                float progress = Math.min(1, (System.currentTimeMillis() - startingTime) / 400f);
                float deltaX = (destinationX - mLayoutParams.x) * progress;
                float deltaY = (destinationY - mLayoutParams.y) * progress;
                move(deltaX, deltaY);
                if (progress < 1) {
                    handler.post(this);
                }
            }
        }

        private void stop() {
            handler.removeCallbacks(this);
        }

    }
}
