package org.laughingpanda.wicket;

import java.util.Iterator;

import org.apache.wicket.markup.repeater.data.EmptyDataProvider;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

class ProxyDataProvider<T> implements IDataProvider<T> {
    private IDataProvider<T> dataProvider;

    public ProxyDataProvider() {
        this(new EmptyDataProvider<T>());
    }

    public ProxyDataProvider(final IDataProvider<T> dataProvider) {
        super();
        this.dataProvider = dataProvider;
    }

    public void setDataProvider(final IDataProvider<T> dataProvider) {
        this.dataProvider = dataProvider;
    }

    public void detach() {
        dataProvider.detach();
    }

    public Iterator iterator(final int i, final int j) {
        return dataProvider.iterator(i, j);
    }

    public IModel<T> model(final T obj) {
        return dataProvider.model(obj);
    }

    public int size() {
        return dataProvider.size();
    }
}
