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
package com.l2skale.multisell.ui.dnd;

import java.awt.datatransfer.Transferable;
import java.util.function.BiConsumer;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.TransferHandler;

/*
 * Makes a JList a drag source: dragging exports the selected value as a
 * LocalObjectTransferable (e.g. drag an entry onto the trash bin to delete it).
 * Dropping a row back onto the same list reorders it (onReorder is called with
 * the source and target indices).
 *
 * @author Skache
 */
public class ListExportTransferHandler extends TransferHandler
{
	private static final long serialVersionUID = 1L;

	private final JList<?> _list;
	private final BiConsumer<Integer, Integer> _onReorder;

	public ListExportTransferHandler(JList<?> list, BiConsumer<Integer, Integer> onReorder)
	{
		_list = list;
		_onReorder = onReorder;
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

	@Override
	public boolean canImport(TransferSupport support)
	{
		return isReorder(support);
	}

	@Override
	public boolean importData(TransferSupport support)
	{
		if (!isReorder(support))
		{
			return false;
		}

		try
		{
			final Object value = support.getTransferable().getTransferData(LocalObjectTransferable.FLAVOR);
			final int from = indexOf(value);
			if (from < 0)
			{
				return false;
			}

			// The drop index is an insertion gap; convert it to a final position so it means
			// the same as the menu's target index. Dropping below the row shifts it by one.
			final int dropIndex = ((JList.DropLocation) support.getDropLocation()).getIndex();
			final int to = (dropIndex > from) ? (dropIndex - 1) : dropIndex;
			_onReorder.accept(from, to);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	// True when the drop is one of this list's own rows being dragged back onto itself.
	private boolean isReorder(TransferSupport support)
	{
		return (_onReorder != null) && support.isDrop() && (support.getComponent() == _list) && support.isDataFlavorSupported(LocalObjectTransferable.FLAVOR);
	}

	private int indexOf(Object value)
	{
		final ListModel<?> model = _list.getModel();
		for (int i = 0; i < model.getSize(); i++)
		{
			if (model.getElementAt(i) == value)
			{
				return i;
			}
		}
		return -1;
	}
}
