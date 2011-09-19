package com.project.website.canvas.client.canvastools.textedit;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.html5.impl.RangeUtils;
import com.project.shared.client.html5.impl.SelectionImpl;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.WidgetUtils;
import com.project.shared.data.Point2D;
import com.project.shared.data.funcs.Func;
import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.canvas.shared.data.TextData;

public class TextEditTool extends FocusPanel implements CanvasTool<TextData>
{
    private static final String CONTENTEDITABLE = "contenteditable";
    // private final FlowPanel editorPanel = new FlowPanel();
    private final SimpleEvent<String> _killRequestEvent = new SimpleEvent<String>();
    private final SimpleEvent<Point2D> _moveRequestEvent = new SimpleEvent<Point2D>();

    private final RegistrationsManager _registrationsManager = new RegistrationsManager();

    private static TextEditToolbar _toolbar = null;

    private TextData _data;
    private boolean _editorReady = false;
    private boolean _isActive = false;
    private boolean _activeStateSet = false;

    public TextEditTool()
    {
        CanvasToolCommon.initCanvasToolWidget(this);

        if (null == TextEditTool._toolbar) {
            TextEditTool._toolbar = new TextEditToolbar();
            RootPanel.get().add(TextEditTool._toolbar);
        }

        this.addStyleName(CanvasResources.INSTANCE.main().textEdit());
        this.setViewMode(false);
        final TextEditTool that = this;
        ElementUtils.generateId("textedit", this.getElement());
        WidgetUtils.getOnAttachAsyncFunc(this).then(new Func.VoidAction() {
            @Override
            public void exec()
            {
                that.initEditable();
            }
        }).run(null);
        this.addStyleName(CanvasResources.INSTANCE.main().textEditBox());
    }

    @Override
    public HandlerRegistration addSelfMoveRequestEventHandler(Handler<Point2D> handler)
    {
        return this._moveRequestEvent.addHandler(handler);
    }

    @Override
    public HandlerRegistration addMoveStartEventHandler(Handler<MouseEvent<?>> handler)
    {
        return null;
    }

    @Override
    public void bind()
    {
        _registrationsManager.add(this.addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event)
            {
                setLooksActive(true, false);
            }
        }));
        _registrationsManager.add(this.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event)
            {
                setLooksActive(false, false);
            }
        }));
    }

    @Override
    public TextData getValue()
    {
        this._data.text = this._editorReady ? this.getContents() : "";
        return this._data;
    }

    @Override
    public ResizeMode getResizeMode()
    {
        return ResizeMode.WIDTH_ONLY;
    }

    @Override
    public void setActive(boolean isActive)
    {
        setLooksActive(isActive, true);
    }

    @Override
    public void setElementData(ElementData data)
    {
        this.setValue((TextData) data);
    }

    @Override
    public void setValue(TextData data)
    {
        this._data = data;
        if (_editorReady) {
            this.setContents(this._data.text);
        }
    }

    private String getContents()
    {
        // TODO: sanitize
        return this.getElement().getInnerHTML();
    }

    private void setContents(String text)
    {
        // TODO: sanitize
        this.getElement().setInnerHTML(text);
    }

    private void registerHandlers()
    {
        this.addKeyDownHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent event)
            {
                boolean isEscape = (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE);
                if (isEscape) {
                    setActive(false);
                }
                // TODO replace this with key bindings manager?
                else if (event.getNativeKeyCode() == KeyCodes.KEY_PAGEDOWN) {
                        SelectionImpl selection = SelectionImpl.getWindowSelection();
                        if (0 < selection.getRangeCount()) {
                            RangeUtils.applyToNodesInRange(selection.getRangeAt(0), new Func.Action<Element>(){
                                @Override
                                public void exec(Element arg)
                                {
                                    arg.getStyle().setFontWeight(FontWeight.BOLD);
                                }});
                        }
                }

            }
        });
    }

    private void setLooksActive(boolean isActive, boolean killIfEmpty)
    {
        // Logger.log("TextEditTool - Setting active: " + isActive + ", text = "
        // + this.getElement().getInnerText());
        // this.isActive is used for remembering what state to get into when
        // ready event occurs.
        // and also for determining whether we need to do anything at all
        // (specifically to prevent multiple moveRequestEvent dispatching)
        if (false == this._editorReady) {
            this._isActive = isActive;
            return;
        } else if (this._activeStateSet && (isActive == this._isActive)) {
            return;
        }
        this._isActive = isActive;
        this._activeStateSet = true;
        if (isActive) {
            // if (null != this.editSize) {
            // // Set only the width - the height depends on the contents
            // this.setWidth(this.editSize.getX() + "px");
            // this.editSize = null;
            // }

            this.addStyleName(CanvasResources.INSTANCE.main().textEditFocused());
            this.removeStyleName(CanvasResources.INSTANCE.main().textEditNotFocused());

            TextEditTool._toolbar.setEditedElement(this.getElement());
            
            // This causes infinite recursion / looping:
            //this.setFocus(true);

        } else {

            this.setFocus(false);

            // this.editSize =
            // ElementUtils.getElementOffsetSize(this.getElement());

            // Must be done AFTER saving size and move offset
            this.removeStyleName(CanvasResources.INSTANCE.main().textEditFocused());
            this.addStyleName(CanvasResources.INSTANCE.main().textEditNotFocused());

            String text = this.getContents().replace((char) 160, ' ');
            if (killIfEmpty) {
                if (text.trim().isEmpty()) {
                    this._killRequestEvent.dispatch("Empty");
                }
            }
        }
    }

    @Override
    public boolean canRotate()
    {
        return true;
    }

    @Override
    public void setViewMode(boolean isViewMode)
    {
        if (isViewMode) {
            this.getElement().removeAttribute(CONTENTEDITABLE);
        } else {
            this.getElement().setAttribute(CONTENTEDITABLE, "true");
        }
    }

    @Override
    public HandlerRegistration addKillRequestEventHandler(Handler<String> handler)
    {
        return this._killRequestEvent.addHandler(handler);
    }

    @Override
    public void onResize()
    {
        // TODO Auto-generated method stub
    }

    private void initEditable()
    {
        this._editorReady = true;
        this.registerHandlers();
        if (null != this._data) {
            this.setContents(this._data.text);
        }
        this.setActive(this._isActive);
    }
}
