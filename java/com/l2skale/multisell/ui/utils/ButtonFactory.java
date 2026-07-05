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
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;

/*
 * The single place UI controls are built, so every button and checkbox looks and sounds the same
 * (a soft click on press). Classes just call these makers instead of wiring their own.
 *
 * @author Skache
 */
public class ButtonFactory
{
	private static final String CLICK_SOUND = "click_2.wav";

	// A text button (Load Items, Save, New Entry, Edit NPCs...).
	public static JButton createButton(String text, ActionListener action)
	{
		final JButton button = new JButton(text);
		addClick(button);
		button.addActionListener(action);
		return button;
	}

	// An icon-only button (theme toggle, clear search). Pass a null icon if it is set later.
	public static JButton createIconButton(Icon icon, ActionListener action)
	{
		final JButton button = new JButton(icon);
		addClick(button);
		button.addActionListener(action);
		return button;
	}

	// A red "danger" button for destructive actions (e.g. Delete). Reuses the standard click.
	public static JButton createDangerButton(String text, ActionListener action)
	{
		final JButton button = createButton(text, action);
		button.setBackground(new Color(170, 45, 45));
		button.setForeground(Color.WHITE);
		return button;
	}

	// A flat, borderless icon-only button (the small clear-search and remove-npc buttons). The named
	// image is loaded from resources and scaled to size x size; a missing image just yields no icon.
	public static JButton createFlatIconButton(String iconName, int size, String tooltip, ActionListener action)
	{
		ImageIcon icon = ResourceIcons.loadResourceIconsIcon(iconName);
		if (icon != null)
		{
			icon = new ImageIcon(icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
		}

		final JButton button = createIconButton(icon, action);
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setContentAreaFilled(false);
		button.setFocusPainted(false);
		button.setToolTipText(tooltip);
		if (icon != null)
		{
			button.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
		}
		return button;
	}

	// A checkbox (applyTaxes, maintainEnchantment). Add your own state listener to the returned box.
	public static JCheckBox createCheckBox(String text)
	{
		final JCheckBox box = new JCheckBox(text);
		addClick(box);
		return box;
	}

	// The one place the UI click lives; every maker above reuses it.
	private static void addClick(AbstractButton button)
	{
		button.addActionListener(_ -> Sound.playSound(CLICK_SOUND));
	}
}
