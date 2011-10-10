package com.project.shared.utils;

import java.util.Random;

public class RandomUtils
{
    /**
     * Returns a non-binary (printable) pseudo-random string of the given length.
     * GWT clients don't have the UUID class, so this is almost as good.
     */
    public static String randomString(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0 ; i < length; i++) {
            stringBuilder.append(String.valueOf(random.nextInt(10)));
        }
        return stringBuilder.toString();
    }
}
