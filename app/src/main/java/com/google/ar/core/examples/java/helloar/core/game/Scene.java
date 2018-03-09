package com.google.ar.core.examples.java.helloar.core.game;

import java.util.HashMap;
import java.util.Map;

public class Scene {
    private int id;
    private String name;
    private String description;
    private Map<Integer, Slot> slots;
    private Map<Integer, InteractiveObject> interactiveObjects;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Map<Integer, Slot> getSlots() {
        return slots;
    }

    public Map<Integer, Slot> getAccessibleSlots() {
        Map<Integer, Slot> result = new HashMap<>();
        for (Map.Entry<Integer, Slot> entry : slots.entrySet()) {
            if (entry.getValue().isAccessible()) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public void setSlots(Map<Integer, Slot> slots) {
        this.slots = slots;
    }

    public Map<Integer, InteractiveObject> getInteractiveObjects() {
        return interactiveObjects;
    }

    public Map<Integer, InteractiveObject> getAccessibleInteractiveObjects() {
        Map<Integer, InteractiveObject> result = new HashMap<>();
        for (Map.Entry<Integer, InteractiveObject> entry : interactiveObjects.entrySet()) {
            if (entry.getValue().isAccessible()) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public void setInteractiveObjects(Map<Integer, InteractiveObject> interactiveObjects) {
        this.interactiveObjects = interactiveObjects;
    }
}
