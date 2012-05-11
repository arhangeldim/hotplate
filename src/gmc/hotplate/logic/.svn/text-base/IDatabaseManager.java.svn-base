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

public interface IDatabaseManager {

    Recipe[] getRecipes(Product ingridient, int categoryId, int limit);

    Recipe[] getRecipes(Product ingridient, int limit);

    Recipe[] getRecipes(int categoryId, int limit);

    Recipe[] getRecipes(int limit);

    List<Product> getProductsList(int type, int limit);

    Recipe getRecipeById(int id);

    Product getProductById(int id);

    void close();

}
