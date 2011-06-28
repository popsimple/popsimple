package com.project.website.canvas.client;

import java.util.HashMap;

import com.project.website.canvas.client.canvastools.base.CanvasTool;
import com.project.website.canvas.client.canvastools.base.CanvasToolFactory;
import com.project.website.canvas.shared.data.ElementData;

public class ToolFactories {

    private ToolFactories() {}

    public final static ToolFactories INSTANCE = new ToolFactories();

    HashMap<String, CanvasToolFactory<? extends CanvasTool<? extends ElementData>> > map = new HashMap<String, CanvasToolFactory<? extends CanvasTool<? extends ElementData>>>();

    public void put(String factoryId, CanvasToolFactory<? extends CanvasTool<? extends ElementData>> factory)
    {
        if (this.map.containsKey(factoryId)) {
            throw new RuntimeException("Factory already registered with id: " + factoryId);
        }
        this.map.put(factoryId, factory);
    }

    public CanvasToolFactory<? extends CanvasTool<? extends ElementData>> get(String factoryId)
    {
        return this.map.get(factoryId);
    }
}
