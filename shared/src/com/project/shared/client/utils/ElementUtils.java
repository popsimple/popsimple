package com.project.shared.client.utils;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Image;
import com.project.shared.client.events.SimpleEvent;
import com.project.shared.client.net.ImageLoader;
import com.project.shared.data.KeyValue;
import com.project.shared.data.Point2D;
import com.project.shared.data.Rectangle;
import com.project.shared.utils.IterableUtils;
import com.project.shared.utils.StringUtils;

public abstract class ElementUtils
{
    private static final String CONTENTEDITABLE = "contenteditable";

    /**
     * A wrapper for the native {@link Element#getChildNodes()} that returns a java ArrayList.
     */
    public static ArrayList<Node> getChildNodes(Element element)
    {
        ArrayList<Node> res = new ArrayList<Node>();
        NodeList<Node> childNodes = element.getChildNodes();
        for (int i = 0 ; i < childNodes.getLength(); i++)
        {
            res.add(childNodes.getItem(i));
        }
        return res;
    }

    public static boolean areOverlappingElements(Element element1, Element element2) {
        // TODO: fix bugs in Rectangle and use isOverlapping instead of isExternalCircleOverlapping
        return ElementUtils.getElementAbsoluteRectangle(element1).isExternalCircleOverlapping(
                ElementUtils.getElementAbsoluteRectangle(element2));
    }

