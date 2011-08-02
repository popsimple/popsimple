package com.project.shared.utils;

import com.project.shared.interfaces.ICloneable;

public class CloneableUtils {

	public static Object clone(ICloneable cloneable){
		Object clone = cloneable.createInstance();
		cloneable.copyTo(clone);
		return clone;
	}

}
