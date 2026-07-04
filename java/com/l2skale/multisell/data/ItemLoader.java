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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.l2skale.multisell.model.Item;

/*
 * Loads item definitions from the datapack's stats/items folder (and its custom/ subfolder).
 * Only <item> elements that are DIRECT children of <list> are real definitions - nested
 * <item> (e.g. rewards inside <capsuled_items>) carry just an id, no name/type, and would
 * otherwise overwrite the real definition; the base loader already skips those.
 *
 * @author Skache
 */
public class ItemLoader extends XmlListLoader<List<Item>>
{
	public ItemLoader(String itemsFolderPath)
	{
		super(itemsFolderPath);
	}

	@Override
	protected String elementTag()
	{
		return "item";
	}

	@Override
	protected List<Item> createResult()
	{
		return new ArrayList<>();
	}

	@Override
	protected void handle(Element element, List<Item> items)
	{
		final int id = Integer.parseInt(element.getAttribute("id"));
		final String name = element.getAttribute("name");
		final String type = element.getAttribute("type");
		final String iconName = getSetValue(element, "icon");
		final String crystalType = getSetValue(element, "crystal_type");
		final boolean isQuestItem = Boolean.parseBoolean(getSetValue(element, "is_questitem"));

		// The icon is loaded lazily by Item on first use.
		items.add(new Item(id, name, type, isQuestItem, iconName, crystalType));
	}

	// Returns the val of the item's <set name="..."> child, or null if that set is missing.
	private String getSetValue(Element element, String setName)
	{
		final NodeList setNodes = element.getElementsByTagName("set");
		for (int j = 0; j < setNodes.getLength(); j++)
		{
			final Node setNode = setNodes.item(j);
			if (setNode.getNodeType() == Node.ELEMENT_NODE)
			{
				final Element setElement = (Element) setNode;
				if (setElement.getAttribute("name").equals(setName))
				{
					return setElement.getAttribute("val");
				}
			}
		}

		return null;
	}
}
