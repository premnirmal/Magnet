package com.premnirmal.Magnet;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.ref.WeakReference;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ View.class, AnimationUtils.class, SimpleAnimator.class })
public class SimpleAnimatorTest {

  // Class being tested
  private SimpleAnimator simpleAnimator;

  private Animation animation;
  private Animation.AnimationListener listener;
  private View view;

  @Before public void setUp() throws Exception {
    view = mock(View.class);
    WeakReference viewWeakReference = mock(WeakReference.class);
    whenNew(WeakReference.class).withArguments(view).thenReturn(viewWeakReference);
    doReturn(view).when(viewWeakReference).get();

    simpleAnimator = new SimpleAnimator(view, 1);

    mockStatic(AnimationUtils.class);
    animation = mock(Animation.class);
    doReturn(animation).when(AnimationUtils.class, "loadAnimation", null, 1);
  }

  @Test public void testStartAnimationWithListener() throws Exception {
    // given
    listener = mock(Animation.AnimationListener.class);

    // when
    simpleAnimator.startAnimation(listener);

    // then
    verify(view).startAnimation(animation);
  }

  @Test public void testStartAnimation() throws Exception {
    // when
    simpleAnimator.startAnimation();

    // then
    verify(animation, never()).setAnimationListener(listener);
    verify(view).startAnimation(animation);
  }
}
