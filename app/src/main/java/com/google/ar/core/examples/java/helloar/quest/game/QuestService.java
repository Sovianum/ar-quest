package com.google.ar.core.examples.java.helloar.quest.game;

import com.google.ar.core.Pose;
import com.google.ar.core.examples.java.helloar.core.ar.collision.Collider;
import com.google.ar.core.examples.java.helloar.core.ar.collision.shape.Sphere;
import com.google.ar.core.examples.java.helloar.core.ar.drawable.TextureDrawable;
import com.google.ar.core.examples.java.helloar.core.game.Action;
import com.google.ar.core.examples.java.helloar.core.game.InteractionArgument;
import com.google.ar.core.examples.java.helloar.core.game.InteractionResult;
import com.google.ar.core.examples.java.helloar.core.game.InteractiveObject;
import com.google.ar.core.examples.java.helloar.core.game.Item;
import com.google.ar.core.examples.java.helloar.core.game.ItemlessAction;
import com.google.ar.core.examples.java.helloar.core.game.Place;
import com.google.ar.core.examples.java.helloar.core.game.Utils;
import com.google.ar.core.examples.java.helloar.core.game.slot.Slot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QuestService {
    public static Place getDemoPlace() {
        InteractiveObject root = new InteractiveObject(1, "andy", "andy", true);
        final InteractiveObject rose = new InteractiveObject(2, "rose", "rose", false);
        final InteractiveObject banana = new InteractiveObject(3, "banana", "banana", false);

        root.setDrawable(new TextureDrawable("andy.obj", "andy.png"));
        root.getGeom().apply(Pose.makeTranslation(0, 0, -0.5f));
        root.setCollider(new Collider(new Sphere(0.3f)));
        root.setAction(new Action() {
            private final Item toy = new Item(10, "toy", "toy", "bigmax.obj", "bigmax.jpg");

            @Override
            public Collection<InteractionResult> act(InteractionArgument argument) {
                rose.setEnabled(true);
                Collection<InteractionResult> results = new ArrayList<>();
                results.add(
                        new InteractionResult(
                                InteractionResult.Type.MESSAGE,
                                "You interacted andy; now rose is available"
                        )
                );
                results.add(
                        new InteractionResult(
                                InteractionResult.Type.NEW_ITEMS,
                                "Andy presented you a toy",
                                new Slot.RepeatedItem(toy)
                        )
                );
                return results;
            }

            @Override
            public Collection<Item> getItems() {
                toy.getGeom().setScale(0.001f);
                return Utils.singleItemCollection(toy);
            }
        });

        rose.getIdentifiable().setParentID(1);
        rose.setDrawable(new TextureDrawable("rose.obj", "rose.jpg"));
        rose.getGeom().apply(Pose.makeTranslation(0.5f, 0, 0)).setScale(0.003f);
        rose.setCollider(new Collider(new Sphere(0.3f)));
        rose.setAction(new ItemlessAction() {
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
        banana.setAction(new ItemlessAction() {
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
}
