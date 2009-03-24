package org.laughingpanda.oegyscrolldemo;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;
import org.laughingpanda.wicket.LazyLoadScrollableList;

public class OegyScrollDemoPage extends WebPage {
    static int counter = 0;

    private static List<String> sampleData() {
        int count = ++counter;
        List<String> data = new ArrayList<String>();
        for (int i = 0; i < 21111; i++) {
            data.add("Row " + i + " of dataset " + count);
        }
        return data;
    }

    public OegyScrollDemoPage() {
        this(sampleData(), 100);
    }

    public OegyScrollDemoPage(final List<String> data, final int blockSize) {
        final LazyLoadScrollableList<String> scroller = new LazyLoadScrollableList<String>("djuizyScroller", new ListDataProvider(data), blockSize) {
            @Override
            protected void populateRow(final WebMarkupContainer rowContainer, final String modelObject) {
                rowContainer.add(new Label("rowLabel", new Model<String>(modelObject)));
            }
        };
        add(scroller);
        add(new AjaxLink<Object>("refresh") {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                data.clear();
                data.addAll(sampleData());
                target.addComponent(scroller);
            }
        });
    }
}
