package com.project.canvas.client.shared;

import java.util.HashMap;

import com.google.gwt.dom.client.Element;
import com.project.canvas.shared.data.Rectangle2D;

public abstract class ElementUtils {
    public static boolean isOverlappingElements(Element element1, Element element2) {
        return ElementUtils.getElementRectangle(element1).isOverlapping(
                ElementUtils.getElementRectangle(element2));
    }

    public static Rectangle2D getElementRectangle(Element element) {
        return new Rectangle2D(element.getAbsoluteLeft(), element.getAbsoluteTop(),
                element.getAbsoluteRight(), element.getAbsoluteBottom());
    }

    // TODO: warning, this may keep element objects alive after not being used!
    private static HashMap<Element, Integer> rotations = new HashMap<Element, Integer>();

    public static void setRotation(Element element, int degrees) {
        sandpaperSetRotation(element, degrees);
        if (0 == degrees) {
            rotations.remove(element);
        }
        rotations.put(element, degrees);
    }

    public static int getRotation(Element element) {
        Integer rotation = rotations.get(element);
        return rotation != null ? rotation.intValue() : 0;
    }

    public static void sandpaperSetRotation(Element element, int degrees) {
       element.getStyle().setProperty("transform", "rotate(" + degrees + "deg)");
       element.getStyle().setProperty("MozTransform", "rotate(" + degrees + "deg)");
       element.getStyle().setProperty("WebkitTransform", "rotate(" + degrees + "deg)");
   	}

}
