package com.l2skale.multisell.ui.renders;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import com.l2skale.multisell.MultisellEntry;
import com.l2skale.multisell.model.Item;
import com.l2skale.multisell.model.ItemAmount;

/*
 * @author Skache
 */
@SuppressWarnings("serial")
public class MultisellEntryRenderer extends JPanel implements ListCellRenderer<MultisellEntry>
{

	private JLabel _label; // Label for product
	private JPanel _ingredientPanel; // Panel to show ingredients
	private JLabel _expandIcon; // Label for the expand/collapse icon
	private JDialog _ingredientDialog; // Popup dialog for ingredients

	public MultisellEntryRenderer()
	{
		setLayout(new BorderLayout());
		_label = new JLabel();
		_ingredientPanel = new JPanel();
		_ingredientPanel.setLayout(new BoxLayout(_ingredientPanel, BoxLayout.Y_AXIS));
		_ingredientPanel.setVisible(false); // Initially hidden

		// Create a label for the expand/collapse icon
		_expandIcon = new JLabel("+"); // Default collapsed icon ( + )
		_expandIcon.setFont(new Font("Arial", Font.BOLD, 18)); // Make the icon larger
		add(_expandIcon, BorderLayout.WEST); // Add the icon to the left of the label
		add(_label, BorderLayout.CENTER);
		add(_ingredientPanel, BorderLayout.SOUTH);

		// Initialize the ingredient dialog
		_ingredientDialog = new JDialog();
		_ingredientDialog.setTitle("Ingredients");
		_ingredientDialog.setSize(400, 300);
		_ingredientDialog.setLocationRelativeTo(null); // Center the dialog
		_ingredientDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends MultisellEntry> list, MultisellEntry multisellEntry, int index, boolean isSelected, boolean cellHasFocus)
	{
		StringBuilder displayText = new StringBuilder();

		// Set product name and icon
		Item productItem = multisellEntry.getFinalProducts().get(0).getItem();
		_label.setIcon(resizeIcon(productItem.getIcon(), 32, 32)); // Resize product icon to 32x32
		displayText.append("<html><b>").append(productItem.getName()).append("</b></html>") // Bold product name
				.append(" <i>(x: ").append(multisellEntry.getFinalProducts().get(0).getAmount()).append(")</i>"); // Italicized
																													// quantity
		_label.setText(displayText.toString());

		// Handle selection with better visual appearance
		if (isSelected)
		{
			setBackground(list.getSelectionBackground());
			_label.setForeground(list.getSelectionForeground());
		}
		else
		{
			setBackground(list.getBackground());
			_label.setForeground(list.getForeground());
		}

		// Add ActionListener to open the popup window when the expand icon is clicked
		_expandIcon.addMouseListener(new java.awt.event.MouseAdapter()
		{
			public void mousePressed(java.awt.event.MouseEvent evt)
			{
				openIngredientDialog(multisellEntry);
			}
		});

		return this;
	}

	// Helper method to resize icons
	private ImageIcon resizeIcon(ImageIcon icon, int width, int height)
	{
		Image img = icon.getImage(); // Get the image from the icon
		Image resizedImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH); // Resize the image
		return new ImageIcon(resizedImage); // Return the resized icon
	}

	// Open the dialog to show ingredients
	// Open the dialog to show ingredients
	public void openIngredientDialog(MultisellEntry entry)
	{
		// Create a panel to hold the ingredients
		JPanel ingredientPanel = new JPanel();
		ingredientPanel.setLayout(new BoxLayout(ingredientPanel, BoxLayout.Y_AXIS));

		// Add ingredients to the panel
		for (ItemAmount ingredient : entry.getIngredients())
		{
			// Create a label for each ingredient
			Item ingredientItem = ingredient.getItem();
			JLabel ingredientLabel = new JLabel(resizeIcon(ingredientItem.getIcon(), 16, 16)); // Resize ingredient icon
																								// to 16x16
			ingredientLabel.setText("<html>" + ingredientItem.getName() + " <i>(x: " + ingredient.getAmount() + ")</i></html>"); // Ingredient
			// name
			// and
			// quantity
			ingredientLabel.setFont(new Font("Arial", Font.PLAIN, 12)); // Set smaller font for ingredients
			ingredientPanel.add(ingredientLabel); // Add ingredient label with icon
		}

		// Add a scroll pane to make it scrollable
		JScrollPane scrollPane = new JScrollPane(ingredientPanel);
		scrollPane.setPreferredSize(new Dimension(350, 200));

		// Create the dialog and add components
		JDialog ingredientDialog = new JDialog();
		ingredientDialog.setTitle("Ingredients");
		ingredientDialog.setSize(400, 300);
		ingredientDialog.setLocationRelativeTo(null); // Center the dialog
		ingredientDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		ingredientDialog.getContentPane().removeAll(); // Clear previous components
		ingredientDialog.getContentPane().add(scrollPane, BorderLayout.CENTER);

		// Add a Close button
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(e -> ingredientDialog.dispose()); // Close the dialog
		ingredientDialog.getContentPane().add(closeButton, BorderLayout.SOUTH);

		// Show the dialog
		ingredientDialog.setVisible(true);
	}
}