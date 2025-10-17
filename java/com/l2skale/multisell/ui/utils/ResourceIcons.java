package com.l2skale.multisell.ui.utils;

import java.net.URL;

import javax.swing.ImageIcon;

/*
 * @author Skache
 */
public class ResourceIcons
{
	// Path prefix for all image icons.
	private static final String ICONS_DIR = "images/";

	/**
	 * Loads an image icon from the classpath.
	 * 
	 * @param fileName Name of the image file (e.g., "MSC_64x64.png")
	 * @return ImageIcon if found, otherwise null
	 */
	public static ImageIcon loadResourceIconsIcon(String fileName)
	{
		String path = ICONS_DIR + fileName;

		URL imageUrl = ResourceIcons.class.getClassLoader().getResource(path);

		if (imageUrl == null)
		{
			return null;
		}

		return new ImageIcon(imageUrl);
	}
}