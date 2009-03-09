package org.laughingpanda.wicket;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;

public class LazyLoadScrollableListTestPage extends WebPage {
    public LazyLoadScrollableListTestPage(final List<String> data, final int blockSize) {
        final LazyLoadScrollableList<String> scroller = new LazyLoadScrollableList<String>("djuizyScroller", new ListDataProvider(data), blockSize) {
            @Override
            protected void populateRow(final WebMarkupContainer rowContainer, final String modelObject) {
                rowContainer.add(new Label("rowLabel", new Model(modelObject)));
            }
        };        
        scroller.setOutputMarkupId(true);
        add(scroller);
        add(new AjaxLink("refresh") {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                data.clear();
                data.add("lol");
                target.addComponent(scroller);
            }
        });
    }
}