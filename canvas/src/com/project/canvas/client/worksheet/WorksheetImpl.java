package com.project.canvas.client.worksheet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.canvas.client.canvastools.Image.ImageToolFactory;
import com.project.canvas.client.canvastools.TaskList.TaskListToolFactory;
import com.project.canvas.client.canvastools.TextEdit.TextEditToolFactory;
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolFactory;
import com.project.canvas.client.canvastools.base.CanvasToolFrame;
import com.project.canvas.client.canvastools.base.ToolboxItem;
import com.project.canvas.client.shared.ElementUtils;
import com.project.canvas.client.shared.RegistrationsManager;
import com.project.canvas.client.shared.ZIndexAllocator;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.client.worksheet.WorksheetView.OperationStatus;
import com.project.canvas.client.worksheet.WorksheetView.ToolCreationRequest;
import com.project.canvas.shared.contracts.CanvasService;
import com.project.canvas.shared.contracts.CanvasServiceAsync;
import com.project.canvas.shared.data.CanvasPage;
import com.project.canvas.shared.data.CanvasPageOptions;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.ImageData;
import com.project.canvas.shared.data.Point2D;
import com.project.canvas.shared.data.TaskListData;
import com.project.canvas.shared.data.TextData;
import com.project.canvas.shared.data.Transform2D;

public class WorksheetImpl implements Worksheet
{
    public final SimpleEvent<Void> defaultToolRequestEvent = new SimpleEvent<Void>();

    public final SimpleEvent<Boolean> viewModeEvent = new SimpleEvent<Boolean>();
    private WorksheetView view;

    protected CanvasPage page = new CanvasPage();

    ToolboxItem activeToolboxItem;

    RegistrationsManager activeToolRegistrations = new RegistrationsManager();

    final HashMap<CanvasTool<?>, ToolInstanceInfo> toolInfoMap = new HashMap<CanvasTool<?>, ToolInstanceInfo>();

    public WorksheetImpl(WorksheetView view)
    {
        super();
        this.view = view;
        setRegistrations();
    }

    @Override
    public SimpleEvent<Void> getDefaultToolRequestEvent()
    {
        return defaultToolRequestEvent;
    }

    @Override
    public SimpleEvent<Boolean> getViewModeEvent()
    {
        return viewModeEvent;
    }

    @Override
    public void load(String idStr)
    {
        Long id = null;
        try {
            id = Long.valueOf(idStr);
        } catch (NumberFormatException e) {
            return;
        }
        load(id);
    }

    @Override
    public void save()
    {
        // TODO: Defrag zIndex of all tools before saving.
        ArrayList<ElementData> activeElems = new ArrayList<ElementData>();
        for (Entry<CanvasTool<? extends ElementData>, ToolInstanceInfo> entry : toolInfoMap.entrySet()) {
            CanvasTool<? extends ElementData> tool = entry.getKey();
            ToolInstanceInfo toolInfo = entry.getValue();
            ElementData toolData = tool.getValue();
            int x = Integer.valueOf(toolInfo.toolFrame.getElement().getOffsetLeft());
            int y = Integer.valueOf(toolInfo.toolFrame.getElement().getOffsetTop());
            toolData.zIndex = ZIndexAllocator.getElementZIndex(toolInfo.toolFrame.getElement());
            toolData.transform = new Transform2D(new Point2D(x, y), toolInfo.toolFrame.getToolSize(), ElementUtils.getRotation(toolInfo.toolFrame.getElement()));
            activeElems.add(toolData);
        }
        this.page.elements.clear();
        this.page.elements.addAll(activeElems);

        CanvasServiceAsync service = (CanvasServiceAsync) GWT.create(CanvasService.class);

        view.onSaveOperationChange(OperationStatus.PENDING, null);

        service.SavePage(page, new AsyncCallback<CanvasPage>() {
            @Override
            public void onFailure(Throwable caught)
            {
                view.onSaveOperationChange(OperationStatus.FAILURE, caught.toString());
            }

            @Override
            public void onSuccess(CanvasPage result)
            {
                view.onSaveOperationChange(OperationStatus.SUCCESS, null);
                String newURL = Window.Location.createUrlBuilder().setHash(result.id.toString()).buildString();
                Window.Location.replace(newURL);
            }
        });
    }

    @Override
    public void setActiveToolboxItem(ToolboxItem toolboxItem)
    {
        this.clearActiveToolboxItem();
        this.view.setActiveToolboxItem(toolboxItem);
        this.activeToolboxItem = toolboxItem;
    }

