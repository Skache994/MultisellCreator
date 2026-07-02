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

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/*
 * @author Skache
 */
public class DialogUtils
{
	public static Integer promptForAmount(Window parent, String itemName)
	{
		return promptForAmount(parent, itemName, 1);
	}

	public static Integer promptForAmount(Window parent, String itemName, int initial)
	{
		final AmountField field = new AmountField();
		if (initial > 0)
		{
			field.setValue(initial);
		}

		// Focus and select the field as soon as the dialog is shown.
		field.addAncestorListener(new AncestorListener()
		{
			@Override
			public void ancestorAdded(AncestorEvent event)
			{
				field.requestFocusInWindow();
				field.selectAll();
			}

			@Override
			public void ancestorMoved(AncestorEvent event)
			{
			}

			@Override
			public void ancestorRemoved(AncestorEvent event)
			{
			}
		});

		final JPanel panel = new JPanel(new BorderLayout(0, 6));
		panel.add(new JLabel("Enter amount for \"" + itemName + "\":"), BorderLayout.NORTH);
		panel.add(field, BorderLayout.CENTER);

		final int result = JOptionPane.showConfirmDialog(parent, panel, "Amount", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (result != JOptionPane.OK_OPTION)
		{
			return null;
		}

		final int amount = field.getValue();
		if (amount > 0)
		{
			return amount;
		}

		MessageUtils.showErrorMessage(parent, "Enter a valid amount greater than 0.", "Input Error");
		return null;
	}
}
