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
package com.l2skale.multisell.ui.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.l2skale.multisell.managers.ThemeManager;
import com.l2skale.multisell.model.AvailableItemList;
import com.l2skale.multisell.model.Item;
import com.l2skale.multisell.ui.dnd.ItemExportTransferHandler;
import com.l2skale.multisell.ui.renders.ItemListRenderer;
import com.l2skale.multisell.ui.utils.HintList;
import com.l2skale.multisell.ui.utils.ContextMenuStyler;
import com.l2skale.multisell.ui.utils.MessageUtils;
import com.l2skale.multisell.ui.utils.ResourceIcons;
import com.l2skale.multisell.ui.utils.Sound;

/*
 * @author Skache
 */
public class AvailableItemPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final AvailableItemList _availableItemsList;
	private final Consumer<Item> _onAddIngredient;
	private final Consumer<Item> _onAddProduct;
	private JList<Item> _availableItemsView;
	private JTextField _searchField;

	public AvailableItemPanel(AvailableItemList item, Consumer<Item> onAddIngredient, Consumer<Item> onAddProduct)
	{
		this._availableItemsList = item;
		this._onAddIngredient = onAddIngredient;
		this._onAddProduct = onAddProduct;
		initialize();
		createContextMenuToAvailableItems();
	}

	private void initialize()
	{
		setLayout(new BorderLayout());

		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

		// Add available items label
		JLabel availableItemsLabel = new JLabel("Search Available Items");
		availableItemsLabel.setFont(new Font("Arial", Font.BOLD, 14));
		availableItemsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		northPanel.add(availableItemsLabel);

		// Create a panel to hold the search field and clear button with BorderLayout
		JPanel searchPanel = new JPanel(new BorderLayout());
		searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// Set maximum size to prevent vertical expansion
		searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, new JTextField().getPreferredSize().height));

		// Add search field (takes all available width)
		_searchField = new JTextField();
		_searchField.setToolTipText("Search items by name or ID (Press ESC to clear)");
		_searchField.addActionListener(this::filterItems);

		// Set preferred height for the search field (adjust the 30 value as needed)
		Dimension searchFieldSize = _searchField.getPreferredSize();
		searchFieldSize.height = 25; // Increase height
		_searchField.setPreferredSize(searchFieldSize);

		searchPanel.add(_searchField, BorderLayout.CENTER);

		// Set maximum size for the search panel to match the new height
		searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, searchFieldSize.height));

		// Create clear button
		JButton clearButton = createClearButton();

		// Create a fixed-size container that will always occupy the button's space
		JPanel buttonContainer = new JPanel(new BorderLayout());
		buttonContainer.setOpaque(false);
		buttonContainer.setPreferredSize(new Dimension(clearButton.getPreferredSize().width + 5, _searchField.getPreferredSize().height));

		// Add the button to the container
		buttonContainer.add(clearButton, BorderLayout.CENTER);

		// Initially hide the button
		clearButton.setVisible(false);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		buttonPanel.setOpaque(false);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5)); // Right padding
		buttonPanel.add(buttonContainer);
		searchPanel.add(buttonPanel, BorderLayout.EAST);

		// ESC key binding
		_searchField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clearSearch");
		_searchField.getActionMap().put("clearSearch", new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (clearButton.isVisible())
				{
					clearButton.doClick();
				}
			}
		});

		// Document listener
		_searchField.getDocument().addDocumentListener(new DocumentListener()
		{
			public void insertUpdate(DocumentEvent e)
			{
				update();
			}

			public void removeUpdate(DocumentEvent e)
			{
				update();
			}

			public void changedUpdate(DocumentEvent e)
			{
				update();
			}

			private void update()
			{
				boolean hasText = _searchField.getText().length() > 0;
				clearButton.setVisible(hasText);
			}
		});

		northPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		northPanel.add(searchPanel);
		add(northPanel, BorderLayout.NORTH);

		// List with scroll pane
		final HintList<Item> list = new HintList<>(_availableItemsList.getModel());
		list.setHint("Open a datapack\nto load items");
		_availableItemsView = list;
		_availableItemsView.setCellRenderer(new ItemListRenderer());

		// Setup drag
		_availableItemsView.setDragEnabled(true);
		_availableItemsView.setTransferHandler(new ItemExportTransferHandler());

		JScrollPane scrollPane = new JScrollPane(_availableItemsView);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		add(scrollPane, BorderLayout.CENTER);
	}

	private JButton createClearButton()
	{
		ImageIcon clearIcon = ResourceIcons.loadResourceIconsIcon("delete_button.png");

		if (clearIcon != null)
		{
			// Scale down the icon if needed.
			Image img = clearIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
			clearIcon = new ImageIcon(img);
		}
		else
		{
			System.err.println("Failed to load delete_button.png");
		}

		JButton clearButton = new JButton(clearIcon);
		clearButton.setPreferredSize(new Dimension(clearIcon.getIconWidth(), clearIcon.getIconHeight()));
		clearButton.setBorder(BorderFactory.createEmptyBorder());
		clearButton.setContentAreaFilled(false);
		clearButton.setFocusPainted(false);
		clearButton.setToolTipText("Clear search");
		clearButton.addActionListener(_ ->
		{
			_searchField.setText("");
			resetFilter();
			_searchField.requestFocus();
		});
		return clearButton;
	}

	// Reset the list to show all items
	private void resetFilter()
	{
		_availableItemsView.setModel(_availableItemsList.getModel()); // Restore original model
		_searchField.requestFocusInWindow(); // Keep focus on the search field
	}

	// Method to filter items based on search input
	private void filterItems(ActionEvent e)
	{
		String query = _searchField.getText().toLowerCase(); // Get the search query
		DefaultListModel<Item> filteredModel = new DefaultListModel<>(); // Create a new model for the filtered items

		// Loop through the available items and add matching ones to the filteredModel
		for (int i = 0; i < _availableItemsList.getModel().size(); i++)
		{
			Item item = _availableItemsList.getModel().getElementAt(i);
			if (item.getName().toLowerCase().contains(query) || String.valueOf(item.getId()).contains(query))
			{
				filteredModel.addElement(item);
			}
		}

		// Update the availableItemsList with the filtered model
		_availableItemsView.setModel(filteredModel);
	}

	private void createContextMenuToAvailableItems()
	{
		_availableItemsView.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				showContextMenu(e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				showContextMenu(e);
			}

			private void showContextMenu(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					int index = _availableItemsView.locationToIndex(e.getPoint());
					if (index != -1)
					{
						_availableItemsView.setSelectedIndex(index);

						// Recreate the context menu on demand
						boolean isDarkTheme = ThemeManager.getCurrentTheme();
						ContextMenuStyler menuStyler = new ContextMenuStyler(isDarkTheme);
						JPopupMenu itemMenu = new JPopupMenu();

						// Add "Add as Ingredient"
						JMenuItem addIngredientItem = new JMenuItem("Add as Ingredient");
						menuStyler.styleMenuItem(addIngredientItem);
						addIngredientItem.addActionListener(_ ->
						{
							Item selectedItem = _availableItemsView.getSelectedValue();
							if (selectedItem != null)
							{
								_onAddIngredient.accept(selectedItem);
							}
						});
						itemMenu.add(addIngredientItem);

						// Add "Add as Product"
						JMenuItem addProductItem = new JMenuItem("Add as Product");
						menuStyler.styleMenuItem(addProductItem);
						addProductItem.addActionListener(_ ->
						{
							Item selectedItem = _availableItemsView.getSelectedValue();
							if (selectedItem != null)
							{
								_onAddProduct.accept(selectedItem);
							}
						});
						itemMenu.add(addProductItem);

						// Add "View Info"
						JMenuItem viewInfoItem = new JMenuItem("View Info");
						menuStyler.styleMenuItem(viewInfoItem);
						viewInfoItem.addActionListener(_ -> showItemInfo(_availableItemsView.getSelectedValue()));
						itemMenu.add(viewInfoItem);

						itemMenu.show(_availableItemsView, e.getX(), e.getY());
					}
				}
			}
		});
	}

	private void showItemInfo(Item item)
	{
		if (item != null)
		{
			String questStatus = item.isQuestItem() ? "Yes" : "No";

			ImageIcon itemIcon = item.getIcon();

			// HTML formatting for better presentation.
			String itemInfo = String.format("<html><b>Item Name:</b> %s<br>" + "<b>ID:</b> %d<br>" + "<b>Type:</b> %s<br>" + "<b>Quest Item:</b> %s</html>", item.getName(), item.getId(), item.getType(), questStatus);
			final JLabel label = new JLabel(itemInfo);
			label.setFont(new Font("Arial", Font.PLAIN, 13));

			Sound.playSound("window_open.wav");
			MessageUtils.showInfoMessage(SwingUtilities.getWindowAncestor(this), label, "Item Information", itemIcon);
		}
	}
}
