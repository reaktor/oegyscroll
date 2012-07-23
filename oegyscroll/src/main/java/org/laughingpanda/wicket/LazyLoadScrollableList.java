package org.laughingpanda.wicket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.data.IDataProvider;

public abstract class LazyLoadScrollableList<T extends Serializable> extends WebMarkupContainer implements IHeaderContributor {
    private int remainder;
    private int blockCountExcludingRemainderBlock;
    private final IDataProvider<T> dataProvider;
    private final int blockSize;
    private final List<Block<T>> blocks = new ArrayList<Block<T>>();
    private boolean javaScriptInitialized;
    private int initialRow;

    public LazyLoadScrollableList(final String id, final IDataProvider<T> dataProvider, final int blockSize) {
        super(id);
        this.blockSize = blockSize;
        this.dataProvider = dataProvider;
        add(new ScrolledContentView<T>("scrolledContent", blocks));
        setMarkupId("scroller" + System.identityHashCode(this));
        setOutputMarkupId(true);
    }

    public int getInitialRow() {
        return initialRow;
    }

    public void setInitialRow(final int initialRow) {
        this.initialRow = initialRow;
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
            remainder = blockSize;
            blockCountExcludingRemainderBlock--;
        }
    }

    protected abstract void populateRow(final WebMarkupContainer rowContainer, final int index, final T modelObject);

    public IDataProvider<T> getDataProvider() {
        return dataProvider;
    }    
    
    public void renderHead(IHeaderResponse response) {
    	if (!javaScriptInitialized) {
    		addScrollableListJavascript(response);
    		addContentLoaderInitializationJavascript(response);
    		javaScriptInitialized = true;
    	}
    }

    private void addScrollableListJavascript(IHeaderResponse response) {
        response.renderJavascriptReference(new ResourceReference(LazyLoadScrollableList.class, "oegyscroll-updater.js"));
    }

    private void addContentLoaderInitializationJavascript(IHeaderResponse response) {
        final String scrollerId = getMarkupId();
        final String scrolledContentId = get("scrolledContent").getMarkupId();
        response.renderOnDomReadyJavaScript("if (!window.oegyscroll) { window.oegyscroll = {timers: []}};" +
        		"window.oegyscroll.timers['"+scrollerId+"'] = new OegyScrollUpdater(\""+scrollerId+"\", \""+scrolledContentId+"\").scheduleScrollPositionUpdate();");
    }
}
