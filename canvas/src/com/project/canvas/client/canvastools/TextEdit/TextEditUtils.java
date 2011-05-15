package com.project.canvas.client.canvastools.TextEdit;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class TextEditUtils {

	static final HTMLPanel testWidget = new HTMLPanel("");
	static boolean testWidgetInit = false;

	protected static void autoSizeWidget(Widget widget, String html, boolean usePreWhiteSpace) {
		Style targetStyle = testWidget.getElement().getStyle();
		if (false == testWidgetInit)
		{
			targetStyle.setProperty("width", "auto");
			targetStyle.setProperty("height", "auto");
			targetStyle.setTop(-9999, Unit.PX);
			targetStyle.setLeft(-9999, Unit.PX);
			targetStyle.setDisplay(Display.INLINE_BLOCK);
			RootPanel.getBodyElement().appendChild(testWidget.getElement());
			testWidgetInit = true;
		}
		
		// This code based on:
		// http://stackoverflow.com/questions/1288297/jquery-auto-size-text-input-not-textarea/1288475#1288475
		int comfortZone = 40;
		int minWidth = 0;
		Style widgetStyle = widget.getElement().getStyle();
		copyTextSizingProps(targetStyle, widgetStyle);
		targetStyle.setFontSize(18.0, Unit.PX);
		if (usePreWhiteSpace) {
			targetStyle.setProperty("whiteSpace", "pre");
		}
		
		// append a char after every newline. fixes some PRE formatting bugs (esp. last empty line)
		//text.replace("\n", "\nM");
		// Also prepend a character 
		// (if the text begins with whitespace the browser may strip it in the test widget)
		testWidget.getElement().setInnerHTML("M" + html + "M");
		
		int testerWidth = testWidget.getOffsetWidth();
		int newWidth = (testerWidth + comfortZone) >= minWidth ? testerWidth + comfortZone : minWidth;
		int currentWidth = widget.getOffsetWidth();
		boolean isValidWidthChange = (newWidth < currentWidth && newWidth >= minWidth)
									|| (newWidth > minWidth);
		
		int newHeight = testWidget.getOffsetHeight(); // always add a spare
		
		widget.setHeight(Integer.toString(newHeight) + "px");
		if (isValidWidthChange) {
			widget.setWidth(Integer.toString(newWidth) + "px");
		}
		testWidget.getElement().setInnerHTML(""); // for security reasons don't leave hiding data...
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

}
