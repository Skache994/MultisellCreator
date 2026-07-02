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
