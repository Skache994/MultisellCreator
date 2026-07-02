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
