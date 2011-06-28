package com.project.shared.utils;

public class GenericUtils {
    public static <T> T defaultIfNull(T obj, T _default) {
        return obj != null ? obj : _default;
    }
}
