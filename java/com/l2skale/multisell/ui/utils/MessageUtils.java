package com.l2skale.multisell.ui.utils;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/*
 * @author Skache
 */
public class MessageUtils
{
	// Show an information message.
	public static void showInfoMessage(Component parent, String message, String title)
	{
		JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	// Show an information message with an icon.
	public static void showInfoMessage(Component parent, JLabel label, String title, ImageIcon icon)
	{
		JOptionPane.showMessageDialog(parent, label, title, JOptionPane.INFORMATION_MESSAGE, icon);
	}

	// Show an error message.
	public static void showErrorMessage(Component parent, String message, String title)
	{
		JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
	}

	// Show a warning message.
	public static void showWarningMessage(Component parent, String message, String title)
	{
		JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
	}

	// Show a custom message with a custom message type.
	public static void showMessage(Component parent, String message, String title, int messageType)
	{
		JOptionPane.showMessageDialog(parent, message, title, messageType);
	}
}
