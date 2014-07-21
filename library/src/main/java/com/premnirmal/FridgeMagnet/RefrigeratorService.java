package com.premnirmal.FridgeMagnet;

import android.app.Notification;
import android.app.Service;

/**
 * Created by prem on 7/20/14.
 * The service used to create the FridgeMagnet
 */
public abstract class RefrigeratorService extends Service implements IconCallback {

    private int mNotificationId = 89427842;
    private FridgeMagnet mIconView;

    @Override
    public void onCreate() {
        super.onCreate();
        mIconView = new FridgeMagnet(this, this, getIcon());
        startForeground(mNotificationId, createNotification());
    }

    /**
     * Create the notification to show when the service is running
     *
     * @return
     */
    protected abstract Notification createNotification();

    /**
     * Implementation of {@link FridgeMagnetRequirements} that gives all the icon parameters
     * required to create the window icon.
     * @return your implementation of {@link FridgeMagnetRequirements}
     */
    protected abstract FridgeMagnetRequirements getIcon();
}
