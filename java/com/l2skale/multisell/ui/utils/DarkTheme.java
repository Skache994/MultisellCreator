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
package com.l2skale.multisell.ui.utils;

import java.awt.Color;

import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

/*
 * The app always uses the Nimbus look and feel; the theme just swaps colors.
 * Dark applies dark overrides; light removes them (Nimbus' default light colors).
 * Using a single look and feel avoids layout shifts and lost borders when toggling.
 *
 * @author Skache
 */
public class DarkTheme
{
	// The keys the dark theme overrides - removed again for the light theme.
	private static final String[] DARK_KEYS =
	{
		"control",
		"info",
		"nimbusBase",
		"nimbusAlertYellow",
		"nimbusDisabledText",
		"nimbusFocus",
		"nimbusGreen",
		"nimbusInfoBlue",
		"nimbusLightBackground",
		"nimbusOrange",
		"nimbusRed",
		"nimbusSelectedText",
		"nimbusSelectionBackground",
		"text"
	};

	public static void activate()
	{
		// Modify the Nimbus colors to a dark palette.
		UIManager.put("control", new Color(128, 128, 128));
		UIManager.put("info", new Color(128, 128, 128));
		UIManager.put("nimbusBase", Color.DARK_GRAY);
		UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
		UIManager.put("nimbusDisabledText", new Color(180, 180, 180));
		UIManager.put("nimbusFocus", Color.DARK_GRAY);
		UIManager.put("nimbusGreen", new Color(176, 179, 50));
		UIManager.put("nimbusInfoBlue", Color.DARK_GRAY);
		UIManager.put("nimbusLightBackground", Color.DARK_GRAY);
		UIManager.put("nimbusOrange", new Color(191, 98, 4));
		UIManager.put("nimbusRed", new Color(169, 46, 34));
		UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
		UIManager.put("nimbusSelectionBackground", new Color(104, 93, 156));
		UIManager.put("text", new Color(230, 230, 230));

		applyNimbus();
	}

	// Light theme: drop the dark overrides so Nimbus falls back to its default (light) colors.
	public static void deactivate()
	{
		for (String key : DARK_KEYS)
		{
			UIManager.put(key, null);
		}

		applyNimbus();
	}

	// Re-install a fresh Nimbus so the color changes take effect.
	private static void applyNimbus()
	{
		try
		{
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
