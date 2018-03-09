package com.google.ar.core.examples.java.helloar.core.ar.record;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneRecord {
    private final List<ObjectRecord> objectRecords;
    private Map<Integer, ObjectRecord> index;

    public SceneRecord(List<ObjectRecord> objectRecords) {
        objectRecords.sort(new Comparator<ObjectRecord>() {
            @Override
            public int compare(ObjectRecord o1, ObjectRecord o2) {
                return o1.getId() - o2.getId();
            }
        });
        this.objectRecords = objectRecords;
        index = new HashMap<>(objectRecords.size());
        buildIndex();
    }

    public ObjectRecordStorage getObjectStorage() {
        return new ObjectRecordStorage(objectRecords);
    }

    public ObjectRecord getById(int id) {
        return index.get(id);
    }

    public List<ObjectRecord> getObjectRecords() {
        return objectRecords;
    }

    private void buildIndex() {
        for (ObjectRecord rec : objectRecords) {
            index.put(rec.getId(), rec);
        }
    }
}
