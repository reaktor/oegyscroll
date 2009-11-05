package org.laughingpanda.wicket;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;

class Block<T extends Serializable> implements Serializable {
    private Component placeholder;
    private RowDataView<T> rowDataView;
    private final int startIndex;
    private final int itemCount;
    private Item<Block<T>> blockListItem;
    private final LazyLoadScrollableList<T> list;

    public Block(final int startIndex, final int itemCount, LazyLoadScrollableList<T> list) {
        this.startIndex = startIndex;
        this.itemCount = itemCount;
        this.list = list;        
    }

    public void populate(final Item<Block<T>> item) {
        item.setOutputMarkupId(true);
        item.add(new AttributeAppender("class", true, new Model<String>("block"), " "));
        this.blockListItem = item;
        placeholder = new PlaceHolder<T>(this);
        item.add(placeholder);
        rowDataView = new RowDataView<T>("row", list, startIndex);
        item.add(rowDataView);
    }

    protected void showRows() {
        placeholder.setVisible(false);
        rowDataView.setDataProvider(new SublistDataProvider<T>(list.getDataProvider(), startIndex, itemCount));
    }

    public void showRows(final AjaxRequestTarget target) {
        showRows();
        target.addComponent(blockListItem);
    }
}
