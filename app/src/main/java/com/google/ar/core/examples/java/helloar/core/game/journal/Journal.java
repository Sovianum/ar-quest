package com.google.ar.core.examples.java.helloar.core.game.journal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Journal<T> {
    List<TimestampRecord<T>> records;

    public Journal() {
        records = new ArrayList<>();
    }

    public List<TimestampRecord<T>> getRecords() {
        return records;
    }

    void add(T data, Date time) {
        records.add(new TimestampRecord<>(data, time));
    }

    void addNow(T data) {
        records.add(TimestampRecord.recordNow(data));
    }
}
