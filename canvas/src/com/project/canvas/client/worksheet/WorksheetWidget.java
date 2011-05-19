package com.project.canvas.client.worksheet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class WorksheetWidget extends Composite {
    private static WorksheetWidgetUiBinder uiBinder = GWT.create(WorksheetWidgetUiBinder.class);

    interface WorksheetWidgetUiBinder extends UiBinder<Widget, WorksheetWidget> {
    }

	@UiField
	protected FlowPanel worksheetPanel;
	@UiField
	protected Button saveButton;
	@UiField
	protected TextBox loadIdBox;
	@UiField
	protected Button loadButton;
	@UiField
	protected Button viewButton;
	@UiField
	HTMLPanel worksheetContainer;
	@UiField
	protected HTMLPanel worksheetHeader;
	@UiField
	protected Anchor optionsBackground;
	@UiField
	protected FlowPanel worksheetBackground;
	@UiField
	protected HTMLPanel dragPanel;

    public WorksheetWidget() {
        initWidget(uiBinder.createAndBindUi(this));
    }
}
