package technopark.diploma.arquest.storage;

import technopark.diploma.arquest.core.game.slot.Slot;

import java.util.HashMap;
import java.util.Map;

public class Inventories {
    private Map<Integer, Slot> inventories;

    public Inventories() {
        inventories = new HashMap<>();
    }

    public void setInventory(Integer id, Slot inventory) {
        inventories.put(id, inventory);
    }

    public Slot getInventory(Integer id) {
        return inventories.get(id);
    }
}
