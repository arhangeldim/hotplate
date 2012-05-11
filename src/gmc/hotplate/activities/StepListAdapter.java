package gmc.hotplate.activities;

import gmc.hotplate.R;
import gmc.hotplate.entities.Step;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class StepListAdapter extends BaseAdapter {

    private static final String LOG_TAG = "StepListAdapter";
    private Context context;
    private List<Step> steps;

    // When view is created, set isCreated=true
    // and add view in List views.  List size = number of positions = number of steps
    private List<Boolean> isCreated;
    private List<View> views;

    private TextView tvDescription;
    private TextView tvTimerLabel;
    private Button btnTimerControl;
    private Activity activity;

    public StepListAdapter(Context context, List<Step> steps, Activity activity) {
        this.context = context;
        this.steps = steps;
        this.activity = activity;
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
        View v;
        // Don't need to create view more than one time
        if (isCreated.get(position)) {
            return views.get(position);
        }
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.step_list_item, parent, false);

        tvDescription = (TextView) v.findViewById(R.id.tvStepDescr);
        Log.d(LOG_TAG, "position: " + position);
        Step step = (Step) getItem(position);
        tvDescription.setText(step.getDescription());
        tvTimerLabel = (TextView) v.findViewById(R.id.tvTimerLabel);
        btnTimerControl = (Button) v.findViewById(R.id.btnTimerControl);
        if (step.getTime() != 0) {
            tvTimerLabel.setText(String.valueOf(step.getTime()));
            btnTimerControl.setOnClickListener(
                    new TimerButtonListener(activity, tvTimerLabel, step.getTime()));
            btnTimerControl.setText("Start");
        } else {
            btnTimerControl.setVisibility(View.INVISIBLE);
        }
        views.add(v);
        isCreated.set(position, Boolean.TRUE);
        return v;
    }

}


