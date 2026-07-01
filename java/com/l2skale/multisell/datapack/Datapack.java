package com.l2skale.multisell.datapack;

import java.io.File;

/*
 * Represents a selected L2J Mobius server datapack folder and resolves the
 * locations this tool needs inside it. Path resolution only - no I/O logic.
 *
 * @author Skache
 */
public class Datapack
{
	private static final String ITEMS_PATH = "data/stats/items";
	private static final String MULTISELL_PATH = "data/multisell";

	private final File _root;

	public Datapack(File root)
	{
		_root = root;
	}

	public File getRoot()
	{
		return _root;
	}

	// <root>/data/stats/items - where the server item definitions live.
	public File getItemsDir()
	{
		return new File(_root, ITEMS_PATH);
	}

	// <root>/data/multisell - where multisell lists are read from and saved to.
	public File getMultisellDir()
	{
		return new File(_root, MULTISELL_PATH);
	}

	// A folder counts as a datapack only if it actually holds the item definitions.
	public boolean isValid()
	{
		final File items = getItemsDir();
		return items.isDirectory();
	}

	@Override
	public String toString()
	{
		return _root == null ? "<no datapack>" : _root.getAbsolutePath();
	}
}
