package org.laughingpanda.wicket;

import java.io.Serializable;

import org.apache.wicket.markup.repeater.Item;

class RemainderBlock<T extends Serializable> extends Block<T> {
    public RemainderBlock(int remainder, LazyLoadScrollableList<T> list) {
        super(0, remainder, list);
    }

    @Override
    public void populate(final Item<Block<T>> item) {
        super.populate(item);
        showRows();
    }
}
