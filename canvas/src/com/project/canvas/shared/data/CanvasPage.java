package com.project.canvas.shared.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;


@Entity
public class CanvasPage  implements Serializable  {
	public @Id Long id;
	public String title;
	
	@Transient
	transient public List<ElementData> elements;
	
	public CanvasPage() {
		this.elements = new ArrayList<ElementData>();
	}
}
