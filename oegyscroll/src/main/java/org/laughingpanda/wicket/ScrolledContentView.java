package org.laughingpanda.wicket;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;

class ScrolledContentView<T extends Serializable> extends WebMarkupContainer {
    public ScrolledContentView(final String id, final List<Block<T>> blocks) {
        super(id);
        setOutputMarkupId(true);
        add(new DataView<Block<T>>("block", new ListDataProvider<Block<T>>(blocks)) {
            @Override
            protected void populateItem(Item<Block<T>> item) {
                Block<T> block= item.getModelObject();
                block.populate(item);
            }
        });

    }
}
