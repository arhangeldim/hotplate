/* Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.activities;

import gmc.hotplate.R;
import gmc.hotplate.entities.Recipe;
import gmc.hotplate.logic.AppManager;
import gmc.hotplate.logic.LocalDataManager;
import gmc.hotplate.logic.DataManager;
import gmc.hotplate.logic.ServerDataManager;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    private int defaultQueryLimit;
    private int queryLimit;
    private ListView lvRecipes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.recipe_list);
        Log.d(LOG_TAG, "Created RecipesListMenyActivity");
        defaultQueryLimit = Integer.parseInt(getResources()
                .getString(R.string.default_query_limit));

        TextView tvRecipeMenu = (TextView) findViewById(R.id.tvRecipeMenu);
        tvRecipeMenu.setTypeface(robotoLight);
        lvRecipes = (ListView) findViewById(R.id.lvRecipes);
        queryLimit = defaultQueryLimit;
        DataManager dm = null;
        List<Recipe> recipes = null;
        
        if (hasInternetConnection()) {
            Log.d(LOG_TAG, "Network is avaliable.");
            dm = new ServerDataManager(this);
            recipes = dm.getRecipes(queryLimit);
            if (recipes == null || recipes.isEmpty()) {
                Log.d(LOG_TAG, "Network request returns empty set. Try to get local data");
                dm = new LocalDataManager(this);
                recipes = dm.getRecipes(queryLimit);
            }
        } else {
            Log.d(LOG_TAG, "Network is not avaliable. Get local data");
            dm = new LocalDataManager(this);
            recipes = dm.getRecipes(queryLimit);
        }
        manager.setDataManager(dm);
        RecipeListAdapter adapter = new RecipeListAdapter(recipes);
        lvRecipes.setAdapter(adapter);
        lvRecipes.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        manager.setCurrentRecipeId(AppManager.NONE);
    }

    public boolean hasInternetConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        if (netInfo == null) {
            return false;
        }
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                if (ni.isConnected()) {
                    Log.d(this.toString(), "wifi conncetion found");
                    return true;
                }
            }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (ni.isConnected()) {
                    Log.d(this.toString(), "mobile connection found");
                    return true;
                }
            }
        }
        return false;
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
            manager.setCurrentRecipeId(recipe.getId());
        }
        Intent intent = new Intent(RecipesListMenuActivity.this,
                RecipeDescriptionActivity.class);
        manager.setIntentFromMenu(true);
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
