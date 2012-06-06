/* Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.util;

import gmc.hotplate.entities.Ingredient;
import gmc.hotplate.entities.Recipe;
import gmc.hotplate.entities.Step;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JsonParser {

    private static final String LOG_TAG = JsonParser.class.getName();
    private static final String TAG_RECIPES = "recipes";
    private static final String TAG_RECIPE_ID = "id";
    private static final String TAG_RECIPE_NAME = "name";
    private static final String TAG_RECIPE_DESCRIPTION = "description";
    private static final String TAG_RECIPE_PERSON = "personCount";
    private static final String TAG_RECIPE_STEPS = "steps";
    private static final String TAG_RECIPE_INGREDIENTS = "ingredients";
    private static final String TAG_RECIPE_CATEGORIES = "categories";
    private static final String TAG_STEP_ID = "id";
    private static final String TAG_STEP_DESCRIPTION = "description";
    private static final String TAG_STEP_TIME = "time";
    private static final String TAG_INGREDIENTS_NAME = "name";
    private static final String TAG_INGREDIENTS_AMOUNT = "amount";
    private static final String TAG_INGREDIENTS_TYPE = "type";

    public List<Recipe> parseMessage(JSONObject obj) {
        List<Recipe> recipes = new ArrayList<Recipe>();
        try {
            JSONArray jsonRecipes = obj.getJSONArray(TAG_RECIPES);
            for (int i = 0; i < jsonRecipes.length(); i++) {
                Recipe recipe = parseRecipeObject(jsonRecipes.getJSONObject(i));
                if (recipe != null) {
                    recipes.add(recipe);
                }
            }
        } catch (JSONException e) {
            Log.w(LOG_TAG, "ParseMessage() error: " + e.getMessage());
        }
        return recipes;
    }

    public Recipe parseRecipeObject(JSONObject obj) {
        Recipe recipe = null;
        try {
            int recipeId = obj.getInt(TAG_RECIPE_ID);
            String recipeName = obj.getString(TAG_RECIPE_NAME);
            String recipeDescription = obj.getString(TAG_RECIPE_DESCRIPTION);
            int personCount = obj.getInt(TAG_RECIPE_PERSON);
            JSONArray jsonSteps = obj.getJSONArray(TAG_RECIPE_STEPS);
            List<Step> steps = new ArrayList<Step>();
            for (int i = 0; i < jsonSteps.length(); i++) {
                Step step = parseStepObject(jsonSteps.getJSONObject(i));
                if (step != null) {
                    steps.add(step);
                }
            }

            List<Ingredient> ingredients = new ArrayList<Ingredient>();
            JSONArray jsonIngredients = obj.getJSONArray(TAG_RECIPE_INGREDIENTS);
            for (int i = 0; i < jsonIngredients.length(); i++) {
                Ingredient ingredient = parseIngredientObject(jsonIngredients.getJSONObject(i));
                if (ingredient != null) {
                    ingredients.add(ingredient);
                }
            }

            List<String> categories = new ArrayList<String>();
            if (obj.has(TAG_RECIPE_CATEGORIES)) {
                JSONArray jsonCategories = obj.getJSONArray(TAG_RECIPE_CATEGORIES);
                for (int i = 0; i < jsonCategories.length(); i++) {
                    String tag = jsonCategories.getString(i);
                    categories.add(tag);
                }
            }
            recipe = new Recipe(recipeId, recipeName, recipeDescription, personCount, steps);
            recipe.setIngredients(ingredients);
            recipe.setCategories(categories);
        } catch (JSONException e) {
            Log.w(LOG_TAG, "ParseRecipe() error: " + e.getMessage());
        }

        return recipe;
    }

    public Step parseStepObject(JSONObject obj) {
        Step step = null;
        try {
            int stepId = obj.getInt(TAG_STEP_ID);
            String stepDescription = obj.getString(TAG_STEP_DESCRIPTION);
            int seconds = obj.getInt(TAG_STEP_TIME);
            step = new Step(stepId, stepDescription, seconds);
        } catch (JSONException e) {
            Log.w(LOG_TAG, "ParseStep() error: " + e.getMessage());
        }
        return step;
    }

    public Ingredient parseIngredientObject(JSONObject obj) {
        Ingredient ingredient = null;
        try {
            String name = obj.getString(TAG_INGREDIENTS_NAME);
            double amount = obj.getDouble(TAG_INGREDIENTS_AMOUNT);
            String type = obj.getString(TAG_INGREDIENTS_TYPE);
            ingredient = new Ingredient(name, amount, type);
        } catch (JSONException e) {
            Log.w(LOG_TAG, "ParseIngredient() error: " + e.getMessage());
        }
        return ingredient;
    }
}
