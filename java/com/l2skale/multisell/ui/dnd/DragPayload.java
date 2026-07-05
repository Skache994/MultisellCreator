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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import javax.swing.JList;

/*
 * The one thing every drag in the app carries: the dragged value (an Item from the catalog, or a
 * MultisellItem or Entry row), the list it came from, and its row there. A drop target decides what
 * to do purely from this - the source list tells it the origin (and so the type), the index lets it
 * reorder. It carries itself as the Transferable, so there is a single flavor for the whole app.
 *
 * @author Skache
 */
public record DragPayload(Object value, JList<?> sourceList, int sourceIndex) implements Transferable
{
	public static final DataFlavor FLAVOR = createFlavor();
	private static final DataFlavor[] FLAVORS =
	{ FLAVOR };

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		return FLAVORS;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return FLAVOR.equals(flavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
	{
		if (FLAVOR.equals(flavor))
		{
			return this;
		}
		throw new UnsupportedFlavorException(flavor);
	}

	private static DataFlavor createFlavor()
	{
		try
		{
			return new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + DragPayload.class.getName());
		}
		catch (ClassNotFoundException e)
		{
			throw new IllegalStateException(e);
		}
	}
}
