package edu.technopark.arquest.quest.game;

import com.viro.core.Object3D;
import com.viro.core.PhysicsBody;
import com.viro.core.PhysicsShapeBox;
import com.viro.core.PhysicsShapeSphere;
import com.viro.core.Vector;

import java.util.Arrays;
import java.util.Collections;

import edu.technopark.arquest.game.InteractionResult;
import edu.technopark.arquest.game.InteractiveObject;
import edu.technopark.arquest.game.Item;
import edu.technopark.arquest.game.Place;
import edu.technopark.arquest.game.script.ActionCondition;
import edu.technopark.arquest.game.script.ObjectState;
import edu.technopark.arquest.game.script.ScriptAction;
import edu.technopark.arquest.game.slot.Slot;
import edu.technopark.arquest.model.VisualResource;

public class SkullPlaceConstructor {
    private static void setContainerStates(InteractiveObject container, Slot.RepeatedItem item) {
        ObjectState state1 = new ObjectState(1, true);
        state1.setVisible(true);
        state1.setCollidable(true);

        state1.setActions(
                Collections.singletonList(
                        new ScriptAction(
                                1,
                                Arrays.asList(
                                        InteractionResult.newItemsResult(item),
                                        InteractionResult.transitionsResult(
                                                Collections.singletonList(
                                                        new ScriptAction.StateTransition(container.getName(), 2)
                                                )
                                        )
                                )
                        )
                )
        );
        state1.setConditions(ActionCondition.makeConditionMap(
                Collections.singletonList(1),
                Collections.singletonList(
                        new ActionCondition(1)
                )
        ));

        ObjectState state2 = new ObjectState(2, false);
        state2.setVisible(false);
        state2.setCollidable(false);

        container.setStates(Arrays.asList(state1, state2));
        container.setAction(container.getActionFromStates());
    }

    private static void setAppearanceStates(InteractiveObject object) {
        ObjectState state1 = new ObjectState(1, true);
        state1.setVisible(false);
        state1.setCollidable(false);

        ObjectState state2 = new ObjectState(2, false);
        state2.setVisible(true);
        state2.setCollidable(false);

        object.setStates(Arrays.asList(state1, state2));
        object.setAction(object.getActionFromStates());
    }

    private float mainScale;
    private float smallScale;
    private String assetPrefix;

    private Item bottleItem;
    private InteractiveObject bottleContainer;
    private InteractiveObject bottleSmall;

    private Item glassItem;
    private InteractiveObject glassContainer;
    private InteractiveObject glassSmall;

    private Item helmetItem;
    private InteractiveObject helmetContainer;
    private InteractiveObject columnHelmet;

    private Item axeItem;
    private InteractiveObject axeContainer;

    private InteractiveObject column;
    private InteractiveObject map;
    private InteractiveObject skull;
    private InteractiveObject pig;

    public SkullPlaceConstructor(float mainScale, float smallScale, String assetPrefix) {
        this.mainScale = mainScale;
        this.smallScale = smallScale;
        this.assetPrefix = assetPrefix;
    }

    public Place getPlace() {
        createObjects();
        setBottleStates();
        setGlassStates();
        setHelmetStates();
        setAxeStates();
        setColumnStates();
        setMapStates();
        setPigStates();
        setSkullStates();

        Place place = new Place();
        place.loadInteractiveObjects(Arrays.asList(
                bottleContainer, bottleSmall,
                glassContainer, glassSmall,
                helmetContainer, columnHelmet,
                axeContainer, column, map, skull, pig
        ));
        place.setStartPurpose("Подойдите к свину и поговорите с ним по поводу карты");
        return place;
    }

    private void setBottleStates() {
        setContainerStates(bottleContainer, new Slot.RepeatedItem(bottleItem));
        setAppearanceStates(bottleSmall);
    }

    private void setGlassStates() {
        setContainerStates(glassContainer, new Slot.RepeatedItem(glassItem));
        setAppearanceStates(glassSmall);
    }

    private void setHelmetStates() {
        setContainerStates(helmetContainer, new Slot.RepeatedItem(helmetItem));
        setAppearanceStates(columnHelmet);
    }

    private void setAxeStates() {
        setContainerStates(axeContainer, new Slot.RepeatedItem(axeItem));
    }

