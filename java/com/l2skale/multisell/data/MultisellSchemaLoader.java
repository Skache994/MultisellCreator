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

import com.l2skale.multisell.model.multisell.AttributeType;
import com.l2skale.multisell.model.multisell.MultisellSchema;
import com.l2skale.multisell.model.multisell.SchemaAttribute;

/*
 * Reads a datapack's data/xsd/multisell.xsd into a MultisellSchema - the list of
 * attributes THIS server allows on <list>, <ingredient> and <production>. This is
 * the source of truth that lets the app adapt instead of guessing per chronicle.
 *
 * We only care about the attribute declarations, so we walk the known Mobius shape:
 *   list (complexType) -> attributes            (applyTaxes, useRate, multipliers, ...)
 *          -> sequence -> item (complexType)
 *                            -> sequence -> ingredient (complexType) -> attributes
 *                                        -> production (complexType) -> attributes
 * Element/attribute prefixes (xs:, xsd:, none) are ignored so any prefix works.
 *
 * @author Skache
 */
public class MultisellSchemaLoader
{
	public static MultisellSchema load(File xsd) throws Exception
	{
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = factory.newDocumentBuilder();
		final Document doc = builder.parse(xsd);
		doc.getDocumentElement().normalize();

		// xs:schema -> the <list> element declaration and its complexType.
		final Element schema = doc.getDocumentElement();
		final Element listElement = requireChildNamed(schema, "element", "list", "list element");
		final Element listType = requireChild(listElement, "complexType", "list complexType");

		// <list> attributes are the direct xs:attribute children of its complexType.
		final List<SchemaAttribute> listAttributes = readAttributes(listType);

		// Dive to the <ingredient> and <production> complexTypes: list -> sequence -> item -> sequence.
		final Element listSequence = requireChild(listType, "sequence", "list sequence");
		final Element itemElement = requireChildNamed(listSequence, "element", "item", "item element");
		final Element itemType = requireChild(itemElement, "complexType", "item complexType");
		final Element itemSequence = requireChild(itemType, "sequence", "item sequence");

		final Element ingredientElement = requireChildNamed(itemSequence, "element", "ingredient", "ingredient element");
		final Element productionElement = requireChildNamed(itemSequence, "element", "production", "production element");

		final List<SchemaAttribute> ingredientAttributes = readAttributes(requireChild(ingredientElement, "complexType", "ingredient complexType"));
		final List<SchemaAttribute> productionAttributes = readAttributes(requireChild(productionElement, "complexType", "production complexType"));

		return new MultisellSchema(listAttributes, ingredientAttributes, productionAttributes);
	}

	// The direct xs:attribute children of a complexType, in document order.
	private static List<SchemaAttribute> readAttributes(Element complexType)
	{
		final List<SchemaAttribute> attributes = new ArrayList<>();
		final NodeList children = complexType.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)
		{
			final Node child = children.item(i);
			if (isTag(child, "attribute"))
			{
				final Element attribute = (Element) child;
				final String name = attribute.getAttribute("name");
				if (!name.isEmpty())
				{
					final AttributeType type = AttributeType.fromXsd(attribute.getAttribute("type"));
					final boolean required = "required".equalsIgnoreCase(attribute.getAttribute("use"));
					attributes.add(new SchemaAttribute(name, type, required));
				}
			}
		}
		return attributes;
	}

	// The first direct child element with the given local tag name.
	private static Element requireChild(Element parent, String localName, String what) throws Exception
	{
		final Element child = childElement(parent, localName, null);
		if (child == null)
		{
			throw new IllegalStateException("multisell.xsd is not in the expected shape: missing " + what + ".");
		}
		return child;
	}

	// The first direct child element with the given local tag name AND name="nameAttr".
	private static Element requireChildNamed(Element parent, String localName, String nameAttr, String what) throws Exception
	{
		final Element child = childElement(parent, localName, nameAttr);
		if (child == null)
		{
			throw new IllegalStateException("multisell.xsd is not in the expected shape: missing " + what + ".");
		}
		return child;
	}

	// First direct child element matching localName (and, when given, its name attribute).
	private static Element childElement(Element parent, String localName, String nameAttr)
	{
		final NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)
		{
			final Node child = children.item(i);
			if (isTag(child, localName) && ((nameAttr == null) || nameAttr.equals(((Element) child).getAttribute("name"))))
			{
				return (Element) child;
			}
		}
		return null;
	}

	// True when node is an element whose tag name, ignoring any prefix, equals localName.
	private static boolean isTag(Node node, String localName)
	{
		if (node.getNodeType() != Node.ELEMENT_NODE)
		{
			return false;
		}

		final String name = node.getNodeName();
		final int colon = name.indexOf(':');
		final String local = (colon >= 0) ? name.substring(colon + 1) : name;
		return local.equals(localName);
	}
}
