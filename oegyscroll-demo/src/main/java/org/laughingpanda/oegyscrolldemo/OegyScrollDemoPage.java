package org.laughingpanda.oegyscrolldemo;

import java.util.Iterator;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.laughingpanda.wicket.LazyLoadScrollableList;

public class OegyScrollDemoPage extends WebPage {
    private final class SampleDataProvider implements
			IDataProvider<String> {
		private final int size;

		private SampleDataProvider(int size) {
			this.size = size;
		}

		public Iterator<? extends String> iterator(final int first, final int count) {
			return new Iterator<String>() {
				int index = first;

				public boolean hasNext() {
					return index < first + count;
				}

				public String next() {
					try {
						return "Row " + index + " of sample data set";						
					} finally {
						index++;
					}
				}

				public void remove() {
					throw new UnsupportedOperationException();
				}};
		}

		public IModel<String> model(String object) {
			return new Model<String>(object);
		}

		public int size() {
			return size;
		}

		public void detach() {
		}
	}

	static int counter = 0;


    public OegyScrollDemoPage() {
        this(100000, 100);
    }

    public OegyScrollDemoPage(final int size, final int blockSize) {
        final LazyLoadScrollableList<String> scroller = new LazyLoadScrollableList<String>("djuizyScroller", new SampleDataProvider(size), blockSize) {
            @Override
            protected void populateRow(final WebMarkupContainer rowContainer, final int index, final String modelObject) {
                rowContainer.add(new Label("rowLabel", new Model<String>(modelObject)));
            }
        };
        add(scroller);
        
        add(new AjaxLink<Object>("refresh") {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                target.addComponent(scroller);
            }
        });
    }
}
