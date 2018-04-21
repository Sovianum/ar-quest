package edu.technopark.arquest.quest.game;

import edu.technopark.arquest.game.Item;
import edu.technopark.arquest.game.Place;
import edu.technopark.arquest.game.Player;

public class ActorPlayer extends Player {
    private Item item;
    private Place place;

    public Item getItem() {
        return item;
    }

    public void hold(Item item) {
        if (item == null) {
            return;
        }
        this.item = item;
        this.item.setEnabled(true);
    }

    public void release() {
        if (item == null) {
            return;
        }
        item.setEnabled(false);
        item = null;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
}
