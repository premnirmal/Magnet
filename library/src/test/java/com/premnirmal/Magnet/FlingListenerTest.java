package com.premnirmal.Magnet;

import android.view.MotionEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertFalse;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class) public class FlingListenerTest {

  // Class being tested
  private FlingListener flingListener;

  @Before public void setUp() throws Exception {
    flingListener = new FlingListener();
  }

  @Test public void testOnFlingFalse() {
    // given
    MotionEvent motionEvent1 = mock(MotionEvent.class);
    MotionEvent motionEvent2 = mock(MotionEvent.class);

    // when
    boolean result = flingListener.onFling(motionEvent1, motionEvent2, 0, 0);

    // then
    assertFalse(result);
  }
}
