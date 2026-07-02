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
