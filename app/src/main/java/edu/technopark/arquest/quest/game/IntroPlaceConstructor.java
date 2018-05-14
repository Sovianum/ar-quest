package edu.technopark.arquest.quest.game;

import com.viro.core.Object3D;
import com.viro.core.PhysicsBody;
import com.viro.core.PhysicsShapeBox;
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
import edu.technopark.arquest.quest.PlaceConstructor;

public class IntroPlaceConstructor extends PlaceConstructor {
    private float mainScale;
    private float smallScale;

    private Item item;
    private InteractiveObject npc;

    public IntroPlaceConstructor(float mainScale, float smallScale, String assetPrefix) {
        super(assetPrefix);
        this.mainScale = mainScale;
        this.smallScale = smallScale;
    }

    public Place getPlace() {
        createObjects();
        setNPCStates();

        Place place = new Place();
        place.loadInteractiveObjects(Collections.singletonList(npc));
        place.setStartPurpose("Подойдите к обучателю и поговорите с ним (кнопка в центре станет зеленой)");
        return place;
    }

    private void setNPCStates() {
        ObjectState state1 = new ObjectState(1, true);
        state1.setActions(Collections.singletonList(
                new ScriptAction(
                        1,
                        Arrays.asList(
                                InteractionResult.journalRecordResult(
                                        "Обучатель сказал: Здравствуй, благородный искатель приключений. Я сориентирую тебя в нашем мире." +
                                        "Все действия встреченных тобой существ записываются в журнал. Эту мою фразу ты тоже сможешь в нем увидеть." +
                                        "Посмотри в свой журнал и поговори со мной еще раз для того, чтобы начать работать с инвентарем."
                                ),
                                InteractionResult.nextPurposeResult(
                                        "Посмотрите в свой журнал и еще раз поговорите с обучателем"
                                ),
                                InteractionResult.transitionsResult(Collections.singletonList(
                                        new ScriptAction.StateTransition(npc.getName(), 2)
                                ))
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
                                InteractionResult.journalRecordResult(
                                        "Обучатель сказал: Кажется, у тебя в инвентаре моя копия. Дай мне на нее посмотреть"
                                ),
                                InteractionResult.nextPurposeResult(
                                        "Возьмите из инвентаря копию скелета и покажите ее обучателю"
                                ),
                                InteractionResult.newItemsResult(new Slot.RepeatedItem(item)),
                                InteractionResult.transitionsResult(Collections.singletonList(
                                        new ScriptAction.StateTransition(npc.getName(), 3)
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
                                InteractionResult.journalRecordResult(
                                        "Обучатель сказал: Да, и правда, один в один. Оставь его себе и поговори со мной еще раз"
                                ),
                                InteractionResult.nextPurposeResult(
                                        "Положите предмет обратно в инвентарь и еще раз поговорите с обучателем"
                                ),
                                InteractionResult.transitionsResult(Collections.singletonList(
                                        new ScriptAction.StateTransition(npc.getName(), 4)
                                ))
                        )
                ),
                new ScriptAction(
                        2,
                        Collections.singletonList(
                                InteractionResult.journalRecordResult(
                                        "Обучатель сказал: Сначала достань мою копию из инвентаря"
                                )
                        )
                )
        ));
        state3.setConditions(ActionCondition.makeConditionMap(
                Arrays.asList(1, 2),
                Arrays.asList(
                        new ActionCondition(Collections.singletonList(
                                new ActionCondition.ItemInfo(item.getId(), 1)
                        ), 3),
                        new ActionCondition(3)

                )
        ));

        ObjectState state4 = new ObjectState(4, false);
        state4.setActions(Arrays.asList(
                new ScriptAction(1, Collections.singletonList(InteractionResult.questEndResult())),
                new ScriptAction(2, Collections.singletonList(
                        InteractionResult.journalRecordResult(
                                "Сначала убери предмет в инвентарь с помощью кнопки слева"
                        )
                ))
        ));
        state4.setConditions(ActionCondition.makeConditionMap(
                Arrays.asList(1, 2),
                Arrays.asList(
                        new ActionCondition(4),
                        new ActionCondition(Collections.singletonList(
                                new ActionCondition.ItemInfo(item.getId(), 1)
                        ), 4)
                )
        ));

        npc.setStates(Arrays.asList(state1, state2, state3, state4));
        npc.setAction(npc.getActionFromStates());
    }

    private void createObjects() {
        item = new Item(
                100, "Скелет", "Скелет",
                new VisualResource(Object3D.Type.FBX).setModelUri(asset("skeleton.vrx")),
                "skeleton.png"
        );
        item.setUniformScale(smallScale);

        npc = new InteractiveObject(
                101, "Обучающий персонаж", "Обучающий персонаж", Collections.singletonList(item)
        );
        npc.setVisualResource(
                new VisualResource(Object3D.Type.FBX).setModelUri(asset("skeleton.vrx"))
        );
        npc.setUniformScale(mainScale);
        npc.initPhysicsBody(PhysicsBody.RigidBodyType.KINEMATIC, 0, new PhysicsShapeBox(0.5f, 5f, 0.5f));
        npc.setOriginalPosition(new Vector(0, 0, -0.2));
    }
}
