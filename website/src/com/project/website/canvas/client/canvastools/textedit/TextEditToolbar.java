package com.project.website.canvas.client.canvastools.textedit;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.html5.Range;
import com.project.shared.client.html5.impl.RangeImpl;
import com.project.shared.client.html5.impl.RangeUtils;
import com.project.shared.client.html5.impl.SelectionImpl;
import com.project.shared.client.utils.StyleUtils;
import com.project.shared.data.funcs.Func;
import com.project.shared.data.funcs.Func.Action;

public class TextEditToolbar extends Composite
{

    private static TextEditToolbarUiBinder uiBinder = GWT.create(TextEditToolbarUiBinder.class);

    interface TextEditToolbarUiBinder extends UiBinder<Widget, TextEditToolbar>
    {
    }


    @UiField
    HTMLPanel rootPanel;
    private Element _element;

    public TextEditToolbar()
    {
        initWidget(uiBinder.createAndBindUi(this));
        initButtons();
    }

    private void initButtons() {
        //setSimpleCssValueButton("fontWeight", "bold", "Bold");
        this.addSimpleIntegerCssValueButton("fontWeight", 400, 700, "Bold");
        this.setSimpleStringCssValueButton("fontStyle", "normal", "italic", "Italic");

        this.setSimpleStringCssValueButton("textDecoration", "none", "underline", "Underline");
    }

    private void addSimpleIntegerCssValueButton(final String cssAttribute, final int unsetValue, final int setValue, String title)
    {
        this.addButton(new Button(title), new Func<Element,Boolean>() { @Override
            public Boolean call(Element arg) {
                Integer currentValue = getNumericalComputedCssProperty(cssAttribute, arg);
                if ((null == currentValue) || (setValue != currentValue)) {
                    return false;
                }
                return true;
            }},
            new Action<Element>(){ @Override public void exec(Element arg) {
                arg.getStyle().setProperty(cssAttribute, String.valueOf(setValue));
            }},
            new Action<Element>(){ @Override public void exec(Element arg) {
                arg.getStyle().setProperty(cssAttribute, String.valueOf(unsetValue));
            }});
    }

    private void setSimpleStringCssValueButton(final String cssAttribute, final String unsetValue, final String cssValue, final String title)
    {
        this.addButton(new Button(title), new Func<Element,Boolean>() { @Override
            public Boolean call(Element arg) {
                String currentValue = null;
                if (cssAttribute.equals("textDecoration")) {
                    currentValue = StyleUtils.getInheritedTextDecoration(arg);
                }
                else {
                    currentValue = StyleUtils.getComputedStyle(arg, null).getProperty(cssAttribute);
                }
                return currentValue.contains(cssValue);
            }},
            new Action<Element>(){ @Override public void exec(Element arg) {
                arg.getStyle().setProperty(cssAttribute, cssValue);
            }},
            new Action<Element>(){ @Override public void exec(Element arg) {
                arg.getStyle().setProperty(cssAttribute, unsetValue);
            }});
    }

    public void setEditedElement(Element element)
    {
        this._element = element;
    }

    protected <T extends HasClickHandlers & IsWidget> void addButton(T widget, final Func<Element, Boolean> isSet, final Func.Action<Element> setFunc, final Func.Action<Element> unsetFunc)
    {
        final TextEditToolbar that = this;
        this.rootPanel.add(widget);
        widget.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                that.buttonPressed(isSet, setFunc, unsetFunc);
            }
        });
    }

    protected void buttonPressed(Func<Element, Boolean> isSet, Action<Element> setFunc, Action<Element> unsetFunc)
    {
        if (null == this._element) {
            return;
        }

        ArrayList<RangeImpl> updatedRanges = new ArrayList<RangeImpl>();
        SelectionImpl selection = SelectionImpl.getWindowSelection();
        Node focusNode = selection.getFocusNode();
        if (null == focusNode) {
            return;
        }
        Element elem = focusNode.getParentElement();
        boolean isSetInFocusNode = null == elem ? false : isSet.call(elem);
        for (int i = 0 ; i < selection.getRangeCount(); i++) {
            Range range = selection.getRangeAt(i);
            Action<Element> action = null;
            if (isSetInFocusNode) {
                action = unsetFunc;
            }
            else {
                action = setFunc;
            }
            updatedRanges.add(RangeUtils.applyToNodesInRange(range, action));
        }
        selection.removeAllRanges();
        for (RangeImpl range : updatedRanges) {
            selection.addRangeNative(range);
        }

        // TODO this kills the range's validity...
        StyleUtils.pushStylesDownToTextNodes(this._element);

        this._element.focus();
    }

    private Integer getNumericalComputedCssProperty(final String cssAttribute, Element arg)
    {
        String currentValueStr = StyleUtils.getComputedStyle(arg, null).getProperty(cssAttribute);
        try {
            return Integer.parseInt(currentValueStr);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

}
