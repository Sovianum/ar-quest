package com.google.ar.core.examples.java.helloar.core.ar.record;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ObjectRecordStorage {
    private Map<String, Collection<ObjectRecord>> nameIndex;
    private Map<Integer, ObjectRecord> idIndex;
    private Map<Integer, ObjectRecord> sceneIDIndex;

    public ObjectRecordStorage(Collection<ObjectRecord> records) {
        nameIndex = new HashMap<>();
        idIndex = new HashMap<>();
        sceneIDIndex = new HashMap<>();

        load(records);
    }

    public Collection<ObjectRecord> getByName(String name) {
        return nameIndex.get(name);
    }

    public ObjectRecord getByID(int id) {
        return idIndex.get(id);
    }

    public ObjectRecord getBySceneID(int sceneID) {
        return sceneIDIndex.get(sceneID);
    }

    public Map<String, Collection<ObjectRecord>> getNameIndex() {
        return nameIndex;
    }

    public Map<Integer, ObjectRecord> getIdIndex() {
        return idIndex;
    }

    public Map<Integer, ObjectRecord> getSceneIDIndex() {
        return sceneIDIndex;
    }

    private void load(Collection<ObjectRecord> records) {
        for (ObjectRecord record : records) {
            idIndex.put(record.getId(), record);
            sceneIDIndex.put(record.getSceneId(), record);

            if (!nameIndex.containsKey(record.getName())) {
                nameIndex.put(record.getName(), new ArrayList<ObjectRecord>());
            }
            nameIndex.get(record.getName()).add(record);
        }
    }
}
