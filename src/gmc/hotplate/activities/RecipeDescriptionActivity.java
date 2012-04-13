/*
 * Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.activities;

import gmc.hotplate.R;
import gmc.hotplate.logic.RecipeManager;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class RecipeDescriptionActivity extends Activity {

    private static final String LOG_TAG = "RecipesDescriptionActivity";
    private RecipeManager recipeManager;
    private TextView tvDescription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_description);

        Log.d(LOG_TAG, "Created RecipeDescriptionActivity");
        recipeManager = RecipeManager.getInstance();
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvDescription.setText(recipeManager.getCurrentRecipe().getDescription());
    }
}
