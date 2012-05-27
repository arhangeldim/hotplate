/* Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.activities;

import gmc.hotplate.R;
import gmc.hotplate.entities.Ingredient;
import gmc.hotplate.entities.Step;
import gmc.hotplate.logic.Manager;
import gmc.hotplate.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
    private Button btnCancelAllTimers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        /*
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        */

        setContentView(R.layout.recipe_description);

        Log.d(LOG_TAG, "Activity Created");
        manager = Manager.getInstance();
        manager.setActivity(this);
        tvRecipeName = (TextView) findViewById(R.id.tvRecipeName);
        tvIngredients = (TextView) findViewById(R.id.tvRecipeIngredients);
        lvSteps = (ListView) findViewById(R.id.lvSteps);
        btnCancelAllTimers = (Button) findViewById(R.id.btnCancelAllTimers);
        btnCancelAllTimers.setOnClickListener(new TimerControlListener());

        // Default: button is disabled
        manager.setBtnAllTimerCancelEnabled(Boolean.FALSE);
        if (manager.isAnyTimerStarted()
                && manager.getStartedRecipeId() == manager.getCurrentRecipe().getId()) {
            Log.d(LOG_TAG, "Set button enabled");
            manager.setBtnAllTimerCancelEnabled(Boolean.TRUE);
        }
        tvRecipeName.setText(manager.getCurrentRecipe().getName());
        List<Step> steps = manager.getCurrentRecipe().getSteps();

        // Show ingredients (text)
        StringBuilder builder = new StringBuilder();
        Map<Ingredient, Float> ingredients = manager.getCurrentRecipe().getIngredients();
        for (Map.Entry<Ingredient, Float> entry : ingredients.entrySet()) {
            builder.append(entry.getKey().getName() + "     -   " + entry.getValue() + "\n");
        }
        tvIngredients.setText(builder.toString());

        adapter = new StepListAdapter(steps);
        lvSteps.setAdapter(adapter);
    }

    public Button getBtnCancelAllTimers() {
        return btnCancelAllTimers;
    }

    @Override
    protected void onStart() {
        super.onStart();
        manager.setActivity(this);
        Log.d(LOG_TAG, "onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        // If this recipe is started
        if (manager.isAnyTimerStarted()
                && manager.getCurrentRecipe().getId() == manager.getStartedRecipeId()) {
            Log.d(LOG_TAG, "Activity destroyed. Show dialog");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.timer_dialog_title))
                   .setMessage(getString(R.string.timer_dialog_message))
                   .setCancelable(false)
                   .setPositiveButton(
                           getString(R.string.timer_dialog_positive), dialogButtonListener)
                   .setNegativeButton(
                           getString(R.string.timer_dialog_negative), dialogButtonListener)
                   .setNeutralButton(
                           getString(R.string.timer_dialog_neutral), dialogButtonListener);
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            super.onBackPressed();
        }
    }


    private android.content.DialogInterface.OnClickListener dialogButtonListener =
            new android.content.DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent(RecipeDescriptionActivity.this, TimerService.class);
            switch (which) {

            // Notificate about timers
            case Dialog.BUTTON_POSITIVE:
                Log.d(LOG_TAG, "Set background process");
                RecipeDescriptionActivity.this.finish();
                break;

            // Stop all timers
            case Dialog.BUTTON_NEGATIVE:
                intent.putExtra(TimerService.ITEM_ACTION, TimerService.STOP_ALL_TIMERS);
                RecipeDescriptionActivity.this.startService(intent);
                manager.setBtnAllTimerCancelEnabled(Boolean.FALSE);
                RecipeDescriptionActivity.this.finish();
                break;
            case Dialog.BUTTON_NEUTRAL:
                Log.d(LOG_TAG, "Cancel dialog view");
                dialog.cancel();
            default:
                Log.d(LOG_TAG, "Stub. No implementation");
                break;
            }
        }

    };

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
                Log.d(LOG_TAG, "Get cached view " + position);
                return manager.getCachedViews().get(position);
            }
            View item = inflater.inflate(R.layout.step_list_item, parent, false);
            TextView tvDescription = (TextView) item.findViewById(R.id.tvStepDescr);
            ImageView ivTimerControl = (ImageView) item.findViewById(R.id.ivTimerImage);
            TextView tvElapsedTime = (TextView) item.findViewById(R.id.tvElapsedTime);

            // Set info to fields from entity
            Step step = (Step) getItem(position);
            tvDescription.setText(step.getDescription());

            // Cache info about view
            manager.getIsCached().set(position, Boolean.TRUE);
            manager.getCachedViews().add(position, item);

            // If recipe has timers and there are no started timer or timers
            // belong to this recipe
            if (step.getTime() != 0
                    && (manager.getStartedRecipeId() == manager.getCurrentRecipe().getId()
                    || manager.getStartedRecipeId() == Manager.NONE)) {
                if (manager.isTimerStarted(position)) {
                    manager.setImageClockPressed(position, Boolean.TRUE);
                    Log.d(LOG_TAG, "Timers are running already.");
                } else {
                    manager.setImageClockPressed(position, Boolean.FALSE);
                }
                tvElapsedTime.setText(Utils.format(step.getTime()));
                View.OnClickListener listener = new TimerControlListener(position);
                tvElapsedTime.setOnClickListener(listener);
                ivTimerControl.setOnClickListener(listener);
            } else {
                tvElapsedTime.setVisibility(View.GONE);
                ivTimerControl.setVisibility(View.GONE);
            }
            return item;
        }
    }

    class TimerControlListener implements OnClickListener {

        private int position;

        public TimerControlListener() {

        }

        public TimerControlListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(RecipeDescriptionActivity.this, TimerService.class);
            switch (v.getId()) {

            // If cancelAllTimers pressed
            case R.id.btnCancelAllTimers:
                intent.putExtra(TimerService.ITEM_ACTION, TimerService.STOP_ALL_TIMERS);
                RecipeDescriptionActivity.this.startService(intent);
                break;

            // If TimerControl pressed
            default:
                intent.putExtra(TimerService.ITEM_POSITION, position);
                intent.putExtra(TimerService.ITEM_TIMER,
                        ((Step) adapter.getItem(position)).getTime());

                // Timer stop
                if (manager.isTimerStarted(position)) {
                    Log.d(LOG_TAG, "Button #" + position + " pressed: stopping");
                    intent.putExtra(TimerService.ITEM_ACTION, TimerService.STOP_TIMER);
                    RecipeDescriptionActivity.this.startService(intent);

                // Timer start
                } else {
                    Log.d(LOG_TAG, "Button #" + position + " pressed: starting");

                    // Set started recipe
                    manager.setStartedRecipeId(manager.getCurrentRecipe().getId());
                    intent.putExtra(TimerService.ITEM_ACTION, TimerService.START_TIMER);
                    RecipeDescriptionActivity.this.startService(intent);
                }
            }
        }
    }
}
