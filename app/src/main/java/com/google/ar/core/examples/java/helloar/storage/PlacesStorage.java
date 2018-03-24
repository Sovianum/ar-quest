package com.google.ar.core.examples.java.helloar.storage;

import com.google.ar.core.examples.java.helloar.quest.place.Places;

import java.util.HashMap;
import java.util.Map;

public class PlacesStorage {
    private Map<Integer, Places> storage;

    public PlacesStorage() {
        storage = new HashMap<>();
    }

    public void appPlaces(Integer id, Places places) {
        storage.put(id, places);
    }

    public Places getPlaces(Integer id) {
        return storage.get(id);
    }
}
