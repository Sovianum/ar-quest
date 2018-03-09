package com.google.ar.core.examples.java.helloar.core.ar;


import com.google.ar.core.Pose;
import com.google.ar.core.examples.java.helloar.core.ar.collision.Collider;
import com.google.ar.core.examples.java.helloar.core.ar.collision.shape.Sphere;
import com.google.ar.core.examples.java.helloar.core.ar.record.ObjectRecord;
import com.google.ar.core.examples.java.helloar.core.ar.record.PoseRecord;
import com.google.ar.core.examples.java.helloar.core.ar.record.Rotation;
import com.google.ar.core.examples.java.helloar.core.ar.record.SceneRecord;
import com.google.ar.core.examples.java.helloar.core.ar.record.Translation;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class SceneTest {
    private SceneRecord sceneRecord;
    private Scene scene;

    @Before
    public void setUp() {
        ObjectRecord root = new ObjectRecord();
        root.setId(1);
        root.setParentId(0);
        root.setName("andy");
        root.setPoseRecord(new PoseRecord(
                new Translation(0, 0, 0),
                Rotation.Identity()
        ));

        ObjectRecord child1 = new ObjectRecord();
        child1.setId(2);
        child1.setParentId(1);
        child1.setName("rose");
        child1.setPoseRecord(new PoseRecord(
                new Translation(10, 0, 0),
                Rotation.Identity()
        ));

        ObjectRecord child2 = new ObjectRecord();
        child2.setId(3);
        child2.setParentId(1);
        child2.setName("banana");
        child2.setPoseRecord(new PoseRecord(
                new Translation(20, 0, 1),
                Rotation.Identity()
        ));

        List<ObjectRecord> records = new ArrayList<>();
        records.add(root);
        records.add(child1);
        records.add(child2);
        sceneRecord = new SceneRecord(records);

        scene = new Scene();
        scene.load(sceneRecord);
    }

    @Test
    public void testUpdate() {
        scene.updateColliders();

        Collider collider1 = new Collider(new Sphere(5));

        collider1.setPosition(Pose.makeTranslation(-10, 0, 0));
        Collection<Integer> ids1 = scene.getCollisions(collider1);
        assertEquals(0, ids1.size());

        collider1.setPosition(Pose.makeTranslation(0, 0, 0));
        Collection<Integer> ids2 = scene.getCollisions(collider1);
        assertEquals(1, ids2.size());

        Collider collider2 = new Collider(new Sphere(7.5f));
        collider2.setPosition(Pose.makeTranslation(5, 0, 5));
        Collection<Integer> ids3 = scene.getCollisions(collider2);
        assertEquals(2, ids3.size());

        Collider collider3 = new Collider(new Sphere(100f));
        Collection<Integer> ids4 = scene.getCollisions(collider3);
        assertEquals(3, ids4.size());
    }
}