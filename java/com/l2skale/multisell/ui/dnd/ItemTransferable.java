package com.l2skale.multisell.ui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import com.l2skale.multisell.model.Item;

/*
 * @author Skache
 */
public class ItemTransferable implements Transferable
{
	public static final DataFlavor ITEM_FLAVOR = new DataFlavor(Item.class, "Item");
	private static final DataFlavor[] SUPPORTED_FLAVORS =
	{ ITEM_FLAVOR };

	private final Item _item;

	public ItemTransferable(Item item)
	{
		_item = item;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		return SUPPORTED_FLAVORS;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return flavor.equals(ITEM_FLAVOR);
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
	{
		if (flavor.equals(ITEM_FLAVOR))
		{
			return _item;
		}
		throw new UnsupportedFlavorException(flavor);
	}
}
