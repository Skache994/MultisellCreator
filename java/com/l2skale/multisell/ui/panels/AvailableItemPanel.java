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
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.l2skale.multisell.managers.SettingsManager;
import com.l2skale.multisell.model.AvailableItemList;
import com.l2skale.multisell.model.Item;
import com.l2skale.multisell.ui.dnd.ItemExportTransferHandler;
import com.l2skale.multisell.ui.renders.ItemListRenderer;
import com.l2skale.multisell.ui.utils.ButtonFactory;
import com.l2skale.multisell.ui.utils.Fonts;
import com.l2skale.multisell.ui.utils.HintList;
import com.l2skale.multisell.ui.utils.ListContextMenu;
import com.l2skale.multisell.ui.utils.MessageUtils;
import com.l2skale.multisell.ui.utils.ResourceIcons;
import com.l2skale.multisell.ui.utils.Sound;
import com.l2skale.multisell.ui.utils.TextFields;

/*
 * @author Skache
 */
public class AvailableItemPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	// Empty-state hints: which one shows depends on whether any items are loaded at all.
	private static final String HINT_NO_ITEMS = "Click Load Items\nto get started";
	private static final String HINT_NO_MATCH = "No matching items";

	private final AvailableItemList _availableItemsList;
	private final Consumer<Item> _onAddIngredient;
	private final Consumer<Item> _onAddProduct;
	private JList<Item> _availableItemsView;
	private HintList<Item> _hintList;
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
		availableItemsLabel.setFont(Fonts.SECTION_TITLE);
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

		// Show the clear button only while there is search text.
		TextFields.onChange(_searchField, text -> clearButton.setVisible(!text.isEmpty()));

		northPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		northPanel.add(searchPanel);
		add(northPanel, BorderLayout.NORTH);

		// List with scroll pane
		final HintList<Item> list = new HintList<>(_availableItemsList.getModel());
		_hintList = list;
		_availableItemsView = list;
		_availableItemsView.setCellRenderer(new ItemListRenderer());

		// Fix the cell size so the list does NOT measure every row up front. Measuring a row renders
		// it, which decodes and caches that item's icon - with ~48k items (OrcVillage) that decoded
		// every icon on load and RAM shot to ~2GB. With a fixed size, only visible rows are rendered.
		// The width is intentionally small: a vertical list still paints each row at the full width.
		_availableItemsView.setFixedCellHeight(36);
		_availableItemsView.setFixedCellWidth(40);
		updateHint();

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

		JButton clearButton = ButtonFactory.createIconButton(clearIcon, _ ->
		{
			_searchField.setText("");
			resetFilter();
			_searchField.requestFocus();
		});
		clearButton.setPreferredSize(new Dimension(clearIcon.getIconWidth(), clearIcon.getIconHeight()));
		clearButton.setBorder(BorderFactory.createEmptyBorder());
		clearButton.setContentAreaFilled(false);
		clearButton.setFocusPainted(false);
		clearButton.setToolTipText("Clear search");
		return clearButton;
	}

	// Reset the list to show all items
	private void resetFilter()
	{
		_availableItemsView.setModel(_availableItemsList.getModel()); // Restore original model
		updateHint();
		_searchField.requestFocusInWindow(); // Keep focus on the search field
	}

	// Choose the right empty-state hint: if items are loaded, an empty list means the search matched
	// nothing; if none are loaded, it means the user still has to load them.
	private void updateHint()
	{
		final boolean hasItems = _availableItemsList.getModel().getSize() > 0;
		_hintList.setHint(hasItems ? HINT_NO_MATCH : HINT_NO_ITEMS);
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
		updateHint();
	}

	private void createContextMenuToAvailableItems()
	{
		ListContextMenu.install(_availableItemsView, (menu, item, _) ->
		{
			menu.item("Add as Ingredient", () -> _onAddIngredient.accept(item));
			menu.item("Add as Product", () -> _onAddProduct.accept(item));
			menu.separator();
			menu.item("View Info", () -> showItemInfo(item));
			menu.item("Export Icon...", () -> exportIcon(item));
		});
	}

	// Save the item's icon (as decoded from the client .utx) to a PNG file the user chooses.
	private void exportIcon(Item item)
	{
		if (item == null)
		{
			return;
		}

		final BufferedImage image = item.getIconImage();
		if (image == null)
		{
			MessageUtils.showInfoMessage(SwingUtilities.getWindowAncestor(this), "This item has no icon to export.", "No icon");
			return;
		}

		// Default to the icon's real asset name (e.g. weapon_sword_i00.png), falling back to the id.
		final String baseName = (item.getIconTextureName() != null) ? item.getIconTextureName() : String.valueOf(item.getId());

		// Reopen in the last folder an icon was exported to, so the user does not re-navigate each time.
		final String lastExport = SettingsManager.getLastExportPath();
		final File startDir = ((lastExport != null) && new File(lastExport).isDirectory()) ? new File(lastExport) : null;

		final JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Export icon as PNG");
		chooser.setSelectedFile((startDir != null) ? new File(startDir, baseName + ".png") : new File(baseName + ".png"));
		chooser.setFileFilter(new FileNameExtensionFilter("PNG image (*.png)", "png"));
		if (chooser.showSaveDialog(SwingUtilities.getWindowAncestor(this)) != JFileChooser.APPROVE_OPTION)
		{
			return;
		}

		File file = chooser.getSelectedFile();
		if (!file.getName().toLowerCase().endsWith(".png"))
		{
			file = new File(file.getParentFile(), file.getName() + ".png");
		}

		try
		{
			ImageIO.write(image, "png", file);
			SettingsManager.setLastExportPath(file.getParent());
			Sound.playSound("sys_enchant_success.wav");
			MessageUtils.showInfoMessage(SwingUtilities.getWindowAncestor(this), "Saved:\n" + file.getAbsolutePath(), "Icon exported");
		}
		catch (Exception e)
		{
			MessageUtils.showErrorMessage(SwingUtilities.getWindowAncestor(this), "Could not export icon:\n" + e.getMessage(), "Export failed");
		}
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
