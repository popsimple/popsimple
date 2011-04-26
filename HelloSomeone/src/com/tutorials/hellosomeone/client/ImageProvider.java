package com.tutorials.hellosomeone.client;

import java.util.HashMap;

import com.google.gwt.regexp.shared.RegExp;

public class ImageProvider 
{
	private static String UNKNOWN_IMAGE_URL = "http://www.thefewtheproudtheorange.net/templates/Techos/Images/Category/unknown.png";
	
	HashMap<String, String> images = new HashMap<String, String>();
	
	public ImageProvider()
	{
		this.InitializeImages();
	}
	
	private void InitializeImages()
	{
		this.images.put("robot", "http://t2.gstatic.com/images?q=tbn:ANd9GcTGLUPlK2HPlv7Nv5C7sVY-As6Knrh_SMF3MMkCyr8dikzpUJQPRA");
		this.images.put("fix", "http://t2.gstatic.com/images?q=tbn:ANd9GcTHoJ6QVJjT3uDPZ2r7-6M7pyvhFDsa8qAHNyPMBHBOwb6bNmvBwA");
		this.images.put("bike", "http://t1.gstatic.com/images?q=tbn:ANd9GcQdlKPqOL2eXYhcMMKcNZHdnMuGCSPzJznhYu5AyplJ8BbxFX0DSg");
		this.images.put("bank", "http://www.treasurysoftware.com/images3/bank-reconciliation-account.gif");
		this.images.put("phone", "http://t3.gstatic.com/images?q=tbn:ANd9GcR_0lM7MrNlShcOlz9Vcf8MYeME61vn5AEi6HxJIMNIuwlHjgyN");
	}
	
	public String GetImageUrl(String imageTag)
	{
		for (String key : this.images.keySet())
		{
			//TODO: Use consts for the RegExp flags.
			if (RegExp.compile(key, "i").test(imageTag))
			{
				return this.images.get(key);
			}
		}
		return UNKNOWN_IMAGE_URL;
	}
}
