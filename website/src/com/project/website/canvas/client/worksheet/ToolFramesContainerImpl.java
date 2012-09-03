package com.project.website.canvas.client.worksheet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.data.funcs.Func;
import com.project.shared.utils.ArrayUtils;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasToolFrame;
import com.project.website.canvas.client.worksheet.data.CanvasToolFrameInfo;
import com.project.website.canvas.client.worksheet.interfaces.ToolFramesContainer;

public class ToolFramesContainerImpl extends Composite implements ToolFramesContainer 
{
    interface WorksheetCanvasImplUiBinder extends UiBinder<Widget, ToolFramesContainerImpl> {
    }
    private static WorksheetCanvasImplUiBinder uiBinder = GWT.create(WorksheetCanvasImplUiBinder.class);

    @UiField
    HTMLPanel toolsContainerPanel;

    
    private final HashMap<CanvasToolFrame, CanvasToolFrameInfo> _toolFrameInfos = new HashMap<CanvasToolFrame, CanvasToolFrameInfo>();
    private final HashSet<CanvasToolFrame> _overToolFrames = new HashSet<CanvasToolFrame>();
    private boolean _isEditMode;
    
    
    
    public ToolFramesContainerImpl() {
        super();
        initWidget(uiBinder.createAndBindUi(this));
        this.setIsEditMode(true);
    }

    @Override
    public Iterable<CanvasToolFrame> getToolFrames() {
        return this._toolFrameInfos.keySet();
    }

    @Override
    public CanvasToolFrameInfo addToolFrame(CanvasToolFrame toolFrame) {
        if (this._toolFrameInfos.containsKey(toolFrame)) {
            // not good.
            throw new RuntimeException("toolFrame already exists in canvas");
        }
        CanvasToolFrameInfo info = new CanvasToolFrameInfo(toolFrame);
        info.getRegistrations().setEnabled(this._isEditMode);
        this._toolFrameInfos.put(toolFrame, info);
        this.setToolFrameRegistrations(toolFrame, info.getRegistrations().asRegistrationsManager(this));
        this.toolsContainerPanel.add(toolFrame);
        return info;
    }
    
    public RegistrationsManager getRegistrationsManager(CanvasToolFrame toolFrame, Object regsManagerKey)
    {
        return this._toolFrameInfos.get(toolFrame).getRegistrations().asRegistrationsManager(regsManagerKey);
    }

    @Override
    public void removeToolFrame(CanvasToolFrame toolFrame) {
        CanvasToolFrameInfo info = this._toolFrameInfos.get(toolFrame);
        if (null == info) {
            // not good.
            throw new RuntimeException("toolFrame does not exists in canvas");
        }
        info.getRegistrations().clearAll();
        this.toolsContainerPanel.remove(toolFrame);
        this._overToolFrames.remove(toolFrame);
        this._toolFrameInfos.remove(toolFrame);
    }

    private void setToolFrameRegistrations(final CanvasToolFrame toolFrame, RegistrationsManager regs)
    {
        regs.clear();
        regs.addRecurringMultiple(new Func<Void, Iterable<HandlerRegistration>>() {
            @Override public Iterable<HandlerRegistration> apply(Void arg) {
                return ArrayUtils.toList(new HandlerRegistration[] {
                        toolFrame.asWidget().addDomHandler(new MouseOverHandler() {
                            @Override public void onMouseOver(MouseOverEvent event) {
                                addOverToolFrame(toolFrame);
                            }}, MouseOverEvent.getType()),
                        toolFrame.asWidget().addDomHandler(new MouseOutHandler() {
                            @Override public void onMouseOut(MouseOutEvent event) {
                                removeOverToolFrame(toolFrame);
                            }}, MouseOutEvent.getType()),
                });
            }});
    }
    

    private void addOverToolFrame(final CanvasToolFrame toolFrame)
    {
        this._overToolFrames.add(toolFrame);
//        if (_activeToolboxItem instanceof MoveToolboxItem) {
//            toolFrame.setDragging(true);
//        }
    }
    

    private void removeOverToolFrame(final CanvasToolFrame toolFrame)
    {
        this._overToolFrames.remove(toolFrame);
        
//        if (_activeToolboxItem instanceof MoveToolboxItem) {
//            toolFrame.setDragging(false);
//        }
    }

    @Override
    public Set<CanvasToolFrame> getHoveredToolFrames() {
        return this._overToolFrames;
    }

    @Override
    public void setIsEditMode(final boolean isEditMode) {
        if (isEditMode == this._isEditMode)
        {
            return;
        }
        this._isEditMode = isEditMode;
        for (CanvasToolFrameInfo info : this._toolFrameInfos.values())
        {
            info.getRegistrations().setEnabled(isEditMode);
        }
    }
    
    public Set<Entry<CanvasToolFrame,CanvasToolFrameInfo>> getToolFrameInfos()
    {
        return this._toolFrameInfos.entrySet();
    }
}
