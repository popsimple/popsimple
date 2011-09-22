package com.project.website.canvas.client.canvastools.textedit;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.html5.Range;
import com.project.shared.client.html5.impl.RangeUtils;
import com.project.shared.client.html5.impl.SelectionImpl;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.StyleUtils;
import com.project.shared.client.utils.widgets.ListBoxUtils;
import com.project.shared.data.funcs.Func.Action;
import com.project.shared.utils.ListUtils;
import com.project.website.canvas.client.resources.CanvasResources;

public class TextEditToolbarImpl extends Composite implements TextEditToolbar
{
    private static TextEditToolbarImplUiBinder uiBinder = GWT.create(TextEditToolbarImplUiBinder.class);

    interface TextEditToolbarImplUiBinder extends UiBinder<Widget, TextEditToolbarImpl>
    {}

    private final RegistrationsManager registrationsManager = new RegistrationsManager();

    @UiField
    HTMLPanel rootPanel;
    private Element _editedElement;

    private ArrayList<ToolbarButtonInfo> buttonInfos = new ArrayList<ToolbarButtonInfo>();

    public TextEditToolbarImpl()
    {
        initWidget(uiBinder.createAndBindUi(this));
        initButtons();

    }

    @Override
    public Element getEditedElement()
    {
        return this._editedElement;
    }

    @Override
    public void setEditedElement(Element elem)
    {
        this._editedElement = elem;
    }

    @Override
    protected void onLoad()
    {
        super.onLoad();
        this.setRegistrations();
    }

    @Override
    protected void onUnload()
    {
        super.onUnload();
        this.registrationsManager.clear();
    }

    private void setRegistrations()
    {
        final TextEditToolbarImpl that = this;
        this.registrationsManager.add(Event.addNativePreviewHandler(new NativePreviewHandler() {
            @Override
            public void onPreviewNativeEvent(NativePreviewEvent event)
            {
                String eventType = event.getNativeEvent().getType();
                if (eventType.equals(MouseDownEvent.getType().getName())
                        || eventType.equals(MouseUpEvent.getType().getName())
                        || eventType.equals(KeyDownEvent.getType().getName())) {
                    that.updateButtonStates();
                }
            }
        }));
    }

    private void initButtons()
    {
        // setSimpleCssValueButton("fontWeight", "bold", "Bold");
        this.addCssStringValueToggleButton("fontWeight", new String[] { "400", "normal" },
                new String[] { "700", "bold" }, "Bold", false);
        this.addCssStringValueToggleButton("fontStyle", "normal", "italic", "Italic", false);
        this.addCssStringValueToggleButton("textDecoration", "none", "underline", "Underline", false);
        this.addCssStringValueListBox("fontFamily", "Font:", true, getFontFamilies());
        this.addCssStringValueListBox("fontSize", "Size:", false, getFontSizes());

        // TODO replace these two with color-pickers:
        this.addCssStringValueListBox("color", "Color:", false, getColors());
        this.addCssStringValueListBox("backgroundColor", "Background:", false, getColors());

        this.addCssStringValueToggleButton("direction", "ltr", "rtl", "Direction", true);
    }

    private ArrayList<String> getColors()
    {
        return ListUtils.create("transparent", "black", "purple", "blue", "cyan", "green", "yellow", "orange", "red",
                "pink", "beige", "white");
    }

    private Iterable<String> getFontSizes()
    {
        int[] sizes = new int[] { 8, 10, 12, 13, 14, 16, 18, 20, 24, 28, 32, 36, 48, 72, 96, 144 };
        ArrayList<String> values = new ArrayList<String>();
        for (int size : sizes) {
            values.add(String.valueOf(size) + "px");
        }
        return values;
    }

    private Iterable<String> getFontFamilies()
    {
        return ListUtils.create("arial", "georgia", "monospace", "verdana", "times");
    }

