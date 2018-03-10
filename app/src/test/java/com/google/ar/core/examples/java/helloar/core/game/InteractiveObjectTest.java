package com.google.ar.core.examples.java.helloar.core.game;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class InteractiveObjectTest {
    private InteractiveObject obj;

    @Before
    public void setUp() {
        obj = new InteractiveObject(0, "name", "description", true);
        obj.setAction(new Action() {
            @Override
            public Collection<InteractionResult> act(InteractionArgument argument) {
                return Utils.singleItemCollection(new InteractionResult(InteractionResult.Type.JOURNAL_RECORD, "record"));
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
}