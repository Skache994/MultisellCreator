package com.l2skale.multisell.ui.dnd;

import java.util.function.Consumer;

import javax.swing.TransferHandler;

import com.l2skale.multisell.model.Item;

/*
 * Accepts an item dragged from the item list and hands it to a callback - e.g.
 * add it as an ingredient or product of the selected entry.
 *
 * @author Skache
 */
public class AddItemTransferHandler extends TransferHandler
{
	private static final long serialVersionUID = 1L;

	private final Consumer<Item> _onDrop;

	public AddItemTransferHandler(Consumer<Item> onDrop)
	{
		_onDrop = onDrop;
	}

	@Override
	public boolean canImport(TransferSupport support)
	{
		return support.isDataFlavorSupported(ItemTransferable.ITEM_FLAVOR);
	}

	@Override
	public boolean importData(TransferSupport support)
	{
		if (!canImport(support))
		{
			return false;
		}

		try
		{
			final Item item = (Item) support.getTransferable().getTransferData(ItemTransferable.ITEM_FLAVOR);
			_onDrop.accept(item);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
