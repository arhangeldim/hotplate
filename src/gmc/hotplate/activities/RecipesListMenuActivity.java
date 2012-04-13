/*
 * Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.activities;

import gmc.hotplate.R;
import gmc.hotplate.entities.Recipe;
import gmc.hotplate.logic.DatabaseManager;
import gmc.hotplate.logic.IDatabaseManager;
import gmc.hotplate.logic.RecipeManager;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RecipesListMenuActivity extends Activity {

    public static final int QUERY_LIMIT = 10;
    private static final String LOG_TAG = "RecipesListMenuActivity";
    private IDatabaseManager dbManager;
    private RecipeManager recipeManager;
    private List<Recipe> recipes;
    private ListView lvRecipes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_list);
        Log.d(LOG_TAG, "Created RecipesListMenyActivity");
        dbManager = DatabaseManager.getInstance(this);
        recipeManager = RecipeManager.getInstance();
        lvRecipes = (ListView) findViewById(R.id.lvRecipes);
        initialize();

        Recipe[] recipesArray = new Recipe[recipes.size()];
        recipes.toArray(recipesArray);
        ArrayAdapter<Recipe> adapter =
                new ArrayAdapter<Recipe>(this, android.R.layout.simple_list_item_1,
                        recipesArray);
        lvRecipes.setAdapter(adapter);
        lvRecipes.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
                Log.d(LOG_TAG, "OnItemClick: id = " + id);
                Object selected = parent.getAdapter().getItem(position);
                if (selected instanceof Recipe) {
                    Recipe recipe = (Recipe) selected;
                    Log.d(LOG_TAG, "Selected recipe name: " + recipe.getName());
                    recipeManager.setCurrentRecipe(recipe);
                }
                Intent intent = new Intent(RecipesListMenuActivity.this,
                        RecipeDescriptionActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initialize() {
        recipes = dbManager.getAllRecipesName(QUERY_LIMIT);
        Log.d(LOG_TAG, "Recipe list size = " + recipes.size());
        for (Recipe r : recipes) {
            Log.d(LOG_TAG, "Recipe: id=" + r.getId() + ", name=" + r.getName());
        }
    }

}
