package com.project.canvas.client.canvastools.TaskList;

import java.util.HashMap;

import com.google.gwt.regexp.shared.RegExp;

public class ImageProvider {
	private static String UNKNOWN_IMAGE_URL = "http://cdn2.iconfinder.com/data/icons/oxygen/32x32/categories/system-help.png";

	HashMap<String, String> images = new HashMap<String, String>();

	public ImageProvider() {
		this.InitializeImages();
	}

	private void InitializeImages() {
		this.images
				.put("robot",
						"http://cdn3.iconfinder.com/data/icons/walle/32/media_player.png");
		this.images
				.put("fix",
						"http://cdn3.iconfinder.com/data/icons/officeicons/PNG/32/Fix.png");
		this.images
				.put("bike",
						"http://cdn4.iconfinder.com/data/icons/mapicons/icons/cycling.png");
		this.images.put("bank",
				"http://cdn1.iconfinder.com/data/icons/stuttgart/32/bank.png");
		this.images
				.put("phone",
						"http://cdn1.iconfinder.com/data/icons/free-business-desktop-icons/32/Telephone.png");
		this.images
				.put("call",
						"http://cdn1.iconfinder.com/data/icons/free-business-desktop-icons/32/Telephone.png");
		this.images
				.put("gay",
						"http://cdn5.iconfinder.com/data/icons/iconshock_avatars/32/gay_32.png");
	}

	public String GetDefaultImageUrl() {
		return UNKNOWN_IMAGE_URL;
	}

	public String GetImageUrl(String imageTag) {
		for (String key : this.images.keySet()) {
			// TODO: Use consts for the RegExp flags.
			if (RegExp.compile(key, "i").test(imageTag)) {
				return this.images.get(key);
			}
		}
		return UNKNOWN_IMAGE_URL;
	}
}
