package edu.technopark.arquest.core.game.journal;

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

    public void add(T data, Date time) {
        records.add(new TimestampRecord<>(data, time));
    }

    public void addNow(T data) {
        records.add(TimestampRecord.recordNow(data));
    }

    public void clear() {
        records.clear();
    }
}
