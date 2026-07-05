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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/*
 * An ordered bag of XML attribute values keyed by name, with the store-as-text rules the multisell
 * format uses: an empty value clears the attribute (so it is simply left off the saved element),
 * a boolean flag is stored only when true (datapacks omit false flags), and order is preserved for
 * a stable save. Shared by the <list> options (Multisell) and the per-line extras (MultisellItem)
 * so the "how an attribute is stored" logic lives in exactly one place.
 *
 * @author Skache
 */
public class AttributeMap
{
	private final Map<String, String> _values = new LinkedHashMap<>();

	// The raw value of an attribute, or null when it is not set.
	public String get(String name)
	{
		return _values.get(name);
	}

	public boolean has(String name)
	{
		return _values.containsKey(name);
	}

	// Set an attribute, or clear it when the value is null/empty. Storing verbatim keeps meaningful
	// zeroes (a production's chance="0") while an empty field just drops the attribute.
	public void set(String name, String value)
	{
		if ((value == null) || value.isEmpty())
		{
			_values.remove(name);
		}
		else
		{
			_values.put(name, value);
		}
	}

	// An attribute read as an int (0 when absent or non-numeric) - e.g. enchantmentLevel.
	public int getInt(String name)
	{
		final String value = _values.get(name);
		if (value == null)
		{
			return 0;
		}
		try
		{
			return Integer.parseInt(value.trim());
		}
		catch (NumberFormatException e)
		{
			return 0;
		}
	}

	// An attribute read as a boolean flag: true only when present and "true" - e.g. applyTaxes.
	public boolean getBoolean(String name)
	{
		return Boolean.parseBoolean(_values.get(name));
	}

	// Store a boolean flag: written as "true" when on, removed when off.
	public void setBoolean(String name, boolean value)
	{
		if (value)
		{
			_values.put(name, "true");
		}
		else
		{
			_values.remove(name);
		}
	}

	public boolean isEmpty()
	{
		return _values.isEmpty();
	}

	// The attributes in order, for writing them back to XML.
	public Set<Map.Entry<String, String>> entries()
	{
		return _values.entrySet();
	}

	// Copy every value from another bag (used when duplicating an item or entry).
	public void copyFrom(AttributeMap other)
	{
		_values.putAll(other._values);
	}
}
