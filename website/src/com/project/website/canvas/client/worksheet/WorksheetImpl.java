package com.project.website.canvas.client.worksheet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.common.base.Objects;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.UrlUtils;
import com.project.shared.client.utils.ZIndexAllocator;
import com.project.shared.client.utils.widgets.DialogWithZIndex;
import com.project.shared.data.Pair;
import com.project.shared.data.Point2D;
import com.project.shared.data.funcs.AsyncFunc;
import com.project.shared.data.funcs.Func;
import com.project.shared.utils.GenericUtils;
import com.project.shared.utils.QueryString;
import com.project.shared.utils.StringUtils;
import com.project.shared.utils.ThrowableUtils;
import com.project.website.canvas.client.ToolFactories;
import com.project.website.canvas.client.canvastools.base.CanvasToolFrameImpl;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasTool;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasToolFactory;
import com.project.website.canvas.client.canvastools.base.interfaces.CanvasToolFrame;
import com.project.website.canvas.client.canvastools.base.interfaces.ToolboxItem;
import com.project.website.canvas.client.canvastools.image.ImageTool;
import com.project.website.canvas.client.canvastools.image.ImageToolFactory;
import com.project.website.canvas.client.shared.ImageOptionTypes;
import com.project.website.canvas.client.shared.ImageOptionsProviderUtils;
import com.project.website.canvas.client.shared.UndoManager;
import com.project.website.canvas.client.worksheet.interfaces.Worksheet;
import com.project.website.canvas.client.worksheet.interfaces.WorksheetView;
import com.project.website.canvas.client.worksheet.interfaces.WorksheetView.ImageDropInfo;
import com.project.website.canvas.client.worksheet.interfaces.WorksheetView.OperationStatus;
import com.project.website.canvas.client.worksheet.interfaces.WorksheetView.ToolCreationRequest;
import com.project.website.canvas.shared.contracts.CanvasService;
import com.project.website.canvas.shared.contracts.CanvasServiceAsync;
import com.project.website.canvas.shared.data.CanvasPage;
import com.project.website.canvas.shared.data.CanvasPageOptions;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.canvas.shared.data.Transform2D;
import com.project.website.shared.client.widgets.MessageBox;
import com.project.website.shared.client.widgets.authentication.invite.InviteWidget;
import com.project.website.shared.client.widgets.authentication.invite.InviteWidget.InviteRequestData;
import com.project.website.shared.contracts.authentication.AuthenticationService;
import com.project.website.shared.contracts.authentication.AuthenticationServiceAsync;
import com.project.website.shared.data.QueryParameters;

public class WorksheetImpl implements Worksheet
{
    private CanvasPage page = new CanvasPage();
    private final SimpleEvent<Void> _defaultToolboxItemRequestEvent = new SimpleEvent<Void>();
    private final SimpleEvent<Boolean> viewModeEvent = new SimpleEvent<Boolean>();
    private final WorksheetView view;
    private final HashMap<CanvasTool<?>, ToolInstanceInfo> toolInfoMap = new HashMap<CanvasTool<?>, ToolInstanceInfo>();
	private CanvasTool<?> activeToolInstance;
    private ToolboxItem activeToolboxItem;
    private ArrayList<ElementData> _toolClipboard = new ArrayList<ElementData>();
    private final RegistrationsManager viewModeRegistrations = new RegistrationsManager();
    private boolean _inViewMode = false;

    //TODO: Think about something else. (Hadas)
    private final WorksheetImageOptionsProvider _imageOptionsProvider = new WorksheetImageOptionsProvider();
    private AsyncFunc<Void,Void> newPageAction = AsyncFunc.fromFunc(new Func.VoidAction(){
        @Override public void exec() {
            navigateToNewPage();
    }});

    public WorksheetImpl(WorksheetView view)
    {
        super();
        this.view = view;
        //AuthenticationServiceAsync service = getAuthService();
        //updateUserSpecificInfo(view, service);
        this.setRegistrations();

        this.setDefaultPageOptions();
    }

    @Override
    public HandlerRegistration addDefaultToolboxItemRequestHandler(SimpleEvent.Handler<Void> handler)
    {
        return _defaultToolboxItemRequestEvent.addHandler(handler);
    }

    @Override
    public HandlerRegistration addViewModeChangedHandler(Handler<Boolean> handler)
    {
        return this.viewModeEvent.addHandler(handler);
    }

