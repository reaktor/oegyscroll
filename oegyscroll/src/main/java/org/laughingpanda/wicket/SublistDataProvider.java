package org.laughingpanda.wicket;

import java.util.Iterator;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

public class SublistDataProvider<T> implements IDataProvider<T> {
    private final IDataProvider<T> dataProvider;
    private final int index, count;

    public SublistDataProvider(final IDataProvider<T> dataProvider, final int index, final int count) {
        super();
        this.count = count;
        this.dataProvider = dataProvider;
        this.index = index;
    }

    public Iterator<? extends T> iterator(final int index, final int count) {
        return dataProvider.iterator(this.index + index, count);
    }

    public IModel<T> model(final T obj) {
        return dataProvider.model(obj);
    }

    public int size() {
        return count;
    }

    public void detach() {
        dataProvider.detach();
    }
}
