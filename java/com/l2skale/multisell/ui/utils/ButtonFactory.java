package com.l2skale.multisell.ui.utils;

import java.awt.event.ActionListener;

import javax.swing.JButton;

public class ButtonFactory
{
	public static JButton createButton(String text, ActionListener action)
	{
		JButton button = new JButton(text);
		button.addActionListener(action);
		return button;
	}
}
