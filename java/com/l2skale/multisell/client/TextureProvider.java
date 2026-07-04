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
package com.l2skale.multisell.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/*
 * Resolves an item's icon value ("package.texture", e.g. "icon.weapon_sword_i00") to an image,
 * by reading the matching .utx package from a chosen textures folder (the client's SysTextures).
 * Packages are opened once and cached; the client files are only read, never modified.
 *
 * @author Skache
 */
public final class TextureProvider
{
	// The bundled default package, shipped inside the jar. Covers the base "Icon" textures so icons
	// work out of the box; pointing at a full client folder adds the rest (BranchIcon, BranchSys...).
	private static final String DEFAULT_RESOURCE = "Icon.utx";
	private static final String DEFAULT_PACKAGE = "Icon";

	private static File _folder;

	// Opened packages by lower-case package name; a null value means "tried, not available".
	private static final Map<String, UtxReader> PACKAGES = new HashMap<>();

	// The bundled default reader, loaded from the classpath on first use (null once tried and absent).
	private static UtxReader _defaultReader;
	private static boolean _defaultTried;

	private TextureProvider()
	{
	}

	// Point at a folder of .utx files (e.g. the client's SysTextures). Clears any cached packages.
	public static synchronized void setFolder(File folder)
	{
		_folder = folder;
		PACKAGES.clear();
	}

	// Icons can always be resolved: from a chosen client folder if set, otherwise the bundled default.
	public static synchronized boolean isReady()
	{
		return (_folder != null) || (defaultReader() != null);
	}

	// The decoded icon for an item's icon value, or null if it cannot be resolved.
	public static synchronized BufferedImage get(String iconValue)
	{
		if (iconValue == null)
		{
			return null;
		}

		// Values are "package.texture" or "package.group.texture": the package is before the first
		// dot, the texture name is after the last dot (the middle group, if any, is not needed).
		final int firstDot = iconValue.indexOf('.');
		final int lastDot = iconValue.lastIndexOf('.');
		if (firstDot <= 0)
		{
			return null;
		}

		final String packageName = iconValue.substring(0, firstDot);
		final String texture = iconValue.substring(lastDot + 1);

		// Prefer a chosen client folder (it may carry extra or newer packages).
		if (_folder != null)
		{
			final BufferedImage image = readSafe(packageFor(packageName), texture);
			if (image != null)
			{
				return image;
			}
		}

		// Fall back to the bundled default, which only holds the base "Icon" package.
		if (packageName.equalsIgnoreCase(DEFAULT_PACKAGE))
		{
			return readSafe(defaultReader(), texture);
		}
		return null;
	}

	// Reads a texture, turning any decode error or a missing reader into null (show no image).
	private static BufferedImage readSafe(UtxReader reader, String texture)
	{
		if (reader == null)
		{
			return null;
		}
		try
		{
			return reader.readTexture(texture);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	// The bundled default package, loaded from the classpath once (cached, including a null miss).
	private static UtxReader defaultReader()
	{
		if (!_defaultTried)
		{
			_defaultTried = true;
			try (InputStream in = TextureProvider.class.getClassLoader().getResourceAsStream(DEFAULT_RESOURCE))
			{
				if (in != null)
				{
					_defaultReader = UtxReader.open(in.readAllBytes(), DEFAULT_RESOURCE);
				}
			}
			catch (Exception e)
			{
				System.err.println("Could not load bundled default textures (" + DEFAULT_RESOURCE + "): " + e.getMessage());
			}
		}
		return _defaultReader;
	}

	private static UtxReader packageFor(String packageName)
	{
		final String key = packageName.toLowerCase();
		if (PACKAGES.containsKey(key))
		{
			return PACKAGES.get(key);
		}

		UtxReader reader = null;
		final File file = findUtx(packageName);
		if (file != null)
		{
			try
			{
				reader = UtxReader.open(file);
			}
			catch (Exception e)
			{
				System.err.println("Could not open " + file.getName() + ": " + e.getMessage());
			}
		}

		PACKAGES.put(key, reader); // cache even null so we do not retry a missing package every time
		return reader;
	}

	// Finds <folder>/<packageName>.utx, case-insensitively (icon vs Icon, branchSys vs BranchSys).
	private static File findUtx(String packageName)
	{
		final File direct = new File(_folder, packageName + ".utx");
		if (direct.isFile())
		{
			return direct;
		}

		final File[] matches = _folder.listFiles((_, name) -> name.equalsIgnoreCase(packageName + ".utx"));
		return ((matches != null) && (matches.length > 0)) ? matches[0] : null;
	}
}
