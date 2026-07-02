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
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.TransferHandler;

import com.l2skale.multisell.model.Item;
import com.l2skale.multisell.model.multisell.MultisellItem;

/*
 * Handles the editor lists' drag-and-drop: imports an item dragged from the item
 * list (hands it to onDrop to add), exports the selected line so it can be dragged
 * out onto the trash bin, and reorders a row when it is dragged within its own list
 * (onReorder is called with the source and target indices).
 *
 * @author Skache
 */
public class AddItemTransferHandler extends TransferHandler
{
	private static final long serialVersionUID = 1L;

	private final JList<MultisellItem> _list;
	private final Consumer<Item> _onDrop;
	private final BiConsumer<Integer, Integer> _onReorder;

	public AddItemTransferHandler(JList<MultisellItem> list, Consumer<Item> onDrop, BiConsumer<Integer, Integer> onReorder)
	{
		_list = list;
		_onDrop = onDrop;
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
		final MultisellItem selected = _list.getSelectedValue();
		return selected == null ? null : new LocalObjectTransferable(selected);
	}

	@Override
	public boolean canImport(TransferSupport support)
	{
		// An item dragged from the Available Items list - add it.
		if (support.isDataFlavorSupported(ItemTransferable.ITEM_FLAVOR))
		{
			return true;
		}

		// One of this list's own rows dragged within the list - reorder it.
		return isReorder(support);
	}

	@Override
	public boolean importData(TransferSupport support)
	{
		if (support.isDataFlavorSupported(ItemTransferable.ITEM_FLAVOR))
		{
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

		if (isReorder(support))
		{
			try
			{
				final Object value = support.getTransferable().getTransferData(LocalObjectTransferable.FLAVOR);
				final int from = indexOf(value);
				if (from < 0)
				{
					return false; // Dragged from a different list - not a reorder here.
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

		return false;
	}

	// True when the drop is one of this list's own rows being dragged back onto itself.
	private boolean isReorder(TransferSupport support)
	{
		return (_onReorder != null) && support.isDrop() && (support.getComponent() == _list) && support.isDataFlavorSupported(LocalObjectTransferable.FLAVOR);
	}

	private int indexOf(Object value)
	{
		final ListModel<MultisellItem> model = _list.getModel();
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
