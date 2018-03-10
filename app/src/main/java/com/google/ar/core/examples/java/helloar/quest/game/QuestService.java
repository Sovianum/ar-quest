package com.google.ar.core.examples.java.helloar.quest.game;

import com.google.ar.core.Pose;
import com.google.ar.core.examples.java.helloar.core.ar.drawable.TextureDrawable;
import com.google.ar.core.examples.java.helloar.core.game.InteractiveObject;
import com.google.ar.core.examples.java.helloar.core.game.Place;

import java.util.ArrayList;
import java.util.List;

public class QuestService {
    public static Place getDemoPlace() {
        InteractiveObject root = new InteractiveObject(1, "root", "root", true);
        root.setDrawable(new TextureDrawable("andy.obj", "andy.png"));
        root.getGeom().apply(Pose.makeTranslation(0, 0, -0.5f));

        InteractiveObject child1 = new InteractiveObject(2, "child1", "child1", true);
        child1.getIdentifiable().setParentID(1);
        child1.setDrawable(new TextureDrawable("rose.obj", "rose.jpg"));
        child1.getGeom().apply(Pose.makeTranslation(0.25f, 0, 0)).setScale(0.003f);

        InteractiveObject child2 = new InteractiveObject(3, "child2", "child2", true);
        child2.getIdentifiable().setParentID(1);
        child2.setDrawable(new TextureDrawable("banana.obj", "banana.jpg"));
        child2.getGeom().apply(Pose.makeTranslation(0, 0, 0.25f)).setScale(0.001f);

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
