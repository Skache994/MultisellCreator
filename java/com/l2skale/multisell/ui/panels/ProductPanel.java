package com.l2skale.multisell.ui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DropMode;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.l2skale.multisell.enums.ItemType;
import com.l2skale.multisell.model.ItemAmount;
import com.l2skale.multisell.model.ProductList;
import com.l2skale.multisell.ui.dnd.ItemImportTransferHandler;
import com.l2skale.multisell.ui.renders.ProductsListRenderer;

public class ProductPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private JList<ItemAmount> _productsListView;
	private JScrollPane _productionsScrollPane;

	// Constructor now takes Product as a parameter
	public ProductPanel(ProductList product)
	{
		setLayout(new BorderLayout());

		// Set up the label
		JLabel productsLabel = new JLabel("Products");
		productsLabel.setFont(new Font("Arial", Font.BOLD, 14));
		add(productsLabel, BorderLayout.NORTH);

		// Set up the JList for products
		_productsListView = new JList<>(product.getProductsModel());
		_productsListView.setCellRenderer(new ProductsListRenderer());

		// Add this to support item drop
		_productsListView.setDropMode(DropMode.INSERT);
		_productsListView.setTransferHandler(new ItemImportTransferHandler(null, product, ItemType.PRODUCT));

		// Add the JList to a scroll pane
		_productionsScrollPane = new JScrollPane(_productsListView);
		add(_productionsScrollPane, BorderLayout.CENTER);

		// Set up the context menu (JPopupMenu)
		JPopupMenu popupMenu = new JPopupMenu();

		// Remove whole product
		JMenuItem removeProductItem = new JMenuItem("Remove Product");
		removeProductItem.addActionListener(e ->
		{
			ItemAmount selectedProduct = _productsListView.getSelectedValue();
			if (selectedProduct != null)
			{
				product.removeProduct(selectedProduct, selectedProduct.getAmount());
			}
		});

		// Remove specified amount of product
		JMenuItem removeAmountItem = new JMenuItem("Remove Specific Amount");
		removeAmountItem.addActionListener(e ->
		{
			ItemAmount selectedProduct = _productsListView.getSelectedValue();
			if (selectedProduct != null)
			{
				// Prompt the user to input the amount to remove
				Window parentWindow = SwingUtilities.getWindowAncestor(ProductPanel.this);
				String input = JOptionPane.showInputDialog(parentWindow, "Enter amount to remove:");

				// Handle the case where the user cancels the input dialog (input == null)
				if (input == null)
				{
					return;
				}

				// Trim the input to avoid leading or trailing spaces
				input = input.trim();
				if (input.isEmpty())
				{
					JOptionPane.showMessageDialog(parentWindow, "Amount cannot be empty. Please enter a valid amount.", "Error", JOptionPane.ERROR_MESSAGE);
					return; // Exit the method if the input is empty
				}

				try
				{
					// Parse the input and remove the specified amount
					int amountToRemove = Integer.parseInt(input);
					if (amountToRemove > 0 && amountToRemove <= selectedProduct.getAmount())
					{
						product.removeProduct(selectedProduct, amountToRemove);
					}
					else
					{
						// Show error if the amount is invalid
						JOptionPane.showMessageDialog(parentWindow, "Invalid amount. Enter a valid amount.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				catch (NumberFormatException ex)
				{
					// Handle invalid input (non-numeric)
					JOptionPane.showMessageDialog(parentWindow, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// Add items to the menu
		popupMenu.add(removeProductItem);
		popupMenu.add(removeAmountItem);

		// Add MouseListener to the JList to show the right-click menu
		_productsListView.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isRightMouseButton(e))
				{
					int index = _productsListView.locationToIndex(e.getPoint());
					if (index >= 0)
					{
						_productsListView.setSelectedIndex(index);
						popupMenu.show(_productsListView, e.getX(), e.getY());
					}
				}
			}
		});

		// Set the preferred size here
		setPreferredSize(new Dimension(200, 200));
	}
}
