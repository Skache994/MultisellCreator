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
package com.l2skale.multisell.model;

import java.io.File;
import java.io.Serializable;

import javax.swing.ImageIcon;

/*
 * A datapack item. The icon is loaded lazily on the first getIcon() call and then
 * cached, so opening a datapack does not load thousands of images up front.
 *
 * @author Skache
 */
public class Item implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final String NO_IMAGE = "NOIMAGE.png";

	private final int _id;
	private final String _name;
	private final String _type;
	private final boolean _isQuestItem;
	private final String _iconName;
	private final String _iconsFolder;

	private transient ImageIcon _icon;
	private transient boolean _iconLoaded;

	public Item(int id, String name, String type, boolean isQuestItem, String iconName, String iconsFolder)
	{
		_id = id;
		_name = name;
		_type = type;
		_isQuestItem = isQuestItem;
		_iconName = iconName;
		_iconsFolder = iconsFolder;
	}

	public int getId()
	{
		return _id;
	}

	public String getName()
	{
		return _name;
	}

	public String getType()
	{
		return _type;
	}

	public boolean isQuestItem()
	{
		return _isQuestItem;
	}

	// Loads the icon on first use (then caches it). May return null if no image is found.
	public ImageIcon getIcon()
	{
		if (!_iconLoaded)
		{
			_icon = loadIcon();
			_iconLoaded = true;
		}
		return _icon;
	}

	private ImageIcon loadIcon()
	{
		if (_iconsFolder == null)
		{
			return null;
		}

		if ((_iconName != null) && !_iconName.isEmpty())
		{
			final File iconFile = new File(_iconsFolder, _iconName.replace("icon.", "") + ".png");
			if (iconFile.exists())
			{
				return new ImageIcon(iconFile.getAbsolutePath());
			}
		}

		// Fall back to the default "no image" icon.
		final File noImage = new File(_iconsFolder, NO_IMAGE);
		return noImage.exists() ? new ImageIcon(noImage.getAbsolutePath()) : null;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass()))
		{
			return false;
		}
		return _id == ((Item) obj)._id;
	}

	@Override
	public int hashCode()
	{
		return Integer.hashCode(_id);
	}

	@Override
	public String toString()
	{
		return _name + " (ID: " + _id + ") " + (_isQuestItem ? "[Quest Item]" : "");
	}
}
