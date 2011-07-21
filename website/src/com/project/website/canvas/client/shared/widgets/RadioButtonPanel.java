package com.project.website.canvas.client.shared.widgets;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class RadioButtonPanel extends Composite implements HasWidgets, HasClickHandlers {

    private static RadioButtonPanelUiBinder uiBinder = GWT.create(RadioButtonPanelUiBinder.class);

    interface RadioButtonPanelUiBinder extends UiBinder<Widget, RadioButtonPanel> {
    }

    @UiField
    RadioButton radioButton;

    @UiField
    FlowPanel panel;

    public RadioButtonPanel() {
        initWidget(uiBinder.createAndBindUi(this));

        // Note: RadioButton doesn't fire ValueChangeEvent properly which is why the users of this control
        // must add handlers to our ClickHandler instead.
        this.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                radioButton.setValue(true);
                event.stopPropagation();
            }
        }, ClickEvent.getType());
    }

    public void setName(String name)
    {
        this.radioButton.setName(name);
    }

    public boolean getValue() {
        return this.radioButton.getValue();
    }
    public void setValue(boolean value) {
        this.radioButton.setValue(value);
    }

    @Override
    public void add(Widget w) {
        this.panel.add(w);
    }

    @Override
    public void clear() {
        this.panel.clear();

    }

    @Override
    public Iterator<Widget> iterator() {
        return this.panel.iterator();
    }

    @Override
    public boolean remove(Widget w) {
        return this.panel.remove(w);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler)
    {
        return this.addHandler(handler, ClickEvent.getType());
    }
}
