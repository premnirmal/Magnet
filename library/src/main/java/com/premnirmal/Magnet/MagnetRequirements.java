package com.premnirmal.Magnet;

import android.content.Context;
import android.view.View;

/**
 * Created by prem on 7/20/14.
 * Desc: Interface that gives all the necessary requirements to create a Magnet.
 */
public interface MagnetRequirements {

    /**
     * Return the view of the icon that will be floating on the screen. Use the context to
     * instantiate your view, or create a {@link android.view.LayoutInflater}
     * via {@link android.view.LayoutInflater#from(android.content.Context)}
     * @param context used to create the view
     * @return the view that will float on the screen
     */
    public View getIconView(Context context);

    /**
     * The id of the remove icon which will be used if {@link #shouldFlingAway()} returns true.
     * Return {@code -1} if you want the default icon which is an image of a trash can.
     * @return
     */
    public int getRemoveIconResID();

    /**
     * Return true if you want the remove icon to be response i.e. when the user drags the icon
     * left and right on the screen, the remove icon will follow the user's touch location/
     * @return
     */
    public boolean removeIconShouldBeResponsive();

    /**
     * Return true if you want your icon to stick to wall when the user lifts their finger off the
     * screen. The icon will animate to the closest wall of the screen.
     * @return
     */
    public boolean shouldStickToWall();

    /**
     * Return true if you want the user to be able to fling away the icon. If you return true then
     * a remove icon will be shown when the user touches the icon. Set the remove icon image using
     * {@link #getRemoveIconResID()}
     * @return
     */
    public boolean shouldFlingAway();
}
