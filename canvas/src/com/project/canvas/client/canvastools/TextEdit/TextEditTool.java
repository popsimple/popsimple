package com.project.canvas.client.canvastools.TextEdit;


import com.axeiya.gwtckeditor.client.CKConfig;
import com.axeiya.gwtckeditor.client.CKConfig.PRESET_TOOLBAR;
import com.axeiya.gwtckeditor.client.CKEditor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.TextData;

public class TextEditTool extends FlowPanel implements CanvasTool<TextData> {
	private final FlowPanel innerPanel = new FlowPanel();
	private final CKEditor editBox;
	private final SimpleEvent<String> killRequestEvent = new SimpleEvent<String>();
	private TextData data;
	protected boolean toolBarFocused;
	private int index;
	
	public TextEditTool() {
		CanvasToolCommon.initCanvasToolWidget(this);
		this.data = new TextData();
		this.add(innerPanel);
		
		CKConfig editBoxConfig = new CKConfig(PRESET_TOOLBAR.BASIC);
		editBoxConfig.setRemovePlugins("elementspath");
		editBoxConfig.setWidth("400px");
		editBoxConfig.setHeight("100px");
		this.editBox = new CKEditor(editBoxConfig);
		this.editBox.setHTML("");
		//editBox.addStyleName(CanvasResources.INSTANCE.main().textEdit());
		//editBox.addStyleName(CanvasResources.INSTANCE.main().textEditFocused());
		this.innerPanel.add(editBox);
		registerHandlers();
//		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
//			@Override
//			public void execute() {
//				registerHandlers();
//			}
//		});
	}

	private void registerHandlers() {
		//ElementWrapper editorWrapper = new ElementWrapper(this.editBox.getEditorElement());
		//editorWrapper.addDomHandler(new BlurHandler() {
//			@Override
//			public void onBlur(BlurEvent event) {
//				GWT.log("editbox blur");
//				setSelfFocus(false);
//			}
//		}, BlurEvent.getType());
		this.editBox.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
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
//		this.editBox.addKeyUpHandler(new KeyUpHandler() {
//			public void onKeyUp(KeyUpEvent event) {
//				updateEditBoxVisibleLength();
//			}
//		});
//		this.editBox.addKeyDownHandler(new KeyDownHandler() {
//			public void onKeyDown(KeyDownEvent event) {
//				updateEditBoxVisibleLength();
//			}
//		});
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
			HTML editorHTML = new HTML(this.editBox.getData());
			String text = editorHTML.getText().trim();
			text = text.replace(new String(new char[]{(char)160}), "");
			if (text.isEmpty()) {
				this.killRequestEvent.dispatch("Empty");
			}
		}
	}

	public SimpleEvent<String> getKillRequestedEvent() {
		return this.killRequestEvent;
	}

	public int getTabIndex() {
		return this.index;
	}

	public void setAccessKey(char key) {
	}

	public void setTabIndex(int index) {
		this.index = index;
	}


	@Override
	public TextData getValue() {
		this.data._text = this.editBox.getHTML();
		return this.data;
	}

	@Override
	public void setValue(TextData data) {
		this.data = data;
		this.editBox.setHTML(this.data._text);
		this.updateEditBoxVisibleLength();
	}

	@Override
	public void setElementData(ElementData data) {
		this.setValue((TextData) data);
	}
}