    private CanvasToolFrame createToolInstance(final Point2D relativePos,
            CanvasToolFactory<? extends CanvasTool<? extends ElementData>> toolFactory)
    {
        return this.createToolInstance(new Transform2D(relativePos, null, 0), toolFactory);
    }

    private CanvasToolFrame createToolInstance(final Transform2D transform,
            CanvasToolFactory<? extends CanvasTool<? extends ElementData>> toolFactory)
    {
        final CanvasTool<? extends ElementData> tool = toolFactory.create();
        final CanvasToolFrame toolFrame = new CanvasToolFrame(tool);

        final Point2D creationOffset = toolFactory.getCreationOffset();
        ToolInstanceInfo toolInfo = new ToolInstanceInfo(toolFactory, toolFrame, null);
        this.toolInfoMap.put(tool, toolInfo);

        RegistrationsManager regs = registerToolInstanceHandlers(toolFrame, toolInfo);

        view.addToolInstanceWidget(toolFrame, transform, creationOffset);
        toolInfo.killRegistration = tool.getKillRequestedEvent().addHandler(new SimpleEvent.Handler<String>() {
            @Override
            public void onFire(String arg)
            {
                removeToolInstance(toolFrame);
            }
        });
        regs.add(toolInfo.killRegistration);

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute()
            {
                // TODO: Set zIndex according to the data instead.
                ZIndexAllocator.allocateSetZIndex(toolFrame.getElement());
            }
        });
        tool.bind();
        return toolFrame;
    }

    private void createToolInstancesFromData(HashMap<Long, ElementData> updatedElements)
    {
        for (ElementData newElement : this.sortByZIndex(updatedElements.values())) {
            CanvasToolFactory<? extends CanvasTool<? extends ElementData>> factory = null;
            Class<?> cls = newElement.getClass();
            if (cls.equals(TextData.class)) {
                factory = new TextEditToolFactory();
            } else if (cls.equals(TaskListData.class)) {
                factory = new TaskListToolFactory();
            } else if (cls.equals(ImageData.class)) {
                factory = new ImageToolFactory();
            }

            if (null == factory) {
                continue;
            }
            // TODO: Refactor
            CanvasToolFrame toolFrame = this.createToolInstance(newElement.transform, factory);
            toolFrame.getTool().setElementData(newElement);
            toolFrame.getTool().setActive(false);
        }
    }

    private void setRegistrations()
    {
        view.addToolCreationRequestHandler(new Handler<WorksheetView.ToolCreationRequest>() {
            @Override
            public void onFire(ToolCreationRequest arg)
            {
                CanvasToolFrame toolFrame = createToolInstance(arg.getPosition(), arg.getFactory());
                toolFrame.getTool().setActive(true);
                if (arg.getFactory().isOneShot()) {
                    defaultToolRequestEvent.dispatch(null);
                }
            }
        });

        view.addSaveHandler(new Handler<Void>() {
            @Override
            public void onFire(Void arg)
            {
                save();
            }
        });
        view.addLoadHandler(new Handler<String>() {
            @Override
            public void onFire(String idStr)
            {
                load(idStr);
            }
        });
        view.addViewHandler(new Handler<Void>() {
            @Override
            public void onFire(Void arg)
            {
                viewModeEvent.dispatch(true);
                view.setViewMode(true);
                final RegistrationsManager regs = new RegistrationsManager();
                regs.add(view.addStopOperationHandler(new SimpleEvent.Handler<Void>() {
                    @Override
                    public void onFire(Void arg)
                    {
                        view.setViewMode(false);
                        viewModeEvent.dispatch(false);
                        regs.clear();
                    }
                }));
            }
        });
        view.addOptionsUpdatedHandler(new Handler<CanvasPageOptions>() {
            @Override
            public void onFire(CanvasPageOptions arg)
            {
                updateOptions(arg);
            }
        });
        view.addStopOperationHandler(new Handler<Void>() {
            @Override
            public void onFire(Void arg)
            {
                escapeOperation();
            }
        });
    }

    protected void clearActiveToolboxItem()
    {
        activeToolRegistrations.clear();
        view.clearActiveToolboxItem();
    }

    protected void escapeOperation()
    {
        clearActiveToolboxItem();
        // TODO dispatch stop operation
        // stopOperationEvent.dispatch(null);
        defaultToolRequestEvent.dispatch(null);
    }

    protected void load(CanvasPage result)
    {
        this.removeAllTools();
        // TODO: Currently because it's static.
        ZIndexAllocator.reset();

        this.page = result;
        this.updateOptions(result.options);
        HashMap<Long, ElementData> updatedElements = new HashMap<Long, ElementData>();
        for (ElementData elem : this.page.elements) {
            updatedElements.put(elem.id, elem);
        }

        // TODO: Support updating already existing items.
        // for (Entry<CanvasTool<? extends ElementData>, ToolInstanceInfo> entry
        // : new HashSet<Entry<CanvasTool<? extends ElementData>,
        // ToolInstanceInfo>>(toolInfoMap.entrySet()))
        // {
        // CanvasTool<? extends ElementData> tool = entry.getKey();
        // ToolInstanceInfo toolInfo = entry.getValue();
        // ElementData toolData = tool.getValue();
        // if (updatedElements.containsKey(toolData.id)) {
        // tool.setElementData(updatedElements.get(toolData.id));
        // updatedElements.remove(toolData.id);
        // }
        // else {
        // this.removeToolInstance(toolInfo.toolFrame);
        // }
        // }
        createToolInstancesFromData(updatedElements);
    }

    protected RegistrationsManager registerToolInstanceHandlers(final CanvasToolFrame toolFrame,
            ToolInstanceInfo toolInfo)
    {
        RegistrationsManager regs = toolInfo.registrations;
        regs.add(toolFrame.getCloseRequest().addHandler(new SimpleEvent.Handler<Void>() {
            @Override
            public void onFire(Void arg)
            {
                removeToolInstance(toolFrame);
            }
        }));

        regs.add(toolFrame.addMoveBackRequestHandler(new SimpleEvent.Handler<Void>() {
            @Override
            public void onFire(Void arg)
            {
                ZIndexAllocator.moveElementBelow(toolFrame.getElement());
            }
        }));

        regs.add(toolFrame.addMoveFrontRequestHandler(new SimpleEvent.Handler<Void>() {
            @Override
            public void onFire(Void arg)
            {
                ZIndexAllocator.moveElementAbove(toolFrame.getElement());
            }
        }));
        return regs;
    }

    protected void removeAllTools()
    {
        for (ToolInstanceInfo toolInfo : new ArrayList<ToolInstanceInfo>(this.toolInfoMap.values())) {
            this.removeToolInstance(toolInfo.toolFrame);
        }
    }

    protected void removeToolInstance(CanvasToolFrame toolFrame)
    {
        ZIndexAllocator.deallocateZIndex(toolFrame.getElement());
        ToolInstanceInfo info = this.toolInfoMap.remove(toolFrame.getTool());
        view.removeToolInstanceWidget(toolFrame);
        info.registrations.clear();
    }

    protected Collection<ElementData> sortByZIndex(Collection<ElementData> elements)
    {
        TreeMap<Integer, ElementData> elementsByZIndex = new TreeMap<Integer, ElementData>();
        for (ElementData element : elements) {
            elementsByZIndex.put(element.zIndex, element);
        }
        return elementsByZIndex.values();
    }

    protected void updateOptions(CanvasPageOptions value)
    {
        if (null == value) {
            return;
        }
        this.page.options = value;
        view.setOptions(value);
    }

    public void load(Long id)
    {
        CanvasServiceAsync service = (CanvasServiceAsync) GWT.create(CanvasService.class);

        view.onLoadOperationChange(OperationStatus.PENDING, null);
        
        if (null == id) {
            if (null != this.page.id) {
                id = this.page.id;
            }
            else {
                // Can't reload a page that was never saved!
                view.onLoadOperationChange(OperationStatus.FAILURE, "Can't reload because this page was never saved.");
                return;
            }
        }

        service.GetPage(id, new AsyncCallback<CanvasPage>() {
            @Override
            public void onFailure(Throwable caught)
            {
                view.onLoadOperationChange(OperationStatus.FAILURE, caught.toString());
            }

            @Override
            public void onSuccess(CanvasPage result)
            {
                view.onLoadOperationChange(OperationStatus.SUCCESS, null);
                if (null != result) {
                    String newURL = Window.Location.createUrlBuilder().setHash(result.id.toString()).buildString();
                    Window.Location.replace(newURL);
                    load(result);
                } else {
                    Window.alert("No such page on server.");
                }
            }
        });
    }
}
