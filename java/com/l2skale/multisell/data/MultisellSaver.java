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
import java.util.Map;
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
	// npcLookup does the same for npc ids in the <npcs> block (may be null, returns null when unknown).
	public static void save(Multisell multisell, File file, IntFunction<Item> itemLookup, IntFunction<String> npcLookup) throws Exception
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

		// Root <list> with its options first, then the schema location. Every attribute the pack
		// allowed is written back in the order it was loaded/set, so nothing is dropped or reordered.
		sb.append("<list");
		for (Map.Entry<String, String> option : multisell.getListAttributes().entries())
		{
			sb.append(" ").append(option.getKey()).append("=\"").append(option.getValue()).append("\"");
		}
		sb.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"").append(schemaLocation(file)).append("\">\n");

		// Allowed NPCs.
		if (!multisell.getNpcIds().isEmpty())
		{
			sb.append("\t<npcs>\n");
			for (int npcId : multisell.getNpcIds())
			{
				sb.append("\t\t<npc>").append(npcId).append("</npc>");
				final String npcName = npcComment(npcId, npcLookup);
				if (npcName != null)
				{
					// "--" is illegal inside an XML comment, so soften it just in case.
					sb.append(" <!-- ").append(npcName.replace("--", "-")).append(" -->");
				}
				sb.append("\n");
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

		// Match the datapack's own file convention so a saved file diffs cleanly against the original:
		// CRLF line endings and no trailing newline (Mobius multisell files end exactly at "</list>").
		String xml = sb.toString();
		if (xml.endsWith("\n"))
		{
			xml = xml.substring(0, xml.length() - 1);
		}
		xml = xml.replace("\n", "\r\n");

		Files.writeString(file.toPath(), xml, StandardCharsets.UTF_8);
	}

	// The inline comment for an <npc> line: the datapack name, or "CB" for the special -1 id
	// (a community-board / "works from everywhere" multisell). Null means no comment.
	private static String npcComment(int npcId, IntFunction<String> npcLookup)
	{
		if (npcId == -1)
		{
			return "CB";
		}

		return (npcLookup == null) ? null : npcLookup.apply(npcId);
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

	// One ingredient/production line with the item name as an inline comment. id and count come
	// first (our house order), then every extra attribute the line carries, in the order it was
	// loaded/set - so enchantmentLevel, maintainIngredient, chance, etc. all survive.
	private static void appendLine(StringBuilder sb, String tag, MultisellItem item, IntFunction<Item> itemLookup)
	{
		sb.append("\t\t<").append(tag);
		sb.append(" id=\"").append(item.getItemId()).append("\"");
		sb.append(" count=\"").append(item.getCount()).append("\"");
		for (Map.Entry<String, String> extra : item.getExtras().entries())
		{
			sb.append(" ").append(extra.getKey()).append("=\"").append(extra.getValue()).append("\"");
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
