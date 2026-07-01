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
import com.l2skale.multisell.model.IngredientList;
import com.l2skale.multisell.model.ItemAmount;
import com.l2skale.multisell.ui.dnd.ItemImportTransferHandler;
import com.l2skale.multisell.ui.renders.IngredientsListRenderer;

/*
 * @author Skache
 */
public class IngredientPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private JList<ItemAmount> _ingredientsView;
	private JScrollPane _ingredientsScrollPane;

	// Constructor now takes Ingredient as a parameter.
	public IngredientPanel(IngredientList ingredient)
	{
		setLayout(new BorderLayout());

		// Set up the label
		JLabel ingredientsLabel = new JLabel("Ingredients");
		ingredientsLabel.setFont(new Font("Arial", Font.BOLD, 14));
		add(ingredientsLabel, BorderLayout.NORTH);

		// Set up the JList for ingredients
		_ingredientsView = new JList<>(ingredient.getIngredientsModel());
		_ingredientsView.setCellRenderer(new IngredientsListRenderer());

		// Add this to support item drop
		_ingredientsView.setDropMode(DropMode.INSERT);
		_ingredientsView.setTransferHandler(new ItemImportTransferHandler(ingredient, null, ItemType.INGREDIENT));

		// Add the JList to a scroll pane
		_ingredientsScrollPane = new JScrollPane(_ingredientsView);
		add(_ingredientsScrollPane, BorderLayout.CENTER);

		// Set up the context menu (JPopupMenu)
		JPopupMenu popupMenu = new JPopupMenu();

		// Remove whole ingredient
		JMenuItem removeItem = new JMenuItem("Remove Ingredient");
		removeItem.addActionListener(_ ->
		{
			ItemAmount selectedIngredient = _ingredientsView.getSelectedValue();
			if (selectedIngredient != null)
			{
				ingredient.removeIngredient(selectedIngredient, selectedIngredient.getAmount());
			}
		});

		// Remove specified amount of ingredient
		JMenuItem removeAmountItem = new JMenuItem("Remove Specific Amount");
		removeAmountItem.addActionListener(_ ->
		{
			ItemAmount selectedIngredient = _ingredientsView.getSelectedValue();
			if (selectedIngredient != null)
			{
				// Prompt the user to input the amount to remove
				Window parentWindow = SwingUtilities.getWindowAncestor(IngredientPanel.this);
				String input = JOptionPane.showInputDialog(parentWindow, "Enter amount to remove:");

				// Handle the case where the user cancels the input dialog (input == null).
				if (input == null)
				{
					return;
				}

				// Trim the input to avoid leading or trailing spaces.
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
					if (amountToRemove > 0 && amountToRemove <= selectedIngredient.getAmount())
					{
						ingredient.removeIngredient(selectedIngredient, amountToRemove);
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
		popupMenu.add(removeItem);
		popupMenu.add(removeAmountItem);

		// Add MouseListener to the JList to show the right-click menu
		_ingredientsView.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isRightMouseButton(e))
				{
					int index = _ingredientsView.locationToIndex(e.getPoint());
					if (index >= 0)
					{
						_ingredientsView.setSelectedIndex(index);
						popupMenu.show(_ingredientsView, e.getX(), e.getY());
					}
				}
			}
		});

		// Set the preferred size here.
		setPreferredSize(new Dimension(200, 200));
	}
}