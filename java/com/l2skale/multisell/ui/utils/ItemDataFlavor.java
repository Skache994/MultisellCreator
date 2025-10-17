package com.l2skale.multisell.ui.utils;

import java.awt.datatransfer.DataFlavor;

import com.l2skale.multisell.model.ItemAmount;

/*
 * @author Skache
 */
public class ItemDataFlavor extends DataFlavor
{
	// Static field for ItemAmount flavor
	public static final DataFlavor ITEM_AMOUNT_FLAVOR = new DataFlavor(ItemAmount.class, "ItemAmount");

	public ItemDataFlavor()
	{
		super(ItemAmount.class, "ItemAmount");
	}
}
