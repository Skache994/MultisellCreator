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
package com.l2skale.multisell.ui.renders;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.function.IntFunction;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.l2skale.multisell.model.Item;
import com.l2skale.multisell.model.multisell.MultisellItem;
import com.l2skale.multisell.ui.utils.Numbers;

/*
 * Renders one multisell item as "slot + name xCount", used inside the editor's
 * Ingredients and Products lists. The name/icon are resolved by id through the lookup.
 *
 * @author Skache
 */
public class MultisellItemRenderer extends JPanel implements ListCellRenderer<MultisellItem>
{
	private static final long serialVersionUID = 1L;
	private static final int SLOT = 32;

	// Marker colors, chosen to read on both the dark and light list backgrounds.
	private static final Color CHANCE_COLOR = new Color(90, 160, 220); // blue - a product's odds
	private static final Color KEEP_COLOR = new Color(210, 160, 40); // amber - ingredient not consumed

	private IntFunction<Item> _itemLookup;

	public MultisellItemRenderer()
	{
		setLayout(new FlowLayout(FlowLayout.LEFT, 6, 3));
	}

	public void setItemLookup(IntFunction<Item> itemLookup)
	{
		_itemLookup = itemLookup;
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends MultisellItem> list, MultisellItem value, int index, boolean isSelected, boolean cellHasFocus)
	{
		removeAll();
		setOpaque(true);
		setBackground(RowColors.background(isSelected));
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, RowColors.separator()), BorderFactory.createEmptyBorder(1, 4, 1, 4)));

		final Color fg = RowColors.foreground();
		final Item item = _itemLookup == null ? null : _itemLookup.apply(value.getItemId());

		add(new ItemSlot(item, value.getItemId(), SLOT));

		// "+N" enchant prefix, then the name and count.
		final String name = item != null ? item.getName() : ("id " + value.getItemId());
		final int enchant = value.getExtras().getInt("enchantmentLevel");
		final String prefix = enchant > 0 ? "+" + enchant + " " : "";
		final JLabel label = new JLabel(prefix + name + Numbers.countSuffix(value.getCount()));
		label.setForeground(fg);
		add(label);

		// Per-line markers so the row is not a mystery: a product's chance, a kept ingredient.
		if (value.getExtras().has("chance"))
		{
			add(marker(formatChance(value.getExtras().get("chance")), CHANCE_COLOR));
		}
		if (value.getExtras().getBoolean("maintainIngredient"))
		{
			add(marker("keep", KEEP_COLOR));
		}

		return this;
	}

	// A small colored tag (e.g. "60%", "keep") shown after the item name.
	private static JLabel marker(String text, Color color)
	{
		final JLabel tag = new JLabel(text);
		tag.setForeground(color);
		tag.setFont(tag.getFont().deriveFont(Font.BOLD, 11f));
		return tag;
	}

	// "60" -> "60%", "12.0" -> "12%"; leaves real decimals (e.g. "12.5") intact.
	private static String formatChance(String chance)
	{
		String value = chance.trim();
		if (value.endsWith(".0"))
		{
			value = value.substring(0, value.length() - 2);
		}
		return value + "%";
	}
}
