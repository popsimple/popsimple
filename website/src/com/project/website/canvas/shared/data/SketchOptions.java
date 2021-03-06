package com.project.website.canvas.shared.data;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.project.website.canvas.client.canvastools.sketch.DrawingTool;
import com.project.website.canvas.client.canvastools.sketch.SketchTool.SpiroCurveType;

public class SketchOptions implements IsSerializable, Serializable {
    private static final long serialVersionUID = 1L;

    public int penWidth;
    public int penSkip;
    public int eraserWidth;
    public DrawingTool drawingTool;
    public String penColor;
    public boolean useBezierSmoothing;
    public SpiroCurveType spiroCurveType;

    public SketchOptions() { }

    public SketchOptions(int penWidth, int penSkip, int eraserWidth, DrawingTool defaultDrawingTool, String penColor, boolean useBezierSmoothing, SpiroCurveType spiroCurveType)
    {
        super();
        this.useBezierSmoothing = useBezierSmoothing;
        this.penWidth = penWidth;
        this.penSkip = penSkip;
        this.eraserWidth = eraserWidth;
        this.drawingTool = defaultDrawingTool;
        this.penColor = penColor;
        this.spiroCurveType = spiroCurveType;
    }

    public SketchOptions(SketchOptions other) {
        this(other.penWidth, other.penSkip, other.eraserWidth, other.drawingTool, other.penColor, other.useBezierSmoothing, other.spiroCurveType);
    }
}