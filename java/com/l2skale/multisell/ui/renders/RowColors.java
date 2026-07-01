package com.l2skale.multisell.ui.renders;

import java.awt.Color;

import com.l2skale.multisell.managers.ThemeManager;

/*
 * The item list's selection/highlight colours, shared so every list (items,
 * ingredients, products, entries) highlights the same nice way. Single source
 * of truth - taken from the original ItemListRenderer.
 *
 * @author Skache
 */
public final class RowColors
{
	private RowColors()
	{
	}

	// Row background: purple/light-blue when selected, else the theme's list background.
	public static Color background(boolean selected)
	{
		final boolean dark = ThemeManager.isDarkMode();
		if (selected)
		{
			return dark ? new Color(104, 93, 156) : new Color(204, 204, 255);
		}
		return dark ? new Color(50, 50, 50) : Color.WHITE;
	}

	public static Color foreground()
	{
		return ThemeManager.isDarkMode() ? Color.WHITE : Color.BLACK;
	}
}
