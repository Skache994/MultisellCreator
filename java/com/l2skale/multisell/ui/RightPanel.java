package com.l2skale.multisell.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import com.l2skale.multisell.ui.panels.EntriesPanel;
import com.l2skale.multisell.ui.panels.EntrySidePanel;

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
