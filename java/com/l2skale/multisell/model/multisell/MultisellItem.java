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

	// Extra attributes beyond id/count (enchantmentLevel, maintainIngredient, chance, ...), kept by
	// name in file order; an attribute is present only when set (see AttributeMap).
	private final AttributeMap _extras = new AttributeMap();

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

	// The extra attributes, to read/write by name (see AttributeMap).
	public AttributeMap getExtras()
	{
		return _extras;
	}

	// A new item with the same values.
	public MultisellItem copy()
	{
		final MultisellItem copy = new MultisellItem(_itemId, _count);
		copy._extras.copyFrom(_extras);
		return copy;
	}
}
