package com.project.canvas.client.canvastools.textedit;

import com.google.gwt.user.client.ui.Widget;
import com.project.canvas.client.canvastools.base.CanvasToolFactoryBase;
import com.project.canvas.shared.data.Point2D;
import com.project.canvas.shared.data.TextData;

public class TextEditToolFactory extends CanvasToolFactoryBase<TextEditTool> {
    
    //TODO: Set a better uniqueId.
    public static final String UNIQUE_ID = "TextEditToolFactory";
    
    public TextEditTool create() {
        TextEditTool textEditTool = new TextEditTool();
        textEditTool.setValue(new TextData(UNIQUE_ID));
        return textEditTool;
    }

    @Override
    public boolean isOneShot() {
        return false;
    }

    @Override
    public Widget getFloatingWidget() {
        return null;
    }
    
    @Override
    public Point2D getCreationOffset() {
        return new Point2D(-10, -62);
    }
}