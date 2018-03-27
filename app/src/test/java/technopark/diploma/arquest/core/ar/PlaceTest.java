package technopark.diploma.arquest.core.ar;


import com.google.ar.core.Pose;
import technopark.diploma.arquest.common.CollectionUtils;
import technopark.diploma.arquest.core.ar.collision.Collider;
import technopark.diploma.arquest.core.ar.collision.shape.Point;
import technopark.diploma.arquest.core.ar.collision.shape.Sphere;
import technopark.diploma.arquest.core.ar.geom.Geom;
import technopark.diploma.arquest.core.game.InteractiveObject;
import technopark.diploma.arquest.core.game.Place;
import technopark.diploma.arquest.core.game.script.ObjectState;

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
        InteractiveObject root = new InteractiveObject(
                1, "root", "root"
        );
        root.getGeom().apply(Pose.makeTranslation(0, 0, 5));
        root.setCollider(new Collider(new Point()));
        root.setStates(CollectionUtils.listOf(ObjectState.enableObjectState(0, true, true)));

        InteractiveObject child1 = new InteractiveObject(
                2, "child1", "child1"
        );
        child1.getGeom().apply(Pose.makeTranslation(0, 0, 10));
        child1.setCollider(new Collider(new Point()));
        child1.setStates(CollectionUtils.listOf(ObjectState.enableObjectState(0, true, true)));

        InteractiveObject child2 = new InteractiveObject(
                3, "child2", "child2"
        );
        child2.getGeom().apply(Pose.makeTranslation(0, 0, 15));
        child2.setCollider(new Collider(new Point()));
        child2.setStates(CollectionUtils.listOf(ObjectState.enableObjectState(0, true, true)));

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
        Collection<Integer> ids1 = objsToIds(scene.getCollisions(collider1));
        assertEquals(0, ids1.size());

        collider1.setPosition(new Geom().apply(Pose.makeTranslation(0, 0, 0)));
        Collection<Integer> ids2 = objsToIds(scene.getCollisions(collider1));
        assertEquals(1, ids2.size());

        Collider collider2 = new Collider(new Sphere(7.5f));
        collider2.setPosition(new Geom().apply(Pose.makeTranslation(5, 0, 5)));
        Collection<Integer> ids3 = objsToIds(scene.getCollisions(collider2));
        assertEquals(2, ids3.size());

        Collider collider3 = new Collider(new Sphere(100f));
        Collection<Integer> ids4 = objsToIds(scene.getCollisions(collider3));
        assertEquals(3, ids4.size());
    }

    private Collection<Integer> objsToIds(Collection<SceneObject> objects) {
        Collection<Integer> result = new ArrayList<>(objects.size());
        for (SceneObject object : objects) {
            result.add(object.getIdentifiable().getId());
        }
        return result;
    }
}