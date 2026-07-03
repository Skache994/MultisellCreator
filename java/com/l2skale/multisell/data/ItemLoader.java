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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.l2skale.multisell.model.Item;

/*
 * @author Skache
 */
public class ItemLoader
{
	private final String itemsFolderPath;
	private final String iconsFolderPath;

	// Constructor to initialize paths.
	public ItemLoader(String itemsFolderPath, String iconsFolderPath)
	{
		this.itemsFolderPath = itemsFolderPath;
		this.iconsFolderPath = iconsFolderPath;
	}

	// Load items from XML files.
	public List<Item> loadItems()
	{
		List<Item> items = new ArrayList<>();
		File folder = new File(itemsFolderPath);

		// Check if the items folder is valid.
		if (!folder.exists() || !folder.isDirectory())
		{
			System.err.println("Items folder not found: " + itemsFolderPath);
			return items;
		}

		// List all XML files in the folder.
		File[] files = folder.listFiles((_, name) -> name.endsWith(".xml"));
		if (files != null)
		{
			// Parse items from each XML file.
			for (File file : files)
			{
				items.addAll(parseItemsFromFile(file));
			}
		}

		return items;
	}

	// Parses a single XML file for items.
	private List<Item> parseItemsFromFile(File file)
	{
		List<Item> items = new ArrayList<>();
		try
		{
			// Set up XML parsing.
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);
			final Element root = doc.getDocumentElement();
			root.normalize();

			// Only <item> elements that are DIRECT children of <list> are real item definitions.
			// Nested <item> (e.g. rewards inside <capsuled_items>) carry just an id, no name/type -
			// reading those would overwrite the real definition and leave the item nameless.
			NodeList children = root.getChildNodes();
			for (int i = 0; i < children.getLength(); i++)
			{
				Node node = children.item(i);
				if ((node.getNodeType() != Node.ELEMENT_NODE) || !"item".equals(node.getNodeName()))
				{
					continue;
				}

				Element element = (Element) node;
				int id = Integer.parseInt(element.getAttribute("id"));
				String name = element.getAttribute("name");
				String type = element.getAttribute("type");
				String iconName = getIconNameFromElement(element);
				boolean isQuestItem = getIsQuestItemFromElement(element);

				// The icon is loaded lazily by Item on first use.
				items.add(new Item(id, name, type, isQuestItem, iconName, iconsFolderPath));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return items;
	}

	// Extracts the icon name from an XML element.
	private String getIconNameFromElement(Element element)
	{
		NodeList setNodes = element.getElementsByTagName("set");
		for (int j = 0; j < setNodes.getLength(); j++)
		{
			Node setNode = setNodes.item(j);
			if (setNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element setElement = (Element) setNode;
				if (setElement.getAttribute("name").equals("icon"))
				{
					return setElement.getAttribute("val");
				}
			}
		}

		return null;
	}

	// Extracts whether the item is a quest item from an XML element.
	private boolean getIsQuestItemFromElement(Element element)
	{
		NodeList setNodes = element.getElementsByTagName("set");
		for (int j = 0; j < setNodes.getLength(); j++)
		{
			Node setNode = setNodes.item(j);
			if (setNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element setElement = (Element) setNode;
				if (setElement.getAttribute("name").equals("is_questitem"))
				{
					return Boolean.parseBoolean(setElement.getAttribute("val"));
				}
			}
		}

		return false; // Default value (if missing, it's false).
	}
}
