package com.premnirmal.Magnet;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import com.facebook.rebound.Spring;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.spy;
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
  private final int iconWidth = 200;
  private final int iconHeight = 200;

  @Before public void setUp() throws Exception {
    final Display displayMock = mock(Display.class);
    final Resources resourcesMock = mock(Resources.class);
    final Context contextMock = mock(Context.class);

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

    Magnet instance = Magnet.newBuilder(contextMock).setIconView(iconViewMock)
        .setIconCallback(iconCallbackMock)
        .setRemoveIconResId(R.drawable.ic_close)
        .setRemoveIconShadow(R.drawable.bottom_shadow)
        .setShouldFlingAway(true)
        .setShouldStickToXWall(true)
        .setRemoveIconShouldBeResponsive(true)
        .setInitialPosition(initialX, initialY)
        .setIconWidth(iconWidth)
        .setIconHeight(iconHeight)
        .build();
    magnet = spy(instance);

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
    final Spring xSpringMock = mock(Spring.class);
    setInternalState(magnet, "xSpring", xSpringMock);

    // when
    magnet.goToWall();

    // then
    verify(xSpringMock).setEndValue(anyDouble());
  }

  @Test public void testMove() {
    // when
    magnet.setPosition(initialX, initialY);

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
    Context context = getInternalState(magnet, "context");
    assertNull("mContext field must be set to null after destroy method call", context);
  }

  @Test public void testSetPosition() {
    // when
    magnet.setPosition(initialX, initialY, false);

    // then
    final LayoutParams layoutParams = getInternalState(magnet, "layoutParams");
    assertEquals("SetPosition method must set x value of layoutParams field.", initialX,
        layoutParams.x);
    assertEquals("SetPosition method must set x value of layoutParams field.", initialY,
        layoutParams.y);
  }

  @Test public void testIconSize() {
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
