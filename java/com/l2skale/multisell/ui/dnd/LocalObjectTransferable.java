package com.l2skale.multisell.ui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

/*
 * Carries a live object reference for drag-and-drop within the app (no
 * serialization) - used when dragging an item or entry onto the trash bin.
 *
 * @author Skache
 */
public class LocalObjectTransferable implements Transferable
{
	public static final DataFlavor FLAVOR = createFlavor();
	private static final DataFlavor[] FLAVORS =
	{ FLAVOR };

	private final Object _value;

	public LocalObjectTransferable(Object value)
	{
		_value = value;
	}

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
			return _value;
		}
		throw new UnsupportedFlavorException(flavor);
	}

	private static DataFlavor createFlavor()
	{
		try
		{
			return new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=java.lang.Object");
		}
		catch (ClassNotFoundException e)
		{
			throw new IllegalStateException(e);
		}
	}
}
