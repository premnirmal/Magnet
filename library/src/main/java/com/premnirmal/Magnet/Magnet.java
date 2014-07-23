package com.premnirmal.Magnet;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
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


    public static class Builder {

        Magnet magnet;

        public Builder(Context context) {
            magnet = new Magnet(context);
        }

        /**
         * The Icon must have a view, provide a view or a layout using {@link #setIconView(int)}
         * @param iconView the view representing the icon
         * @return
         */
        public Builder setIconView(View iconView) {
            magnet.mIconView = iconView;
            magnet.mIconView.setOnTouchListener(magnet);
            return this;
        }

        /**
         * Use an xml layout to provide the button view
         * @param iconViewRes the layout id of the icon
         * @return
         */
        public Builder setIconView(int iconViewRes) {
            magnet.mIconView = LayoutInflater.from(magnet.mContext).inflate(iconViewRes, null);
            magnet.mIconView.setOnTouchListener(magnet);
            return this;
        }

        /**
         * whether your magnet sticks to the edge of your screen when you release it
         * @param shouldStick
         * @return
         */
        public Builder setShouldStickToWall(boolean shouldStick) {
            magnet.shouldStickToWall = shouldStick;
            return this;
        }

        /**
         * whether you can fling away your Magnet towards the bottom of the screen
         * @param shoudlFling
         * @return
         */
        public Builder setShouldFlingAway(boolean shoudlFling) {
            magnet.shouldFlingAway = shoudlFling;
            return this;
        }

        /**
         * Callback for when the icon moves, or when it isis flung away and destroyed
         * @param callback
         * @return
         */
        public Builder setIconCallback(IconCallback callback) {
            magnet.mListener = callback;
            return this;
        }

        /**
         *
         * @param shouldBeResponsive
         * @return
         */
        public Builder setRemoveIconShouldBeResponsive(boolean shouldBeResponsive) {
            magnet.mRemoveView.shouldBeResponsive = shouldBeResponsive;
            return this;
        }

        /**
         * you can set a custom remove icon or use the default one
         * @param removeIconResId
         * @return
         */
        public Builder setRemoveIconResId(int removeIconResId) {
            magnet.mRemoveView.setIconResId(removeIconResId);
            return this;
        }

        /**
         * you can set a custom remove icon shadow or use the default one
         * @param shadow
         * @return
         */
        public Builder setRemoveIconShadow(int shadow) {
            magnet.mRemoveView.setShadowBG(shadow);
            return this;
        }

        public Magnet build() {
            if(magnet.mIconView == null) {
                throw new NullPointerException("Magnet view is null! Must set a view for the magnet!");
            }
            return magnet;
        }
    }


    private Magnet(Context context) {
        mContext = context;
        mGestureDetector = new GestureDetector(context, new FlingListener());
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mAnimator = new MoveAnimator();
        mRemoveView = new RemoveView(context);
    }

    public void show() {
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
                    if (mListener != null) {
                        mListener.onIconClick(mIconView, x, y);
                    }
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
            if (mListener != null) {
                mListener.onFlingAway();
            }
            destroy();
        }
    }

    private void showRemoveView() {
        if (mRemoveView != null && shouldFlingAway) {
            mRemoveView.show();
        }
    }

    private void hideRemoveView() {
        if (mRemoveView != null && shouldFlingAway) {
            mRemoveView.hide();
        }
    }

    private void goToWall() {
        if (shouldStickToWall) {
            float nearestXWall = mLayoutParams.x >= 0 ? mWidth : -mWidth;
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
        if (mRemoveView != null && shouldFlingAway) {
            mRemoveView.onMove(mLayoutParams.x, mLayoutParams.y);
        }
        mWindowManager.updateViewLayout(mIconView, mLayoutParams);
        if (mListener != null) {
            mListener.onMove(mLayoutParams.x, mLayoutParams.y);
        }
        if (shouldFlingAway && !isBeingDragged && Math.abs(mLayoutParams.x) < 50
                && Math.abs(mLayoutParams.y - (mContext.getResources().getDisplayMetrics().heightPixels / 2)) < 250) {
            flingAway();
        }
    }

    public void destroy() {
        mWindowManager.removeView(mIconView);
        if (mRemoveView != null) {
            mRemoveView.destroy();
        }
        if (mListener != null) {
            mListener.onIconDestroyed();
        }
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
