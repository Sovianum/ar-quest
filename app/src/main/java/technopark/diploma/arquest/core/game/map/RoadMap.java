package technopark.diploma.arquest.core.game.map;

import java.util.ArrayList;
import java.util.List;

public class RoadMap {
    public static class Place {
        String name;
        String description;
        String where;
    }

    private final List<Place> places;

    public RoadMap() {
        places = new ArrayList<>();
    }

    void add(final Place place) {
        places.add(place);
    }

    List<Place> points() {
        return places;
    }
}


