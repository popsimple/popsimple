package com.project.canvas.shared.data;

import com.google.code.twig.annotation.Embedded;

public class ImageData extends ElementData {
	private static final long serialVersionUID = 1L;

	public String url;
	
	@Embedded
	public Point2D size;
	
	public int rotation; // degrees, counter-clockwise
}
