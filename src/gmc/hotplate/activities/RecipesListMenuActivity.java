/* Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.activities;

import gmc.hotplate.R;
import gmc.hotplate.entities.Recipe;
import gmc.hotplate.logic.DatabaseManager;
import gmc.hotplate.logic.IDataManager;

import java.util.List;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RecipesListMenuActivity extends ParentActivity implements OnItemClickListener {

    private static final String LOG_TAG = RecipesListMenuActivity.class.getName();
    public static final int QUERY_LIMIT = 10;
    private IDataManager dbManager;
    private ListView lvRecipes;
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.recipe_list);
        Log.d(LOG_TAG, "Created RecipesListMenyActivity");
        dbManager = DatabaseManager.getInstance(this);
        TextView tvRecipeMenu = (TextView) findViewById(R.id.tvRecipeMenu);
        tvRecipeMenu.setTypeface(robotoLight);
        
        
        lvRecipes = (ListView) findViewById(R.id.lvRecipes);

        List<Recipe> recipes = dbManager.getRecipes(QUERY_LIMIT);
        RecipeListAdapter adapter = new RecipeListAdapter(recipes);
        lvRecipes.setAdapter(adapter);
        lvRecipes.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        manager.setCurrentRecipe(null);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        manager.cancelNotification();
        manager.getLogoActivity().finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object selected = parent.getAdapter().getItem(position);
        if (selected instanceof Recipe) {
            Recipe recipe = (Recipe) selected;
            Log.d(LOG_TAG, "Selected recipe name: " + recipe.getName());
            manager.setCurrentRecipe(recipe);
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

            holder.populateFrom((Recipe) getItem(position));
            return item;
        }
    }

    class RecipeItemHolder {
        private TextView tvName;
        private TextView tvDescr;

        RecipeItemHolder(View item) {
            tvName = (TextView) item.findViewById(R.id.tvName);
            tvDescr = (TextView) item.findViewById(R.id.tvShortDescription);
        }

        void populateFrom(Recipe recipe) {
            tvName.setText(recipe.getName());
            tvDescr.setText(recipe.getDescription());
            tvDescr.setTypeface(robotoRegular);
            tvName.setTypeface(robotoCondensed);
        }
    }

    @Override
    public void setDefault() {
        // TODO Auto-generated method stub
    }



    @Override
    public void setActive() {
        // TODO Auto-generated method stub
    }



    @Override
    public void setInactive() {
        // TODO Auto-generated method stub
    }



    @Override
    public void update(Message msg) {
        // TODO Auto-generated method stub
    }
}
