package com.project.canvas.client.canvastools.textedit;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
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
        public void onSuccess(Void result) {
            nicEditorReady = true;
            registerHandlers();
            if (null != data) {
                nicEditor.setContent(data.text);
            }
            setActive(_isActive);
        }

        @Override
        public void onFailure(Throwable caught) {
            // TODO Auto-generated method stub

        }
    };

    private final TextArea editTextArea = new TextArea();
    private final HTML viewBox = new HTML();
    private final FlowPanel editorPanel = new FlowPanel();
    private final SimpleEvent<String> killRequestEvent = new SimpleEvent<String>();
    private final SimpleEvent<Point2D> moveRequestEvent = new SimpleEvent<Point2D>();

    private NicEditor nicEditor;
    private TextData data;
    private boolean nicEditorReady = false;
    private boolean _isActive = false;
    private boolean activeStateSet = false;
    private Point2D editSize;

    public TextEditTool() {
        CanvasToolCommon.initCanvasToolWidget(this);
        this.addStyleName(CanvasResources.INSTANCE.main().textEdit());
        this.add(viewBox);
        this.add(editorPanel);
        this.editorPanel.add(editTextArea);
        this.editTextArea.addStyleName(CanvasResources.INSTANCE.main().textEditBox());
        this.viewBox.addStyleName(CanvasResources.INSTANCE.main().textEditViewBox());
    }

    @Override
    public HandlerRegistration addSelfMoveRequestEventHandler(Handler<Point2D> handler) {
        return this.moveRequestEvent.addHandler(handler);
    }

    @Override
    public HandlerRegistration addMoveStartEventHandler(Handler<MouseEvent<?>> handler) {
        return null;
    }

    @Override
    public void bind() {
        if (null == nicEditor) {
            nicEditor = new NicEditor(editTextArea, editorReady);
        }
    }

    @Override
    public TextData getValue() {
        this.data.text = this.nicEditorReady ? this.nicEditor.getContent() : "";
        return this.data;
    }

	@Override
	public ResizeMode getResizeMode() {
		return ResizeMode.WIDTH_ONLY;
	}


    public void setAccessKey(char key) {
        this.editTextArea.setAccessKey(key);
    }

    @Override
    public void setActive(boolean isActive) {
        setLooksActive(isActive, true);
    }

    @Override
    public void setElementData(ElementData data) {
        this.setValue((TextData) data);
    }

    @Override
    public void setValue(TextData data) {
        this.data = data;
        if (nicEditorReady) {
            this.nicEditor.setContent(this.data.text);
        }
    }

    private void registerHandlers() {
        this.nicEditor.addKeyDownHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent event) {
                // TODO: depends on a hack in NicEditor.dispatchKeyDown
                boolean isEscape = (event.getNativeKeyCode() == 27);
                setActive(false == isEscape);
            }
        });
    }

    private void setLooksActive(boolean isActive, boolean k1illIfEmpty) {
        // this.isActive is used for remembering what state to get into when
        // ready event occurs.
        // and also for determining whether we need to do anything at all
        // (specifically to prevent multiple
        // moveRequestEvent dispatching)
        if (false == nicEditorReady) {
            this._isActive = isActive;
            return;
        }
        else if (activeStateSet && (isActive == this._isActive)) {
            return;
        }
        this._isActive = isActive;
        this.activeStateSet = true;
        if (isActive) {
            if (null != this.editSize) {
                // Set only the width - the height depends on the contents
                this.setWidth(this.editSize.getX() + "px");
                this.editSize = null;
            }

            this.addStyleName(CanvasResources.INSTANCE.main().textEditFocused());
            this.removeStyleName(CanvasResources.INSTANCE.main().textEditNotFocused());
            nicEditor.getEditorElement().focus();
        }
        else {
            this.editSize = ElementUtils.getElementOffsetSize(this.getElement());

            // Must be done AFTER saving size and move offset
            this.removeStyleName(CanvasResources.INSTANCE.main().textEditFocused());
            this.addStyleName(CanvasResources.INSTANCE.main().textEditNotFocused());

            String text = new HTML(this.nicEditor.getContent()).getText().replace((char) 160, ' ');
            if (k1illIfEmpty) {
                if (text.trim().isEmpty()) {
                    this.killRequestEvent.dispatch("Empty");
                }
            }
        }
    }

    @Override
    public boolean canRotate() {
        return true;
    }

    @Override
    public void setViewMode(boolean isViewMode)
    {
        if (isViewMode) {
            this.viewBox.setHTML(this.nicEditor.getContent());
            // Completely detach the editor from us (and the document)
            this.remove(this.editorPanel);
        }
        else {
            if (false == this.getChildren().contains(this.editorPanel)) {
                this.add(this.editorPanel);
            }
        }
        this.viewBox.setVisible(isViewMode);
        this.editorPanel.setVisible(false == isViewMode);
    }

    @Override
    public HandlerRegistration addKillRequestEventHandler(Handler<String> handler)
    {
        return this.killRequestEvent.addHandler(handler);
    }

    @Override
    public void onResize() {
        // TODO Auto-generated method stub
    }
}
