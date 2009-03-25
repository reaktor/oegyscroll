package org.laughingpanda.wicket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.template.PackagedTextTemplate;
import org.apache.wicket.util.template.TextTemplateHeaderContributor;

public abstract class LazyLoadScrollableList<T extends Serializable> extends WebMarkupContainer {
    private int remainder;
    private int blockCountExcludingRemainderBlock;
    private final IDataProvider<T> dataProvider;
    private final int blockSize;
    private final List<Block<T>> blocks = new ArrayList<Block<T>>();

    public LazyLoadScrollableList(final String id, final IDataProvider<T> dataProvider, final int blockSize) {
        super(id);
        this.blockSize = blockSize;
        this.dataProvider = dataProvider;
        add(new ScrolledContentView<T>("scrolledContent", blocks));
        setMarkupId("scroller" + System.identityHashCode(this));
        add(TextTemplateHeaderContributor.forJavaScript(new PackagedTextTemplate(LazyLoadScrollableList.class, "scroller.js"), getJavascriptVariablesModel()));
        setOutputMarkupId(true);
    }

    @Override
    protected void onBeforeRender() {
        calculateBlockCountAndRemainder();
        blocks.clear();
        blocks.add(new RemainderBlock<T>(remainder, this));
        for (int i = 0; i < blockCountExcludingRemainderBlock; i++) {
            blocks.add(new Block<T>(i * blockSize + remainder, blockSize, this));
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

    protected abstract void populateRow(final WebMarkupContainer rowContainer, final T modelObject);

    public IDataProvider<T> getDataProvider() {
        return dataProvider;
    }
}
