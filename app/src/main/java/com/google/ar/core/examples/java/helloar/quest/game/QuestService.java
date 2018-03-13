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
        final InteractiveObject rose = new InteractiveObject(2, "rose", "rose", false);
        final InteractiveObject banana = new InteractiveObject(3, "banana", "banana", false);

        root.setDrawable(new TextureDrawable("andy.obj", "andy.png"));
        root.getGeom().apply(Pose.makeTranslation(0, 0, -1f));
        root.setCollider(new Collider(new Sphere(0.3f)));
        root.setAction(new Action() {
            @Override
            public Collection<InteractionResult> act(InteractionArgument argument) {
                rose.setEnabled(true);
                return Utils.singleItemCollection(new InteractionResult(
                        InteractionResult.Type.MESSAGE,
                        "You interacted andy; now rose is available"
                ));
            }
        });

        rose.getIdentifiable().setParentID(1);
        rose.setDrawable(new TextureDrawable("rose.obj", "rose.jpg"));
        rose.getGeom().apply(Pose.makeTranslation(0.5f, 0, 0)).setScale(0.003f);
        rose.setCollider(new Collider(new Sphere(0.3f)));
        rose.setAction(new Action() {
            @Override
            public Collection<InteractionResult> act(InteractionArgument argument) {
                banana.setEnabled(true);
                return Utils.singleItemCollection(new InteractionResult(
                        InteractionResult.Type.MESSAGE,
                        "You interacted rose; now banana is available"
                ));
            }
        });

        banana.getIdentifiable().setParentID(1);
        banana.setDrawable(new TextureDrawable("banana.obj", "banana.jpg"));
        banana.getGeom().apply(Pose.makeTranslation(0, 0, 0.5f)).setScale(0.001f);
        banana.setCollider(new Collider(new Sphere(0.3f)));
        banana.setAction(new Action() {
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
        objects.add(rose);
        objects.add(banana);
        place.loadInteractiveObjects(objects);
        return place;
    }

    Place getPlace(int questID, int placeID) {
        return null;
    }
}
