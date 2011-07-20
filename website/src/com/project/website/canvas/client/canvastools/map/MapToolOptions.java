package com.project.website.canvas.client.canvastools.map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.project.gwtmapstraction.client.mxn.MapProvider;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.data.DoubleHashMap;
import com.project.website.canvas.shared.data.MapData;
import com.project.website.canvas.shared.data.MapData.MapType;

public class MapToolOptions extends Composite implements TakesValue<MapData>, HasValueChangeHandlers<MapData>
{

    private static MapToolOptionsUiBinder uiBinder = GWT.create(MapToolOptionsUiBinder.class);

    interface MapToolOptionsUiBinder extends UiBinder<Widget, MapToolOptions>
    {
    }


    @UiField
    FlowPanel providersPanel;

    @UiField
    FlowPanel mapTypesPanel;

    @UiField
    Button doneButton;

    SimpleEvent<Void> doneEvent = new SimpleEvent<Void>();

    private MapData mapData;
    private DoubleHashMap<MapProvider, RadioButton> providerButtons = new DoubleHashMap<MapProvider, RadioButton>();
    private DoubleHashMap<MapType, RadioButton> mapTypeButtons = new DoubleHashMap<MapType, RadioButton>();

    public MapToolOptions(Iterable<MapProvider> availableProviders)
    {
        initWidget(uiBinder.createAndBindUi(this));

        ValueChangeHandler<Boolean> booleanValueChanged = new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event)
            {
                valueUpdated();
            }
        };
        for (MapProvider provider : availableProviders) {
            RadioButton providerButton = new RadioButton("provider", provider.getDescription());
            providerButton.addValueChangeHandler(booleanValueChanged);
            this.providerButtons.put(provider, providerButton);
            this.providersPanel.add(providerButton);
        }

        for (MapType mapType : MapType.values()) {
            String description;
            switch (mapType) {
            case ROAD: description = "Road map"; break;
            case HYBRID: description = "Roads + Satellite"; break;
            case PHYSICAL: description = "Terrain map"; break;
            case SATELLITE: description = "Satellite imagery"; break;
                default:
                    continue;
            }
            RadioButton mapTypeButton = new RadioButton("mapType", description);
            mapTypeButton.addValueChangeHandler(booleanValueChanged);
            this.mapTypeButtons.put(mapType, mapTypeButton);
            this.mapTypesPanel.add(mapTypeButton);
        }
    }


    protected void valueUpdated()
    {
        ValueChangeEvent.fire(this, this.getValue());
    }


    @UiHandler("doneButton")
    void doneButtonClicked(ClickEvent event)
    {
        doneEvent.dispatch(null);
    }


    @Override
    public void setValue(MapData value)
    {
        this.mapData = value;
        this.clearButtons();
        this.providerButtons.getByKey1(MapProvider.valueOf(value.provider)).setValue(true);
        this.mapTypeButtons.getByKey1(value.mapType).setValue(true);
    }

    @Override
    public MapData getValue()
    {
        if (null == this.mapData) {
            // TODO is this what we want?
            throw new RuntimeException("Must set value first!");
        }
        for (RadioButton button : this.providerButtons.values()) {
            if (button.getValue()) {
                this.mapData.provider = this.providerButtons.getByKey2(button).name();
            }
        }
        for (RadioButton button : this.mapTypeButtons.values()) {
            if (button.getValue()) {
                this.mapData.mapType = this.mapTypeButtons.getByKey2(button);
            }
        }
        return this.mapData;
    }

    public HandlerRegistration addDoneHandler(SimpleEvent.Handler<Void> handler) {
        return this.doneEvent.addHandler(handler);
    }

    private void clearButtons()
    {
        for (RadioButton button : this.providerButtons.values()) {
            button.setValue(false);
        }
        for (RadioButton button : this.mapTypeButtons.values()) {
            button.setValue(false);
        }
    }


    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<MapData> handler)
    {
        return this.addHandler(handler, ValueChangeEvent.getType());
    }

}
