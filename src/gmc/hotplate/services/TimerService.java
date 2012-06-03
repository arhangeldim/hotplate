/* Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.services;

import gmc.hotplate.logic.AppManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class TimerService extends Service {

    private static final String LOG_TAG = TimerService.class.getName();

    // Bundle keys
    public static final String ITEM_POSITION = "_item_position";
    public static final String ITEM_ACTION = "_item_action";
    public static final String ITEM_SECONDS = "_item_seconds";

    public static final int START_TIMER = 0;
    public static final int STOP_TIMER = 1;
    public static final int STOP_ALL_TIMERS = 2;

    private static final int PERIOD = 1000; // 1 sec

    private Handler handler;
    private Map<Integer, Timer> timers;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = AppManager.getInstance().getHandler();
        timers = new HashMap<Integer, Timer>();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        int position = bundle.getInt(ITEM_POSITION);
        int action = bundle.getInt(ITEM_ACTION);
        int seconds = bundle.getInt(ITEM_SECONDS);
        Log.d(LOG_TAG, "intent: action=" + action + " pos=" + position + " sec=" + seconds);

        switch (action) {
        case START_TIMER:
            startTimer(position, seconds);
            break;
        case STOP_TIMER:
            stopTimer(position);
            break;
        case STOP_ALL_TIMERS:
            stopAllTimers();
            break;
        default:
            stopAllTimers();
            break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void startTimer(int position, int seconds) {
        handler.sendMessage(createStartMessage(position));
        Timer t = new Timer();
        t.scheduleAtFixedRate(new UpdateTask(position, seconds), 0, PERIOD);
        timers.put(position, t);
    }

    public void stopTimer(int position) {
        timers.get(position).cancel();
        timers.remove(position);
        handler.sendMessage(createStopMessage(position));
    }

    public void stopAllTimers() {
        for (Map.Entry<Integer, Timer> entry : timers.entrySet()) {
            entry.getValue().cancel();
        }
        timers.clear();
        handler.sendMessage(createStopAllMessage());
    }

    private Message createUpdateMessage(int position, int seconds) {
        Message m = new Message();
        m.what = AppManager.ACTION_UPDATE;
        m.arg1 = position;
        m.arg2 = seconds;
        return m;
    }

    private Message createNotificateMessage(int position) {
        Message m = new Message();
        m.what = AppManager.ACTION_NOTIFICATE;
        m.arg1 = position;
        return m;
    }

    private Message createStopMessage(int position) {
        Message m = new Message();
        m.what = AppManager.ACTION_STOP;
        m.arg1 = position;
        return m;
    }

    private Message createStopAllMessage() {
        Message m = new Message();
        m.what = AppManager.ACTION_STOP_ALL;
        return m;
    }

    private Message createStartMessage(int position) {
        Message m = new Message();
        m.what = AppManager.ACTION_START;
        m.arg1 = position;
        return m;
    }

    /*
     * Task for started timer
     * Send update msg every second
     * Send notification msg when timer is end
     */
    class UpdateTask extends TimerTask {

        private int seconds;
        private int position;

        public UpdateTask(int position, int seconds) {
            this.position = position;
            this.seconds = seconds;
        }

        @Override
        public void run() {
            handler.sendMessage(createUpdateMessage(position, seconds));
            seconds--;
            if (seconds < 0) {
                handler.sendMessage(createNotificateMessage(position));
                stopTimer(position);
            }
        }
    }

}
