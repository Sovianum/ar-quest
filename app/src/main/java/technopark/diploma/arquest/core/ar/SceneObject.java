package technopark.diploma.arquest.core.ar;

import technopark.diploma.arquest.core.ar.collision.Collider;
import technopark.diploma.arquest.core.ar.drawable.TextureDrawable;
import technopark.diploma.arquest.core.ar.enabled.Enabled;
import technopark.diploma.arquest.core.ar.geom.Geom;
import technopark.diploma.arquest.core.ar.identifiable.Identifiable;

public class SceneObject extends Enabled {
    private Collider collider;
    private TextureDrawable drawable;
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

    public TextureDrawable getDrawable() {
        return drawable;
    }

    public void setDrawable(TextureDrawable drawable) {
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
