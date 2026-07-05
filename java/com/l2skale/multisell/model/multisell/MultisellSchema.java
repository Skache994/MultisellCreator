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

import java.util.List;

/*
 * The multisell rules for a specific server, read from its own data/xsd/multisell.xsd.
 * It lists exactly which attributes are legal on the <list>, on an <ingredient>, and on
 * a <production> - so the app can drive its UI (what to show, what to make editable) and
 * its output from the server's own truth instead of guessing per chronicle.
 *
 * @author Skache
 */
public class MultisellSchema
{
	private final List<SchemaAttribute> _listAttributes;
	private final List<SchemaAttribute> _ingredientAttributes;
	private final List<SchemaAttribute> _productionAttributes;

	public MultisellSchema(List<SchemaAttribute> listAttributes, List<SchemaAttribute> ingredientAttributes, List<SchemaAttribute> productionAttributes)
	{
		_listAttributes = List.copyOf(listAttributes);
		_ingredientAttributes = List.copyOf(ingredientAttributes);
		_productionAttributes = List.copyOf(productionAttributes);
	}

	// Attributes allowed on the root <list> (e.g. applyTaxes, useRate, or the modern multipliers).
	public List<SchemaAttribute> getListAttributes()
	{
		return _listAttributes;
	}

	// Attributes allowed on an <ingredient> (e.g. id, count, maintainIngredient).
	public List<SchemaAttribute> getIngredientAttributes()
	{
		return _ingredientAttributes;
	}

	// Attributes allowed on a <production> (e.g. id, count, and modern chance).
	public List<SchemaAttribute> getProductionAttributes()
	{
		return _productionAttributes;
	}

	public boolean allowsListAttribute(String name)
	{
		return contains(_listAttributes, name);
	}

	public boolean allowsIngredientAttribute(String name)
	{
		return contains(_ingredientAttributes, name);
	}

	public boolean allowsProductionAttribute(String name)
	{
		return contains(_productionAttributes, name);
	}

	private static boolean contains(List<SchemaAttribute> attributes, String name)
	{
		for (SchemaAttribute attribute : attributes)
		{
			if (attribute.getName().equals(name))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString()
	{
		return "MultisellSchema[list=" + _listAttributes + ", ingredient=" + _ingredientAttributes + ", production=" + _productionAttributes + "]";
	}
}
