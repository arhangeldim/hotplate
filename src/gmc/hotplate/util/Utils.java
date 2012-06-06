/* Copyright (c) 2012 Hotplate developers. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 *
 * project: Hotplate
 */

package gmc.hotplate.util;

public final class Utils {

    private Utils() {
        throw new AssertionError("Not for instantiations!");
    }

    /*
     * @param time - time in seconds
     * Returns time in format hh:mm:ss
     * If hours == 0                    mm:ss
     * If hours == 0 && minutes < 10    0m:ss
     * If hours < 10                    h:mm:ss
     *
     */
    public static String format(int time) {
        if (time <= 0) {
            return "00:00";
        }
        int seconds = 0;
        int minutes = 0;
        int hours = 0;
        hours = time / 3600;
        time -= hours * 3600;
        minutes = time / 60;
        time -= minutes * 60;
        seconds = time;
        return format(hours, minutes, seconds);
    }

    private static String format(int hours, int minutes, int seconds) {
        // Overflow
        if (hours >=24 || minutes >=60 || seconds >= 60) {
            return "00:00";
        }
        StringBuilder builder = new StringBuilder();

        if (hours != 0) {
            builder.append(String.valueOf(hours));
            builder.append(":");
        }
        if ((minutes < 10 && hours != 0) || (minutes == 0)) {
            builder.append(0);
        }
        builder.append(String.valueOf(minutes));
        builder.append(":");
        if (seconds < 10) {
            builder.append(0);
        }
        builder.append(String.valueOf(seconds));
        return builder.toString();
    }

}
