package com.project.website.canvas.client.canvastools.textedit;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.html5.Range;
import com.project.shared.client.html5.impl.RangeUtils;
import com.project.shared.client.html5.impl.SelectionImpl;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.StyleUtils;
import com.project.shared.data.funcs.Func;
import com.project.shared.data.funcs.Func.Action;
import com.project.shared.utils.ListUtils;
import com.project.website.canvas.client.resources.CanvasResources;

public class TextEditToolbar extends Composite
{
    private static TextEditToolbarUiBinder uiBinder = GWT.create(TextEditToolbarUiBinder.class);

    interface TextEditToolbarUiBinder extends UiBinder<Widget, TextEditToolbar>
    {
    }

    @UiField
    HTMLPanel rootPanel;
    private Element _editedElement;

    public TextEditToolbar()
    {
        initWidget(uiBinder.createAndBindUi(this));
        initButtons();
    }

    private void initButtons() {
        //setSimpleCssValueButton("fontWeight", "bold", "Bold");
        this.addCssStringValueToggleButton("fontWeight", new String[] {"400", "normal"}, new String[] { "700", "bold" }, "Bold", false);
        this.addCssStringValueToggleButton("fontStyle", "normal", "italic", "Italic", false);
        this.addCssStringValueToggleButton("textDecoration", "none", "underline", "Underline", false);
        this.addCssStringValueListBox("fontFamily", "Font:", getFontFamilies());
        this.addCssStringValueListBox("fontSize", "Size:", getFontSizes());
        this.addCssStringValueListBox("color", "Color:", getColors());
        this.addCssStringValueListBox("backgroundColor", "Background:", getColors());
        this.addCssStringValueToggleButton("direction", "ltr", "rtl", "Direction", true);
    }

    private ArrayList<String> getColors()
    {
        return ListUtils.create("transparent","black","purple","blue","cyan","green","yellow","orange","red","pink","beige","white");
    }

    private Iterable<String> getFontSizes()
    {
        int[] sizes = new int[] {
            8, 10, 12, 14, 16, 18, 20, 24, 28, 32, 36, 48, 72, 96, 144
        };
        ArrayList<String> values = new ArrayList<String>();
        for (int size : sizes) {
            values.add(String.valueOf(size) + "px");
        }
        return values;
    }

    private Iterable<String> getFontFamilies()
    {
        return ListUtils.create(
            "arial",
            "georgia",
            "monospace",
            "verdana",
            "times"
        );
    }

    private void addCssStringValueListBox(final String cssProperty, String title, Iterable<String> values)
    {
        final TextEditToolbar that = this;

        final ListBox listBox = new ListBox();
        listBox.addStyleName(CanvasResources.INSTANCE.main().textEditToolbarListBox());

        for (String item : values)
        {
            listBox.addItem(item);
            // TODO: apply the style on each OptionElement within the list box
        }

        FlowPanel listBoxWrapper = new FlowPanel();
        listBoxWrapper.addStyleName(CanvasResources.INSTANCE.main().textEditToolbarListWrapper());

        InlineLabel titleLabel = new InlineLabel(title);
        titleLabel.addStyleName(CanvasResources.INSTANCE.main().textEditToolbarListTitle());

        listBoxWrapper.add(titleLabel);
        listBoxWrapper.add(listBox);

        this.rootPanel.add(listBoxWrapper);

        final Func<Element, Boolean> isSet = new Func<Element, Boolean>(){
            @Override
            public Boolean call(Element arg)
            {
                String selectedValue = listBox.getValue(listBox.getSelectedIndex());
                return that.isCssPropertySet(cssProperty, new String[] { selectedValue }, arg);
            }
        };
        final Action<Element> setFunc = new Action<Element>(){ @Override public void exec(Element arg) {
            String selectedValue = listBox.getValue(listBox.getSelectedIndex());
            arg.getStyle().setProperty(cssProperty, selectedValue);
        }};
        final Action<Element> unsetFunc = Action.empty();

        listBox.addChangeHandler(new ChangeHandler() {
            @Override public void onChange(ChangeEvent event)
            {
                that.buttonPressed(isSet, setFunc, unsetFunc, false);
            }
        });
    }

