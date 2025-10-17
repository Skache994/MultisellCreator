package com.l2skale.multisell.model;

import com.l2skale.multisell.enums.ItemType;

/*
 * @author Skache
 */
public class ItemAmount
{
	private Item _item; // The item that this object is representing (could be an ingredient, product, etc.)
	private int _amount; // The amount of the item.
	private ItemType _type; // Enum to represent ingredient, product, or final product.

	/*
	 * Constructor to create a new ItemAmount object with a specific item, amount, and type.
	 * 
	 * @param item The item this object represents.
	 * 
	 * @param amount The quantity of the item.
	 * 
	 * @param type The type of item (e.g., INGREDIENT, PRODUCT, FINAL_PRODUCT).
	 */
	public ItemAmount(Item item, int amount, ItemType type)
	{
		this._item = item;
		this._amount = amount;
		this._type = type;
	}

	/*
	 * Getter for the item.
	 * 
	 * @return The item associated with this ItemAmount object.
	 */
	public Item getItem()
	{
		return _item;
	}

	/*
	 * Getter for the amount of the item.
	 * 
	 * @return The amount of the item.
	 */
	public int getAmount()
	{
		return _amount;
	}

	/*
	 * Setter for the amount of the item.
	 * 
	 * @param amount The new amount to set for the item.
	 */
	public void setAmount(int amount)
	{
		this._amount = amount;
	}

	/*
	 * Getter for the type of the item (INGREDIENT, PRODUCT, FINAL_PRODUCT).
	 * 
	 * @return The type of item (INGREDIENT, PRODUCT, or FINAL_PRODUCT).
	 */
	public ItemType getType()
	{
		return _type;
	}

	/*
	 * Setter for the type of the item.
	 * 
	 * @param type The new type to set for the item (e.g., INGREDIENT, PRODUCT, FINAL_PRODUCT).
	 */
	public void setType(ItemType type)
	{
		this._type = type;
	}

	/*
	 * Overridden toString method to provide a string representation of the ItemAmount object. This method returns a readable format of the item with its amount and type.
	 * 
	 * @return A string representation of the item with its amount and type.
	 */
	@Override
	public String toString()
	{
		return _item.getName() + " (x" + _amount + ") (" + _type + ")";
	}
}