    @Override
    public void load(String idStr, String pageKey)
    {
        load(parsePageIdStr(idStr), pageKey);
    }

    @Override
	public void load(String idStr, boolean viewMode, String pageKey) {
        if (viewMode) {
            this.setModeView();
        }
        else {
            this.setModeEdit();
        }
		this.load(idStr, pageKey);
	}

    @Override
    public void save()
    {
        this.getSaveFunc()
            .run(null);
    }

    public AsyncFunc<Void, CanvasPage> getSaveFunc()
    {
        return new AsyncFunc<Void, CanvasPage>() {
            @Override protected <S, E> void run(Void arg,
                                                Func<CanvasPage, S> successHandler,
                                                Func<Throwable, E> errorHandler) 
            {
                performSave(successHandler, errorHandler);
            }};
    }

    protected enum NewPageDialogResults {
        CANCEL,
        NEW_BUT_SAVE_FIRST,
        NEW_WITHOUT_SAVING,
    }
    protected class NewPageResultLabels extends Pair<NewPageDialogResults, String> {
        private static final long serialVersionUID = 1L;
        public NewPageResultLabels(NewPageDialogResults a, String b) { super(a, b); } 
    }
    
    protected void newPage() {
        final WorksheetImpl that = this;
        AsyncFunc<Void, NewPageDialogResults> shouldSaveFirst = null; 
        if (this.pageIsEditable()) {
            shouldSaveFirst = MessageBox.<NewPageDialogResults>getShowFunc(
                    "Save before leaving page?", 
                    "Do you want to save your changes before leaving this page? If not, any unsaved changes will be gone forever.",
                    new NewPageResultLabels[] {
                            new NewPageResultLabels(NewPageDialogResults.NEW_BUT_SAVE_FIRST, "Save & New"),
                            new NewPageResultLabels(NewPageDialogResults.NEW_WITHOUT_SAVING, "Don't save, just start a new page"),
                            new NewPageResultLabels(NewPageDialogResults.CANCEL, "Cancel - stay on current page"),
                    },
                    NewPageDialogResults.NEW_BUT_SAVE_FIRST);
        }
        else {
            shouldSaveFirst = AsyncFunc.constFunc(NewPageDialogResults.NEW_BUT_SAVE_FIRST);
        }
        shouldSaveFirst.thenSelect(new Func<NewPageDialogResults, AsyncFunc<NewPageDialogResults,Void>>() {
                  @Override public AsyncFunc<NewPageDialogResults, Void> apply(NewPageDialogResults arg) {
                      switch (arg) {
                      case NEW_BUT_SAVE_FIRST: 
                          return that.getSaveFunc()
                                     .<Void>constResult(null)
                                     .then(newPageAction)
                                     .constArg(null);
                      case NEW_WITHOUT_SAVING: 
                          return newPageAction.constArg(null);
                      case CANCEL:
                          return AsyncFunc.constFunc(null);
                      default: throw new RuntimeException();
                  }}})
          .run(null);
    }


    private void navigateToNewPage() {
        History.newItem("");
        this.load(new CanvasPage());
    }

