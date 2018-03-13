package com.google.ar.core.examples.java.helloar.core.game;

import com.google.ar.core.examples.java.helloar.core.ar.SceneObject;
import com.google.ar.core.examples.java.helloar.core.game.slot.Slot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Place {
    private int id;
    private String name;
    private String description;
    private Map<Integer, Slot> slots;
    private Map<Integer, InteractiveObject> interactiveObjects;

    public Place() {
        slots = new HashMap<>();
        interactiveObjects = new HashMap<>();
    }

    public Place(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        slots = new HashMap<>();
        interactiveObjects = new HashMap<>();
    }

    public InteractiveObject getInteractiveObject(int id) {
        return interactiveObjects.get(id);
    }

    public Slot getSolt(int id) {
        return slots.get(id);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<SceneObject> getAll() {
        List<SceneObject> result = new ArrayList<>(slots.size() + interactiveObjects.size());
        result.addAll(slots.values());
        result.addAll(interactiveObjects.values());

        for (Slot slot : slots.values()) {
            result.addAll(slot.getItems());
        }
        for (InteractiveObject obj : interactiveObjects.values()) {
            result.addAll(obj.getItems());
        }
        return result;
    }

    public Map<Integer, Slot> getSlots() {
        return slots;
    }

    public Map<Integer, Slot> getAccessibleSlots() {
        Map<Integer, Slot> result = new HashMap<>();
        for (Map.Entry<Integer, Slot> entry : slots.entrySet()) {
            if (entry.getValue().isEnabled()) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public void setSlots(Map<Integer, Slot> slots) {
        this.slots = slots;
    }

    public void loadSlots(Iterable<Slot> slots) {
        for (Slot slot : slots) {
            this.slots.put(slot.getIdentifiable().getId(), slot);
        }
    }

    public Map<Integer, InteractiveObject> getInteractiveObjects() {
        return interactiveObjects;
    }

    public Map<Integer, InteractiveObject> getAccessibleInteractiveObjects() {
        Map<Integer, InteractiveObject> result = new HashMap<>();
        for (Map.Entry<Integer, InteractiveObject> entry : interactiveObjects.entrySet()) {
            if (entry.getValue().isEnabled()) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public void setInteractiveObjects(Map<Integer, InteractiveObject> interactiveObjects) {
        this.interactiveObjects = interactiveObjects;
    }

    public void loadInteractiveObjects(Iterable<InteractiveObject> interactiveObjects) {
        for (InteractiveObject interactiveObject : interactiveObjects) {
            this.interactiveObjects.put(interactiveObject.getIdentifiable().getId(), interactiveObject);
        }
    }
}
