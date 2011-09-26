package com.project.website.canvas.client.shared.widgets;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class ToggleButtonPanel extends FlowPanel
{
    private ArrayList<ToggleButton> _buttonList = new ArrayList<ToggleButton>();

    @Override
    public void add(Widget w) {
        super.add(w);

        final ToggleButton button = (ToggleButton)w;

        this._buttonList.add(button);
        button.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (false == event.getValue())
                {
                    return;
                }
                handleButtonToggled(button);
            }
        });
    }

    private void handleButtonToggled(ToggleButton clickedButton)
    {
        for (ToggleButton button : this._buttonList)
        {
            if (button == clickedButton)
            {
                continue;
            }
            button.setValue(false, true);
        }
    }
}
