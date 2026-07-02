package com.l2skale.multisell.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.l2skale.multisell.MultisellCreator;
import com.l2skale.multisell.data.MultisellLoader;
import com.l2skale.multisell.data.MultisellSaver;
import com.l2skale.multisell.datapack.Datapack;
import com.l2skale.multisell.managers.ItemManager;
import com.l2skale.multisell.managers.ThemeManager;
import com.l2skale.multisell.model.AvailableItemList;
import com.l2skale.multisell.model.Item;
import com.l2skale.multisell.model.multisell.Entry;
import com.l2skale.multisell.model.multisell.Multisell;
import com.l2skale.multisell.model.multisell.MultisellItem;
import com.l2skale.multisell.ui.panels.AvailableItemPanel;
import com.l2skale.multisell.ui.panels.MultisellSettingsPanel;
import com.l2skale.multisell.ui.panels.TrashBinPanel;
import com.l2skale.multisell.ui.utils.ButtonFactory;
import com.l2skale.multisell.ui.utils.DialogUtils;
import com.l2skale.multisell.ui.utils.MessageUtils;
import com.l2skale.multisell.ui.utils.Sound;

/*
 * author Skache
 */
public class Gui
{
	private final MultisellCreator _frame; // Main frame reference.
	private final AvailableItemList _availableItemsList = new AvailableItemList();
	private final MultisellSettingsPanel _settingsPanel = new MultisellSettingsPanel();

	private Datapack _datapack;
	private ItemManager _itemManager;
	private RightPanel _rightPanel;
	private Multisell _multisell;
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

		// Top strip: toolbar on the left, theme toggle on the right.
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(buildToolbar(), BorderLayout.WEST);
		topPanel.add(themeButton, BorderLayout.EAST);

		// North: theme row on top, the multisell settings bar below it.
		JPanel north = new JPanel(new BorderLayout());
		north.add(topPanel, BorderLayout.NORTH);
		north.add(_settingsPanel, BorderLayout.CENTER);
		_frame.add(north, BorderLayout.NORTH);

		// Start empty - items are loaded when the user opens a datapack (File > Open Datapack).

		// Add the menu bar.
		JMenuBar menuBar = MenuBar.createMenuBar(_frame, this::openDatapack, this::newMultisell, this::openMultisell, this::saveMultisell);
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
		_rightPanel.getIngredientsPanel().setOnEditAmount(this::editAmount);
		_rightPanel.getProductsPanel().setOnEditAmount(this::editAmount);
		_rightPanel.getEntriesPanel().enableEntryDrag();
		_rightPanel.getEntriesPanel().setOnDuplicate(this::duplicateEntry);
		_rightPanel.getEntriesPanel().setOnDelete(this::deleteEntry);

