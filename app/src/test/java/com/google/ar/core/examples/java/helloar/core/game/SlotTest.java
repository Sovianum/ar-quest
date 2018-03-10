package com.google.ar.core.examples.java.helloar.core.game;

import com.google.ar.core.examples.java.helloar.core.game.slot.Slot;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SlotTest {
    private Slot slot1;
    private Slot slot2;

    private Item item1;
    private Item item2;
    private Item item3;

    @Before
    public void setUp() {
        item1 = new Item(1, "item1", "d1", "m1", "t1");
        item2 = new Item(2, "item2", "d2", "m2", "t2");
        item3 = new Item(3, "item3", "d3", "m3", "t3");

        slot1 = new Slot(0, "slot1", true);
        slot1.put(item1);
        slot1.put(item1);

        slot2 = new Slot(0, "slot2", true);
        slot2.put(item2);
        slot2.put(item3);
    }

    @Test
    public void testItemWatch() {
        Collection<Slot.RepeatedItem> items1 = slot1.getRepeatedItems().values();
        assertEquals(1, items1.size());

        Slot.RepeatedItem item1 = items1.iterator().next();
        assertEquals(2, item1.getCnt());
        assertEquals(1, item1.getItem().getId());

        List<Slot.RepeatedItem> items2 = new ArrayList<>();
        items2.addAll(slot2.getRepeatedItems().values());
        items2.sort(new Comparator<Slot.RepeatedItem>() {
            @Override
            public int compare(Slot.RepeatedItem o1, Slot.RepeatedItem o2) {
                return o1.getItem().getId() - o2.getItem().getId();
            }
        });

        assertEquals(2, items2.size());
        assertEquals(2, items2.get(0).getItem().getId());
        assertEquals(1, items2.get(0).getCnt());
        assertEquals(3, items2.get(1).getItem().getId());
        assertEquals(1, items2.get(1).getCnt());
    }

    @Test
    public void testItemMove() {
        boolean fail = slot1.move(99, slot2);
        assertFalse(fail);
        assertEquals(1, slot1.getRepeatedItems().size());
        assertEquals(2, slot2.getRepeatedItems().size());

        boolean success = slot1.move(1, slot2);
        assertTrue(success);
        assertEquals(0, slot1.getRepeatedItems().size());
        assertEquals(3, slot2.getRepeatedItems().size());
    }
}