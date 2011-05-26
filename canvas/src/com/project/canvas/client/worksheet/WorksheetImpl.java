package com.project.canvas.client.worksheet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Element;
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
            	// Trying to reload when page wasn't yet saved. Might as well do nothing.
                return;
            }
        }

        service.getPage(id, new AsyncCallback<CanvasPage>() {
            @Override
            public void onFailure(Throwable caught)
            {
                view.onLoadOperationChange(OperationStatus.FAILURE, caught.toString());
            }

            @Override
            public void onSuccess(CanvasPage result)
            {
                if (null == result) {
                    view.onLoadOperationChange(OperationStatus.FAILURE, "Page not found");
                    return;
                }
                view.onLoadOperationChange(OperationStatus.SUCCESS, null);
                serverLoadCompleted(result);
            }
        });
    }

    @Override
    public void load(String idStr)
    {
        Long id = null;
        if ((null != idStr) && (false == idStr.trim().isEmpty()))
        {
            try {
                id = Long.valueOf(idStr);
            } catch (NumberFormatException e) {
                // TODO instead of catching (or in addition) throw an exception? currently quietly ignores...
                return;
            }
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
            Element frameElement = toolInfo.toolFrame.getElement();
            toolData.zIndex = ZIndexAllocator.getElementZIndex(toolInfo.toolFrame.getElement());
			toolData.transform = new Transform2D(ElementUtils.getElementOffsetPosition(frameElement),
 												 toolInfo.toolFrame.getToolSize(),
 												 ElementUtils.getRotation(frameElement));
            activeElems.add(toolData);
        }
        this.page.elements.clear();
        this.page.elements.addAll(activeElems);

        CanvasServiceAsync service = (CanvasServiceAsync) GWT.create(CanvasService.class);

        view.onSaveOperationChange(OperationStatus.PENDING, null);

        service.savePage(page, new AsyncCallback<CanvasPage>() {
            @Override
            public void onFailure(Throwable caught)
            {
                view.onSaveOperationChange(OperationStatus.FAILURE, 
                        ThrowableUtils.joinStackTrace(caught));
            }

            @Override
            public void onSuccess(CanvasPage result)
            {
                // TODO: see issue #92
                load(result);
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
        this.setActiveToolInstance(null);
    }

    private CanvasToolFrame createToolInstance(final Point2D relativePos,
            CanvasToolFactory<? extends CanvasTool<? extends ElementData>> toolFactory)
    {
        return this.createToolInstance(new Transform2D(relativePos, null, 0), toolFactory, true);
    }

    private CanvasToolFrame createToolInstance(final Transform2D transform,
            CanvasToolFactory<? extends CanvasTool<? extends ElementData>> toolFactory, boolean useCreationOffset)
    {
        final CanvasTool<? extends ElementData> tool = toolFactory.create();
        final CanvasToolFrame toolFrame = new CanvasToolFrame(tool);

        ToolInstanceInfo toolInfo = new ToolInstanceInfo(toolFactory, toolFrame, null);
        this.toolInfoMap.put(tool, toolInfo);

        RegistrationsManager regs = registerToolInstanceHandlers(toolFrame, toolInfo);

        Point2D creationOffset = useCreationOffset ? toolFactory.getCreationOffset() : Point2D.zero;
		view.addToolInstanceWidget(toolFrame, transform, creationOffset);
        toolInfo.killRegistration = tool.addKillRequestEventHandler(new SimpleEvent.Handler<String>() {
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

    private CanvasToolFrame createToolInstanceFromData(ElementData newElement)
    {
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
            return null;
        }
        // TODO: Refactor
        CanvasToolFrame toolFrame = this.createToolInstance(newElement.transform, factory, false);
        toolFrame.getTool().setElementData(newElement);
        toolFrame.getTool().setActive(false);
        return toolFrame;
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
			    CanvasTool<?> tool = frame != null ? frame.getTool() : null;
		    	setActiveToolInstance(tool);
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

    private void load(CanvasPage newPage)
    {
        this.page = newPage;
        this.updateOptions(this.page.options);
        
        HashMap<Long, ElementData> newElements = new HashMap<Long, ElementData>();
        for (ElementData elem : this.page.elements) {
            newElements.put(elem.id, elem);
        }
        
        HashSet<Entry<CanvasTool<? extends ElementData>, ToolInstanceInfo>> entries = 
            new HashSet<Entry<CanvasTool<? extends ElementData>, ToolInstanceInfo>>(toolInfoMap.entrySet());
        
        // Update existing and remove deleted tools
        for (Entry<CanvasTool<? extends ElementData>, ToolInstanceInfo> entry : entries) {
            CanvasTool<? extends ElementData> tool = entry.getKey();
            ToolInstanceInfo toolInfo = entry.getValue();
            ElementData oldData = tool.getValue();
            ElementData newData = newElements.get(oldData.id);
            if (null != newData) {
                tool.setElementData(newData);
                view.setToolFrameTransform(toolInfo.toolFrame, newData.transform, Point2D.zero);
                newElements.remove(oldData.id);
            } else {
                this.removeToolInstance(toolInfo.toolFrame);
            }
        }
        
        // Create the new tool instances
        for (ElementData newElement : this.sortByZIndex(newElements.values())) {
            this.createToolInstanceFromData(newElement);
        }
    }

    private RegistrationsManager registerToolInstanceHandlers(final CanvasToolFrame toolFrame,
            ToolInstanceInfo toolInfo)
    {
        RegistrationsManager regs = toolInfo.registrations;
        regs.add(toolFrame.addCloseRequestHandler(new SimpleEvent.Handler<Void>() {
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

    private void serverLoadCompleted(CanvasPage result)
    {
        if ((null != this.page.id) && (false == this.page.id.equals(result.id))) {
            String newURL = Window.Location.createUrlBuilder().setHash(result.id.toString()).buildString();
            Window.Location.replace(newURL);
        }
        else {
            load(result);
        }
    }
}
