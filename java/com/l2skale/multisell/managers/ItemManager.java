package com.l2skale.multisell.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.l2skale.multisell.data.ItemLoader;
import com.l2skale.multisell.model.Item;

/*
 * @author Skache
 */
public class ItemManager
{
	private final ItemLoader _itemLoader;

	private final Map<Integer, Item> items = new HashMap<>();

	// Constructor to initialize the ItemLoader.
	public ItemManager(String itemsFolderPath, String iconsFolderPath)
	{
		this._itemLoader = new ItemLoader(itemsFolderPath, iconsFolderPath);
	}

	// Load items into memory.
	public void loadItems()
	{
		try
		{
			List<Item> loadedItems = _itemLoader.loadItems();
			items.clear();
			for (Item item : loadedItems)
			{
				items.put(item.getId(), item);
			}
		}
		catch (Exception e)
		{
			System.err.println("Error loading items: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Retrieve an item by ID.
	public Item getItemById(int id)
	{
		return items.get(id);
	}

	// Get all items.
	public Map<Integer, Item> getAllItems()
	{
		return items;
	}
}
