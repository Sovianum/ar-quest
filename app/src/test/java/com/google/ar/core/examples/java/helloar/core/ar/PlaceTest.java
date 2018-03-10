package com.google.ar.core.examples.java.helloar.core.ar;


import com.google.ar.core.Pose;
import com.google.ar.core.examples.java.helloar.core.ar.collision.Collider;
import com.google.ar.core.examples.java.helloar.core.ar.collision.shape.Point;
import com.google.ar.core.examples.java.helloar.core.ar.collision.shape.Sphere;
import com.google.ar.core.examples.java.helloar.core.ar.geom.Geom;
import com.google.ar.core.examples.java.helloar.core.game.InteractiveObject;
import com.google.ar.core.examples.java.helloar.core.game.Place;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class PlaceTest {
    private Scene scene;
    private Place place;

    @Before
    public void setUp() {
        InteractiveObject root = new InteractiveObject(1, "root", "root", true);
        root.getGeom().apply(Pose.makeTranslation(0, 0, 5));
        root.setCollider(new Collider(new Point()));

        InteractiveObject child1 = new InteractiveObject(2, "child1", "child1", true);
        child1.getGeom().apply(Pose.makeTranslation(0, 0, 10));
        child1.setCollider(new Collider(new Point()));

        InteractiveObject child2 = new InteractiveObject(3, "child2", "child2", true);
        child2.getGeom().apply(Pose.makeTranslation(0, 0, 15));
        child2.setCollider(new Collider(new Point()));

        place = new Place();
        List<InteractiveObject> objects = new ArrayList<>();
        objects.add(root);
        objects.add(child1);
        objects.add(child2);
        place.loadInteractiveObjects(objects);

        scene = new Scene();
        scene.load(place.getAll());
    }

    @Test
    public void testUpdate() {
        Collider collider1 = new Collider(new Sphere(5));

        collider1.setPosition(new Geom().apply(Pose.makeTranslation(-10, 0, 0)));
        Collection<Integer> ids1 = scene.getCollisions(collider1);
        assertEquals(0, ids1.size());

        collider1.setPosition(new Geom().apply(Pose.makeTranslation(0, 0, 0)));
        Collection<Integer> ids2 = scene.getCollisions(collider1);
        assertEquals(1, ids2.size());

        Collider collider2 = new Collider(new Sphere(7.5f));
        collider2.setPosition(new Geom().apply(Pose.makeTranslation(5, 0, 5)));
        Collection<Integer> ids3 = scene.getCollisions(collider2);
        assertEquals(2, ids3.size());

        Collider collider3 = new Collider(new Sphere(100f));
        Collection<Integer> ids4 = scene.getCollisions(collider3);
        assertEquals(3, ids4.size());
    }
}