package com.l2skale.multisell.ui.dnd;

import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import com.l2skale.multisell.model.Item;

/*
 * @author Skache
 */
public class ItemExportTransferHandler extends TransferHandler
{
	private static final long serialVersionUID = 1L;

	@Override
	public int getSourceActions(JComponent c)
	{
		return COPY; // We want to copy the item when dragging
	}

	@Override
	protected Transferable createTransferable(JComponent c)
	{
		@SuppressWarnings("unchecked")
		JList<Item> list = (JList<Item>) c;
		Item selectedItem = list.getSelectedValue();
		if (selectedItem != null)
		{
			return new ItemTransferable(selectedItem); // Create a transferable object with the selected item
		}
		return null;
	}

	@Override
	public boolean canImport(TransferSupport support)
	{
		return true; // Indicate that the drop operation is allowed
	}
}