    public static Rectangle getElementAbsoluteRectangle(Element element) {
        // remember that css coordinates are from top-left of screen
        // and css rotation is clockwise
        return new Rectangle(ElementUtils.getAbsoluteLeftWithoutTransforms(element), ElementUtils.getAbsoluteLeftWithoutTransforms(element),
                             ElementUtils.getAbsoluteRightWithoutTransforms(element), ElementUtils.getAbsoluteBottomWithoutTransforms(element),
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
    private static HashMap<Element, Double> rotations = new HashMap<Element, Double>();

    public static void setRotation(Element element, double degrees) {
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
		final Style style = element.getStyle();
        style.setProperty("transformOrigin", originValue);
        style.setProperty("MozTransformOrigin", originValue);
        style.setProperty("WebkitTransformOrigin", originValue);
        style.setProperty("MsTransformOrigin", originValue);
        cssSetMSProperty(element, "transform-origin", originValue);
	}

    public static void resetTransformOrigin(Element element) {
        setTransformOrigin(element, "");
    }


    public static double getRotation(Element element) {
        Double rotation = rotations.get(element);
        return rotation != null ? rotation.intValue() : 0;
    }

    private static void cssSetRotation(Element element, double degrees)
    {
        String transformValue = "rotate(" + degrees + "deg)";
        final Style style = element.getStyle();
        style.setProperty("transform", transformValue);
        style.setProperty("MozTransform", transformValue);
        style.setProperty("WebkitTransform", transformValue);
        style.setProperty("MsTransform", transformValue);
        cssSetMSProperty(element, "transform", transformValue);
    }

    private static final native void cssSetMSProperty(Element element, String name, String value) /*-{
        element.style['-ms-' + name] = value;
    }-*/;

    /**
     * Calculates the position of the mouse event relative to a given element.
     * <strong>Don't use event.getRelativeX/Y!</strong>, because Firefox and IE/Chrome have different results for when the element is rotated.
     * @param event
     * @param elem
     * @return
     */
	public static Point2D getRelativePosition(MouseEvent<?> event, Element elem) {
	    Point2D eventPos = new Point2D(event.getClientX(), event.getClientY());
	    final Point2D elementAbsolutePosition = ElementUtils.getElementAbsolutePosition(elem);
        return eventPos.minus(elementAbsolutePosition);
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
            Point2D curPos = pos.minus(oldPos).mul(Math.max(0, progress)).plus(oldPos);
            setElementCSSPosition(element, curPos);
        }
    };

    /**
     * TODO: For animation to work without glitches, the element must not have
     * margins, because getElementOffsetPosition does not take them into
     * account, but setElementPosition does.
     */
    public static void setElementCSSPosition(final Element element, final Point2D pos, int animationDuration) {
        if (0 == animationDuration)
        {
            ElementUtils.setElementCSSPosition(element, pos);
        }
        final Point2D oldPos = getElementOffsetPosition(element);
        PositionAnimation anim = new PositionAnimation(oldPos, pos, element);
        anim.run(animationDuration);
    }

    /**
     * Sets the element's style's top and left css properties, in PX units, to the given coordinates.
     * @param element
     * @param pos
     */
	public static void setElementCSSPosition(Element element, Point2D pos) {
		element.getStyle().setLeft(pos.getX(), Unit.PX);
		element.getStyle().setTop(pos.getY(), Unit.PX);
    }

	public static Point2D getElementCSSPosition(Element element)
	{
	    String leftStr = element.getStyle().getLeft();
	    String topStr = element.getStyle().getTop();
	    Integer left = fromPXUnitString(leftStr);
	    Integer top = fromPXUnitString(topStr);
	    if (null == left || null == top) {
	        return null;
	    }
	    return new Point2D(left, top);
	}

	private static Integer fromPXUnitString(String cssPxUnitNumStr)
    {
	    String PX_SUFFIX = "px";
	    String trimmedStr = cssPxUnitNumStr.trim();
	    if (false == trimmedStr.toLowerCase().endsWith(PX_SUFFIX))
	    {
	        return null;
	    }
	    try {
	        Double value = Double.valueOf(trimmedStr.substring(0, trimmedStr.length() - PX_SUFFIX.length()));
	        return (int)Math.round(value);
	    }
	    catch (NumberFormatException e) {
	        return null;
	    }
    }

    public static Point2D getElementOffsetPosition(Element element) {
    	return new Point2D(element.getOffsetLeft(), element.getOffsetTop());
    }

    /**
     * Returns the element's absolute position <strong>disregarding css transforms</strong> (as if no transforms are applied)
     * @param element
     */
    public static Point2D getElementAbsolutePosition(Element element) {
    	return new Point2D(ElementUtils.getAbsoluteLeftWithoutTransforms(element), ElementUtils.getAbsoluteTopWithoutTransforms(element));
    }

    /**
     * An alternative to GWT's own implementation of getAbsoluteLeft/Top which is inconsistent among browsers.
     *
     * TODO: Force GWT to use this function instead of <code>getAbsoluteLeft</code> using deferred binding.
     *
     * @See <a href="http://code.google.com/p/google-web-toolkit/issues/detail?id=5645">GWT issue 5645</a>
     * @param elem
     */
    public static native int getAbsoluteLeftWithoutTransforms(Element elem)
    /*-{
        var left = 0;
        var curr = elem;
        // This intentionally excludes body which has a null offsetParent.
        while (curr.offsetParent) {
          left -= curr.scrollLeft;
          curr = curr.parentNode;
        }
        while (elem) {
          left += elem.offsetLeft;
          elem = elem.offsetParent;
        }
        return left;
    }-*/;

    /**
     * See {@link #getAbsoluteLeftWithoutTransforms(Element)}
     */
    public static native int getAbsoluteTopWithoutTransforms(Element elem)
    /*-{
        var top = 0;
        var curr = elem;
        // This intentionally excludes body which has a null offsetParent.
        while (curr.offsetParent) {
            top -= curr.scrollTop;
            curr = curr.parentNode;
        }
        while (elem) {
            top += elem.offsetTop;
            elem = elem.offsetParent;
        }
        return top;
    }-*/;

    /**
     * See {@link #getAbsoluteLeftWithoutTransforms(Element)}
     */
    public static int getAbsoluteBottomWithoutTransforms(Element elem) {
        return getAbsoluteTopWithoutTransforms(elem) + elem.getOffsetHeight();
    }

    /**
     * See {@link #getAbsoluteLeftWithoutTransforms(Element)}
     */
    public static int getAbsoluteRightWithoutTransforms(Element elem) {
        return getAbsoluteLeftWithoutTransforms(elem) + elem.getOffsetWidth();
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
		setElementCSSPosition(element, new Point2D(rectangle.getLeft(), rectangle.getTop()));
		setElementSize(element, rectangle.getSize());
    }

    public static void setBackgroundImage(Element element, Image image, boolean autoSize)
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
                StyleUtils.buildBackgroundUrl(image.getUrl()));
    }

    public static void setBackgroundImageAsync(final Element element,
            String imageUrl, String errorImageUrl, final boolean autoSize,
            final SimpleEvent.Handler<Void> loadHandler, final SimpleEvent.Handler<Void> errorHandler)
    {
        ImageLoader imageLoader = new ImageLoader();
        imageLoader.addLoadHandler(new SimpleEvent.Handler<KeyValue<Integer,Image>>() {
            @Override
            public void onFire(KeyValue<Integer, Image> arg) {
                ElementUtils.setBackgroundImage(element, arg.getValue(), autoSize);
                loadHandler.onFire(null);
            };
        });
        imageLoader.addErrorHandler(new SimpleEvent.Handler<Void>() {
            @Override
            public void onFire(Void arg) {
                errorHandler.onFire(null);
            };
        });
        imageLoader.load(new String[]{imageUrl, errorImageUrl});
    }

    public static void addClassName(Element element, String className)
    {
        if (StringUtils.isEmptyOrNull(className))
        {
            return;
        }
        element.addClassName(className);
    }

    public static void removeClassName(Element element, String className)
    {
        if (StringUtils.isEmptyOrNull(className))
        {
            return;
        }
        element.removeClassName(className);
    }

    public static void generateId(String prefix, Element elem)
    {
        elem.setId(prefix + "_" + String.valueOf(Random.nextInt()));
    }

    /**
     * Merges or removes redundant span elements from the hierarchy rooted at rootElem.
     * The merges that are done are:
     * <el>
     * <li>If two adjacent siblings are spans and have the same style, they are merged.</li>
     * <li>If an element has only a single child and it is a span, it is merged with the child.</li>
     * <li>If a span has no style properties (it completely inherits parent styles) - replace it with its children (move the children to the parent and remove the span).</li>
     * </el>
     * <p>TODO: Merge adjacent text nodes.</p>
     * @param rootElem
     * @return True if the tree was changed; false otherwise.
     */
    public static boolean mergeSpans(Element rootElem)
    {
        boolean anyChangeOccured = false;
        boolean hasChanged = false;
        do {
            // first, merge any adjacent child spans that have identical styles
            hasChanged = ElementUtils.mergeSiblingSpans(rootElem);

            while (true)
            {
                // repeat until current element doesn't have a single child to be merged with
                if (ElementUtils.mergeUpSingleChildSpan(rootElem))
                {
                    hasChanged = true;
                    continue;
                }
                break;
            }

            // Perform the same on all element children
            for (Node childNode : ElementUtils.getChildNodes(rootElem))
            {
                if (Node.ELEMENT_NODE != childNode.getNodeType())
                {
                    continue;
                }
                Element childElem = Element.as(childNode);
                if (ElementUtils.isSpanElement(childElem) && StyleUtils.hasCompletelyInheritedStyle(childElem)) {
                    for (Node grandChildNode : IterableUtils.reverse(ElementUtils.getChildNodes(childElem))) {
                        grandChildNode.removeFromParent();
                        rootElem.insertAfter(grandChildNode, childElem);
                    }
                    rootElem.removeChild(childElem);
                    hasChanged = true;
                }
                else {
                    hasChanged |= ElementUtils.mergeSpans(childElem);
                }
            }
            anyChangeOccured |= hasChanged;

        } while (hasChanged);

        return anyChangeOccured;
    }

    /**
     * Merges every two adjacent span children of the given <code>element</code> if they have equivalent styles.
     * @param element
     * @return True if any change was made to the tree; false otherwise.
     */
    private static boolean mergeSiblingSpans(Element element)
    {
        boolean hasChanged = false;
        Element previousChild = null;
        for (Node childNode : ElementUtils.getChildNodes(element))
        {
            if (Node.ELEMENT_NODE != childNode.getNodeType())
            {
                previousChild = null;
                continue;
            }

            Element childElem = Element.as(childNode);
            if (false == ElementUtils.isSpanElement(childElem))
            {
                previousChild = null;
                continue;
            }

            if (null == previousChild)
            {
                previousChild = childElem;
                continue;
            }

            if (false == StyleUtils.areComputedStylesEquivalent(previousChild, childElem))
            {
                // can't unify.
                previousChild = childElem;
                continue;
            }

            // Unify the spans.
            for (Node grandChildNode : ElementUtils.getChildNodes(childElem))
            {
                grandChildNode.removeFromParent();
                previousChild.appendChild(grandChildNode);
            }
            childElem.removeFromParent();
            hasChanged = true;
        }
        return hasChanged;
    }

    public static boolean isSpanElement(Element childElem)
    {
        return childElem.getTagName().toLowerCase().equals("span");
    }

    /**
     * Checks if the given element has a single child which is a <span>
     * if yes, it moves all the grand children (the children of the <span>)
     * to become children of this element, and then removes the empty <span>.
     * Also, the style of the span is moved up to the element.
     * @param element The element for which to perform the operation, if there's a single span child.
     */
    public static boolean mergeUpSingleChildSpan(Element element)
    {
        if (1 != element.getChildCount()) {
            return false;
        }
        Node childNode = element.getChild(0);
        if (Node.ELEMENT_NODE != childNode.getNodeType()) {
            return false;
        }
        Element childElem = Element.as(childNode);
        if (false == ElementUtils.isSpanElement(childElem))
        {
            return false;
        }

        StyleUtils.copyStyle(childElem, element, false);
        StyleUtils.copyStyle(element, childElem, true);

        for (Node node : ElementUtils.getChildNodes(childElem))
        {
            node.removeFromParent();
            element.appendChild(node);
        }

        childElem.removeFromParent();
        return true;
    }


    public static void setContentEditable(Element element, boolean isContentEditable)
    {
        if (isContentEditable) {
            element.removeAttribute(CONTENTEDITABLE);
        } else {
            element.setAttribute(CONTENTEDITABLE, "true");
        }
    }

}
