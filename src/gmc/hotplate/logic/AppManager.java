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
import gmc.hotplate.entities.Ingredient;
import gmc.hotplate.entities.Step;
import gmc.hotplate.util.AlarmReciver;
import gmc.hotplate.util.Notificator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;

public final class AppManager {

    public static final String LOG_TAG = AppManager.class.getName();
    public static final long NONE = -1;
    public static final int VIBRATE_TIME = 300;
    private static AppManager sInstance = null;
    private Handler handler;
    private Activity logoActivity;
    private ParentActivity currentActivity;
    private long currentRecipeId;
    private long startedRecipeId = NONE;
    private long notifyRecipeId = NONE;
    private boolean isNotify = false;
    private boolean inDescription = false;
    private List<Boolean> isTimerStarted;
    private Map<Integer, Long> alarms;
    private MapValueComparator comparator;
    private Notificator notificator;
    private AlarmReciver alarmReciver = new AlarmReciver();
    private DataManager dataManager;
    private boolean intentFromMenu = false;

    // Handled messages
    public static final int ACTION_NOTIFICATE = 0;
    public static final int ACTION_START = 1;
    public static final int ACTION_STOP = 2;
    public static final int ACTION_STOP_ALL = 3;
    public static final int ACTION_UPDATE = 4;

    private AppManager() {
        handler = new TimerHandler();
        isTimerStarted = new ArrayList<Boolean>();
        alarms = new HashMap<Integer, Long>();
        comparator = new MapValueComparator(alarms);
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
        return isAnyTimerStarted() && (isCurrentRecipeStarted());
    }

    public Boolean isTimerStarted(int position) {
        if (isTimerStarted.isEmpty()) {
            return Boolean.FALSE;
        }
        return isTimerStarted.get(position);
    }

    private void printMap(Map<Integer, Long> map) {
        for (Map.Entry<Integer, Long> e : map.entrySet()) {
            Log.d(LOG_TAG, e.getKey() + " : " + e.getValue());
        }
    }

    public boolean isInDescription() {
        return inDescription;
    }

    public void setInDescription(boolean inDescription) {
        this.inDescription = inDescription;
    }

    /* Returns position of timer, that has a minimal time to alarm
     * TreeMap sorted by value
     */
    private synchronized int getLeastTimerPosition() {
        TreeMap<Integer, Long> sorted = new TreeMap<Integer, Long>(comparator);
        sorted.putAll(alarms);
        return sorted.firstKey();
    }

    private void doStart(int position) {
        Log.d(LOG_TAG, "doStart() on position " + position);
        
        if (startedRecipeId == NONE) {
            startedRecipeId = currentRecipeId;
            for (int i = 0; i < getStartedRecipeSteps().size(); i++) {
                isTimerStarted.add(Boolean.FALSE);
            }
        }

        // if this timer has a minimal time to start
        // set alarm for this timer
        long timeUp = getStartedRecipeSteps().get(position).getTime() * 1000
                + System.currentTimeMillis();
        alarms.put(position, timeUp);
        if (getLeastTimerPosition() == position) {
            alarmReciver.cancelAlarm(logoActivity);
            alarmReciver.setAlarm(logoActivity, timeUp);
            Log.d(LOG_TAG, "Set alarm on pos = " + position + " with time=" + timeUp);
        }

        isTimerStarted.set(position, Boolean.TRUE);
        updateActivityControl(position);
        currentActivity.setActive();
    }

    private void doNotification(int position) {
        Log.d(LOG_TAG, "doNotification() on position " + position);
        notificationRoutine();
    }

