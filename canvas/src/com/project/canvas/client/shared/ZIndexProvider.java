package com.project.canvas.client.shared;

public abstract class ZIndexProvider 
{
	private static int nextZIndex = 1;
	
	public static int allocateZIndex()
	{
		int allocatedZIndex = nextZIndex;
		nextZIndex += 1;
		return allocatedZIndex;
	}
	
	public static int getTopMostZIndex()
	{
		return nextZIndex;
	}

}
