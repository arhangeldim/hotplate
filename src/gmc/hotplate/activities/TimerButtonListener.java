package gmc.hotplate.activities;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TimerButtonListener implements OnClickListener {

    private static final String LOG_TAG = TimerButtonListener.class.getName();
    private Activity activity;
    private TextView view;
    private Timer timer;
    private int timerDefault;
    private int timerCount;
    private Boolean isStarted = Boolean.FALSE;
    public static final int ONE_SECOND = 1000;

    public TimerButtonListener(Activity activity, TextView view, int timerCount) {
        super();
        this.activity = activity;
        this.view = view;
        this.timerCount = timerCount;
        this.timerDefault = timerCount;
    }

    @Override
    public void onClick(final View v) {
        if (isStarted) {
            resetTimer();
            ((Button) v).setText("Start");
            view.setText(String.valueOf(timerDefault));
        } else {
            ((Button) v).setText("Stop");
            if (getTimerCount() <= 0) {
                return;
            }
            timer = new Timer();
            final Runnable updateTask = new Runnable() {

                @Override
                public void run() {
                    if (timerCount <= 0) {
                        resetTimer();
                        ((Button) v).setText("Start");
                        view.setText(String.valueOf(timerDefault));
                        Log.d(LOG_TAG, "def: " + timerDefault);
                    } else {
                        decTimerCount();
                        view.setText(String.valueOf(timerCount));
                    }
                }
            };
            timer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    activity.runOnUiThread(updateTask);
                }
            }, 0, ONE_SECOND);
            isStarted = true;
        }
    }

    void resetTimer() {
        isStarted = false;
        cancelTimer();
        timerCount = timerDefault;
    }

    void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public int getTimerCount() {
        return timerCount;
    }

    private void decTimerCount() {
        timerCount--;
    }


}
