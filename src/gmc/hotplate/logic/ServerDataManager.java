/* Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.logic;

import gmc.hotplate.R;
import gmc.hotplate.entities.Ingredient;
import gmc.hotplate.entities.Recipe;
import gmc.hotplate.net.Connection;
import gmc.hotplate.net.Connection.ConnectionStatus;
import gmc.hotplate.util.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class ServerDataManager extends DataManager {

    public static final String LOG_TAG = ServerDataManager.class.getName();
    public static final String PARAM_KEY_TYPE = "type";
    public static final String PARAM_KEY_OFFSET = "offset";
    public static final String PARAM_KEY_LIMIT = "limit";
    public static final String PARAM_VAL_ALL = "all";
    public static final String PARAM_VAL_NO_LIMIT = "-1";
    public static final String PARAM_VAL_DEFAULT_OFFSET = "0";
    private String urlString;
    private Context context;
    private List<Recipe> recipes;
    private Connection connection;

    public ServerDataManager(Context context) {
        this.context = context;
        Resources r = context.getResources();
        urlString = r.getString(R.string.default_url);
        recipes = new ArrayList<Recipe>();
        connection = new Connection(context);
    }

    @Override
    public List<Recipe> getRecipes(int limit) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(PARAM_KEY_TYPE, PARAM_VAL_ALL);
        params.put(PARAM_KEY_OFFSET, PARAM_VAL_DEFAULT_OFFSET);
        params.put(PARAM_KEY_LIMIT, String.valueOf(limit));
        String response = connection.requestServer(urlString, params);
        if (connection.getStatus() != ConnectionStatus.CONNECT_OK) {

            // TODO(arhangeldim): Handle error
            Log.w(LOG_TAG, "No recipes to display");
        } else {
            Log.d(LOG_TAG, response);
            JsonParser parser = new JsonParser();
            try {
                JSONObject obj = new JSONObject(response);
                recipes = parser.parseMessage(obj);
            } catch (JSONException e) {
                Log.w(LOG_TAG, "No recipes to display");
            }
        }
        for (Recipe r : recipes) {
            recipeCache.put(r.getId(), r);
        }
        return recipes;
    }

    @Override
    public List<Ingredient> getProductsList(int type, int limit) {
        throw new AssertionError("Not implemented yet!");
    }

    @Override
    public Map<Ingredient, Float> getIngredients(long recipeId) {
        throw new AssertionError("Not implemented yet!");
    }

    @Override
    public void close() {
    }

    @Override
    public List<Recipe> getRecipes(int offset, int limit) {
        throw new AssertionError("Not implemented yet!");
    }

}
