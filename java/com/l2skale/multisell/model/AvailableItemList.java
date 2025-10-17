package com.l2skale.multisell.model;

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
}