    private void setColumnStates() {
        ObjectState columnState1 = new ObjectState(1, true);
        columnState1.setVisible(true);
        columnState1.setCollidable(false);
        column.setStates(Collections.singletonList(columnState1));
        column.setAction(column.getActionFromStates());
    }

    private void setMapStates() {
        setAppearanceStates(map);
    }

    private void setPigStates() {
        ObjectState state1 = new ObjectState(1, true);
        state1.setActions(Collections.singletonList(
                new ScriptAction(
                        1,
                        Arrays.asList(
                                InteractionResult.journalRecordResult("Свин сказал: Отдал бы, но у меня ее уже забрала эта черепушка. Посмотри поблизости: она должна быть недалеко."),
                                InteractionResult.nextPurposeResult("Поговорите с черепом"),
                                InteractionResult.transitionsResult(
                                        Arrays.asList(
                                                new ScriptAction.StateTransition(pig.getName(), 2),
                                                new ScriptAction.StateTransition(skull.getName(), 2)
                                        )
                                )
                        )
                )
        ));
        state1.setConditions(ActionCondition.makeConditionMap(
                Collections.singletonList(1),
                Collections.singletonList(
                        new ActionCondition(1)
                )
        ));

        ObjectState state2 = new ObjectState(2, false);
        state2.setActions(
                Collections.singletonList(
                        new ScriptAction(
                                1,
                                Collections.singletonList(
                                        InteractionResult.messageResult("Свин сказал: По поводу карты обращайся к черепушке")
                                )
                        )
                )
        );
        state2.setConditions(ActionCondition.makeConditionMap(
                Collections.singletonList(1),
                Collections.singletonList(
                        new ActionCondition(2)
                )
        ));

        pig.setStates(Arrays.asList(state1, state2));
        pig.setAction(pig.getActionFromStates());
    }

