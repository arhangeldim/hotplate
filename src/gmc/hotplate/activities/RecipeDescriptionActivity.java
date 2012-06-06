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
import gmc.hotplate.services.TimerService;
import gmc.hotplate.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
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
public class RecipeDescriptionActivity extends ParentActivity {
    private static final String LOG_TAG = RecipeDescriptionActivity.class.getName();
    private ListView lvSteps;
    private StepListAdapter adapter;
    private TextView tvRecipeName;
    private TextView tvIngredients;
    private TextView tvIngredientAmount;
    private Button btnCancelAll;
    private Map<Integer, View> views = new HashMap<Integer, View>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.recipe_description);

        tvRecipeName = (TextView) findViewById(R.id.tvRecipeName);
        tvRecipeName.setTypeface(robotoCondensed);
        tvIngredients = (TextView) findViewById(R.id.tvRecipeIngredients);
        tvIngredients.setTypeface(robotoLight);

        tvIngredientAmount = (TextView) findViewById(R.id.tvIngredientAmount);
        tvIngredientAmount.setTypeface(robotoCondensed);

        lvSteps = (ListView) findViewById(R.id.lvSteps);
        btnCancelAll = (Button) findViewById(R.id.btnCancelAllTimers);
        btnCancelAll.setOnClickListener(new TimerControlListener());
        tvRecipeName.setText(manager.getCurrentRecipe().getName());
        List<Step> steps = manager.getCurrentRecipe().getSteps();
        adapter = new StepListAdapter(steps);
        lvSteps.setAdapter(adapter);
        showIngredients();
    }

    /*
     * Set activity state
     * default - no started recipes
     * active - current recipe is started
     * inactive - other recipe is started
     */
    private void setState() {
        if (manager.getStartedRecipe() == null) {
            setDefault();
        } else if (manager.getStartedRecipe() == manager.getCurrentRecipe()) {
            setActive();
        } else {
            setInactive();
        }
    }


    private void showIngredients() {
        StringBuilder builderIngred = new StringBuilder();
        StringBuilder builderAmount = new StringBuilder();
        for (Ingredient i : manager.getCurrentRecipe().getIngredients()) {
            builderIngred.append(i.getName() + "\n");
            Double amount = i.getAmount();
            if (amount.doubleValue() == amount.intValue()) {
                builderAmount.append(amount.intValue() + " " + i.getType() + "\n");
            } else {
                builderAmount.append(amount + " " + i.getType() + "\n");
            }
        }
        tvIngredients.setText(builderIngred.toString());
        tvIngredientAmount.setText(builderAmount.toString());
    }

    public void setBtnCancelAllEnabled(Boolean enabled) {
        btnCancelAll.setEnabled(enabled);
        Log.d(LOG_TAG, "set button enabled = " + enabled);
        if (enabled) {
            btnCancelAll.setTextColor(getResources().getColor(R.color.cblack));
        } else {
            btnCancelAll.setTextColor(getResources().getColor(R.color.inactive_button));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.setCurrentActivity(this);
        setState();
    }

    @Override
    public void onBackPressed() {
        // If this recipe is started
        if (manager.isCurrentActivityStarted()) {
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
                RecipeDescriptionActivity.this.finish();
                break;

            // Stop all timers
            case Dialog.BUTTON_NEGATIVE:
                intent.putExtra(TimerService.ITEM_ACTION, TimerService.STOP_ALL_TIMERS);
                RecipeDescriptionActivity.this.startService(intent);
                RecipeDescriptionActivity.this.finish();
                break;
            case Dialog.BUTTON_NEUTRAL:
                dialog.cancel();
            default:
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
            if (views.containsKey(position)) {
                return views.get(position);
            }
            View item = inflater.inflate(R.layout.step_list_item, parent, false);
            TextView tvDescription = (TextView) item.findViewById(R.id.tvStepDescr);

            // Set info to fields from entity
            Step step = (Step) getItem(position);
            tvDescription.setText(step.getDescription());
            tvDescription.setTypeface(robotoRegular);
            views.put(position, item);
            updateControlState(position);
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
            case R.id.btnCancelAllTimers:
                intent.putExtra(TimerService.ITEM_ACTION, TimerService.STOP_ALL_TIMERS);
                RecipeDescriptionActivity.this.startService(intent);
                break;
            default:
                intent.putExtra(TimerService.ITEM_POSITION, position);
                intent.putExtra(TimerService.ITEM_SECONDS,
                        ((Step) adapter.getItem(position)).getTime());
                if (manager.isTimerStarted(position)) {
                    intent.putExtra(TimerService.ITEM_ACTION, TimerService.STOP_TIMER);
                    RecipeDescriptionActivity.this.startService(intent);
                } else {
                    intent.putExtra(TimerService.ITEM_ACTION, TimerService.START_TIMER);
                    RecipeDescriptionActivity.this.startService(intent);
                }
            }
        }
    }

    /*
     * Set state of control elements
     */
    public void updateControlState(int position) {
        // FIXME(arhangeldim): NullPointer when try to set state
        // for view that not created yet
        if (views.size() <= position) {
            return;
        }
        Boolean visible = Boolean.FALSE;
        Boolean running = Boolean.FALSE;
        if (manager.getCurrentRecipe() == manager.getStartedRecipe()
                || manager.getStartedRecipe() == null) {
            running = manager.isTimerStarted(position);
            visible = Boolean.TRUE;
        }
        View v = views.get(position);
        Step step = manager.getCurrentRecipe().getSteps().get(position);

        ImageView iv = (ImageView) v.findViewById(R.id.ivTimerImage);
        TextView tv = (TextView) v.findViewById(R.id.tvElapsedTime);
        tv.setText(Utils.format(step.getTime()));
        if (visible && (step.getTime() != 0)) {
            iv.setVisibility(View.VISIBLE);
            tv.setVisibility(View.VISIBLE);
            TimerControlListener listener = new TimerControlListener(position);
            iv.setOnClickListener(listener);
            tv.setOnClickListener(listener);
            if (running) {
                iv.setImageResource(R.drawable.clock_pressed);
                tv.setTextColor(getResources().getColor(R.color.orange));
            } else {
                iv.setImageResource(R.drawable.clock_normal);
                tv.setTextColor(getResources().getColor(R.color.palette_gray));
            }
        } else {
            iv.setVisibility(View.GONE);
            tv.setVisibility(View.GONE);
        }
    }

    public void updateControls() {
        for (Map.Entry<Integer, View> v : views.entrySet()) {
            updateControlState(v.getKey());
        }
    }

     /* Default state:
     *  Disabled btnCancelAll
     */
    @Override
    public void setDefault() {
        btnCancelAll.setVisibility(View.VISIBLE);
        setBtnCancelAllEnabled(Boolean.FALSE);
    }

     /* Active state:
     *  Enabled btnCancelAll
     */
    @Override
    public void setActive() {
        btnCancelAll.setVisibility(View.VISIBLE);
        setBtnCancelAllEnabled(Boolean.TRUE);
    }

    @Override
    public void setInactive() {
        btnCancelAll.setVisibility(View.GONE);
    }

    @Override
    public void update(Message msg) {
        int position = msg.arg1;
        int seconds = msg.arg2;
        // FIXME(arhangeldim): Maybe it's a hack
        // Sometimes throws NullPointerException on Update
        if (views.size() > position) {
            TextView tvElapsedTime = (TextView) views.get(position)
                    .findViewById(R.id.tvElapsedTime);
            tvElapsedTime.setText(Utils.format(seconds));
        }
    }
}
