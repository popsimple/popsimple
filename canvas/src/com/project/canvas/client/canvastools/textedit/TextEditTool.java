package com.project.canvas.client.canvastools.textedit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RichTextArea;
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.canvas.client.gwtsamples.text.RichTextToolbar;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.ElementUtils;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.Point2D;
import com.project.canvas.shared.data.TextData;

public class TextEditTool extends FlowPanel implements CanvasTool<TextData> 
{

    
    private final SimpleEvent<String> killRequestEvent = new SimpleEvent<String>();
    private final SimpleEvent<Point2D> moveRequestEvent = new SimpleEvent<Point2D>();
    
    private RichTextArea rtfArea = GWT.create(RichTextArea.class);
    private RichTextToolbar toolbar = new RichTextToolbar(rtfArea);
    private TextData data;
    private boolean editorReady = false;
	private boolean isActive = false;
	private Point2D editSize;
    private boolean needsBinding;

    public TextEditTool() {
        CanvasToolCommon.initCanvasToolWidget(this);
        this.data = new TextData();
        this.addStyleName(CanvasResources.INSTANCE.main().textEdit());
        this.add(toolbar);
        this.add(rtfArea);
        //this.rtfArea.addStyleName(CanvasResources.INSTANCE.main().textEditBox());
        this.rtfArea.addInitializeHandler(new InitializeHandler() {
            @Override
            public void onInitialize(InitializeEvent event)
            {
                editorReady = true;
                if (needsBinding) {
                    internalBind();
                }
            }
        });
    }

    @Override
	public HandlerRegistration addMoveEventHandler(Handler<Point2D> handler) {
		return this.moveRequestEvent.addHandler(handler);
	}

    @Override
    public HandlerRegistration addMoveStartEventHandler(Handler<MouseEvent<?>> handler) {
        return null;
    }

    @Override
    public void bind() {
        needsBinding = true;
        if (editorReady) {
            internalBind();
        }
    }

    private void internalBind()
    {
        registerHandlers();
        if (null != data) {
            rtfArea.setHTML(data.text);
        }
        setActive(isActive);
    }

    public SimpleEvent<String> getKillRequestedEvent() {
        return this.killRequestEvent;
    }

	public int getTabIndex() {
        return this.rtfArea.getTabIndex();
    }

    @Override
    public TextData getValue() {
        this.data.text = this.editorReady ? this.rtfArea.getHTML() : "";
        return this.data;
    }

	@Override
    public boolean hasResizeableHeight()
    {
        return false;
    }

    @Override
    public boolean hasResizeableWidth()
    {
        return true;
    }

    public void setAccessKey(char key) {
        this.rtfArea.setAccessKey(key);
    }

    @Override
    public void setActive(boolean isActive) {
        rtfArea.setFocus(isActive);

    	setLooksActive(isActive, true);
    }

    @Override
    public void setElementData(ElementData data) {
        this.setValue((TextData) data);
    }

    public void setTabIndex(int index) {
        this.rtfArea.setTabIndex(index);
    }

    @Override
    public void setValue(TextData data) {
        this.data = data;
        if (editorReady) {
            this.rtfArea.setHTML(this.data.text);
        }
    }

    private void registerHandlers() {
//        this.rtfArea.addKeyDownHandler(new KeyDownHandler() {
//            public void onKeyDown(KeyDownEvent event) {
//                // TODO: depends on a hack in NicEditor.dispatchKeyDown
//            	boolean isEscape = (event.getNativeKeyCode() == 27);
//                setActive(false == isEscape);
//            }
//        });
    }

    private void setLooksActive(boolean isActive, boolean k1illIfEmpty) {
    	// this.isActive is used for remembering what state to get into when ready event occurs.
    	// and also for determining whether we need to do anything at all (specifically to prevent multiple
    	// moveRequestEvent dispatching)
        if (false == editorReady) {
    		this.isActive = isActive; 
            return;
        }
        else if (isActive == this.isActive) {
    		return;
        }
		this.isActive = isActive; 
        if (isActive) {
        	if (null != this.editSize) {
        		// Set only the width - the height depends on the contents
            	this.setWidth(this.editSize.getX() + "px");
            	this.editSize = null;
        	}

        	this.addStyleName(CanvasResources.INSTANCE.main().textEditFocused());
        	this.removeStyleName(CanvasResources.INSTANCE.main().textEditNotFocused());
        } 
        else {
        	this.editSize = ElementUtils.getElementSize(this.getElement());
        	
        	// Must be done AFTER saving size and move offset
        	this.removeStyleName(CanvasResources.INSTANCE.main().textEditFocused());
        	this.addStyleName(CanvasResources.INSTANCE.main().textEditNotFocused());
        	
            String text = new HTML(this.rtfArea.getHTML()).getText().replace((char) 160, ' ');
            if (k1illIfEmpty) {
	            if (text.trim().isEmpty()) {
	                this.killRequestEvent.dispatch("Empty");
	            }
            }
        }
	}
}
