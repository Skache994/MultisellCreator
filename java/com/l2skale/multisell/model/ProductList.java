package com.l2skale.multisell.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import com.l2skale.multisell.enums.ItemType;
import com.l2skale.multisell.ui.utils.Sound;

/*
 * @author Skache
 */
public class ProductList
{
	private DefaultListModel<ItemAmount> _productList;

	public ProductList()
	{
		_productList = new DefaultListModel<>();
	}

	public DefaultListModel<ItemAmount> getProductsModel()
	{
		return _productList;
	}

	public List<ItemAmount> getProducts()
	{
		List<ItemAmount> ingredients = new ArrayList<>();
		for (int i = 0; i < _productList.size(); i++)
		{
			ingredients.add(_productList.getElementAt(i));
		}
		return ingredients;
	}

	// Method to add or update a product in the list.
	public boolean addAsProduct(Item selectedItem, int amount)
	{
		// Check if the item is already in the list.
		for (int i = 0; i < _productList.size(); i++)
		{
			ItemAmount existingProduct = _productList.getElementAt(i);
			if (existingProduct.getItem().equals(selectedItem) && existingProduct.getType() == ItemType.PRODUCT)
			{
				// Update the existing product with the new amount.
				existingProduct.setAmount(existingProduct.getAmount() + amount);
				_productList.set(i, existingProduct);
				Sound.playSound("click_2.wav");
				// printProductList();
				return true;
			}
		}

		// If the product doesn't exist, create a new entry.
		ItemAmount newProduct = new ItemAmount(selectedItem, amount, ItemType.PRODUCT);
		_productList.addElement(newProduct);
		// printProductList();
		Sound.playSound("item_drop_equip_armor_shield.wav");
		return true;
	}

	// Method to remove or reduce the amount of a product
	public void removeProduct(ItemAmount product, int amountToRemove)
	{
		if (product.getAmount() > amountToRemove)
		{
			// If the amount to remove is less than the current amount, reduce it
			product.setAmount(product.getAmount() - amountToRemove);
			// After updating the amount, set it back to the list
			_productList.set(_productList.indexOf(product), product);
		}
		else
		{
			// If the amount to remove is equal or greater than the current amount, remove the product entirely
			_productList.removeElement(product);
		}
	}

	@SuppressWarnings("unused")
	private void printProductList()
	{
		System.out.println("[DEBUG] Current Product in the list:");
		if (_productList.isEmpty())
		{
			System.out.println("  - (empty)");
			return;
		}
		for (int i = 0; i < _productList.size(); i++)
		{
			ItemAmount ingr = _productList.get(i);
			System.out.println("  - " + ingr.getItem().getName() + " x" + ingr.getAmount());
		}
	}
}
