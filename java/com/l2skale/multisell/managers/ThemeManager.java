package com.l2skale.multisell.managers;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.l2skale.multisell.ui.utils.DarkTheme;

public class ThemeManager
{
	private static boolean isDarkMode = true; // Start with dark mode as default.

	// Toggle between dark and light themes.
	public static void toggleTheme(JButton themeButton, JFrame frame)
	{
		if (isDarkMode)
		{
			DarkTheme.deactivate();
			isDarkMode = false;
			themeButton.setText("🌙  Dark");
		}
		else
		{
			DarkTheme.activate();
			isDarkMode = true;
			themeButton.setText("☀️  Light");
		}

		SwingUtilities.updateComponentTreeUI(frame);
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
}
