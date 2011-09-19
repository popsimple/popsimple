package com.project.website.canvas.client.canvastools.textedit;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.FontWeight;
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

        this.addButton(new Button("Bold"), new Func<Element,Boolean>(){
            @Override
            public Boolean call(Element arg)
            {
                return arg.getStyle().getFontWeight().toLowerCase().equals("bold");
            }},
            new Action<Element>(){
                @Override
                public void exec(Element arg)
                {
                    arg.getStyle().setFontWeight(FontWeight.BOLD);
                }},
            new Action<Element>(){

                @Override
                public void exec(Element arg)
                {
                    arg.getStyle().setFontWeight(FontWeight.NORMAL);
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
        this._element.focus();
    }

}
