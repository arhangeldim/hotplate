/* Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.entities;

import java.util.List;
import java.util.Map;

public class Recipe {

    private int id;
    private String name;
    private String description;
    private int personCount;
    private List<Step> steps;
    private Map<Ingredient, Float> ingredients;
    private List<Category> categories;


    public Recipe() {

    }

    public Recipe(int id, int categoryId, String name, String description, int personCount,
            List<Step> steps) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.personCount = personCount;
        this.steps = steps;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPersonCount() {
        return personCount;
    }

    public void setPersonCount(int personCount) {
        this.personCount = personCount;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public Map<Ingredient, Float> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Map<Ingredient, Float> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return name;
    }

}
