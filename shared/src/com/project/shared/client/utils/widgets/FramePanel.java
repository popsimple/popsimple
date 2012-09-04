package com.project.shared.client.utils.widgets;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.project.shared.utils.ArrayUtils;

public class FramePanel extends DecoratorPanel {
	public class FrameElements {
		public final Element TopLeft;
		public final Element Top;
		public final Element TopRight;
		public final Element Left;
		public final Element Right;
		public final Element BottomLeft;
		public final Element Bottom;
		public final Element BottomRight;
		public FrameElements(Element topLeft, Element top, Element topRight,
				Element left, Element right, Element bottomLeft,
				Element bottom, Element bottomRight) {
			super();
			TopLeft = topLeft;
			Top = top;
			TopRight = topRight;
			Left = left;
			Right = right;
			BottomLeft = bottomLeft;
			Bottom = bottom;
			BottomRight = bottomRight;
		}
		public Element[] getAll()
		{
			return new Element[] {
					TopLeft,
					Top,
					TopRight,
					Left,
					Right,
					BottomLeft,
					Bottom,
					BottomRight
			};
		}
	}
	public FrameElements getFrameElements()
	{
		return new FrameElements(this.getCellElement(0, 0),
								 this.getCellElement(0, 1),
								 this.getCellElement(0, 2),
								 this.getCellElement(1, 0),
								 this.getCellElement(1, 2),
								 this.getCellElement(2, 0),
								 this.getCellElement(2, 1),
								 this.getCellElement(2, 2)
				);
	}
}
