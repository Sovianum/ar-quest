package com.google.ar.core.examples.java.helloar.core.game;

import com.google.ar.core.examples.java.helloar.common.CollectionUtils;
import com.google.ar.core.examples.java.helloar.core.game.script.ObjectState;
import com.google.ar.core.examples.java.helloar.core.game.slot.Slot;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class PlaceTest {
    private Place place;

    private InteractiveObject inter1;
    private InteractiveObject inter2;

    private Item item1;
    private Item item2;

    private Slot slot1;
    private Slot slot2;
    private Slot inventory;

    @Before
    public void setUp() {
        item1 = new Item(1, "item1", "d1", "m1", "t1");
        item2 = new Item(2, "item2", "d2", "m2", "t2");

        slot1 = new Slot(1, "slot1", true);
        slot1.put(item1);

        slot2 = new Slot(2, "slot2", false);
        slot2.put(item2);

        inventory = new Slot(0, "inv", true);

        inter1 = new InteractiveObject(1, "name1", "descr1");
        inter1.setStates(CollectionUtils.listOf(ObjectState.enableObjectState(0, true, true)));
        inter1.setAction(new ItemlessAction() {
            @Override
            public Collection<InteractionResult> act(InteractionArgument argument) {
                inter2.setEnabled(true);
                return CollectionUtils.singleItemList(InteractionResult.journalRecordResult("open inter2"));
            }
        });

        inter2 = new InteractiveObject(2, "name2", "descr2");
        inter2.setStates(CollectionUtils.listOf(ObjectState.enableObjectState(0, true, false)));
        inter2.setAction(new ItemlessAction() {
            @Override
            public Collection<InteractionResult> act(InteractionArgument argument) {
                for (Slot.RepeatedItem r : argument.items) {
                    if (r.getItem().getId() == 1) {
                        r.dropAll();
                        slot2.setEnabled(true);
                        return CollectionUtils.singleItemList(
                                InteractionResult.journalRecordResult("open slot2")
                        );
                    }
                }
                return CollectionUtils.singleItemList(
                        InteractionResult.messageResult("failed to interact")
                );
            }
        });

        Map<Integer, InteractiveObject> interactives = new HashMap<>();
        interactives.put(inter1.getId(), inter1);
        interactives.put(inter2.getId(), inter2);

        place = new Place();
        place.setInteractiveObjects(interactives);
    }

    @Test
    public void testScenario() {
        assertEquals(1, place.getAccessibleInteractiveObjects().size());

        Collection<InteractionResult> results1 = place.getAccessibleInteractiveObjects().get(1).interact(null);
        assertEquals(1, results1.size());
        InteractionResult result1 = CollectionUtils.first(results1);
        assertEquals(InteractionResult.Type.JOURNAL_RECORD, result1.type);
        assertEquals(2, place.getAccessibleInteractiveObjects().size());

        InteractiveObject inter2 = place.getAccessibleInteractiveObjects().get(2);

        InteractionArgument badEmptyArgument = new InteractionArgument();
        Collection<InteractionResult> badResults1 = inter2.interact(badEmptyArgument);
        assertEquals(InteractionResult.Type.MESSAGE, CollectionUtils.first(badResults1).type);
        assertEquals(1, slot1.getItemCnt(1));

        InteractionArgument badItemArgument = InteractionArgument.itemArg(CollectionUtils.singleItemList(new Slot.RepeatedItem(item2)));
        Collection<InteractionResult> badResults2 = inter2.interact(badItemArgument);
        assertEquals(InteractionResult.Type.MESSAGE, CollectionUtils.first(badResults2).type);
        assertEquals(1, slot1.getItemCnt(1));

        InteractionArgument goodArgument = InteractionArgument.itemArg(CollectionUtils.singleItemList(slot1.getRepeatedItems().get(1)));
        Collection<InteractionResult> goodResults = inter2.interact(goodArgument);
        assertEquals(InteractionResult.Type.JOURNAL_RECORD, CollectionUtils.first(goodResults).type);
        assertEquals(0, slot1.getItemCnt(1));
    }
}