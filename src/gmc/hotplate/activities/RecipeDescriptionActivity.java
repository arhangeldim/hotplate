/*
 * Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.activities;

import gmc.hotplate.R;
import gmc.hotplate.entities.Product;
import gmc.hotplate.entities.Step;
import gmc.hotplate.logic.RecipeManager;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

public class RecipeDescriptionActivity extends Activity {

    private static final String LOG_TAG = "RecipesDescriptionActivity";
    private RecipeManager recipeManager;
    private TextView tvRecipeName;
    private TextView tvIngredients;
    private ListView lvSteps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_description);

        Log.d(LOG_TAG, "Activity Created");
        recipeManager = RecipeManager.getInstance();
        tvRecipeName = (TextView) findViewById(R.id.tvRecipeName);
        tvIngredients = (TextView) findViewById(R.id.tvRecipeIngredients);
        lvSteps = (ListView) findViewById(R.id.lvSteps);

        tvRecipeName.setText(recipeManager.getCurrentRecipe().getName());

        StringBuilder builder = new StringBuilder();
        Map<Product, Float> ingredients = recipeManager.getCurrentRecipe().getIngredients();
        for (Map.Entry<Product, Float> entry : ingredients.entrySet()) {
            builder.append(entry.getKey().getName() + "     -   " + entry.getValue() + "\n");
        }
        tvIngredients.setText(builder.toString());

        List<Step> steps = recipeManager.getCurrentRecipe().getSteps();
        StepListAdapter adapter = new StepListAdapter(this, steps, this);
        lvSteps.setAdapter(adapter);
    }

}
