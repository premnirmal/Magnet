package com.premnirmal.Magnet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.tumblr.backboard.Actor;
import com.tumblr.backboard.MotionProperty;
import com.tumblr.backboard.imitator.Imitator;
import com.tumblr.backboard.imitator.InertialImitator;
import com.tumblr.backboard.performer.Performer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by prem on 7/20/14.
 * Class that holds the Magnet icon, and performs touchEvents on the view.
 */
public class Magnet
    implements View.OnTouchListener, View.OnClickListener, ViewTreeObserver.OnGlobalLayoutListener,
    SpringListener {

  public static Builder<Magnet> newBuilder(Context context) {
    return new MagnetBuilder(context);
  }

  private static class MagnetBuilder extends Builder<Magnet> {

    MagnetBuilder(Context context) {
      super(Magnet.class, context);
    }

    @Override public Magnet build() {
      return super.build();
    }
  }

  /**
   * Builder class to create your {@link Magnet}
   */
  public static class Builder<T extends Magnet> {

    protected T magnet;

    /**
     * Used to instantiate your subclass of {@link Magnet}
     *
     * @param clazz your subclass
     */
    public Builder(Class<T> clazz, @NonNull Context context) {
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
     * The Icon must have a view, provide a view or a layout using {@link #setIconView(int)}
     *
     * @param iconView the view representing the icon
     */
    public Builder<T> setIconView(@NonNull View iconView) {
      magnet.iconView = iconView;
      return this;
    }

    /**
     * Use an xml layout to provide the button view
     *
     * @param iconViewRes the layout id of the icon
     */
    public Builder<T> setIconView(@LayoutRes int iconViewRes) {
      return setIconView(LayoutInflater.from(magnet.context).inflate(iconViewRes, null));
    }

    /**
     * whether your magnet sticks to the edge your screen when you release it
     *
     * @deprecated use {@link #setShouldStickToXWall(boolean)} and {@link #setShouldStickToYWall(boolean)}
     */
    @Deprecated public Builder<T> setShouldStickToWall(boolean shouldStick) {
      magnet.shouldStickToXWall = shouldStick;
      magnet.shouldStickToYWall = shouldStick;
      return this;
    }

    /**
     * Whether your magnet sticks to the left or right edge of your screen when you release it
     */
    public Builder<T> setShouldStickToXWall(boolean shouldStick) {
      magnet.shouldStickToXWall = shouldStick;
      return this;
    }

    /**
     * Whether your magnet sticks to the top or bottom edge of your screen when you release it
     */
    public Builder<T> setShouldStickToYWall(boolean shouldStick) {
      magnet.shouldStickToYWall = shouldStick;
      return this;
    }

    /**
     * Whether you can fling away your Magnet towards the bottom of the screen
     *
     * @deprecated use {@link #setShouldShowRemoveView(boolean)} instead
     */
    @Deprecated public Builder<T> setShouldFlingAway(boolean shouldFling) {
      return this;
    }

    /**
     * Callback for when the icon moves, is clicked, is flinging away, and destroyed
     */
    public Builder<T> setIconCallback(IconCallback callback) {
      magnet.iconCallback = callback;
      return this;
    }

    /**
     * Whether the remove icon should be shown
     */
    public Builder<T> setShouldShowRemoveView(boolean showRemoveView) {
      magnet.shouldShowRemoveView = showRemoveView;
      return this;
    }

    /**
     * Whether the remove icon should respond to touch movements
     */
    public Builder<T> setRemoveIconShouldBeResponsive(boolean shouldBeResponsive) {
      magnet.removeView.shouldBeResponsive = shouldBeResponsive;
      return this;
    }

    /**
     * You can set a custom remove icon or use the default one
     */
    public Builder<T> setRemoveIconResId(int removeIconResId) {
      magnet.removeView.setIconResId(removeIconResId);
      return this;
    }

    /**
     * You can set a custom remove icon shadow or use the default one
     */
    public Builder<T> setRemoveIconShadow(int shadow) {
      magnet.removeView.setShadowBG(shadow);
      return this;
    }

    /**
     * Set the initial coordinates of the magnet in pixels
     */
    public Builder<T> setInitialPosition(int x, int y) {
      magnet.initialX = x;
      magnet.initialY = y;
      return this;
    }

    /**
     * Set a custom width for the icon view in pixels. default is {@link
     * WindowManager.LayoutParams#WRAP_CONTENT}
     */
    public Builder<T> setIconWidth(int width) {
      magnet.iconWidth = width;
      return this;
    }

    /**
     * Set a custom height for the icon view in pixels. default is {@link
     * WindowManager.LayoutParams#WRAP_CONTENT}
     */
    public Builder<T> setIconHeight(int height) {
      magnet.iconHeight = height;
      return this;
    }

    /**
     * Set the configuration for the springs used by this magnet.
     */
    public Builder<T> withSpringConfig(@NonNull SpringConfig springConfig) {
      magnet.springConfig = springConfig;
      return this;
    }

    public T build() {
      if (magnet.iconView == null) {
        throw new NullPointerException("IconView is null!");
      }
      return magnet;
    }
  }

  protected static double distSq(double x1, double y1, double x2, double y2) {
    return Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2);
  }

  protected View iconView;
  protected RemoveView removeView;
  protected WindowManager windowManager;
  protected WindowManager.LayoutParams layoutParams;
  protected Context context;
  protected IconCallback iconCallback;
  protected final BroadcastReceiver orientationChangeReceiver;

  protected SpringConfig springConfig;
  protected Spring xSpring, ySpring;
  protected Actor actor;
  protected MagnetImitator motionImitatorX;
  protected MagnetImitator motionImitatorY;
  protected WindowManagerPerformer xWindowManagerPerformer;
  protected WindowManagerPerformer yWindowManagerPerformer;
  protected int xMinValue, xMaxValue;
  protected int yMinValue, yMaxValue;
  protected int iconWidth = -1, iconHeight = -1;
  protected int initialX = -1, initialY = -1;
  protected int[] iconPosition = new int[2];

  protected boolean shouldShowRemoveView = true;
  protected float goToWallVelocity;
  protected float flingVelocityMinimum;
  protected float restVelocity;
  protected boolean shouldStickToYWall = false;
  protected boolean shouldStickToXWall = true;
  protected long lastTouchDown;
  protected boolean isBeingDragged;
  protected boolean addedToWindow;
  protected boolean isFlinging;
  protected boolean isSnapping;
  protected boolean isGoingToWall;

  public Magnet(Context context) {
    this.context = context;
    orientationChangeReceiver = new OrientationChangeReceiver();
    windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    removeView = new RemoveView(context);
    goToWallVelocity = pxFromDp(700);
    flingVelocityMinimum = pxFromDp(400);
    restVelocity = pxFromDp(100);
    springConfig = SpringConfig.fromBouncinessAndSpeed(1, 20);
  }

  @NonNull protected SpringSystem getSpringSystem() {
    return SpringSystem.create();
  }

  @NonNull protected SpringConfig getSpringConfig() {
    return springConfig;
  }

  protected Spring createXSpring(SpringSystem springSystem, SpringConfig config) {
    Spring spring = springSystem.createSpring();
    spring.setSpringConfig(config);
    spring.setRestSpeedThreshold(restVelocity);
    return spring;
  }

  protected Spring createYSpring(SpringSystem springSystem, SpringConfig config) {
    Spring spring = springSystem.createSpring();
    spring.setSpringConfig(config);
    spring.setRestSpeedThreshold(restVelocity);
    return spring;
  }

  protected void initializeMotionPhysics() {
    SpringConfig config = getSpringConfig();
    SpringSystem springSystem = getSpringSystem();
    xSpring = createXSpring(springSystem, config);
    ySpring = createYSpring(springSystem, config);
    motionImitatorX =
        new MagnetImitator(MotionProperty.X, Imitator.TRACK_ABSOLUTE, Imitator.FOLLOW_SPRING, 0, 0);
    motionImitatorY =
        new MagnetImitator(MotionProperty.Y, Imitator.TRACK_ABSOLUTE, Imitator.FOLLOW_SPRING, 0, 0);
    xWindowManagerPerformer = new WindowManagerPerformer(MotionProperty.X);
    yWindowManagerPerformer = new WindowManagerPerformer(MotionProperty.Y);
    actor = new Actor.Builder(springSystem, iconView).addMotion(xSpring, motionImitatorX,
        xWindowManagerPerformer)
        .addMotion(ySpring, motionImitatorY, yWindowManagerPerformer)
        .onTouchListener(this)
        .build();
    iconView.getViewTreeObserver().addOnGlobalLayoutListener(this);
  }

  protected int getStatusBarHeight() {
    int result = 0;
    int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      result = context.getResources().getDimensionPixelSize(resourceId);
    }
    return result;
  }

  protected int getNavBarHeight() {
    int result = 0;
    int resourceId =
        context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
    if (resourceId > 0) {
      return context.getResources().getDimensionPixelSize(resourceId);
    }
    return result;
  }

  protected void addToWindow() {
    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
        iconWidth > 0 ? iconWidth : WindowManager.LayoutParams.WRAP_CONTENT,
        iconHeight > 0 ? iconHeight : WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSPARENT);
    params.gravity = Gravity.TOP | Gravity.START;
    windowManager.addView(iconView, layoutParams = params);
    addedToWindow = true;
  }

  protected float pxFromDp(float dp) {
    return dp * context.getResources().getDisplayMetrics().density;
  }

  protected boolean iconOverlapsWithRemoveView() {
    if (removeView.isShowing()) {
      View firstView = removeView.button;
      View secondView = iconView;
      int[] firstPosition = new int[2];
      int[] secondPosition = new int[2];

      firstView.getLocationOnScreen(firstPosition);
      secondView.getLocationOnScreen(secondPosition);

      // Rect constructor parameters: left, top, right, bottom
      Rect rectFirstView = new Rect(firstPosition[0], firstPosition[1],
          firstPosition[0] + firstView.getMeasuredWidth(),
          firstPosition[1] + firstView.getMeasuredHeight());
      Rect rectSecondView = new Rect(secondPosition[0], secondPosition[1],
          secondPosition[0] + secondView.getMeasuredWidth(),
          secondPosition[1] + secondView.getMeasuredHeight());
      return rectFirstView.intersect(rectSecondView);
    }
    return false;
  }

  protected void showRemoveView() {
    if (removeView != null && shouldShowRemoveView && !removeView.isShowing()) {
      removeView.show();
    }
  }

  protected void hideRemoveView() {
    if (removeView != null && shouldShowRemoveView && removeView.isShowing()) {
      removeView.hide();
    }
  }

  protected void onOrientationChange() {
    iconView.getViewTreeObserver().addOnGlobalLayoutListener(Magnet.this);
  }

  /**
   * Show the Magnet i.e. add it to the Window
   */
  public void show() {
    addToWindow();
    iconView.setOnClickListener(this);
    initializeMotionPhysics();
    if (initialX != -1 || initialY != -1) {
      setPosition(initialX, initialY);
    } else {
      goToWall();
    }
    xSpring.addListener(this);
    ySpring.addListener(this);
    context.registerReceiver(orientationChangeReceiver,
        new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED));
  }

  /**
   * Move the icon to the given position
   *
   * @param x The x coordinate to move to in pixels
   * @param y The y coordinate to move to in pixels
   * @param animate Whether to animate to the position. This param is deprecated and will be
   * ignored
   * @deprecated use {@link #setPosition(int, int)}
   */
  @Deprecated public void setPosition(int x, int y, boolean animate) {
    setPosition(x, y);
  }

  /**
   * Move the icon to the given position
   *
   * @param x The x coordinate to move to in pixels
   * @param y The y coordinate to move to in pixels
   */
  public void setPosition(int x, int y) {
    actor.removeAllListeners();
    xSpring.setEndValue(x);
    ySpring.setEndValue(y);
    actor.addAllListeners();
  }

  /**
   * Update the icon view size after the magnet has been shown
   *
   * @param width the width of the icon view in pixels
   * @param height the height of the icon view in pixels
   */
  public void setIconSize(int width, int height) {
    iconWidth = width;
    iconHeight = height;
    if (addedToWindow) {
      layoutParams.width = width;
      layoutParams.height = height;
      windowManager.updateViewLayout(iconView, layoutParams);
    }
  }

  /**
   * Move the magnet to the nearest wall
   * See {@link Builder#setShouldStickToXWall(boolean)}
   */
  public void goToWall() {
    if ((shouldStickToXWall || shouldStickToYWall) && !isGoingToWall) {
      isGoingToWall = true;
      iconView.getLocationOnScreen(iconPosition);
      boolean endX = iconPosition[0] > context.getResources().getDisplayMetrics().widthPixels / 2;
      boolean endY = iconPosition[1] > context.getResources().getDisplayMetrics().heightPixels / 2;
      float nearestXWall = endX ? xMaxValue : xMinValue;
      float nearestYWall = endY ? yMaxValue : yMinValue;
      actor.removeAllListeners();
      if (shouldStickToXWall && (!shouldStickToYWall
          || Math.abs(iconPosition[0] - nearestXWall) < Math.abs(iconPosition[1] - nearestYWall))) {
        xSpring.setEndValue(nearestXWall);
        float velocity = iconPosition[0] > nearestXWall ? -goToWallVelocity : goToWallVelocity;
        if (endX) {
          xSpring.setVelocity(velocity);
        } else {
          xSpring.setVelocity(velocity);
        }
      } else if (shouldStickToYWall) {
        float velocity = iconPosition[1] > nearestYWall ? -goToWallVelocity : goToWallVelocity;
        ySpring.setEndValue(nearestYWall);
        if (endY) {
          ySpring.setVelocity(velocity);
        } else {
          ySpring.setVelocity(velocity);
        }
      }
      actor.addAllListeners();
    }
  }

  /**
   * Destroys the magnet - removes the view from the WindowManager and calls
   * {@link IconCallback#onIconDestroyed()}
   */
  public void destroy() {
    actor.removeAllListeners();
    xSpring.setAtRest();
    ySpring.setAtRest();
    windowManager.removeView(iconView);
    context.unregisterReceiver(orientationChangeReceiver);
    if (removeView != null) {
      removeView.destroy();
    }
    if (iconCallback != null) {
      iconCallback.onIconDestroyed();
    }
    context = null;
  }

  // ViewTreeObserver.OnGlobalLayoutListener

  @Override public void onGlobalLayout() {
    xMinValue = -iconView.getMeasuredWidth() / 2;
    motionImitatorX.setMinValue(xMinValue);
    xMaxValue =
        context.getResources().getDisplayMetrics().widthPixels - iconView.getMeasuredWidth() / 2;
    motionImitatorX.setMaxValue(xMaxValue);
    yMinValue = getStatusBarHeight() - iconView.getMeasuredHeight() / 2;
    motionImitatorY.setMinValue(yMinValue);
    yMaxValue = context.getResources().getDisplayMetrics().heightPixels
        - getNavBarHeight()
        - iconView.getMeasuredHeight() / 2;
    motionImitatorY.setMaxValue(yMaxValue);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      iconView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    } else {
      iconView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }
    goToWall();
  }

  // View.OnTouchListener

  @Override public boolean onTouch(View view, MotionEvent event) {
    int action = event.getAction();
    if (action == MotionEvent.ACTION_DOWN) {
      isBeingDragged = true;
      lastTouchDown = System.currentTimeMillis();
      return true;
    } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
      isBeingDragged = false;
      return true;
    }
    return false;
  }

  // View.OnClickListener

  @Override public void onClick(View view) {
    xSpring.setAtRest();
    ySpring.setAtRest();
    view.getLocationOnScreen(iconPosition);
    if (iconCallback != null) {
      iconCallback.onIconClick(view, iconPosition[0], iconPosition[1]);
    }
  }

  // SpringListener

  @Override public void onSpringUpdate(Spring spring) {
    iconView.getLocationOnScreen(iconPosition);
    if (iconCallback != null) {
      iconCallback.onMove(iconPosition[0], iconPosition[1]);
    }
  }

  @Override public void onSpringAtRest(Spring spring) {

  }

  @Override public void onSpringActivate(Spring spring) {

  }

  @Override public void onSpringEndStateChange(Spring spring) {

  }

  class OrientationChangeReceiver extends BroadcastReceiver {

    @Override public void onReceive(Context context, Intent intent) {
      onOrientationChange();
    }
  }

  protected class MagnetImitator extends InertialImitator {

    protected MagnetImitator(@NonNull MotionProperty property, int trackStrategy,
        int followStrategy, double minValue, double maxValue) {
      super(property, trackStrategy, followStrategy, minValue, maxValue);
    }

    protected boolean canSnap(float x, float y) {
      if (!removeView.isShowing()) {
        return false;
      }
      View view = removeView.button;
      int[] removeViewPosition = new int[2];
      view.getLocationOnScreen(removeViewPosition);
      double distSq = distSq(x, y, removeViewPosition[0] + view.getMeasuredWidth() / 2,
          removeViewPosition[1] + view.getMeasuredHeight() / 2);
      return distSq < Math.pow(1.5f * view.getMeasuredWidth(), 2);
    }

    @Override public void constrain(MotionEvent event) {
      super.constrain(event);
      showRemoveView();
      isSnapping = false;
      isFlinging = false;
    }

    @Override public void release(MotionEvent event) {
      super.release(event);
      if (!isGoingToWall && !isSnapping && (Math.abs(ySpring.getVelocity()) >= flingVelocityMinimum
          || Math.abs(xSpring.getVelocity()) >= flingVelocityMinimum)) {
        isFlinging = true;
      }
      if (!isFlinging && !isSnapping) {
        goToWall();
        hideRemoveView();
      } else if (isSnapping) {
        if (iconOverlapsWithRemoveView()) {
          if (mProperty == MotionProperty.Y) {
            isSnapping = false;
            isFlinging = false;
            if (iconCallback != null) {
              iconCallback.onFlingAway();
            }
          }
          hideRemoveView();
        }
      } else if (isFlinging) {
        hideRemoveView();
      }
    }

    @Override public void imitate(final View view, @NonNull final MotionEvent event) {
      final float viewValue;
      if (mProperty == MotionProperty.X) {
        viewValue = layoutParams.x;
      } else if (mProperty == MotionProperty.Y) {
        viewValue = layoutParams.y;
      } else {
        viewValue = 0;
      }
      final float eventValue = mProperty.getValue(event);
      mOffset = mProperty.getOffset(view);
      if (event.getHistorySize() > 0) {
        final float historicalValue = mProperty.getOldestValue(event);
        imitate(viewValue + mOffset, eventValue, eventValue - historicalValue, event);
      } else {
        imitate(viewValue + mOffset, eventValue, 0, event);
      }
    }

    @Override
    public void mime(float offset, float value, float delta, float dt, MotionEvent event) {
      if (iconOverlapsWithRemoveView() && canSnap(event.getRawX(), event.getRawY())) {
        isSnapping = true;
        // snap to it - remember to compensate for translation
        int[] removeViewPosition = new int[2];
        removeView.button.getLocationOnScreen(removeViewPosition);
        switch (mProperty) {
          case X:
            int midPoint = context.getResources().getDisplayMetrics().widthPixels / 2;
            getSpring().setEndValue(midPoint - (iconView.getWidth() / 2));
            break;
          case Y:
            getSpring().setEndValue(removeViewPosition[1] - iconView.getHeight() / 2);
            break;
        }
      } else {
        // follow finger
        isSnapping = false;
        super.mime(offset, value, delta, dt, event);
      }
    }
  }

  protected class WindowManagerPerformer extends Performer {

    protected final MotionProperty motionProperty;

    protected WindowManagerPerformer(MotionProperty motionProperty) {
      super(null, null);
      this.motionProperty = motionProperty;
    }

    @Override public void onSpringUpdate(@NonNull Spring spring) {
      double currentValue = spring.getCurrentValue();
      if (motionProperty == MotionProperty.X) {
        layoutParams.x = (int) currentValue;
        windowManager.updateViewLayout(iconView, layoutParams);
      } else if (motionProperty == MotionProperty.Y) {
        layoutParams.y = (int) currentValue;
        windowManager.updateViewLayout(iconView, layoutParams);
      }
      if (removeView.isShowing()) {
        removeView.onMove(layoutParams.x, layoutParams.y);
      }
      if (isFlinging && !isBeingDragged && iconOverlapsWithRemoveView()) {
        if (motionProperty == MotionProperty.Y) {
          isSnapping = false;
          isFlinging = false;
          if (iconCallback != null) {
            iconCallback.onFlingAway();
          }
        }
        hideRemoveView();
      }
    }

    @Override public void onSpringAtRest(Spring spring) {
      super.onSpringAtRest(spring);
      isGoingToWall = false;
      if (!isSnapping && !isBeingDragged) {
        hideRemoveView();
      }
    }
  }
}
