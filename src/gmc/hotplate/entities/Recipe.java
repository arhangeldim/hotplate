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

    private long id;
    private String name;
    private String description;
    private int personCount;
    private List<Step> steps;
    private Map<Ingredient, Float> oldFormatIngredients;
    private List<Ingredient> ingredients;
    private List<String> categories;


    public Recipe() {

    }

    public Recipe(long id, String name, String description, int personCount, List<Step> steps) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.personCount = personCount;
        this.steps = steps;
    }

    public long getId() {
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

    public Map<Ingredient, Float> getOldFormatIngredients() {
        return oldFormatIngredients;
    }

    public void setOldFormatIngredients(Map<Ingredient, Float> ingredients) {
        this.oldFormatIngredients = ingredients;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("id = " + id + "\n")
                .append("name = " + name + "\n")
                .append("description = " + description + "\n")
                .append("person count = " + personCount + "\n");
        for (int i = 0; i < steps.size(); i++) {
            builder.append("\t" + steps.get(i).toString() + "\n");
        }
        for (int i = 0; i < ingredients.size(); i++) {
            builder.append("\t" + ingredients.get(i).toString() + "\n");
        }
        return builder.toString();
    }

}