    private void addCssStringValueListBox(final String cssProperty, String title, final boolean setOptionsStyles, final Iterable<String> values)
    {
        final TextEditToolbarImpl that = this;

        final ListBox listBox = new ListBox();
        listBox.addStyleName(CanvasResources.INSTANCE.main().textEditToolbarListBox());

        for (String item : values) {
            listBox.addItem(item);
            if (setOptionsStyles) {
                OptionElement optionElement = ListBoxUtils.getOptionElement(listBox, listBox.getItemCount() - 1);
                optionElement.getStyle().setProperty(cssProperty, item);
            }
        }

        FlowPanel listBoxWrapper = new FlowPanel();
        listBoxWrapper.addStyleName(CanvasResources.INSTANCE.main().textEditToolbarListWrapper());

        InlineLabel titleLabel = new InlineLabel(title);
        titleLabel.addStyleName(CanvasResources.INSTANCE.main().textEditToolbarListTitle());

        listBoxWrapper.add(titleLabel);
        listBoxWrapper.add(listBox);

        this.rootPanel.add(listBoxWrapper);

        final ToolbarButtonInfo buttonInfo = new ToolbarButtonInfo() {
            @Override
            public void unset(Element elem)
            {}

            @Override
            public void set(Element elem)
            {
                String selectedValue = getListBoxValue(listBox);
                elem.getStyle().setProperty(cssProperty, selectedValue);
            }
            @Override
            public boolean isSet(Element elem)
            {
                // since we don't do anything in "unSet",
                // always cause a change in the listbox to be applied, don't bother detecting if the given element
                // already has the correct style.
                return false;
//                String selectedValue = getListBoxValue(listBox);
//                boolean isSet = that.isCssPropertySet(cssProperty, new String[] { selectedValue }, testedElement);
            }

            @Override
            public boolean isOnRootElemOnly()
            {
                return false;
            }

            @Override
            public void updateButtonStatus(Element testedElement)
            {
                String value = getCssPropertyValue(cssProperty, testedElement);
                boolean found = false;
                int selectedIndex = 0;
                for (int i = 0;  i < listBox.getItemCount(); i++)
                {
                    if (listBox.getValue(i).equals(value)) {
                        selectedIndex = i;
                        found = true;
                        break;
                    }
                }
                if (false == found) {
                    selectedIndex = listBox.getItemCount() - 1;
                    listBox.addItem(value);
                }
                listBox.setSelectedIndex(selectedIndex);
                updateListBoxSelectItemStyle(setOptionsStyles, listBox);
            }

            private String getCssPropertyValue(final String cssProperty, Element testedElement)
            {
                String value = StyleUtils.getComputedStyle(testedElement, null).getProperty(cssProperty);
                if (cssProperty.equals("fontFamily")) {
                    // css heuristic: pick out only the first part of the value
                    // (Arial Unicode MS,Arial,sans-serif --> arial
                    value = value.split("[ ,]")[0].toLowerCase();
                }
                return value;
            }

            private String getListBoxValue(final ListBox listBox)
            {
                String selectedValue = listBox.getValue(listBox.getSelectedIndex());
                return selectedValue;
            }
        };

        // Deliberately not added to this.registrationsManager
        // because after onUnload+onLoad we currently won't be restoring these registrations properly
        listBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event)
            {
                that.buttonPressed(buttonInfo);
                updateListBoxSelectItemStyle(setOptionsStyles, listBox);
            }
        });
        updateListBoxSelectItemStyle(setOptionsStyles, listBox);

        this.addButtonInfo(buttonInfo);
    }

    /**
     * A wrapper for {@link #addCssStringValueToggleButton(String, String[], String[], String)}, that create arrays with
     * a single value for unset and set value arrays.
     */
    private void addCssStringValueToggleButton(final String cssProperty, final String unsetValue,
            final String setValue, final String title, boolean onRootElemOnly)
    {
        this.addCssStringValueToggleButton(cssProperty, new String[] { unsetValue }, new String[] { setValue }, title,
                onRootElemOnly);
    }

    /**
     * Creates a button that toggles a css property between two states. It will use the first value in each of the given
     * arrays (see below) to set the property, and will use the arrays themselves for testing the current value of the
     * property to decide if it is currently set or unset.
     *
     * @param cssProperty
     *            Name of property to toggle, in camel case format ("fontStyle", not "font-style")
     * @param unsetValues
     *            An array, with at least one element, of equivalent values for the unset state
     * @param setValues
     *            An array, with at least one element, of equivalent values for the set state
     * @param title
     *            Of the button
     */
    private void addCssStringValueToggleButton(final String cssProperty, final String[] unsetValues,
            final String[] setValues, final String title, final boolean onRootElemOnly)
    {
        assert (unsetValues.length >= 1);
        assert (setValues.length >= 1);

        final ToggleButton buttonWidget = createButtonWidget(cssProperty, setValues, title);

        ToolbarButtonInfo buttonInfo = new ToolbarButtonInfo() {
            @Override
            public boolean isSet(Element elem)
            {
                return isCssPropertySet(cssProperty, setValues, elem);
            }

            @Override
            public void set(Element elem)
            {
                elem.getStyle().setProperty(cssProperty, setValues[0]);
            }

            @Override
            public void unset(Element elem)
            {
                elem.getStyle().setProperty(cssProperty, unsetValues[0]);
            }

            @Override
            public boolean isOnRootElemOnly()
            {
                return onRootElemOnly;
            }

            @Override
            public void updateButtonStatus(Element testedElement)
            {
                buttonWidget.setDown(this.isSet(testedElement));
            }
        };

        this.addButton(buttonWidget, buttonInfo);
    }

    private ToggleButton createButtonWidget(final String cssProperty, final String[] setValues, final String title)
    {
        // Must be a button element, otherwise when it's clicked it will remove the current selection because the
        // browser
        // will see it as clicking on text.
        ToggleButton buttonWidget = new ToggleButton();
        buttonWidget.getElement().getStyle().setProperty(cssProperty, setValues[0]);
        buttonWidget.getElement().setInnerText(title);
        buttonWidget.addStyleName(CanvasResources.INSTANCE.main().textEditToolbarToggleButton());
        return buttonWidget;
    }

    private void addButton(ToggleButton widget, final ToolbarButtonInfo buttonInfo)
    {
        final TextEditToolbarImpl that = this;
        this.rootPanel.add(widget);
        // Deliberately not added to this.registrationsManager
        // because after onUnload+onLoad we currently won't be restoring these registrations properly
        widget.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override public void onValueChange(ValueChangeEvent<Boolean> event)
            {
                that.buttonPressed(buttonInfo);
            }
        });
        this.addButtonInfo(buttonInfo);
    }

    private void addButtonInfo(ToolbarButtonInfo buttonInfo)
    {
        this.buttonInfos.add(buttonInfo);
    }


    protected void updateButtonStates()
    {
        final Element editedElement = this.getEditedElement();
        if (null == editedElement) {
            return;
        }

        SelectionImpl selection = SelectionImpl.getWindowSelection();

        for (ToolbarButtonInfo buttonInfo : this.buttonInfos)
        {
            Element testedElement = editedElement;
            if (false == buttonInfo.isOnRootElemOnly()) {
                if (null != selection.getAnchorNode()) {
                    testedElement = selection.getAnchorNode().getParentElement();
                }
                else if (null != selection.getFocusNode()) {
                    testedElement = selection.getFocusNode().getParentElement();
                }
                else {
                    continue;
                }
            }

            if (null == testedElement) {
                continue;
            }
            buttonInfo.updateButtonStatus(testedElement);
        }
    }


    private void buttonPressed(final ToolbarButtonInfo buttonInfo)
    {
        final Element editedElement = this.getEditedElement();
        if (null == editedElement) {
            return;
        }

        if (buttonInfo.isOnRootElemOnly()) {
            this.applyButtonOnRootElement(buttonInfo, editedElement);
            return;
        }

        this.applyButtonOnSelectedRange(buttonInfo, editedElement);
    }

    private void applyButtonOnSelectedRange(final ToolbarButtonInfo buttonInfo, final Element editedElement)
    {
        SelectionImpl selection = SelectionImpl.getWindowSelection();
        Node anchorNode = selection.getAnchorNode();
        if (null == anchorNode) {
            return;
        }
        Element elem = anchorNode.getParentElement();
        boolean isSetInFocusNode = null == elem ? false : buttonInfo.isSet(elem);

        for (int i = 0; i < selection.getRangeCount(); i++) {
            Range range = selection.getRangeAt(i);
            Action<Element> action = null;
            if (isSetInFocusNode) {
                action = new Action<Element>() {
                    @Override
                    public void exec(Element arg)
                    {
                        buttonInfo.unset(arg);
                    }
                };
            } else {
                action = new Action<Element>() {
                    @Override
                    public void exec(Element arg)
                    {
                        buttonInfo.set(arg);
                    }
                };
            }
            RangeUtils.applyToNodesInRange(range, action);
        }

        // TODO this kills the range's validity...
        this.pushStylesInChildren();
        ElementUtils.mergeSpans(editedElement);

        editedElement.focus();
    }

    private void applyButtonOnRootElement(final ToolbarButtonInfo buttonInfo, final Element editedElement)
    {
        if (buttonInfo.isSet(editedElement)) {
            buttonInfo.unset(editedElement);
        } else {
            buttonInfo.set(editedElement);
        }
    }

    private void pushStylesInChildren()
    {
        // We really should have run pushStylesDownToTextNodes on the edited
        // element itself rather than iterating and running the code on the
        // children.
        // But in TextEdit that would mean moving properties such as Width and
        // Height down into the children, which isn't what we want.
        for (Node node : ElementUtils.getChildNodes(this.getEditedElement())) {
            if (Node.ELEMENT_NODE != node.getNodeType()) {
                continue;
            }
            Element childElem = Element.as(node);
            StyleUtils.pushStylesDownToTextNodes(childElem);
        }
    }

    private Boolean isCssPropertySet(final String cssProperty, final String[] setValues, Element element)
    {
        String currentValue = null;
        if (cssProperty.equals("textDecoration")) {
            currentValue = StyleUtils.getInheritedTextDecoration(element);
        } else {
            currentValue = StyleUtils.getComputedStyle(element, null).getProperty(cssProperty);
        }
        for (String setValue : setValues) {
            if (setValue.equals("")) {
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

    private void updateListBoxSelectItemStyle(final boolean setOptionsStyles, final ListBox listBox)
    {
        if (setOptionsStyles) {
            int selectedIndex = listBox.getSelectedIndex();
            StyleUtils.copyStyle(listBox.getElement(), ListBoxUtils.getOptionElement(listBox, selectedIndex), true);
        }
    }
}
