package com.l2skale.multisell.ui.renders;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.l2skale.multisell.managers.ThemeManager;
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
		// Get the current theme state (dark mode or not)
		boolean isDarkMode = ThemeManager.isDarkMode();

		// Set text and icon
		setIcon(item.getIcon() != null ? item.getIcon() : null);
		setText(item.isQuestItem() ? "<html><b style='color:#FFAA00;'>[Quest]</b> " + item.getName() + "</html>" : item.getName());

		// Define colors based on theme
		Color selectedBg = isDarkMode ? new Color(104, 93, 156) : new Color(204, 204, 255); // Dark vs light theme selection background
		Color defaultBg = isDarkMode ? new Color(50, 50, 50) : Color.WHITE; // Darker gray vs white for normal items
		Color fgColor = isDarkMode ? Color.WHITE : Color.BLACK; // Text color for dark vs light theme

		// Set default style (background, foreground, border)
		setBackground(defaultBg);
		setForeground(fgColor);
		setBorder(null); // Remove any previous border styles

		// Apply selection styling if the item is selected
		if (isSelected)
		{
			setBackground(selectedBg);
			setForeground(fgColor);
			setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		}

		setOpaque(true);

		return this;
	}
}