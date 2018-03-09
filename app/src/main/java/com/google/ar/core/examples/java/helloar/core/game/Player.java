package com.google.ar.core.examples.java.helloar.core.game;

import com.google.ar.core.examples.java.helloar.core.game.journal.Journal;
import com.google.ar.core.examples.java.helloar.core.game.map.RoadMap;

public class Player {
    public static final String INVENTORY = "INVENTORY";

    private final Slot inventory;
    private final RoadMap roadMap;
    private final Journal<String> journal;

    public Player(Slot inventory, RoadMap roadMap, Journal<String> journal) {
        this.inventory = inventory;
        this.roadMap = roadMap;
        this.journal = journal;
    }

    public Player() {
        inventory = new Slot(0, INVENTORY, true);
        roadMap = new RoadMap();
        journal = new Journal<>();
    }

    public Journal<String> getJournal() {
        return journal;
    }

    public RoadMap getRoadMap() {
        return roadMap;
    }

    public Slot getInventory() {
        return inventory;
    }
}
