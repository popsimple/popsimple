package com.project.canvas.client.shared;

import java.util.HashMap;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.user.client.ui.Image;
import com.project.canvas.client.shared.events.SimpleEvent;
import com.project.canvas.shared.StringUtils;
import com.project.canvas.shared.data.Point2D;
import com.project.canvas.shared.data.Rectangle;

public abstract class ElementUtils {
    public static boolean areOverlappingElements(Element element1, Element element2) {
        // TODO: fix bugs in Rectangle and use isOverlapping instead of isExternalCircleOverlapping
        return ElementUtils.getElementAbsoluteRectangle(element1).isExternalCircleOverlapping(
                ElementUtils.getElementAbsoluteRectangle(element2));
    }

    public static Rectangle getElementAbsoluteRectangle(Element element) {
        // remember that css coordinates are from top-left of screen
        // and css rotation is clockwise
        return new Rectangle(element.getAbsoluteLeft(),  element.getAbsoluteTop(),
                             element.getAbsoluteRight(), element.getAbsoluteBottom(),
                             getRotation(element));
    }

    public static Rectangle getElementOffsetRectangle(Element element) {
        // remember that css coordinates are from top-left of screen
        // and css rotation is clockwise
        return new Rectangle(element.getOffsetLeft(),  element.getOffsetTop(),
                element.getOffsetLeft() + element.getOffsetWidth(),
                element.getOffsetTop() + element.getOffsetHeight(),
                             getRotation(element));
    }


    // TODO: warning, this may keep element objects alive after not being used!
    private static HashMap<Element, Integer> rotations = new HashMap<Element, Integer>();

    public static void setRotation(Element element, int degrees) {
        cssSetRotation(element, degrees);
        if (0 == degrees) {
            rotations.remove(element);
        }
        rotations.put(element, degrees);
    }

    public static void setTransformOriginTopLeft(Element element) {
    	String originValue = "0 0";
        setTransformOrigin(element, originValue);
    }

	private static void setTransformOrigin(Element element, String originValue) {
		element.getStyle().setProperty("transformOrigin", originValue);
        element.getStyle().setProperty("MozTransformOrigin", originValue);
        element.getStyle().setProperty("WebkitTransformOrigin", originValue);
        element.getStyle().setProperty("MsTransformOrigin", originValue);
        cssSetMSProperty(element, "transform-origin", originValue);
	}

    public static void resetTransformOrigin(Element element) {
        setTransformOrigin(element, "");
    }


    public static int getRotation(Element element) {
        Integer rotation = rotations.get(element);
        return rotation != null ? rotation.intValue() : 0;
    }

    private static void cssSetRotation(Element element, int degrees) {
    	String transformValue = "rotate(" + degrees + "deg)";
	   element.getStyle().setProperty("transform", transformValue);
	   element.getStyle().setProperty("MozTransform", transformValue);
	   element.getStyle().setProperty("WebkitTransform", transformValue);
	   element.getStyle().setProperty("MsTransform", transformValue);
	   cssSetMSProperty(element, "transform", transformValue);
   	}

    private static final native void cssSetMSProperty(Element element, String name, String value) /*-{
        element.style['-ms-' + name] = value;
    }-*/;

	public static Point2D relativePosition(MouseEvent<?> event, Element elem) {
	    return new Point2D(event.getRelativeX(elem), event.getRelativeY(elem));
	}

    private static class PositionAnimation extends Animation {
        private Point2D pos;
        private Point2D oldPos;
        private Element element;

        public PositionAnimation(Point2D oldPos, Point2D pos, Element element)
        {
            this.oldPos = oldPos;
            this.pos = pos;
            this.element = element;
        }

        @Override
        protected void onUpdate(double progress)
        {
            Point2D curPos = pos.minus(oldPos).mul(progress).plus(oldPos);
            setElementPosition(element, curPos);
        }
    };

    public static void setElementPosition(final Element element, final Point2D pos, int animationDuration) {
        final Point2D oldPos = getElementOffsetPosition(element);
        PositionAnimation anim = new PositionAnimation(oldPos, pos, element);
        anim.run(animationDuration);
    }

	public static void setElementPosition(Element element, Point2D pos) {
		element.getStyle().setLeft(pos.getX(), Unit.PX);
		element.getStyle().setTop(pos.getY(), Unit.PX);
    }

	public static Point2D getElementOffsetPosition(Element element) {
    	return new Point2D(element.getOffsetLeft(), element.getOffsetTop());
    }

    public static Point2D getElementAbsolutePosition(Element element) {
    	return new Point2D(element.getAbsoluteLeft(), element.getAbsoluteTop());
    }

    /**
     * Note: this size includes padding, scroll bars (and margin?) of the element.
     * See https://developer.mozilla.org/en/Determining_the_dimensions_of_elements
     */
	public static Point2D getElementOffsetSize(Element element) {
		return new Point2D(element.getOffsetWidth(), element.getOffsetHeight());
	}

    public static Point2D getElementClientSize(Element element) {
        return new Point2D(element.getClientWidth(), element.getClientHeight());
    }

    public static void setElementSize(Element element, Point2D size)
    {
        element.getStyle().setWidth(size.getX(), Unit.PX);
        element.getStyle().setHeight(size.getY(), Unit.PX);
    }

    public static void setElementRectangle(Element element, Rectangle rectangle) {
		setElementPosition(element, new Point2D(rectangle.getLeft(), rectangle.getTop()));
		setElementSize(element, rectangle.getSize());
    }

    public static void SetBackgroundImage(Element element, Image image, boolean autoSize)
    {
        if (autoSize)
        {
            Point2D imageSize = new Point2D(image.getWidth(), image.getHeight());
            // getWidth/getHeight return zero if the image size is not known. So don't set it.
            if (false == imageSize.equals(Point2D.zero)) {
                ElementUtils.setElementSize(element, imageSize);
            }
        }
        element.getStyle().setBackgroundImage(
                StyleUtils.BuildBackgroundUrl(image.getUrl()));
    }
}
