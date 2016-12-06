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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by prem on 7/20/14.
 * Desc: Class holding the Magnet Icon, and performing touchEvents on the view.
 */
public class Magnet implements View.OnTouchListener {

  protected static final int TOUCH_TIME_THRESHOLD = 200;

  protected View mIconView;
  protected RemoveView mRemoveView;
  protected WindowManager mWindowManager;
  protected WindowManager.LayoutParams mLayoutParams;
  protected Context mContext;
  protected GestureDetector mGestureDetector;
  protected boolean shouldStickToWall = true;
  protected boolean shouldFlingAway = true;
  protected IconCallback mListener;
  protected MoveAnimator mAnimator;

  protected long lastTouchDown;
  protected float lastXPose, lastYPose;
  protected boolean isBeingDragged = false;
  protected int mWidth, mHeight;
  protected int mIconWidth = -1, mIconHeight = -1;

  protected int mInitialX = -1, mInitialY = -1;

  /**
   * Builder class to create your {@link Magnet}
   */
  public static class Builder {

    protected Magnet magnet;

    /**
     * Used to instantiate your subclass of {@link Magnet}
     *
     * @param clazz your subclass
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public <T extends Magnet> Builder(Class<T> clazz, Context context) {
      final Constructor<T> constructor;
      try {
        constructor = clazz.getDeclaredConstructor(Context.class);
        constructor.setAccessible(true);
        magnet = constructor.newInstance(context);
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      } catch (InstantiationException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      } catch (InvocationTargetException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }
    }

    /**
     * Instantiate a {@link Magnet}
     */
    public Builder(Context context) {
      magnet = new Magnet(context);
    }

    /**
     * The Icon must have a view, provide a view or a layout using {@link #setIconView(int)}
     *
     * @param iconView the view representing the icon
     */
    public Builder setIconView(View iconView) {
      magnet.mIconView = iconView;
      magnet.mIconView.setOnTouchListener(magnet);
      return this;
    }

    /**
     * Use an xml layout to provide the button view
     *
     * @param iconViewRes the layout id of the icon
     */
    public Builder setIconView(int iconViewRes) {
      magnet.mIconView = LayoutInflater.from(magnet.mContext).inflate(iconViewRes, null);
      magnet.mIconView.setOnTouchListener(magnet);
      return this;
    }

    /**
     * whether your magnet sticks to the edge of your screen when you release it
     */
    public Builder setShouldStickToWall(boolean shouldStick) {
      magnet.shouldStickToWall = shouldStick;
      return this;
    }

    /**
     * whether you can fling away your Magnet towards the bottom of the screen
     */
    public Builder setShouldFlingAway(boolean shoudlFling) {
      magnet.shouldFlingAway = shoudlFling;
      return this;
    }

    /**
     * Callback for when the icon moves, or when it isis flung away and destroyed
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
     */
    public Builder setRemoveIconResId(int removeIconResId) {
      magnet.mRemoveView.setIconResId(removeIconResId);
      return this;
    }

    /**
     * you can set a custom remove icon shadow or use the default one
     */
    public Builder setRemoveIconShadow(int shadow) {
      magnet.mRemoveView.setShadowBG(shadow);
      return this;
    }

    /**
     * Set the initial coordinates of the magnet
     */
    public Builder setInitialPosition(int x, int y) {
      magnet.mInitialX = x;
      magnet.mInitialY = y;
      return this;
    }

    /**
     * Set a custom width for the icon view. default is {@link WindowManager.LayoutParams#WRAP_CONTENT}
     */
    public Builder setIconWidth(int width) {
      magnet.mIconWidth = width;
      return this;
    }

    /**
     * * Set a custom height for the icon view. default is {@link WindowManager.LayoutParams#WRAP_CONTENT}
     */
    public Builder setIconHeight(int height) {
      magnet.mIconHeight = height;
      return this;
    }

    public Magnet build() {
      if (magnet.mIconView == null) {
        throw new NullPointerException("Magnet view is null! Must set a view for the magnet!");
      }
      return magnet;
    }
  }

