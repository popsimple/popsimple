package com.project.canvas.client.canvastools.TextEdit;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.canvas.client.gwtsamples.text.RichTextToolbar;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.TextData;

public class TextEditTool extends FlowPanel implements CanvasTool<TextData> {
	private final FlowPanel innerPanel = new FlowPanel();
	private final RichTextArea editBox;
	private final RichTextToolbar toolbar; 
	private final SimpleEvent<String> killRequestEvent = new SimpleEvent<String>();
	private TextData data;
	protected boolean toolBarFocused;
	
	public TextEditTool() {
		CanvasToolCommon.initCanvasToolWidget(this);
		this.data = new TextData();
		this.add(innerPanel);
		this.editBox = GWT.create(RichTextArea.class);
		this.toolbar = new  RichTextToolbar(editBox);		
		this.toolbar.addStyleName(CanvasResources.INSTANCE.main().textEditToolbar());
		GWT.log("creating TextEditTool");
		this.editBox.addInitializeHandler(new InitializeHandler() {
			@Override
			public void onInitialize(InitializeEvent event) {
				GWT.log("initialized editbox");
				CanvasToolCommon.addEscapeUnfocusesHandler(editBox);
				innerPanel.add(toolbar);
				editBox.addStyleName(CanvasResources.INSTANCE.main().textEdit());
				editBox.addStyleName(CanvasResources.INSTANCE.main().textEditFocused());
				registerHandlers();
			}
		});
		this.innerPanel.add(editBox);
	}

	private void registerHandlers() {
		this.toolbar.addDomHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				updateEditBoxVisibleLength();
				setSelfFocus(true);
				editBox.setFocus(true);
				event.preventDefault();
			}}, ClickEvent.getType());
		this.editBox.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				GWT.log("editbox focus");
				setSelfFocus(true);
			}
		});
		this.editBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				GWT.log("editbox blur");
				setSelfFocus(false);
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

	@Override
	public void setFocus(final boolean isFocused) {
		GWT.log("setting focus " + isFocused + " on TextEditTool");
		setSelfFocus(isFocused);
		this.editBox.setFocus(isFocused);
		// In case this was run before the editbox was initialized:
		this.editBox.addInitializeHandler(new InitializeHandler() {
			@Override
			public void onInitialize(InitializeEvent event) {
				GWT.log("setting focus " + isFocused + " on editbox in initializer");
				editBox.setFocus(isFocused);
			}
		});
	}

	private void setSelfFocus(boolean isFocused) {
		if (isFocused) {
			updateEditBoxVisibleLength();
			this.editBox.addStyleName(CanvasResources.INSTANCE.main().textEditFocused());
			this.editBox.removeStyleName(CanvasResources.INSTANCE.main().textEditNotFocused());
		}
		else {
			this.editBox.removeStyleName(CanvasResources.INSTANCE.main().textEditFocused());
			this.editBox.addStyleName(CanvasResources.INSTANCE.main().textEditNotFocused());
			// use getText rather than getHTML, so that if
			// there is no text in the box - it will be destroyed
			String text = this.editBox.getText();
			if (text.trim().isEmpty()) {
				this.killRequestEvent.dispatch("Empty");
			}
		}
		
		this.toolbar.setVisible(isFocused);
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
		this.data.text = this.editBox.getHTML();
		return this.data;
	}

	@Override
	public void setValue(TextData data) {
		this.data = data;
		this.editBox.setHTML(this.data.text);
		this.updateEditBoxVisibleLength();
	}

	@Override
	public void setElementData(ElementData data) {
		this.setValue((TextData) data);
	}
}
