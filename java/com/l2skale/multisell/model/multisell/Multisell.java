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
 * A whole multisell list: its id (the file name number), its options, the NPCs
 * allowed to open it, and its entries. Mirrors the server's ListContainer.
 *
 * @author Skache
 */
public class Multisell
{
	private int _id;
	private boolean _applyTaxes;
	private boolean _maintainEnchantment;
	private String _useRate; // Kept as text: either a number or a config field name.

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

	public boolean isApplyTaxes()
	{
		return _applyTaxes;
	}

	public void setApplyTaxes(boolean applyTaxes)
	{
		_applyTaxes = applyTaxes;
	}

	public boolean isMaintainEnchantment()
	{
		return _maintainEnchantment;
	}

	public void setMaintainEnchantment(boolean maintainEnchantment)
	{
		_maintainEnchantment = maintainEnchantment;
	}

	public String getUseRate()
	{
		return _useRate;
	}

	public void setUseRate(String useRate)
	{
		_useRate = useRate;
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
