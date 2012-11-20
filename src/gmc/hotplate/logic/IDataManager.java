/* Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.logic;

import gmc.hotplate.entities.Ingredient;
import gmc.hotplate.entities.Recipe;

import java.util.List;
import java.util.Map;

public interface IDataManager {

    Recipe[] getRecipes(Ingredient ingridient, int categoryId, int limit);

    Recipe[] getRecipes(Ingredient ingridient, int limit);

    Recipe[] getRecipes(int categoryId, int limit);

    List<Recipe> getRecipes(int limit);

    List<Ingredient> getProductsList(int type, int limit);

    Map<Ingredient, Float> getIngredients(int recipeId);

    Recipe getRecipeById(int id);

    Ingredient getProductById(int id);

    void close();

}
