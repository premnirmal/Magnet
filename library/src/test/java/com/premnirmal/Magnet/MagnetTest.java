package com.premnirmal.Magnet;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.reflect.Whitebox.setInternalState;
import static org.powermock.reflect.internal.WhiteboxImpl.getInternalState;

@RunWith(PowerMockRunner.class) @PrepareForTest({ RemoveView.class, Magnet.class })
public class MagnetTest {

  private Magnet magnet;
  private IconCallback iconCallbackMock;
  private RemoveView removeViewMock;
  private WindowManager windowManagerMock;
  private ImageView iconViewMock;
  private View buttonMock;
  private DisplayMetrics displayMetricsMock;
  private LayoutParams paramsMock;
  private final int initialX = 2;
  private final int initialY = 4;
  private final int iconWidth = 200;
  private final int iconHeight = 200;
  private Spring mockXSpring;
  private Spring mockYSpring;

  @Before public void setUp() throws Exception {
    final Display displayMock = mock(Display.class);
    final Resources resourcesMock = mock(Resources.class);
    final Context contextMock = mock(Context.class);
    final ViewTreeObserver viewTreeObserverMock = mock(ViewTreeObserver.class);
    final SpringConfig config = SpringConfig.fromBouncinessAndSpeed(1, 20);
    displayMetricsMock = mock(DisplayMetrics.class);
    displayMetricsMock.widthPixels = initialX;
    displayMetricsMock.heightPixels = initialY;
    removeViewMock = mock(RemoveView.class);
    buttonMock = mock(View.class);
    windowManagerMock = mock(WindowManager.class);
    iconCallbackMock = mock(IconCallback.class);
    iconViewMock = mock(ImageView.class);
    paramsMock = mock(LayoutParams.class);
    mockXSpring = mock(Spring.class);
    mockYSpring = mock(Spring.class);

    doReturn(viewTreeObserverMock).when(iconViewMock).getViewTreeObserver();
    doReturn(windowManagerMock).when(contextMock).getSystemService(Context.WINDOW_SERVICE);
    doReturn(displayMetricsMock).when(resourcesMock).getDisplayMetrics();
    doReturn(displayMock).when(windowManagerMock).getDefaultDisplay();
    doReturn(resourcesMock).when(contextMock).getResources();
    setInternalState(removeViewMock, "button", buttonMock);
    whenNew(RemoveView.class).withArguments(contextMock).thenReturn(removeViewMock);
    whenNew(DisplayMetrics.class).withNoArguments().thenReturn(displayMetricsMock);
    whenNew(WindowManager.LayoutParams.class).withAnyArguments().thenReturn(paramsMock);
    doReturn(config).when(mockXSpring).getSpringConfig();
    doReturn(config).when(mockYSpring).getSpringConfig();

    Magnet instance = Magnet.newBuilder(contextMock).setIconView(iconViewMock)
        .setIconCallback(iconCallbackMock)
        .setRemoveIconResId(R.drawable.ic_close)
        .setRemoveIconShadow(R.drawable.bottom_shadow)
        .setShouldStickToWall(true)
        .setRemoveIconShouldBeResponsive(true)
        .setInitialPosition(initialX, initialY)
        .setIconWidth(iconWidth)
        .setIconHeight(iconHeight)
        .build();
    magnet = spy(instance);

    doReturn(mockXSpring).when(magnet).createXSpring(any(SpringSystem.class), any(SpringConfig.class));
    doReturn(mockYSpring).when(magnet).createYSpring(any(SpringSystem.class), any(SpringConfig.class));

    setInternalState(magnet, "layoutParams", paramsMock);
  }

  @Test public void testShow() throws Exception {
    // when
    magnet.show();

    // then
    verify(windowManagerMock).addView(iconViewMock, paramsMock);
    verify(magnet).setPosition(initialX, initialY);
  }

  @Test public void testAddToWindow() throws Exception {
    // given
    final View iconMock = mock(View.class);

    setInternalState(magnet, "iconView", iconMock);

    // when
    magnet.addToWindow();

    // then
    verify(windowManagerMock).addView(iconMock, paramsMock);
  }

  @Test public void testShowRemoveView() {
    // given
    when(removeViewMock.isShowing()).thenReturn(false);
    // when
    magnet.showRemoveView();

    // then
    verify(removeViewMock).show();
  }

  @Test public void testHideRemoveView() {
    // given
    when(removeViewMock.isShowing()).thenReturn(true);

    // when
    magnet.hideRemoveView();

    // then
    verify(removeViewMock).hide();
  }

  @Test public void testGoToWall() {
    // given
    magnet.show();
    reset(mockXSpring, mockYSpring);

    // when
    magnet.goToWall();

    // then
    verify(mockXSpring).setEndValue(anyDouble());
  }

