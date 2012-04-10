/*
 * Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.entities;

import java.util.List;

public class Step {

    private int id;
    private String name;
    private String description;
    private int time;
    private List<Product> ingredients;
    private List<Step> blockList;

    public Step() {

    }

    public Step(int id, String name, String description, int seconds, List<Product> ingridients,
            List<Step> blockingSteps) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.time = seconds;
        this.ingredients = ingridients;
        this.blockList = blockingSteps;
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
    public int getTime() {
        return time;
    }
    public void setTime(int seconds) {
        this.time = seconds;
    }
    public List<Product> getIngridients() {
        return ingredients;
    }
    public void setIngridients(List<Product> ingridients) {
        this.ingredients = ingridients;
    }
    public List<Step> getBlockList() {
        return blockList;
    }
    public void setBlocking(List<Step> blockList) {
        this.blockList = blockList;
    }


}
