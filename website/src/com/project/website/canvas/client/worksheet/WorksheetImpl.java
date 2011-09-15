package com.project.website.canvas.client.worksheet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.data.Point2D;
import com.project.shared.utils.CloneableUtils;
import com.project.shared.utils.ThrowableUtils;
import com.project.website.canvas.client.ToolFactories;
import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.canvastools.base.CanvasToolFactory;
import com.project.website.canvas.client.canvastools.base.CanvasToolFrame;
import com.project.website.canvas.client.canvastools.base.ToolboxItem;
import com.project.website.canvas.client.shared.ZIndexAllocator;
import com.project.website.canvas.client.shared.widgets.DialogWithZIndex;
import com.project.website.canvas.client.worksheet.interfaces.Worksheet;
import com.project.website.canvas.client.worksheet.interfaces.WorksheetView;
import com.project.website.canvas.client.worksheet.interfaces.WorksheetView.OperationStatus;
import com.project.website.canvas.client.worksheet.interfaces.WorksheetView.ToolCreationRequest;
import com.project.website.canvas.shared.contracts.CanvasService;
import com.project.website.canvas.shared.contracts.CanvasServiceAsync;
import com.project.website.canvas.shared.data.CanvasPage;
import com.project.website.canvas.shared.data.CanvasPageOptions;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.canvas.shared.data.Transform2D;
import com.project.website.shared.client.widgets.authentication.invite.InviteWidget;
import com.project.website.shared.client.widgets.authentication.invite.InviteWidget.InviteRequestData;
import com.project.website.shared.contracts.authentication.AuthenticationService;
import com.project.website.shared.contracts.authentication.AuthenticationServiceAsync;
import com.project.website.shared.data.UserProfile;

public class WorksheetImpl implements Worksheet
{
    private CanvasPage page = new CanvasPage();
    private final SimpleEvent<Void> _defaultToolRequestEvent = new SimpleEvent<Void>();
    private final SimpleEvent<Boolean> viewModeEvent = new SimpleEvent<Boolean>();
    private final WorksheetView view;
    private final HashMap<CanvasTool<?>, ToolInstanceInfo> toolInfoMap = new HashMap<CanvasTool<?>, ToolInstanceInfo>();
	private CanvasTool<?> activeToolInstance;
    private ToolboxItem activeToolboxItem;
    private ArrayList<ElementData> _toolClipboard = new ArrayList<ElementData>();

    public WorksheetImpl(WorksheetView view)
    {
        super();
        this.view = view;
        AuthenticationServiceAsync service = getAuthService();
        updateUserSpecificInfo(view, service);
        setRegistrations();
    }

