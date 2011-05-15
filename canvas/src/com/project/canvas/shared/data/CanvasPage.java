package com.project.canvas.shared.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.code.twig.annotation.Id;
import com.google.gwt.user.client.rpc.IsSerializable;


public class CanvasPage  implements Serializable, IsSerializable  {
	private static final long serialVersionUID = 1L;
	
	@Id 
	public Long id;
	public String title;
	
	public List<ElementData> elements = new ArrayList<ElementData>();
}
