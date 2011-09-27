package com.project.shared.client.utils;

public class MathUtils
{
    public static final int CIRCLE_DEGREES = 360;

    public static double IEEEremainder(double dividend, double divisor)
    {
        return dividend - (divisor * Math.round(dividend / divisor));
    }

    public static double normalAbsoluteDegrees(double angle) {
        return (angle %= CIRCLE_DEGREES) >= 0 ? angle : (angle + CIRCLE_DEGREES);
    }
}
