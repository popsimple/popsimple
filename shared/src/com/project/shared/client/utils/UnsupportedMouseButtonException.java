package com.project.shared.client.utils;

import com.project.shared.data.MouseButtons;

public class UnsupportedMouseButtonException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    public UnsupportedMouseButtonException(MouseButtons mouseButton)
    {
        super("Unsupported MouseButton: " + mouseButton.toString());
    }
}