    private void setSkullStates() {
        ObjectState state1 = new ObjectState(1, true);
        state1.setActions(Collections.singletonList(
                new ScriptAction(
                        1,
                        Collections.singletonList(
                                InteractionResult.journalRecordResult("Череп сказал: Быть или не быть?.. Так, в чем вопрос, чувак?")
                        )
                )
        ));
        state1.setConditions(ActionCondition.makeConditionMap(
                Collections.singletonList(1),
                Collections.singletonList(
                        new ActionCondition(1)
                )
        ));

        ObjectState state2 = new ObjectState(2, false);
        state2.setActions(Collections.singletonList(
                new ScriptAction(
                        1,
                        Arrays.asList(
                                InteractionResult.journalRecordResult("Череп сказал: Карта? Я бы рад, да в горле что-то совсем пересохло. Принеси мне чего-нибудь выпить, чувак."),
                                InteractionResult.nextPurposeResult("Дайте черепу бутылку с выпивкой. Она должна быть неподалеку"),
                                InteractionResult.transitionsResult(Collections.singletonList(
                                        new ScriptAction.StateTransition(skull.getName(), 3)
                                ))
                        )
                )
        ));
        state2.setConditions(ActionCondition.makeConditionMap(
                Collections.singletonList(1),
                Collections.singletonList(
                        new ActionCondition(2)
                )
        ));

        ObjectState state3 = new ObjectState(3, false);
        state3.setActions(Arrays.asList(
                new ScriptAction(
                        1,
                        Arrays.asList(
                                InteractionResult.journalRecordResult("Череп сказал: Ну и чем мне из нее хлебать прикажешь? Стакан ищи."),
                                InteractionResult.nextPurposeResult("Дайте черепу стакан. Он должен быть где-то здесь"),
                                InteractionResult.transitionsResult(
                                        Arrays.asList(
                                                new ScriptAction.StateTransition(bottleSmall.getName(), 2),
                                                new ScriptAction.StateTransition(skull.getName(), 4)
                                        )
                                ),
                                InteractionResult.takeItemsResult(new Slot.RepeatedItem(bottleItem))
                        )
                ),
                new ScriptAction(
                        2,
                        Collections.singletonList(
                                InteractionResult.journalRecordResult("Череп сказал: Нет бутылки - нет карты!")
                        )
                )
        ));
        state3.setConditions(ActionCondition.makeConditionMap(
                Arrays.asList(1, 2),
                Arrays.asList(
                        new ActionCondition(Collections.singletonList(
                                new ActionCondition.ItemInfo(bottleItem.getId(), 1)
                        ),3),
                        new ActionCondition(3)
                )
        ));

        ObjectState state4 = new ObjectState(4, false);
        state4.setActions(Arrays.asList(
                new ScriptAction(
                        1,
                        Arrays.asList(
                                InteractionResult.journalRecordResult("Череп сказал: Во! Теперь ништяк! Тока шлем теперь мой найди, и лады."),
                                InteractionResult.journalRecordResult("Вы думаете: Мысль: достала меня эта костяшка. Может, привести аргумент посильнее? Где-то поблизости я тут видел топор..."),
                                InteractionResult.nextPurposeResult("Раздобудьте этой костяшке шлем... Ну, или топор - по настроению)"),
                                InteractionResult.transitionsResult(Arrays.asList(
                                        new ScriptAction.StateTransition(skull.getName(), 5),
                                        new ScriptAction.StateTransition(glassSmall.getName(), 2)
                                )),
                                InteractionResult.takeItemsResult(new Slot.RepeatedItem(glassItem))
                        )
                ),
                new ScriptAction(
                        2,
                        Collections.singletonList(
                                InteractionResult.messageResult("Череп сказал: Не, так у меня выпить не получится...")
                        )
                )
        ));
        state4.setConditions(ActionCondition.makeConditionMap(
                Arrays.asList(1, 2),
                Arrays.asList(
                        new ActionCondition(Collections.singletonList(
                                new ActionCondition.ItemInfo(glassItem.getId(), 1)
                        ),4),
                        new ActionCondition(4)
                )
        ));

        ObjectState state5 = new ObjectState(5,false);
        state5.setActions(Arrays.asList(
                new ScriptAction(
                        1,
                        Arrays.asList(
                                InteractionResult.journalRecordResult("Череп сказал: Отлично! Карта? Какая карта? Не, чувак, ты что-то спутал...)"),
                                InteractionResult.journalRecordResult("Вы подумали: Ну, все, пора доставать топор"),
                                InteractionResult.nextPurposeResult("Примените к черепу топор"),
                                InteractionResult.transitionsResult(
                                        Arrays.asList(
                                                new ScriptAction.StateTransition(columnHelmet.getName(), 2),
                                                new ScriptAction.StateTransition(skull.getName(), 6)
                                        )
                                ),
                                InteractionResult.takeItemsResult(new Slot.RepeatedItem(helmetItem))
                        )
                ),
                new ScriptAction(
                        2,
                        Arrays.asList(
                                InteractionResult.journalRecordResult("Череп сказал: Не, так дело не пойдет"),
                                InteractionResult.nextPurposeResult("Больше делать нечего. Приду в другой раз"),
                                InteractionResult.questEndResult(),
                                InteractionResult.transitionsResult(
                                        Collections.singletonList(
                                                new ScriptAction.StateTransition(skull.getName(), 8)
                                        )
                                )
                        )
                ),
                new ScriptAction(
                        3,
                        Collections.singletonList(
                                InteractionResult.messageResult("Череп сказал: Шлем бы мне, а то дождь весь мозг пропапал")
                        )
                )
        ));
        state5.setConditions(ActionCondition.makeConditionMap(
                Arrays.asList(1, 2, 3),
                Arrays.asList(
                        new ActionCondition(Collections.singletonList(
                                new ActionCondition.ItemInfo(helmetItem.getId(), 1)
                        ),5),
                        new ActionCondition(Collections.singletonList(
                                new ActionCondition.ItemInfo(axeItem.getId(), 1)
                        ),5),
                        new ActionCondition(5)
                )
        ));

        ObjectState state6 = new ObjectState(6,false);
        state6.setActions(Arrays.asList(
                new ScriptAction(
                        1,
                        Arrays.asList(
                                InteractionResult.journalRecordResult("Череп сказал: Эй, да чего ты такой нервный?! На, смотри!"),
                                InteractionResult.journalRecordResult("Вы подумали: сразу бы так"),
                                InteractionResult.nextPurposeResult("Вы получили данные о новых квестах"),
                                InteractionResult.transitionsResult(
                                        Arrays.asList(
                                                new ScriptAction.StateTransition(skull.getName(), 7),
                                                new ScriptAction.StateTransition(map.getName(), 2)
                                        )
                                ),
                                InteractionResult.questEndResult()
                        )
                ),
                new ScriptAction(
                        2,
                        Collections.singletonList(
                                InteractionResult.messageResult("Череп сказал: Я же сказал, что не в курсах...")
                        )
                )
        ));
        state6.setConditions(ActionCondition.makeConditionMap(
                Arrays.asList(1, 2),
                Arrays.asList(
                        new ActionCondition(Collections.singletonList(
                                new ActionCondition.ItemInfo(axeItem.getId(), 1)
                        ),6),
                        new ActionCondition(6)
                )
        ));

        ObjectState state7 = new ObjectState(7,false);
        state7.setActions(Collections.singletonList(
                new ScriptAction(
                        1,
                        Collections.singletonList(
                                InteractionResult.messageResult("Череп сказал: Я же показал тебе карту. Чего тебе еще нужно?")
                        )
                )
        ));
        state7.setConditions(ActionCondition.makeConditionMap(
                Collections.singletonList(1),
                Collections.singletonList(new ActionCondition(7))
        ));

        ObjectState state8 = new ObjectState(8,false);
        state7.setActions(Collections.singletonList(
                new ScriptAction(
                        1,
                        Collections.singletonList(
                                InteractionResult.messageResult("Череп сказал: Не, я так не играю.")
                        )
                )
        ));
        state8.setConditions(ActionCondition.makeConditionMap(
                Collections.singletonList(1),
                Collections.singletonList(new ActionCondition(8))
        ));

        skull.setStates(Arrays.asList(state1, state2, state3, state4, state5, state6, state7, state8));
        skull.setAction(skull.getActionFromStates());
    }

