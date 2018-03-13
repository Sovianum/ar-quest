package com.google.ar.core.examples.java.helloar.quest.place;

import java.util.ArrayList;
import java.util.List;

public class Checkpoints {
    private List<Checkpoint> checkpoints;

    public Checkpoints() {
        checkpoints = new ArrayList<>();
    }

    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    public void setItems(List<Checkpoint> items) {
        this.checkpoints = items;
    }

    public void addCheckpoint(Checkpoint item) {
        checkpoints.add(item);
    }
}
