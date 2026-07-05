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
import java.util.LinkedHashSet;
import java.util.List;
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
	// The file name without .xml (e.g. "001"). This IS the multisell's identity - the same string
	// on open, on screen and on save - so the file keeps its exact name. Empty means a new one whose
	// name has not been set yet.
	private String _id;

	// <list> options (applyTaxes, useRate, isChanceMultisell, multipliers, ...) kept by name, exactly
	// as the server reads them into a StatSet, so every attribute the xsd allows has a home.
	private final AttributeMap _listAttributes = new AttributeMap();

	private final Set<Integer> _npcIds = new LinkedHashSet<>();
	private final List<Entry> _entries = new ArrayList<>();

	public Multisell(String id)
	{
		_id = (id == null) ? "" : id;
	}

	public String getId()
	{
		return _id;
	}

	public void setId(String id)
	{
		_id = (id == null) ? "" : id;
	}

	// The <list> options, to read/write by name (see AttributeMap).
	public AttributeMap getListAttributes()
	{
		return _listAttributes;
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
