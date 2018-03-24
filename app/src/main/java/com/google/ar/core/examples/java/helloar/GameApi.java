package com.google.ar.core.examples.java.helloar;

import com.google.ar.core.examples.java.helloar.storage.CheckpointsStorage;
import com.google.ar.core.examples.java.helloar.storage.Inventories;
import com.google.ar.core.examples.java.helloar.storage.Journals;

public class GameApi {
    private static final GameApi INSTANCE = new GameApi();
    private static Journals journals;
    private static Inventories inventories;
    private static CheckpointsStorage checkpointsStorage;
    private static Integer currentQuestId;

    private GameApi() {
        journals = new Journals();
        inventories = new Inventories();
        checkpointsStorage = new CheckpointsStorage();
    }

    public static Journals getJournals() {
        return GameApi.journals;
    }

    public static Inventories getInventories() {
        return GameApi.inventories;
    }

    public static CheckpointsStorage getCheckpointsStorage() {
        return GameApi.checkpointsStorage;
    }

    public static void setCurrentQuestId(Integer id) {
        GameApi.currentQuestId = id;
    }

    public static Integer getCurrentQuestId() {
        return GameApi.currentQuestId;
    }

    public static GameApi
    getInstance() {
        return INSTANCE;
    }
}
