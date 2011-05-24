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
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolFactory;
import com.project.canvas.client.canvastools.base.CanvasToolFrame;
import com.project.canvas.client.canvastools.base.ToolboxItem;
import com.project.canvas.client.canvastools.image.ImageToolFactory;
import com.project.canvas.client.canvastools.tasklist.TaskListToolFactory;
import com.project.canvas.client.canvastools.textedit.TextEditToolFactory;
import com.project.canvas.client.canvastools.video.VideoToolFactory;
import com.project.canvas.client.shared.ElementUtils;
import com.project.canvas.client.shared.RegistrationsManager;
import com.project.canvas.client.shared.ZIndexAllocator;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.client.worksheet.interfaces.Worksheet;
import com.project.canvas.client.worksheet.interfaces.WorksheetView;
import com.project.canvas.client.worksheet.interfaces.WorksheetView.OperationStatus;
import com.project.canvas.client.worksheet.interfaces.WorksheetView.ToolCreationRequest;
import com.project.canvas.shared.ThrowableUtils;
import com.project.canvas.shared.contracts.CanvasService;
import com.project.canvas.shared.contracts.CanvasServiceAsync;
import com.project.canvas.shared.data.CanvasPage;
import com.project.canvas.shared.data.CanvasPageOptions;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.Point2D;
import com.project.canvas.shared.data.Transform2D;

public class WorksheetImpl implements Worksheet
{
    private CanvasPage page = new CanvasPage();
    private final SimpleEvent<Void> defaultToolRequestEvent = new SimpleEvent<Void>();
    private final SimpleEvent<Boolean> viewModeEvent = new SimpleEvent<Boolean>();
    private final WorksheetView view;
    private final HashMap<CanvasTool<?>, ToolInstanceInfo> toolInfoMap = new HashMap<CanvasTool<?>, ToolInstanceInfo>();
	private CanvasTool<?> activeToolInstance;
    private ToolboxItem activeToolboxItem;

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
                view.onSaveOperationChange(OperationStatus.FAILURE, 
                        ThrowableUtils.joinStackTrace(caught));
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
        this.view.setActiveToolboxItem(toolboxItem);
        if (toolboxItem == this.activeToolboxItem) {
            return;
        }
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

        ToolInstanceInfo toolInfo = new ToolInstanceInfo(toolFactory, toolFrame, null);
        this.toolInfoMap.put(tool, toolInfo);

        RegistrationsManager regs = registerToolInstanceHandlers(toolFrame, toolInfo);

        view.addToolInstanceWidget(toolFrame, transform, toolFactory.getCreationOffset());
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
        this.setActiveToolInstance(tool);
        return toolFrame;
    }

    private void createToolInstancesFromData(HashMap<Long, ElementData> updatedElements)
    {
        //TODO: Refactor
        for (ElementData newElement : this.sortByZIndex(updatedElements.values())) {
            CanvasToolFactory<? extends CanvasTool<? extends ElementData>> factory = null;
            if (newElement.factoryUniqueId.equals(TextEditToolFactory.UNIQUE_ID)) 
            {
                factory = new TextEditToolFactory();
            } 
            else if (newElement.factoryUniqueId.equals(TaskListToolFactory.UNIQUE_ID)) 
            { 
                factory = new TaskListToolFactory();
            } 
            else if (newElement.factoryUniqueId.equals(ImageToolFactory.UNIQUE_ID)) 
            {
                factory = new ImageToolFactory();
            }
            else if (newElement.factoryUniqueId.equals(VideoToolFactory.UNIQUE_ID)) 
            {
                factory = new VideoToolFactory();
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
            	CanvasToolFactory<? extends CanvasTool<? extends ElementData>> factory = arg.getFactory();
            	if (null == factory) {
            		return;
            	}
                CanvasToolFrame toolFrame = createToolInstance(arg.getPosition(), factory);
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
        view.addToolFrameClickHandler(new Handler<CanvasToolFrame>() {
			@Override
			public void onFire(CanvasToolFrame frame) {
		    	setActiveToolInstance(frame.getTool());
			}
		});
    }

	private void setActiveToolInstance(CanvasTool<?> tool) 
	{
		if (tool == this.activeToolInstance) {
			return;
		}
		if (null != this.activeToolInstance) {
			this.activeToolInstance.setActive(false);
		}
		this.activeToolInstance = tool;
		if (null != tool) {
		    tool.setActive(true);
		}
	}

	private void clearActiveToolboxItem()
    {
        view.clearActiveToolboxItem();
    }

    private void escapeOperation()
    {
        clearActiveToolboxItem();
        setActiveToolInstance(null);
        // TODO dispatch stop operation
        // stopOperationEvent.dispatch(null);
        defaultToolRequestEvent.dispatch(null);
    }

    private void load(CanvasPage result)
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

    private RegistrationsManager registerToolInstanceHandlers(final CanvasToolFrame toolFrame,
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

    private void removeAllTools()
    {
        for (ToolInstanceInfo toolInfo : new ArrayList<ToolInstanceInfo>(this.toolInfoMap.values())) {
            this.removeToolInstance(toolInfo.toolFrame);
        }
    }

    private void removeToolInstance(CanvasToolFrame toolFrame)
    {
        ZIndexAllocator.deallocateZIndex(toolFrame.getElement());
        ToolInstanceInfo info = this.toolInfoMap.remove(toolFrame.getTool());
        view.removeToolInstanceWidget(toolFrame);
        info.registrations.clear();
    }

    private Collection<ElementData> sortByZIndex(Collection<ElementData> elements)
    {
        TreeMap<Integer, ElementData> elementsByZIndex = new TreeMap<Integer, ElementData>();
        for (ElementData element : elements) {
            elementsByZIndex.put(element.zIndex, element);
        }
        return elementsByZIndex.values();
    }

    private void updateOptions(CanvasPageOptions value)
    {
        if (null == value) {
            return;
        }
        this.page.options = value;
        view.setOptions(value);
    }
}
