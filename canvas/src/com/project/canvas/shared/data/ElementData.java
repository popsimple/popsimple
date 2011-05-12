package com.project.canvas.shared.data;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Id;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class ElementData implements Serializable, IsSerializable  {
	private static final long serialVersionUID = 1L;
	@Id
	public Long id;
	
	@Embedded
	public Point2D position;
}
