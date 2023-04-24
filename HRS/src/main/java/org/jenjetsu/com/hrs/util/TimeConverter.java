package org.jenjetsu.com.hrs.util;

public class TimeConverter {

    public static long ceilSecondsToMinutes(long seconds) {
        return (long) Math.ceil(seconds / 60.0);
    }
}
