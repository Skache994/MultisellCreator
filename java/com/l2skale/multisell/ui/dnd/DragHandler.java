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

/*
 * The single drag-and-drop handler for every list.
 *
 * @author Skache
 */
public class DragHandler extends TransferHandler
{
	private static final long serialVersionUID = 1L;

	// A drop zone's rule: given what was dropped and the target row it landed on, return the action
	// to run, or null to refuse the drop.
	@FunctionalInterface
	public interface DropRule
	{
		Runnable resolve(DragPayload payload, int targetIndex);
	}

	private final JList<?> _list;
	private final DropRule _onDrop; // null = drag source only

	public DragHandler(JList<?> list, DropRule onDrop)
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
		final int index = _list.getSelectedIndex();
		return (index < 0) ? null : new DragPayload(_list.getSelectedValue(), _list, index);
	}

	@Override
	public boolean canImport(TransferSupport support)
	{
		return resolve(support) != null;
	}

	@Override
	public boolean importData(TransferSupport support)
	{
		final Runnable action = resolve(support);
		if (action == null)
		{
			return false;
		}

		action.run();
		return true;
	}

	private Runnable resolve(TransferSupport support)
	{
		if ((_onDrop == null) || !support.isDrop() || !support.isDataFlavorSupported(DragPayload.FLAVOR))
		{
			return null;
		}

		try
		{
			final DragPayload payload = (DragPayload) support.getTransferable().getTransferData(DragPayload.FLAVOR);
			return _onDrop.resolve(payload, targetIndex(support, payload));
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	private int targetIndex(TransferSupport support, DragPayload payload)
	{
		final int gap = ((JList.DropLocation) support.getDropLocation()).getIndex();
		final int from = payload.sourceIndex();
		return ((payload.sourceList() == _list) && (gap > from)) ? (gap - 1) : gap;
	}
}
