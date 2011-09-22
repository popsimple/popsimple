package com.project.shared.client.utils;

import java.util.ArrayList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;

public class StyleUtils
{
    public static Integer getTopPx(Style style) {
        String topStr = style.getTop();
        Integer top = fromPXUnitString(topStr);
        return top;
    }


    public static Integer getLeftPx(Style style) {
        String leftStr = style.getLeft();
        Integer left = fromPXUnitString(leftStr);
        return left;
    }


    /**
     * @return True if the given style objects are equivalent - every property set in <code>a</code> is also set in <code>b</code> and has the same value, and vice verse.
     */
    public static boolean areEquivalent(Style a, Style b)
    {
        return StyleUtils.isSubsetOf(a, b) && StyleUtils.isSubsetOf(b, a);
    }

    /**
     * @return True if two elements have equivalent computed styles (more accurately: "used styles"). False otherwise.
     * <p>Note: Having equivalent computed styles does not guarantee that they look the same on the screen - due to text-decoration's weird specification.</p>
     */
    public static boolean areComputedStylesEquivalent(Element a, Element b)
    {
        return StyleUtils.areEquivalent(StyleUtils.getComputedStyle(a, null), StyleUtils.getComputedStyle(b, null));
    }

    /**
     * @return True if the element has a style equivalent to it's direct parent.
     */
    public static boolean hasCompletelyInheritedStyle(Element childElem)
    {
        return StyleUtils.areComputedStylesEquivalent(childElem, childElem.getParentElement());
    }

    /**
     * Compares two styles, checking if one is a subset of the other.
     * @return true if every property of the "subsetCandidate" exists in set and has the same value. Otherwise, returns false.
     */
    public static native final boolean isSubsetOf(Style subsetCandidate, Style set)
    /*-{
        for (var i = 0; i < subsetCandidate.length; i++)
        {
            var name = subsetCandidate[i];
            if (subsetCandidate.getPropertyValue(name) !== set.getPropertyValue(name))
            {
                return false;
            }
        }
        return true;
    }-*/;

    /**
     * See https://developer.mozilla.org/en/DOM/window.getComputedStyle
     * <p><strong>Warning</strong>: for the text-decoration css property, consider using {@link #getInheritedTextDecoration(Element)}.</p>
     * @param elem The element for which to get the computed style object
     * @param pseudoElement can be null for most purposes, may work with ":after" or ":before" - I couldn't get it to work.
     * @return The "final" style of the element
     */
    public static native final Style getComputedStyle(Element elem, String pseudoElement)
    /*-{
        return $wnd.getComputedStyle(elem, pseudoElement);
    }-*/;

    /**
     * <p>This method is neccesary because getComputedStyle will not return what you expect in the case of text-decoration - because no matter what value
     * the element has in text-decoration, it will always actually be displayed with the top-most ancestor that sets this property.</p>
     * @see <a href="http://stackoverflow.com/questions/4481318/css-text-decoration-property-cannot-be-overridden-by-ancestor-element">Question in stackoverflow.com</a>
     * @param elem
     * @return The inherited value for the css text-decoration property.
     */
    public static String getInheritedTextDecoration(Element elem)
    {
        if (null == elem) {
            return "";
        }
        String value = elem.getStyle().getTextDecoration();
        if (value.isEmpty()) {
            return StyleUtils.getInheritedTextDecoration(elem.getParentElement());
        }
        return value;
    }

    /**
     * Returns the native <code>cssText</code> property of a style object.
     */
    public static final native String getCssText(Style style)
    /*-{
        return style.cssText;
    }-*/;

    
    public static native void setCssText(Style style, String cssText) 
    /*-{
        style.cssText = cssText;
    }-*/;

