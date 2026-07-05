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
package com.l2skale.multisell.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.l2skale.multisell.MultisellCreator;
import com.l2skale.multisell.client.TextureProvider;
import com.l2skale.multisell.data.MultisellLoader;
import com.l2skale.multisell.data.MultisellSaver;
import com.l2skale.multisell.data.MultisellSchemaLoader;
import com.l2skale.multisell.datapack.Datapack;
import com.l2skale.multisell.managers.ItemManager;
import com.l2skale.multisell.managers.NpcManager;
import com.l2skale.multisell.managers.SettingsManager;
import com.l2skale.multisell.managers.ThemeManager;
import com.l2skale.multisell.model.AvailableItemList;
import com.l2skale.multisell.model.Item;
import com.l2skale.multisell.model.multisell.Entry;
import com.l2skale.multisell.model.multisell.Multisell;
import com.l2skale.multisell.model.multisell.MultisellItem;
import com.l2skale.multisell.model.multisell.MultisellSchema;
import com.l2skale.multisell.model.multisell.SchemaAttribute;
import com.l2skale.multisell.ui.dialogs.LineEditorDialog;
import com.l2skale.multisell.ui.panels.AvailableItemPanel;
import com.l2skale.multisell.ui.panels.MultisellSettingsPanel;
import com.l2skale.multisell.ui.panels.TrashBinPanel;
import com.l2skale.multisell.ui.utils.AttentionPulse;
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
	private NpcManager _npcManager;
	private MultisellSchema _multisellSchema; // The loaded pack's own multisell rules (from its xsd); null if the pack has no xsd or it could not be read.
	private RightPanel _rightPanel;
	private JSplitPane _splitPane;
	private Multisell _multisell;
	private Entry _selectedEntry;
	private File _multisellDir; // Folder the current multisell was opened from; save writes back here so a file from custom/ stays in custom/. Null for a new multisell.
	private AttentionPulse _loadItemsPulse; // Gently glows the Load Items button until items are loaded.
	private JLabel _packLabel; // Shows which datapack is currently loaded (e.g. its folder name).

	public Gui(MultisellCreator frame)
	{
		this._frame = frame;
		initialize();
	}

	public void initialize()
	{
		// Icons come from the bundled default Icon.utx out of the box. If the user pointed at a full
		// client folder before, restore it so its extra packages (BranchIcon, BranchSys...) load too.
		final String texturesPath = SettingsManager.getLastTexturesPath();
		if (texturesPath != null)
		{
			final File texturesDir = new File(texturesPath);
			if (texturesDir.isDirectory())
			{
				TextureProvider.setFolder(texturesDir);
			}
		}

		// Theme toggle button (sun/moon icon). The icon is set by ThemeManager just below.
		JButton themeButton = ButtonFactory.createIconButton(null, event ->
		{
			ThemeManager.toggleTheme((JButton) event.getSource(), _frame);
			applyThemeBorder();
		});
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

		// Start empty - items are loaded when the user loads a datapack (Load Items).

		// Add the menu bar.
		JMenuBar menuBar = MenuBar.createMenuBar(_frame, this::openDatapack, this::newMultisell, this::openMultisell, this::saveMultisell, this::deleteMultisell);
		_frame.setJMenuBar(menuBar);

		// Create the UI components.
		createDualPanelLayout();
		addBottomBar();

		// App starts empty: gently glow Load Items so it is obvious where to begin. Stops once loaded.
		_loadItemsPulse.start();
	}

	private void createDualPanelLayout()
	{
		// Left Panel: Available Items
		AvailableItemPanel leftPanel = new AvailableItemPanel(_availableItemsList, item -> addItemToSelected(true, item), item -> addItemToSelected(false, item));

		// Right Panel: editor (Ingredients | Products) + the Entries list
		_rightPanel = new RightPanel();
		_rightPanel.getEntriesPanel().addSelectionListener(this::showEntryInEditor);
		_rightPanel.getIngredientsPanel().setOnRemove(this::delete);
		_rightPanel.getProductsPanel().setOnRemove(this::delete);
		_rightPanel.getIngredientsPanel().enableItemDrop(item -> addItemToSelected(true, item), (from, to) -> reorderItem(true, from, to));
		_rightPanel.getProductsPanel().enableItemDrop(item -> addItemToSelected(false, item), (from, to) -> reorderItem(false, from, to));
		_rightPanel.getIngredientsPanel().setOnEdit(item -> editLine(item, true));
		_rightPanel.getProductsPanel().setOnEdit(item -> editLine(item, false));
		_rightPanel.getIngredientsPanel().setOnMoveUp(item -> moveItem(true, item, -1));
		_rightPanel.getIngredientsPanel().setOnMoveDown(item -> moveItem(true, item, 1));
		_rightPanel.getProductsPanel().setOnMoveUp(item -> moveItem(false, item, -1));
		_rightPanel.getProductsPanel().setOnMoveDown(item -> moveItem(false, item, 1));
		_rightPanel.getEntriesPanel().enableEntryDrag((from, to) -> reorderEntry(from, to));
		_rightPanel.getEntriesPanel().setOnDuplicate(this::duplicateEntry);
		_rightPanel.getEntriesPanel().setOnDelete(this::delete);
		_rightPanel.getEntriesPanel().setOnMoveUp(entry -> moveEntry(entry, -1));
		_rightPanel.getEntriesPanel().setOnMoveDown(entry -> moveEntry(entry, 1));

		// Set up the split pane (holds the 4 lists) with an explicit outer border.
		_splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, _rightPanel);
		_splitPane.setDividerLocation(200);
		applyThemeBorder();
		_frame.add(_splitPane, BorderLayout.CENTER);
	}

	// Border around the split pane (the box around the 4 lists), re-applied on theme
	// change because Nimbus does not reliably refresh its own border color.
	private void applyThemeBorder()
	{
		if (_splitPane == null)
		{
			return;
		}

		final Color color = ThemeManager.isDarkMode() ? new Color(170, 170, 170) : new Color(120, 120, 120);
		_splitPane.setBorder(BorderFactory.createLineBorder(color));
	}

	private JPanel buildToolbar()
	{
		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));

		// The two buttons that get the app working: load the server's items, then (optionally) icons.
		final JButton loadItems = ButtonFactory.createButton("Load Items", _ -> openDatapack());
		loadItems.setToolTipText("Load the server's items - point at the datapack ('game' folder or its 'data' folder).");
		_loadItemsPulse = new AttentionPulse(loadItems, new Color(255, 180, 40)); // amber, reads in both themes
		toolbar.add(loadItems);

		final JButton loadIcons = ButtonFactory.createButton("Load Icons", _ -> openTexturesFolder());
		loadIcons.setToolTipText("Optional - only if some icons are missing. Just point at your Lineage 2 game folder.");
		toolbar.add(loadIcons);

		toolbar.add(toolbarSeparator());
		toolbar.add(ButtonFactory.createButton("New", _ -> newMultisell()));
		toolbar.add(ButtonFactory.createButton("Open", _ -> openMultisell()));
		toolbar.add(ButtonFactory.createButton("Save", _ -> saveMultisell()));
		toolbar.add(ButtonFactory.createDangerButton("Delete", _ -> deleteMultisell()));

		// Shows the loaded datapack so it is always clear which pack (Interlude, OrcVillage, ...) is active.
		toolbar.add(toolbarSeparator());
		_packLabel = new JLabel("No pack loaded");
		toolbar.add(_packLabel);

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
		trashBin.setOnDelete(this::delete);

		// Bottom bar: buttons in the middle, trash bin on the right.
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(buttons, BorderLayout.CENTER);
		southPanel.add(trashBin, BorderLayout.EAST);
		_frame.add(southPanel, BorderLayout.SOUTH);
	}

	// Load the available items off the UI thread so a big datapack does not freeze the app.
	private void loadItemsAsync(File itemsDir, Datapack datapack)
	{
		_frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		new SwingWorker<Integer, Void>()
		{
			@Override
			protected Integer doInBackground()
			{
				_itemManager = new ItemManager(itemsDir.getPath());
				_itemManager.loadItems();

				// NPC names ride along on the same background load - a tiny id -> name map used to
				// label the npcs a multisell is attached to (in the UI and as save comments).
				_npcManager = new NpcManager(datapack.getNpcsDir().getPath());
				_npcManager.loadNpcNames();

				// This pack's own multisell rules, read from its data/xsd/multisell.xsd. It defines
				// which attributes are legal on this server; the UI/output will be driven from it.
				_multisellSchema = loadMultisellSchema(datapack);

				return _itemManager.getAllItems().size();
			}

			@Override
			protected void done()
			{
				try
				{
					_availableItemsList.clear();
					_availableItemsList.addItems(_itemManager.getAllItems().values());
					_settingsPanel.setNpcLookups(_npcManager::getNpcName, _npcManager::isCustomNpc); // names + custom flags for the editor
					_settingsPanel.setSchema(_multisellSchema); // list-option controls now come from this pack's xsd

					// A multisell belongs to the pack it came from (its items, npcs and xsd), so switching
					// packs discards the open one - otherwise an Interlude list would linger on an OrcVillage pack.
					clearMultisell();
					_packLabel.setText("Pack: " + packDisplayName(datapack.getRoot()));

					_loadItemsPulse.stop(); // items are in - the hint has done its job
					Sound.playSound("inventory_open_01.wav");
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

	// A meaningful name for the loaded pack. The user usually picks the "game" folder (or its
	// "data"), whose name says nothing, so we walk up past those generic wrappers to the project
	// folder - e.g. .../L2J_Mobius_CT_0_Interlude/dist/game -> "L2J_Mobius_CT_0_Interlude".
	private static String packDisplayName(File root)
	{
		final Set<String> generic = Set.of("game", "data", "dist");
		File dir = root;
		while ((dir != null) && generic.contains(dir.getName().toLowerCase()))
		{
			dir = dir.getParentFile();
		}
		return (dir != null) && !dir.getName().isEmpty() ? dir.getName() : root.getName();
	}

	// Read the pack's data/xsd/multisell.xsd into a schema of allowed attributes. A missing or
	// unreadable xsd is not fatal - we just return null and carry on (the editor keeps working),
	// because the xsd only drives what the UI offers, not whether the app runs.
	private static MultisellSchema loadMultisellSchema(Datapack datapack)
	{
		final File xsd = datapack.getMultisellXsd();
		if (!xsd.isFile())
		{
			// System.out.println("[multisell.xsd] not found at " + xsd + " - editor will use its built-in defaults.");
			return null;
		}

		try
		{
			final MultisellSchema schema = MultisellSchemaLoader.load(xsd);
			// System.out.println("[multisell.xsd] " + xsd);
			// System.out.println("[multisell.xsd]   list:       " + schema.getListAttributes());
			// System.out.println("[multisell.xsd]   ingredient: " + schema.getIngredientAttributes());
			// System.out.println("[multisell.xsd]   production: " + schema.getProductionAttributes());
			return schema;
		}
		catch (Exception e)
		{
			// System.out.println("[multisell.xsd] could not read " + xsd + ": " + e.getMessage());
			return null;
		}
	}

	// Prompt for the Lineage 2 game folder and load icons from it. The user only picks the game
	// folder; the textures folder inside it is found automatically (see resolveTexturesDir).
	private void openTexturesFolder()
	{
		final JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select your Lineage 2 game folder");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		final String lastPath = SettingsManager.getLastTexturesPath();
		if (lastPath != null)
		{
			final File lastDir = new File(lastPath);
			if (lastDir.isDirectory())
			{
				chooser.setCurrentDirectory(lastDir);
			}
		}

		if (chooser.showOpenDialog(_frame) != JFileChooser.APPROVE_OPTION)
		{
			return;
		}

		final File gameFolder = chooser.getSelectedFile();
		final File texturesDir = resolveTexturesDir(gameFolder);
		if (texturesDir == null)
		{
			MessageUtils.showErrorMessage(_frame, "No client icons were found in:\n" + gameFolder + "\n\nPick your Lineage 2 game folder (the one that contains the 'system' folder).", "Icons not found");
			return;
		}

		SettingsManager.setLastTexturesPath(gameFolder.getAbsolutePath());
		TextureProvider.setFolder(texturesDir);
		reloadIcons();
	}

	// Finds the client textures folder inside the chosen game folder. Its name varies by chronicle
	// (OrcVillage "SysTextures", Interlude "systextures"), so it is matched case-insensitively. If
	// the user already picked a folder that holds .utx files, that folder is used as-is.
	private static File resolveTexturesDir(File gameFolder)
	{
		if ((gameFolder == null) || !gameFolder.isDirectory())
		{
			return null;
		}

		if (hasUtxFiles(gameFolder))
		{
			return gameFolder;
		}

		final File[] matches = gameFolder.listFiles((_, name) -> name.equalsIgnoreCase("SysTextures"));
		if (matches != null)
		{
			for (File match : matches)
			{
				if (match.isDirectory() && hasUtxFiles(match))
				{
					return match;
				}
			}
		}
		return null;
	}

	private static boolean hasUtxFiles(File folder)
	{
		final File[] utx = folder.listFiles((_, name) -> name.toLowerCase().endsWith(".utx"));
		return (utx != null) && (utx.length > 0);
	}

	// Drop every item's cached icon and repaint, so icons reload from the new source.
	private void reloadIcons()
	{
		if (_itemManager != null)
		{
			for (Item item : _itemManager.getAllItems().values())
			{
				item.resetIcon();
			}
		}
		_frame.repaint();
	}

	// Prompt for a server datapack folder and load its items into the list.
	private void openDatapack()
	{
		final JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select the server 'game' folder (or its 'data' folder)");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		// Start the chooser at the last opened datapack, if we have one.
		final String lastPath = SettingsManager.getLastDatapackPath();
		if (lastPath != null)
		{
			final File lastDir = new File(lastPath);
			if (lastDir.isDirectory())
			{
				chooser.setCurrentDirectory(lastDir);
			}
		}

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

		// Remember this datapack for next launch.
		SettingsManager.setLastDatapackPath(chooser.getSelectedFile().getAbsolutePath());

		_datapack = datapack;
		loadItemsAsync(datapack.getItemsDir(), datapack);
	}

	// Start a new, empty multisell (its id is chosen at save time).
	private void newMultisell()
	{
		if ((_datapack == null) || (_itemManager == null))
		{
			MessageUtils.showErrorMessage(_frame, "Load items first (click Load Items).", "No items loaded");
			return;
		}

		_multisell = new Multisell(""); // Name is set when it is first saved.
		_multisellDir = null; // New multisell has no source folder yet; save falls back to the datapack's multisell dir.
		_rightPanel.getEntriesPanel().setMultisell(_multisell, _itemManager::getItemById);
		_settingsPanel.setMultisell(_multisell);
		showEntryInEditor(null);
		_frame.setTitle("Multisell XML Creator  -  new multisell");
		Sound.playSound("quest_accept.wav");
	}

	// Prompt for a multisell XML file from the datapack and display its entries.
	private void openMultisell()
	{
		if ((_datapack == null) || (_itemManager == null))
		{
			MessageUtils.showErrorMessage(_frame, "Load items first (click Load Items).", "No items loaded");
			return;
		}

		// Start at the last folder a multisell was opened from, else the datapack's multisell dir.
		File startDir = _datapack.getMultisellDir();
		final String lastMultisell = SettingsManager.getLastMultisellPath();
		if (lastMultisell != null)
		{
			final File lastDir = new File(lastMultisell);
			if (lastDir.isDirectory())
			{
				startDir = lastDir;
			}
		}

		final JFileChooser chooser = new JFileChooser(startDir);
		chooser.setDialogTitle("Open multisell XML");
		chooser.setFileFilter(new FileNameExtensionFilter("Multisell XML (*.xml)", "xml"));
		if (chooser.showOpenDialog(_frame) != JFileChooser.APPROVE_OPTION)
		{
			return;
		}

		// Remember the folder for next time.
		SettingsManager.setLastMultisellPath(chooser.getSelectedFile().getParent());

		try
		{
			_multisell = MultisellLoader.load(chooser.getSelectedFile());
			_multisellDir = chooser.getSelectedFile().getParentFile(); // Remember the source folder (e.g. custom/) so Save writes back here.
			_rightPanel.getEntriesPanel().setMultisell(_multisell, _itemManager::getItemById);
			_settingsPanel.setMultisell(_multisell);
			showEntryInEditor(null);
			_frame.setTitle("Multisell XML Creator  -  " + _multisell.getId() + " (" + _multisell.getEntries().size() + " entries)");
			Sound.playSound("metal_door_open_01.wav"); // old metal door creak for opening an existing multisell
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
		Sound.playSound("quest_itemget.wav");
	}

	// Save the current multisell to the datapack, asking for its id (the file name number).
	private void saveMultisell()
	{
		if (_multisell == null)
		{
			MessageUtils.showErrorMessage(_frame, "Nothing to save - create or open a multisell first.", "Nothing to save");
			return;
		}

		final String input = JOptionPane.showInputDialog(_frame, "Save as multisell id (number = file name):", _multisell.getId());
		if (input == null)
		{
			return;
		}

		// The id IS the file name (server requires a plain number, e.g. 001.xml), so keep it verbatim.
		final String id = input.trim();
		if (!id.matches("\\d+"))
		{
			MessageUtils.showErrorMessage(_frame, "The id must be a number (it becomes the file name, e.g. 001).", "Invalid id");
			return;
		}

		_multisell.setId(id);
		// Save back to the folder the multisell was opened from (keeps custom/ files in custom/); new multisells go to the datapack's multisell dir.
		final File dir = (_multisellDir != null) ? _multisellDir : _datapack.getMultisellDir();
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
			MultisellSaver.save(_multisell, file, _itemManager::getItemById, _npcManager == null ? null : _npcManager::getNpcName);
			_multisellDir = file.getParentFile();
			_settingsPanel.setMultisell(_multisell);
			_frame.setTitle("Multisell XML Creator  -  " + id + " (" + _multisell.getEntries().size() + " entries)");
			MessageUtils.showInfoMessage(_frame, "Saved to:\n" + file.getAbsolutePath(), "Saved");
			Sound.playSound("quest_finish.wav");
		}
		catch (Exception e)
		{
			MessageUtils.showErrorMessage(_frame, "Could not save:\n" + e.getMessage(), "Save failed");
		}
	}

	// Delete the currently open multisell's XML file (with confirmation), then clear the editor.
	private void deleteMultisell()
	{
		if (_multisell == null)
		{
			MessageUtils.showErrorMessage(_frame, "Nothing to delete - create or open a multisell first.", "Nothing to delete");
			return;
		}

		// The file this multisell maps to: its id is the file name. A new/unsaved one has no id yet.
		final File dir = (_multisellDir != null) ? _multisellDir : (_datapack != null ? _datapack.getMultisellDir() : null);
		final File file = (!_multisell.getId().isEmpty() && (dir != null)) ? new File(dir, _multisell.getId() + ".xml") : null;

		if ((file == null) || !file.exists())
		{
			final int discard = JOptionPane.showConfirmDialog(_frame, "This multisell has not been saved yet. Discard it?", "Discard multisell", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (discard == JOptionPane.YES_OPTION)
			{
				clearMultisell();
				Sound.playSound("quest_giveup.wav");
			}
			return;
		}

		final int choice = JOptionPane.showConfirmDialog(_frame, "Delete " + file.getName() + "?\nThis cannot be undone.", "Delete multisell", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (choice != JOptionPane.YES_OPTION)
		{
			return;
		}

		if (file.delete())
		{
			Sound.playSound("trash_basket.wav");
			clearMultisell();
			MessageUtils.showInfoMessage(_frame, "Deleted:\n" + file.getAbsolutePath(), "Deleted");
		}
		else
		{
			MessageUtils.showErrorMessage(_frame, "Could not delete:\n" + file.getAbsolutePath(), "Delete failed");
		}
	}

	// Return to the empty "no multisell open" state.
	private void clearMultisell()
	{
		_multisell = null;
		_multisellDir = null;
		_rightPanel.getEntriesPanel().clear();
		_settingsPanel.setMultisell(null);
		showEntryInEditor(null); // also resets _selectedEntry to null
		_frame.setTitle("Multisell XML Creator");
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

	// The single delete path for the whole app: removes an item from the selected
	// entry, or an entry from the multisell, then plays the delete sound. Every
	// delete trigger (right-click or drag-to-bin) routes through here.
	private void delete(Object target)
	{
		boolean deleted = false;

		if (target instanceof MultisellItem item)
		{
			if ((_selectedEntry != null) && (_selectedEntry.getIngredients().remove(item) || _selectedEntry.getProducts().remove(item)))
			{
				refreshAfterEdit();
				deleted = true;
			}
		}
		else if (target instanceof Entry entry)
		{
			if ((_multisell != null) && _multisell.getEntries().remove(entry))
			{
				_rightPanel.getEntriesPanel().setMultisell(_multisell, _itemManager::getItemById);
				showEntryInEditor(null);
				deleted = true;
			}
		}

		if (deleted)
		{
			Sound.playSound("trash_basket.wav");
		}
	}

	// Open the line editor for an item (double-click or right-click Edit): its count plus whatever
	// the pack's schema allows on this row type (enchantmentLevel, chance, maintainIngredient, ...).
	private void editLine(MultisellItem item, boolean isIngredient)
	{
		final Item template = _itemManager == null ? null : _itemManager.getItemById(item.getItemId());
		final String name = template != null ? template.getName() : ("id " + item.getItemId());

		Sound.playSound("window_open.wav"); // Same open sound as viewing an item's info.
		if (LineEditorDialog.edit(_frame, name, item, rowAttributes(isIngredient)))
		{
			refreshAfterEdit();
			Sound.playSound("Type.wav");
		}
	}

	// The attributes the schema allows on an ingredient or production row (empty when no xsd was
	// loaded, so the editor then just edits the count).
	private List<SchemaAttribute> rowAttributes(boolean isIngredient)
	{
		if (_multisellSchema == null)
		{
			return List.of();
		}
		return isIngredient ? _multisellSchema.getIngredientAttributes() : _multisellSchema.getProductionAttributes();
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
		Sound.playSound("quest_middle.wav");
	}

	// Right-click Move Up (-1) / Move Down (+1): just a reorder to the neighbouring position.
	private void moveEntry(Entry entry, int delta)
	{
		final int from = (_multisell == null) ? -1 : _multisell.getEntries().indexOf(entry);
		reorderEntry(from, from + delta);
	}

	// Move an entry from one position to another (menu or drag). This is its line order in the XML.
	private void reorderEntry(int from, int to)
	{
		if (_multisell == null)
		{
			return;
		}

		final Entry moved = moveTo(_multisell.getEntries(), from, to);
		if (moved != null)
		{
			_rightPanel.getEntriesPanel().setMultisell(_multisell, _itemManager::getItemById);
			_rightPanel.getEntriesPanel().selectEntry(moved);
			Sound.playSound("Item_Throw.wav");
		}
	}

	// Right-click Move Up (-1) / Move Down (+1) for an ingredient or product.
	private void moveItem(boolean ingredient, MultisellItem item, int delta)
	{
		if (_selectedEntry == null)
		{
			return;
		}

		final int from = itemList(ingredient).indexOf(item);
		reorderItem(ingredient, from, from + delta);
	}

	// Move an ingredient or product from one position to another (menu or drag).
	private void reorderItem(boolean ingredient, int from, int to)
	{
		if (_selectedEntry == null)
		{
			return;
		}

		final MultisellItem moved = moveTo(itemList(ingredient), from, to);
		if (moved != null)
		{
			refreshAfterEdit();
			(ingredient ? _rightPanel.getIngredientsPanel() : _rightPanel.getProductsPanel()).selectItem(moved);
			Sound.playSound("Item_Throw.wav");
		}
	}

	private List<MultisellItem> itemList(boolean ingredient)
	{
		return ingredient ? _selectedEntry.getIngredients() : _selectedEntry.getProducts();
	}

	// The one reorder operation, shared by the right-click menu and drag-and-drop: take the
	// element at 'from' and put it at index 'to' (clamped). Returns the moved element, or null
	// when nothing actually moved - an invalid 'from', or a drag dropped back in the same spot -
	// so callers only refresh and play the reorder sound on a real move.
	private static <T> T moveTo(List<T> list, int from, int to)
	{
		if ((from < 0) || (from >= list.size()))
		{
			return null;
		}

		final int target = Math.max(0, Math.min(to, list.size() - 1));
		if (target == from)
		{
			return null; // Dropped in place - not a reorder.
		}

		final T element = list.remove(from);
		list.add(target, element);
		return element;
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
