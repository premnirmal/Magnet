package com.premnirmal.Magnet;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.lang.ref.WeakReference;

/**
 * Created by prem on 7/20/14.
 * A class that takes care of animating a view in a simple way.
 */
public class SimpleAnimator {

  protected WeakReference<View> viewRef;
  protected int animation;

  protected SimpleAnimator(View view, int anim) {
    this.animation = anim;
    this.viewRef = new WeakReference<View>(view);
  }

  protected void startAnimation() {
    startAnimation(null);
  }

  protected void startAnimation(Animation.AnimationListener listener) {
    viewRef.get().clearAnimation();
    Animation anim = AnimationUtils.loadAnimation(viewRef.get().getContext(), animation);
    if (listener != null) {
      anim.setAnimationListener(listener);
    }
    anim.setFillAfter(true);
    viewRef.get().startAnimation(anim);
  }
}
