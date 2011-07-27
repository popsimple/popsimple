package com.project.website.shared.data;

import java.io.Serializable;

import com.google.code.twig.annotation.Id;

public class Invitation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    public String id;
}
