package com.premnirmal.Magnet;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
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
  private DisplayMetrics displayMetricsMock;
  private LayoutParams paramsMock;
  private final int initialX = 2;
  private final int initialY = 4;

  @Before public void setUp() throws Exception {
    Display displayMock = mock(Display.class);
    Resources resourcesMock = mock(Resources.class);
    Context contextMock = mock(Context.class);

    displayMetricsMock = mock(DisplayMetrics.class);
    displayMetricsMock.widthPixels = initialX;
    displayMetricsMock.heightPixels = initialY;
    removeViewMock = mock(RemoveView.class);
    windowManagerMock = mock(WindowManager.class);
    iconCallbackMock = mock(IconCallback.class);
    iconViewMock = mock(ImageView.class);
    paramsMock = mock(LayoutParams.class);

    doReturn(windowManagerMock).when(contextMock).getSystemService(Context.WINDOW_SERVICE);
    doReturn(displayMetricsMock).when(resourcesMock).getDisplayMetrics();
    doReturn(displayMock).when(windowManagerMock).getDefaultDisplay();
    doReturn(resourcesMock).when(contextMock).getResources();

    whenNew(RemoveView.class).withArguments(contextMock).thenReturn(removeViewMock);
    whenNew(DisplayMetrics.class).withNoArguments().thenReturn(displayMetricsMock);
    whenNew(WindowManager.LayoutParams.class).withAnyArguments().thenReturn(paramsMock);

    magnet = new Magnet.Builder(contextMock).setIconView(iconViewMock)
        .setIconCallback(iconCallbackMock)
        .setRemoveIconResId(R.drawable.trash)
        .setRemoveIconShadow(R.drawable.bottom_shadow)
        .setShouldFlingAway(true)
        .setShouldStickToWall(true)
        .setRemoveIconShouldBeResponsive(true)
        .setInitialPosition(initialX, initialY)
        .build();

    setInternalState(magnet, "mLayoutParams", paramsMock);
  }

  @Test public void testShow() throws Exception {
    // given
    Magnet.MoveAnimator moveAnimatorMock = mock(Magnet.MoveAnimator.class);
    setInternalState(magnet, "mAnimator", moveAnimatorMock);

    // when
    magnet.show();

    // then
    verify(windowManagerMock).addView(iconViewMock, paramsMock);
    verify(moveAnimatorMock).start(initialX, initialY);
  }

  @Test public void testAddToWindow() throws Exception {
    // given
    View iconMock = mock(View.class);

    // when
    magnet.addToWindow(iconMock);

    // then
    verify(windowManagerMock).addView(iconMock, paramsMock);
  }

  @Test public void testUpdateSize() throws Exception {
    // when
    magnet.updateSize();

    // then
    int width = getInternalState(magnet, "mWidth");
    int height = getInternalState(magnet, "mHeight");
    assertEquals("updateSize method must set width value to displayMetricsMock.widthPixels / 2",
        displayMetricsMock.widthPixels / 2, width);
    assertEquals("updateSize method must set height value to displayMetricsMock.heightPixels / 2",
        displayMetricsMock.heightPixels / 2, height);
  }

  @Test public void testFlingAway() {
    // when
    magnet.flingAway();

    // then
    verify(iconCallbackMock).onFlingAway();
  }

  @Test public void testShowRemoveView() {
    // when
    magnet.showRemoveView();

    // then
    verify(removeViewMock).show();
  }

  @Test public void testHideRemoveView() {
    // when
    magnet.hideRemoveView();

    // then
    verify(removeViewMock).hide();
  }

  @Test public void testGoToWall() {
    // given
    Magnet.MoveAnimator moveAnimatorMock = mock(Magnet.MoveAnimator.class);
    setInternalState(magnet, "mAnimator", moveAnimatorMock);

    // when
    magnet.goToWall();

    // then
    verify(moveAnimatorMock).start(0, 0);
  }

  @Test public void testMove() {
    // when
    magnet.move(initialX, initialY);

    // then
    verify(removeViewMock).onMove(initialX, initialY);
    verify(iconCallbackMock).onMove(initialX, initialY);
  }

  @Test public void testDestroy() {
    // when
    magnet.destroy();

    // then
    verify(windowManagerMock).removeView(iconViewMock);
    verify(removeViewMock).destroy();
    verify(iconCallbackMock).onIconDestroyed();
    Context context = getInternalState(magnet, "mContext");
    assertNull("mContext field must be set to null after destroy method call", context);
  }

  @Test public void testSetPosition() {
    // when
    magnet.setPosition(initialX, initialY, false);

    // then
    LayoutParams layoutParams = getInternalState(magnet, "mLayoutParams");
    assertEquals("SetPosition method must set x value of layoutParams field.", initialX,
        layoutParams.x);
    assertEquals("SetPosition method must set x value of layoutParams field.", initialY,
        layoutParams.y);
  }
}
