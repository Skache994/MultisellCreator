package com.l2skale.multisell.ui.renders;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.l2skale.multisell.model.Item;

/*
 * A single bordered item slot showing the item's icon (inventory-cell look),
 * with the item name on hover. The count is shown separately next to the slot.
 *
 * @author Skache
 */
public class ItemSlot extends JComponent
{
	private static final long serialVersionUID = 1L;

	private final Icon _icon;
	private final int _size;

	public ItemSlot(Item item, int itemId, int size)
	{
		_size = size;
		_icon = scaledIcon(item, size - 4);

		final Dimension dim = new Dimension(size, size);
		setPreferredSize(dim);
		setMinimumSize(dim);
		setMaximumSize(dim);
		setToolTipText(item != null ? item.getName() : ("Item id " + itemId));
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		// Slot background and border.
		g.setColor(new Color(0, 0, 0, 70));
		g.fillRect(0, 0, _size - 1, _size - 1);
		g.setColor(new Color(110, 110, 110));
		g.drawRect(0, 0, _size - 1, _size - 1);

		// Centered icon.
		if (_icon != null)
		{
			final int x = (_size - _icon.getIconWidth()) / 2;
			final int y = (_size - _icon.getIconHeight()) / 2;
			_icon.paintIcon(this, g, x, y);
		}
	}

	// Wrapping in ImageIcon forces the scaled image to load synchronously (MediaTracker),
	// so it is ready to paint immediately.
	private static Icon scaledIcon(Item item, int iconSize)
	{
		if ((item == null) || (item.getIcon() == null) || (item.getIcon().getImage() == null))
		{
			return null;
		}

		final Image scaled = item.getIcon().getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
	}
}
