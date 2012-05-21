/* Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.activities;

import gmc.hotplate.R;
import gmc.hotplate.entities.Product;
import gmc.hotplate.entities.Step;
import gmc.hotplate.logic.Manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/*
 * Activity contains recipe name, list of ingredients, steps
 */
public class RecipeDescriptionActivity extends Activity {
    private static final String LOG_TAG = RecipeDescriptionActivity.class.getName();
    private Manager manager;
    private ListView lvSteps;
    private StepListAdapter adapter;
    private TextView tvRecipeName;
    private TextView tvIngredients;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_description);

        Log.d(LOG_TAG, "Activity Created");
        manager = Manager.getInstance();
        tvRecipeName = (TextView) findViewById(R.id.tvRecipeName);
        tvIngredients = (TextView) findViewById(R.id.tvRecipeIngredients);
        lvSteps = (ListView) findViewById(R.id.lvSteps);

        tvRecipeName.setText(manager.getCurrentRecipe().getName());
        List<Step> steps = manager.getCurrentRecipe().getSteps();

        // Show ingredients (text)
        StringBuilder builder = new StringBuilder();
        Map<Product, Float> ingredients = manager.getCurrentRecipe().getIngredients();
        for (Map.Entry<Product, Float> entry : ingredients.entrySet()) {
            builder.append(entry.getKey().getName() + "     -   " + entry.getValue() + "\n");
        }
        tvIngredients.setText(builder.toString());

        adapter = new StepListAdapter(steps);
        lvSteps.setAdapter(adapter);
        manager.setActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    /*
     * Custom adapter for steps list
     */
    public class StepListAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private List<Step> steps;

        public StepListAdapter(List<Step> steps) {
            this.steps = steps;
            inflater = (LayoutInflater) RecipeDescriptionActivity.this.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);

            // Set cached flags to FALSE if there is no old cached info
            Log.d(LOG_TAG, "Setting cached flags to FALSE");
            List<Boolean> isCached = new ArrayList<Boolean>();
            for (int i = 0; i < steps.size(); i++) {
                isCached.add(Boolean.FALSE);
            }
            manager.setIsCached(isCached);

            // Set timers flags to FALSE if there is no running timers
            if (!manager.isAnyTimerStarted()) {
                List<Boolean> isTimerStarted = new ArrayList<Boolean>();
                for (int i = 0; i < steps.size(); i++) {
                    isTimerStarted.add(Boolean.FALSE);
                }
                manager.setIsTimerStarted(isTimerStarted);
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

            // Returns cached view if it exists
            if (manager.getIsCached().get(position)) {
                return manager.getCachedViews().get(position);
            }
            View item = inflater.inflate(R.layout.step_list_item, parent, false);
            TextView tvDescription = (TextView) item.findViewById(R.id.tvStepDescr);
            TextView tvTimerLabel = (TextView) item.findViewById(R.id.tvTimerLabel);
            Button btnTimerControl = (Button) item.findViewById(R.id.btnTimerControl);

            // Set info to fields from entity
            Step step = (Step) getItem(position);
            tvDescription.setText(step.getDescription());
            if (step.getTime() != 0) {
                tvTimerLabel.setText(String.valueOf(step.getTime()));
                if (manager.getIsTimerStarted().get(position)) {
                    btnTimerControl.setText("Stop");
                } else {
                    btnTimerControl.setText("Start");
                }
                btnTimerControl.setOnClickListener(new TimerButtonListener(position));
            } else {
                tvTimerLabel.setVisibility(View.INVISIBLE);
                btnTimerControl.setVisibility(View.INVISIBLE);
            }

            // Cache info about view
            manager.getIsCached().set(position, Boolean.TRUE);
            manager.getCachedTextViews().add(position, tvTimerLabel);
            manager.getCachedViews().add(position, item);
            return item;
        }
    }

    class TimerButtonListener implements OnClickListener {

        private int position;

        public TimerButtonListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(RecipeDescriptionActivity.this, TimerService.class);
            intent.putExtra(TimerService.ITEM_POSITION, position);
            intent.putExtra(TimerService.ITEM_TIMER,
                    ((Step) adapter.getItem(position)).getTime());

            if (manager.isTimerStarted(position)) {
                Log.d(LOG_TAG, "Button #" + position + " pressed: stopping");
                intent.putExtra(TimerService.ITEM_ACTION, TimerService.TIMER_STOP);
                RecipeDescriptionActivity.this.startService(intent);
                manager.getButton(position).setText("Start");
                manager.setTimerStarted(position, Boolean.FALSE);
            } else {
                Log.d(LOG_TAG, "Button #" + position + " pressed: starting");
                intent.putExtra(TimerService.ITEM_ACTION, TimerService.TIMER_START);
                RecipeDescriptionActivity.this.startService(intent);
                manager.getButton(position).setText("Stop");
                manager.setTimerStarted(position, Boolean.TRUE);
            }
        }
    }
}
