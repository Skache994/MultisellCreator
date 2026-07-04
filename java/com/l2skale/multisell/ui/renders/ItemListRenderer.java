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

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.l2skale.multisell.model.Item;
import com.l2skale.multisell.ui.utils.GradeBadge;

/*
 * @author Skache
 */
public class ItemListRenderer extends JLabel implements ListCellRenderer<Item>
{
	private static final long serialVersionUID = 1L;

	// Every row shows its icon at this fixed size, regardless of the source icon's size.
	private static final int ICON_SIZE = 32;

	@Override
	public Component getListCellRendererComponent(JList<? extends Item> list, Item item, int index, boolean isSelected, boolean cellHasFocus)
	{
		setIcon(item.getScaledIcon(ICON_SIZE));
		final String quest = item.isQuestItem() ? "<b style='color:#FFAA00;'>[Quest]</b> " : "";
		setText("<html>" + quest + item.getName() + GradeBadge.htmlTag(item) + "</html>");

		setBackground(RowColors.background(isSelected));
		setForeground(RowColors.foreground());
		setBorder(isSelected ? BorderFactory.createLineBorder(Color.BLACK, 2) : null);
		setOpaque(true);

		return this;
	}
}
