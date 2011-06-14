package com.project.canvas.shared.contracts;

public interface ICloneable {
	//Cant use clone() since it's not supported by GWT and fails on compile time.
	Object createInstance();
	void copyTo(Object object);
	
}
