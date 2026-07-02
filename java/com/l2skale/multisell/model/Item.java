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
