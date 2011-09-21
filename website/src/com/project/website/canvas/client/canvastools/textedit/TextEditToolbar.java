package com.project.website.canvas.client.canvastools.textedit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.html5.Range;
import com.project.shared.client.html5.impl.RangeUtils;
import com.project.shared.client.html5.impl.SelectionImpl;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.StyleUtils;
import com.project.shared.data.Rectangle;
import com.project.shared.data.funcs.Func;
import com.project.shared.data.funcs.Func.Action;
import com.project.website.canvas.client.resources.CanvasResources;

public class TextEditToolbar extends Composite
{

    private static TextEditToolbarUiBinder uiBinder = GWT.create(TextEditToolbarUiBinder.class);

    interface TextEditToolbarUiBinder extends UiBinder<Widget, TextEditToolbar>
    {
    }

    private final RegistrationsManager registrationsManager = new RegistrationsManager();

    @UiField
    HTMLPanel rootPanel;

    private Widget _editedWidget = null;

    public TextEditToolbar()
    {
        initWidget(uiBinder.createAndBindUi(this));
        initButtons();
    }

    private void initButtons() {
        //setSimpleCssValueButton("fontWeight", "bold", "Bold");
        this.setCssStringValueToggleButton("fontWeight", new String[] {"400", "normal"}, new String[] { "700", "bold" }, "Bold");
        this.setCssStringValueToggleButton("fontStyle", "normal", "italic", "Italic");
        this.setCssStringValueToggleButton("textDecoration", "none", "underline", "Underline");
    }

    public void setEditedWidget(Widget widget)
    {
        if (this._editedWidget == widget)
        {
            return;
        }
        this._editedWidget = widget;
        this.setVisible(null != this._editedWidget);
        this.registrationsManager.clear();

        if (null == this._editedWidget)
        {
            return;
        }

        final TextEditToolbar that = this;
        this.registrationsManager.add(this._editedWidget.addDomHandler(new MouseDownHandler() {
            @Override public void onMouseDown(MouseDownEvent event) {
                that.updatePosition();
            }}, MouseDownEvent.getType()));
        this.registrationsManager.add(this._editedWidget.addDomHandler(new MouseUpHandler() {
            @Override public void onMouseUp(MouseUpEvent event) {
                that.updatePosition();
            }}, MouseUpEvent.getType()));
        this.registrationsManager.add(this._editedWidget.addDomHandler(new KeyDownHandler() {
            @Override public void onKeyDown(KeyDownEvent event) {
                that.updatePosition();
            }}, KeyDownEvent.getType()));
        this.registrationsManager.add(this._editedWidget.addDomHandler(new FocusHandler() {
            @Override public void onFocus(FocusEvent event) {
                that.updatePosition();
            }}, FocusEvent.getType()));
    }

    protected void updatePosition()
    {
        Rectangle elementRect = ElementUtils.getElementAbsoluteRectangle(this._editedWidget.getElement());
        this.getElement().getStyle().setTop(elementRect.getBottom() + 10, Unit.PX);
        this.getElement().getStyle().setLeft(elementRect.getLeft(), Unit.PX);
    }

    /**
     * A wrapper for {@link #setCssStringValueToggleButton(String, String[], String[], String)},
     * that create arrays with a single value for unset and set value arrays.
     */
    private void setCssStringValueToggleButton(final String cssProperty, final String unsetValue, final String setValue, final String title)
    {
        this.setCssStringValueToggleButton(cssProperty, new String[] { unsetValue },  new String[] { setValue}, title);
    }

    /**
     * Creates a button that toggles a css property between two states. It will use the first value in each of the given arrays (see below) to set the property,
     * and will use the arrays themselves for testing the current value of the property to decide if it is currently set or unset.
     * @param cssProperty Name of property to toggle, in camel case format ("fontStyle", not "font-style")
     * @param unsetValues An array, with at least one element, of equivalent values for the unset state
     * @param setValues An array, with at least one element, of equivalent values for the set state
     * @param title Of the button
     */
    private void setCssStringValueToggleButton(final String cssProperty, final String[] unsetValues, final String[] setValues, final String title)
    {
        assert (unsetValues.length >= 1);
        assert (setValues.length >= 1);

        Widget buttonWidget = createButtonWidget(cssProperty, setValues, title);

        this.addButton(buttonWidget, new Func<Element,Boolean>() { @Override
            public Boolean call(Element arg) {
                String currentValue = null;
                if (cssProperty.equals("textDecoration")) {
                    currentValue = StyleUtils.getInheritedTextDecoration(arg);
                }
                else {
                    currentValue = StyleUtils.getComputedStyle(arg, null).getProperty(cssProperty);
                }
                for (String setValue : setValues)
                {
                    if (currentValue.contains(setValue)) {
                        return true;
                    }
                }
                return false;
            }},
            new Action<Element>(){ @Override public void exec(Element arg) {
                arg.getStyle().setProperty(cssProperty, setValues[0]);
            }},
            new Action<Element>(){ @Override public void exec(Element arg) {
                arg.getStyle().setProperty(cssProperty, unsetValues[0]);
            }});
    }

    private Widget createButtonWidget(final String cssProperty, final String[] setValues, final String title)
    {
        // Must be a button element, otherwise when it's clicked it will remove the current selection because the browser
        // will see it as clicking on text.
        Button buttonWidget = new Button();
        buttonWidget.getElement().getStyle().setProperty(cssProperty, setValues[0]);
        buttonWidget.getElement().setInnerText(title);
        buttonWidget.addStyleName(CanvasResources.INSTANCE.main().textEditToolbarToggleButton());
        return buttonWidget;
    }

    protected void addButton(Widget widget, final Func<Element, Boolean> isSet, final Func.Action<Element> setFunc, final Func.Action<Element> unsetFunc)
    {
        final TextEditToolbar that = this;
        this.rootPanel.add(widget);
        widget.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                that.buttonPressed(isSet, setFunc, unsetFunc);
            }
        }, ClickEvent.getType());
    }

    protected void buttonPressed(Func<Element, Boolean> isSet, Action<Element> setFunc, Action<Element> unsetFunc)
    {
        if (null == this._editedWidget) {
            return;
        }

        SelectionImpl selection = SelectionImpl.getWindowSelection();
        Node anchorNode = selection.getAnchorNode();
        if (null == anchorNode) {
            return;
        }
        Element elem = anchorNode.getParentElement();
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
            RangeUtils.applyToNodesInRange(range, action);
        }


        // TODO this kills the range's validity...
        StyleUtils.pushStylesDownToTextNodes(_editedWidget.getElement());
        ElementUtils.mergeSpans(_editedWidget.getElement());
        this._editedWidget.getElement().focus();
    }
}
