package com.google.ar.core.examples.java.helloar.quest.game;

import com.google.ar.core.Pose;
import com.google.ar.core.examples.java.helloar.core.game.Item;
import com.google.ar.core.examples.java.helloar.core.game.Place;
import com.google.ar.core.examples.java.helloar.core.game.Player;

public class ActorPlayer extends Player {
    private Pose displace;
    private Item item;
    private Place place;

    public ActorPlayer(Pose displace) {
        this.displace = displace;
    }

    public Item getItem() {
        if (item == null) {
            return Item.VOID;
        }
        return item;
    }

    public void update(Pose pose) {
        if (item != null) {
            item.getGeom().applyGlobal(getGeom().getPose());
        }
        getGeom().applyGlobal(pose.compose(displace));
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
