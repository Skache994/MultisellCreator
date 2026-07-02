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
