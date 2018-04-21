package edu.technopark.arquest.core.game.slot;

import edu.technopark.arquest.core.ar.SceneObject;
import edu.technopark.arquest.core.ar.identifiable.Identifiable;
import edu.technopark.arquest.core.game.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Slot extends SceneObject {
    public static class RepeatedItem {
        private final Item item;
        private int cnt;

        public RepeatedItem(Item item) {
            this.item = item;
            this.cnt = 1;
        }

        public RepeatedItem(Item item, int cnt) {
            this.item = item;
            this.cnt = cnt;
        }

        RepeatedItem emptyCopy() {
            return new RepeatedItem(item, 0);
        }

        public Item getItem() {
            return item;
        }

        public int getCnt() {
            return cnt;
        }

        public boolean empty() {
            return cnt == 0;
        }

        public int moveAll(RepeatedItem another) {
            return move(another, cnt);
        }

        public int move(RepeatedItem another, int moveCnt) {
            return another.add(drop(moveCnt));
        }

        public int add(int addCnt) {
            cnt += addCnt;
            return addCnt;
        }

        public int dropAll() {
            return drop(cnt);
        }

        public int drop(int dropCnt) {
            int dropped = dropCnt < cnt ? dropCnt : cnt;
            cnt -= dropped;
            return dropped;
        }
    }
    protected int id;
    protected String name;
    protected Map<Integer, RepeatedItem> items;

    public Slot(int id, String name, boolean enabled) {
        this.id = id;
        this.name = name;
        setEnabled(enabled);
        this.items = new HashMap<>();

        setIdentifiable(new Identifiable(name, id));
        setEnabled(enabled);
    }

    public boolean move(int itemID, Slot another) {
        if (!items.containsKey(itemID) || items.get(itemID).empty()) {
            return false;
        }
        RepeatedItem item = items.get(itemID);

        if (!another.items.containsKey(itemID)) {
            another.items.put(itemID, item.emptyCopy());
        }
        RepeatedItem anotherItem = another.items.get(itemID);

        item.moveAll(anotherItem);

        if (item.empty()) {
            items.remove(itemID);
        }
        return true;
    }

    public boolean remove(int itemID, int itemCnt) {
        RepeatedItem item = items.get(itemID);
        if (item == null || item.getCnt() < itemCnt) {
            return false;
        }
        item.drop(itemCnt);
        if (item.empty()) {
            items.remove(itemID);
        }
        return true;
    }

    public void removeAll(int itemID) {
        items.remove(itemID);
    }

    public boolean put(Item item) {
        return put(new RepeatedItem(item, 1));
    }

    public boolean put(RepeatedItem repeatedItem) {
        int id = repeatedItem.item.getId();
        if (items.containsKey(id)) {
            RepeatedItem old = items.get(id);
            old.cnt += repeatedItem.cnt;
            return true;
        }
        items.put(id, repeatedItem);
        return true;
    }

    public int getItemCnt(int itemID) {
        if (!items.containsKey(itemID)) {
            return 0;
        }
        return items.get(itemID).cnt;
    }

    public Map<Integer, RepeatedItem> getRepeatedItems() {
        return items;
    }

    public List<Item> getItems() {
        List<Item> result = new ArrayList<>(items.size());
        for (RepeatedItem ri : items.values()) {
            result.add(ri.item);
        }
        return result;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void clear() {
        items.clear();
    }
}
