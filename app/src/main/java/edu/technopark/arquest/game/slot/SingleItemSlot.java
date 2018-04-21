package edu.technopark.arquest.game.slot;

import edu.technopark.arquest.game.Item;
import edu.technopark.arquest.common.CollectionUtils;

public class SingleItemSlot extends Slot {
    public SingleItemSlot(int id, String name, boolean isAccessible) {
        super(id, name, isAccessible);
    }

    @Override
    public boolean put(Item item) {
        return put(new RepeatedItem(item, 1));
    }

    @Override
    public boolean put(RepeatedItem repeatedItem) {
        if (items.size() == 0) {
            return super.put(repeatedItem);
        } else if (items.size() == 1) {
            RepeatedItem item = CollectionUtils.first(items.values());
            if (item.getItem().getId() == repeatedItem.getItem().getId()) {
                item.add(repeatedItem.getCnt());
                return true;
            }
        }
        return false;
    }
}
