package com.project.website.canvas.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.website.canvas.client.canvastools.base.BuiltinTools;
import com.project.website.canvas.client.canvastools.base.ToolboxItem;
import com.project.website.canvas.client.resources.CanvasResources;

public class Toolbox extends Composite {

    private static ToolboxUiBinder uiBinder = GWT.create(ToolboxUiBinder.class);

    interface ToolboxUiBinder extends UiBinder<Widget, Toolbox> {
    }

    @UiField
    FlowPanel toolsPanel;

    final HashMap<ToolboxItem, Widget> toolboxItems = new HashMap<ToolboxItem, Widget>();
    final SimpleEvent<ToolboxItem> toolChosenEvent = new SimpleEvent<ToolboxItem>();

    final ArrayList<Widget> toolIconHolders = new ArrayList<Widget>();

    public Toolbox() {
        initWidget(uiBinder.createAndBindUi(this));

        for (ToolboxItem toolboxItem : BuiltinTools.getTools()) {
            this.addTool(toolboxItem);
        }
    }

    public SimpleEvent<ToolboxItem> getToolChosenEvent() {
        return this.toolChosenEvent;
    }

    private void addTool(final ToolboxItem toolboxItem) {
        final FlowPanel outerElem = new FlowPanel();
        this.toolboxItems.put(toolboxItem, outerElem);
        Label elem = new Label();
        outerElem.add(elem);
        this.toolsPanel.add(outerElem);
        this.toolIconHolders.add(outerElem);
        outerElem.addStyleName(CanvasResources.INSTANCE.main().toolboxCommonIconStyle());
        outerElem.setTitle(toolboxItem.getToolboxIconToolTip());

        elem.addStyleName(CanvasResources.INSTANCE.main().toolboxInnerIconStyle());
        elem.addStyleName(toolboxItem.getToolboxIconStyle());
        elem.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                setActiveTool(toolboxItem);
                event.stopPropagation();
            }
        });
    }

    public void setActiveTool(ToolboxItem toolboxItem) {
        for (Widget w : toolIconHolders) {
            w.removeStyleName(CanvasResources.INSTANCE.main().toolboxCommonSelectedIconStyle());
        }
        this.toolboxItems.get(toolboxItem).addStyleName(
                CanvasResources.INSTANCE.main().toolboxCommonSelectedIconStyle());
        toolChosenEvent.dispatch(toolboxItem);
    }
}
