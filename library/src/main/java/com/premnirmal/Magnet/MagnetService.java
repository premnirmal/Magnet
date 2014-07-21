package com.premnirmal.Magnet;

import android.app.Notification;
import android.app.Service;

/**
 * Created by prem on 7/20/14.
 * The service used to create the Magnet
 */
public abstract class MagnetService extends Service implements IconCallback {

    private int mNotificationId = 89427842;
    private Magnet mIconView;

    @Override
    public void onCreate() {
        super.onCreate();
        mIconView = new Magnet(this, this, getIcon());
        startForeground(mNotificationId, createNotification());
    }

    /**
     * Create the notification to show when the service is running
     *
     * @return
     */
    protected abstract Notification createNotification();

    /**
     * Implementation of {@link MagnetRequirements} that gives all the icon parameters
     * required to create the window icon.
     * @return your implementation of {@link MagnetRequirements}
     */
    protected abstract MagnetRequirements getIcon();
}
