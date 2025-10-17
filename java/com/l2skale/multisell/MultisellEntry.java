package com.l2skale.multisell;

import java.util.List;

import com.l2skale.multisell.model.ItemAmount;

/*
 * @author Skache
 */
public class MultisellEntry
{
	private final List<ItemAmount> ingredients;
	private final List<ItemAmount> productions;

	public MultisellEntry(List<ItemAmount> ingredients, List<ItemAmount> productions)
	{
		this.ingredients = ingredients;
		this.productions = productions;

		System.out.println("[DEBUG] MultisellEntry created.");
		System.out.println("[DEBUG] Ingredients:");
		for (ItemAmount ingredient : ingredients)
		{
			System.out.println("   > " + ingredient + " (INGREDIENT)");
		}
		System.out.println("[DEBUG] Final Products:");
		for (ItemAmount product : productions)
		{
			System.out.println("   > " + product + " (FINAL PRODUCT)");
		}
	}

	// Renamed for clarity
	public List<ItemAmount> getIngredients()
	{
		return ingredients;
	}

	public List<ItemAmount> getFinalProducts()
	{
		System.out.println("[DEBUG] getFinalProducts called. Returning " + productions.size() + " final products.");
		return productions;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder("MultisellEntry:\n");
		sb.append("  Ingredients:\n");
		for (ItemAmount ing : ingredients)
		{
			sb.append("    - ").append(ing).append("\n");
		}
		sb.append("  Final Products:\n");
		for (ItemAmount prod : productions)
		{
			sb.append("    - ").append(prod).append("\n");
		}
		return sb.toString();
	}
}
