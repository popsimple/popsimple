package com.project.canvas.shared.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Serialized;


@Entity
public class CanvasPage  implements Serializable  {
	private static final long serialVersionUID = 1L;

	public @Id Long id;
	public String title;
	
	@Transient
	transient public List<ElementData> elements;
	
	public CanvasPage() {
		this.elements = new ArrayList<ElementData>();
	}
}
