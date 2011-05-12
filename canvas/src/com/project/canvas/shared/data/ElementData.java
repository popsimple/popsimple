package com.project.canvas.shared.data;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class ElementData implements Serializable  {
	private static final long serialVersionUID = 1L;
	public @Id Long id;
	public Point2D position;
}