    /**
     * A wrapper for {@link #addCssStringValueToggleButton(String, String[], String[], String)},
     * that create arrays with a single value for unset and set value arrays.
     */
    private void addCssStringValueToggleButton(final String cssProperty, final String unsetValue, final String setValue, final String title, boolean onRootElemOnly)
    {
        this.addCssStringValueToggleButton(cssProperty, new String[] { unsetValue },  new String[] { setValue}, title, onRootElemOnly);
    }

    /**
     * Creates a button that toggles a css property between two states. It will use the first value in each of the given arrays (see below) to set the property,
     * and will use the arrays themselves for testing the current value of the property to decide if it is currently set or unset.
     * @param cssProperty Name of property to toggle, in camel case format ("fontStyle", not "font-style")
     * @param unsetValues An array, with at least one element, of equivalent values for the unset state
     * @param setValues An array, with at least one element, of equivalent values for the set state
     * @param title Of the button
     */
    private void addCssStringValueToggleButton(final String cssProperty, final String[] unsetValues, final String[] setValues, final String title, boolean onRootElemOnly)
    {
        assert (unsetValues.length >= 1);
        assert (setValues.length >= 1);

        Widget buttonWidget = createButtonWidget(cssProperty, setValues, title);

        this.addButton(buttonWidget,
            this.getIsCssPropertySetFunc(cssProperty, setValues),
            this.getCssPropertySetterFunc(cssProperty, setValues[0]),
            this.getCssPropertySetterFunc(cssProperty, unsetValues[0]),
            onRootElemOnly);
    }

    private Action<Element> getCssPropertySetterFunc(final String cssProperty, final String value)
    {
        return new Action<Element>(){ @Override public void exec(Element arg) {
            arg.getStyle().setProperty(cssProperty, value);
        }};
    }

    private Func<Element, Boolean> getIsCssPropertySetFunc(final String cssProperty, final String[] setValues)
    {
        return new Func<Element,Boolean>() { @Override
            public Boolean call(Element element) {
                return isCssPropertySet(cssProperty, setValues, element);
            }};
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

    private void addButton(Widget widget, final Func<Element, Boolean> isSet, final Func.Action<Element> setFunc, final Func.Action<Element> unsetFunc, final boolean onRootElemOnly)
    {
        final TextEditToolbar that = this;
        this.rootPanel.add(widget);
        widget.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                that.buttonPressed(isSet, setFunc, unsetFunc, onRootElemOnly);
            }
        }, ClickEvent.getType());
    }

    private void buttonPressed(Func<Element, Boolean> isSet, Action<Element> setFunc, Action<Element> unsetFunc, boolean onRootElemOnly)
    {
        if (null == this.getEditedElement()) {
            return;
        }

        if (onRootElemOnly)
        {
           if (isSet.call(this.getEditedElement()))
           {
               unsetFunc.call(this.getEditedElement());
           }
           else {
               setFunc.call(this.getEditedElement());
           }
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
        this.pushStylesInChildren();
        ElementUtils.mergeSpans(this.getEditedElement());

        this.getEditedElement().focus();
    }

    private void pushStylesInChildren()
    {
        // We really should have run pushStylesDownToTextNodes on the edited
        // element itself rather than iterating and running the code on the
        // children.
        // But in TextEdit that would mean moving properties such as Width and
        // Height down into the children, which isn't what we want.
        for (Node node : ElementUtils.getChildNodes(this.getEditedElement()))
        {
            if (Node.ELEMENT_NODE != node.getNodeType()) {
                continue;
            }
            Element childElem = Element.as(node);
            StyleUtils.pushStylesDownToTextNodes(childElem);
        }
    }

    private Element getEditedElement()
    {
        return this._editedElement;
    }

    public void setEditedElement(Element elem)
    {
        this._editedElement = elem;
    }

    private Boolean isCssPropertySet(final String cssProperty, final String[] setValues, Element element)
    {
        String currentValue = null;
        if (cssProperty.equals("textDecoration")) {
            currentValue = StyleUtils.getInheritedTextDecoration(element);
        }
        else {
            currentValue = StyleUtils.getComputedStyle(element, null).getProperty(cssProperty);
        }
        for (String setValue : setValues)
        {
            if (setValue.equals(""))
            {
                if ((currentValue.equals("inherit") || (currentValue.equals("")))) {
                    // treat empty values as inherit, and only consider "" set if the css property is set or is inherit
                    return true;
                }
                continue;
            }
            if (currentValue.contains(setValue)) {
                return true;
            }
        }
        return false;
    }
}
