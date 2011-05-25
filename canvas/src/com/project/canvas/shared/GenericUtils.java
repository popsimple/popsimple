package com.project.canvas.shared;

public class GenericUtils {
    public static <T> T defaultIfNull(T obj, T _default) {
        return obj != null ? obj : _default;
    }
}
