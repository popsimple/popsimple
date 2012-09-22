package com.project.website.canvas.client.pages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AboutUs extends Composite {

	private static AboutUsUiBinder uiBinder = GWT.create(AboutUsUiBinder.class);

	interface AboutUsUiBinder extends UiBinder<Widget, AboutUs> {
	}

	public AboutUs() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
