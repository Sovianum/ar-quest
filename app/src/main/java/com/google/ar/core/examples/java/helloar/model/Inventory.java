package com.google.ar.core.examples.java.helloar.model;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Item> items;

    public Inventory() {
        items = new ArrayList<>();
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void addItem(Item item) {
        items.add(item);
    }
}
