package com.project.canvas.client.canvastools.TextEdit;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.canvas.client.gwtsamples.text.RichTextToolbar;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.TextData;

public class TextEditTool extends FormPanel implements CanvasTool<TextData> {
	private final FlowPanel innerPanel = new FlowPanel();
	private final RichTextArea editBox = new RichTextArea();
	private final RichTextToolbar toolbar = new  RichTextToolbar(editBox); 
	private final SimpleEvent<String> killRequestEvent = new SimpleEvent<String>();
	private TextData data;
	
	public TextEditTool() {
		CanvasToolCommon.initCanvasToolWidget(this);
		this.data = new TextData();
		this.editBox.addStyleName(CanvasResources.INSTANCE.main().textEdit());
		this.innerPanel.add(toolbar);
		this.innerPanel.add(editBox);
		this.add(innerPanel);
		this.toolbar.addStyleName(CanvasResources.INSTANCE.main().textEditToolbar());
		this.editBox.addStyleName(CanvasResources.INSTANCE.main().textEditFocused());
		this.registerHandlers();
	}

	private void registerHandlers() {
		this.addDomHandler(new BlurHandler(){
			@Override
			public void onBlur(BlurEvent event) {
				setFocus(false);
			}}, BlurEvent.getType());
		this.editBox.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				setFocus(true);
				event.stopPropagation();
			}
		});

		this.editBox.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				setFocus(true);
			}
		});
		this.editBox.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				updateEditBoxVisibleLength();
			}
		});
		this.editBox.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				updateEditBoxVisibleLength();
			}
		});
	}

	protected void updateEditBoxVisibleLength() {
//		this.editBox.setVisibleLength(Math.max(MINIMUM_EDITBOX_VISIBLE_LENGTH, spareLength));
		
		TextEditUtils.autoSizeWidget(this.editBox, this.editBox.getHTML(), true);
	}

	
	public void setFocus(boolean isFocused) {
		this.toolbar.setVisible(isFocused);

		if (isFocused) {
			updateEditBoxVisibleLength();
			this.editBox.setFocus(true);
			this.editBox.addStyleName(CanvasResources.INSTANCE.main().textEditFocused());
			this.editBox.removeStyleName(CanvasResources.INSTANCE.main().textEditNotFocused());
		}
		else {
			this.editBox.setFocus(false);
			this.editBox.removeStyleName(CanvasResources.INSTANCE.main().textEditFocused());
			this.editBox.addStyleName(CanvasResources.INSTANCE.main().textEditNotFocused());
			String text = this.editBox.getText();
			if (text.trim().isEmpty()) {
				this.killRequestEvent.dispatch("Empty");
			}
		}
	}

	public SimpleEvent<String> getKillRequestedEvent() {
		return this.killRequestEvent;
	}

	public int getTabIndex() {
		return this.editBox.getTabIndex();
	}

	public void setAccessKey(char key) {
		this.editBox.setAccessKey(key);
	}

	public void setTabIndex(int index) {
		this.editBox.setTabIndex(index);
	}


	@Override
	public TextData getValue() {
		this.data.text = this.editBox.getText();
		return this.data;
	}

	@Override
	public void setValue(TextData data) {
		this.data = data;
		this.editBox.setText(this.data.text);
		this.updateEditBoxVisibleLength();
	}

	@Override
	public void setElementData(ElementData data) {
		this.setValue((TextData) data);
	}
}
