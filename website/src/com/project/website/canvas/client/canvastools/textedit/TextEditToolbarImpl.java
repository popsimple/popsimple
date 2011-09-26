package com.project.website.canvas.client.canvastools.textedit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.html5.Range;
import com.project.shared.client.html5.impl.RangeImpl;
import com.project.shared.client.html5.impl.RangeUtils;
import com.project.shared.client.html5.impl.SelectionImpl;
import com.project.shared.client.utils.DocumentUtils;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.EventUtils;
import com.project.shared.client.utils.SchedulerUtils;
import com.project.shared.client.utils.StyleUtils;
import com.project.shared.client.utils.widgets.ListBoxUtils;
import com.project.shared.data.funcs.Func;
import com.project.shared.data.funcs.Func.Action;
import com.project.shared.utils.ListUtils;
import com.project.shared.utils.ObjectUtils;
import com.project.shared.utils.loggers.Logger;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.shared.widgets.ColorPicker;

public class TextEditToolbarImpl extends Composite implements TextEditToolbar
{
    private static TextEditToolbarImplUiBinder uiBinder = GWT.create(TextEditToolbarImplUiBinder.class);

    interface TextEditToolbarImplUiBinder extends UiBinder<Widget, TextEditToolbarImpl>
    {}

    private final RegistrationsManager registrationsManager = new RegistrationsManager();

    @UiField
    HTMLPanel rootPanel;

    private Widget _editedWidget;

    private ArrayList<ToolbarButtonInfo> buttonInfos = new ArrayList<ToolbarButtonInfo>();

    private ArrayList<Func<Void,Void>> onUnloadFuncs = new ArrayList<Func<Void,Void>>();

    private HashSet<Range> savedRanges = new HashSet<Range>();

    public TextEditToolbarImpl()
    {
        initWidget(uiBinder.createAndBindUi(this));
        initButtons();

    }

    @Override
    public Widget getEditedWidget()
    {
        return this._editedWidget;
    }

    @Override
    public void setEditedWidget(Widget elem)
    {
        if (elem == this._editedWidget) {
            this.updateButtonStates();
            return;
        }
        this._editedWidget = elem;

        this.clearRegistrations();
        if (null != elem) {
            this.setRegistrations();
        }
    }

    @Override
    protected void onLoad()
    {
        super.onLoad();
        if (null != this._editedWidget)
        {
            this.setRegistrations();
        }
    }

    @Override
    protected void onUnload()
    {
        this.clearRegistrations();
        for (Func<Void,Void> func : this.onUnloadFuncs) {
            func.call(null);
        }
        super.onUnload();
    }

    private void clearRegistrations()
    {
        this.registrationsManager.clear();
    }

    private void setRegistrations()
    {
        if (this.registrationsManager.hasRegistrations()) {
            // already set.
            return;
        }
        final TextEditToolbarImpl that = this;


        if (null != this._editedWidget) {
            this.registrationsManager.add(this._editedWidget.addDomHandler(new BlurHandler(){
                @Override public void onBlur(BlurEvent event) {
                    that.saveSelectedRanges();
                }}, BlurEvent.getType()));
        }

        this.registrationsManager.add(Event.addNativePreviewHandler(new NativePreviewHandler() {
            @Override
            public void onPreviewNativeEvent(NativePreviewEvent event)
            {
                if (EventUtils.nativePreviewEventTypeIsAny(event,
                        new DomEvent.Type<?>[] {
                            MouseDownEvent.getType(),
                            MouseUpEvent.getType(),
                            KeyDownEvent.getType()
                    }))
                {
                    that.saveSelectedRanges();
                    SchedulerUtils.OneTimeScheduler.get().scheduleDeferredOnce(new ScheduledCommand() {
                        @Override public void execute() {
                            if (that.isActiveElementTree()) {
                                that.updateButtonStates();
                            }
                        }
                    });
                }
            }
        }));

        this.updateButtonStates();
    }

    protected boolean isActiveElementTree()
    {
        if (null == this._editedWidget) {
            return false;
        }
        return DocumentUtils.isActiveElementTree(this._editedWidget.getElement());
    }

    private void initButtons()
    {
        // setSimpleCssValueButton("fontWeight", "bold", "Bold");
        this.addCssStringValueButton("fontWeight", new String[] { "400", "normal" },
                new String[] { "700", "bold" }, "Bold", false);
        this.addCssStringValueButton("fontStyle", "normal", "italic", "Italic", false);
        this.addCssStringValueButton("textDecoration", "none", "underline", "Underline", false);
        this.addCssStringValueListBox("fontFamily", "Font:", true, getFontFamilies());
        this.addCssStringValueListBox("fontSize", "Size:", false, getFontSizes());

        // TODO replace these two with color-pickers:
        this.addColorPicker("color", "Color:");

        this.addCssStringValueButton("direction", "ltr", "rtl", "Direction", true);
    }

