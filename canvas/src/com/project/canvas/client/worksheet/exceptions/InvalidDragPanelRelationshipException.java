package com.project.canvas.client.worksheet.exceptions;


public class InvalidDragPanelRelationshipException extends RuntimeException  {
    private static final long serialVersionUID = 1L;

    public InvalidDragPanelRelationshipException()
    {
        super("Invalid drag panel relationship.");
    }
}
