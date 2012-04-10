/*
 * Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.logic;

import gmc.hotplate.entities.Product;
import gmc.hotplate.entities.Recipe;

import java.util.List;
import java.util.Map;

public interface IDatabaseManager {

    Map<Integer, String> getRecipesName(Product ingridient, int categoryId);

    Map<Integer, String> getRecipesName(Product ingridient);

    Map<Integer, String> getRecipesName(int categoryId);

    Map<Integer, String> getAllRecipesName();

    List<Product> getProductsList(int type);

    Recipe getRecipeById(int id);

    Product getProductById(int id);

    void close();

}
