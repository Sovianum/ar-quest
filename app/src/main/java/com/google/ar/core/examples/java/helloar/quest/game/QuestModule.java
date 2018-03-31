package com.google.ar.core.examples.java.helloar.quest.game;

import android.content.Context;

import com.google.ar.core.Pose;
import com.google.ar.core.examples.java.helloar.App;
import com.google.ar.core.examples.java.helloar.GameModule;
import com.google.ar.core.examples.java.helloar.R;
import com.google.ar.core.examples.java.helloar.common.CollectionUtils;
import com.google.ar.core.examples.java.helloar.core.ar.collision.Collider;
import com.google.ar.core.examples.java.helloar.core.ar.collision.shape.Shape;
import com.google.ar.core.examples.java.helloar.core.ar.collision.shape.Sphere;
import com.google.ar.core.examples.java.helloar.core.ar.drawable.IDrawable;
import com.google.ar.core.examples.java.helloar.core.ar.drawable.TextureDrawable;
import com.google.ar.core.examples.java.helloar.core.game.Action;
import com.google.ar.core.examples.java.helloar.core.game.InteractionArgument;
import com.google.ar.core.examples.java.helloar.core.game.InteractionResult;
import com.google.ar.core.examples.java.helloar.core.game.InteractiveObject;
import com.google.ar.core.examples.java.helloar.core.game.Item;
import com.google.ar.core.examples.java.helloar.core.game.ItemlessAction;
import com.google.ar.core.examples.java.helloar.core.game.Place;
import com.google.ar.core.examples.java.helloar.core.game.script.ActionCondition;
import com.google.ar.core.examples.java.helloar.core.game.script.ObjectState;
import com.google.ar.core.examples.java.helloar.core.game.script.ScriptAction;
import com.google.ar.core.examples.java.helloar.core.game.slot.Slot;
import com.google.ar.core.examples.java.helloar.model.Quest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class QuestModule {
    @Inject
    GameModule gameModule;

    @Inject
    Context context;

    @Provides
    @Singleton
    public QuestModule provideQuestModule() {
        App.getAppComponent().inject(this);
        return this;
    }

    public List<Quest> getQuests() {
        Quest q1 = new Quest(
                2,
                "Демо-квест взаимодействие с персонажами",
                "Это демонстрационный квест из одного места." +
                        "Здесь вы можете опробовать взаимодействие с виртуальным объектами", 3
        );
        q1.addPlace(getAppearanceDemoPlace());

        Quest q2 = new Quest(
                1,
                "Демо-квест инвентарь + загрузка из скрипта",
                "Это демонстрационный квест из одного места, загружаемый из сценария." +
                        "Здесь вы можете опробовать работу с инвентарем.", 3
        );
//        q2.addPlace(getNewStyleInteractionDemoPlaceFromScript());
        q2.addPlace(getNewStyleInteractionDemoPlace());

        List<Quest> result = CollectionUtils.listOf(q1, q2);
        result.sort(new Comparator<Quest>() {
            @Override
            public int compare(Quest o1, Quest o2) {
                return o1.getId() - o2.getId();
            }
        });
        return result;
    }

    public Place getNewStyleInteractionDemoPlaceFromScript() {
        InputStream in;
        try {
            in = context.getAssets().open("scripts/inter_place.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Reader reader = new InputStreamReader(in);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(
                Shape.class,
                new JsonDeserializer<Shape>() {
                    @Override
                    public Shape deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return new Sphere(
                                json.getAsJsonObject().get("radius").getAsFloat()
                        );
                    }
                }
        );
        gsonBuilder.registerTypeAdapter(
                IDrawable.class,
                new JsonDeserializer<IDrawable>() {
                    @Override
                    public IDrawable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        JsonObject jsonObject = json.getAsJsonObject();
                        return new TextureDrawable(
                                jsonObject.get("modelName").getAsString(),
                                jsonObject.get("textureName").getAsString()
                        );
                    }
                }
        );
        gsonBuilder.registerTypeAdapter(
                Item.class,
                new JsonDeserializer<Item>() {
                    @Override
                    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        JsonObject jsonObject = json.getAsJsonObject();
                        Item item = new Item(
                                jsonObject.get("id").getAsInt(),
                                jsonObject.get("name").getAsString(),
                                jsonObject.get("description").getAsString(),
                                jsonObject.get("modelName").getAsString(),
                                jsonObject.get("textureName").getAsString()
                        );
                        item.getGeom().setScale(jsonObject.get("geom").getAsJsonObject().get("scale").getAsFloat());
                        return item;
                    }
                }
        );

        Gson gson = gsonBuilder.create();
        Place place = gson.fromJson(reader, Place.class);

        for (Map.Entry<Integer, InteractiveObject> entry : place.getInteractiveObjects().entrySet()) {
            InteractiveObject obj = entry.getValue();
            obj.setAction(obj.getActionFromStates());
        }

        return place;
    }

    public Place getNewStyleInteractionDemoPlace() {
        final Item rose = new Item(20, "rose", "rose","rose.obj", "rose.jpg");
        rose.getGeom().setScale(0.001f);
        final Item banana = new Item(10, "banana", "banana","banana.obj", "banana.jpg");
        banana.getGeom().setScale(0.001f);

        InteractiveObject andy = new InteractiveObject(
                1, "andy", "andy",
                CollectionUtils.singleItemList(rose)
        );
        andy.setDrawable(new TextureDrawable("andy.obj", "andy.png"));
        andy.getGeom().apply(Pose.makeTranslation(0, 0, -0.5f));
        andy.setCollider(new Collider(new Sphere(0.3f)));

        ObjectState andyState1 = new ObjectState(1, true);
        andyState1.setActions(CollectionUtils.listOf(
                new ScriptAction(
                        1,
                        CollectionUtils.listOf(
                                InteractionResult.journalRecordResult("Андроид сказал: Возьми поесть у белого человека"),
                                InteractionResult.nextPurposeResult("Найдите неподалеку белого человека и попросите поесть"),
                                InteractionResult.transitionsResult(
                                        CollectionUtils.listOf(
                                                new ScriptAction.StateTransition(1, 2),
                                                new ScriptAction.StateTransition(2, 1)
                                        )
                                ),
                                InteractionResult.hintResult(R.id.journal_btn_hint)
                        )
                )
        ));
        andyState1.setConditions(ActionCondition.makeConditionMap(
                CollectionUtils.singleItemList(1),
                CollectionUtils.singleItemList(
                        new ActionCondition(1)
                )
        ));

        ObjectState andyState2 = new ObjectState(2, false);
        andyState2.setActions(CollectionUtils.listOf(
                new ScriptAction(
                        1,
                        CollectionUtils.listOf(
                                InteractionResult.journalRecordResult("Отблагодари белого человека"),
                                InteractionResult.nextPurposeResult("Передайте розу белому человеку"),
                                InteractionResult.newItemsResult(new Slot.RepeatedItem(rose)),
                                InteractionResult.takeItemsResult(new Slot.RepeatedItem(banana)),
                                InteractionResult.transitionsResult(
                                        CollectionUtils.listOf(
                                                new ScriptAction.StateTransition(1, 3)
                                        )
                                )
                        )
                )
        ));
        andyState2.setConditions(ActionCondition.makeConditionMap(
                CollectionUtils.singleItemList(1),
                CollectionUtils.singleItemList(
                        new ActionCondition(
                                CollectionUtils.singleItemList(
                                        new ActionCondition.ItemInfo(banana.getId(), 1)
                                ),
                                2
                        )
                )
        ));

        ObjectState andyState3 = new ObjectState(3, false);
        andyState3.setActions(CollectionUtils.listOf(
                new ScriptAction(
                        1,
                        CollectionUtils.listOf(
                                InteractionResult.messageResult("Мне нечего тебе сказать")
                        )
                )
        ));
        andyState3.setConditions(ActionCondition.makeConditionMap(
                CollectionUtils.singleItemList(1),
                CollectionUtils.singleItemList(new ActionCondition(3))
        ));

        andy.setStates(CollectionUtils.listOf(andyState1, andyState2, andyState3));


        final InteractiveObject whiteGuy = new InteractiveObject(
                2, "white", "white",
                CollectionUtils.singleItemList(banana)
        );
        whiteGuy.setDrawable(new TextureDrawable("bigmax.obj", "bigmax.jpg"));
        whiteGuy.getGeom().apply(Pose.makeTranslation(0.25f, 0, 0f)).setScale(0.003f);
        whiteGuy.setCollider(new Collider(new Sphere(0.3f)));
        whiteGuy.setStates(CollectionUtils.listOf(ObjectState.enableObjectState(0, true, false)));

        ObjectState guyState0 = new ObjectState(0, true);
        guyState0.setEnabled(false);

        ObjectState guyState1 = new ObjectState(1, false);
        guyState1.setActions(CollectionUtils.listOf(
                new ScriptAction(
                        1,
                        CollectionUtils.listOf(
                                InteractionResult.newItemsResult(new Slot.RepeatedItem(banana)),
                                InteractionResult.journalRecordResult("Дай ему поесть"),
                                InteractionResult.nextPurposeResult("Передайте банан андроиду"),
                                InteractionResult.hintResult(R.id.inventory_btn_hint),
                                InteractionResult.transitionsResult(CollectionUtils.listOf(
                                        new ScriptAction.StateTransition(2, 2)
                                ))
                        )
                )
        ));
        guyState1.setConditions(ActionCondition.makeConditionMap(
                CollectionUtils.listOf(1),
                CollectionUtils.listOf(new ActionCondition(1))
        ));

        ObjectState guyState2= new ObjectState(2, false);
        guyState2.setActions(CollectionUtils.listOf(
                new ScriptAction(
                        1,
                        CollectionUtils.listOf(
                                InteractionResult.journalRecordResult("Да за кого он меня принимает?!"),
                                InteractionResult.nextPurposeResult("Квест окончен. Пожете еще поговорить с виртуалами"),
                                InteractionResult.transitionsResult(CollectionUtils.listOf(
                                        new ScriptAction.StateTransition(2, 3)
                                ))
                        )
                )
        ));
        guyState2.setConditions(ActionCondition.makeConditionMap(
                CollectionUtils.listOf(1),
                CollectionUtils.listOf(new ActionCondition(
                        CollectionUtils.listOf(
                                new ActionCondition.ItemInfo(rose.getId(), 1)
                        ),
                        2
                ))
        ));

        ObjectState guyState3 = new ObjectState(3, false);
        guyState3.setActions(CollectionUtils.listOf(
                new ScriptAction(
                        1,
                        CollectionUtils.listOf(
                                InteractionResult.messageResult("Ммм?")
                        )
                )
        ));
        guyState3.setConditions(ActionCondition.makeConditionMap(
                CollectionUtils.listOf(1),
                CollectionUtils.listOf(new ActionCondition(3))
        ));

        whiteGuy.setStates(CollectionUtils.listOf(guyState0, guyState1, guyState2, guyState3));

        andy.setAction(andy.getActionFromStates());
        whiteGuy.setAction(whiteGuy.getActionFromStates());
        Place place = new Place();
        place.loadInteractiveObjects(CollectionUtils.listOf(andy, whiteGuy));

        return place;
    }

    public Place getInteractionDemoPlace() {
        final Item rose = new Item(20, "rose", "rose","rose.obj", "rose.jpg");
        final Item banana = new Item(10, "banana", "banana","banana.obj", "banana.jpg");

        InteractiveObject andy = new InteractiveObject(
                1, "andy", "andy",
                CollectionUtils.singleItemList(rose)
        );
        andy.setDrawable(new TextureDrawable("andy.obj", "andy.png"));
        andy.getGeom().apply(Pose.makeTranslation(0, 0, 0f));
        andy.setCollider(new Collider(new Sphere(0.3f)));
        andy.setStates(CollectionUtils.listOf(ObjectState.enableObjectState(0, true, true)));

        final InteractiveObject whiteGuy = new InteractiveObject(
                2, "white", "white",
                CollectionUtils.singleItemList(banana)
        );
        whiteGuy.setDrawable(new TextureDrawable("bigmax.obj", "bigmax.jpg"));
        whiteGuy.getGeom().apply(Pose.makeTranslation(0.25f, 0, 0f)).setScale(0.003f);
        whiteGuy.setCollider(new Collider(new Sphere(0.3f)));
        whiteGuy.setStates(CollectionUtils.listOf(ObjectState.enableObjectState(0, true, false)));

        andy.setAction(new Action() {
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
                            gameModule.getCurrentInventory().removeAll(10);
                            return resultTransitions.get(cnt++);
                        }
                    }
                    return CollectionUtils.singleItemList(InteractionResult.messageResult("Где еда?"));
                }
                return CollectionUtils.singleItemList(InteractionResult.messageResult("Мне нечего тебе сказать"));
            }

            private List<Collection<InteractionResult>> getResultTransitions() {
                List<Collection<InteractionResult>> result = new ArrayList<>();

                Collection<InteractionResult> transition1 = new ArrayList<>();
                transition1.add(
                        InteractionResult.messageResult(
                                "Возьми поесть у белого человека"
                        )
                );
                result.add(transition1);

                Collection<InteractionResult> transition2 = new ArrayList<>();
                transition2.add(
                        InteractionResult.messageResult(
                                "Отблагодари белого человека"
                        )
                );
                transition2.add(
                        InteractionResult.newItemsResult(
                                new Slot.RepeatedItem(rose)
                        )
                );
                result.add(transition2);

                return result;
            }
        });

        whiteGuy.setAction(new Action() {
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
                            gameModule.getCurrentInventory().removeAll(20);
                            return transitions.get(cnt++);
                        }
                    }
                }
                return CollectionUtils.singleItemList(InteractionResult.messageResult("Ммм?"));
            }

            private List<Collection<InteractionResult>> getResultTransitions() {
                List<Collection<InteractionResult>> result = new ArrayList<>();

                Collection<InteractionResult> transition1 = new ArrayList<>();
                transition1.add(
                        InteractionResult.messageResult(
                                "Дай ему поесть"
                        )
                );
                transition1.add(
                        InteractionResult.newItemsResult(
                                new Slot.RepeatedItem(banana)
                        )
                );
                result.add(transition1);

                Collection<InteractionResult> transition2 = new ArrayList<>();
                transition2.add(
                        InteractionResult.messageResult(
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

    public Place getAppearanceDemoPlace() {
        InteractiveObject andy = new InteractiveObject(
                1, "andy", "andy"

        );
        andy.setDrawable(new TextureDrawable("andy.obj", "andy.png"));
        andy.getGeom().apply(Pose.makeTranslation(0, 0, 0f));
        andy.setCollider(new Collider(new Sphere(0.3f)));
        andy.setStates(CollectionUtils.listOf(ObjectState.enableObjectState(0, true, true)));

        final InteractiveObject rose = new InteractiveObject(
                2, "rose", "rose"
        );
        rose.setStates(CollectionUtils.listOf(ObjectState.enableObjectState(0, true, false)));

        final InteractiveObject banana = new InteractiveObject(
                3, "banana", "banana"
        );
        banana.setStates(CollectionUtils.listOf(ObjectState.enableObjectState(0, true, false)));


        andy.setAction(new Action() {
            private final Item toy = new Item(10, "toy", "toy", "bigmax.obj", "bigmax.jpg");

            @Override
            public Collection<InteractionResult> act(InteractionArgument argument) {
                rose.setEnabled(true);
                Collection<InteractionResult> results = new ArrayList<>();
                results.add(
                        InteractionResult.messageResult(
                                "You interacted andy; now rose is available"
                        )
                );
                results.add(
                        InteractionResult.newItemsResult(
                                new Slot.RepeatedItem(toy)
                        )
                );
                return results;
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
                return CollectionUtils.singleItemList(InteractionResult.messageResult(
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
                return CollectionUtils.singleItemList(InteractionResult.messageResult(
                        "You interacted banana"
                ));
            }
        });

        Place place = new Place();
        List<InteractiveObject> objects = new ArrayList<>();
        objects.add(andy);
        objects.add(rose);
        objects.add(banana);
        place.loadInteractiveObjects(objects);
        return place;
    }
}
