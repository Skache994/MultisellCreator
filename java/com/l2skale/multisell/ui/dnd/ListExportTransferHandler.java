package com.l2skale.multisell.ui.dnd;

import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

/*
 * Makes a JList a drag source: dragging exports the selected value as a
 * LocalObjectTransferable (e.g. drag an entry onto the trash bin to delete it).
 *
 * @author Skache
 */
public class ListExportTransferHandler extends TransferHandler
{
	private static final long serialVersionUID = 1L;

	private final JList<?> _list;

	public ListExportTransferHandler(JList<?> list)
	{
		_list = list;
	}

	@Override
	public int getSourceActions(JComponent c)
	{
		return MOVE;
	}

	@Override
	protected Transferable createTransferable(JComponent c)
	{
		final Object value = _list.getSelectedValue();
		return value == null ? null : new LocalObjectTransferable(value);
	}
}