    public void load(Long id)
    {
        CanvasServiceAsync service = (CanvasServiceAsync) GWT.create(CanvasService.class);

        if (null == id) {
            if (null != this.page.id) {
                id = this.page.id;
            }
            else {
            	// Trying to reload when page wasn't yet saved. Might as well do nothing.
                return;
            }
        }

        view.onLoadOperationChange(OperationStatus.PENDING, null);

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
                load(result);
            }
        });
    }

    @Override
    public void load(String idStr)
    {
        load(parsePageIdStr(idStr));
    }

    private Long parsePageIdStr(String idStr)
    {
        Long id = null;
        if ((null != idStr) && (false == idStr.trim().isEmpty()))
        {
            try {
                id = Long.valueOf(idStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return id;
    }

    protected ElementData updateToolData(CanvasToolFrame toolFrame){
        ElementData toolData = toolFrame.getTool().getValue();
        Element frameElement = toolFrame.getElement();
        toolData.zIndex = ZIndexAllocator.getElementZIndex(frameElement);
        toolData.transform = new Transform2D(ElementUtils.getElementOffsetPosition(frameElement),
                toolFrame.getToolSize(), ElementUtils.getRotation(frameElement));
        return toolData;
    }

    protected void invite()
    {
        final DialogWithZIndex dialog = new DialogWithZIndex(false, true);
        final InviteWidget regWidget = new InviteWidget();
        dialog.add(regWidget);
        regWidget.addInviteRequestHandler(new SimpleEvent.Handler<InviteRequestData>() {
            @Override
            public void onFire(InviteRequestData arg)
            {
                regWidget.setEnabled(false);
                inviteRequest(dialog, arg);
            }
        });
        regWidget.addCancelRequestHandler(new Handler<Void>() {
            @Override
            public void onFire(Void arg)
            {
                dialog.hide();
            }
        });
        dialog.setText("Invite a friend to PopSimple.com");
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute()
            {
                dialog.center();
            }
        });
    }

    private void logout()
    {
        AuthenticationServiceAsync service = getAuthService();
        service.logout(new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Window.Location.reload();
            }

            @Override
            public void onFailure(Throwable caught) {
            }
        });
    }

    private AuthenticationServiceAsync getAuthService()
    {
        AuthenticationServiceAsync service =
            (AuthenticationServiceAsync)GWT.create(AuthenticationService.class);
        return service;
    }

    @Override
    public void save()
    {
        // TODO: Defrag zIndex of all tools before saving.
        ArrayList<ElementData> activeElems = new ArrayList<ElementData>();
        for (Entry<CanvasTool<? extends ElementData>, ToolInstanceInfo> entry : toolInfoMap.entrySet()) {
            ToolInstanceInfo toolInfo = entry.getValue();
            ElementData toolData = this.updateToolData(toolInfo.toolFrame);
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
        if (null != this.activeToolboxItem.getToolFactory())
        {
            this.setActiveToolInstance(null);
        }
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
        this.setActiveToolInstance(toolFrame);
        return toolFrame;
    }

    private CanvasToolFrame createToolInstanceFromData(ElementData newElement)
    {
        CanvasToolFactory<? extends CanvasTool<? extends ElementData>> factory = ToolFactories.INSTANCE.get(newElement.factoryUniqueId);
        CanvasToolFrame toolFrame = this.createToolInstance(newElement.transform, factory, false);
        toolFrame.getTool().setElementData(newElement);
        toolFrame.setActive(false);
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
                toolFrame.setActive(true);
                if (arg.getFactory().isOneShot()) {
                    _defaultToolRequestEvent.dispatch(null);
                }
                else
                {
                    setActiveToolboxItem(activeToolboxItem);
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
        view.addLogoutHandler(new Handler<Void>() {
            @Override
            public void onFire(Void arg) {
                logout();
            }
        });
        view.addInviteHandler(new Handler<Void>() {
            @Override
            public void onFire(Void arg) {
                invite();
            }
        });

        view.addLoadHandler(new Handler<String>() {
            @Override
            public void onFire(String idStr)
            {
                updateLoadedPageURL(idStr);
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
        view.addCopyToolHandler(new Handler<ArrayList<CanvasToolFrame>>() {
            @Override
            public void onFire(ArrayList<CanvasToolFrame> arg) {
                copyToolsToClipboard(arg);
            }
        });
        view.addPasteToolHandler(new Handler<Void>() {
            @Override
            public void onFire(Void arg) {
                pasteToolsFromClipboard();
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
        view.addActiveToolFrameChangedHandler(new Handler<CanvasToolFrame>() {
			@Override
			public void onFire(CanvasToolFrame frame) {
		    	setActiveToolInstance(frame);
			}
		});
        view.addRemoveToolsRequest(new Handler<ArrayList<CanvasToolFrame>>() {
			@Override
			public void onFire(ArrayList<CanvasToolFrame> arg) {
				removeToolInstances(arg);
			}
		});
    }

    protected void updateLoadedPageURL(String idStr)
    {
        Long id = parsePageIdStr(idStr);
        if (null == id) {
        	return;
        }
        if ((null != this.page.id) && (false == this.page.id.equals(id))) {
            // Page id changed.
            // Change the URL hash and trigger a history load event.
            String newURL = Window.Location.createUrlBuilder().setHash(id.toString()).buildString();
            Window.Location.replace(newURL);
            return;
        }
        // Page id not changed, just reload
        this.load(idStr);
    }

    private void copyToolsToClipboard(Collection<CanvasToolFrame> toolFrames)
    {
        this._toolClipboard.clear();
        for (CanvasToolFrame toolFrame : toolFrames)
        {
            this._toolClipboard.add((ElementData)CloneableUtils.clone(
                    updateToolData(toolFrame)));
        }
    }

    private void pasteToolsFromClipboard()
    {
        if (this._toolClipboard.isEmpty())
        {
            return;
        }
        view.clearToolFrameSelection();
        for (ElementData data : _toolClipboard)
        {
            ElementData offsetData = (ElementData)CloneableUtils.clone(data);
            //TODO: does it make sense that the Worksheet will add the offset?
            offsetData.transform.translation =
                offsetData.transform.translation.plus(new Point2D(10, 10));
            view.selectToolFrame(createToolInstanceFromData(offsetData));
        }
    }

	private void setActiveToolInstance(CanvasToolFrame toolFrame)
	{
        CanvasTool<?> tool = toolFrame != null ? toolFrame.getTool() : null;
		if (tool == this.activeToolInstance) {
			return;
		}
		if (null != this.activeToolInstance) {
		    ToolInstanceInfo toolInfo = this.toolInfoMap.get(this.activeToolInstance);
		    if (null != toolInfo)
		    {
		        toolInfo.toolFrame.setActive(false);
		    }
		}
		this.activeToolInstance = tool;
		if (null != toolFrame) {
		    toolFrame.setActive(true);
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
        _defaultToolRequestEvent.dispatch(null);
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

    private void removeToolInstances(ArrayList<CanvasToolFrame> toolFrames)
    {
    	for (CanvasToolFrame toolFrame : toolFrames)
    	{
    		this.removeToolInstance(toolFrame);
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

    private void inviteRequest(final DialogWithZIndex dialog, InviteRequestData arg)
    {
        AuthenticationServiceAsync service = getAuthService();
        service.invite(arg.getEmail(), arg.getMessage(), arg.getName(), new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result)
            {
                Window.alert("Invite sent!");
                dialog.hide();
            }

            @Override
            public void onFailure(Throwable caught)
            {
                Window.alert("Error: " + caught.toString());
                dialog.hide();
            }
        });
    }


    private void updateUserSpecificInfo(WorksheetView view, AuthenticationServiceAsync service)
    {
        this.view.setUserProfile(null);
        final WorksheetImpl that = this;
        service.getUserProfile(new AsyncCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile result)
            {
                that.view.setUserProfile(result);
            }

            @Override
            public void onFailure(Throwable caught)
            {
            }
        });
    }

    @Override
    public HandlerRegistration addDefaultToolRequestHandler(SimpleEvent.Handler<Void> handler)
    {
        return _defaultToolRequestEvent.addHandler(handler);
    }

    @Override
    public HandlerRegistration addViewModeChangedHandler(Handler<Boolean> handler)
    {
        return this.viewModeEvent.addHandler(handler);
    }
}

