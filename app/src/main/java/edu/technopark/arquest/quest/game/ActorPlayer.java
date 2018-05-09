package edu.technopark.arquest.quest.game;

import android.util.Log;

import com.viro.core.Animation;
import com.viro.core.AnimationTimingFunction;
import com.viro.core.AnimationTransaction;
import com.viro.core.PhysicsShape;
import com.viro.core.PhysicsShapeSphere;
import com.viro.core.Vector;

import java.sql.Time;
import java.util.Queue;

import edu.technopark.arquest.game.Item;
import edu.technopark.arquest.game.Place;
import edu.technopark.arquest.game.Player;

public class ActorPlayer extends Player {
    private Item item;
    private Place place;
    private PhysicsShapeSphere shape;
    FlyingAnimator animator = new FlyingAnimator(item);

    public void updateOrientation(Vector position, Vector rotation, Vector forward) {
        setPosition(position);
        setRotation(rotation);

        animator.addCheckPoint(position, rotation, forward);
    }

    public PhysicsShapeSphere getShape() {
        return shape;
    }

    public void setShape(PhysicsShapeSphere shape) {
        this.shape = shape;
    }

    public Item getItem() {
        return item;
    }

    public void hold(Item item) {
        if (item == null) {
            return;
        }
        animator.setAnimatedNode(item);
        this.item = item;
        this.item.setEnabled(true);
        this.item.setVisible(true);
    }

    public void release() {
        if (item == null) {
            return;
        }
        animator.setAnimatedNode(null);
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
