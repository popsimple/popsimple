package com.project.canvas.client.canvastools.TextEdit;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.client.shared.events.SimpleEvent.Handler;
import com.project.canvas.client.shared.nicedit.NicEditor;
import com.project.canvas.shared.data.ElementData;
import com.project.canvas.shared.data.Point2D;
import com.project.canvas.shared.data.TextData;

public class TextEditTool extends FlowPanel implements CanvasTool<TextData> {

    private final TextArea editBox = new TextArea();
    private final SimpleEvent<String> killRequestEvent = new SimpleEvent<String>();
    private NicEditor nicEditor;
    private TextData data;
    private boolean nicEditorReady = false;
    
    protected AsyncCallback<Void> editorReady = new AsyncCallback<Void>() {
        @Override
        public void onSuccess(Void result)
        {
            nicEditorReady = true;
            registerHandlers();
            nicEditor.setContent(data.text);
        }
        
        @Override
        public void onFailure(Throwable caught)
        {
            // TODO Auto-generated method stub
            
        }
    };

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
        this.editBox.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                event.stopPropagation();
            }
        });
        this.editBox.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                updateEditBoxVisibleLength();
            }
        });
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
        // this.editBox.setVisibleLength(Math.max(MINIMUM_EDITBOX_VISIBLE_LENGTH,
        // spareLength));
        Point2D newSize = TextEditUtils.autoSizeWidget(this, this.getElement().getInnerHTML(), true);
        newSize = newSize.plus(new Point2D(10,20));
        this.setWidth(newSize.getX() + "px");
        this.setHeight(newSize.getY() + "px");
    }

    @Override
    public void setActive(boolean isActive) {
        if (false == nicEditorReady) {
            return;
        }
        if (isActive) {
                updateEditBoxVisibleLength();
        } else {
            String text = this.nicEditor.getContent();
            if (text.trim().isEmpty()) {
                this.killRequestEvent.dispatch("Empty");
            }
        }
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
            this.updateEditBoxVisibleLength();
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
}
