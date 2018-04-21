package edu.technopark.arquest.game;

import com.viro.core.Object3D;

public class Identifiable3D extends Object3D implements IEnabled {
    protected boolean isEnabled;
    protected int id;

    public Identifiable3D(String name) {
        this(0, name, true);
    }

    public Identifiable3D(int id, String name, boolean isEnabled) {
        super();
        this.id = id;
        this.isEnabled = isEnabled;
        setName(name);
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
