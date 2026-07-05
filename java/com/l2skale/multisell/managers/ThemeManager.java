/*
 * Copyright (c) 2026 Skache
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.l2skale.multisell.managers;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.l2skale.multisell.ui.utils.DarkTheme;
import com.l2skale.multisell.ui.utils.ResourceIcons;

/*
 * @author Skache
 */
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

		SettingsManager.setDarkTheme(isDarkMode);
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

	// Apply the theme saved from last session (defaults to dark on first run).
	public static void applyInitialTheme()
	{
		isDarkMode = SettingsManager.isDarkTheme();
		if (isDarkMode)
		{
			DarkTheme.activate();
		}
		else
		{
			DarkTheme.deactivate();
		}
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
