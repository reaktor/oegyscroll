package org.laughingpanda.wicket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.template.PackagedTextTemplate;
import org.apache.wicket.util.template.TextTemplateHeaderContributor;

public abstract class LazyLoadScrollableList<T> extends WebMarkupContainer {
    private int remainder;
    private int blockCountExcludingRemainderBlock;
    private final IDataProvider<T> dataProvider;
    private final int blockSize;
    private final List<Block> blocks = new ArrayList<Block>();

    public LazyLoadScrollableList(final String id, final IDataProvider<T> dataProvider, final int blockSize) {
        super(id);
        this.blockSize = blockSize;
        this.dataProvider = dataProvider;
        ScrolledContentView scrolledContent = new ScrolledContentView("scrolledContent");
        scrolledContent.add(new BlockDataView("block", blocks));
        add(scrolledContent);
        setMarkupId("scroller" + System.identityHashCode(this));
        add(TextTemplateHeaderContributor.forJavaScript(new PackagedTextTemplate(LazyLoadScrollableList.class, "scroller.js"), getJavascriptVariablesModel()));
        setOutputMarkupId(true);
    }

    @Override
    protected void onBeforeRender() {
        calculateBlockCountAndRemainder();
        blocks.clear();
        blocks.add(new RemainderBlock());
        for (int i = 0; i < blockCountExcludingRemainderBlock; i++) {
            blocks.add(new Block(i * blockSize + remainder, blockSize));
        }
        super.onBeforeRender();
    }

    private void calculateBlockCountAndRemainder() {
        int rowCount = getDataProvider().size();
        blockCountExcludingRemainderBlock = rowCount / blockSize;
        remainder = rowCount - blockCountExcludingRemainderBlock * blockSize;
        if (remainder == 0 && blockCountExcludingRemainderBlock > 0) {
            remainder = 1;
            blockCountExcludingRemainderBlock--;
        }
    }

    private IModel<Map<String, Object>> getJavascriptVariablesModel() {
        final Map<String, Object> javascripsVariables = new HashMap<String, Object>();
        javascripsVariables.put("scrollerId", getMarkupId());
        javascripsVariables.put("scrolledContentId", get("scrolledContent").getMarkupId());
        IModel<Map<String, Object>> variablesModel = new AbstractReadOnlyModel<Map<String, Object>>() {
            @Override
            public Map<String, Object> getObject() {
                return javascripsVariables;
            }
        };
        return variablesModel;
    }

    void setAttribute(final Component c, final String attribute, final String value) {
        c.add(new AttributeModifier(attribute, true, new Model<String>(value)));
    }

    protected abstract void populateRow(final WebMarkupContainer rowContainer, final T modelObject);

    public IDataProvider<T> getDataProvider() {
        return dataProvider;
    }

    private class Block implements Serializable {
        private Component placeholder;
        private RowDataView rows;
        private final int startIndex;
        private final int itemCount;
        private Item blockListItem;

        public Block(final int startIndex, final int itemCount) {
            this.startIndex = startIndex;
            this.itemCount = itemCount;
        }

        public void populate(final Item item) {
            item.setOutputMarkupId(true);
            this.blockListItem = item;
            placeholder = new PlaceHolder(this);
            item.add(placeholder);
            rows = new RowDataView("row");
            item.add(rows);
        }

        void showRows() {
            placeholder.setVisible(false);
            rows.setDataProvider(new SublistDataProvider<T>(getDataProvider(), startIndex, itemCount));
        }

        public void showRows(final AjaxRequestTarget target) {
            showRows();
            target.addComponent(blockListItem);
        }
    }

    private final class RowDataView extends DataView<T> {
        private RowDataView(final String id) {
            super(id, new ProxyDataProvider<T>());
        }

        public void setDataProvider(final SublistDataProvider<T> dataProvider) {
            ((ProxyDataProvider<T>) getDataProvider()).setDataProvider(dataProvider);
        }

        @Override
        protected void populateItem(final Item<T> item) {
            T modelObject = item.getModelObject();
            setAttribute(item, "class", "loaded-row");
            populateRow(item, modelObject);
        }
    }

    private final class RemainderBlock extends Block {
        public RemainderBlock() {
            super(0, remainder);
        }

        @Override
        public void populate(final Item item) {
            super.populate(item);
            showRows();
        }
    }

    private final class BlockDataView extends DataView<Block> {
        private BlockDataView(final String id, final List<Block> list) {
            super(id, new ListDataProvider<Block>(list));
        }

        @Override
        protected void populateItem(final Item<Block> item) {
            ((Block) item.getDefaultModelObject()).populate(item);
        }
    }

    final class PlaceHolder extends WebMarkupContainer {
        private PlaceHolder(final Block block) {
            super("placeholder");
            add(new AjaxEventBehavior("onclick") {
                @Override
                protected void onEvent(final AjaxRequestTarget target) {
                    block.showRows(target);
                }
            });
            setAttribute(this, "class", "loader-placeholder");
        }
    }
}
