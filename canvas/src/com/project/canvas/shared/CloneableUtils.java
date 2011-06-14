package com.project.canvas.shared;

import com.project.canvas.shared.contracts.ICloneable;

public class CloneableUtils {
	
	public static Object clone(ICloneable cloneable){
		Object clone = cloneable.createInstance();
		cloneable.copyTo(clone);
		return clone;
	}

}
