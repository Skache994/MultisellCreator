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

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.l2skale.multisell.ui.panels.EntriesPanel;
import com.l2skale.multisell.ui.panels.EntrySidePanel;

/*
 * Lays the editor out like the game's Store window: the Entries list on the left
 * (the game's "List"), and the selected entry's Products over Ingredients on the
 * right (the game's "Item Information" over "Required Items").
 *
 * @author Skache
 */
public class RightPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	// The selected entry's two lists: Products (top) over Ingredients (bottom), game order.
	private final EntrySidePanel _ingredientsPanel = new EntrySidePanel("Ingredients");
	private final EntrySidePanel _productsPanel = new EntrySidePanel("Products");

	// The whole multisell - the game's "List".
	private final EntriesPanel _entriesPanel = new EntriesPanel();

	public RightPanel()
	{
		setLayout(new BorderLayout());

		// Right side: Products on top, Ingredients on the bottom (Item Information over Required Items).
		final JSplitPane detailSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, _productsPanel, _ingredientsPanel);
		detailSplit.setResizeWeight(0.5);
		detailSplit.setContinuousLayout(true);

		// Entries (the "List") on the left, the selected entry's detail on the right.
		final JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, _entriesPanel, detailSplit);
		mainSplit.setResizeWeight(0.5);
		mainSplit.setContinuousLayout(true);

		add(mainSplit, BorderLayout.CENTER);
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
