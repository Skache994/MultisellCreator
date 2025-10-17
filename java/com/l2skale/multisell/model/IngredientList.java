package com.l2skale.multisell.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import com.l2skale.multisell.enums.ItemType;
import com.l2skale.multisell.ui.utils.Sound;

/*
 * @author Skache
 */
public class IngredientList
{
	private DefaultListModel<ItemAmount> _ingredientList;

	public IngredientList()
	{
		_ingredientList = new DefaultListModel<>();
	}

	public DefaultListModel<ItemAmount> getIngredientsModel()
	{
		return _ingredientList;
	}

	public List<ItemAmount> getIngredients()
	{
		List<ItemAmount> ingredients = new ArrayList<>();
		for (int i = 0; i < _ingredientList.size(); i++)
		{
			ingredients.add(_ingredientList.getElementAt(i));
		}
		return ingredients;
	}

	// Method to add or update an ingredient in the list.
	public boolean addAsIngredient(Item selectedItem, int amount)
	{
		// Check if the item is already in the list.
		for (int i = 0; i < _ingredientList.size(); i++)
		{
			ItemAmount existing = _ingredientList.getElementAt(i);

			if (existing.getItem().equals(selectedItem) && existing.getType() == ItemType.INGREDIENT)
			{
				// Update the existing ingredient with the new amount.
				existing.setAmount(existing.getAmount() + amount);
				_ingredientList.set(i, existing);
				Sound.playSound("click_2.wav");
				// printIngredientList();
				return true;
			}
		}

		// If the ingredient doesn't exist, create a new entry.
		ItemAmount newIngredient = new ItemAmount(selectedItem, amount, ItemType.INGREDIENT);
		_ingredientList.addElement(newIngredient);
		Sound.playSound("item_drop_equip_armor_shield.wav");
		// printIngredientList();
		return true;
	}

	// Method to remove an ingredient or reduce its amount
	public void removeIngredient(ItemAmount ingredient, int amountToRemove)
	{
		if (ingredient.getAmount() > amountToRemove)
		{
			// If the amount to remove is less than the current amount, reduce it
			ingredient.setAmount(ingredient.getAmount() - amountToRemove);
			// After updating the amount, set it back to the list
			_ingredientList.set(_ingredientList.indexOf(ingredient), ingredient);
		}
		else
		{
			// If the amount to remove is equal or greater than the current amount, remove the item entirely
			_ingredientList.removeElement(ingredient);
		}
	}

	@SuppressWarnings("unused")
	private void printIngredientList()
	{
		System.out.println("[DEBUG] Current Ingredients in the list:");
		if (_ingredientList.isEmpty())
		{
			System.out.println("  - (empty)");
			return;
		}
		for (int i = 0; i < _ingredientList.size(); i++)
		{
			ItemAmount ingr = _ingredientList.get(i);
			System.out.println("  - " + ingr.getItem().getName() + " x" + ingr.getAmount());
		}
	}
}
