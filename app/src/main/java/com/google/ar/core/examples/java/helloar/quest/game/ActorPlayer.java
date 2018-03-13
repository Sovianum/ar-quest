package com.google.ar.core.examples.java.helloar.quest.game;

import com.google.ar.core.Pose;
import com.google.ar.core.examples.java.helloar.core.game.Item;
import com.google.ar.core.examples.java.helloar.core.game.Player;

public class ActorPlayer extends Player {
    private Pose displace;
    private Item item;

    public ActorPlayer(Pose displace) {
        this.displace = displace;
    }

    public Item getItem() {
        if (item == null) {
            return null;
        }
        item.getGeom().applyGlobal(getGeom().getPose());
        return item;
    }

    public void update(Pose pose) {
        getGeom().applyGlobal(pose.compose(displace));
    }

    public void hold(Item item) {
        this.item = item;
        this.item.setEnabled(true);
    }

    public void release() {
        item.setEnabled(false);
        item = null;
    }
}
