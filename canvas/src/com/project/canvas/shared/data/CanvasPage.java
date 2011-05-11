package com.project.canvas.shared.data;

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;


@Entity
public class CanvasPage  implements Serializable  {
	private static final long serialVersionUID = 1L;
	
	public @Id Long id;
	public String title;
	
	@Transient
	public ArrayList<ElementData> elements;
	
	public CanvasPage() {}
}
