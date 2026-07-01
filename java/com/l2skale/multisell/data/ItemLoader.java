package com.l2skale.multisell.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
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

	private static String ICON_PATH = "data/icons/NOIMAGE.png";

	// Constructor to initialize paths and IconLoader.
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
			doc.getDocumentElement().normalize();

			// Get all item nodes.
			NodeList itemNodes = doc.getElementsByTagName("item");
			for (int i = 0; i < itemNodes.getLength(); i++)
			{
				Node node = itemNodes.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element) node;
					int id = Integer.parseInt(element.getAttribute("id"));
					String name = element.getAttribute("name");
					String type = element.getAttribute("type");
					String iconName = getIconNameFromElement(element);

					// Load the icon as an ImageIcon.
					ImageIcon icon = loadIcon(iconName);
					if (icon == null)
					{
						System.err.println("Missing icon for item " + id + " (" + name + "). Skipping, using default icon.");
						icon = getDefaultIcon();
					}

					// Check if the item is a quest item.
					boolean isQuestItem = getIsQuestItemFromElement(element);

					// Create the Item object with isQuestItem field.
					items.add(new Item(id, name, type, icon, isQuestItem));
				}
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

	// Directly load the icon from the icons folder
	private ImageIcon loadIcon(String iconName)
	{
		if (iconName == null || iconName.isEmpty())
		{
			return null;
		}

		String iconFileName = iconName.replace("icon.", "") + ".png";
		String iconPath = iconsFolderPath + File.separator + iconFileName;
		File iconFile = new File(iconPath);

		return iconFile.exists() ? new ImageIcon(iconPath) : null;
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

	// Default icon if the actual icon is missing.
	public ImageIcon getDefaultIcon()
	{
		File iconFile = new File(ICON_PATH);

		if (!iconFile.exists())
		{
			System.err.println("Default icon not found at path: " + ICON_PATH);
			return new ImageIcon();
		}

		return new ImageIcon(iconFile.getAbsolutePath());
	}
}
