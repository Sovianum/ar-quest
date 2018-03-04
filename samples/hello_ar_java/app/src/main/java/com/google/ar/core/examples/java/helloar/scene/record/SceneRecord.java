package com.google.ar.core.examples.java.helloar.scene.record;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class SceneRecord {
    private final ObjectRecord[] objectRecords;
    private Map<Integer, ObjectRecord> index;

    public SceneRecord(ObjectRecord[] objectRecords) {
        Arrays.sort(objectRecords, new Comparator<ObjectRecord>() {
            @Override
            public int compare(ObjectRecord o1, ObjectRecord o2) {
                return o1.getId() - o2.getId();
            }
        });
        this.objectRecords = objectRecords;
        index = new HashMap<>(objectRecords.length);
        buildIndex();
    }

    public ObjectRecord getById(int id) {
        return index.get(id);
    }

    public ObjectRecord[] getObjectRecords() {
        return objectRecords;
    }

    private void buildIndex() {
        for (ObjectRecord rec : objectRecords) {
            index.put(rec.getId(), rec);
        }
    }
}
