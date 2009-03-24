package org.laughingpanda.wicket;

import org.apache.wicket.markup.html.WebMarkupContainer;

class ScrolledContentView extends WebMarkupContainer {
    public ScrolledContentView(final String id) {
        super(id);
        setOutputMarkupId(true);
    }
}
