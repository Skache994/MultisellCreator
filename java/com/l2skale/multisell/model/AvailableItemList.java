package com.l2skale.multisell.model;

import java.util.Collection;

import javax.swing.DefaultListModel;

/*
 * @author Skache
 */
public class AvailableItemList
{
	private final DefaultListModel<Item> _availableItemList;

	public AvailableItemList()
	{
		_availableItemList = new DefaultListModel<>();
	}

	public DefaultListModel<Item> getModel()
	{
		return _availableItemList;
	}

	// Add an item to the list.
	public void addItem(Item item)
	{
		_availableItemList.addElement(item);
	}

	// Add many items at once (a single list event - much faster than adding one by one).
	public void addItems(Collection<Item> items)
	{
		_availableItemList.addAll(items);
	}

	// Remove all items from the list.
	public void clear()
	{
		_availableItemList.clear();
	}
}
