/* Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.activities;

import gmc.hotplate.R;
import gmc.hotplate.logic.Manager;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class HotplateActivity extends Activity {

    private static final String LOG_TAG = HotplateActivity.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.d(LOG_TAG, "Created HotplateActivity");
        Timer timer = new Timer();
        Manager.getInstance().setLogoScreen(this);
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                HotplateActivity.this.startActivity(
                        new Intent(HotplateActivity.this, RecipesListMenuActivity.class));
            }
        }, Integer.parseInt(getResources().getString(R.string.screen_delay)));
    }


}
