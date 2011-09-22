package com.project.shared.client.utils.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
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
        widget.addDomHandler(new MouseDownHandler() {
            public void onMouseDown(MouseDownEvent arg0) {
                arg0.stopPropagation();
            }
        }, MouseDownEvent.getType());
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

    public static void disableDrag(Widget widget) {
        widget.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                event.preventDefault();
            }
        }, MouseDownEvent.getType());
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
	        final boolean autoSize, final String loadingStyleName,
	        final SimpleEvent.Handler<Void> loadHandler, final SimpleEvent.Handler<Void> errorHandler)
    {
	    widget.getElement().getStyle().clearBackgroundImage();
	    widget.addStyleName(loadingStyleName);
	    ElementUtils.setBackgroundImageAsync(widget.getElement(), imageUrl, errorImageUrl, autoSize,
            new SimpleEvent.Handler<Void>() {
                @Override
                public void onFire(Void arg) {
                    widget.removeStyleName(loadingStyleName);
                    loadHandler.onFire(null);
                }},
            new SimpleEvent.Handler<Void>() {
                @Override
                public void onFire(Void arg) {
                    widget.removeStyleName(loadingStyleName);
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
					successHandler.call(null);
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
						successHandler.call(null);
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
}
