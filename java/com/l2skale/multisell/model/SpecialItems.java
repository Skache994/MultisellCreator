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
package com.l2skale.multisell.model;

import java.util.HashMap;
import java.util.Map;

/*
 * The server-side "special" multisell items - virtual currencies with negative ids - PC Cafe points,
 * Clan Reputation, Fame ...
 * @author Skache
 */
public final class SpecialItems
{
	private record Special(String name, String iconValue)
	{
		// ;d
	}

	private static final Map<Integer, Special> DEFS = new HashMap<>();
	private static final Map<Integer, Item> ITEMS = new HashMap<>(); // synthetic Items, cached so icons cache too

	static
	{
		define(-100, "PC Cafe Points", "icon.etc_pccafe_point_i00");
		define(-200, "Clan Reputation", "icon.etc_bloodpledge_point_i00");
		define(-300, "Fame", "icon.pvp_point_i00");
		// TODO add icons for -400 Field Cycle Points, -500 Raidboss Points, -700 Honor Coinsssssssssssssssssssss
	}

	private SpecialItems()
	{
	}

	private static void define(int id, String name, String iconValue)
	{
		DEFS.put(id, new Special(name, iconValue));
	}

	public static boolean isSpecial(int id)
	{
		return DEFS.containsKey(id);
	}

	// The synthetic Item for a special id (same instance each call, so its icon stays cached), or null.
	public static Item get(int id)
	{
		final Special special = DEFS.get(id);
		if (special == null)
		{
			return null;
		}
		return ITEMS.computeIfAbsent(id, key -> new Item(key, special.name(), "Special Item", false, special.iconValue(), null));
	}
}
