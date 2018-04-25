package edu.technopark.arquest.game.script;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.technopark.arquest.game.slot.Slot;

public class ActionCondition {
    public static Map<Integer, ActionCondition> makeConditionMap(List<Integer> actionIDs, List<ActionCondition> conditions) {
        if (actionIDs.size() != conditions.size()) {
            throw new RuntimeException("length not equal");
        }
        Map<Integer, ActionCondition> result = new HashMap<>();
        for (int i = 0; i != actionIDs.size(); ++i) {
            result.put(actionIDs.get(i), conditions.get(i));
        }
        return result;
    }

    public static class ItemInfo {
        private int itemID;
        private int itemCnt;

        public ItemInfo(int itemID, int itemCnt) {
            this.itemID = itemID;
            this.itemCnt = itemCnt;
        }

        public int getItemID() {
            return itemID;
        }

        public int getItemCnt() {
            return itemCnt;
        }
    }

    private Map<Integer, ItemInfo> itemInfoMap;
    private List<String> strings;
    private int sourceStateID;

    public ActionCondition(int sourceStateID) {
        this(new ArrayList<ItemInfo>(), new ArrayList<String>(), sourceStateID);
    }

    public ActionCondition(Collection<ItemInfo> itemInfoCollection, int sourceStateID) {
        this(itemInfoCollection, new ArrayList<String>(), sourceStateID);
    }

    public ActionCondition(List<String> strings, int sourceStateID) {
        this(new ArrayList<ItemInfo>(), strings, sourceStateID);
    }

    public ActionCondition(Collection<ItemInfo> itemInfoCollection, List<String> strings, int sourceStateID) {
        this.strings = strings;
        this.sourceStateID = sourceStateID;

        this.itemInfoMap = new HashMap<>(itemInfoCollection.size());
        for (ItemInfo info : itemInfoCollection) {
            itemInfoMap.put(info.getItemID(), info);
        }
    }

    public int checkStrings(List<String> strings, int stateID) {
        return check(new ArrayList<Slot.RepeatedItem>(), strings, stateID);
    }

    public int checkItems(Collection<Slot.RepeatedItem> argItems, int stateID) {
        return check(argItems, new ArrayList<String>(), stateID);
    }

    public int check(Collection<Slot.RepeatedItem> argItems, List<String> argStrings, int stateID) {
        int matchRate = 0;

        if (stateID != sourceStateID) {
            return -1;
        }

        if (itemInfoMap.size() > 0 && (argItems == null || argItems.size() != itemInfoMap.size())) {
            return -1;
        }

        if (itemInfoMap.size() > 0) {
            for (Slot.RepeatedItem item : argItems) {
                ItemInfo itemInfo = itemInfoMap.get(item.getItem().getId());
                if (itemInfo == null) {
                    return -1;
                }
                if (itemInfo.getItemCnt() < item.getCnt()) {
                    return -1;
                }
            }
            matchRate += itemInfoMap.size();
        }

        if (strings.size() > 0 && (argStrings == null || argStrings.size() != strings.size())) {
            return -1;
        }

        if (strings.size() > 0) {
            for (int i = 0; i != argStrings.size(); ++i) {
                if (!argStrings.get(i).equals(strings.get(i))) {
                    return -1;
                }
            }
            matchRate += strings.size();
        }

        return matchRate;
    }
}