		// Set up the split pane
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, _rightPanel);
		splitPane.setDividerLocation(200);
		_frame.add(splitPane, BorderLayout.CENTER);
	}

	private JPanel buildToolbar()
	{
		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
		toolbar.add(ButtonFactory.createButton("Open Datapack", _ -> openDatapack()));
		toolbar.add(toolbarSeparator());
		toolbar.add(ButtonFactory.createButton("New", _ -> newMultisell()));
		toolbar.add(ButtonFactory.createButton("Open", _ -> openMultisell()));
		toolbar.add(ButtonFactory.createButton("Save", _ -> saveMultisell()));
		return toolbar;
	}

	private JComponent toolbarSeparator()
	{
		JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
		separator.setPreferredSize(new Dimension(2, 24));
		return separator;
	}

	private void addBottomBar()
	{
		// New Entry button (adds a blank trade to the current multisell).
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttons.add(ButtonFactory.createButton("New Entry", _ -> newEntry()));

		// Trash bin: drag an item or entry here to delete it.
		TrashBinPanel trashBin = new TrashBinPanel();
		trashBin.setOnDelete(this::deleteDragged);

		// Bottom bar: buttons in the middle, trash bin on the right.
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(buttons, BorderLayout.CENTER);
		southPanel.add(trashBin, BorderLayout.EAST);
		_frame.add(southPanel, BorderLayout.SOUTH);
	}

	// Load the available items off the UI thread so a big datapack does not freeze the app.
	private void loadItemsAsync(File itemsDir, File iconsDir, Datapack datapack)
	{
		_frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		new SwingWorker<Integer, Void>()
		{
			@Override
			protected Integer doInBackground()
			{
				_itemManager = new ItemManager(itemsDir.getPath(), iconsDir.getPath());
				_itemManager.loadItems();
				return _itemManager.getAllItems().size();
			}

			@Override
			protected void done()
			{
				try
				{
					_availableItemsList.clear();
					_availableItemsList.addItems(_itemManager.getAllItems().values());
					MessageUtils.showInfoMessage(_frame, "Loaded " + get() + " items from:\n" + datapack, "Datapack loaded");
				}
				catch (Exception e)
				{
					MessageUtils.showErrorMessage(_frame, "Could not load items:\n" + e.getMessage(), "Load failed");
				}
				finally
				{
					_frame.setCursor(Cursor.getDefaultCursor());
				}
			}
		}.execute();
	}

	// Prompt for a server datapack folder and load its items into the list.
	private void openDatapack()
	{
		final JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select the server 'game' folder (or its 'data' folder)");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (chooser.showOpenDialog(_frame) != JFileChooser.APPROVE_OPTION)
		{
			return;
		}

		final Datapack datapack = new Datapack(chooser.getSelectedFile());
		if (!datapack.isValid())
		{
			MessageUtils.showErrorMessage(_frame, "That folder is not a valid datapack.\nPick the server 'game' folder or its 'data' folder (must contain data/stats/items).", "Invalid datapack");
			return;
		}

		_datapack = datapack;
		loadItemsAsync(datapack.getItemsDir(), new File(ICON_PATH), datapack);
	}

	// Start a new, empty multisell (its id is chosen at save time).
	private void newMultisell()
	{
		if ((_datapack == null) || (_itemManager == null))
		{
			MessageUtils.showErrorMessage(_frame, "Open a datapack first (File > Open Datapack).", "No datapack");
			return;
		}

		_multisell = new Multisell(0);
		_rightPanel.getEntriesPanel().setMultisell(_multisell, _itemManager::getItemById);
		_settingsPanel.setMultisell(_multisell);
		showEntryInEditor(null);
		_frame.setTitle("Multisell XML Creator  -  new multisell");
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
			_multisell = MultisellLoader.load(chooser.getSelectedFile());
			_rightPanel.getEntriesPanel().setMultisell(_multisell, _itemManager::getItemById);
			_settingsPanel.setMultisell(_multisell);
			showEntryInEditor(null);
			_frame.setTitle("Multisell XML Creator  -  #" + _multisell.getId() + " (" + _multisell.getEntries().size() + " entries)");
		}
		catch (Exception e)
		{
			MessageUtils.showErrorMessage(_frame, "Could not load multisell:\n" + e.getMessage(), "Load failed");
		}
	}

	// Add a blank entry (trade) to the current multisell and select it.
	private void newEntry()
	{
		if (_multisell == null)
		{
			MessageUtils.showErrorMessage(_frame, "Create or open a multisell first (File menu).", "No multisell");
			return;
		}

		final Entry entry = new Entry();
		_multisell.getEntries().add(entry);
		_rightPanel.getEntriesPanel().addEntry(entry);
	}

	// Save the current multisell to the datapack, asking for its id (the file name number).
	private void saveMultisell()
	{
		if (_multisell == null)
		{
			MessageUtils.showErrorMessage(_frame, "Nothing to save - create or open a multisell first.", "Nothing to save");
			return;
		}

		final String suggested = _multisell.getId() > 0 ? String.valueOf(_multisell.getId()) : "";
		final String input = JOptionPane.showInputDialog(_frame, "Save as multisell id (number = file name):", suggested);
		if (input == null)
		{
			return;
		}

		final int id;
		try
		{
			id = Integer.parseInt(input.trim());
		}
		catch (NumberFormatException e)
		{
			MessageUtils.showErrorMessage(_frame, "The id must be a number.", "Invalid id");
			return;
		}

		_multisell.setId(id);
		final File dir = _datapack.getMultisellDir();
		dir.mkdirs();
		final File file = new File(dir, id + ".xml");

		if (file.exists())
		{
			final int choice = JOptionPane.showConfirmDialog(_frame, file.getName() + " already exists. Overwrite?", "File exists", JOptionPane.YES_NO_OPTION);
			if (choice != JOptionPane.YES_OPTION)
			{
				return;
			}
		}

		try
		{
			MultisellSaver.save(_multisell, file, _itemManager::getItemById);
			_settingsPanel.setMultisell(_multisell);
			_frame.setTitle("Multisell XML Creator  -  #" + id + " (" + _multisell.getEntries().size() + " entries)");
			MessageUtils.showInfoMessage(_frame, "Saved to:\n" + file.getAbsolutePath(), "Saved");
			Sound.playSound("sys_exchange_success.wav");
		}
		catch (Exception e)
		{
			MessageUtils.showErrorMessage(_frame, "Could not save:\n" + e.getMessage(), "Save failed");
		}
	}

	// Show the selected entry's ingredients and products in the editor panels.
	private void showEntryInEditor(Entry entry)
	{
		_selectedEntry = entry;
		if (entry == null)
		{
			_rightPanel.getIngredientsPanel().setHint("Select an entry to edit");
			_rightPanel.getProductsPanel().setHint("Select an entry to edit");
			_rightPanel.getIngredientsPanel().clearItems();
			_rightPanel.getProductsPanel().clearItems();
			return;
		}

		_rightPanel.getIngredientsPanel().setHint("Drag items here");
		_rightPanel.getProductsPanel().setHint("Drag items here");
		_rightPanel.getIngredientsPanel().setItems(entry.getIngredients(), _itemManager::getItemById);
		_rightPanel.getProductsPanel().setItems(entry.getProducts(), _itemManager::getItemById);
	}

	// Handle something dragged onto the trash bin: an item (remove from the selected
	// entry) or an entry (remove from the multisell).
	private void deleteDragged(Object dragged)
	{
		if (dragged instanceof MultisellItem multisellItem)
		{
			if (_selectedEntry != null)
			{
				_selectedEntry.getIngredients().remove(multisellItem);
				_selectedEntry.getProducts().remove(multisellItem);
				refreshAfterEdit();
			}
		}
		else if (dragged instanceof Entry entry)
		{
			deleteEntry(entry);
		}
	}

	// Change the amount of an item in the selected entry (double-click? Why not :D)
	private void editAmount(MultisellItem item)
	{
		final Item template = _itemManager == null ? null : _itemManager.getItemById(item.getItemId());
		final String name = template != null ? template.getName() : ("id " + item.getItemId());

		final Integer amount = DialogUtils.promptForAmount(_frame, name, item.getCount());
		if (amount == null)
		{
			return;
		}

		item.setCount(amount);
		refreshAfterEdit();
	}

	// Duplicate an entry (right-click) and select the copy.
	private void duplicateEntry(Entry entry)
	{
		if (_multisell == null)
		{
			return;
		}

		final Entry copy = entry.copy();
		final int index = _multisell.getEntries().indexOf(entry);
		if (index >= 0)
		{
			_multisell.getEntries().add(index + 1, copy);
		}
		else
		{
			_multisell.getEntries().add(copy);
		}

		_rightPanel.getEntriesPanel().setMultisell(_multisell, _itemManager::getItemById);
		_rightPanel.getEntriesPanel().selectEntry(copy);
	}

	// Remove an entry from the current multisell.
	private void deleteEntry(Entry entry)
	{
		if (_multisell == null)
		{
			return;
		}

		_multisell.getEntries().remove(entry);
		_rightPanel.getEntriesPanel().setMultisell(_multisell, _itemManager::getItemById);
		showEntryInEditor(null);
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
