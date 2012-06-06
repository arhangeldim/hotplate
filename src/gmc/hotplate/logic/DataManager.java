/* Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.logic;

import gmc.hotplate.entities.Ingredient;
import gmc.hotplate.entities.Recipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

public abstract class DataManager {

    private static final String LOG_TAG = "DataManager";
    protected Map<Long, Recipe> recipeCache = new HashMap<Long, Recipe>();

    public abstract List<Recipe> getRecipes(int limit);

    abstract List<Recipe> getRecipes(int offset, int limit);

    abstract List<Ingredient> getProductsList(int type, int limit);

    abstract Map<Ingredient, Float> getIngredients(long recipeId);

    public Recipe getRecipeById(long id) {
        Log.d(LOG_TAG, "getRecipeById(): id=" + id + "recipe_size=" + recipeCache.size());
        Recipe recipe = null;
        if (recipeCache.containsKey(id)) {
            recipe = recipeCache.get(id);
        }
        return recipe;
    }

    abstract void close();

}
