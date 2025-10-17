package com.l2skale.multisell.model;

import java.util.List;

import javax.swing.DefaultListModel;

import com.l2skale.multisell.MultisellEntry;

public class MultisellList
{
	private DefaultListModel<MultisellEntry> _multisellList;

	public MultisellList()
	{
		_multisellList = new DefaultListModel<>();
		System.out.println("[DEBUG] MultisellList initialized. Ready to store entries.");
	}

	// Getter for the multisellEntriesModel
	public DefaultListModel<MultisellEntry> getMultisellEntriesModel()
	{
		System.out.println("[DEBUG] getMultisellEntriesModel called. Current size: " + _multisellList.size());
		return _multisellList;
	}

	// Method to add ingredients and products as a final product to the list
	public void addAsFinalProduct(List<ItemAmount> ingredients, List<ItemAmount> products)
	{
		System.out.println("[DEBUG] addAsFinalProduct called.");
		System.out.println("[DEBUG] Ingredients count: " + ingredients.size());
		for (ItemAmount ing : ingredients)
		{
			System.out.println("   > Ingredient: " + ing);
		}

		System.out.println("[DEBUG] Products count: " + products.size());
		for (ItemAmount prod : products)
		{
			System.out.println("   > Product: " + prod);
		}

		// Create a new MultisellEntry with ingredients and products
		MultisellEntry newEntry = new MultisellEntry(ingredients, products);
		System.out.println("[DEBUG] New MultisellEntry created.");

		// Add the new entry to the multisell list
		_multisellList.addElement(newEntry);
		System.out.println("[DEBUG] MultisellEntry added to the list. Total entries now: " + _multisellList.size());
	}
}
