/* Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.logic;

import gmc.hotplate.entities.Ingredient;
import gmc.hotplate.entities.Recipe;
import gmc.hotplate.util.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public final class LocalDataManager implements IDataManager {

    private static final String LOG_TAG = LocalDataManager.class.getName();

    private Context context;

    public LocalDataManager(Context context) {
        this.context = context;
    }

    @Override
    public List<Recipe> getRecipes(int limit) {
        Log.d(LOG_TAG, "parse Message");
        List<Recipe> recipes = new ArrayList<Recipe>();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    context.getAssets().open("json")));
            String line = null;
            StringBuilder builder = new StringBuilder();
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            JsonParser parser = new JsonParser();
            JSONObject obj = new JSONObject(builder.toString());
            recipes = parser.parseMessage(obj);
        } catch (JSONException e) {
            Log.d(LOG_TAG, "Json parse error: " + e.getMessage());
        } catch (IOException e) {
            Log.d(LOG_TAG, "Read json file error: " + e.getMessage());
        }
        return recipes;
    }

    @Override
    public Map<Ingredient, Float> getIngredients(long recipeId) {
        throw new AssertionError("Not implemented yet!");
    }

    @Override
    public Recipe getRecipeById(long id) {
        throw new AssertionError("Not implemented yet!");
    }

    @Override
    public List<Recipe> getRecipes(int offset, int limit) {
        throw new AssertionError("Not implemented yet!");
    }

    @Override
    public List<Ingredient> getProductsList(int type, int limit) {
        throw new AssertionError("Not implemented yet!");
    }

    @Override
    public void close() {
    }

}
