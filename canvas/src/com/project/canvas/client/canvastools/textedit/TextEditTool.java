package com.project.canvas.client.canvastools.textedit;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.project.canvas.client.canvastools.base.CanvasTool;
import com.project.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.canvas.client.resources.CanvasResources;
import com.project.canvas.client.shared.ElementUtils;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.client.shared.nicedit.NicEditor;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.Point2D;
import com.project.canvas.shared.data.TextData;

public class TextEditTool extends FlowPanel implements CanvasTool<TextData> 
{

    protected AsyncCallback<Void> editorReady = new AsyncCallback<Void>() {
        @Override
        public void onSuccess(Void result)
        {
            nicEditorReady = true;
            registerHandlers();
            if (null != data) {
                nicEditor.setContent(data.text);
            }
        	setActive(isActive);
        }
        
        @Override
        public void onFailure(Throwable caught)
        {
            // TODO Auto-generated method stub
            
        }
    };
    
    private final TextArea editBox = new TextArea();
    private final SimpleEvent<String> killRequestEvent = new SimpleEvent<String>();
    private final SimpleEvent<Point2D> moveRequestEvent = new SimpleEvent<Point2D>();
    private final SimpleEvent<Boolean> rotationStateEvent = new SimpleEvent<Boolean>();
    
    private NicEditor nicEditor;
    private TextData data;
    private boolean nicEditorReady = false;
	private boolean isActive = false;
	private Point2D editSize;

    public TextEditTool() {
        CanvasToolCommon.initCanvasToolWidget(this);
        this.data = new TextData();
        this.addStyleName(CanvasResources.INSTANCE.main().textEdit());
        this.add(editBox);
        this.editBox.addStyleName(CanvasResources.INSTANCE.main().textEditBox());
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
        if (null == nicEditor) {
            nicEditor = new NicEditor(editBox, editorReady);
        }
    }

    public SimpleEvent<String> getKillRequestedEvent() {
        return this.killRequestEvent;
    }

	public int getTabIndex() {
        return this.editBox.getTabIndex();
    }

    @Override
    public TextData getValue() {
        this.data.text = this.nicEditorReady ? this.nicEditor.getContent() : "";
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
        this.editBox.setAccessKey(key);
    }

    @Override
    public void setActive(boolean isActive) {
    	setLooksActive(isActive, true);
    }

    @Override
    public void setElementData(ElementData data) {
        this.setValue((TextData) data);
    }

    public void setTabIndex(int index) {
        this.editBox.setTabIndex(index);
    }

    @Override
    public void setValue(TextData data) {
        this.data = data;
        if (nicEditorReady) {
            this.nicEditor.setContent(this.data.text);
        }
    }

    private Point2D getEditorOffsetPos() {
		Point2D editorPos = ElementUtils.getElementAbsolutePosition(this.nicEditor.getEditorElement());
		Point2D myPos = ElementUtils.getElementAbsolutePosition(this.getElement());
		return editorPos.minus(myPos);
	}

    private void registerHandlers() {
        this.editBox.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
            	boolean isEscape = (event.getNativeEvent().getKeyCode() == 27);
                setActive(false == isEscape);
            }
        });
    }

    private void setLooksActive(boolean isActive, boolean k1illIfEmpty) {
    	// this.isActive is used for remembering what state to get into when ready event occurs.
    	// and also for determining whether we need to do anything at all (specifically to prevent multiple
    	// moveRequestEvent dispatching)
        if (false == nicEditorReady) {
    		this.isActive = isActive; 
            return;
        }
        else if (isActive == this.isActive) {
    		return;
        }
		this.isActive = isActive; 
    	// check if we had the NotFocused style, to know if we need to fire the moveRequestEvent
    	boolean hadNotFocusedStyle = this.getStyleName().contains(CanvasResources.INSTANCE.main().textEditNotFocused());
    	
        if (isActive) {
        	if (null != this.editSize) {
        		// Set only the width - the height depends on the contents
            	this.setWidth(this.editSize.getX() + "px");
            	this.editSize = null;
        	}

        	this.addStyleName(CanvasResources.INSTANCE.main().textEditFocused());
        	this.removeStyleName(CanvasResources.INSTANCE.main().textEditNotFocused());

        	if (hadNotFocusedStyle) {
        		this.moveRequestEvent.dispatch(getEditorOffsetPos().mul(-1));
        	}
        } 
        else {
        	if (false == hadNotFocusedStyle) {
        		this.moveRequestEvent.dispatch(getEditorOffsetPos());
        	}
        	
        	this.editSize = ElementUtils.getElementSize(this.getElement());
        	
        	// Must be done AFTER saving size and move offset
        	this.removeStyleName(CanvasResources.INSTANCE.main().textEditFocused());
        	this.addStyleName(CanvasResources.INSTANCE.main().textEditNotFocused());
        	
            String text = this.nicEditor.getContent();
            if (k1illIfEmpty) {
	            if (text.trim().isEmpty()) {
	                this.killRequestEvent.dispatch("Empty");
	            }
            }
        }
	}
}
