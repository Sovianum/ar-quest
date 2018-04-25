package edu.technopark.arquest.quest.game;

import android.util.Log;

import com.viro.core.AnimationTimingFunction;
import com.viro.core.AnimationTransaction;
import com.viro.core.PhysicsShape;
import com.viro.core.PhysicsShapeSphere;
import com.viro.core.Vector;

import java.sql.Time;

import edu.technopark.arquest.game.Item;
import edu.technopark.arquest.game.Place;
import edu.technopark.arquest.game.Player;

public class ActorPlayer extends Player {
    private Item item;
    private Place place;
    private PhysicsShapeSphere shape;
    private float accessRange;

    private long previousTime;
    private Vector previousPosition;

    public void updateOrientation(Vector position, Vector rotation, Vector forward) {
        setPosition(position);
        setRotation(rotation);

        if (item != null) {
            long currTime = System.currentTimeMillis();
            Vector offset = forward.normalize().scale(0.2f);
            Vector currPosition = position.add(offset);

            if (previousTime == 0 || previousPosition == null) {
                previousTime = currTime;
                previousPosition = currPosition;
                return;
            }

            long timeDelta = currTime - previousTime;
            previousTime = currTime;
            previousPosition = currPosition;

            AnimationTransaction.begin();
            AnimationTransaction.setAnimationDuration(timeDelta*10);
            AnimationTransaction.setTimingFunction(AnimationTimingFunction.EaseOut);
            item.setPosition(currPosition);
            item.setRotation(rotation);
            AnimationTransaction.commit();
        }
    }

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
        this.item.setVisible(true);
    }

    public void release() {
        if (item == null) {
            return;
        }
        item.setEnabled(false);
        item.setVisible(false);
        item = null;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
}
