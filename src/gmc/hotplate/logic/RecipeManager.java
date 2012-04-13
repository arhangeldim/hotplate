/*
 * Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.logic;

import gmc.hotplate.entities.Recipe;

public class RecipeManager {

    private static RecipeManager sInstance = null;
    private Recipe currentRecipe;

    private RecipeManager() {

    }

    public static synchronized RecipeManager getInstance() {
    	if (sInstance == null) {
			sInstance = new RecipeManager();
		}
		return sInstance;
	}

	public Recipe getCurrentRecipe() {
		return currentRecipe;
	}

	public void setCurrentRecipe(Recipe currentRecipe) {
		this.currentRecipe = currentRecipe;
	}

}
