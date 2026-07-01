package com.l2skale.multisell.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.l2skale.multisell.MultisellController;
import com.l2skale.multisell.MultisellCreator;
import com.l2skale.multisell.datapack.Datapack;
import com.l2skale.multisell.managers.ItemManager;
import com.l2skale.multisell.managers.ThemeManager;
import com.l2skale.multisell.model.AvailableItemList;
import com.l2skale.multisell.model.IngredientList;
import com.l2skale.multisell.model.Item;
import com.l2skale.multisell.model.MultisellList;
import com.l2skale.multisell.model.ProductList;
import com.l2skale.multisell.ui.panels.AvailableItemPanel;
import com.l2skale.multisell.ui.utils.ButtonFactory;
import com.l2skale.multisell.ui.utils.MessageUtils;

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
		themeButton.addActionListener(_ -> ThemeManager.toggleTheme(themeButton, _frame));

		// Panel for the theme toggle button.
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(themeButton, BorderLayout.EAST);
		_frame.add(topPanel, BorderLayout.NORTH);

		// Start empty - items are loaded when the user opens a datapack (File > Open Datapack).

		// Add the menu bar.
		JMenuBar menuBar = MenuBar.createMenuBar(_frame, this::openDatapack);
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
		buttonPanel.add(ButtonFactory.createButton("New Item", _ -> _controller.createNewItem(_frame)));

		// "Save" Button
		buttonPanel.add(ButtonFactory.createButton("Save", _ -> _controller.saveMultisell()));

		// "Clear" Button
		buttonPanel.add(ButtonFactory.createButton("Clear", _ -> _controller.clearLists()));

		_frame.add(buttonPanel, BorderLayout.SOUTH);
	}

	// (Re)load the available items from the given item and icon folders.
	private int loadItems(File itemsDir, File iconsDir)
	{
		_availableItemsList.clear();

		final ItemManager itemManager = new ItemManager(itemsDir.getPath(), iconsDir.getPath());
		itemManager.loadItems();
		for (Item item : itemManager.getAllItems().values())
		{
			_availableItemsList.addItem(item);
		}

		return itemManager.getAllItems().size();
	}

	// Prompt for a server datapack folder and load its items into the list.
	private void openDatapack()
	{
		final JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select server datapack folder");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (chooser.showOpenDialog(_frame) != JFileChooser.APPROVE_OPTION)
		{
			return;
		}

		final Datapack datapack = new Datapack(chooser.getSelectedFile());
		if (!datapack.isValid())
		{
			MessageUtils.showErrorMessage(_frame, "That folder is not a valid datapack.\nExpected to find: " + datapack.getItemsDir(), "Invalid datapack");
			return;
		}

		final int count = loadItems(datapack.getItemsDir(), new File(ICON_PATH));
		MessageUtils.showInfoMessage(_frame, "Loaded " + count + " items from:\n" + datapack, "Datapack loaded");
	}
}