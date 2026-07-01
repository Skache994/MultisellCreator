package com.l2skale.multisell.managers;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.l2skale.multisell.ui.utils.DarkTheme;
import com.l2skale.multisell.ui.utils.ResourceIcons;

public class ThemeManager
{
	private static boolean isDarkMode = true; // Start with dark mode as default.

	// Sun is shown while dark is active (click to go light); moon while light is active.
	private static final ImageIcon SUN_ICON = loadScaled("map_Sun.png");
	private static final ImageIcon MOON_ICON = loadScaled("map_Moon.png");

	// Toggle between dark and light themes.
	public static void toggleTheme(JButton themeButton, JFrame frame)
	{
		if (isDarkMode)
		{
			DarkTheme.deactivate();
			isDarkMode = false;
		}
		else
		{
			DarkTheme.activate();
			isDarkMode = true;
		}

		updateThemeButton(themeButton);
		SwingUtilities.updateComponentTreeUI(frame);
	}

	// Set the theme button's icon to match the current theme.
	public static void updateThemeButton(JButton button)
	{
		if (isDarkMode)
		{
			button.setIcon(SUN_ICON);
			button.setToolTipText("Switch to light theme");
		}
		else
		{
			button.setIcon(MOON_ICON);
			button.setToolTipText("Switch to dark theme");
		}
	}

	// Get current theme as boolean (true = dark, false = light).
	public static boolean isDarkMode()
	{
		return isDarkMode;
	}

	public static boolean getCurrentTheme()
	{
		return isDarkMode; // Returns true for Dark, false for Light.
	}

	// Apply the initial theme (default is dark mode).
	public static void applyInitialTheme()
	{
		DarkTheme.activate(); // Always apply dark theme first.
	}

	private static ImageIcon loadScaled(String name)
	{
		final ImageIcon icon = ResourceIcons.loadResourceIconsIcon(name);
		if (icon == null)
		{
			return null;
		}

		final Image scaled = icon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
	}
}
