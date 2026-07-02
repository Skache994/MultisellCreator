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
package com.l2skale.multisell.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import com.l2skale.multisell.ui.panels.EntriesPanel;
import com.l2skale.multisell.ui.panels.EntrySidePanel;

/*
 * @author Skache
 */
public class RightPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	// Editor (top): the selected entry's two lists.
	private final EntrySidePanel _ingredientsPanel = new EntrySidePanel("Ingredients");
	private final EntrySidePanel _productsPanel = new EntrySidePanel("Products");

	// The whole multisell (bottom).
	private final EntriesPanel _entriesPanel = new EntriesPanel();

	public RightPanel()
	{
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 5, 5, 5);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.5;
		gbc.weighty = 0.6;
		add(_ingredientsPanel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0.5;
		gbc.weighty = 0.6;
		add(_productsPanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		add(_entriesPanel, gbc);
	}

	public EntrySidePanel getIngredientsPanel()
	{
		return _ingredientsPanel;
	}

	public EntrySidePanel getProductsPanel()
	{
		return _productsPanel;
	}

	public EntriesPanel getEntriesPanel()
	{
		return _entriesPanel;
	}
}
