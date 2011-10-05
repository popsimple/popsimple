package com.project.shared.interfaces;

public interface ICloneable<T> {
	//Cant use clone() since it's not supported by GWT and fails on compile time.
	T getClone();
}
