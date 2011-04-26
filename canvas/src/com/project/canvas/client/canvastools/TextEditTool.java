package com.project.canvas.client.canvastools;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextBox;
import com.project.canvas.client.events.SimpleEvent;
import com.project.canvas.client.resources.CanvasResources;

public class TextEditTool extends FlowPanel implements CanvasTool {
	public static class Maker implements CanvasToolFactory<TextEditTool> {
		@Override
		public String getToolboxIconStyle() {
			return CanvasResources.INSTANCE.main().toolboxTextIconStyle();
		}

		@Override
		public String getCanvasStyleInCreateMode() {
			return CanvasResources.INSTANCE.main().textBoxCreateModeCanvasStyle();
		}

		@Override
		public String getDragIconStyle() {
			return "";
		}

		@Override
		public TextEditTool create() {
			return new TextEditTool();
		}
	}
	
	private final TextBox editBox = new TextBox();
	private final InlineLabel labelBox = new InlineLabel();
	private final SimpleEvent<String> killRequestEvent = new SimpleEvent<String>();
	
	public TextEditTool() {
		this.addStyleName(CanvasResources.INSTANCE.main().textEdit());
		this.add(editBox);
		
		this.editBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				editBlur();
			}
		});
		this.labelBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				labelClick();
			}
		});
	}

	protected void labelClick() {
		this.setFocus(true);
	}

	protected void editBlur() {
		this.setFocus(false);
	}

	@Override
	public void setFocus(boolean isFocused) {
		if (isFocused) {
			this.remove(this.labelBox);
			this.add(this.editBox);
			this.editBox.setFocus(true);
		}
		else {
			String text = this.editBox.getText();
			if (text.trim().isEmpty()) {
				this.killRequestEvent.dispatch("Empty");
			}
			this.labelBox.setText(text);
			this.remove(this.editBox);
			this.add(this.labelBox);
		}
	}

	@Override
	public SimpleEvent<String> getKillRequestedEvent() {
		return this.killRequestEvent;
	}

	@Override
	public int getTabIndex() {
		return this.editBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		this.editBox.setAccessKey(key);
	}

	@Override
	public void setTabIndex(int index) {
		this.editBox.setTabIndex(index);
	}
}
