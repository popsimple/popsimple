package com.project.canvas.shared.data;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class PageElementAssociation {
	protected PageElementAssociation() {}
	public PageElementAssociation(Key<CanvasPage> page, Key<ElementData> data) {
		this.page = page;
		this.data = data;
	}
	public @Id Long id;
	public Key<CanvasPage> page;
	public Key<ElementData> data;
}
