package com.google.ar.core.examples.java.helloar.core.game.slot;

import com.google.ar.core.examples.java.helloar.core.ar.drawable.IDrawable;
import com.google.ar.core.examples.java.helloar.core.game.Item;
import com.google.ar.core.examples.java.helloar.core.game.Utils;

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
            RepeatedItem item = Utils.first(items.values());
            if (item.getItem().getId() == repeatedItem.getItem().getId()) {
                item.add(repeatedItem.getCnt());
                return true;
            }
        }
        return false;
    }

    @Override
    public IDrawable getDrawable() {
        if (items.size() == 0) {
            return null;
        }
        return Utils.first(items.values()).getItem();
    }
}
