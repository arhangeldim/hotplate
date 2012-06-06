/* Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmReciver extends BroadcastReceiver {

    private static final String LOG_TAG = AlarmReciver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Alarm", "OnReceive()");
        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm
                .newWakeLock(
                        (PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                                | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP),
                        "TAG");
        /*
        KeyguardManager keyguardManager = (KeyguardManager) context
                .getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardLock keyguardLock =  keyguardManager.newKeyguardLock("TAG");
        keyguardLock.disableKeyguard();
        */
        wakeLock.acquire();
        Toast.makeText(context, "Шаг завершен!", Toast.LENGTH_SHORT).show();
        wakeLock.release();

    }

    public void setAlarm(Context context, long startTime) {
        Log.d("Alarm", "setAlarm() on " + startTime + "sec");
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReciver.class);
        PendingIntent pintent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        am.set(AlarmManager.RTC_WAKEUP, startTime, pintent);

    }

    public void cancelAlarm(Context context) {
        Log.d(LOG_TAG, "Alarm cancelled");
        Intent intent = new Intent(context, AlarmReciver.class);
        PendingIntent sender = PendingIntent
                .getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmMananer = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmMananer.cancel(sender);
    }
}
