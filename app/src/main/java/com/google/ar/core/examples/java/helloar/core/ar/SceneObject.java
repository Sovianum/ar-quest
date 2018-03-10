package com.google.ar.core.examples.java.helloar.core.ar;

import com.google.ar.core.examples.java.helloar.core.ar.collision.Collider;
import com.google.ar.core.examples.java.helloar.core.ar.drawable.IDrawable;
import com.google.ar.core.examples.java.helloar.core.ar.enabled.Enabled;
import com.google.ar.core.examples.java.helloar.core.ar.geom.Geom;
import com.google.ar.core.examples.java.helloar.core.ar.identifiable.Identifiable;

public class SceneObject extends Enabled {
    private Collider collider;
    private IDrawable drawable;
    private Geom geom;
    private Identifiable identifiable;

    public SceneObject() {
        geom = new Geom();
    }

    public Collider getCollider() {
        return collider;
    }

    public void setCollider(Collider collider) {
        this.collider = collider;
        this.collider.setPosition(this.geom);
    }

    public IDrawable getDrawable() {
        return drawable;
    }

    public void setDrawable(IDrawable drawable) {
        this.drawable = drawable;
    }

    public Geom getGeom() {
        return geom;
    }

    public Identifiable getIdentifiable() {
        return identifiable;
    }

    public void setIdentifiable(Identifiable identifiable) {
        this.identifiable = identifiable;
    }
}
