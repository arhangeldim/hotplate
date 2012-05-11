/*
* Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.activities;

import gmc.hotplate.R;
import gmc.hotplate.entities.Recipe;
import gmc.hotplate.logic.DatabaseManager;
import gmc.hotplate.logic.IDatabaseManager;
import gmc.hotplate.logic.RecipeManager;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RecipesListMenuActivity extends Activity implements OnItemClickListener {

    public static final int QUERY_LIMIT = 10;
    private static final String LOG_TAG = "RecipesListMenuActivity";
    private IDatabaseManager dbManager;
    private RecipeManager recipeManager;
    private ListView lvRecipes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_list);
        Log.d(LOG_TAG, "Created RecipesListMenyActivity");
        dbManager = DatabaseManager.getInstance(this);
        recipeManager = RecipeManager.getInstance();
        lvRecipes = (ListView) findViewById(R.id.lvRecipes);

        List<Recipe> recipes = dbManager.getRecipes(QUERY_LIMIT);
        RecipeListAdapter adapter = new RecipeListAdapter(recipes);
        lvRecipes.setAdapter(adapter);
        lvRecipes.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object selected = parent.getAdapter().getItem(position);
        if (selected instanceof Recipe) {
            Recipe recipe = (Recipe) selected;
            Log.d(LOG_TAG, "Selected recipe name: " + recipe.getName());
            recipeManager.setCurrentRecipe(recipe);
        }
        Intent intent = new Intent(RecipesListMenuActivity.this,
                RecipeDescriptionActivity.class);
        startActivity(intent);
    }


    class RecipeListAdapter extends BaseAdapter {
    
        private List<Recipe> recipes;
    
        public RecipeListAdapter(List<Recipe> recipes) {
            this.recipes = recipes;
        }
    
        @Override
        public int getCount() {
            return recipes.size();
        }
    
        @Override
        public Object getItem(int position) {
            return recipes.get(position);
        }
    
        @Override
        public long getItemId(int position) {
            return position;
        }
    
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View item = convertView;
            RecipeItemHolder holder = null;
            if (item == null) {
                item = getLayoutInflater().inflate(
                        R.layout.recipe_list_item, parent, false);
                holder = new RecipeItemHolder(item);
                item.setTag(holder);
            } else {
                holder = (RecipeItemHolder) item.getTag();
            }
            
            holder.populateFrom((Recipe)getItem(position));
            return item;
        }
    
    }
    
    static class RecipeItemHolder {
        private TextView tvName;
        private TextView tvDescr;
        
        RecipeItemHolder(View item) {
            tvName = (TextView) item.findViewById(R.id.tvName);
            tvDescr = (TextView) item.findViewById(R.id.tvDescr);
        }
        
        void populateFrom(Recipe recipe) {
            tvName.setText(recipe.getName());
            tvDescr.setText(recipe.getDescription());
        }
    }
}