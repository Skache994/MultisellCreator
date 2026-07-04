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
package com.l2skale.multisell.data;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

/*
 * Loads only the id -> name of every NPC in the datapack's stats/npcs folder (and its
 * custom/ subfolder)
 * 
 * @author Skache
 */
public class NpcNameLoader extends XmlListLoader<Map<Integer, String>>
{
	public NpcNameLoader(String npcsFolderPath)
	{
		super(npcsFolderPath);
	}

	@Override
	protected String elementTag()
	{
		return "npc";
	}

	@Override
	protected Map<Integer, String> createResult()
	{
		return new HashMap<>();
	}

	@Override
	protected void handle(Element element, Map<Integer, String> names)
	{
		final String name = element.getAttribute("name");

		// Some npcs (EffectPoint, some Folk/Monster) carry no name - the client supplies it
		// by id. We skip those; callers fall back to the bare id when the name is missing.
		if (name.isEmpty())
		{
			return;
		}

		final int id = Integer.parseInt(element.getAttribute("id"));
		names.put(id, name);
	}
}
