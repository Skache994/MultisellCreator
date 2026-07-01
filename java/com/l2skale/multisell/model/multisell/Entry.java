package com.l2skale.multisell.model.multisell;

import java.util.ArrayList;
import java.util.List;

/*
 * One multisell entry (a single trade): the ingredients the player gives and
 * the products the player receives.
 *
 * @author Skache
 */
public class Entry
{
	private final List<MultisellItem> _ingredients = new ArrayList<>();
	private final List<MultisellItem> _products = new ArrayList<>();

	public List<MultisellItem> getIngredients()
	{
		return _ingredients;
	}

	public List<MultisellItem> getProducts()
	{
		return _products;
	}

	public void addIngredient(MultisellItem ingredient)
	{
		_ingredients.add(ingredient);
	}

	public void addProduct(MultisellItem product)
	{
		_products.add(product);
	}

	// A deep copy of this entry (with copies of its items).
	public Entry copy()
	{
		final Entry copy = new Entry();
		for (MultisellItem ingredient : _ingredients)
		{
			copy.addIngredient(ingredient.copy());
		}
		for (MultisellItem product : _products)
		{
			copy.addProduct(product.copy());
		}
		return copy;
	}
}
