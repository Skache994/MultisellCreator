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
