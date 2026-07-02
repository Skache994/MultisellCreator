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

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.l2skale.multisell.model.multisell.Entry;
import com.l2skale.multisell.model.multisell.Multisell;
import com.l2skale.multisell.model.multisell.MultisellItem;

/*
 * Reads an existing multisell XML file into a Multisell object. Mirrors how the
 * server parses multisells (see MultisellData). Counterpart of MultisellSaver.
 *
 * @author Skache
 */
public class MultisellLoader
{
	// Parse a multisell XML file. The file name (without .xml) is the list id.
	public static Multisell load(File file) throws Exception
	{
		final Multisell multisell = new Multisell(parseId(file));

		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = factory.newDocumentBuilder();
		final Document doc = builder.parse(file);
		doc.getDocumentElement().normalize();

		// Root <list> element and its options.
		final Element list = doc.getDocumentElement();
		multisell.setApplyTaxes(boolAttr(list, "applyTaxes"));
		multisell.setMaintainEnchantment(boolAttr(list, "maintainEnchantment"));
		if (list.hasAttribute("useRate"))
		{
			multisell.setUseRate(list.getAttribute("useRate"));
		}

		// Allowed NPCs (<npcs><npc>id</npc>...</npcs>).
		final NodeList npcNodes = list.getElementsByTagName("npc");
		for (int i = 0; i < npcNodes.getLength(); i++)
		{
			final String text = npcNodes.item(i).getTextContent().trim();
			if (!text.isEmpty())
			{
				multisell.getNpcIds().add(Integer.parseInt(text));
			}
		}

		// Entries (<item> blocks).
		final NodeList itemNodes = list.getElementsByTagName("item");
		for (int i = 0; i < itemNodes.getLength(); i++)
		{
			final Node node = itemNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				multisell.getEntries().add(parseEntry((Element) node));
			}
		}

		return multisell;
	}

	// Turn one <item> element into an Entry with its ingredients and products.
	private static Entry parseEntry(Element item)
	{
		final Entry entry = new Entry();

		final NodeList ingredients = item.getElementsByTagName("ingredient");
		for (int i = 0; i < ingredients.getLength(); i++)
		{
			entry.addIngredient(parseItem((Element) ingredients.item(i)));
		}

		final NodeList products = item.getElementsByTagName("production");
		for (int i = 0; i < products.getLength(); i++)
		{
			entry.addProduct(parseItem((Element) products.item(i)));
		}

		return entry;
	}

	// Turn one <ingredient> or <production> element into a MultisellItem.
	private static MultisellItem parseItem(Element element)
	{
		final int itemId = Integer.parseInt(element.getAttribute("id"));
		final int count = Integer.parseInt(element.getAttribute("count"));
		final MultisellItem item = new MultisellItem(itemId, count);

		if (element.hasAttribute("enchantmentLevel"))
		{
			item.setEnchantmentLevel(Integer.parseInt(element.getAttribute("enchantmentLevel")));
		}
		if (element.hasAttribute("maintainIngredient"))
		{
			item.setMaintainIngredient(Boolean.parseBoolean(element.getAttribute("maintainIngredient")));
		}

		return item;
	}

	// The list id is the file name without the .xml extension (e.g. 1000.xml -> 1000).
	private static int parseId(File file)
	{
		final String name = file.getName().replaceFirst("(?i)\\.xml$", "");
		try
		{
			return Integer.parseInt(name);
		}
		catch (NumberFormatException e)
		{
			return 0; // Not a numeric name; caller can set a proper id before saving.
		}
	}

	// Reads an attribute as a boolean, defaulting to false when absent.
	private static boolean boolAttr(Element element, String name)
	{
		return element.hasAttribute(name) && Boolean.parseBoolean(element.getAttribute(name));
	}
}
