package org.laughingpanda.wicket;

import static java.util.Collections.emptyList;

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
    List<String> testData = emptyList();

	private void createTestData(int rowCount) {
		testData = new ArrayList<String>(rowCount);
		for (int i = 0; i < rowCount ; i++) {
			testData.add(new String("row" + i));
		}
	}
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


    public class WhenUpdatingListUsingAjax {
        public LazyLoadScrollableListTestPage create() {
        	createTestData(2);
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
}
