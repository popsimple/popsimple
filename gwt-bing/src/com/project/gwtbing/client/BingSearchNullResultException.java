package com.project.gwtbing.client;

public class BingSearchNullResultException extends Exception 
{
    private static final long serialVersionUID = 1L;
    
    public BingSearchNullResultException()
    {
        super();
    }
    
    public BingSearchNullResultException(String message)
    {
        super(message);
    }

}
