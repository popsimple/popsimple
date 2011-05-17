package com.project.canvas.client.shared;

import com.google.gwt.dom.client.Element;
import com.project.canvas.shared.data.Rectangle2D;

public abstract class ElementExtensions 
{
	public static boolean isOverlappingElements(Element element1, Element element2)
	{
		return ElementExtensions.getElementRectangle(element1).isOverlapping(
				ElementExtensions.getElementRectangle(element2));
	}
	
	public static Rectangle2D getElementRectangle(Element element)
	{
		return new Rectangle2D(element.getAbsoluteLeft(), element.getAbsoluteTop(),
				element.getAbsoluteRight(), element.getAbsoluteBottom());
	}

}
