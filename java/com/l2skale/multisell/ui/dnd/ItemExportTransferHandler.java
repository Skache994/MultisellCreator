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
