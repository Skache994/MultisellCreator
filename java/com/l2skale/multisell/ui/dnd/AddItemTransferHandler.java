package com.l2skale.multisell.ui.dnd;

import java.awt.datatransfer.Transferable;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import com.l2skale.multisell.model.Item;
import com.l2skale.multisell.model.multisell.MultisellItem;

/*
 * Handles the editor lists' drag-and-drop: imports an item dragged from the item
 * list (hands it to onDrop to add), and exports the selected line so it can be
 * dragged out onto the trash bin.
 *
 * @author Skache
 */
public class AddItemTransferHandler extends TransferHandler
{
	private static final long serialVersionUID = 1L;

	private final JList<MultisellItem> _list;
	private final Consumer<Item> _onDrop;

	public AddItemTransferHandler(JList<MultisellItem> list, Consumer<Item> onDrop)
	{
		_list = list;
		_onDrop = onDrop;
	}

	@Override
	public int getSourceActions(JComponent c)
	{
		return MOVE;
	}

	@Override
	protected Transferable createTransferable(JComponent c)
	{
		final MultisellItem selected = _list.getSelectedValue();
		return selected == null ? null : new LocalObjectTransferable(selected);
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
