/* Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.logic;

import gmc.hotplate.R;
import gmc.hotplate.activities.ParentActivity;
import gmc.hotplate.activities.RecipeDescriptionActivity;
import gmc.hotplate.activities.RecipesListMenuActivity;
import gmc.hotplate.entities.Recipe;
import gmc.hotplate.util.AlarmReciver;
import gmc.hotplate.util.Notificator;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public final class AppManager {

    public static final String LOG_TAG = AppManager.class.getName();
    private static AppManager sInstance = null;
    private Handler handler;
    private Activity logoActivity;
    private ParentActivity currentActivity;
    private Recipe currentRecipe;
    private Recipe startedRecipe;
    private List<Boolean> isTimerStarted;

    // Handled messages
    public static final int ACTION_NOTIFICATE = 0;
    public static final int ACTION_START = 1;
    public static final int ACTION_STOP = 2;
    public static final int ACTION_STOP_ALL = 3;
    public static final int ACTION_UPDATE = 4;

    private AppManager() {
        handler = new TimerHandler();
        isTimerStarted = new ArrayList<Boolean>();
    }

    public static synchronized AppManager getInstance() {
        if (sInstance == null) {
            sInstance = new AppManager();
        }
        return sInstance;
    }

    public Boolean isAnyTimerStarted() {
        Boolean result = Boolean.FALSE;
        for (int i = 0; i < isTimerStarted.size(); i++) {
            result |= isTimerStarted.get(i);
        }
        return result;
    }

    public Boolean isCurrentActivityStarted() {
        return isAnyTimerStarted() && (currentRecipe == startedRecipe);
    }

    public Boolean isTimerStarted(int position) {
        if (isTimerStarted.isEmpty()) {
            return Boolean.FALSE;
        }
        return isTimerStarted.get(position);
    }

    private void doStart(int position) {
        Log.d(LOG_TAG, "doStart() on position " + position);
        if (startedRecipe == null) {
            startedRecipe = currentRecipe;
            for (int i = 0; i < startedRecipe.getSteps().size(); i++) {
                isTimerStarted.add(Boolean.FALSE);
            }
        }

        /*
        AlarmReciver alarm = new AlarmReciver();
        alarm.setAlarm(logoActivity, startedRecipe.getSteps().get(position).getTime());
        */

        isTimerStarted.set(position, Boolean.TRUE);
        updateActivityControl(position);
        currentActivity.setActive();
    }

    private void doNotification(int position) {
        Log.d(LOG_TAG, "doNotification() on position " + position);
        /*
        Context context = (Context) getCurrentActivity();
        AlarmManager am = (AlarmManager) getCurrentActivity()
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getCurrentActivity(), TimeAlarm.class);
        PendingIntent pintent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pintent);
        */
    }

    public void notificationRoutine() {
        Context context = getCurrentActivity();
        Notificator.Builder builder = new Notificator.Builder(context);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, RecipesListMenuActivity.class), 0);
        builder.setContentText("Шаг завершен")
                .setContentTitle("Уведомление")
                .setIcon(R.drawable.icon_notification)
                .setTicker("Шаг завершен")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pIntent)
                .autoCancel(Boolean.TRUE);

        new Notificator(context).doNotification(builder.buildNotification());
        MediaPlayer mp = MediaPlayer.create(currentActivity, R.raw.tone);
        mp.start();
    }

    private void doUpdate(int position, int seconds) {
        Log.v(LOG_TAG, "doUpdate() on position " + position + " " + seconds);
        if (currentRecipe == startedRecipe) {
            Message msg = new Message();
            msg.arg1 = position;
            msg.arg2 = seconds;
            currentActivity.update(msg);
        }
    }

    private void doStop(int position) {
        Log.d(LOG_TAG, "doStop() on position " + position);
        isTimerStarted.set(position, Boolean.FALSE);
        updateActivityControl(position);
        if (!isAnyTimerStarted()) {
            resetToDefault();
        }
    }

    private void doStopAll() {
        Log.d(LOG_TAG, "doStopAll()");
        resetToDefault();
    }

    /*
     * There are no started activity Clear all timers and set interface to
     * default
     */
    private void resetToDefault() {
        startedRecipe = null;
        isTimerStarted.clear();
        currentActivity.setDefault();
        updateActivityControls();
    }

    private void updateActivityControls() {
        if (currentActivity instanceof RecipeDescriptionActivity) {
            ((RecipeDescriptionActivity) currentActivity).updateControls();
        }
    }

    private void updateActivityControl(int position) {
        if (currentRecipe == startedRecipe) {
            ((RecipeDescriptionActivity) currentActivity)
                    .updateControlState(position);
        }
    }

    class TimerHandler extends Handler {

        /*
         * Message.arg1 = position
         * Message.arg2 = seconds
         * Message.what = action
         */
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case ACTION_START:
                doStart(msg.arg1);
                break;
            case ACTION_NOTIFICATE:
                doNotification(msg.arg1);
                break;
            case ACTION_UPDATE:
                doUpdate(msg.arg1, msg.arg2);
                break;
            case ACTION_STOP:
                doStop(msg.arg1);
                break;
            case ACTION_STOP_ALL:
                doStopAll();
                break;
            default:
                Log.d(LOG_TAG, "HandleMessage: Undefined action " + msg.what);
            }
        }

    }

    public Handler getHandler() {
        return handler;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(ParentActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public Recipe getCurrentRecipe() {
        return currentRecipe;
    }

    public void setCurrentRecipe(Recipe currentRecipe) {
        this.currentRecipe = currentRecipe;
    }

    public Recipe getStartedRecipe() {
        return startedRecipe;
    }

    public Activity getLogoActivity() {
        return logoActivity;
    }

    public void setLogoActivity(Activity logoActivity) {
        this.logoActivity = logoActivity;
    }
}