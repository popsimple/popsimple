package com.project.canvas.client.shared.widgets;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class RadioButtonPanel extends Composite implements HasWidgets {

    private static RadioButtonPanelUiBinder uiBinder = GWT.create(RadioButtonPanelUiBinder.class);

    interface RadioButtonPanelUiBinder extends UiBinder<Widget, RadioButtonPanel> {
    }
    
    @UiField
    RadioButton radioButton;
    
    @UiField
    FlowPanel panel;

    public RadioButtonPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        
        this.addHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                radioButton.fireEvent(event);
            }
        }, ClickEvent.getType());
    }
    
    public void setName(String name)
    {
        this.radioButton.setName(name);
    }
    
    public RadioButton getRadioButton()
    {
        return this.radioButton;
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
}
