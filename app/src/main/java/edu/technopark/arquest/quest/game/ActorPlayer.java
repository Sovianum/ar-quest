package edu.technopark.arquest.quest.game;

import com.viro.core.PhysicsShape;
import com.viro.core.PhysicsShapeSphere;

import edu.technopark.arquest.game.Item;
import edu.technopark.arquest.game.Place;
import edu.technopark.arquest.game.Player;

public class ActorPlayer extends Player {
    private Item item;
    private Place place;
    private PhysicsShapeSphere shape;
    private float accessRange;

    public PhysicsShapeSphere getShape() {
        return shape;
    }

    public void setShape(PhysicsShapeSphere shape) {
        this.shape = shape;
        accessRange = shape.getRadius();
    }

    public float getAccessRange() {
        return accessRange;
    }

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
