package com.project.shared.client.utils.widgets;

import com.google.common.base.Strings;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.events.SimpleEvent.Handler;
import com.project.shared.client.handlers.RegistrationsManager;
import com.project.shared.client.utils.ElementUtils;
import com.project.shared.client.utils.HandlerUtils;
import com.project.shared.data.Point2D;
import com.project.shared.data.funcs.AsyncFunc;
import com.project.shared.data.funcs.Func;

public class WidgetUtils {

    public static void stopClickPropagation(Widget widget) {
        widget.addDomHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                arg0.stopPropagation();
            }
        }, ClickEvent.getType());
        WidgetUtils.addMovementStartHandler(widget, new Handler<HumanInputEvent<?>>() {
            @Override
            public void onFire(HumanInputEvent<?> arg)
            {
                arg.stopPropagation();
            }});
    }

    public static <T extends Widget & Focusable> void addEscapeUnfocusesHandler(final T widget) {
        widget.addDomHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == 27) {
                    widget.setFocus(false);
                }
            }
        }, KeyDownEvent.getType());
    }

    public static HandlerRegistration stopMouseMovePropagation(Widget widget) {
        return widget.addDomHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                event.stopPropagation();
            }
        }, MouseMoveEvent.getType());
    }

    public static void mouseDownPreventDefault(Widget widget) {
        WidgetUtils.addMovementStartHandler(widget, new Handler<HumanInputEvent<?>>() {
            @Override
            public void onFire(HumanInputEvent<?> arg)
            {
                arg.preventDefault();
            }});
    }

	public static void setWidgetSize(Widget widget, Point2D editSize) {
		widget.setWidth(editSize.getX() + "px");
		widget.setHeight(editSize.getY() + "px");
	}

	public static void setBackgroundImageAsync(final Widget widget,
            String imageUrl, String errorImageUrl, final boolean autoSize, final String loadingStyleName)
    {
	    WidgetUtils.setBackgroundImageAsync(widget, imageUrl, errorImageUrl, autoSize, loadingStyleName,
	            HandlerUtils.<Void>emptyHandler(), HandlerUtils.<Void>emptyHandler());
    }

	public static void setBackgroundImageAsync(final Widget widget, String imageUrl, String errorImageUrl,
            final boolean autoSize, final SimpleEvent.Handler<Void> loadHandler,
            final SimpleEvent.Handler<Void> errorHandler)
    {
	    WidgetUtils.setBackgroundImageAsync(widget, imageUrl, errorImageUrl, autoSize, "",
                loadHandler, HandlerUtils.<Void>emptyHandler());
    }

	public static void setBackgroundImageAsync(final Widget widget, String imageUrl, String errorImageUrl,
	        final boolean autoSize, final String loadingStyleName,
	        final SimpleEvent.Handler<Void> loadHandler, final SimpleEvent.Handler<Void> errorHandler)
    {
	    widget.getElement().getStyle().clearBackgroundImage();
	    if (Strings.isNullOrEmpty(imageUrl)) {
            WidgetUtils.removeNonEmptyStyleName(widget, loadingStyleName);
	        loadHandler.onFire(null);
	        return;
	    }
	    WidgetUtils.addNonEmptyStyleName(widget, loadingStyleName);

        ElementUtils.setBackgroundImageAsync(widget.getElement(), imageUrl, errorImageUrl, autoSize,
            new SimpleEvent.Handler<Void>() {
                @Override
                public void onFire(Void arg) {
                    WidgetUtils.removeNonEmptyStyleName(widget, loadingStyleName);
                    loadHandler.onFire(null);
                }},
            new SimpleEvent.Handler<Void>() {
                @Override
                public void onFire(Void arg) {
                    WidgetUtils.removeNonEmptyStyleName(widget, loadingStyleName);
                    errorHandler.onFire(null);
                }});
    }

	/**
	 * Returns an AsyncFunc that waits until the widget is attached to the DOM, and the completes (runs once).
	 * If the widget is already attached, it will complete immediately.
	 * @param widget
	 * @return
	 */
    public static AsyncFunc<Void, Void>  getOnAttachAsyncFunc(final Widget widget)
    {
    	if (widget.isAttached()) {
    		return AsyncFunc.immediate();
    	}
    	return new AsyncFunc<Void,Void>(){
			@Override
			protected <S, E> void run(Void arg, final Func<Void, S> successHandler, Func<Throwable, E> errorHandler) {
				if (widget.isAttached()) {
					successHandler.apply(null);
					return;
				}
				final RegistrationsManager regs = new RegistrationsManager();
		    	regs.add(widget.addAttachHandler(new AttachEvent.Handler() {
					@Override
					public void onAttachOrDetach(AttachEvent event) {
						if (false == event.isAttached()) {
							return;
						}
						regs.clear();
						successHandler.apply(null);
					}
				}));
			}
		};
	}

    public static void disableContextMenu(Widget widget){
        widget.addDomHandler(new ContextMenuHandler() {

            @Override
            public void onContextMenu(ContextMenuEvent event) {
                event.preventDefault();
            }
        }, ContextMenuEvent.getType());
    }

    public static Func.VoidAction setEnabledFunc(final HasEnabled widget, final boolean isEnabled)
    {
        return new Func.VoidAction() {
            @Override
            public void exec()
            {
                widget.setEnabled(isEnabled);
            }
        };
    }

    public static void addNonEmptyStyleName(Widget widget, String style)
    {
        if (Strings.isNullOrEmpty(style))
        {
            return;
        }
        widget.addStyleName(style);
    }

    public static void removeNonEmptyStyleName(Widget widget, String style)
    {
        if (Strings.isNullOrEmpty(style))
        {
            return;
        }
        widget.removeStyleName(style);
    }

    public static HandlerRegistration addMovementStartHandler(Widget widget, final SimpleEvent.Handler<HumanInputEvent<?>> handler)
    {
        RegistrationsManager regs = new RegistrationsManager();
        regs.add(widget.addDomHandler(new MouseDownHandler() {
            @Override public void onMouseDown(MouseDownEvent event) {
                handler.onFire(event);
            }
        }, MouseDownEvent.getType()));
        regs.add(widget.addDomHandler(new TouchStartHandler() {
            @Override public void onTouchStart(TouchStartEvent event) {
                handler.onFire(event);
            }
        }, TouchStartEvent.getType()));
        return regs.asSingleRegistration();
    }

    public static HandlerRegistration addMovementStopHandler(Widget widget, final SimpleEvent.Handler<HumanInputEvent<?>> handler)
    {
        RegistrationsManager regs = new RegistrationsManager();
        regs.add(widget.addDomHandler(new MouseUpHandler() {
            @Override public void onMouseUp(MouseUpEvent event) {
                handler.onFire(event);
            }
        }, MouseUpEvent.getType()));
        regs.add(widget.addDomHandler(new TouchEndHandler() {
            @Override public void onTouchEnd(TouchEndEvent event) {
                handler.onFire(event);
            }
        }, TouchEndEvent.getType()));
        return regs.asSingleRegistration();
    }

    public static HandlerRegistration addMovementMoveHandler(Widget widget, final SimpleEvent.Handler<HumanInputEvent<?>> handler)
    {
        RegistrationsManager regs = new RegistrationsManager();
        regs.add(widget.addDomHandler(new MouseMoveHandler() {
            @Override public void onMouseMove(MouseMoveEvent event) {
                handler.onFire(event);
            }
        }, MouseMoveEvent.getType()));
        regs.add(widget.addDomHandler(new TouchMoveHandler() {
            @Override public void onTouchMove(TouchMoveEvent event) {
                handler.onFire(event);
            }
        }, TouchMoveEvent.getType()));
        return regs.asSingleRegistration();
    }
}
