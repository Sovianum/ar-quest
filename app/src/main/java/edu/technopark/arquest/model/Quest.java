package edu.technopark.arquest.model;

import android.location.Location;

import edu.technopark.arquest.game.Place;
import com.google.common.collect.Lists;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Quest {
    private int id;
    private String title;
    private String description;
    private float rating;
    private Map<Integer, Place> placeMap;
    private Map<Location, Integer> placeIdMap;
    private String currPurpose;

    public Quest(int id, String title, String description, float rating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.rating = rating;
        placeMap = new HashMap<>();
        placeIdMap = new HashMap<>();
        currPurpose = null;
    }

    public void addPlace(Place place) {
        placeMap.put(place.getId(), place);
        placeIdMap.put(place.getLocation(), place.getId());
    }

    public int getId() {
        return id;
    }

    public Place getPlaceByID(int id) {
        return placeMap.get(id);
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public float getRating() {
        return rating;
    }

    public Map<Location, Integer> getPlaceIdMap() {
        return placeIdMap;
    }

    public Map<Integer, Place> getPlaceMap() {
        return placeMap;
    }

    public List<Place> getAvailablePlaces() {
        List<Place> places = Lists.newArrayList(placeMap.values());
        // todo check places on accessibility
        places.sort(new Comparator<Place>() {
            @Override
            public int compare(Place o1, Place o2) {
                return o1.getId() - o2.getId();
            }
        });
        return places;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getCurrPurpose() {
        return currPurpose;
    }

    public void setCurrPurpose(String currPurpose) {
        this.currPurpose = currPurpose;
    }
}
