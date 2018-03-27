package technopark.diploma.arquest.core.ar.collision;

import com.google.ar.core.Pose;
import technopark.diploma.arquest.core.ar.collision.shape.Empty;
import technopark.diploma.arquest.core.ar.collision.shape.Point;
import technopark.diploma.arquest.core.ar.collision.shape.Shape;
import technopark.diploma.arquest.core.ar.collision.shape.Sphere;
import technopark.diploma.arquest.core.ar.enabled.Enabled;
import technopark.diploma.arquest.core.ar.geom.Geom;

import java.util.Collection;

public class Collider extends Enabled {
    public static Collider EMPTY = new Collider(new Empty());

    private static int POINT_CNT = 20;

    private static boolean collideDefault(Collider collider1, Collider collider2) {
        Collection<Pose> points1 = collider1.shape.generateBoundaryPoints(POINT_CNT);
        for (Pose point : points1) {
            Pose localPoint = collider2.toLocals(collider1.toGlobals(point));
            if (collider2.shape.contains(localPoint)) {
                return true;
            }
        }
        return false;
    }

    private static boolean collideSpheres(Collider collider1, Collider collider2) {
        Pose pose1 = collider1.position.getPose().extractTranslation().compose(collider1.offset.getPose().extractTranslation());
        Pose pose2 = collider2.position.getPose().extractTranslation().compose(collider2.offset.getPose().extractTranslation());

        Sphere sphere1 = (Sphere) collider1.shape;
        Sphere sphere2 = (Sphere) collider2.shape;

        float dx = pose1.tx() - pose2.tx();
        float dy = pose1.ty() - pose2.ty();
        float dz = pose1.tz() - pose2.tz();

        float r1 = sphere1.getRadius();
        float r2 = sphere2.getRadius();

        return (r1 + r2)*(r1 + r2) >= dx*dx + dy*dy + dz*dz;
    }

    private Shape shape;
    private Geom position;
    private Geom offset;

    public Collider(Shape shape, Geom position, Geom offset) {
        this.shape = shape;
        this.position = position;
        this.offset = offset;
    }

    public Collider(Shape shape, Geom offset) {
        this.shape = shape;
        this.offset = offset;
        this.position = new Geom();
    }

    public Collider(Shape shape) {
        this.shape = shape;
        this.offset = new Geom();
        this.position = new Geom();
    }

    public Collider() {
        this.shape = new Point();
        this.offset = new Geom();
        this.offset = new Geom();
    }

    public Shape getShape() {
        return shape;
    }

    public boolean collide(Collider another) {
        if (shape instanceof Empty || another.shape instanceof Empty) {
            return false;
        } else if (shape instanceof Sphere && another.shape instanceof Sphere) {
            return collideSpheres(this, another);
        } else if (shape instanceof Point) {
            return collideDefault(this, another);
        } else if (another.shape instanceof Point) {
            return collideDefault(another, this);
        }
        return collideDefault(this, another) || collideDefault(another, this);
    }

    public Geom getPosition() {
        return position;
    }

    public void setPosition(Geom position) {
        this.position = position;
    }

    private Pose toLocals(Pose global) {
        return global.compose(position.getPose().compose(offset.getPose()).inverse());
    }

    private Pose toGlobals(Pose local) {
        return local.compose(position.getPose()).compose(offset.getPose());
    }
}
