/*
 * Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.activities;

import gmc.hotplate.R;
import gmc.hotplate.logic.DatabaseManager;
import gmc.hotplate.logic.IDatabaseManager;

import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HotplateActivity extends Activity implements OnClickListener
{
    private static final String LOG_TAG = "HotplateActivity";
    private IDatabaseManager dbManager;
    private Button btnShowRecipes;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        btnShowRecipes = (Button) findViewById(R.id.btnRecipeList);
        btnShowRecipes.setOnClickListener(this);
        dbManager = new DatabaseManager(this);
        Log.d(LOG_TAG, "Created HotplateActivity");
    }

    public void onClick(View v) {
        Log.d(LOG_TAG, "OnClick method called");
        switch (v.getId()) {
            case R.id.btnRecipeList:
                Log.d(LOG_TAG, "  btnShowRecipes case");
                Map<Integer, String> recipes = dbManager.getAllRecipesName();
                Log.d(LOG_TAG, "Recipe list size = " + recipes.size());
                for (Map.Entry<Integer, String> entry : recipes.entrySet()) {
                    Log.d(LOG_TAG, "  Recipe #" + entry.getKey() + " is called " + entry.getValue());
                }
                break;
        }
    }
}
