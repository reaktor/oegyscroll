package org.laughingpanda.wicket;

import java.io.Serializable;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;

class PlaceHolder<T extends Serializable> extends WebMarkupContainer {
    public PlaceHolder(final Block<T> block) {
        super("placeholder");
        add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(final AjaxRequestTarget target) {
                block.showRows(target);
            }
        });
        add(new AttributeModifier("class", true, new Model<String>("loader-placeholder")));
    }
}
