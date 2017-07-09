package com.premnirmal.Magnet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.reflect.internal.WhiteboxImpl.getInternalState;

@RunWith(PowerMockRunner.class) @PrepareForTest({ RemoveView.class, LayoutInflater.class })
public class RemoveViewConstructorTest {

  @Test public void testConstructor() throws Exception {
    // given
    Context contextMock = mock(Context.class);
    LayoutInflater layoutInflaterMock = mock(LayoutInflater.class);

    mockStatic(LayoutInflater.class);
    when(LayoutInflater.from(contextMock)).thenReturn(layoutInflaterMock);
    View layoutMock = mock(View.class);
    View buttonMock = mock(View.class);
    View shadowMock = mock(View.class);
    ImageView buttonImageMock = mock(ImageView.class);
    WindowManager windowManagerMock = mock(WindowManager.class);
    SimpleAnimator simpleAnimatorMock = mock(SimpleAnimator.class);
    int buttonBottomPaddingTest = 10;

    whenNew(SimpleAnimator.class).withAnyArguments().thenReturn(simpleAnimatorMock);

    doReturn(layoutMock).when(layoutInflaterMock).inflate(R.layout.x_button_holder, null);
    doReturn(buttonMock).when(layoutMock).findViewById(R.id.xButton);
    doReturn(shadowMock).when(layoutMock).findViewById(R.id.shadow);
    doReturn(buttonImageMock).when(layoutMock).findViewById(R.id.xButtonImg);
    doReturn(windowManagerMock).when(contextMock).getSystemService(Context.WINDOW_SERVICE);
    doReturn(buttonBottomPaddingTest).when(buttonMock).getPaddingBottom();
    WindowManager.LayoutParams paramsMock = mock(WindowManager.LayoutParams.class);

    whenNew(WindowManager.LayoutParams.class).withAnyArguments().thenReturn(paramsMock);

    // when
    RemoveView removeView = new RemoveView(contextMock);

    // then
    View mLayout = getInternalState(removeView, "layout");
    View mButton = getInternalState(removeView, "button");
    View mShadow = getInternalState(removeView, "shadow");
    View mButtonImage = getInternalState(removeView, "buttonImage");
    WindowManager mWindowManager = getInternalState(removeView, "mWindowManager");
    int buttonBottomPadding = getInternalState(removeView, "buttonBottomPadding");

    // Verify all the fields set and view added to window
    assertEquals(
        "RemoveView's layout field must be equal to the return value of LayoutInflater.from() method.",
        layoutMock, mLayout);
    assertEquals(
        "RemoveView's button field must be equal to the return value of layout.findViewById(R.id.xButton) method.",
        buttonMock, mButton);
    assertEquals(
        "RemoveView's shadow field must be equal to the return value of layout.findViewById(R.id.shadow) method.",
        shadowMock, mShadow);
    assertEquals(
        "RemoveView's buttonImage field must be equal to the return value of layout.findViewById(R.id.xButtonImg) method.",
        buttonImageMock, mButtonImage);
    assertEquals(
        "RemoveView's mWindowManager field must be equal to the return value of contextMock.getSystemService(Context.WINDOW_SERVICE) method.",
        windowManagerMock, mWindowManager);
    assertEquals(
        "RemoveView's buttonBottomPadding field must be equal to the return value of button.getPaddingBottom() method.",
        buttonBottomPaddingTest, buttonBottomPadding);
    verify(mWindowManager).addView(layoutMock, paramsMock);
  }
}
