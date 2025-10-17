package com.l2skale.multisell.ui.panels.popup;

import java.awt.Window;

import javax.swing.JOptionPane;

import com.l2skale.multisell.ui.utils.MessageUtils;

/*
 * @author Skache
 */
public class DialogUtils
{
	public static Integer promptForAmount(Window parent, String itemName)
	{
		String input = JOptionPane.showInputDialog(parent, "Enter amount for \"" + itemName + "\"", "1");

		if (input != null && !input.trim().isEmpty())
		{
			try
			{
				int amount = Integer.parseInt(input.trim());
				if (amount > 0)
				{
					return amount;
				}
				else
				{
					MessageUtils.showErrorMessage(parent, "Amount must be greater than 0", "Input Error");
				}
			}
			catch (NumberFormatException ex)
			{
				MessageUtils.showErrorMessage(parent, "Invalid number entered.", "Input Error");
			}
		}
		return null;
	}
}