    /*
     * Build notification message
     * Play audio signal
     */
    public void notificationRoutine() {
        Context context = getCurrentActivity();
        Notificator.Builder builder = new Notificator.Builder(context);
        notifyRecipeId = startedRecipeId;
        isNotify = true;
        PendingIntent pIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, RecipeDescriptionActivity.class), 0);
        builder.setContentText("Шаг завершен")
                .setContentTitle("Hotplate")
                .setIcon(R.drawable.icon_notification)
                .setTicker("Hotplate уведомление")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pIntent)
                .setVibrate(Boolean.TRUE)
                .autoCancel(Boolean.TRUE);
        if (currentRecipeId != startedRecipeId || !inDescription) {
            notificator.doNotification(builder.buildNotification());
        } else {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(VIBRATE_TIME);

        }
        MediaPlayer mp = MediaPlayer.create(currentActivity, R.raw.tone);
        mp.start();
    }

    private void doUpdate(int position, int seconds) {
        Log.v(LOG_TAG, "doUpdate() on position " + position + " " + seconds);
        if (isCurrentRecipeStarted()) {
            Message msg = new Message();
            msg.arg1 = position;
            msg.arg2 = seconds;
            currentActivity.update(msg);
        }
    }

    private void doStop(int position) {
        Log.d(LOG_TAG, "doStop() on position " + position);

        // Find next timer with minimal time to alarm
        if (getLeastTimerPosition() == position) {
            alarmReciver.cancelAlarm(logoActivity);
            alarms.remove(position);
            if (!alarms.isEmpty()) {
                Log.d(LOG_TAG, "Current alarm is stopped. Set new Alarm with pos="
                        + getLeastTimerPosition());
                alarmReciver.setAlarm(logoActivity, alarms.get(getLeastTimerPosition()));
            }
        } else {
            alarms.remove(position);
        }

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
        Log.d(LOG_TAG, "Reset to default");
        startedRecipeId = NONE;
        isTimerStarted.clear();
        currentActivity.setDefault();
        updateActivityControls();
        alarms.clear();
        alarmReciver.cancelAlarm(logoActivity);
    }

    private void updateActivityControls() {
        if (currentActivity instanceof RecipeDescriptionActivity
                && currentRecipeId != NONE) {
            ((RecipeDescriptionActivity) currentActivity).updateControls();
        }
    }

    private void updateActivityControl(int position) {
        if (isCurrentRecipeStarted()) {
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

    class MapValueComparator implements Comparator {
        private Map base;

        public MapValueComparator(Map base) {
            this.base = base;
        }

        public int compare(Object a, Object b) {
            if ((Long) base.get(a) < (Long) base.get(b)) {
                return -1;
            } else if ((Long) base.get(a) == (Long) base.get(b)) {
                return 0;
            } else {
                return 1;
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

    public long getCurrentRecipeId() {
        return currentRecipeId;
    }

    public void setCurrentRecipeId(long currentRecipeId) {
        this.currentRecipeId = currentRecipeId;
    }

    public long getStartedRecipeId() {
        return startedRecipeId;
    }

    public Activity getLogoActivity() {
        return logoActivity;
    }

    public void setLogoActivity(Activity logoActivity) {
        this.logoActivity = logoActivity;
    }

    public void setNotificator(Context context) {
        notificator = new Notificator(context);
    }

    public void cancelNotification() {
        notificator.cancel();
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public String getCurrentRecipeName() {
        return getRecipeName(currentRecipeId);
    }

    public List<Step> getCurrentRecipeSteps() {
        return getRecipeSteps(currentRecipeId);
    }

    public List<Ingredient> getCurrentRecipeIngredients() {
        return getRecipeIngredients(currentRecipeId);
    }

    public String getStartedRecipeName() {
        return getRecipeName(startedRecipeId);
    }

    public List<Step> getStartedRecipeSteps() {
        return getRecipeSteps(startedRecipeId);
    }

    public List<Ingredient> getStartedRecipeIngredients() {
        return getRecipeIngredients(startedRecipeId);
    }

    private String getRecipeName(long id) {
        String name = "";
        if (dataManager != null) {
            name = dataManager.getRecipeById(id).getName();
        }
        return name;
    }

    private List<Step> getRecipeSteps(long id) {
        List<Step> steps = null;
        if (dataManager != null) {
            steps = dataManager.getRecipeById(id).getSteps();
        }
        return steps;
    }

    private List<Ingredient> getRecipeIngredients(long id) {
        List<Ingredient> ingredients = null;
        if (dataManager != null) {
            ingredients = dataManager.getRecipeById(id).getIngredients();
        }
        return ingredients;
    }

    public boolean isCurrentRecipeStarted() {
        Log.d(LOG_TAG, "cur=" + currentRecipeId + " started=" + startedRecipeId);
        return currentRecipeId == startedRecipeId;
    }

    public boolean isCurrentRecipeNotify() {
        return currentRecipeId == notifyRecipeId;
    }

    public boolean isNotify() {
        return isNotify;
    }

    public void setNotify(boolean isNotify) {
        this.isNotify = isNotify;
    }

    public long getNotifyRecipeId() {
        return notifyRecipeId;
    }

    public void setNotifyRecipeId(long notifyRecipeId) {
        this.notifyRecipeId = notifyRecipeId;
    }

    public boolean isIntentFromMenu() {
        return intentFromMenu;
    }

    public void setIntentFromMenu(boolean intentFromMenu) {
        this.intentFromMenu = intentFromMenu;
    }

}
