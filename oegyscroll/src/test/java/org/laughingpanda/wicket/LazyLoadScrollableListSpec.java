package org.laughingpanda.wicket;

import static java.util.Collections.emptyList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jdave.junit4.JDaveRunner;
import jdave.wicket.ComponentSpecification;

import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(JDaveRunner.class)
public class LazyLoadScrollableListSpec extends ComponentSpecification<LazyLoadScrollableListTestPage, Void> {
    List<String> testData = emptyList();

    private void createTestData(final int rowCount) {
        testData = new ArrayList<String>(rowCount);
        for (int i = 0; i < rowCount; i++) {
            testData.add(new String("row" + i));
        }
    }
    int blockSize = 4;

    @SuppressWarnings("unchecked")
    @Override
    protected LazyLoadScrollableListTestPage newComponent(final String id, final IModel model) {
        return new LazyLoadScrollableListTestPage(testData, blockSize);
    }

    @Ignore
    public class AnyScroller {
        public LazyLoadScrollableListTestPage create() {
            return startComponent();
        }

        public void hasFixedMarkupId() {
            specify(getScroller().getMarkupId(), must.equal("scroller" + System.identityHashCode(getScroller())));
        }

        public void hasOutputMarkupIdSetToTrue() {
            specify(getScroller().getOutputMarkupId());
        }
    }

    @Ignore
    public class AnyScrollerWithData {
        public LazyLoadScrollableListTestPage create() {
            blockSize = 5;
            createTestData(7);
            return startComponent();
        }

        public void setsClassAttributeOfBlockElements() {
            String attribute = wicket.getTagByWicketId("block").getAttribute("class");
            specify(attribute, must.equal("markup-class-for-block block"));
        }
    }

    @Ignore
    public class WhenDatasetSizeIsZero {
        public LazyLoadScrollableListTestPage create() {
            testData = Arrays.asList();
            return startComponent();
        }

        public void noRowsAreRendered() {
            specify(getRenderedRows().size(), 0);
        }

        public void thereIsOnePlaceholderButItIsInvisible() {
            specify(getPlaceholders().size(), 1);
            specify(getPlaceholders().get(0).isVisible(), false);
        }
    }

    @Ignore
    public class WhenDatasetIsLessThanBlockSize {
        public LazyLoadScrollableListTestPage create() {
            blockSize = 4;
            createTestData(3);
            return startComponent();
        }

        public void allRowsAreRendered() {
            specify(getRenderedRows().size(), 3);
        }

        public void placeHolderIsHidden() {
            specify(getPlaceholders().get(0).isVisible(), false);
        }

        public void providesIndexForRows() {
            verifyIndexes();
        }
    }

    @Ignore
    public class WhenDatasetIsEqualToBlockSize {
        public LazyLoadScrollableListTestPage create() {
            blockSize = 4;
            createTestData(blockSize);
            return startComponent();
        }

        public void allRowsAreRendered() {
            specify(getRenderedRows().size(), 4);
        }

        public void placeHolderIsHidden() {
            specify(getPlaceholders().get(0).isVisible(), false);
        }

        public void providesIndexForRows() {
            verifyIndexes();
        }
    }

    @Ignore
    public abstract class WhenDataSetIsLargerThanBlockSize {
        public void placeHolderForFirstBlockIsHidden() {
            specify(getPlaceholders().get(0).isVisible(), false);
        }

        public void placeHoldersForOtherBlocksAreShown() {
            specify(getPlaceholders().get(1).isVisible());
        }

        public void providesConsecutiveIndexesForRowsOnDifferentBlocks() {
            showSecondBlock();
            verifyIndexes();
        }
    }

    @Ignore
    public class WhenDatasetIsTwoTimesBlockSize extends WhenDataSetIsLargerThanBlockSize {
        public LazyLoadScrollableListTestPage create() {
            blockSize = 4;
            createTestData(8);
            return startComponent();
        }

        public void firstBlockIsRendered() {
            specify(getRenderedRows().size(), 4);
        }