    private boolean pageIsEditable() {
        return (null == this.page.id) || (false == StringUtils.isWhitespaceOrNull(this.page.key));
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

    private QueryString buildPageQueryString(Long pageId, String key, boolean viewMode)
    {
        QueryString query = QueryString.create(UrlUtils.getUrlEncoder());
        if (null != pageId) {
            query.append(QueryParameters.PAGE_ID, pageId);
        }
        if (null != key) {
            query.append(QueryParameters.PAGE_KEY, key);
        }
        if (viewMode) {
            query.append(QueryParameters.VIEW_MODE_FLAG, (String)null);
        }
        return query;
    }

    private String buildPageUrl(long pageId, String key, boolean viewMode)
    {
        QueryString query = buildPageQueryString(pageId, key, viewMode);
        String newURL = Window.Location.createUrlBuilder().setHash(query.toString()).buildString();
        return newURL;
    }

    private void clearActiveToolboxItem()
    {
        view.clearActiveToolboxItem();
    }

    private void copyToolsToClipboard(Collection<CanvasToolFrame> toolFrames)
    {
        this._toolClipboard.clear();
        for (CanvasToolFrame toolFrame : toolFrames)
        {
            this.updateToolData(toolFrame);
            ElementData data = toolFrame.getTool().getValue();
            this._toolClipboard.add(data.getCloneable().getClone());
        }
    }

    private CanvasToolFrame createToolInstance(final Point2D relativePos,
            CanvasToolFactory<? extends CanvasTool<? extends ElementData>> toolFactory)
    {
        return this.createToolInstance(new Transform2D(relativePos, null, 0), toolFactory, true);
    }

    private CanvasToolFrameImpl createToolInstance(final Transform2D transform,
            CanvasToolFactory<? extends CanvasTool<? extends ElementData>> toolFactory, boolean useCreationOffset)
    {
        final CanvasTool<? extends ElementData> tool = toolFactory.create();
        final CanvasToolFrameImpl toolFrame = new CanvasToolFrameImpl(tool);

        ToolInstanceInfo toolInfo = new ToolInstanceInfo(toolFactory, toolFrame, null);
        this.toolInfoMap.put(tool, toolInfo);

        RegistrationsManager regs = registerToolInstanceHandlers(toolFrame, toolInfo);
        Point2D creationOffset = useCreationOffset ? toolFactory.getCreationOffset() : Point2D.zero;
		view.addToolInstanceWidget(toolFrame, transform, creationOffset, true);
        toolInfo.killRegistration = tool.getToolEvents().addKillRequestEventHandler(
                new SimpleEvent.Handler<Void>() {
            @Override
            public void onFire(Void arg)
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
        tool.setViewMode(this._inViewMode);
        return toolFrame;
    }

    private CanvasToolFrameImpl createToolInstanceFromData(ElementData newElement)
    {
        CanvasToolFactory<? extends CanvasTool<? extends ElementData>> factory = ToolFactories.INSTANCE.get(newElement.factoryUniqueId);
        CanvasToolFrameImpl toolFrame = this.createToolInstance(newElement.transform, factory, false);
        toolFrame.setViewMode(this._inViewMode);
        toolFrame.getTool().setElementData(newElement);
        toolFrame.setActive(false);
        return toolFrame;
    }

    private void escapeOperation()
    {
        this.clearActiveToolboxItem();
        this.setActiveToolInstance(null);
        this.view.clearToolFrameSelection();
        // TODO dispatch stop operation
        // stopOperationEvent.dispatch(null);
        this._defaultToolboxItemRequestEvent.dispatch(null);
    }

    private AuthenticationServiceAsync getAuthService()
    {
        AuthenticationServiceAsync service =
            (AuthenticationServiceAsync)GWT.create(AuthenticationService.class);
        return service;
    }

    private void invite()
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

	private void inviteRequest(final DialogWithZIndex dialog, InviteRequestData arg)
    {
        AuthenticationServiceAsync service = getAuthService();
        service.invite(arg.getEmail(), arg.getMessage(), arg.getName(), new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught)
            {
                Window.alert("Error: " + caught.toString());
                dialog.hide();
            }

            @Override
            public void onSuccess(Void result)
            {
                Window.alert("Invite sent!");
                dialog.hide();
            }
        });
    }

	private void load(CanvasPage newPage)
    {
	    UndoManager.get().clear();
	    
	    if (false == pageIsEditable()) {
	    	newPage.id = null;
	    	newPage.key = null;
	    	List<ElementData> elements = newPage.elements;
	    	newPage.elements = new ArrayList<ElementData>();
            // TODO: elements may contain sub-objects in them that are also persisted with an ID. So this cloning may not ensure
	    	// that saving the elements later won't overwrite that data.
            for (ElementData elem : elements)
            {
            	newPage.elements.add(elem.getCloneable().getClone());
            }
	    }
	    
	    this.replaceCurrentPage(newPage);
	    
        this.view.setPageEditable(pageIsEditable());
        view.setOptions(newPage.options);
        this.updateHistoryToken();

        HashMap<String, ElementData> newElements = new HashMap<String, ElementData>();
        for (ElementData elem : this.page.elements) {
            newElements.put(elem.uniqueId, elem);
        }

        HashSet<Entry<CanvasTool<? extends ElementData>, ToolInstanceInfo>> entries =
            new HashSet<Entry<CanvasTool<? extends ElementData>, ToolInstanceInfo>>(toolInfoMap.entrySet());

        // Update existing and remove deleted tools
        for (Entry<CanvasTool<? extends ElementData>, ToolInstanceInfo> entry : entries) {
            CanvasTool<? extends ElementData> tool = entry.getKey();
            ToolInstanceInfo toolInfo = entry.getValue();
            ElementData oldData = tool.getValue();
            ElementData newData = newElements.get(oldData.uniqueId);
            if (null != newData) {
                tool.setElementData(newData);
                view.setToolFrameTransform(toolInfo.toolFrame, newData.transform, Point2D.zero);
                newElements.remove(oldData.uniqueId);
            } else {
                this.removeToolInstance(toolInfo.toolFrame);
            }
        }

        // Create the new tool instances
        for (ElementData newElement : this.sortByZIndex(newElements.values())) {
            this.createToolInstanceFromData(newElement);
        }
    }

    private void replaceCurrentPage(CanvasPage newPage) {
        // The server never send us the page key, unless this is the first time the page is saved.
	    // To make sure we don't lose the key we save it aside first.
	    String key = this.page.key;
        this.page = newPage;
        // If the loaded page doesn't contain a key, but has the same ID as the current page, reuse the same key
	    if ((null != this.page) && (GenericUtils.areEqual(newPage.id, this.page.id)) && (null == newPage.key)) 
	    {
	        // If our current page has the same id as the newly loaded page, use the same key
	        this.page.key = key;
	    }
    }

    private void load(Long id, String pageKey)
    {
        CanvasServiceAsync service = (CanvasServiceAsync) GWT.create(CanvasService.class);

        this.updateViewForPageId(id, pageKey); 

        if (null == id) {
            if (null != this.page.id) {
                id = this.page.id;
            }
            else {
            	// Trying to reload when page wasn't yet saved. Might as well do nothing.
                return;
            }
        }

        if (Objects.equal(id, this.page.id)) {
            return;
        }
        
        if (null != this.page.id)
        {
            // Page is being replaced.
            this.page = new CanvasPage();
        }
        
        if (StringUtils.isWhitespaceOrNull(this.page.key)) {
            this.page.key = pageKey;
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

    private void updateViewForPageId(Long id, String key)
    {
        this.view.setViewLinkTargetHistoryToken(this.buildPageQueryString(id, null, true).toString());
    }

    private void logout()
    {
        AuthenticationServiceAsync service = getAuthService();
        service.logout(new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Void result) {
                Window.Location.reload();
            }
        });
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

    private void pasteToolsFromClipboard()
    {
        if (this._toolClipboard.isEmpty())
        {
            return;
        }
        view.clearToolFrameSelection();
        for (ElementData data : _toolClipboard)
        {
            ElementData offsetData = data.getCloneable().getClone();
            //TODO: does it make sense that the Worksheet will add the offset?
            offsetData.transform.translation =
                offsetData.transform.translation.plus(new Point2D(10, 10));
            view.selectToolFrame(createToolInstanceFromData(offsetData));
        }
    }

    private RegistrationsManager registerToolInstanceHandlers(final CanvasToolFrameImpl toolFrame,
            ToolInstanceInfo toolInfo)
    {
        RegistrationsManager regs = toolInfo.registrations;
        regs.add(toolFrame.addCloseRequestHandler(new SimpleEvent.Handler<Void>() {
            @Override public void onFire(Void arg) {
                removeToolInstance(toolFrame);
            }
        }));

        regs.add(toolFrame.addMoveBackRequestHandler(new SimpleEvent.Handler<Void>() {
            @Override public void onFire(Void arg) {
                ZIndexAllocator.moveElementBelow(toolFrame.getElement());
            }
        }));

        regs.add(toolFrame.addMoveFrontRequestHandler(new SimpleEvent.Handler<Void>() {
            @Override public void onFire(Void arg) {
                ZIndexAllocator.moveElementAbove(toolFrame.getElement());
            }
        }));
        return regs;
    }

    private void removeToolInstance(CanvasToolFrame toolFrame)
    {
        ZIndexAllocator.deallocateZIndex(toolFrame.asWidget().getElement());
        ToolInstanceInfo info = this.toolInfoMap.remove(toolFrame.getTool());
        view.removeToolInstanceWidget(toolFrame);
        info.registrations.clear();
    }

    private void removeToolInstances(ArrayList<CanvasToolFrame> toolFrames)
    {
    	for (CanvasToolFrame toolFrame : toolFrames)
    	{
    		this.removeToolInstance(toolFrame);
    	}
    }


    private void setActiveToolInstance(CanvasToolFrame toolFrame)
	{
        CanvasTool<?> tool = toolFrame != null ? toolFrame.getTool() : null;
		if (tool == this.activeToolInstance) {
		    if (null != tool) {
    	        // Even if the current tool is already active, notify it, because
    	        // it may need to re-capture focus.
                toolFrame.setActive(true);
		    }
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
		    this.view.selectToolFrame(toolFrame);
	    }
	}


    private void setModeEdit()
    {
        if (false == this._inViewMode) {
            return;
        }
        this.view.setViewMode(false);
        this.viewModeEvent.dispatch(false);
        this.viewModeRegistrations.clear();

        this._inViewMode = false;
    }

    private void setModeView()
    {
        if (this._inViewMode) {
            return;
        }
        this._inViewMode = true;

        viewModeEvent.dispatch(true);
        view.setViewMode(true);
        viewModeRegistrations.add(view.addStopOperationHandler(new SimpleEvent.Handler<Void>() {
            @Override public void onFire(Void arg) {
                setModeEdit();
                updateHistoryToken();
            }
        }));
    }

    private void setRegistrations()
    {
        view.addToolCreationRequestHandler(new Handler<WorksheetView.ToolCreationRequest>() {
            @Override public void onFire(ToolCreationRequest arg) {
            	handleToolCreationRequest(arg);
            }
        });

        view.addSaveHandler(new Handler<Void>() {
            @Override public void onFire(Void arg) {
                save();
            }
        });
        view.addNewPageHandler(new Handler<Void>(){
            @Override public void onFire(Void arg) {
                newPage();
            }
        });
        
        view.addLogoutHandler(new Handler<Void>() {
            @Override public void onFire(Void arg) {
                logout();
            }
        });
        view.addInviteHandler(new Handler<Void>() {
            @Override public void onFire(Void arg) {
                invite();
            }
        });
        view.addAddSpaceHandler(new Handler<Void>() {
            @Override public void onFire(Void arg) {
            }
        });
        
        /*
        // TODO: Allow passing a pageKey here too
        view.addLoadHandler(new Handler<String>() {
            @Override public void onFire(String idStr) {
                updateLoadedPageURL(idStr);
            }
        });*/

        view.addCopyToolHandler(new Handler<ArrayList<CanvasToolFrame>>() {
            @Override public void onFire(ArrayList<CanvasToolFrame> arg) {
                copyToolsToClipboard(arg);
            }
        });
        view.addPasteToolHandler(new Handler<Void>() {
            @Override public void onFire(Void arg) {
                pasteToolsFromClipboard();
            }
        });
        view.addOptionsUpdatedHandler(new Handler<CanvasPageOptions>() {
            @Override public void onFire(CanvasPageOptions arg) {
                updateOptions(arg);
            }
        });
        view.addStopOperationHandler(new Handler<Void>() {
            @Override public void onFire(Void arg) {
                escapeOperation();
            }
        });
        view.addActiveToolFrameChangedHandler(new Handler<CanvasToolFrame>() {
			@Override public void onFire(CanvasToolFrame frame) {
		    	setActiveToolInstance(frame);
			}
		});
        view.addRemoveToolsRequest(new Handler<ArrayList<CanvasToolFrame>>() {
			@Override public void onFire(ArrayList<CanvasToolFrame> arg) {
				removeToolInstances(arg);
			}
		});
        view.addImageDropHandler(new Handler<WorksheetView.ImageDropInfo>() {
			@Override public void onFire(ImageDropInfo arg) {
				addImageFromDragAndDrop(arg);
		}});
    }

    protected void addImageFromDragAndDrop(ImageDropInfo imageDropInfo) {
    	ImageTool imageTool = ImageToolFactory.INSTANCE.create();
    	imageTool.getValue().imageInformation.setUrl(imageDropInfo.getDataUrl());
    	imageTool.getValue().transform.translation = imageDropInfo.getPosition();
    	this.setActiveToolInstance(this.createToolInstanceFromData(imageTool.getValue()));
	}

	private Collection<ElementData> sortByZIndex(Collection<ElementData> elements)
    {
        TreeMap<Integer, ElementData> elementsByZIndex = new TreeMap<Integer, ElementData>();
        for (ElementData element : elements) {
            elementsByZIndex.put(element.zIndex, element);
        }
        return elementsByZIndex.values();
    }

	private void updateHistoryToken()
    {
	    Long id = null;
	    String key = null;
	    if (null != this.page) {
	        id = this.page.id;
	        key = this.page.key;
	    }
        History.newItem(this.buildPageQueryString(id, key, this._inViewMode).toString(), false);
        this.updateViewForPageId(id, key);
    }
/*
    private void updateLoadedPageURL(String idStr)
    {
        Long id = parsePageIdStr(idStr);
        if (null == id) {
        	return;
        }
        if (false == Objects.equal(this.page.id, id)) {
            // Page id changed.
            // Change the URL hash and trigger a history load event.
            Window.Location.replace(this.buildPageUrl(id, this._inViewMode));
            return;
        }
        // Page id not changed, just reload
        this.load(idStr, ???);
    }
*/
    private void setDefaultPageOptions()
    {
        this.page.options.backgroundImage.options = ImageOptionsProviderUtils.getImageOptions(
                this._imageOptionsProvider, ImageOptionTypes.OriginalSize);
        view.setOptions(this.page.options);
    }

    private void updateOptions(CanvasPageOptions value)
    {
        if (null == value) {
            return;
        }
        this.page.options = value;
        view.setOptions(value);
    }

    private void updateToolData(CanvasToolFrame toolFrame){
        ElementData toolData = toolFrame.getTool().getValue();
        Element frameElement = toolFrame.asWidget().getElement();
        toolData.zIndex = ZIndexAllocator.getElementZIndex(frameElement);
        toolData.transform = new Transform2D(ElementUtils.getElementOffsetPosition(frameElement),
                toolFrame.getToolSize(), ElementUtils.getRotation(frameElement));
    }
/*
    private void updateUserSpecificInfo(WorksheetView view, AuthenticationServiceAsync service)
    {
        this.view.setUserProfile(null);
        final WorksheetImpl that = this;
        service.getUserProfile(new AsyncCallback<UserProfile>() {
            @Override public void onFailure(Throwable caught)
            {
                // TODO: handle
            }

            @Override public void onSuccess(UserProfile result)
            {
                that.view.setUserProfile(result);
            }
        });
    }*/

    private void handleToolCreationRequest(ToolCreationRequest toolCreationRequest)
    {
        CanvasToolFactory<? extends CanvasTool<? extends ElementData>> factory = toolCreationRequest.getFactory();
        if (null == factory) {
        	return;
        }
        CanvasToolFrame toolFrame = createToolInstance(toolCreationRequest.getPosition(), factory);
        setActiveToolInstance(toolFrame);
        if (toolCreationRequest.getFactory().isOneShot()) {
            _defaultToolboxItemRequestEvent.dispatch(null);
        }
        else
        {
            setActiveToolboxItem(activeToolboxItem);
        }
        toolCreationRequest.toolCreated(toolFrame.getTool());
    }

    private <S, E> void performSave(final Func<CanvasPage, S> successHandler, final Func<Throwable, E> errorHandler) 
    {
        // TODO: Defrag zIndex of all tools before saving.
        ArrayList<ElementData> activeElems = new ArrayList<ElementData>();
        for (Entry<CanvasTool<? extends ElementData>, ToolInstanceInfo> entry : toolInfoMap.entrySet()) {
            ToolInstanceInfo toolInfo = entry.getValue();
            this.updateToolData(toolInfo.toolFrame);
            activeElems.add(toolInfo.toolFrame.getTool().getValue());
        }
        this.page.elements.clear();
        this.page.elements.addAll(activeElems);
        
        if (false == this.pageIsEditable()) {
            // Make sure the service generates a new id and key for this page
            this.page.id = null;
            this.page.key = null;
        }

        CanvasServiceAsync service = (CanvasServiceAsync) GWT.create(CanvasService.class);

        view.onSaveOperationChange(OperationStatus.PENDING, null);

                
        service.savePage(page, new AsyncCallback<CanvasPage>() {
            @Override
            public void onFailure(Throwable caught)
            {
                view.onSaveOperationChange(OperationStatus.FAILURE,
                        ThrowableUtils.joinStackTrace(caught));
                errorHandler.apply(caught);
            }

            @Override
            public void onSuccess(CanvasPage result)
            {
                // TODO: see issue #92
                load(result);
                view.onSaveOperationChange(OperationStatus.SUCCESS, null);
                Window.Location.replace(buildPageUrl(result.id, result.key, false));
                successHandler.apply(result);
            }
        });
    }
}

