package com.project.website.canvas.client.worksheet.interfaces;

import com.project.shared.data.Point2D;

public interface MouseMoveOperationHandler
{
    void onMouseMove(Point2D pos);
    void onStart();
    void onCancel();
    void onStop(Point2D pos);
}
