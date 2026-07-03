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
package com.l2skale.multisell.ui.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.l2skale.multisell.managers.ThemeManager;

/*
 * The one right-click menu for every list in the app. A list installs it once and
 * supplies only the data - the items and separators for the clicked row; this class
 * owns all the boilerplate (popup trigger, row selection) and the dark/light styling,
 * so every menu looks and behaves the same.
 *
 * @author Skache
 */
public final class ListContextMenu
{
	private static final Object SEPARATOR = new Object();

	private ListContextMenu()
	{
	}

	// Given the clicked value and its row index, add the menu's items/separators.
	@FunctionalInterface
	public interface MenuBuilder<T>
	{
		void build(Menu menu, T value, int index);
	}

	// Install the menu on a list. The builder runs each time a row is right-clicked.
	public static <T> void install(JList<T> list, MenuBuilder<T> builder)
	{
		list.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				maybeShow(e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				maybeShow(e);
			}

			private void maybeShow(MouseEvent e)
			{
				if (!e.isPopupTrigger())
				{
					return;
				}

				final int index = list.locationToIndex(e.getPoint());
				if (index < 0)
				{
					return;
				}

				// locationToIndex returns the nearest row even below the last one; ignore empty space.
				final Rectangle cell = list.getCellBounds(index, index);
				if ((cell == null) || !cell.contains(e.getPoint()))
				{
					return;
				}

				list.setSelectedIndex(index);

				final Menu menu = new Menu();
				builder.build(menu, list.getModel().getElementAt(index), index);
				menu.toPopup().show(list, e.getX(), e.getY());
			}
		});
	}

	// Collects the menu contents as data; the caller adds items and separators.
	public static final class Menu
	{
		private final List<Object> _entries = new ArrayList<>();

		public Item item(String label, Runnable action)
		{
			final Item item = new Item(label, action);
			_entries.add(item);
			return item;
		}

		public void separator()
		{
			_entries.add(SEPARATOR);
		}

		private JPopupMenu toPopup()
		{
			final Palette p = Palette.current();
			final JPopupMenu popup = new JPopupMenu();
			popup.setBackground(p.bg);
			popup.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(p.border), BorderFactory.createEmptyBorder(4, 0, 4, 0)));

			for (Object entry : _entries)
			{
				if (entry == SEPARATOR)
				{
					popup.addSeparator();
				}
				else
				{
					popup.add(styled((Item) entry, p));
				}
			}
			return popup;
		}

		private JMenuItem styled(Item item, Palette p)
		{
			final JMenuItem menuItem = new JMenuItem();
			menuItem.setOpaque(true);
			menuItem.setBackground(p.bg);
			menuItem.setFont(p.font);
			menuItem.setBorder(BorderFactory.createEmptyBorder(5, 16, 5, 16));
			menuItem.setEnabled(item.enabled);

			if (item.enabled)
			{
				menuItem.setText(item.label);
				menuItem.setForeground(p.fg);
				menuItem.addActionListener(_ -> item.action.run());

				// Highlight on hover (armed), reliably across look and feels.
				menuItem.getModel().addChangeListener(_ ->
				{
					final boolean armed = menuItem.getModel().isArmed();
					menuItem.setBackground(armed ? p.hoverBg : p.bg);
					menuItem.setForeground(armed ? p.hoverFg : p.fg);
				});
			}
			else
			{
				// HTML forces the muted colour even though a disabled item would otherwise be repainted.
				menuItem.setText("<html><font color='" + hex(p.disabledFg) + "'>" + item.label + "</font></html>");
			}
			return menuItem;
		}
	}

	// One clickable menu entry; enabled(false) greys it out (e.g. Move Up on the first row).
	public static final class Item
	{
		private final String label;
		private final Runnable action;
		private boolean enabled = true;

		private Item(String label, Runnable action)
		{
			this.label = label;
			this.action = action;
		}

		public Item enabled(boolean enabled)
		{
			this.enabled = enabled;
			return this;
		}
	}

	// The menu colours for the active theme.
	private static final class Palette
	{
		private final Color bg;
		private final Color fg;
		private final Color disabledFg;
		private final Color hoverBg;
		private final Color hoverFg;
		private final Color border;
		private final Font font;

		private Palette(Color bg, Color fg, Color disabledFg, Color hoverBg, Color hoverFg, Color border, Font font)
		{
			this.bg = bg;
			this.fg = fg;
			this.disabledFg = disabledFg;
			this.hoverBg = hoverBg;
			this.hoverFg = hoverFg;
			this.border = border;
			this.font = font;
		}

		private static Palette current()
		{
			final Font font = new Font("Segoe UI", Font.PLAIN, 13);
			if (ThemeManager.isDarkMode())
			{
				return new Palette(new Color(43, 43, 43), new Color(232, 232, 232), new Color(120, 120, 120), new Color(104, 93, 156), Color.WHITE, new Color(85, 85, 85), font);
			}
			return new Palette(Color.WHITE, new Color(30, 30, 30), new Color(160, 160, 160), new Color(204, 204, 255), new Color(20, 20, 20), new Color(184, 184, 184), font);
		}
	}

	private static String hex(Color c)
	{
		return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
	}
}
