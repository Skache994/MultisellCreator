package com.l2skale.multisell.data;

import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.l2skale.multisell.MultisellEntry;
import com.l2skale.multisell.model.Item;
import com.l2skale.multisell.model.ItemAmount;

/*
* @author Skache
*/
public class MultisellSaver
{
	private DefaultListModel<MultisellEntry> _multisellSaver;

	public MultisellSaver(DefaultListModel<MultisellEntry> multisellEntriesModel)
	{
		this._multisellSaver = multisellEntriesModel;
	}

	public void saveMultisell()
	{
		// Create a new XML document.
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();

			// Create the root element <list>
			Element rootElement = doc.createElement("list");
			rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			rootElement.setAttribute("xsi:noNamespaceSchemaLocation", "../xsd/multisell.xsd");
			doc.appendChild(rootElement);

			// Iterate over the DefaultListModel (which is a list of MultisellEntry)
			for (int i = 0; i < _multisellSaver.getSize(); i++)
			{
				MultisellEntry entry = _multisellSaver.getElementAt(i);

				// Create a new <item> element for each MultisellEntry
				Element itemElement = doc.createElement("item");
				rootElement.appendChild(itemElement);

				// Add ingredients.
				for (ItemAmount amountItem : entry.getIngredients())
				{
					Item item = amountItem.getItem();
					Comment ingredientComment = doc.createComment(" " + item.getName() + " ");
					itemElement.appendChild(ingredientComment);

					Element ingredientElement = doc.createElement("ingredient");
					ingredientElement.setAttribute("count", String.valueOf(amountItem.getAmount()));
					ingredientElement.setAttribute("id", String.valueOf(item.getId()));
					itemElement.appendChild(ingredientElement);
				}

				// Add productions.
				for (ItemAmount amountItem : entry.getFinalProducts())
				{
					Item item = amountItem.getItem();
					Comment productionComment = doc.createComment(" " + item.getName() + " ");
					itemElement.appendChild(productionComment);

					Element productionElement = doc.createElement("production");
					productionElement.setAttribute("count", String.valueOf(amountItem.getAmount()));
					productionElement.setAttribute("id", String.valueOf(item.getId()));
					itemElement.appendChild(productionElement);
				}
			}

			// Save the XML to a file.
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Save Multisell XML");
			int userSelection = fileChooser.showSaveDialog(null);

			if (userSelection == JFileChooser.APPROVE_OPTION)
			{
				File file = fileChooser.getSelectedFile();
				if (!file.getName().endsWith(".xml"))
				{
					file = new File(file.getAbsolutePath() + ".xml"); // Ensure the file has a .xml extension.
				}

				// Check if the file already exists.
				if (file.exists())
				{
					int response = JOptionPane.showConfirmDialog(null, "The file already exists. Do you want to overwrite it?", "File exists", JOptionPane.YES_NO_OPTION);
					if (response != JOptionPane.YES_OPTION)
					{
						// User chose "No" or closed the dialog, do not overwrite
						JOptionPane.showMessageDialog(null, "Save operation cancelled.");
						return;
					}
				}

				// Write the XML to the file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // Enable indentation
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); // Set indentation size

				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(file);
				transformer.transform(source, result);

				JOptionPane.showMessageDialog(null, "Multisell saved successfully to: " + file.getAbsolutePath());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error saving multisell: " + e.getMessage());
		}
	}
}
