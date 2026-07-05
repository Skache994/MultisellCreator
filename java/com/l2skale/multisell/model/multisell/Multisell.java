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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * A whole multisell list: its id (the file name number), its <list> options, the
 * NPCs allowed to open it, and its entries. Mirrors the server's ListContainer.
 *
 * The <list> options (applyTaxes, useRate, isChanceMultisell, the multipliers, ...)
 * are kept generically by name, exactly as the server itself reads them into a
 * StatSet. This way every attribute the pack's xsd allows has a home, and loading
 * then saving never drops one we do not have a hardcoded field for.
 *
 * @author Skache
 */
public class Multisell
{
	private int _id;

	// <list> attribute values, keyed by name, in the order they were added/loaded. Values are the
	// raw XML text (e.g. "true", "1.5"); booleans are stored only when true, matching the datapack.
	private final Map<String, String> _listAttributes = new LinkedHashMap<>();

	private final Set<Integer> _npcIds = new LinkedHashSet<>();
	private final List<Entry> _entries = new ArrayList<>();

	public Multisell(int id)
	{
		_id = id;
	}

	public int getId()
	{
		return _id;
	}

	public void setId(int id)
	{
		_id = id;
	}

	// All <list> attributes in order, for the saver to write back verbatim.
	public Map<String, String> getListAttributes()
	{
		return _listAttributes;
	}

	// The raw value of a <list> attribute, or null when it is not set.
	public String getListAttribute(String name)
	{
		return _listAttributes.get(name);
	}

	// Set (or, with a null/empty value, clear) a <list> attribute.
	public void setListAttribute(String name, String value)
	{
		if ((value == null) || value.isEmpty())
		{
			_listAttributes.remove(name);
		}
		else
		{
			_listAttributes.put(name, value);
		}
	}

	// A <list> boolean flag (applyTaxes, isChanceMultisell, ...): true only when present and "true".
	public boolean getListBoolean(String name)
	{
		return Boolean.parseBoolean(_listAttributes.get(name));
	}

	// Store a boolean flag: written as "true" when on, removed when off (datapacks omit false flags).
	public void setListBoolean(String name, boolean value)
	{
		if (value)
		{
			_listAttributes.put(name, "true");
		}
		else
		{
			_listAttributes.remove(name);
		}
	}

	public Set<Integer> getNpcIds()
	{
		return _npcIds;
	}

	public List<Entry> getEntries()
	{
		return _entries;
	}
}
