package com.project.canvas.shared;

public class Utils {
	public static <T> T DefaultIfNull(T obj, T _default) {
		return obj != null ? obj : _default;
	}
}
