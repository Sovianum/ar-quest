package com.google.ar.core.examples.java.helloar.storage;

import com.google.ar.core.examples.java.helloar.network.Api;
import com.google.ar.core.examples.java.helloar.quest.place.Checkpoints;

import java.util.HashMap;
import java.util.Map;

public class CheckpointsStorage {
    private Map<Integer, Checkpoints> storage;

    public CheckpointsStorage() {
        storage = new HashMap<>();
    }

    public void addCheckpoints(Integer id, Checkpoints checkpoints) {
        storage.put(id, checkpoints);
    }

    public void addCurrentCheckpoints(Checkpoints checkpoints) {
        storage.put(Api.getCurrentQuestId(), checkpoints);
    }


    public Checkpoints getCheckpoints(Integer id) {
        return storage.get(id);
    }

    public Checkpoints getCurrentCheckpoints() {
        return this.getCheckpoints(Api.getCurrentQuestId());
    }
}