    private void createObjects() {
        // group of objects related to bottle
        bottleItem = new Item(
                100, "Бутылка", "Бутылка со спиртным",
                new VisualResource(Object3D.Type.FBX).setModelUri(asset("bottle_big.vrx"))
        );
        bottleItem.setUniformScale(smallScale);

        bottleContainer = new InteractiveObject(
                101, "Бутылка, лежащая на улице", "Здесь можно взять бутылку", Collections.singletonList(bottleItem)
        );
        bottleContainer.setVisualResource(
                new VisualResource(Object3D.Type.FBX).setModelUri(asset("bottle_big.vrx"))
        );
        bottleContainer.setUniformScale(mainScale);
        bottleContainer.initPhysicsBody(PhysicsBody.RigidBodyType.KINEMATIC, 0, new PhysicsShapeSphere(0.5f));
        bottleContainer.setPosition(new Vector(-1, 0, 0));

        bottleSmall = new InteractiveObject(
                102, "Бутылка на постаменте", "Из этой бутылки пьет череп"
        );
        bottleSmall.setVisualResource(
                new VisualResource(Object3D.Type.FBX).setModelUri(asset("bottle_small.vrx"))
        );
        bottleSmall.setUniformScale(mainScale);
        bottleSmall.initPhysicsBody(PhysicsBody.RigidBodyType.KINEMATIC, 0, new PhysicsShapeSphere(0.5f));

        // group of objects related to glass
        glassItem = new Item(
                200, "Стакан", "Не особенно чистый стакан",
                new VisualResource(Object3D.Type.FBX).setModelUri(asset("glass_big.vrx"))
        );
        glassItem.setUniformScale(smallScale);

        glassContainer = new InteractiveObject(
                201, "Стакан, лежащий на улице", "Здесь можно взять стакан", Collections.singletonList(glassItem)
        );
        glassContainer.setVisualResource(
                new VisualResource(Object3D.Type.FBX).setModelUri(asset("glass_big.vrx"))
        );
        glassContainer.setUniformScale(mainScale);
        glassContainer.initPhysicsBody(PhysicsBody.RigidBodyType.KINEMATIC, 0, new PhysicsShapeSphere(0.5f));
        glassContainer.setPosition(new Vector(-2, 0, 0));

        glassSmall = new InteractiveObject(
                202, "Стакан на постаменте", "Из этого стакана пьет череп"
        );
        glassSmall.setVisualResource(
                new VisualResource(Object3D.Type.FBX).setModelUri(asset("glass_small.vrx"))
        );
        glassSmall.setUniformScale(mainScale);
        glassSmall.initPhysicsBody(PhysicsBody.RigidBodyType.KINEMATIC, 0, new PhysicsShapeSphere(0.5f));

        // group of objects related to helmet
        helmetItem = new Item(
                300, "Шлем", "Классный мотоциклетный шлем",
                new VisualResource(Object3D.Type.FBX).setModelUri(asset("helmet2.vrx"))
        );
        helmetItem.setUniformScale(smallScale);

        helmetContainer = new InteractiveObject(
                301, "Контейнер для шлема", "Здесь можно взять шлем", Collections.singletonList(helmetItem)
        );
        helmetContainer.setVisualResource(
                new VisualResource(Object3D.Type.FBX).setModelUri(asset("helmet2.vrx"))
        );
        helmetContainer.setUniformScale(mainScale);
        helmetContainer.initPhysicsBody(PhysicsBody.RigidBodyType.KINEMATIC, 0, new PhysicsShapeSphere(0.5f));
        helmetContainer.setPosition(new Vector(0, 0, -2));

        columnHelmet = new InteractiveObject(
                302, "Шлем на черепе", "Шлем на черепе"
        );
        columnHelmet.setVisualResource(
                new VisualResource(Object3D.Type.FBX).setModelUri(asset("helmet.vrx"))
        );
        columnHelmet.setUniformScale(mainScale);
        columnHelmet.initPhysicsBody(PhysicsBody.RigidBodyType.KINEMATIC, 0, new PhysicsShapeSphere(0.5f));

        axeItem = new Item(
                400, "Топор", "Отличный топор",
                new VisualResource(Object3D.Type.FBX).setModelUri(asset("axe.vrx"))
        );
        axeItem.setUniformScale(smallScale);

        // axe related objects
        axeContainer = new InteractiveObject(
                401, "Топор, лежащий на земле", "Здесь можно взять топор", Collections.singletonList(axeItem)
        );
        axeContainer.setVisualResource(
                new VisualResource(Object3D.Type.FBX).setModelUri(asset("axe.vrx"))
        );
        axeContainer.setUniformScale(mainScale);
        axeContainer.initPhysicsBody(PhysicsBody.RigidBodyType.KINEMATIC, 0, new PhysicsShapeSphere(0.5f));
        axeContainer.setPosition(new Vector(0, 0, -4));

        column = new InteractiveObject(
                1001, "Колонна", "Колонна, на которой лежит череп"
        );
        column.setVisualResource(
                new VisualResource(Object3D.Type.FBX).setModelUri(assetPrefix + "column.vrx")
        );
        column.setUniformScale(mainScale);
        column.initPhysicsBody(PhysicsBody.RigidBodyType.KINEMATIC, 0, new PhysicsShapeSphere(0.5f));

        map = new InteractiveObject(
                1002, "Карта", "Карта"
        );
        map.setVisualResource(
                new VisualResource(Object3D.Type.FBX).setModelUri(assetPrefix + "map.vrx")
        );
        map.setUniformScale(mainScale);
        map.initPhysicsBody(PhysicsBody.RigidBodyType.KINEMATIC, 0, new PhysicsShapeSphere(0.5f));

        skull = new InteractiveObject(
                1003, "Череп", "Череп"
        );
        skull.setVisualResource(
                new VisualResource(Object3D.Type.FBX).setModelUri(assetPrefix + "skull.vrx")
        );
        skull.setUniformScale(mainScale);
        skull.initPhysicsBody(PhysicsBody.RigidBodyType.KINEMATIC, 0, new PhysicsShapeBox(0.5f, 2f, 0.5f));

        pig = new InteractiveObject(
                1004, "Свин", "Свин"
        );
        pig.setVisualResource(
                new VisualResource(Object3D.Type.FBX).setModelUri(assetPrefix + "pig.vrx")
        );
        pig.setUniformScale(mainScale);
        pig.initPhysicsBody(PhysicsBody.RigidBodyType.KINEMATIC, 0, new PhysicsShapeBox(0.5f, 2f, 0.5f));
        pig.setPosition(new Vector(0.5, 0.5, 2.5));
    }

    private String asset(String name) {
        return assetPrefix + name;
    }
}
