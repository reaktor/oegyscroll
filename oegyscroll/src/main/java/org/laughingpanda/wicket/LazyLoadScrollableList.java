package org.laughingpanda.wicket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.template.PackagedTextTemplate;
import org.apache.wicket.util.template.TextTemplateHeaderContributor;

public abstract class LazyLoadScrollableList<T> extends WebMarkupContainer {
    int remainder;
    private int blockCountExcludingRemainderBlock;
    private final IDataProvider<T> dataProvider;
    final int blockSize;
    private final BlockDataView blockDataView;
    private final ArrayList<Block> blocks = new ArrayList<Block>();
    private final ScrolledContentView scrolledContent;

    public LazyLoadScrollableList(final String id, final IDataProvider<T> dataProvider, final int blockSize) {
        super(id);
        this.blockSize = blockSize;
        scrolledContent = new ScrolledContentView("scrolledContent");
        add(scrolledContent);
        this.dataProvider = dataProvider;
        blockDataView = new BlockDataView("block", blocks);
        scrolledContent.add(blockDataView);
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

    @SuppressWarnings("unchecked")
	private Model getJavascriptVariablesModel() {
        final HashMap<String, String> javascripsVariables = new HashMap<String, String>();
        javascripsVariables.put("scrollerId", getMarkupId());
        javascripsVariables.put("scrolledContentId", scrolledContent.getMarkupId());
        Model variablesModel = new Model(javascripsVariables);
        return variablesModel;
    }

    void setAttribute(final Component c, final String attribute, final String value) {
        c.add(new AttributeModifier(attribute, true, new Model(value)));
    }

    protected abstract void populateRow(final WebMarkupContainer rowContainer, final T modelObject);

    public IDataProvider getDataProvider() {
        return dataProvider;
    }

    private final class ScrolledContentView extends WebMarkupContainer {
        private ScrolledContentView(final String id) {
            super(id);
            setOutputMarkupId(true);
        }
    }

    class Block implements Serializable {
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
            rows.setDataProvider(new SublistDataProvider(getDataProvider(), startIndex, itemCount));
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

        @SuppressWarnings("unchecked")
        @Override
        protected void populateItem(final Item item) {
            T modelObject = (T) item.getDefaultModelObject();
            setAttribute(item, "class", "loaded-row");
            populateRow(item, modelObject);
        }
    }

    class RemainderBlock extends Block {
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
