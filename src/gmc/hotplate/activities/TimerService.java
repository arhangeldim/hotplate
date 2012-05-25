/* Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.activities;

import gmc.hotplate.logic.Manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class TimerService extends Service {

    public static final String LOG_TAG = TimerService.class.getName();
    public static final String ITEM_ACTION = "item_action";
    public static final String ITEM_POSITION = "item_position";
    public static final String ITEM_TIMER = "item_timer";
    public static final int TIMER_START = 0;
    public static final int TIMER_STOP = 1;
    public static final int ALL_TIMERS_STOP = 2;
    public static final int INTERVAL = 1000;

    private Manager manager;
    private Map<Integer, Timer> timers;
    // Default value for timer on positoin <position, seconds>
    private Map<Integer, Integer> defaultTimerSeconds;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "Service created");
        timers = new HashMap<Integer, Timer>();
        defaultTimerSeconds = new HashMap<Integer, Integer>();
        manager = Manager.getInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAllTimers();
        Log.d(LOG_TAG, "Service destroyed. All time cancelled");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        int position = bundle.getInt(ITEM_POSITION);
        int action = bundle.getInt(ITEM_ACTION);
        int seconds = bundle.getInt(ITEM_TIMER);
        Log.d(LOG_TAG, "Extras: action=" + action + ", pos=" + position + "sec=" + seconds);
        if (action == TIMER_START) {
            startTimer(position, seconds);
        } else if (action == TIMER_STOP) {
            stopTimer(position, seconds);
        } else if (action == ALL_TIMERS_STOP) {
            stopAllTimers();
        } else {
            stopAllTimers();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startTimer(int position, int seconds) {
        manager.setBtnAllTimerCancelEnabled(Boolean.TRUE);
        manager.setIsTimerStarted(position, Boolean.TRUE);
        manager.setImageClockPressed(position, Boolean.TRUE);
        defaultTimerSeconds.put(position, seconds);
        final Runnable task = new UpdateViewTask(position, seconds);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                manager.getActivity().runOnUiThread(task);
            }
        }, 0, INTERVAL);
        timers.put(position, timer);
    }

    private void stopTimer(int position, int seconds) {
        Log.d(LOG_TAG, "cancel timer: pos = " + position + " timers.size() = " + timers.size());
        manager.setIsTimerStarted(position, Boolean.FALSE);
        timers.get(position).cancel();
        manager.setElapsedTime(position, seconds);
        manager.setImageClockPressed(position, Boolean.FALSE);
        if (!manager.isAnyTimerStarted()) {
            Log.d(LOG_TAG, "Set all field to default");
            if (manager.getCurrentRecipe().getId() == manager.getStartedRecipeId()) {
                manager.setBtnAllTimerCancelEnabled(Boolean.FALSE);
            }
            manager.setStartedRecipeId(Manager.NONE);
            // If recipe is ended clear timers hash
            timers.clear();
            defaultTimerSeconds.clear();
        }
        Log.d(LOG_TAG, "Timer #" + position + " stopped");
    }

    private void stopAllTimers() {
        Log.d(LOG_TAG, "timers size = " + timers.size());
        for (Map.Entry<Integer, Timer> t : timers.entrySet()) {
            Log.d(LOG_TAG, "Stopping timer #" + t.getKey() + " from " + timers.size());
            if (t.getValue() != null) {
                int position = t.getKey();
                int seconds = defaultTimerSeconds.get(t.getKey());
                manager.setIsTimerStarted(position, Boolean.FALSE);
                timers.get(position).cancel();
                manager.setElapsedTime(position, seconds);
                manager.setImageClockPressed(position, Boolean.FALSE);
            }
        }
        if (manager.getCurrentRecipe().getId() == manager.getStartedRecipeId()) {
            manager.setBtnAllTimerCancelEnabled(Boolean.FALSE);
        }
        manager.setStartedRecipeId(Manager.NONE);
        timers.clear();
        defaultTimerSeconds.clear();
    }

    class UpdateViewTask implements Runnable {

        private int position;
        private int defaultSeconds;
        private int seconds;

        public UpdateViewTask(int position, int seconds) {
            this.position = position;
            this.seconds = seconds;
            defaultSeconds = seconds;
        }

        @Override
        public void run() {
            manager.setElapsedTime(position, seconds);
            seconds--;
            if (seconds < 0) {
                stopTimer(position, defaultSeconds);
                Toast.makeText(manager.getActivity(),
                        "Timer #" + position + " ended", Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "Toast notification: Timer #" + position + " ended");
            }
        }
    }
}
