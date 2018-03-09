package com.google.ar.core.examples.java.helloar.core.game.journal;


import java.util.Calendar;
import java.util.Date;

public class TimestampRecord<T> {
    public static <T> TimestampRecord<T> recordNow(T data) {
        return new TimestampRecord<>(data, Calendar.getInstance().getTime());
    }

    T data;
    Date time;

    public TimestampRecord(T data, Date time) {
        this.data = data;
        this.time = time;
    }
}
