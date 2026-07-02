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
package com.l2skale.multisell.datapack;

import java.io.File;

/*
 * Represents a selected L2J Mobius datapack. Accepts either the gameserver root
 * (the "game" folder, which contains data/) or the data/ folder itself, and
 * resolves the locations this tool needs. Path resolution only - no I/O logic.
 *
 * @author Skache
 */
public class Datapack
{
	private static final String ITEMS_PATH = "stats/items";
	private static final String MULTISELL_PATH = "multisell";

	private final File _root;
	private final File _dataDir;

	public Datapack(File root)
	{
		_root = root;
		_dataDir = resolveDataDir(root);
	}

	public File getRoot()
	{
		return _root;
	}

	// <data>/stats/items - where the server item definitions live.
	public File getItemsDir()
	{
		return new File(_dataDir, ITEMS_PATH);
	}

	// <data>/multisell - where multisell lists are read from and saved to.
	public File getMultisellDir()
	{
		return new File(_dataDir, MULTISELL_PATH);
	}

	// A folder counts as a datapack only if it actually holds the item definitions.
	public boolean isValid()
	{
		return getItemsDir().isDirectory();
	}

	@Override
	public String toString()
	{
		return _root == null ? "<no datapack>" : _root.getAbsolutePath();
	}

	// Accept either the game folder (which contains data/) or the data folder itself.
	private static File resolveDataDir(File root)
	{
		final File dataSubfolder = new File(root, "data");
		if (new File(dataSubfolder, ITEMS_PATH).isDirectory())
		{
			return dataSubfolder; // Picked the game folder.
		}

		return root; // Picked the data folder directly (or fall back to it).
	}
}
