package com.l2skale.multisell.model;

import java.io.Serializable;

import javax.swing.ImageIcon;

/*
 * @author Skache
 */
public class Item implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final int _id;
	private final String _name;
	private final String _type;
	private final ImageIcon _icon;
	private final boolean _isQuestItem;

	public Item(int id, String name, String type, ImageIcon icon, boolean isQuestItem)
	{
		this._id = id;
		this._name = name;
		this._type = type;
		this._icon = icon;
		this._isQuestItem = isQuestItem;
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

	public ImageIcon getIcon()
	{
		return _icon;
	}

	public boolean isQuestItem()
	{
		return _isQuestItem;
	}

	@Override
	public boolean equals(Object obj)
	{
		// If the two objects are the same (this is a simple check to avoid extra work)
		if (this == obj)
		{
			return true;
		}

		// If the other object is not an Item or is null, they can't be equal
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		// Compare by id. If they have the same id, they are considered the same item
		Item item = (Item) obj;
		{
			return _id == item._id;
		}
	}

	@Override
	public int hashCode()
	{
		return Integer.hashCode(_id); // Just use the ID to create the hash
	}

	@Override
	public String toString()
	{
		return _name + " (ID: " + _id + ") " + (_isQuestItem ? "[Quest Item]" : "");
	}
}