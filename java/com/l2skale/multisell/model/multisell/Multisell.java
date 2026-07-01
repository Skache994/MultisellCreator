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
