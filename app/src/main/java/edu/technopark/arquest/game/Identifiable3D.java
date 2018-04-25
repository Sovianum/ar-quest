package edu.technopark.arquest.game;

import com.viro.core.Object3D;
import com.viro.core.Vector;

import edu.technopark.arquest.model.VisualResource;

public class Identifiable3D extends Object3D implements IEnabled {
    protected int id;
    private boolean isEnabled;
    private VisualResource visualResource;

    public Identifiable3D(String name) {
        this(0, name, true);
    }

    public Identifiable3D(int id, String name, boolean isEnabled) {
        super();
        this.id = id;
        this.isEnabled = isEnabled;
        setName(name);
    }

    public VisualResource getVisualResource() {
        return visualResource;
    }

    public void setVisualResource(VisualResource visualResource) {
        this.visualResource = visualResource;
    }

    public void setUniformScale(float scale) {
        setScale(new Vector(scale, scale, scale));
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
