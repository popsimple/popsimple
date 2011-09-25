package com.project.website.canvas.client.canvastools.textedit;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.utils.CssProperties;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.StyleUtils;
import com.project.shared.client.utils.widgets.WidgetUtils;
import com.project.shared.data.Point2D;
import com.project.shared.data.funcs.Func;
import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.canvastools.base.CanvasToolCommon;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.shared.data.ElementData;
import com.project.website.canvas.shared.data.TextData;

public class TextEditTool extends FocusPanel implements CanvasTool<TextData>
{
    // private final FlowPanel editorPanel = new FlowPanel();
    private final SimpleEvent<String> _killRequestEvent = new SimpleEvent<String>();
    private final SimpleEvent<Point2D> _moveRequestEvent = new SimpleEvent<Point2D>();

    private final Widget _editedWidget;

    private final TextEditToolbarImpl _toolbar;

    private TextData _data = null;
    private boolean _editorReady = false;
    private boolean _isActive = false;
    private boolean _initialized = false;

    public TextEditTool()
    {
        CanvasToolCommon.initCanvasToolWidget(this);

        //this.add(this._editPanel);
        this._editedWidget = this;
        this._toolbar = new TextEditToolbarImpl();

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
    }

    @Override
    public TextData getValue()
    {
        this._data.innerHtml = this._editorReady ? this.getContents() : "";
        this._data.cssText = StyleUtils.getCssText(this._editedWidget.getElement().getStyle());
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
        if (false == _editorReady) {
            return;
        }
        this.setContents(this._data.innerHtml);
        Style style = this._editedWidget.getElement().getStyle();

        // Never override the element's width/height with cssText data.

        String width = style.getWidth();
        String height = style.getHeight();

        StyleUtils.setCssText(style, this._data.cssText);

        style.setProperty(CssProperties.WIDTH, width);
        style.setProperty(CssProperties.HEIGHT, height);
    }

    /**
     * Returns the HTML that represents the content that the user sees. This
     * includes both the innerHtml of the element, and also the styles applied
     * on the element.
     */
    private String getContents()
    {
        return this._editedWidget.getElement().getInnerHTML();
    }

    private void setContents(String text)
    {
        // TODO: sanitize
        this._editedWidget.getElement().setInnerHTML(text);
    }

    private void registerHandlers()
    {
        this.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event)
            {
                handleKeyDown(event);
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
        } else if (this._initialized && (isActive == this._isActive)) {
            return;
        }
        this._isActive = isActive;
        this._initialized = true;

        if (isActive) {
            // if (null != this.editSize) {
            // // Set only the width - the height depends on the contents
            // this.setWidth(this.editSize.getX() + "px");
            // this.editSize = null;
            // }

            this.addStyleName(CanvasResources.INSTANCE.main().textEditFocused());
            this.removeStyleName(CanvasResources.INSTANCE.main().textEditNotFocused());

            // This causes infinite recursion / looping:
            //this._editPanel.setFocus(true);
            this._toolbar.setEditedWidget(this._editedWidget);

        } else {
            this._toolbar.setEditedWidget(null);

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
        ElementUtils.setContentEditable(this._editedWidget.getElement(), isViewMode);
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
            this.setContents(this._data.innerHtml);
        }
        this.setActive(this._isActive);
    }

    @Override
    public IsWidget getToolbar()
    {
        return this._toolbar;
    }


    private void handleKeyDown(KeyDownEvent event) {
        boolean isEscape = (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE);
        if (isEscape) {
            setActive(false);
        }
    }

}
