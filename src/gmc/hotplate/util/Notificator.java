/* Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.util;

import java.util.HashMap;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;

public class Notificator {
    private static final String LOG_TAG = Notificator.class.getName();
    private int lastId = 0;
    private Map<Integer, Notification> notifications;
    private NotificationManager nm;

    public Notificator(Context context) {
        notifications = new HashMap<Integer, Notification>();
        nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public int doNotification(Notification notification) {
        Log.d(LOG_TAG, "Event notification: " + lastId);
        nm.notify(lastId, notification);
        notifications.put(lastId, notification);
        return lastId++;
    }

    public static class Builder {
        private Context context;
        private Notification notification;
        private String contentText;
        private String contentTitle;

        public Builder(Context context) {
            this.context = context;
            notification = new Notification();
            contentText = "";
            contentTitle = "";
        }

        public Notification buildNotification() {
            return notification;
        }

        public Builder setIcon(int iconId) {
            notification.icon = iconId;
            return this;
        }

        public Builder setTicker(String ticker) {
            notification.tickerText = ticker;
            return this;
        }

        public Builder setWhen(long when) {
            notification.when = when;
            return this;
        }

        public Builder setContentText(String contentText) {
            this.contentText = contentText;
            return this;
        }

        public Builder setContentTitle(String contentTitle) {
            this.contentTitle = contentTitle;
            return this;
        }

        public Builder autoCancel(Boolean auto) {
            if (auto) {
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
            }
            return this;
        }

        public Builder setDefault(int property) {
            notification.defaults = property;
            return this;
        }

        public Builder setContentIntent(PendingIntent intent) {
            notification.setLatestEventInfo(context, contentTitle, contentText,
                    intent);
            return this;
        }

    }

}
