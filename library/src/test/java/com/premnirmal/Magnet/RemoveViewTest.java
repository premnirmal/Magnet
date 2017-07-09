package com.premnirmal.Magnet;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.reflect.internal.WhiteboxImpl.getInternalState;

@RunWith(PowerMockRunner.class) @PrepareForTest({ RemoveView.class, LayoutInflater.class })
public class RemoveViewTest {

  private RemoveView removeView;
  private ImageView buttonImageMock;
  private View shadowMock;
  private SimpleAnimator simpleAnimatorMockFadeIn;
  private SimpleAnimator simpleAnimatorMockFadeOut;
  private View buttonMock;
  private Context contextMock;
  private int buttonBottomPaddingTest = 10;

  @Before public void setUp() throws Exception {
    contextMock = mock(Context.class);
    LayoutInflater layoutInflaterMock = mock(LayoutInflater.class);

    mockStatic(LayoutInflater.class);
    when(LayoutInflater.from(contextMock)).thenReturn(layoutInflaterMock);
    View layoutMock = mock(View.class);
    buttonMock = mock(View.class);
    shadowMock = mock(View.class);
    buttonImageMock = mock(ImageView.class);
    WindowManager windowManagerMock = mock(WindowManager.class);
    SimpleAnimator simpleAnimatorMockShow = mock(SimpleAnimator.class);
    SimpleAnimator simpleAnimatorMockHide = mock(SimpleAnimator.class);
    simpleAnimatorMockFadeIn = mock(SimpleAnimator.class);
    simpleAnimatorMockFadeOut = mock(SimpleAnimator.class);

    whenNew(SimpleAnimator.class).withArguments(buttonMock, R.anim.slide_up)
        .thenReturn(simpleAnimatorMockShow);
    whenNew(SimpleAnimator.class).withArguments(buttonMock, R.anim.slide_down)
        .thenReturn(simpleAnimatorMockHide);
    whenNew(SimpleAnimator.class).withArguments(shadowMock, android.R.anim.fade_in)
        .thenReturn(simpleAnimatorMockFadeIn);
    whenNew(SimpleAnimator.class).withArguments(shadowMock, android.R.anim.fade_out)
        .thenReturn(simpleAnimatorMockFadeOut);

    doReturn(layoutMock).when(layoutInflaterMock).inflate(R.layout.x_button_holder, null);
    doReturn(buttonMock).when(layoutMock).findViewById(R.id.xButton);
    doReturn(shadowMock).when(layoutMock).findViewById(R.id.shadow);
    doReturn(buttonImageMock).when(layoutMock).findViewById(R.id.xButtonImg);
    doReturn(windowManagerMock).when(contextMock).getSystemService(Context.WINDOW_SERVICE);
    doReturn(buttonBottomPaddingTest).when(buttonMock).getPaddingBottom();
    WindowManager.LayoutParams paramsMock = mock(WindowManager.LayoutParams.class);

    whenNew(WindowManager.LayoutParams.class).withAnyArguments().thenReturn(paramsMock);

    removeView = new RemoveView(contextMock);
  }

  @Test public void testSetIconResId() {
    // given
    int iconResId = R.drawable.bottom_shadow;

    // when
    removeView.setIconResId(iconResId);

    // then
    verify(buttonImageMock).setImageResource(iconResId);
  }

  @Test public void testSetShadowBG() {
    // given
    int bottomShadow = R.drawable.bottom_shadow;

    // when
    removeView.setShadowBG(bottomShadow);

    // then
    verify(shadowMock).setBackgroundResource(bottomShadow);
  }

  @Test public void testShow() {
    // when
    removeView.show();

    // then
    verify(simpleAnimatorMockFadeIn).startAnimation();
    verify(simpleAnimatorMockFadeOut).startAnimation();
  }

  @Test public void testOnMoveXBiggerThanZero() {
    // given
    DisplayMetrics displayMetricsMock = mock(DisplayMetrics.class);
    Resources resourcesMock = mock(Resources.class);
    doReturn(contextMock).when(buttonMock).getContext();
    doReturn(resourcesMock).when(contextMock).getResources();
    doReturn(displayMetricsMock).when(resourcesMock).getDisplayMetrics();
    displayMetricsMock.widthPixels = 2;

    int x = 1;
    int xTransformed = Math.abs(x * 100 / (displayMetricsMock.widthPixels / 2));
    int bottomPadding = buttonBottomPaddingTest - (xTransformed / 5);

    // when
    removeView.onMove(x, 1);

    // then
    verify(buttonMock).setPadding(xTransformed, 0, 0, bottomPadding);
  }

  @Test public void testOnMoveXSmallerThanZero() {
    // given
    DisplayMetrics displayMetricsMock = mock(DisplayMetrics.class);
    Resources resourcesMock = mock(Resources.class);
    doReturn(contextMock).when(buttonMock).getContext();
    doReturn(resourcesMock).when(contextMock).getResources();
    doReturn(displayMetricsMock).when(resourcesMock).getDisplayMetrics();
    displayMetricsMock.widthPixels = 2;

    int x = -1;
    int xTransformed = Math.abs(x * 100 / (displayMetricsMock.widthPixels / 2));
    int bottomPadding = buttonBottomPaddingTest - (xTransformed / 5);

    // when
    removeView.onMove(x, 1);

    // then
    verify(buttonMock).setPadding(0, 0, xTransformed, bottomPadding);
  }

  @Test public void testDestroy() {
    // given
    assertNotNull("layout field musn't be null before destroy method call",
        getInternalState(removeView, "layout"));
    assertNotNull("mWindowManager field musn't be null before destroy method call",
        getInternalState(removeView, "mWindowManager"));

    // when
    removeView.destroy();

    // then
    assertNull("layout field must be set to null after destroy method call",
        getInternalState(removeView, "layout"));
    assertNull("mWindowManager field must be set to null after destroy method call",
        getInternalState(removeView, "mWindowManager"));
  }
}