  protected Magnet(Context context) {
    mContext = context;
    mGestureDetector = new GestureDetector(context, new FlingListener());
    mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    mAnimator = new MoveAnimator();
    mRemoveView = new RemoveView(context);
  }

  /**
   * Show the Magnet i.e. add it to the Window
   */
  public void show() {
    addToWindow(mIconView);
    updateSize();
    if (mInitialX != -1 || mInitialY != -1) {
      setPosition(mInitialX, mInitialY, true);
    } else {
      goToWall();
    }
  }

  protected void addToWindow(View icon) {
    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
        mIconWidth > 0 ? mIconWidth : WindowManager.LayoutParams.WRAP_CONTENT,
        mIconHeight > 0 ? mIconHeight : WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT);
    mWindowManager.addView(icon, mLayoutParams = params);
  }

  protected void updateSize() {
    final DisplayMetrics metrics = new DisplayMetrics();
    mWindowManager.getDefaultDisplay().getMetrics(metrics);
    mWidth = (metrics.widthPixels - (mIconWidth > 0 ? mIconWidth : mIconView.getWidth())) / 2;
    mHeight = (metrics.heightPixels - (mIconHeight > 0 ? mIconHeight : mIconView.getHeight())) / 2;
  }

  @Override public boolean onTouch(View view, MotionEvent event) {
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

  protected void flingAway() {
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

  protected void showRemoveView() {
    if (mRemoveView != null && shouldFlingAway) {
      mRemoveView.show();
    }
  }

  protected void hideRemoveView() {
    if (mRemoveView != null && shouldFlingAway) {
      mRemoveView.hide();
    }
  }

  protected void goToWall() {
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

  protected void move(float deltaX, float deltaY) {
    mLayoutParams.x += deltaX;
    mLayoutParams.y += deltaY;
    if (mRemoveView != null && shouldFlingAway) {
      mRemoveView.onMove(mLayoutParams.x, mLayoutParams.y);
    }
    mWindowManager.updateViewLayout(mIconView, mLayoutParams);
    if (mListener != null) {
      mListener.onMove(mLayoutParams.x, mLayoutParams.y);
    }
    if (shouldFlingAway
        && !isBeingDragged
        && Math.abs(mLayoutParams.x) < (mIconWidth > 0 ? mIconWidth : 50)
        && Math.abs(
        mLayoutParams.y - (mContext.getResources().getDisplayMetrics().heightPixels / 2)) < (
        mIconHeight > 0 ? mIconHeight * 3 : 250)) {
      flingAway();
    }
  }

  /**
   * Destroys the magnet - removes the view from the WindowManager and calls
   * {@link IconCallback#onIconDestroyed()}
   */
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

  /**
   * Set the position of the Magnet.
   * Note: must be called **after** {@link #show()} is called.
   * This will call {@link IconCallback#onMove(float, float)} on your listener
   *
   * @param x the x position
   * @param y the y position
   * @param animate whether the Magnet should animate to that position. If false the Magnet
   * will simply just set its coordinates to the given position
   */
  public void setPosition(int x, int y, boolean animate) {
    if (animate) {
      mAnimator.start(x, y);
    } else {
      mLayoutParams.x = x;
      mLayoutParams.y = y;
      mWindowManager.updateViewLayout(mIconView, mLayoutParams);
      if (mListener != null) {
        mListener.onMove(mLayoutParams.x, mLayoutParams.y);
      }
    }
  }

  /**
   * Update the icon view size after the magnet has been shown
   * @param width the width of the icon view
   * @param height the height of the icon view
   */
  public void setIconSize(int width, int height) {
    mIconWidth = width;
    mIconHeight = height;
    mLayoutParams.width = width;
    mLayoutParams.height = height;
    mWindowManager.updateViewLayout(mIconView, mLayoutParams);
  }

  public class MoveAnimator implements Runnable {

    protected Handler handler = new Handler(Looper.getMainLooper());
    public float destinationX;
    public float destinationY;
    public long startingTime;

    public void start(float x, float y) {
      this.destinationX = x;
      this.destinationY = y;
      startingTime = System.currentTimeMillis();
      handler.post(this);
    }

    @Override public void run() {
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

    public void stop() {
      handler.removeCallbacks(this);
    }
  }
}
