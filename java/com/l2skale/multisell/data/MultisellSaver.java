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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.IntFunction;

import com.l2skale.multisell.model.Item;
import com.l2skale.multisell.model.multisell.Entry;
import com.l2skale.multisell.model.multisell.Multisell;
import com.l2skale.multisell.model.multisell.MultisellItem;

/*
 * Writes a Multisell to an XML file in the server's exact style: tab indentation
 * and the item name as an inline comment after each line. Counterpart of MultisellLoader.
 *
 * @author Skache
 */
public class MultisellSaver
{
	// itemLookup resolves item ids to their names for the inline comments (may be null).
	public static void save(Multisell multisell, File file, IntFunction<Item> itemLookup) throws Exception
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

		// Root <list> with its options first, then the schema location.
		sb.append("<list");
		if (multisell.isApplyTaxes())
		{
			sb.append(" applyTaxes=\"true\"");
		}
		if (multisell.isMaintainEnchantment())
		{
			sb.append(" maintainEnchantment=\"true\"");
		}
		if ((multisell.getUseRate() != null) && !multisell.getUseRate().isEmpty())
		{
			sb.append(" useRate=\"").append(multisell.getUseRate()).append("\"");
		}
		sb.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"").append(schemaLocation(file)).append("\">\n");

		// Allowed NPCs.
		if (!multisell.getNpcIds().isEmpty())
		{
			sb.append("\t<npcs>\n");
			for (int npcId : multisell.getNpcIds())
			{
				sb.append("\t\t<npc>").append(npcId).append("</npc>\n");
			}
			sb.append("\t</npcs>\n");
		}

		// Entries.
		for (Entry entry : multisell.getEntries())
		{
			sb.append("\t<item>\n");
			for (MultisellItem ingredient : entry.getIngredients())
			{
				appendLine(sb, "ingredient", ingredient, itemLookup);
			}
			for (MultisellItem product : entry.getProducts())
			{
				appendLine(sb, "production", product, itemLookup);
			}
			sb.append("\t</item>\n");
		}

		sb.append("</list>\n");

		Files.writeString(file.toPath(), sb.toString(), StandardCharsets.UTF_8);
	}

	// The schema path is relative to the file's own location, so its depth depends on how far
	// below the multisell/ folder the file sits: multisell/NNN.xml -> ../xsd, multisell/custom/NNN.xml -> ../../xsd.
	private static String schemaLocation(File file)
	{
		File dir = file.getParentFile();
		int up = 1;
		while (dir != null)
		{
			if (dir.getName().equalsIgnoreCase("multisell"))
			{
				return "../".repeat(up) + "xsd/multisell.xsd";
			}
			up++;
			dir = dir.getParentFile();
		}
		return "../xsd/multisell.xsd"; // Not under a multisell/ folder; use the default depth.
	}

	// One ingredient/production line with the item name as an inline comment.
	private static void appendLine(StringBuilder sb, String tag, MultisellItem item, IntFunction<Item> itemLookup)
	{
		sb.append("\t\t<").append(tag);
		sb.append(" count=\"").append(item.getCount()).append("\"");
		sb.append(" id=\"").append(item.getItemId()).append("\"");
		if (item.getEnchantmentLevel() > 0)
		{
			sb.append(" enchantmentLevel=\"").append(item.getEnchantmentLevel()).append("\"");
		}
		if ("ingredient".equals(tag) && item.isMaintainIngredient())
		{
			sb.append(" maintainIngredient=\"true\"");
		}
		sb.append(" />");

		final Item template = itemLookup == null ? null : itemLookup.apply(item.getItemId());
		if (template != null)
		{
			// "--" is illegal inside an XML comment, so soften it just in case.
			sb.append(" <!-- ").append(template.getName().replace("--", "-")).append(" -->");
		}

		sb.append("\n");
	}
}
