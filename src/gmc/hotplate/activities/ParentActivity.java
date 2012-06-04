/* Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.activities;

import gmc.hotplate.logic.AppManager;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;

public abstract class ParentActivity extends Activity {

    protected AppManager manager;
    protected Typeface robotoLight;
    protected Typeface robotoRegular;
    protected Typeface robotoCondensed;

    private static final String LOG_TAG = ParentActivity.class.getName();

    public abstract void setDefault();

    public abstract void setActive();

    public abstract void setInactive();

    public abstract void update(Message msg);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = AppManager.getInstance();
        robotoLight = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        robotoRegular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        robotoCondensed = Typeface.createFromAsset(getAssets(), "Roboto-Condensed.ttf");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Log.d(LOG_TAG, "onResume()");
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Log.d(LOG_TAG, "onStart()");
        manager.setCurrentActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Log.d(LOG_TAG, "onStop()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Log.d(LOG_TAG, "onPause()");
    }

    @Override
    protected void onDestroy() {
        // Log.d(LOG_TAG, "onDestroy()");
        super.onDestroy();

    }
}
