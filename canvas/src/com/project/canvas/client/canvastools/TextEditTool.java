package com.project.canvas.client.canvastools;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
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

		@Override
		public String getToolboxIconToolTip() {
			return "Text tool";
		}
	}

	private static final int MINIMUM_EDITBOX_VISIBLE_LENGTH = 10;
	
	private final TextBox editBox = new TextBox();
	private final InlineLabel labelBox = new InlineLabel();
	private final SimpleEvent<String> killRequestEvent = new SimpleEvent<String>();
	
	public TextEditTool() {
		this.addStyleName(CanvasResources.INSTANCE.main().textEdit());
		this.add(editBox);
		this.editBox.setVisibleLength(MINIMUM_EDITBOX_VISIBLE_LENGTH);
		this.registerHandlers();
	}

	private void registerHandlers() {
		this.editBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				editBlur();
			}
		});
		this.labelBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				labelClick(event);
				event.stopPropagation();
			}
		});
		this.editBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
			}
		});
		this.editBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				updateEditBoxVisibleLength();
			}
		});
		this.editBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				updateEditBoxVisibleLength();
			}
		});
	}

	protected void updateEditBoxVisibleLength() {
//		this.editBox.setVisibleLength(Math.max(MINIMUM_EDITBOX_VISIBLE_LENGTH, spareLength));
		autoSizeWidget(this.editBox, this.editBox.getText());
	}

	protected void labelClick(ClickEvent event) {
		// Must be done BEFORE setFocus
		int posX = event.getRelativeX(this.labelBox.getElement());
		int letterPos = estimateCharPos(this.labelBox, posX);
		
		this.setFocus(true);
		
		// Must be done After setFocus
		this.editBox.setCursorPos(letterPos);
	}
	
	/**
	 * Heuristic method to estimate the character position in a text-based widget
	 * assuming that the setText method also causes the widget to resize itself to fit the text.
	 */
	protected static <T extends Widget & HasText> int estimateCharPos(T widget, int relativeX) 
	{
		String text = widget.getText();
		widget.setText("");
		int i;
		for (i = 0; i < text.length(); i++) {
			widget.setText(widget.getText() + text.charAt(i));
			if (relativeX <= widget.getOffsetWidth()) {
				break;
			}
		}
		return i;
	}
	
	protected static void autoSizeWidget(Widget widget, String text) {
		// This code based on:
		// http://stackoverflow.com/questions/1288297/jquery-auto-size-text-input-not-textarea/1288475#1288475
		int comfortZone = 70;
		int minWidth = 0;
		int maxWidth = 1000;
		InlineLabel testWidget = new InlineLabel();
		Style widgetStyle = widget.getElement().getStyle();
		Style targetStyle = testWidget.getElement().getStyle();
		targetStyle.setProperty("fontSize", widgetStyle.getFontSize());
		targetStyle.setProperty("fontWeight", widgetStyle.getFontWeight());
		targetStyle.setProperty("fontFamily", widgetStyle.getProperty("fontFamily"));
		targetStyle.setProperty("letterSpacing", widgetStyle.getProperty("letterSpacing"));
		targetStyle.setProperty("width", "auto");
		targetStyle.setTop(-9999, Unit.PX);
		targetStyle.setLeft(-9999, Unit.PX);
		targetStyle.setProperty("whitespace", "nowrap");
		RootPanel.getBodyElement().appendChild(testWidget.getElement());
		testWidget.setText(text);
		
		int testerWidth = testWidget.getOffsetWidth();
		int newWidth = (testerWidth + comfortZone) >= minWidth ? testerWidth + comfortZone : minWidth;
		int currentWidth = widget.getOffsetWidth();
		boolean isValidWidthChange = (newWidth < currentWidth && newWidth >= minWidth)
									|| (newWidth > minWidth && newWidth < maxWidth);
		
		testWidget.getElement().removeFromParent();
		
		if (isValidWidthChange) {
			widget.setWidth(Integer.toString(newWidth) + "px");
		}
	}

	protected void editBlur() {
		this.setFocus(false);
	}

	@Override
	public void setFocus(boolean isFocused) {
		if (isFocused) {
			this.addStyleName(CanvasResources.INSTANCE.main().textEditEditing());
			this.remove(this.labelBox);
			this.add(this.editBox);
			this.editBox.setFocus(true);
		}
		else {
			this.removeStyleName(CanvasResources.INSTANCE.main().textEditEditing());
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
