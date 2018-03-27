package technopark.diploma.arquest.model;

import com.google.common.collect.Lists;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import technopark.diploma.arquest.core.game.Place;

public class Quest {
    private int id;
    private String title;
    private String description;
    private float rating;
    private Map<Integer, Place> placeMap;

    public Quest(int id, String title, String description, float rating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.rating = rating;
        placeMap = new HashMap<>();
    }

    public void addPlace(Place place) {
        placeMap.put(place.getId(), place);
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
}
