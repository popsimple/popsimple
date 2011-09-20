package com.project.shared.client.utils;

import java.util.ArrayList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;

public class StyleUtils
{
    public static boolean areEquivalent(Style a, Style b)
    {
//        Logger.log("Comparing styles: a = " + StyleUtils.getCssText(a));
//        Logger.log("                  b = " + StyleUtils.getCssText(b));
        return StyleUtils.isSubsetOf(a, b) && StyleUtils.isSubsetOf(b, a);
    }

    public static boolean areComputedStylesEquivalent(Element a, Element b)
    {
        return StyleUtils.areEquivalent(StyleUtils.getComputedStyle(a, null), StyleUtils.getComputedStyle(b, null));
    }

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
     * @param elem The element for which to get the computed style object
     * @param pseudoElement can be null for most purposes, may work with ":after" or ":before" - I couldn't get it to work.
     * @return The "final" style of the element
     */
    public static native final Style getComputedStyle(Element elem, String pseudoElement)
    /*-{
        return $wnd.getComputedStyle(elem, pseudoElement);
    }-*/;


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

    public static final native String getCssText(Style style)
    /*-{
        return style.cssText;
    }-*/;

    /**
     * Wraps all the text node descendants with span elements and moves all text-decoration
     * style declarations down into the text-node wrappers.
     * If an element contains only text nodes, it does not wrap the text.
     * If any descendant of elem is an empty span, it removes it from the tree.
     *
     * There reason we need this, is that there's a general problem with text-decoration,
     * that a child element can never override that value if a parent has set it.
     * http://stackoverflow.com/questions/4481318/css-text-decoration-property-cannot-be-overridden-by-ancestor-element
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



    public static native final void copyStyle(Element to, Element from, boolean overrideExistingPropertiesInTarget)
    /*-{
        if (overrideExistingPropertiesInTarget)
        {
            to.style.cssText = from.style.cssText;
            return;
        }

        for (var i = from.style.length; i-->0;)
        {
            var name = from.style[i];
            var existingValue = to.style.getPropertyValue(name);
            if ('' === existingValue) {
                to.style.setProperty(name, from.style.getPropertyValue(name), priority = from.style.getPropertyPriority(name));
            }
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
}