    private void addColorPicker(String cssProperty, String title)
    {
        final TextEditToolbarImpl that = this;
        final ColorPicker colorPicker = new ColorPicker();
        this.addTitledToolbarItem(title, colorPicker);

        this.onUnloadFuncs.add(new Func.VoidAction() {
            @Override public void exec()
            {
                colorPicker.setPickerVisible(false);
            }
        });
        final ToolbarButtonInfo buttonInfo = new ToolbarButtonInfo() {
            @Override
            public void updateButtonStatus(Element testedElement)
            {
                String cssColorString = StyleUtils.getComputedStyle(testedElement, null).getColor();
                colorPicker.getElement().getStyle().setBackgroundColor(cssColorString);
                // Just to make the text invisible:
                colorPicker.getElement().getStyle().setColor(cssColorString);
            }

            @Override
            public void unset(Element elem)
            {
                // Do nothing. TODO: Maybe add a default value for un-setting.
            }

            @Override
            public void set(Element elem)
            {
                String cssColorString = StyleUtils.getComputedStyle(colorPicker.getElement(), null).getBackgroundColor();
                elem.getStyle().setColor(cssColorString);
            }

            @Override
            public boolean isSet(Element elem)
            {
                return false;
            }

            @Override
            public boolean isOnRootElemOnly()
            {
                return false;
            }
        };
//        colorPicker.addChangeHandler(new ChangeHandler() {
//            @Override public void onChange(ChangeEvent event)
//            {
//                that.buttonPressed(buttonInfo);
//            }
//        });
        colorPicker.addDomHandler(new ChangeHandler(){
            @Override public void onChange(ChangeEvent event)
            {
                that.buttonPressed(buttonInfo);
            }}, ChangeEvent.getType());

        this.addButtonInfo(buttonInfo);
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

    private void addCssStringValueListBox(final String cssProperty, String title, final boolean setOptionsStyles, final Iterable<String> values, String... addStyleNames)
    {
        final TextEditToolbarImpl that = this;

        final ListBox listBox = addListBoxWidget(cssProperty, title, setOptionsStyles, values, addStyleNames);

        final ToolbarButtonInfo buttonInfo = new ToolbarButtonInfo() {
            @Override
            public void unset(Element elem) {}

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
                updateValueListBoxStatus(cssProperty, setOptionsStyles, listBox, testedElement);
            }

            private String getListBoxValue(final ListBox listBox)
            {
                return listBox.getValue(listBox.getSelectedIndex());
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

    private ListBox addListBoxWidget(final String cssProperty, String title, final boolean setOptionsStyles,
            final Iterable<String> values, String... addStyleNames)
    {
        final ListBox listBox = new ListBox();
        listBox.addStyleName(CanvasResources.INSTANCE.main().canvasToolbarListBox());
        for (String styleName : addStyleNames) {
            listBox.addStyleName(styleName);
        }

        for (String item : values) {
            listBox.addItem(item);
            if (setOptionsStyles) {
                OptionElement optionElement = ListBoxUtils.getOptionElement(listBox, listBox.getItemCount() - 1);
                optionElement.getStyle().setProperty(cssProperty, item);
            }
        }

        addTitledToolbarItem(title, listBox);
        return listBox;
    }

    private void addTitledToolbarItem(String title, Widget widget)
    {
        FlowPanel listBoxWrapper = new FlowPanel();
        listBoxWrapper.addStyleName(CanvasResources.INSTANCE.main().canvasToolbarItemWrapper());
        InlineLabel titleLabel = new InlineLabel(title);
        titleLabel.addStyleName(CanvasResources.INSTANCE.main().canvasToolbarItemTitle());
        listBoxWrapper.add(titleLabel);
        listBoxWrapper.add(widget);

        this.rootPanel.add(listBoxWrapper);
    }

    /**
     * A wrapper for {@link #addCssStringValueButton(String, String[], String[], String)}, that create arrays with
     * a single value for unset and set value arrays.
     */
    private void addCssStringValueButton(final String cssProperty, final String unsetValue,
            final String setValue, final String title, boolean onRootElemOnly)
    {
        this.addCssStringValueButton(cssProperty, new String[] { unsetValue }, new String[] { setValue }, title,
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
    private void addCssStringValueButton(final String cssProperty, final String[] unsetValues,
            final String[] setValues, final String title, final boolean onRootElemOnly)
    {
        assert (unsetValues.length >= 1);
        assert (setValues.length >= 1);

        final Button buttonWidget = createButtonWidget(cssProperty, setValues, title);

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
                // TODO chang eth button style
                if (this.isSet(testedElement)) {
                    buttonWidget.addStyleName("gwt-ToggleButton-down");
                    buttonWidget.removeStyleName("gwt-ToggleButton-up");
                }
                else {
                    buttonWidget.removeStyleName("gwt-ToggleButton-down");
                    buttonWidget.addStyleName("gwt-ToggleButton-up");
                }

            }
        };

        this.addButton(buttonWidget, buttonInfo);
    }

    private Button createButtonWidget(final String cssProperty, final String[] setValues, final String title)
    {
        // Must be a button element, otherwise when it's clicked it will remove the current selection because the
        // browser
        // will see it as clicking on text.
        Button buttonWidget = new Button();
        buttonWidget.getElement().getStyle().setProperty(cssProperty, setValues[0]);
        buttonWidget.getElement().setInnerText(title);
        buttonWidget.addStyleName(CanvasResources.INSTANCE.main().canvasToolbarToggleButton());
        return buttonWidget;
    }

    private void addButton(Button widget, final ToolbarButtonInfo buttonInfo)
    {
        final TextEditToolbarImpl that = this;
        this.rootPanel.add(widget);
        // Deliberately not added to this.registrationsManager
        // because after onUnload+onLoad we currently won't be restoring these registrations properly
        widget.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event)
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
        final Widget editedElement = this.getEditedWidget();
        if (null == editedElement) {
            return;
        }
        this.saveSelectedRanges();
        Element testElement = this.getTestElementFromSelection();
        for (ToolbarButtonInfo buttonInfo : this.buttonInfos)
        {
            buttonInfo.updateButtonStatus(testElement);
        }
    }

    /**
     * Tries to find an element from within the selection range for testing whether a property is set or not in the range.
     */
    private Element getTestElementFromSelection()
    {
        Element testElement = null;
        for (Range range : this.savedRanges)
        {
            HashMap<Node, Boolean> nodeContainmentMap = RangeUtils.getNodeContainmentMap(range);
            for (Node node : nodeContainmentMap.keySet()) {
                if (Node.TEXT_NODE != node.getNodeType()) {
                    continue;
                }
                testElement = node.getParentElement();
                if (testElement.getInnerText().isEmpty()) {
                    continue;
                }
                return testElement;
            }
        }
        if (null != testElement) {
            return testElement;
        }
        if (this.isActiveElementTree()) {
            SelectionImpl selection = SelectionImpl.getWindowSelection();
            Node node = selection.getAnchorNode();
            if (null == node) {
                node = selection.getFocusNode();
            }
            if (null != node) {
                return node.getParentElement();
            }
        }
        return this._editedWidget.getElement();
    }



    private void buttonPressed(final ToolbarButtonInfo buttonInfo)
    {
        final Widget editedElement = this.getEditedWidget();
        if (null == editedElement) {
            return;
        }

        if (buttonInfo.isOnRootElemOnly()) {
            this.applyButtonOnRootElement(buttonInfo, editedElement.getElement());
            return;
        }

        this.applyButtonOnSelectedRange(buttonInfo, editedElement.getElement());
    }

    private void saveSelectedRanges()
    {
        if (null == this._editedWidget) {
            return;
        }
        if (false == this.isActiveElementTree()) {
            return;
        }
        SelectionImpl selection = SelectionImpl.getWindowSelection();
        if (0 >= selection.getRangeCount()) {
            return;
        }
        this.savedRanges.clear();

        for (int i = 0; i < selection.getRangeCount(); i++) {
            Range range = selection.getRangeAt(i);
            Logger.info(this, ((RangeImpl)range).asString());
            this.savedRanges.add(range.cloneRange());
        }
    }

    private void applyButtonOnSelectedRange(final ToolbarButtonInfo buttonInfo, final Element editedElement)
    {
        boolean isSetResult = this.isSetInSelection(buttonInfo);

        for (Range range : this.savedRanges) {
            Action<Element> action = null;
            if (isSetResult) {
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

    private boolean isSetInSelection(final ToolbarButtonInfo buttonInfo)
    {
        Element testElement = this.getTestElementFromSelection();
        return testElement == null ? false : buttonInfo.isSet(testElement);
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
        for (Node node : ElementUtils.getChildNodes(this.getEditedWidget().getElement())) {
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
        if (ObjectUtils.areEqual(cssProperty, "textDecoration")) {
            currentValue = StyleUtils.getInheritedTextDecoration(element);
        } else {
            currentValue = StyleUtils.getComputedStyle(element, null).getProperty(cssProperty);
        }
        if (null == currentValue)
        {
            return false;
        }
        for (String setValue : setValues) {
            if (ObjectUtils.areEqual(setValue, "")) {
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

    private static void updateListBoxSelectItemStyle(final boolean setOptionsStyles, final ListBox listBox)
    {
        if (setOptionsStyles) {
            int selectedIndex = listBox.getSelectedIndex();
            StyleUtils.copyStyle(listBox.getElement(), ListBoxUtils.getOptionElement(listBox, selectedIndex), true);
        }
    }

    private static String getCssPropertyValue(final String cssProperty, Element testedElement)
    {
        String value = StyleUtils.getComputedStyle(testedElement, null).getProperty(cssProperty);
        if (null == value)
        {
            return "";
        }
        if (ObjectUtils.areEqual(cssProperty, "fontFamily")) {
            // css heuristic: pick out only the first part of the value
            // (Arial Unicode MS,Arial,sans-serif --> arial
            value = value.split("[ ,]")[0].toLowerCase();
        }
        return value;
    }


    private static void updateValueListBoxStatus(final String cssProperty, final boolean setOptionsStyles,
            final ListBox listBox, Element testedElement)
    {
        String value = getCssPropertyValue(cssProperty, testedElement);
        boolean found = false;
        int selectedIndex = 0;
        for (int i = 0;  i < listBox.getItemCount(); i++)
        {
            if (ObjectUtils.areEqual(listBox.getValue(i), value)) {
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
}
