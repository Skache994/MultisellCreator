package com.l2skale.multisell.ui.renders;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.l2skale.multisell.model.Item;

/*
 * @author Skache
 */
public class ItemListRenderer extends JLabel implements ListCellRenderer<Item>
{
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList<? extends Item> list, Item item, int index, boolean isSelected, boolean cellHasFocus)
	{
		setIcon(item.getIcon());
		setText(item.isQuestItem() ? "<html><b style='color:#FFAA00;'>[Quest]</b> " + item.getName() + "</html>" : item.getName());

		setBackground(RowColors.background(isSelected));
		setForeground(RowColors.foreground());
		setBorder(isSelected ? BorderFactory.createLineBorder(Color.BLACK, 2) : null);
		setOpaque(true);

		return this;
	}
}
