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
 * gives) and a product (what the player gets). Holds only what the XML holds;
 * the item name and icon are looked up from the datapack by itemId when needed.
 *
 * @author Skache
 */
public class MultisellItem
{
	private int _itemId;
	private int _count;
	private int _enchantmentLevel;
	private boolean _maintainIngredient; // Ingredients only.

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

	public int getEnchantmentLevel()
	{
		return _enchantmentLevel;
	}

	public void setEnchantmentLevel(int enchantmentLevel)
	{
		_enchantmentLevel = enchantmentLevel;
	}

	public boolean isMaintainIngredient()
	{
		return _maintainIngredient;
	}

	public void setMaintainIngredient(boolean maintainIngredient)
	{
		_maintainIngredient = maintainIngredient;
	}

	// A new item with the same values.
	public MultisellItem copy()
	{
		final MultisellItem copy = new MultisellItem(_itemId, _count);
		copy.setEnchantmentLevel(_enchantmentLevel);
		copy.setMaintainIngredient(_maintainIngredient);
		return copy;
	}
}
