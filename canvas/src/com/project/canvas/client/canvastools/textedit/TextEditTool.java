package com.project.canvas.client.canvastools.textedit;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
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
import com.project.canvas.client.shared.WidgetUtils;
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
    public void bind() {
        if (null == nicEditor) {
            nicEditor = new NicEditor(editBox, editorReady);
        }
    }

    private void registerHandlers() {
//        this.editBox.addKeyPressHandler(new KeyPressHandler() {
//            public void onKeyPress(KeyPressEvent event) {
//                updateEditBoxVisibleLength();
//            }
//        });

        //        this.editBox.addValueChangeHandler(new ValueChangeHandler<String>() {
//            public void onValueChange(ValueChangeEvent<String> event) {
//                updateEditBoxVisibleLength();
//            }
//        });
//        this.editBox.addKeyUpHandler(new KeyUpHandler() {
//            public void onKeyUp(KeyUpEvent event) {
//                updateEditBoxVisibleLength();
//            }
//        });
//        this.editBox.addKeyDownHandler(new KeyDownHandler() {
//            public void onKeyDown(KeyDownEvent event) {
//                updateEditBoxVisibleLength();
//            }
//        });
    }

    protected void updateEditBoxVisibleLength() {
        Point2D newSize = TextEditUtils.autoSizeWidget(this, this.getElement().getInnerHTML(), true);
        newSize = newSize.plus(new Point2D(10,20));
    	WidgetUtils.setWidgetSize(this, newSize);
    }

    @Override
    public void setActive(boolean isActive) {
    	setLooksActive(isActive, true);
    }

	private void setLooksActive(boolean isActive, boolean k1illIfEmpty) {
		this.isActive = isActive;
        if (false == nicEditorReady) {
            return;
        }
        if (isActive) {
        	if (null != this.editSize) {
        		// Set only the width - the height depends on the contents
            	this.setWidth(this.editSize.getX() + "px");
            	this.editSize = null;
        	}

        	this.addStyleName(CanvasResources.INSTANCE.main().textEditFocused());
        	this.removeStyleName(CanvasResources.INSTANCE.main().textEditNotFocused());

        	this.moveRequestEvent.dispatch(getEditorOffsetPos().mul(-1));
        } 
        else {
        	this.moveRequestEvent.dispatch(getEditorOffsetPos());
        	
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

    private Point2D getEditorOffsetPos() {
		Point2D editorPos = ElementUtils.getElementAbsolutePosition(this.nicEditor.getEditorElement());
		Point2D myPos = ElementUtils.getElementAbsolutePosition(this.getElement());
		return editorPos.minus(myPos);
	}

	public SimpleEvent<String> getKillRequestedEvent() {
        return this.killRequestEvent;
    }

    public int getTabIndex() {
        return this.editBox.getTabIndex();
    }

    public void setAccessKey(char key) {
        this.editBox.setAccessKey(key);
    }

    public void setTabIndex(int index) {
        this.editBox.setTabIndex(index);
    }

    @Override
    public TextData getValue() {
        this.data.text = this.nicEditorReady ? this.nicEditor.getContent() : "";
        return this.data;
    }

    @Override
    public void setValue(TextData data) {
        this.data = data;
        if (nicEditorReady) {
            this.nicEditor.setContent(this.data.text);
        }
    }

    @Override
    public void setElementData(ElementData data) {
        this.setValue((TextData) data);
    }

    @Override
    public HandlerRegistration addMoveStartEventHandler(Handler<MouseEvent<?>> handler) {
        return null;
    }

    @Override
    public boolean hasResizeableWidth()
    {
        return true;
    }

    @Override
    public boolean hasResizeableHeight()
    {
        return false;
    }

    @Override
	public HandlerRegistration addMoveEventHandler(Handler<Point2D> handler) {
		return this.moveRequestEvent.addHandler(handler);
	}
}
