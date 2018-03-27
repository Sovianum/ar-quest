package technopark.diploma.arquest.core.game;

import com.google.ar.core.Pose;
import technopark.diploma.arquest.common.CollectionUtils;
import technopark.diploma.arquest.core.ar.collision.Collider;
import technopark.diploma.arquest.core.ar.collision.shape.Sphere;
import technopark.diploma.arquest.core.ar.drawable.TextureDrawable;
import technopark.diploma.arquest.core.game.script.ActionCondition;
import technopark.diploma.arquest.core.game.script.ObjectState;
import technopark.diploma.arquest.core.game.script.ScriptAction;
import technopark.diploma.arquest.core.game.slot.Slot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class InteractiveObjectTest {
    private InteractiveObject obj;

    @Before
    public void setUp() {
        obj = new InteractiveObject(0, "name", "description");
        obj.setStates(CollectionUtils.listOf(ObjectState.enableObjectState(0, true, true)));
        obj.setAction(new ItemlessAction() {
            @Override
            public Collection<InteractionResult> act(InteractionArgument argument) {
                return CollectionUtils.singleItemList(InteractionResult.journalRecordResult("record"));
            }
        });
    }

    @Test
    public void testInteractAccessible() {
        Collection<InteractionResult> results = obj.interact(null);
        assertEquals(1, results.size());

        InteractionResult result = results.iterator().next();
        assertEquals(result.type, InteractionResult.Type.JOURNAL_RECORD);
        assertEquals(result.msg, "record");
    }

    @Test
    public void testInteractInaccessible() {
        obj.setEnabled(false);
        Collection<InteractionResult> results = obj.interact(null);
        assertEquals(1, results.size());

        InteractionResult result = results.iterator().next();
        assertEquals(result.type, InteractionResult.Type.ERROR);
    }

    @Test
    public void testActionFromStates() {
        ObjectState state1 = new ObjectState(1, true);
        state1.setEnabled(true);

        ScriptAction action1 = new ScriptAction(1, CollectionUtils.listOf(
                InteractionResult.messageResult("msg1"),
                InteractionResult.transitionsResult(
                        CollectionUtils.listOf(new ScriptAction.StateTransition(1, 2))
                )
        ));
        state1.setActions(CollectionUtils.listOf(action1));

        ActionCondition defaultCondition = new ActionCondition(1);
        Map<Integer, ActionCondition> conditionMap = new HashMap<>();
        conditionMap.put(1, defaultCondition);
        state1.setConditions(conditionMap);

        obj.setStates(CollectionUtils.listOf(state1));
        obj.setAction(obj.getActionFromStates());

        ArrayList<InteractionResult> results = (ArrayList<InteractionResult>) obj.interact(new InteractionArgument());
        assertEquals(2, results.size());
        assertEquals(InteractionResult.Type.MESSAGE, results.get(0).getType());
        assertEquals(InteractionResult.Type.TRANSITIONS, results.get(1).getType());
    }

    @Test
    public void createInteractionScript() throws IOException {
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
                                InteractionResult.messageResult("Возьми поесть у белого человека"),
                                InteractionResult.transitionsResult(
                                        CollectionUtils.listOf(
                                                new ScriptAction.StateTransition(1, 2),
                                                new ScriptAction.StateTransition(2, 1)
                                        )
                                )
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
                                InteractionResult.messageResult("Отблагодари белого человека"),
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
                                InteractionResult.messageResult("Дай ему поесть"),
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
                                InteractionResult.messageResult("Да за кого он меня принимает?!"),
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

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        FileOutputStream fos = new FileOutputStream("/home/artem/Technopark/Semester_4/hack1/arquest/app/src/main/assets/scripts/inter_place.json");
        DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(fos));
        outStream.write(gson.toJson(place).getBytes());
    }
}