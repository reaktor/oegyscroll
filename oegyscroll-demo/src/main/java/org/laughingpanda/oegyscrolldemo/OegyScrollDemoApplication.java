package org.laughingpanda.oegyscrolldemo;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

public class OegyScrollDemoApplication extends WebApplication {
	@Override
	public Class<? extends Page> getHomePage() {
		return OegyScrollDemoPage.class;
	}
}
