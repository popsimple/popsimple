package com.project.website.canvas.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.website.canvas.client.canvastools.base.BuiltinTools;
import com.project.website.canvas.client.canvastools.base.interfaces.ToolboxItem;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.client.worksheet.WorksheetImpl;
import com.project.website.canvas.client.worksheet.WorksheetViewImpl;
import com.project.website.canvas.client.worksheet.interfaces.Worksheet;

public class CanvasContainer extends Composite {

    private static CanvasContainerUiBinder uiBinder = GWT.create(CanvasContainerUiBinder.class);

    interface CanvasContainerUiBinder extends UiBinder<Widget, CanvasContainer> {
    }

    @UiField
    Toolbox toolbox;
    @UiField
    WorksheetViewImpl worksheetWidget;

    private Worksheet worksheet;
    private final ToolboxItem defaultTool = BuiltinTools.cursorTool;

    public Worksheet getWorksheet() {
        return worksheet;
    }

    public CanvasContainer() {
        initWidget(uiBinder.createAndBindUi(this));
        this.worksheet = new WorksheetImpl(worksheetWidget);

        this.toolbox.getToolChosenEvent().addHandler(new SimpleEvent.Handler<ToolboxItem>() {
            public void onFire(ToolboxItem arg) {
                worksheet.setActiveToolboxItem(arg);
            }
        });
        this.worksheet.addDefaultToolboxItemRequestHandler(new SimpleEvent.Handler<Void>() {
            @Override
            public void onFire(Void arg) {
                toolbox.setActiveTool(defaultTool);
            }
        });

        this.worksheet.addViewModeChangedHandler(new SimpleEvent.Handler<Boolean>() {
            @Override
            public void onFire(Boolean fullScreen) {
                if (fullScreen) {
                    toolbox.addStyleName(CanvasResources.INSTANCE.main().displayNone());
                } else {
                    toolbox.removeStyleName(CanvasResources.INSTANCE.main().displayNone());
                }
            }
        });
        toolbox.setActiveTool(defaultTool);
    }
}
