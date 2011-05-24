package com.project.canvas.shared;


public class ThrowableUtils 
{
    public static String joinStackTrace(Throwable e) 
    {
        StringBuilder builder = new StringBuilder();
        while (e != null) {
            builder.append("\n" + e.toString());
            StackTraceElement[] trace = e.getStackTrace();
            for (int i = 0; i < trace.length; i++)
                builder.append("\n\tat " + trace[i]);

            e = e.getCause();
            if (e != null)
                builder.append("\nCaused by:\r\n");
        }
        return builder.toString();
    }
}
