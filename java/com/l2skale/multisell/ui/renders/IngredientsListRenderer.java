package com.l2skale.multisell.ui.renders;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.l2skale.multisell.managers.ThemeManager;
import com.l2skale.multisell.model.ItemAmount;

/*
 * @author Skache
 */
public class IngredientsListRenderer extends JLabel implements ListCellRenderer<ItemAmount>
{
	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList<? extends ItemAmount> list, ItemAmount amountItem, int index, boolean isSelected, boolean cellHasFocus)
	{
		// Get the current theme state (dark or light mode)
		boolean isDarkMode = ThemeManager.isDarkMode();

		// Set text and icon for the item
		setText(formatItemName(amountItem.getItem().getName(), amountItem.getAmount()));
		setIcon(amountItem.getItem().getIcon());

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

	// Method to format the item name and amount
	private String formatItemName(String itemName, int amount)
	{
		// Style item name normally and make the amount bold or different color
		return "<html>" + itemName + " <span style='font-weight:bold; color:#FFAA00;'>[" + formatAmount(amount) + "]</span></html>";
	}

	// Method to format the amount
	private String formatAmount(int amount)
	{
		return String.format("%,d", amount); // Adds commas to the number (e.g., 1,000)
	}
}