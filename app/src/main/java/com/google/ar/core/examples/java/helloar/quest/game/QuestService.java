package com.google.ar.core.examples.java.helloar.quest.game;

import com.google.ar.core.Pose;
import com.google.ar.core.examples.java.helloar.core.ar.collision.Collider;
import com.google.ar.core.examples.java.helloar.core.ar.collision.shape.Sphere;
import com.google.ar.core.examples.java.helloar.core.ar.drawable.TextureDrawable;
import com.google.ar.core.examples.java.helloar.core.game.Action;
import com.google.ar.core.examples.java.helloar.core.game.InteractionArgument;
import com.google.ar.core.examples.java.helloar.core.game.InteractionResult;
import com.google.ar.core.examples.java.helloar.core.game.InteractiveObject;
import com.google.ar.core.examples.java.helloar.core.game.Place;
import com.google.ar.core.examples.java.helloar.core.game.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QuestService {
    public static Place getDemoPlace() {
        InteractiveObject root = new InteractiveObject(1, "andy", "andy", true);
        root.setDrawable(new TextureDrawable("andy.obj", "andy.png"));
        root.getGeom().apply(Pose.makeTranslation(0, 0, -1f));
        root.setCollider(new Collider(new Sphere(0.1f)));
        root.setAction(new Action() {
            @Override
            public Collection<InteractionResult> act(InteractionArgument argument) {
                return Utils.singleItemCollection(new InteractionResult(
                        InteractionResult.Type.MESSAGE,
                        "You interacted andy"
                ));
            }
        });

        InteractiveObject child1 = new InteractiveObject(2, "rose", "rose", true);
        child1.getIdentifiable().setParentID(1);
        child1.setDrawable(new TextureDrawable("rose.obj", "rose.jpg"));
        child1.getGeom().apply(Pose.makeTranslation(0.5f, 0, 0)).setScale(0.003f);
        child1.setCollider(new Collider(new Sphere(0.1f)));
        child1.setAction(new Action() {
            @Override
            public Collection<InteractionResult> act(InteractionArgument argument) {
                return Utils.singleItemCollection(new InteractionResult(
                        InteractionResult.Type.MESSAGE,
                        "You interacted rose"
                ));
            }
        });


        InteractiveObject child2 = new InteractiveObject(3, "banana", "banana", true);
        child2.getIdentifiable().setParentID(1);
        child2.setDrawable(new TextureDrawable("banana.obj", "banana.jpg"));
        child2.getGeom().apply(Pose.makeTranslation(0, 0, 0.5f)).setScale(0.001f);
        child2.setCollider(new Collider(new Sphere(0.1f)));
        child2.setAction(new Action() {
            @Override
            public Collection<InteractionResult> act(InteractionArgument argument) {
                return Utils.singleItemCollection(new InteractionResult(
                        InteractionResult.Type.MESSAGE,
                        "You interacted banana"
                ));
            }
        });

        Place place = new Place();
        List<InteractiveObject> objects = new ArrayList<>();
        objects.add(root);
        objects.add(child1);
        objects.add(child2);
        place.loadInteractiveObjects(objects);
        return place;
    }

    Place getPlace(int questID, int placeID) {
        return null;
    }
}
