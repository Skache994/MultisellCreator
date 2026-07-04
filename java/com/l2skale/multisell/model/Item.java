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

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import com.l2skale.multisell.client.TextureProvider;

/*
 * A datapack item. The icon is loaded lazily on the first getIcon() call and then
 * cached, so opening a datapack does not load thousands of images up front.
 *
 * @author Skache
 */
public class Item implements Serializable
{
	private static final long serialVersionUID = 1L;

	// Shown when an item's icon cannot be resolved from the textures. Loaded once from resources.
	private static final ImageIcon NO_IMAGE = loadNoImage();

	private final int _id;
	private final String _name;
	private final String _type;
	private final boolean _isQuestItem;
	private final String _iconName;
	private final String _crystalType;

	// True when the definition came from a datapack custom/ subfolder (server-added content).
	private boolean _isCustom;

	private transient ImageIcon _icon;
	private transient boolean _iconLoaded;
	private transient Map<Integer, ImageIcon> _scaledIcons;

	public Item(int id, String name, String type, boolean isQuestItem, String iconName, String crystalType)
	{
		_id = id;
		_name = name;
		_type = type;
		_isQuestItem = isQuestItem;
		_iconName = iconName;
		_crystalType = crystalType;
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

	// Whether this item is custom (loaded from a stats/items/custom subfolder).
	public boolean isCustom()
	{
		return _isCustom;
	}

	public void setCustom(boolean custom)
	{
		_isCustom = custom;
	}

	// The item grade from crystal_type (D, C, B, A, S, S80, S84), or null when the item has no grade.
	public String getGrade()
	{
		if (_crystalType == null)
		{
			return null;
		}
		final String grade = _crystalType.trim().toUpperCase();
		return (grade.isEmpty() || grade.equals("NONE")) ? null : grade;
	}

	// The icon's texture name - the part after the last dot of the icon value (e.g. the icon value
	// "icon.weapon_sword_i00" gives "weapon_sword_i00"). This is the real asset name in the client,
	// used as the default file name when exporting the icon. Null if the item has no icon value.
	public String getIconTextureName()
	{
		if ((_iconName == null) || _iconName.isEmpty())
		{
			return null;
		}
		final int lastDot = _iconName.lastIndexOf('.');
		return (lastDot >= 0) ? _iconName.substring(lastDot + 1) : _iconName;
	}

	// The decoded icon as a raw image, for exporting to PNG. Null if the item has no real icon
	// (only the placeholder), since that is loaded from a resource, not decoded from the textures.
	public BufferedImage getIconImage()
	{
		final ImageIcon icon = getIcon();
		if ((icon != null) && (icon.getImage() instanceof BufferedImage image))
		{
			return image;
		}
		return null;
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

	// The icon scaled to a square of the given size, computed once per size and cached. Big client
	// icons are shrunk and small ones enlarged, so every caller gets a uniform size. May return null.
	// This is the single place item icons are sized - the list and the editor slots both use it.
	public ImageIcon getScaledIcon(int size)
	{
		if (_scaledIcons == null)
		{
			_scaledIcons = new HashMap<>();
		}

		ImageIcon scaled = _scaledIcons.get(size);
		if (scaled == null)
		{
			final ImageIcon icon = getIcon();
			if (icon == null)
			{
				return null;
			}
			scaled = new ImageIcon(icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
			_scaledIcons.put(size, scaled);
		}
		return scaled;
	}

	// Drop the cached icons so they are reloaded (e.g. after choosing a textures folder).
	public void resetIcon()
	{
		_icon = null;
		_iconLoaded = false;
		if (_scaledIcons != null)
		{
			_scaledIcons.clear();
		}
	}

	private ImageIcon loadIcon()
	{
		// Icons come from the client's .utx textures. When one cannot be resolved (no textures
		// folder yet, or the package/texture is missing) we show the "no image" placeholder.
		if (TextureProvider.isReady())
		{
			final BufferedImage image = TextureProvider.get(_iconName);
			if (image != null)
			{
				return new ImageIcon(image);
			}
		}
		return NO_IMAGE;
	}

	private static ImageIcon loadNoImage()
	{
		final URL url = Item.class.getClassLoader().getResource("images/NOIMAGE.png");
		return url != null ? new ImageIcon(url) : null;
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
