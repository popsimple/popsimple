package com.project.canvas.client.canvastools.TextEdit;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.events.SimpleEvent;

public class TextEditTool extends FlowPanel implements CanvasTool {
	
	private final TextArea editBox = new TextArea();
	private final Label labelBox = new Label();
	private final SimpleEvent<String> killRequestEvent = new SimpleEvent<String>();
	private boolean editing = false;
	
	public TextEditTool() {
		CanvasToolCommon.initCanvasToolWidget(this);
		this.addStyleName(CanvasResources.INSTANCE.main().textEdit());
		this.add(editBox);
		this.editBox.addStyleName(CanvasResources.INSTANCE.main().textEditEditBox());
		this.labelBox.addStyleName(CanvasResources.INSTANCE.main().textEditLabelBox());
		this.registerHandlers();
	}

	private void registerHandlers() {
		this.editBox.addBlurHandler(new BlurHandler() {
			public void onBlur(BlurEvent event) {
				editBlur();
			}
		});
		this.labelBox.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				setFocus(true);
				event.stopPropagation();
			}
		});
		this.labelBox.addMouseOverHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				add(editBox);
				remove(labelBox);
				editBox.setFocus(false);
			}
		});
		this.editBox.addMouseOutHandler(new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				if ((false == editing) && (0 == editBox.getSelectionLength())) {
					remove(editBox);
					add(labelBox);
				}
			}
		});
		this.editBox.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				event.stopPropagation();
			}
		});
		this.editBox.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				editing = true;
			}
		});
		this.editBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			public void onValueChange(ValueChangeEvent<String> event) {
				updateEditBoxVisibleLength();
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
		autoSizeWidget(this.editBox, this.editBox.getText(), true);
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
	
	static final InlineLabel testWidget = new InlineLabel();
	static boolean testWidgetInit = false;
	protected static void autoSizeWidget(Widget widget, String text, boolean usePreWhiteSpace) {
		Style targetStyle = testWidget.getElement().getStyle();
		if (false == testWidgetInit)
		{
			targetStyle.setProperty("width", "auto");
			targetStyle.setProperty("height", "auto");
			targetStyle.setTop(-9999, Unit.PX);
			targetStyle.setLeft(-9999, Unit.PX);
			RootPanel.getBodyElement().appendChild(testWidget.getElement());
			testWidgetInit = true;
		}
		
		// This code based on:
		// http://stackoverflow.com/questions/1288297/jquery-auto-size-text-input-not-textarea/1288475#1288475
		int comfortZone = 40;
		int minWidth = 0;
		Style widgetStyle = widget.getElement().getStyle();
		copyTextSizingProps(targetStyle, widgetStyle);
		if (usePreWhiteSpace) {
			targetStyle.setProperty("whiteSpace", "pre");
		}
		
		// append a char after every newline. fixes some PRE formatting bugs (esp. last empty line)
		text.replace("\n", "\nM");
		// Also prepend a character 
		// (if the text begins with whitespace the browser may strip it in the test widget)
		testWidget.setText("M" + text + "M");
		
		int testerWidth = testWidget.getOffsetWidth();
		int newWidth = (testerWidth + comfortZone) >= minWidth ? testerWidth + comfortZone : minWidth;
		int currentWidth = widget.getOffsetWidth();
		boolean isValidWidthChange = (newWidth < currentWidth && newWidth >= minWidth)
									|| (newWidth > minWidth);
		
		int newHeight = testWidget.getOffsetHeight();
		
		widget.setHeight(Integer.toString(newHeight) + "px");
		if (isValidWidthChange) {
			widget.setWidth(Integer.toString(newWidth) + "px");
		}
		testWidget.setText(""); // for security reasons don't leave hiding data...
	}

	private static void copyTextSizingProps(Style targetStyle, Style widgetStyle) {
		String[] copyProps = new String[] {
				"fontFamily",	
				"fontSize",	
				"fontWeight",
				"fontStyle",	
				"textTransform",
				"textDecoration",
				"letterSpacing",
				"wordSpacing",
				"lineHeight",
				"textAlign",	
				"verticalAlign",	
				"direction",
				"padding",
				"border",
				"margin",
				"whiteSpace"
		};
		for (String propName : copyProps) {
			targetStyle.setProperty(propName, widgetStyle.getProperty(propName));
		}
	}

	protected void editBlur() {
		this.setFocus(false);
	}

	public void setFocus(boolean isFocused) {
		this.editing = isFocused;
		if (isFocused) {
			this.remove(this.labelBox);
			this.add(this.editBox);
			updateEditBoxVisibleLength();
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
}
