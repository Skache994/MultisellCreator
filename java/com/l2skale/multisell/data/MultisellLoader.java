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
import org.w3c.dom.NamedNodeMap;
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

		// Root <list> element and its options. Read every attribute the pack put here (applyTaxes,
		// useRate, isChanceMultisell, the multipliers, or anything else) so nothing is dropped on save.
		// The xmlns/schema-location attributes are structural, not data, so they are skipped.
		final Element list = doc.getDocumentElement();
		final NamedNodeMap listAttrs = list.getAttributes();
		for (int i = 0; i < listAttrs.getLength(); i++)
		{
			final Node attr = listAttrs.item(i);
			final String name = attr.getNodeName();
			if (name.startsWith("xmlns") || name.startsWith("xsi:"))
			{
				continue;
			}
			multisell.setListAttribute(name, attr.getNodeValue());
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

	// Turn one <ingredient> or <production> element into a MultisellItem. id and count are the
	// required core; every other attribute (enchantmentLevel, maintainIngredient, chance, ...) is
	// kept verbatim so nothing is dropped on save.
	private static MultisellItem parseItem(Element element)
	{
		final int itemId = Integer.parseInt(element.getAttribute("id"));
		final int count = Integer.parseInt(element.getAttribute("count"));
		final MultisellItem item = new MultisellItem(itemId, count);

		final NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++)
		{
			final Node attr = attrs.item(i);
			final String name = attr.getNodeName();
			if (!name.equals("id") && !name.equals("count"))
			{
				item.setExtra(name, attr.getNodeValue());
			}
		}

		return item;
	}

	// The id is the file name without the .xml extension (e.g. 001.xml -> "001"), kept verbatim so
	// the file keeps its exact name on save.
	private static String parseId(File file)
	{
		return file.getName().replaceFirst("(?i)\\.xml$", "");
	}
}
