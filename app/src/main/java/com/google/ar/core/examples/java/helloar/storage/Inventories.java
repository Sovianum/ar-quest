package com.google.ar.core.examples.java.helloar.storage;

import com.google.ar.core.examples.java.helloar.GameApi;
import com.google.ar.core.examples.java.helloar.core.game.slot.Slot;


import java.util.HashMap;
import java.util.Map;

public class Inventories {
    private Map<Integer, Slot> inventories;

    public Inventories() {
        inventories = new HashMap<>();
    }

    public void addInventory(Integer id, Slot inventory) {
        inventories.put(id, inventory);
    }

    public void addCurrentInventory(Slot inventory) {
        inventories.put(GameApi.getCurrentQuestId(), inventory);
    }


    public Slot getInventory(Integer id) {
        return inventories.get(id);
    }

    public Slot getCurrentInventory() {
        return this.getInventory(GameApi.getCurrentQuestId());
    }
}
