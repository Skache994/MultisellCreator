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

/*
 * Shared skeleton for the datapack loaders
 * @param <R> the type the loader accumulates into (e.g. a List of items, a Map of names).
 * 
 * @author Skache
 */
public abstract class XmlListLoader<R>
{
	private final String folderPath;

	protected XmlListLoader(String folderPath)
	{
		this.folderPath = folderPath;
	}

	// The direct-child element name that marks a real definition (e.g. "item", "npc").
	protected abstract String elementTag();

	// A fresh, empty accumulator for one load run.
	protected abstract R createResult();

	// Handle one matching element, adding to the accumulator. 'custom' is true when the element's
	// file lives under a custom/ subfolder (server-added content).
	protected abstract void handle(Element element, R result, boolean custom);

	// Walk the folder (and subfolders) and parse every matching element into a result.
	public R load()
	{
		final R result = createResult();
		final File folder = new File(folderPath);

		if (!folder.exists() || !folder.isDirectory())
		{
			System.err.println(getClass().getSimpleName() + ": folder not found: " + folderPath);
			return result;
		}

		final List<File> files = new ArrayList<>();
		collectXmlFiles(folder, files);
		for (File file : files)
		{
			parseFile(file, result);
		}

		return result;
	}

	// Every .xml file under the folder AND its subfolders, so custom/ data is picked up too.
	private void collectXmlFiles(File dir, List<File> out)
	{
		final File[] entries = dir.listFiles();
		if (entries == null)
		{
			return;
		}

		for (File entry : entries)
		{
			if (entry.isDirectory())
			{
				collectXmlFiles(entry, out);
			}
			else if (entry.getName().toLowerCase().endsWith(".xml"))
			{
				out.add(entry);
			}
		}
	}

	// Parse one file, handing each direct <list> child of the wanted tag to the subclass.
	private void parseFile(File file, R result)
	{
		try
		{
			final boolean custom = isUnderCustomFolder(file);

			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document doc = builder.parse(file);
			final Element root = doc.getDocumentElement();
			root.normalize();

			final String tag = elementTag();
			final NodeList children = root.getChildNodes();
			for (int i = 0; i < children.getLength(); i++)
			{
				final Node node = children.item(i);
				if ((node.getNodeType() == Node.ELEMENT_NODE) && tag.equals(node.getNodeName()))
				{
					handle((Element) node, result, custom);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// A file is custom when any folder on its path (up to the loaded root) is named "custom".
	private boolean isUnderCustomFolder(File file)
	{
		final File root = new File(folderPath);
		File dir = file.getParentFile();
		while (dir != null)
		{
			if (dir.getName().equalsIgnoreCase("custom"))
			{
				return true;
			}
			if (dir.equals(root))
			{
				break;
			}
			dir = dir.getParentFile();
		}
		return false;
	}
}
