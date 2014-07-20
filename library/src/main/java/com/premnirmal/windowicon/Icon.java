package com.premnirmal.windowicon;

import android.content.Context;
import android.view.View;

/**
 * Created by prem on 7/20/14.
 */
public interface Icon {

    public View getIconView(Context context);

    public int getRemoveIconResID();

    public boolean shouldStickToWall();

    public boolean shouldFlingAway();
}
