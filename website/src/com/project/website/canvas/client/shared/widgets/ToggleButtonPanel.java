package com.project.website.canvas.client.shared.widgets;

import java.util.ArrayList;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class ToggleButtonPanel extends FlowPanel
{
    private ArrayList<ToggleButton> _buttonList = new ArrayList<ToggleButton>();

    private boolean _isDuringChangeHandling = false;
    private ToggleButton _defaultButton = null;

    @Override
    public void add(Widget w) {
        super.add(w);

        final ToggleButton button = (ToggleButton)w;

        this._buttonList.add(button);
        button.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (false == event.getValue()) {
                    handleButtonUnToggled(button);
                }
                else {
                    handleButtonToggled(button);
                }
            }
        });
    }

    public void setDefaultButton(ToggleButton button)
    {
        if (false == this._buttonList.contains(button))
        {
            throw new ButtonNotContainedOnPanelException();
        }
        this._defaultButton = button;
        if (null != this.getActiveButton())
        {
            return;
        }
        this._defaultButton.setValue(true, true);
    }

    public ToggleButton getActiveButton()
    {
        for (ToggleButton button : this._buttonList)
        {
            if (button.getValue())
            {
                return button;
            }
        }
        return null;
    }

    private void handleButtonUnToggled(ToggleButton button)
    {
        if (this._isDuringChangeHandling) {
            return;
        }
        if (null == this._defaultButton) {
            return;
        }
        this._defaultButton.setValue(true, true);
    }

    private void handleButtonToggled(ToggleButton button)
    {
        this._isDuringChangeHandling = true;
        try  {
            for (ToggleButton otherButton : this._buttonList)
            {
                if (otherButton == button)
                {
                    continue;
                }
                otherButton.setValue(false, true);
            }
        }
        finally {
            this._isDuringChangeHandling = false;
        }
    }
}
