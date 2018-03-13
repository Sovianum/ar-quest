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
import com.google.ar.core.examples.java.helloar.network.Api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class QuestService {
    public static Place getInteractionDemoPlace() {
        InteractiveObject andy = new InteractiveObject(1, "andy", "andy", true);
        andy.setDrawable(new TextureDrawable("andy.obj", "andy.png"));
        andy.getGeom().apply(Pose.makeTranslation(0, 0, -0.5f));
        andy.setCollider(new Collider(new Sphere(0.3f)));

        final InteractiveObject whiteGuy = new InteractiveObject(2, "white", "white", false);
        whiteGuy.setDrawable(new TextureDrawable("bigmax.obj", "bigmax.jpg"));
        whiteGuy.getGeom().apply(Pose.makeTranslation(2.5f, 0, -0.5f)).setScale(0.003f);
        whiteGuy.setCollider(new Collider(new Sphere(0.3f)));

        andy.setAction(new Action() {
            private final Item rose = new Item(20, "rose", "rose","rose.obj", "rose.jpg");
            private final List<Collection<InteractionResult>> resultTransitions = getResultTransitions();
            private int cnt = 0;

            @Override
            public Collection<InteractionResult> act(InteractionArgument argument) {
                rose.getGeom().setScale(0.001f);
                if (cnt == 0) {
                    whiteGuy.setEnabled(true);
                    return resultTransitions.get(cnt++);
                }
                if (cnt == 1) {
                    Collection<Slot.RepeatedItem> items = argument.getItems();
                    for (Slot.RepeatedItem item : items) {
                        Item innerItem = item.getItem();
                        if (innerItem != null && Objects.equals(innerItem.getName(), "banana")) {
                            item.getItem().setEnabled(false);
                            Api.getInventories().getCurrentInventory().remove(10);
                            return resultTransitions.get(cnt++);
                        }
                    }
                }
                return Utils.singleItemCollection(new InteractionResult(InteractionResult.Type.MESSAGE, "Мне нечего тебе сказать"));
            }

            @Override
            public Collection<Item> getItems() {
                return Utils.singleItemCollection(rose);
            }

            private List<Collection<InteractionResult>> getResultTransitions() {
                List<Collection<InteractionResult>> result = new ArrayList<>();

                Collection<InteractionResult> transition1 = new ArrayList<>();
                transition1.add(
                        new InteractionResult(
                                InteractionResult.Type.MESSAGE,
                                "Возьми поесть у белого человека"
                        )
                );
                result.add(transition1);

                Collection<InteractionResult> transition2 = new ArrayList<>();
                transition2.add(
                        new InteractionResult(
                                InteractionResult.Type.MESSAGE,
                                "Отблагодари белого человека"
                        )
                );
                transition2.add(
                        new InteractionResult(
                                InteractionResult.Type.NEW_ITEMS,
                                new Slot.RepeatedItem(rose)
                        )
                );
                result.add(transition2);

                return result;
            }
        });

        whiteGuy.setAction(new Action() {
            private final Item banana = new Item(10, "banana", "banana","banana.obj", "banana.jpg");
            private final List<Collection<InteractionResult>> transitions = getResultTransitions();
            private int cnt = 0;

            @Override
            public Collection<InteractionResult> act(InteractionArgument argument) {
                banana.getGeom().setScale(0.001f);
                if (cnt == 0) {
                    return transitions.get(cnt++);
                }
                if (cnt == 1) {
                    for (Slot.RepeatedItem item : argument.getItems()) {
                        if (item.getItem().getName().equals("rose")) {
                            item.getItem().setEnabled(false);
                            Api.getInventories().getCurrentInventory().remove(20);
                            return transitions.get(cnt++);
                        }
                    }
                }
                return Utils.singleItemCollection(new InteractionResult(InteractionResult.Type.MESSAGE, "Ммм?"));
            }

            @Override
            public Collection<Item> getItems() {
                return Utils.singleItemCollection(banana);
            }

            private List<Collection<InteractionResult>> getResultTransitions() {
                List<Collection<InteractionResult>> result = new ArrayList<>();

                Collection<InteractionResult> transition1 = new ArrayList<>();
                transition1.add(
                        new InteractionResult(
                                InteractionResult.Type.MESSAGE,
                                "Дай ему поесть"
                        )
                );
                transition1.add(
                        new InteractionResult(
                                InteractionResult.Type.NEW_ITEMS,
                                new Slot.RepeatedItem(banana)
                        )
                );
                result.add(transition1);

                Collection<InteractionResult> transition2 = new ArrayList<>();
                transition2.add(
                        new InteractionResult(
                                InteractionResult.Type.MESSAGE,
                                "Да за кого он меня принимает?!"
                        )
                );
                result.add(transition2);

                return result;
            }
        });

        Place place = new Place();
        List<InteractiveObject> objects = new ArrayList<>();
        objects.add(andy);
        objects.add(whiteGuy);
        place.loadInteractiveObjects(objects);
        return place;
    }

    public static Place getAppearenceDemoPlace() {
        InteractiveObject root = new InteractiveObject(1, "andy", "andy", true);
        final InteractiveObject rose = new InteractiveObject(2, "rose", "rose", false);
        final InteractiveObject banana = new InteractiveObject(3, "banana", "banana", false);


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
