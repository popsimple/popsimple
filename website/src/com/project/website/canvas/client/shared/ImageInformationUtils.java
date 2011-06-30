package com.project.website.canvas.client.shared;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.utils.StyleUtils;
import com.project.shared.client.utils.WidgetUtils;
import com.project.website.canvas.client.resources.CanvasResources;
import com.project.website.canvas.shared.data.ImageInformation;
import com.project.website.canvas.shared.data.ImageOptions;

//TODO: Probably needs to be a different class, it doesn't exactly complies with ...Utils convention.
public class ImageInformationUtils
{
    private static void clearWidgetBacgkround(Widget widget)
    {
        Style style = widget.getElement().getStyle();
        style.clearBackgroundImage();
        StyleUtils.clearBackgroundRepeat(style);
        StyleUtils.clearBackgroundSize(style);
        StyleUtils.clearBackgroundPosition(style);
    }

    public static void setWidgetBackgroundAsync(final ImageInformation imageInformation,
            Widget widget, boolean autoSizeElement)
    {
        ImageInformationUtils.setWidgetBackgroundAsync(imageInformation, widget, autoSizeElement,
                new SimpleEvent.EmptyHandler<Void>(), new SimpleEvent.EmptyHandler<Void>());
    }

    public static void setWidgetBackgroundAsync(final ImageInformation imageInformation,
            Widget widget, boolean autoSizeElement, final SimpleEvent.Handler<Void> loadHandler,
            final SimpleEvent.Handler<Void> errorHandler)
    {
        ImageInformationUtils.clearWidgetBacgkround(widget);
        if (imageInformation.url == null || imageInformation.url.trim().isEmpty()) {
            return;
        }

        final Style style = widget.getElement().getStyle();

        WidgetUtils.SetBackgroundImageAsync(widget, imageInformation.url,
                CanvasResources.INSTANCE.imageUnavailable().getURL(), autoSizeElement,
                CanvasResources.INSTANCE.main().imageLoadingStyle(), new SimpleEvent.Handler<Void>() {
                    @Override
                    public void onFire(Void arg) {
                        ImageOptions imageOptions = imageInformation.options;
                        StyleUtils.setBackgroundRepeat(style, imageOptions.repeat);
                        StyleUtils.setBackgroundStretch(style,
                                imageOptions.stretchWidth, imageOptions.stretchHeight);
                        if (imageOptions.center)
                        {
                            StyleUtils.setBackgroundCenter(style);
                        }
                        loadHandler.onFire(null);
                    }
                }, errorHandler);
    }
}
