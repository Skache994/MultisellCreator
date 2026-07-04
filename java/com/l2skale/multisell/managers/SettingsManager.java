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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/*
 * Stores user settings in a properties file under the user's home folder.
 *
 * @author Skache
 */
public class SettingsManager
{
	private static final File DIR = resolveDir();
	private static final File FILE = new File(DIR, "settings.properties");

	// On Windows use %APPDATA%\MultisellCreator; elsewhere fall back to ~/.multisellcreator.
	private static File resolveDir()
	{
		final String appData = System.getenv("APPDATA");
		if ((appData != null) && !appData.isBlank())
		{
			return new File(appData, "MultisellCreator");
		}

		return new File(System.getProperty("user.home"), ".multisellcreator");
	}

	private static final String KEY_DATAPACK_PATH = "lastDatapackPath";
	private static final String KEY_MULTISELL_PATH = "lastMultisellPath";
	private static final String KEY_TEXTURES_PATH = "lastTexturesPath";
	private static final String KEY_EXPORT_PATH = "lastExportPath";
	private static final String KEY_THEME = "theme";
	private static final String KEY_WINDOW_WIDTH = "windowWidth";
	private static final String KEY_WINDOW_HEIGHT = "windowHeight";

	private static final Properties PROPS = new Properties();

	// Load the settings file once at startup. A missing file is normal on first run.
	public static void load()
	{
		if (!FILE.exists())
		{
			return;
		}

		try (FileInputStream in = new FileInputStream(FILE))
		{
			PROPS.load(in);
		}
		catch (IOException e)
		{
			System.err.println("Could not read settings: " + e.getMessage());
		}
	}

	// Write the current settings back to disk, creating the folder if needed.
	private static void save()
	{
		if (!DIR.exists() && !DIR.mkdirs())
		{
			System.err.println("Could not create settings folder: " + DIR);
			return;
		}

		try (FileOutputStream out = new FileOutputStream(FILE))
		{
			PROPS.store(out, "MultisellCreator settings");
		}
		catch (IOException e)
		{
			System.err.println("Could not write settings: " + e.getMessage());
		}
	}

	// Last opened datapack folder (null if never set).
	public static String getLastDatapackPath()
	{
		return PROPS.getProperty(KEY_DATAPACK_PATH);
	}

	public static void setLastDatapackPath(String path)
	{
		PROPS.setProperty(KEY_DATAPACK_PATH, path);
		save();
	}

	// Last folder a multisell was opened from (null if never set).
	public static String getLastMultisellPath()
	{
		return PROPS.getProperty(KEY_MULTISELL_PATH);
	}

	public static void setLastMultisellPath(String path)
	{
		PROPS.setProperty(KEY_MULTISELL_PATH, path);
		save();
	}

	// Last chosen client textures folder (SysTextures), or null if never set.
	public static String getLastTexturesPath()
	{
		return PROPS.getProperty(KEY_TEXTURES_PATH);
	}

	public static void setLastTexturesPath(String path)
	{
		PROPS.setProperty(KEY_TEXTURES_PATH, path);
		save();
	}

	// Last folder an icon was exported to, or null if never set.
	public static String getLastExportPath()
	{
		return PROPS.getProperty(KEY_EXPORT_PATH);
	}

	public static void setLastExportPath(String path)
	{
		PROPS.setProperty(KEY_EXPORT_PATH, path);
		save();
	}

	// Last used theme (true = dark). Defaults to dark when unset.
	public static boolean isDarkTheme()
	{
		return Boolean.parseBoolean(PROPS.getProperty(KEY_THEME, "true"));
	}

	public static void setDarkTheme(boolean dark)
	{
		PROPS.setProperty(KEY_THEME, Boolean.toString(dark));
		save();
	}

	// Last window size (returns fallback when unset or unreadable).
	public static int getWindowWidth(int fallback)
	{
		return parseInt(PROPS.getProperty(KEY_WINDOW_WIDTH), fallback);
	}

	public static int getWindowHeight(int fallback)
	{
		return parseInt(PROPS.getProperty(KEY_WINDOW_HEIGHT), fallback);
	}

	public static void setWindowSize(int width, int height)
	{
		PROPS.setProperty(KEY_WINDOW_WIDTH, Integer.toString(width));
		PROPS.setProperty(KEY_WINDOW_HEIGHT, Integer.toString(height));
		save();
	}

	private static int parseInt(String value, int fallback)
	{
		if (value == null)
		{
			return fallback;
		}

		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			return fallback;
		}
	}
}
