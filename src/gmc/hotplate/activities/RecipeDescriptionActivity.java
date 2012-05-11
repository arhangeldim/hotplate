/*
 * Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.activities;

import gmc.hotplate.R;
import gmc.hotplate.entities.Product;
import gmc.hotplate.entities.Step;
import gmc.hotplate.logic.RecipeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/* Activity contains recipe name, list of ingredients, steps
 *
 */
public class RecipeDescriptionActivity extends Activity {

    private static final String LOG_TAG = "RecipesDescriptionActivity";
    private RecipeManager recipeManager;
    private TextView tvRecipeName;
    private TextView tvIngredients;
    private ListView lvSteps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_description);

        Log.d(LOG_TAG, "Activity Created");
        recipeManager = RecipeManager.getInstance();
        tvRecipeName = (TextView) findViewById(R.id.tvRecipeName);
        tvIngredients = (TextView) findViewById(R.id.tvRecipeIngredients);
        lvSteps = (ListView) findViewById(R.id.lvSteps);

        tvRecipeName.setText(recipeManager.getCurrentRecipe().getName());

        StringBuilder builder = new StringBuilder();
        Map<Product, Float> ingredients = recipeManager.getCurrentRecipe().getIngredients();
        for (Map.Entry<Product, Float> entry : ingredients.entrySet()) {
            builder.append(entry.getKey().getName() + "     -   " + entry.getValue() + "\n");
        }
        tvIngredients.setText(builder.toString());

        List<Step> steps = recipeManager.getCurrentRecipe().getSteps();
        StepListAdapter adapter = new StepListAdapter(steps);
        lvSteps.setAdapter(adapter);
    }

    /*
     * Adapter for steps list
     *
     */
    public class StepListAdapter extends BaseAdapter {

        private static final String LOG_TAG = "StepListAdapter";
        private List<Step> steps;

        // When view is created, set isCreated=true
        // and add view in List views.  List size = number of positions = number of steps
        private List<Boolean> isCreated;
        private List<View> views;

        public StepListAdapter(List<Step> steps) {
            this.steps = steps;
            views = new ArrayList<View>();
            isCreated = new ArrayList<Boolean>();
            for (int i = 0; i < steps.size(); i++) {
                isCreated.add(Boolean.FALSE);
            }
        }

        @Override
        public int getCount() {
            return steps.size();
        }

        @Override
        public Object getItem(int position) {
            return steps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(LOG_TAG, "Get view by position: " + position);
            // Don't need to create view more than one time
            if (isCreated.get(position)) {
                return views.get(position);
            }
            View item = convertView;
            StepHolder holder = null;
            if (item == null) {
                item = getLayoutInflater().inflate(
                        R.layout.step_list_item, parent, false);
                holder = new StepHolder(item);
                item.setTag(holder);
            } else {
                holder = (StepHolder) item.getTag();
            }

            holder.populateFrom((Step) getItem(position));
            views.add(item);
            isCreated.set(position, Boolean.TRUE);

            return item;
        }
    }

    /*
     * Set elements of list items
     *
     */
    class StepHolder {
        private TextView tvDescription;
        private TextView tvTimerLabel;
        private Button btnTimerControl;

        StepHolder(View item) {
            tvDescription = (TextView) item.findViewById(R.id.tvStepDescr);
            tvTimerLabel = (TextView) item.findViewById(R.id.tvTimerLabel);
            btnTimerControl = (Button) item.findViewById(R.id.btnTimerControl);
        }

        /*
         * Set fields with text
         * Set properties
         *
         */
        void populateFrom(Step step) {
            tvDescription.setText(step.getDescription());
            if (step.getTime() != 0) {
                tvTimerLabel.setText(String.valueOf(step.getTime()));
                btnTimerControl.setOnClickListener(
                        new TimerButtonListener(
                                RecipeDescriptionActivity.this, tvTimerLabel, step.getTime()));
                btnTimerControl.setText("Start");
            } else {
                btnTimerControl.setVisibility(View.INVISIBLE);
            }
        }
    }
}
