package com.l2skale.multisell.ui.stylers;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JMenuItem;

/*
 * @author Skache
 */
public class ContextMenuStyler
{
	private boolean _isDarkTheme;

	public ContextMenuStyler(boolean isDarkTheme)
	{
		this._isDarkTheme = isDarkTheme;
	}

	// Style a menu item.
	public void styleMenuItem(JMenuItem menuItem)
	{
		// Set background and text colors based on the theme.
		Color menuBgColor = _isDarkTheme ? Color.DARK_GRAY : Color.WHITE;
		Color menuTextColor = _isDarkTheme ? Color.WHITE : Color.BLACK;
		Font menuItemFont = new Font("Arial", Font.PLAIN, 13);

		menuItem.setBackground(menuBgColor);
		menuItem.setForeground(menuTextColor);
		menuItem.setFont(menuItemFont);
	}
}
