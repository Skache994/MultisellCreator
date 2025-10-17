package com.l2skale.multisell.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import com.l2skale.multisell.model.IngredientList;
import com.l2skale.multisell.model.MultisellList;
import com.l2skale.multisell.model.ProductList;
import com.l2skale.multisell.ui.panels.IngredientPanel;
import com.l2skale.multisell.ui.panels.MultisellPanel;
import com.l2skale.multisell.ui.panels.ProductPanel;
import com.l2skale.multisell.ui.panels.TrashBinPanel;

public class RightPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private JPanel _ingredientPanel;
	private JPanel _productPanel;
	private TrashBinPanel _trashPanel;
	private JPanel _multisellPanel;

	public RightPanel(IngredientList ingredient, ProductList product, MultisellList multisellList)
	{
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 5, 5, 5);

		_ingredientPanel = new IngredientPanel(ingredient);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.5;
		gbc.weighty = 1.0;
		gbc.gridheight = 2;
		add(_ingredientPanel, gbc);

		_productPanel = new ProductPanel(product);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0.5;
		gbc.weighty = 0.6;
		gbc.gridheight = 1;
		add(_productPanel, gbc);

		_trashPanel = new TrashBinPanel();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 0.5;
		gbc.weighty = 0.05;
		gbc.insets = new Insets(2, 2, 2, 2);
		_trashPanel.setPreferredSize(new Dimension(40, 40));
		add(_trashPanel, gbc);

		_multisellPanel = new MultisellPanel(multisellList);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		add(_multisellPanel, gbc);
	}
}
