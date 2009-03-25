package org.laughingpanda.wicket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jdave.junit4.JDaveRunner;
import jdave.wicket.ComponentSpecification;

import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.junit.runner.RunWith;

@RunWith(JDaveRunner.class)
public class LazyLoadScrollableListSpec extends ComponentSpecification<LazyLoadScrollableListTestPage> {
    List<String> testData = new ArrayList<String>(Arrays.asList("row1", "row2"));
    int blockSize = 4;

    @SuppressWarnings("unchecked")
    @Override
    protected LazyLoadScrollableListTestPage newComponent(final String id, final IModel model) {
        return new LazyLoadScrollableListTestPage(testData, blockSize);
    }

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

    public class WhenDatasetIsLessThanBlockSize {
        public LazyLoadScrollableListTestPage create() {
            return startComponent();
        }

        public void allRowsAreRendered() {
            specify(getRenderedRows().size(), 2);
        }

        public void placeHolderIsHidden() {
            specify(getPlaceholders().get(0).isVisible(), false);
        }
    }

    public class WhenDatasetIsLargerThanBlockSize {
        public LazyLoadScrollableListTestPage create() {
            blockSize = 1;
            return startComponent();
        }

        public void firstBlockIsRendered() {
            specify(getRenderedRows().size(), 1);
        }

        public void placeHolderForFirstBlockIsHidden() {
            specify(getPlaceholders().get(0).isVisible(), false);
        }

        public void placeHoldersForOtherBlocksAreShown() {
            specify(getPlaceholders().get(1).isVisible());
        }

        public void secondBlockIsRenderedWhenClicked() {
            wicket.executeAjaxEvent(getPlaceholders().get(1), "onclick");
            specify(getPlaceholders().get(1).isVisible(), false);
            specify(getRenderedRows().size(), 2);
        }
    }

    public class WhenUpdatingListUsingAjax {
        public LazyLoadScrollableListTestPage create() {
            return startComponent();
        }

        public void contentIsUpdated() {
            String textOnFirstRow = getTextOnRow(0);
            wicket.executeAjaxEvent(selectFirst(AjaxLink.class, "refresh").from(context), "onclick");
            String newTextOnFirstRow = getTextOnRow(0);
            specify(newTextOnFirstRow, must.not().equal(textOnFirstRow));
        }

        private String getTextOnRow(final int row) {
            return getRenderedRows().get(row).getDefaultModelObjectAsString();
        }
    }

    @SuppressWarnings("unchecked")
    private List<PlaceHolder> getPlaceholders() {
        return selectAll(PlaceHolder.class).from(context);
    }

    private List<Label> getRenderedRows() {
        return selectAll(Label.class).from(context);
    }

    @SuppressWarnings("unchecked")
    private LazyLoadScrollableList<String> getScroller() {
        return selectFirst(LazyLoadScrollableList.class).from(context);
    }
}
