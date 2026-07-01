package com.l2skale.multisell.ui;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.l2skale.multisell.MultisellCreator;
import com.l2skale.multisell.data.MultisellLoader;
import com.l2skale.multisell.datapack.Datapack;
import com.l2skale.multisell.managers.ItemManager;
import com.l2skale.multisell.managers.ThemeManager;
import com.l2skale.multisell.model.AvailableItemList;
import com.l2skale.multisell.model.Item;
import com.l2skale.multisell.model.multisell.Entry;
import com.l2skale.multisell.model.multisell.Multisell;
import com.l2skale.multisell.model.multisell.MultisellItem;
import com.l2skale.multisell.ui.panels.AvailableItemPanel;
import com.l2skale.multisell.ui.panels.TrashBinPanel;
import com.l2skale.multisell.ui.panels.popup.DialogUtils;
import com.l2skale.multisell.ui.utils.MessageUtils;
import com.l2skale.multisell.ui.utils.Sound;

/*
 * author Skache
 */
public class Gui
{
	private final MultisellCreator _frame; // Main frame reference.
	private final AvailableItemList _availableItemsList = new AvailableItemList();

	private Datapack _datapack;
	private ItemManager _itemManager;
	private RightPanel _rightPanel;
	private Entry _selectedEntry;

	private static String ICON_PATH = "data/icons";

	public Gui(MultisellCreator frame)
	{
		this._frame = frame;
		initialize();
	}

	public void initialize()
	{
		// Theme toggle button (sun/moon icon).
		JButton themeButton = new JButton();
		themeButton.addActionListener(_ -> ThemeManager.toggleTheme(themeButton, _frame));
		ThemeManager.updateThemeButton(themeButton);

		// Panel for the theme toggle button.
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(themeButton, BorderLayout.EAST);
		_frame.add(topPanel, BorderLayout.NORTH);

		// Start empty - items are loaded when the user opens a datapack (File > Open Datapack).

		// Add the menu bar.
		JMenuBar menuBar = MenuBar.createMenuBar(_frame, this::openDatapack, this::openMultisell);
		_frame.setJMenuBar(menuBar);

		// Create the UI components.
		createDualPanelLayout();
		addBottomBar();
	}

	private void createDualPanelLayout()
	{
		// Left Panel: Available Items
		AvailableItemPanel leftPanel = new AvailableItemPanel(_availableItemsList, item -> addItemToSelected(true, item), item -> addItemToSelected(false, item));

		// Right Panel: editor (Ingredients | Products) + the Entries list
		_rightPanel = new RightPanel();
		_rightPanel.getEntriesPanel().addSelectionListener(this::showEntryInEditor);
		_rightPanel.getIngredientsPanel().setOnRemove(item -> removeFromSelected(true, item));
		_rightPanel.getProductsPanel().setOnRemove(item -> removeFromSelected(false, item));
		_rightPanel.getIngredientsPanel().enableItemDrop(item -> addItemToSelected(true, item));
		_rightPanel.getProductsPanel().enableItemDrop(item -> addItemToSelected(false, item));

		// Set up the split pane
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, _rightPanel);
		splitPane.setDividerLocation(200);
		_frame.add(splitPane, BorderLayout.CENTER);
	}

	private void addBottomBar()
	{
		// Bottom bar: trash bin on the right (New Entry / Save come next).
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(new TrashBinPanel(), BorderLayout.EAST);
		_frame.add(southPanel, BorderLayout.SOUTH);
	}

	// (Re)load the available items from the given item and icon folders.
	private int loadItems(File itemsDir, File iconsDir)
	{
		_availableItemsList.clear();

		_itemManager = new ItemManager(itemsDir.getPath(), iconsDir.getPath());
		_itemManager.loadItems();
		for (Item item : _itemManager.getAllItems().values())
		{
			_availableItemsList.addItem(item);
		}

		return _itemManager.getAllItems().size();
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

		_datapack = datapack;
		final int count = loadItems(datapack.getItemsDir(), new File(ICON_PATH));
		MessageUtils.showInfoMessage(_frame, "Loaded " + count + " items from:\n" + datapack, "Datapack loaded");
	}

	// Prompt for a multisell XML file from the datapack and display its entries.
	private void openMultisell()
	{
		if ((_datapack == null) || (_itemManager == null))
		{
			MessageUtils.showErrorMessage(_frame, "Open a datapack first (File > Open Datapack).", "No datapack");
			return;
		}

		final JFileChooser chooser = new JFileChooser(_datapack.getMultisellDir());
		chooser.setDialogTitle("Open multisell XML");
		chooser.setFileFilter(new FileNameExtensionFilter("Multisell XML (*.xml)", "xml"));
		if (chooser.showOpenDialog(_frame) != JFileChooser.APPROVE_OPTION)
		{
			return;
		}

		try
		{
			final Multisell multisell = MultisellLoader.load(chooser.getSelectedFile());
			_rightPanel.getEntriesPanel().setMultisell(multisell, _itemManager::getItemById);
			_frame.setTitle("Multisell XML Creator  -  #" + multisell.getId() + " (" + multisell.getEntries().size() + " entries)");
		}
		catch (Exception e)
		{
			MessageUtils.showErrorMessage(_frame, "Could not load multisell:\n" + e.getMessage(), "Load failed");
		}
	}

	// Show the selected entry's ingredients and products in the editor panels.
	private void showEntryInEditor(Entry entry)
	{
		_selectedEntry = entry;
		if (entry == null)
		{
			_rightPanel.getIngredientsPanel().clearItems();
			_rightPanel.getProductsPanel().clearItems();
			return;
		}

		_rightPanel.getIngredientsPanel().setItems(entry.getIngredients(), _itemManager::getItemById);
		_rightPanel.getProductsPanel().setItems(entry.getProducts(), _itemManager::getItemById);
	}

	// Remove an item from the selected entry's ingredient or product list.
	private void removeFromSelected(boolean ingredient, MultisellItem item)
	{
		if (_selectedEntry == null)
		{
			return;
		}

		if (ingredient)
		{
			_selectedEntry.getIngredients().remove(item);
		}
		else
		{
			_selectedEntry.getProducts().remove(item);
		}

		refreshAfterEdit();
	}

	// Add an item (asking for an amount) as an ingredient or product of the selected entry.
	private void addItemToSelected(boolean ingredient, Item item)
	{
		if (_selectedEntry == null)
		{
			MessageUtils.showErrorMessage(_frame, "Select an entry first.", "No entry selected");
			return;
		}

		final Integer amount = DialogUtils.promptForAmount(_frame, item.getName());
		if (amount == null)
		{
			return;
		}

		final MultisellItem multisellItem = new MultisellItem(item.getId(), amount);
		if (ingredient)
		{
			_selectedEntry.getIngredients().add(multisellItem);
		}
		else
		{
			_selectedEntry.getProducts().add(multisellItem);
		}

		Sound.playSound("item_drop_equip_armor_shield.wav");
		refreshAfterEdit();
	}

	// Redraw the editor panels and the changed entry row after an edit.
	private void refreshAfterEdit()
	{
		if (_selectedEntry == null)
		{
			return;
		}

		_rightPanel.getIngredientsPanel().setItems(_selectedEntry.getIngredients(), _itemManager::getItemById);
		_rightPanel.getProductsPanel().setItems(_selectedEntry.getProducts(), _itemManager::getItemById);
		_rightPanel.getEntriesPanel().refreshEntry(_selectedEntry);
	}
}
