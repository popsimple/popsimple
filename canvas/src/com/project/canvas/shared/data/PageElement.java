package com.project.canvas.shared.data;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class PageElement {
	protected PageElement() {}
	public PageElement(CanvasPage page, ElementData data) {
		this.page = new Key<CanvasPage>(CanvasPage.class, page.id);
		this.data = new Key<ElementData>(ElementData.class, data.id);
	}
	public @Id Long id;
	public Key<CanvasPage> page;
	public Key<ElementData> data;
}
