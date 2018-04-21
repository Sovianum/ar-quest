package edu.technopark.arquest.core.ar.collision;

import com.google.ar.core.Pose;
import edu.technopark.arquest.core.ar.collision.shape.Sphere;
import edu.technopark.arquest.core.ar.geom.Geom;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ColliderTest {
    float radius1 = 10;
    float radius2 = 20;

    Sphere sphere1;
    Sphere sphere2;

    Collider collider1;
    Collider collider2;

    @Before
    public void setUp() {
        sphere1 = new Sphere(radius1);
        sphere2 = new Sphere(radius2);

        collider1 = new Collider(sphere1);
        collider2 = new Collider(sphere2);
    }

    @Test
    public void testCollision() {
        collider1.setPosition(new Geom().apply(Pose.makeTranslation(100, 0, 0)));
        assertFalse(collider1.collide(collider2));

        collider1.setPosition(new Geom().apply(Pose.makeTranslation(20, 0, 0)));
        assertTrue(collider1.collide(collider2));
    }
}