    /**
     * <p>Wraps all the text node descendants with span elements and moves all text-decoration
     * style declarations down into the text-node wrappers.</p>
     * <ul><li>If an element contains only text nodes, it does not wrap the text.</li>
     * <li>If any descendant of elem is an empty span, it removes it from the tree.</li></ul>
     * <p>
     * There reason we need this, is that there's a general problem with text-decoration,
     * that a child element can never override that value if a parent has set it.</p>
     * @see <a href="http://stackoverflow.com/questions/4481318/css-text-decoration-property-cannot-be-overridden-by-ancestor-element">Question in stackoverflow.com</a>
     *
     * @param elem
     */
    public static void pushStylesDownToTextNodes(Element elem)
    {
        boolean needsWrappers = false;
        ArrayList<Node> childNodes = ElementUtils.getChildNodes(elem);
        for (Node childNode : childNodes)
        {
            if (Node.TEXT_NODE != childNode.getNodeType())
            {
                needsWrappers = true;
            }
        }
        if (false == needsWrappers) {
            return;
        }
        for (Node childNode : childNodes)
        {
            if (Node.TEXT_NODE == childNode.getNodeType())
            {
                Element newChild = DOM.createSpan();
                StyleUtils.copyStyle(newChild, elem, false);
                newChild.setInnerText(childNode.getNodeValue());
                elem.replaceChild(newChild, childNode);
            }
            else if (Node.ELEMENT_NODE == childNode.getNodeType()) {
                Element childElem = Element.as(childNode);
                if (ElementUtils.isSpanElement(childElem) && (false == childElem.hasChildNodes()))
                {
                    // trim empty spans
                    childElem.removeFromParent();
                    continue;
                }
                StyleUtils.copyStyle(childElem, elem, false);
                pushStylesDownToTextNodes(childElem);

            }
            // Ignore Node.DOCUMENT_NODE
        }
        StyleUtils.deleteStyle(elem);
    }


    /**
     * Copies the element style (not computed style!) from one element to another.
     * @param to
     * @param from
     * @param overrideExistingPropertiesInTarget
     * <ul><li>True - will override the target element's style completely</li>
     * <li>False - will only copy those css properties which are set on the source, but not set on the target.</li></ul>
     */
    public static native final void copyStyle(Element to, Element from, boolean overrideExistingPropertiesInTarget)
    /*-{
        if (overrideExistingPropertiesInTarget) {
            to.style.cssText = from.style.cssText;
            return;
        }

        var existingPropertiesInTarget = [];
        for (var i = 0; i < from.style.length; i++) {
            var name = from.style[i];
            var existsInTarget = false;
            for (var j = 0; j < to.style.length; j++) {
                if (name === to.style[j]) {
                    existsInTarget = true;
                    break;
                }
            }
            if (existsInTarget) {
                continue;
            }
            to.style.setProperty(name, from.style.getPropertyValue(name), from.style.getPropertyPriority(name));
        }
    }-*/;

    public static native final void deleteStyle(Element target)
    /*-{
        target.style.cssText = '';
    }-*/;


    public static final String buildBackgroundUrl(String imageUrl)
    {
        return "url(\"" + imageUrl + "\")";
    }

    public static void clearBackgroundRepeat(Style style)
    {
        style.clearProperty(CssProperties.BACKGROUND_REPEAT);
    }

    public static void setBackgroundRepeat(Style style, boolean repeat)
    {
        if (repeat)
        {
            style.setProperty(CssProperties.BACKGROUND_REPEAT, "repeat");
        }
        else
        {
            style.setProperty(CssProperties.BACKGROUND_REPEAT, "no-repeat");
        }
    }

    public static void clearBackgroundPosition(Style style)
    {
        style.clearProperty(CssProperties.BACKGROUND_POSITION);
    }

    public static void setBackgroundCenter(Style style)
    {
        style.setProperty(CssProperties.BACKGROUND_POSITION, "center center");
    }

    public static void clearBackgroundSize(Style style)
    {
        style.clearProperty(CssProperties.BACKGROUND_SIZE);
    }

    public static void setBackgroundStretch(Style style, boolean stretchWidth, boolean stretchHeight)
    {
        String width = "";
        if (stretchWidth){
            width = "100%";
        }
        else{
            width = "auto";
        }
        String height = "";
        if (stretchHeight){
            height = "100%";
        }
        else{
            height = "auto";
        }
        style.setProperty(CssProperties.BACKGROUND_SIZE, width + " " + height);
    }

    
    public static Integer fromPXUnitString(String cssPxUnitNumStr)
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

}
