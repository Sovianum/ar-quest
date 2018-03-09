package com.google.ar.core.examples.java.helloar.core.game;

import java.util.HashMap;
import java.util.Map;

public class Slot {
    public static class RepeatedItem {
        private final Item item;
        private int cnt;

        public RepeatedItem(Item item) {
            this.item = item;
            this.cnt = 1;
        }

        RepeatedItem(Item item, int cnt) {
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

    private int id;
    private boolean isAccessible;
    private String name;
    private Map<Integer, RepeatedItem> items;

    public Slot(int id, String name, boolean isAccessible) {
        this.id = id;
        this.name = name;
        this.isAccessible = isAccessible;
        this.items = new HashMap<>();
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

    public void put(Item item) {
        put(new RepeatedItem(item, 1));
    }

    public void put(RepeatedItem repeatedItem) {
        int id = repeatedItem.item.getId();
        if (items.containsKey(id)) {
            RepeatedItem old = items.get(id);
            old.cnt += repeatedItem.cnt;
            return;
        }
        items.put(id, repeatedItem);
    }

    public int getItemCnt(int itemID) {
        if (!items.containsKey(itemID)) {
            return 0;
        }
        return items.get(itemID).cnt;
    }

    public Map<Integer, RepeatedItem> getItems() {
        return items;
    }

    public int getId() {
        return id;
    }

    public boolean isAccessible() {
        return isAccessible;
    }

    public void setAccessible(boolean accessible) {
        isAccessible = accessible;
    }

    public String getName() {
        return name;
    }
}
