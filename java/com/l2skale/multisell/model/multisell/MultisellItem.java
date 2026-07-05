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
package com.l2skale.multisell.model.multisell;

import java.util.LinkedHashMap;
import java.util.Map;

/*
 * One line in a multisell entry - used for both an ingredient (what the player
 * gives) and a product (what the player gets). id and count are the required core;
 * every other attribute the pack's xsd allows (enchantmentLevel, maintainIngredient,
 * chance, ...) is kept generically by name, in file order, exactly like the <list>
 * options - so loading then saving never drops one. The item name and icon are
 * looked up from the datapack by itemId when needed.
 *
 * @author Skache
 */
public class MultisellItem
{
	private int _itemId;
	private int _count;

	// Extra attribute values keyed by name (enchantmentLevel, maintainIngredient, chance, ...),
	// in the order they were loaded/set. Values are the raw XML text; an attribute is present only
	// when set, so removing it clears the attribute from the saved line.
	private final Map<String, String> _extras = new LinkedHashMap<>();

	public MultisellItem(int itemId, int count)
	{
		_itemId = itemId;
		_count = count;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public void setItemId(int itemId)
	{
		_itemId = itemId;
	}

	public int getCount()
	{
		return _count;
	}

	public void setCount(int count)
	{
		_count = count;
	}

	// All extra attributes in order, for the saver to write after id and count.
	public Map<String, String> getExtras()
	{
		return _extras;
	}

	// The raw value of an extra attribute, or null when it is not set.
	public String getExtra(String name)
	{
		return _extras.get(name);
	}

	public boolean hasExtra(String name)
	{
		return _extras.containsKey(name);
	}

	// Set (or, with a null/empty value, clear) an extra attribute. Storing verbatim keeps
	// meaningful zeroes (a production's chance="0") while an empty field clears the attribute.
	public void setExtra(String name, String value)
	{
		if ((value == null) || value.isEmpty())
		{
			_extras.remove(name);
		}
		else
		{
			_extras.put(name, value);
		}
	}

	// An extra read as an int (0 when absent or non-numeric) - handy for enchantmentLevel.
	public int getIntExtra(String name)
	{
		final String value = _extras.get(name);
		if (value == null)
		{
			return 0;
		}
		try
		{
			return Integer.parseInt(value.trim());
		}
		catch (NumberFormatException e)
		{
			return 0;
		}
	}

	// An extra read as a boolean flag (true only when present and "true") - e.g. maintainIngredient.
	public boolean getBooleanExtra(String name)
	{
		return Boolean.parseBoolean(_extras.get(name));
	}

	// A new item with the same values.
	public MultisellItem copy()
	{
		final MultisellItem copy = new MultisellItem(_itemId, _count);
		copy._extras.putAll(_extras);
		return copy;
	}
}
