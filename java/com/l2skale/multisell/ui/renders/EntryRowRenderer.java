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
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.l2skale.multisell.model.Item;
import com.l2skale.multisell.model.multisell.Entry;
import com.l2skale.multisell.model.multisell.MultisellItem;
import com.l2skale.multisell.ui.utils.Numbers;

/*
 * Renders one multisell entry as a row, game style: the product first (the
 * headline), then "requires", then the ingredient slots (the cost). Items show
 * as bordered icon slots with the count beside them and the name on hover.
 *
 * @author Skache
 */
public class EntryRowRenderer extends JPanel implements ListCellRenderer<Entry>
{
	private static final long serialVersionUID = 1L;
	private static final int PRODUCT_SLOT = 40;
	private static final int INGREDIENT_SLOT = 32;

	private IntFunction<Item> _itemLookup;

	public EntryRowRenderer()
	{
		setLayout(new FlowLayout(FlowLayout.LEFT, 6, 4));
		setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
	}

	public void setItemLookup(IntFunction<Item> itemLookup)
	{
		_itemLookup = itemLookup;
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Entry> list, Entry entry, int index, boolean isSelected, boolean cellHasFocus)
	{
		removeAll();
		setOpaque(true);
		setBackground(RowColors.background(isSelected));

		final Color fg = RowColors.foreground();

		// Product(s) first - the headline of the entry.
		for (MultisellItem product : entry.getProducts())
		{
			add(productChip(product, fg));
		}

		add(separator("  requires  ", fg));

		// Ingredients - the cost.
		for (MultisellItem ingredient : entry.getIngredients())
		{
			add(ingredientChip(ingredient, fg));
		}

		return this;
	}

	// Product: a bigger slot plus the item name in bold.
	private JPanel productChip(MultisellItem multisellItem, Color fg)
	{
		final Item item = resolve(multisellItem.getItemId());
		final JPanel chip = transparentRow();
		chip.add(new ItemSlot(item, multisellItem.getItemId(), PRODUCT_SLOT));

		String text = item != null ? item.getName() : ("id " + multisellItem.getItemId());
		if (multisellItem.getCount() > 1)
		{
			text += "  x" + Numbers.format(multisellItem.getCount());
		}

		final JLabel name = new JLabel(" " + text);
		name.setFont(name.getFont().deriveFont(Font.BOLD));
		name.setForeground(fg);
		chip.add(name);
		return chip;
	}

	// Ingredient: a slot plus "xCount".
	private JPanel ingredientChip(MultisellItem multisellItem, Color fg)
	{
		final Item item = resolve(multisellItem.getItemId());
		final JPanel chip = transparentRow();
		chip.add(new ItemSlot(item, multisellItem.getItemId(), INGREDIENT_SLOT));

		final JLabel count = new JLabel(" x" + Numbers.format(multisellItem.getCount()));
		count.setForeground(fg);
		chip.add(count);
		return chip;
	}

	private JLabel separator(String text, Color fg)
	{
		final JLabel label = new JLabel(text);
		label.setForeground(fg);
		return label;
	}

	private JPanel transparentRow()
	{
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setOpaque(false);
		return panel;
	}

	private Item resolve(int itemId)
	{
		return _itemLookup == null ? null : _itemLookup.apply(itemId);
	}
}
