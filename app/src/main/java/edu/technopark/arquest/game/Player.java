package edu.technopark.arquest.game;

import edu.technopark.arquest.game.journal.Journal;
import edu.technopark.arquest.game.map.RoadMap;
import edu.technopark.arquest.game.slot.Slot;

public class Player extends Identifiable3D {
    public static final String PLAYER = "PLAYER";
    public static final String INVENTORY = "INVENTORY";

    private final Slot inventory;
    private final RoadMap roadMap;
    private final Journal<String> journal;

    public Player() {
        this(new Slot(0, INVENTORY, false), new RoadMap(), new Journal<String>());
    }

    public Player(Slot inventory, RoadMap roadMap, Journal<String> journal) {
        super(PLAYER);
        this.inventory = inventory;
        this.roadMap = roadMap;
        this.journal = journal;
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
