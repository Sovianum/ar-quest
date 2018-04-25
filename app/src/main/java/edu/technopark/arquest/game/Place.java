package edu.technopark.arquest.game;

import android.location.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Place {
    private int id;
    private String name;
    private String description;
    private Map<String, InteractiveObject> interactiveObjects;
    private Location location;
    private String startPurpose;

    public Place() {
        interactiveObjects = new HashMap<>();
        name = "default";
        location = new Location("");
        location.setLatitude(0.0);
        location.setLongitude(0.0);
    }

    public Place(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = new Location("");
        location.setLatitude(0.0);
        location.setLongitude(0.0);
        interactiveObjects = new HashMap<>();
    }

    public String getStartPurpose() {
        return startPurpose;
    }

    public void setStartPurpose(String startPurpose) {
        this.startPurpose = startPurpose;
    }

    public InteractiveObject getInteractiveObject(int id) {
        return interactiveObjects.get(id);
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Identifiable3D> getAll() {
        List<Identifiable3D> result = new ArrayList<>(interactiveObjects.size());
        result.addAll(interactiveObjects.values());

        for (InteractiveObject obj : interactiveObjects.values()) {
            Collection<Item> items = obj.getItems();
            if (items != null) result.addAll(items);
        }
        return result;
    }

    public Collection<InteractiveObject> getInteractive() {
        return interactiveObjects.values();
    }

    public List<Identifiable3D> getAllVisible() {
        List<Identifiable3D> result = new ArrayList<>(interactiveObjects.size());
        for (Identifiable3D node : interactiveObjects.values()) {
            if (node.isVisible()) {
                result.addAll(interactiveObjects.values());
            }
        }
        return result;
    }

    public Map<String, InteractiveObject> getInteractiveObjects() {
        return interactiveObjects;
    }

    public Map<String, InteractiveObject> getEnabledInteractiveObjects() {
        Map<String, InteractiveObject> result = new HashMap<>();
        for (Map.Entry<String, InteractiveObject> entry : interactiveObjects.entrySet()) {
            if (entry.getValue().isEnabled()) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public void setInteractiveObjects(Map<String, InteractiveObject> interactiveObjects) {
        this.interactiveObjects = interactiveObjects;
    }

    public void loadInteractiveObjects(Iterable<InteractiveObject> interactiveObjects) {
        for (InteractiveObject interactiveObject : interactiveObjects) {
            this.interactiveObjects.put(interactiveObject.getName(), interactiveObject);
        }
    }
}
