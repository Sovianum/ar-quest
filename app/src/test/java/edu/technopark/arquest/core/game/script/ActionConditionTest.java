package edu.technopark.arquest.core.game.script;

import edu.technopark.arquest.common.CollectionUtils;
import edu.technopark.arquest.core.game.Item;
import edu.technopark.arquest.core.game.slot.Slot;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;

public class ActionConditionTest {
    private Map<Integer, ActionCondition.ItemInfo> itemInfoMap;
    private Collection<Slot.RepeatedItem> items;
    private Collection<Slot.RepeatedItem> wrongItems;
    private List<String> strings;

    @Before
    public void setUp() {
        itemInfoMap = new HashMap<>();
        itemInfoMap.put(1, new ActionCondition.ItemInfo(1, 1));
        itemInfoMap.put(2, new ActionCondition.ItemInfo(2, 2));

        items = new ArrayList<>();
        wrongItems = new ArrayList<>();

        Slot.RepeatedItem ri1 = new Slot.RepeatedItem(
                new Item(1, "", "", "", ""),
                1
        );

        items.add(ri1);
        wrongItems.add(ri1);

        items.add(new Slot.RepeatedItem(
                new Item(2, "", "", "", ""),
                2
        ));
        wrongItems.add(new Slot.RepeatedItem(
                new Item(3, "", "", "", ""),
                3
        ));

        strings = new ArrayList<>();
        strings.add("a");
        strings.add("b");
    }

    @Test
    public void testItemCheck() {
        ActionCondition condition = new ActionCondition(itemInfoMap.values(), 1);
        assertFalse("wrong state", condition.checkItems(items, 2));
        assertFalse("not enough item kinds", condition.checkItems(new ArrayList<Slot.RepeatedItem>(), 1));
        assertFalse("wrong items", condition.checkItems(wrongItems, 1));

        Collection<Slot.RepeatedItem> insufficientItems = new ArrayList<>();
        insufficientItems.add(new Slot.RepeatedItem(
                new Item(1, "", "", "", ""),
                0
        ));
        assertFalse("not enough items", condition.checkItems(insufficientItems, 1));
        assertTrue(condition.checkItems(items, 1));
    }

    @Test
    public void testStringCheck() {
        ActionCondition condition = new ActionCondition(strings, 1);
        assertFalse("wrong state", condition.checkStrings(strings, 2));
        assertFalse("not enough arguments", condition.checkStrings(CollectionUtils.listOf("a"), 1));
        assertFalse("wrongOrder", condition.checkStrings(CollectionUtils.listOf("b", "a"), 1));
        assertTrue(condition.checkStrings(CollectionUtils.listOf("a", "b"), 1));
    }
}