        public void secondBlockIsRenderedWhenClicked() {
            showSecondBlock();
            specify(getPlaceholders().get(1).isVisible(), false);
            specify(getRenderedRows().size(), 8);
        }
    }

    @Ignore
    public class WhenDataSetIsBlockSizePlus1 {
        public LazyLoadScrollableListTestPage create() {
            blockSize = 5;
            createTestData(6);
            return startComponent();
        }

        public void remainderBlockIsRendered() {
            specify(getRenderedRows().size(), 1);
        }

        public void secondBlockIsRenderedWhenClicked() {
            showSecondBlock();
            specify(getPlaceholders().get(1).isVisible(), false);
            specify(getRenderedRows().size(), 6);
        }
    }

    @Ignore
    public class WhenDataSetIsBlockSizePlus2 {
        public LazyLoadScrollableListTestPage create() {
            blockSize = 5;
            createTestData(7);
            return startComponent();
        }

        public void remainderBlockIsRendered() {
            specify(getRenderedRows().size(), 2);
        }

        public void secondBlockIsRenderedWhenClicked() {
            showSecondBlock();
            specify(getPlaceholders().get(1).isVisible(), false);
            specify(getRenderedRows().size(), 7);
        }
    }

    @Ignore
    public class WhenSettingInitialRow {
        public LazyLoadScrollableListTestPage create() {
            blockSize = 5;
            createTestData(6);
            return startComponent();
        }

        public void blockContainingInitialRowIsRendered() {
            getScroller().setInitialRow(blockSize);
            wicket.executeAjaxEvent(selectFirst(AjaxLink.class, "refresh").from(context), "onclick");
            specify(getRenderedRows().size(), 6);
        }
    }

    @Ignore
    public class WhenUpdatingListUsingAjax {
        public LazyLoadScrollableListTestPage create() {
            createTestData(2);
            return startComponent();
        }

        public void contentIsUpdated() {
            wicket.executeAjaxEvent(selectFirst(AjaxLink.class, "replaceData").from(context), "onclick");
            String newTextOnFirstRow = getTextOnRow(0);
            specify(newTextOnFirstRow, must.equal("lol"));
        }

        private String getTextOnRow(final int row) {
            return getRenderedRows().get(row).getDefaultModelObjectAsString();
        }
    }

    @Ignore
    public class RendersJavascript {
        IHeaderResponse response = Mockito.mock(IHeaderResponse.class);
        DummyScroller dummyScroller = new DummyScroller();

        public void onFirstRendering() {
            render();
            verify(response).renderJavaScriptReference(any(JavaScriptResourceReference.class));
            verify(response).renderOnDomReadyJavaScript(any(String.class));
        }

        public void notTwice() {
            onFirstRendering();
            render();
            verifyNoMoreInteractions(response);
        }

        private void render() {
            dummyScroller.renderHead(response);
        }
    }

    @SuppressWarnings("unchecked")
    private List<PlaceHolder> getPlaceholders() {
        return selectAll(PlaceHolder.class).from(context);
    }

    private List<Label> getRenderedRows() {
        return selectAll(Label.class, "rowLabel").from(context);
    }

    private List<Label> getRenderedIndexes() {
        return selectAll(Label.class, "indexLabel").from(context);
    }

    @SuppressWarnings("unchecked")
    private LazyLoadScrollableList<String> getScroller() {
        return selectFirst(LazyLoadScrollableList.class).from(context);
    }

    private void showSecondBlock() {
        wicket.executeAjaxEvent(getPlaceholders().get(1), "onclick");
    }

    private void verifyIndexes() {
        specify(getRenderedIndexes().get(0).getDefaultModelObjectAsString(), should.equal("0"));
        specify(getRenderedIndexes().get(1).getDefaultModelObjectAsString(), should.equal("1"));
    }

    private final static class DummyScroller extends LazyLoadScrollableList {
        private DummyScroller() {
            super("id", null, 100);
        }

        @Override
        protected void populateRow(final WebMarkupContainer rowContainer, final int index, final Serializable modelObject) {}
    }
}