  @Test public void testMove() {
    // given
    magnet.show();
    reset(mockXSpring, mockYSpring);
    when(mockXSpring.getCurrentValue()).thenReturn((double) initialX);
    when(mockYSpring.getCurrentValue()).thenReturn((double) initialY);
    doAnswer(new Answer<Void>() {
      @Override public Void answer(InvocationOnMock invocation) throws Throwable {
        Magnet.WindowManagerPerformer performer = magnet.xWindowManagerPerformer;
        performer.onSpringUpdate(mockXSpring);
        magnet.onSpringUpdate(mockXSpring);
        return null;
      }
    }).when(mockXSpring).setEndValue(anyDouble());
    doAnswer(new Answer<Void>() {
      @Override public Void answer(InvocationOnMock invocation) throws Throwable {
        Magnet.WindowManagerPerformer performer = magnet.yWindowManagerPerformer;
        performer.onSpringUpdate(mockYSpring);
        magnet.onSpringUpdate(mockYSpring);
        return null;
      }
    }).when(mockYSpring).setEndValue(anyDouble());
    setInternalState(removeViewMock, "shouldBeResponsive", true);
    when(removeViewMock.isShowing()).thenReturn(true);

    // when
    magnet.setPosition(initialX, initialY);

    // then
    verify(mockXSpring).setEndValue(initialX);
    verify(mockYSpring).setEndValue(initialY);
    verify(windowManagerMock, times(2)).updateViewLayout(eq(iconViewMock), eq(paramsMock));
    verify(removeViewMock, times(2)).onMove(anyInt(), anyInt());
    verify(iconCallbackMock, times(2)).onMove(anyInt(), anyInt());
  }

  @Test public void testFling() {
    // given
    magnet.show();
    when(mockXSpring.getCurrentValue()).thenReturn((double) initialX);
    when(mockYSpring.getCurrentValue()).thenReturn((double) initialY);
    doAnswer(new Answer<Void>() {
      @Override public Void answer(InvocationOnMock invocation) throws Throwable {
        Magnet.WindowManagerPerformer performer = magnet.xWindowManagerPerformer;
        performer.onSpringUpdate(mockXSpring);
        magnet.onSpringUpdate(mockXSpring);
        return null;
      }
    }).when(mockXSpring).setEndValue(anyDouble());
    doAnswer(new Answer<Void>() {
      @Override public Void answer(InvocationOnMock invocation) throws Throwable {
        Magnet.WindowManagerPerformer performer = magnet.yWindowManagerPerformer;
        performer.onSpringUpdate(mockYSpring);
        magnet.onSpringUpdate(mockYSpring);
        return null;
      }
    }).when(mockYSpring).setEndValue(anyDouble());
    setInternalState(removeViewMock, "shouldBeResponsive", true);
    when(removeViewMock.isShowing()).thenReturn(true);
    MotionEvent mockEvent = mock(MotionEvent.class);
    double velocity = magnet.flingVelocityMinimum + 1;
    when(magnet.xSpring.getVelocity()).thenReturn(velocity);
    when(magnet.ySpring.getVelocity()).thenReturn(velocity);
    doReturn(true).when(magnet).iconOverlapsWithRemoveView();

    // when
    magnet.motionImitatorX.release(mockEvent);

    // then
    assertEquals(magnet.isFlinging, true);

    // when
    mockYSpring.setEndValue(1500);

    // then
    verify(iconCallbackMock).onFlingAway();;
  }

  @Test public void testDestroy() {
    // given
    magnet.show();

    // when
    magnet.destroy();

    // then
    verify(windowManagerMock).removeView(iconViewMock);
    verify(removeViewMock).destroy();
    verify(iconCallbackMock).onIconDestroyed();
    Context context = getInternalState(magnet, "context");
    assertNull("mContext field must be set to null after destroy method call", context);
  }

  @Test public void testSetPosition() {
    // given
    magnet.show();
    reset(mockXSpring, mockYSpring);

    // when
    magnet.setPosition(initialX, initialY);

    // then
    verify(mockXSpring).setEndValue(initialX);
    verify(mockYSpring).setEndValue(initialY);
  }

  @Test public void testIconSize() {
    // given
    magnet.show();

    // when
    magnet.setIconSize(iconWidth, iconHeight);

    // then
    final LayoutParams layoutParams = getInternalState(magnet, "layoutParams");
    final int width = getInternalState(magnet, "iconWidth");
    final int height = getInternalState(magnet, "iconHeight");
    assertEquals("setIconWidth method must set the iconWidth value of the magnet", iconWidth, width);
    assertEquals("setIconWidth method must set the iconHeight value of the magnet", iconHeight, height);
    assertEquals("setIconWidth method must set the width value of the layout params field", iconWidth, layoutParams.width);
    assertEquals("setIconWidth method must set the height value of the layout params field", iconHeight, layoutParams.height);
  }
}
