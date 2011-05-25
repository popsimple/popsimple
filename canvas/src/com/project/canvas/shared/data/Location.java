package com.project.canvas.shared.data;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Location implements Serializable, IsSerializable
{
    private static final long serialVersionUID = 1L;

    public double longitude;
    public double latitude;
    
    public String address;
}
