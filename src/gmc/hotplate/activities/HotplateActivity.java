/*
 * Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.activities;

import gmc.hotplate.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HotplateActivity extends Activity implements OnClickListener {

    private static final String LOG_TAG = "HotplateActivity";
    private Button btnShowRecipes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        btnShowRecipes = (Button) findViewById(R.id.btnRecipeList);
        btnShowRecipes.setOnClickListener(this);
        Log.d(LOG_TAG, "Created HotplateActivity");
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, RecipesListMenuActivity.class);
        startActivity(intent);
    }
}
