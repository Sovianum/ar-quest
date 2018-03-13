package com.google.ar.core.examples.java.helloar.storage;

import com.google.ar.core.examples.java.helloar.model.Inventory;
import com.google.ar.core.examples.java.helloar.network.Api;

import java.util.HashMap;
import java.util.Map;

public class Inventories {
    private Map<Integer, Inventory> inventories;

    public Inventories() {
        inventories = new HashMap<>();
    }

    public void addInventory(Integer id, Inventory inventory) {
        inventories.put(id, inventory);
    }

    public void addCurrentInventory(Inventory inventory) {
        inventories.put(Api.getCurrentQuestId(), inventory);
    }


    public Inventory getInventory(Integer id) {
        return inventories.get(id);
    }

    public Inventory getCurrentInventory() {
        return this.getInventory(Api.getCurrentQuestId());
    }
}
