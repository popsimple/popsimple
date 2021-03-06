package com.project.website.canvas.client.canvastools.textedit;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.project.shared.data.Point2D;
import com.project.website.canvas.client.canvastools.base.CanvasToolFactoryBase;
import com.project.website.canvas.shared.data.TextData;

public class TextEditToolFactory extends CanvasToolFactoryBase<TextEditTool>
{

    // TODO: Set a better uniqueId.
    public static final String UNIQUE_ID = "TextEditToolFactory";

    public TextEditTool create()
    {
        TextEditTool textEditTool = new TextEditTool();
        textEditTool.setValue(new TextData(UNIQUE_ID));
        return textEditTool;
    }

    @Override
    public Widget getFloatingWidget()
    {
        return new FlowPanel();
    }

    @Override
    public Point2D getCreationOffset()
    {
        return new Point2D(-3, -10);
    }

    @Override
    public String getFactoryId()
    {
        return TextEditToolFactory.UNIQUE_ID;
    }
}