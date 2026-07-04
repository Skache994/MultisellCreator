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
package com.l2skale.multisell.managers;

import java.util.HashMap;
import java.util.Map;

import com.l2skale.multisell.data.NpcNameLoader;

/*
 * Holds the datapack's npc id and name :D
 *
 * @author Skache
 */
public class NpcManager
{
	private final NpcNameLoader _npcNameLoader;

	private final Map<Integer, String> names = new HashMap<>();

	// Constructor to initialize the NpcNameLoader.
	public NpcManager(String npcsFolderPath)
	{
		this._npcNameLoader = new NpcNameLoader(npcsFolderPath);
	}

	// Load npc names into memory.
	public void loadNpcNames()
	{
		try
		{
			final long start = System.currentTimeMillis();
			Map<Integer, String> loadedNames = _npcNameLoader.load();
			names.clear();
			names.putAll(loadedNames);
			final long ms = System.currentTimeMillis() - start;
			System.out.println("[NpcManager] Loaded " + names.size() + " named npc ids in " + ms + " ms.");
		}
		catch (Exception e)
		{
			System.err.println("Error loading npc names: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Retrieve an npc name by ID, or null if the datapack has no name for it.
	public String getNpcName(int id)
	{
		return names.get(id);
	}

	// Get all npc names.
	public Map<Integer, String> getAllNpcNames()
	{
		return names;
	}
}
