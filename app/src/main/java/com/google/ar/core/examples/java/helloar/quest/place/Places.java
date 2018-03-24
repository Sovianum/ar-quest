package com.google.ar.core.examples.java.helloar.quest.place;

import com.google.ar.core.examples.java.helloar.core.game.Place;

import java.util.ArrayList;
import java.util.List;

public class Places {
    private List<Place> places;

    public Places() {
        places = new ArrayList<>();
    }

    public List<Place> getCheckpoints() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public List<Place> getPlaces;

    public void addCheckpoint(Place item) {
        places.add(item);
    }
}
