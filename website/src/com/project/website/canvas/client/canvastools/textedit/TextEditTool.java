package com.project.website.canvas.client.canvastools.textedit;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.WidgetUtils;
import com.project.shared.data.Point2D;
import com.project.shared.data.funcs.Func;
import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.website.canvas.client.canvastools.textedit.aloha.Aloha;
import com.project.website.canvas.client.canvastools.textedit.aloha.Editable;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.canvas.shared.data.TextData;

public class TextEditTool extends FocusPanel implements CanvasTool<TextData>
{
    //private final FlowPanel editorPanel = new FlowPanel();
    private final SimpleEvent<String> killRequestEvent = new SimpleEvent<String>();
    private final SimpleEvent<Point2D> moveRequestEvent = new SimpleEvent<Point2D>();

    private TextData data;
    private boolean editorReady = false;
    private boolean _isActive = false;
    private boolean activeStateSet = false;
    private Point2D editSize;

    protected Editable alohaEditable;

    public TextEditTool() {
        CanvasToolCommon.initCanvasToolWidget(this);
        this.addStyleName(CanvasResources.INSTANCE.main().textEdit());
        final TextEditTool that = this;
        ElementUtils.generateId("textedit", this.getElement());
        WidgetUtils.getOnAttachAsyncFunc(this).then(new Func.VoidAction() {
                    @Override
                    public void exec()
                    {
                        that.initEditable();
                    }
                })
            .run(null);
        this.addStyleName(CanvasResources.INSTANCE.main().textEditBox());
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

    }

    @Override
    public TextData getValue() {
        this.data.text = this.editorReady ? this.alohaEditable.getContents() : "";
        return this.data;
    }

	@Override
	public ResizeMode getResizeMode() {
		return ResizeMode.WIDTH_ONLY;
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
        if (editorReady) {
            this.alohaEditable.setContents(this.data.text);
        }
    }

    private void registerHandlers() {
//        this.nicEditor.addKeyDownHandler(new KeyDownHandler() {
//            public void onKeyDown(KeyDownEvent event) {
//                // TODO: depends on a hack in NicEditor.dispatchKeyDown
//                boolean isEscape = (event.getNativeKeyCode() == 27);
//                setActive(false == isEscape);
//            }
//        });
    }

    private void setLooksActive(boolean isActive, boolean killIfEmpty) {
        // this.isActive is used for remembering what state to get into when
        // ready event occurs.
        // and also for determining whether we need to do anything at all
        // (specifically to prevent multiple
        // moveRequestEvent dispatching)
        if (false == editorReady) {
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
            this.alohaEditable.activate();
        }
        else {
            this.editSize = ElementUtils.getElementOffsetSize(this.getElement());

            // Must be done AFTER saving size and move offset
            this.removeStyleName(CanvasResources.INSTANCE.main().textEditFocused());
            this.addStyleName(CanvasResources.INSTANCE.main().textEditNotFocused());

            String text = new HTML(this.alohaEditable.getContents()).getText().replace((char) 160, ' ');
            if (killIfEmpty) {
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
        // TODO: Deactivate or hide the floating menu
        if (isViewMode) {
            this.alohaEditable.disable();
        }
        else {
            this.alohaEditable.enable();
        }
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

    private void initEditable()
    {
        this.alohaEditable = Aloha.registerElementById(this.getElement());
        this.alohaEditable.enable();
        this.alohaEditable.activate();
        this.editorReady = true;
        this.registerHandlers();
        if (null != this.data) {
            this.alohaEditable.setContents(this.data.text);
        }
        this.setActive(this._isActive);
    }
}
