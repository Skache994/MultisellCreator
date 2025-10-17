package com.l2skale.multisell.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.l2skale.multisell.MultisellController;
import com.l2skale.multisell.MultisellCreator;
import com.l2skale.multisell.managers.ItemManager;
import com.l2skale.multisell.managers.ThemeManager;
import com.l2skale.multisell.model.AvailableItemList;
import com.l2skale.multisell.model.IngredientList;
import com.l2skale.multisell.model.Item;
import com.l2skale.multisell.model.MultisellList;
import com.l2skale.multisell.model.ProductList;
import com.l2skale.multisell.ui.panels.AvailableItemPanel;
import com.l2skale.multisell.ui.utils.ButtonFactory;

/*
 * author Skache
 */
public class Gui
{
	private final MultisellCreator _frame; // Main frame reference.
	private final AvailableItemList _availableItemsList = new AvailableItemList();
	private final IngredientList _ingredientList = new IngredientList();
	private final ProductList _productList = new ProductList();
	private final MultisellList _multisellList = new MultisellList();
	private final MultisellController _controller;

	private static String ITEM_PATH = "data/items";
	private static String ICON_PATH = "data/icons";

	public Gui(MultisellCreator frame)
	{
		this._frame = frame;
		this._controller = new MultisellController(_ingredientList, _productList, _multisellList); // Initialize controller
		initialize();
	}

	public void initialize()
	{
		// Theme toggle button.
		JButton themeButton = new JButton("☀️  Light");
		themeButton.addActionListener(e -> ThemeManager.toggleTheme(themeButton, _frame));

		// Panel for the theme toggle button.
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(themeButton, BorderLayout.EAST);
		_frame.add(topPanel, BorderLayout.NORTH);

		// Item manager setup
		ItemManager itemManager = new ItemManager(ITEM_PATH, ICON_PATH);
		itemManager.loadItems();

		// Add items to the availableItemsList.
		for (Item item : itemManager.getAllItems().values())
		{
			_availableItemsList.addItem(item);
		}

		// Add the menu bar.
		JMenuBar menuBar = MenuBar.createMenuBar(_frame);
		_frame.setJMenuBar(menuBar);

		// Create the UI components.
		createDualPanelLayout();
		addButtons();
	}

	private void createDualPanelLayout()
	{
		// Left Panel: Available Items
		AvailableItemPanel leftPanel = new AvailableItemPanel(_availableItemsList, _ingredientList, _productList);

		// Right Panel: Ingredients, Products, Trash bin, and Multisell
		JPanel rightPanel = new RightPanel(_ingredientList, _productList, _multisellList);

		// Set up the split pane
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
		splitPane.setDividerLocation(200);
		_frame.add(splitPane, BorderLayout.CENTER);
	}

	private void addButtons()
	{
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		// "New Item" Button
		buttonPanel.add(ButtonFactory.createButton("New Item", e -> _controller.createNewItem(_frame)));

		// "Save" Button
		buttonPanel.add(ButtonFactory.createButton("Save", e -> _controller.saveMultisell()));

		// "Clear" Button
		buttonPanel.add(ButtonFactory.createButton("Clear", e -> _controller.clearLists()));

		_frame.add(buttonPanel, BorderLayout.SOUTH);
	}
}