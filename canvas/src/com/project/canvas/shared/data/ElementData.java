package com.project.canvas.shared.data;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class ElementData implements Serializable  {
	public @Id Long id;
	public Integer posX;
	public Integer posY;
}
