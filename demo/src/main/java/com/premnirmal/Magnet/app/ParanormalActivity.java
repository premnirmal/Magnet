package com.premnirmal.Magnet.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by prem on 7/20/14.
 * <p/>
 * Description:
 * A stub activity used to launch the service
 */
public class ParanormalActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this,MyService.class));
        finish();
    }
}
