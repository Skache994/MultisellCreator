package com.l2skale.multisell.ui.renders;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
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

	private IntFunction<Item> _itemLookup;

	public MultisellItemRenderer()
	{
		setLayout(new FlowLayout(FlowLayout.LEFT, 6, 3));
		setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
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

		final Color fg = RowColors.foreground();
		final Item item = _itemLookup == null ? null : _itemLookup.apply(value.getItemId());

		add(new ItemSlot(item, value.getItemId(), SLOT));

		final String name = item != null ? item.getName() : ("id " + value.getItemId());
		final JLabel label = new JLabel(name + "   x" + Numbers.format(value.getCount()));
		label.setForeground(fg);
		add(label);

		return this;
	}
}
