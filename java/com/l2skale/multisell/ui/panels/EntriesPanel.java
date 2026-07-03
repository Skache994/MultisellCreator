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
import java.awt.Dimension;
import java.awt.Font;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.l2skale.multisell.model.Item;
import com.l2skale.multisell.model.multisell.Entry;
import com.l2skale.multisell.model.multisell.Multisell;
import com.l2skale.multisell.ui.dnd.ListExportTransferHandler;
import com.l2skale.multisell.ui.renders.EntryRowRenderer;
import com.l2skale.multisell.ui.utils.HintList;
import com.l2skale.multisell.ui.utils.ListContextMenu;

/*
 * Shows every entry of a loaded multisell, one row per entry (ingredients -> products).
 * This is the "truth" view: it displays exactly what the XML contains.
 *
 * @author Skache
 */
public class EntriesPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final DefaultListModel<Entry> _model = new DefaultListModel<>();
	private final HintList<Entry> _view = new HintList<>(_model);
	private final EntryRowRenderer _renderer = new EntryRowRenderer();
	private final JLabel _title = new JLabel("Entries");

	private Multisell _multisell;
	private Consumer<Entry> _onDuplicate;
	private Consumer<Entry> _onDelete;
	private Consumer<Entry> _onMoveUp;
	private Consumer<Entry> _onMoveDown;

	public EntriesPanel()
	{
		setLayout(new BorderLayout());

		_title.setFont(new Font("Arial", Font.BOLD, 14));
		add(_title, BorderLayout.NORTH);

		_view.setCellRenderer(_renderer);
		_view.setHint("New or Open a multisell");
		add(new JScrollPane(_view), BorderLayout.CENTER);

		setPreferredSize(new Dimension(200, 200));

		installContextMenu();
	}

	// Right-click menu actions on an entry.
	public void setOnDuplicate(Consumer<Entry> onDuplicate)
	{
		_onDuplicate = onDuplicate;
	}

	public void setOnDelete(Consumer<Entry> onDelete)
	{
		_onDelete = onDelete;
	}

	public void setOnMoveUp(Consumer<Entry> onMoveUp)
	{
		_onMoveUp = onMoveUp;
	}

	public void setOnMoveDown(Consumer<Entry> onMoveDown)
	{
		_onMoveDown = onMoveDown;
	}

	// Select an entry in the list (used after duplicating).
	public void selectEntry(Entry entry)
	{
		_view.setSelectedValue(entry, true);
	}

	// Notify when the selected entry changes (null when nothing is selected).
	public void addSelectionListener(Consumer<Entry> onSelect)
	{
		_view.addListSelectionListener(e ->
		{
			if (!e.getValueIsAdjusting())
			{
				onSelect.accept(_view.getSelectedValue());
			}
		});
	}

	// Display the given multisell; itemLookup resolves item ids to Items for icons/names.
	public void setMultisell(Multisell multisell, IntFunction<Item> itemLookup)
	{
		_multisell = multisell;
		_renderer.setItemLookup(itemLookup);
		_model.clear();
		for (Entry entry : multisell.getEntries())
		{
			_model.addElement(entry);
		}
		_view.setHint("Click 'New Entry' to add a trade");
		updateTitle();
	}

	// Append a new entry to the list and select it.
	public void addEntry(Entry entry)
	{
		_model.addElement(entry);
		updateTitle();
		_view.setSelectedValue(entry, true);
	}

	private void updateTitle()
	{
		final String id = ((_multisell != null) && (_multisell.getId() > 0)) ? String.valueOf(_multisell.getId()) : "(new)";
		_title.setText("Entries  -  multisell " + id + "  (" + _model.getSize() + ")");
	}

	// Allow entries to be dragged out of the list (e.g. onto the trash bin) and to be
	// reordered by dragging within the list (onReorder is given the source and target indices).
	public void enableEntryDrag(BiConsumer<Integer, Integer> onReorder)
	{
		_view.setDragEnabled(true);
		_view.setDropMode(DropMode.INSERT);
		_view.setTransferHandler(new ListExportTransferHandler(_view, onReorder));
	}

	// Tell the list that one entry changed so its row is re-rendered.
	public void refreshEntry(Entry entry)
	{
		final int index = _model.indexOf(entry);
		if (index >= 0)
		{
			_model.set(index, entry);
		}
	}

	private void installContextMenu()
	{
		ListContextMenu.install(_view, (menu, entry, index) ->
		{
			menu.item("Move Up", () -> fire(_onMoveUp, entry)).enabled(index > 0);
			menu.item("Move Down", () -> fire(_onMoveDown, entry)).enabled(index < (_model.getSize() - 1));
			menu.separator();
			menu.item("Duplicate", () -> fire(_onDuplicate, entry));
			menu.item("Delete", () -> fire(_onDelete, entry));
		});
	}

	private static void fire(Consumer<Entry> action, Entry entry)
	{
		if (action != null)
		{
			action.accept(entry);
		}
	}
}
