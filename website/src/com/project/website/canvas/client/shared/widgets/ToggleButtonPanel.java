package com.project.website.canvas.client.shared.widgets;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                HandleButtonClicked(button);
            }
        });
    }

    private void HandleButtonClicked(ToggleButton clickedButton)
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
