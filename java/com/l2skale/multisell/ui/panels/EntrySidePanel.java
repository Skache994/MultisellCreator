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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.l2skale.multisell.model.Item;
import com.l2skale.multisell.model.multisell.MultisellItem;
import com.l2skale.multisell.ui.dnd.AddItemTransferHandler;
import com.l2skale.multisell.ui.renders.MultisellItemRenderer;
import com.l2skale.multisell.ui.utils.Fonts;
import com.l2skale.multisell.ui.utils.HintList;
import com.l2skale.multisell.ui.utils.ListContextMenu;

/*
 * A titled list of multisell items - used for the selected entry's ingredients
 * or its products. Right-clicking an item offers to remove it.
 *
 * @author Skache
 */
public class EntrySidePanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final DefaultListModel<MultisellItem> _model = new DefaultListModel<>();
	private final HintList<MultisellItem> _view = new HintList<>(_model);
	private final MultisellItemRenderer _renderer = new MultisellItemRenderer();

	// Default to no-ops so a row action is always safe to call, even before Gui wires them up.
	private Consumer<MultisellItem> _onRemove = _ -> {};
	private Consumer<MultisellItem> _onEdit = _ -> {};
	private Consumer<MultisellItem> _onMoveUp = _ -> {};
	private Consumer<MultisellItem> _onMoveDown = _ -> {};

	public EntrySidePanel(String title)
	{
		setLayout(new BorderLayout());

		JLabel label = new JLabel(title);
		label.setFont(Fonts.SECTION_TITLE);
		add(label, BorderLayout.NORTH);

		_view.setCellRenderer(_renderer);
		_view.setHint("Select an entry to edit");
		add(new JScrollPane(_view), BorderLayout.CENTER);

		installContextMenu();
	}

	// Called when the user right-clicks an item and chooses Remove.
	public void setOnRemove(Consumer<MultisellItem> onRemove)
	{
		_onRemove = onRemove;
	}

	// Called when the user opens the line editor for an item (double-click or right-click Edit).
	public void setOnEdit(Consumer<MultisellItem> onEdit)
	{
		_onEdit = onEdit;
	}

	public void setOnMoveUp(Consumer<MultisellItem> onMoveUp)
	{
		_onMoveUp = onMoveUp;
	}

	public void setOnMoveDown(Consumer<MultisellItem> onMoveDown)
	{
		_onMoveDown = onMoveDown;
	}

	// Reselect an item after it has moved, so it stays highlighted.
	public void selectItem(MultisellItem item)
	{
		_view.setSelectedValue(item, true);
	}

	// Show the given items; itemLookup resolves ids to Items for icons/names.
	public void setItems(List<MultisellItem> items, IntFunction<Item> itemLookup)
	{
		_renderer.setItemLookup(itemLookup);
		_model.clear();
		for (MultisellItem item : items)
		{
			_model.addElement(item);
		}
	}

	// Empty the panel (no entry selected).
	public void clearItems()
	{
		_model.clear();
	}

	// Set the empty-state hint shown when the list is empty.
	public void setHint(String hint)
	{
		_view.setHint(hint);
	}

	// Allow items dragged from the item list to be dropped here (onDrop), the list's own
	// items to be dragged out (e.g. onto the trash bin), and rows to be reordered by
	// dragging within the list (onReorder is given the source and target indices).
	public void enableItemDrop(Consumer<Item> onDrop, BiConsumer<Integer, Integer> onReorder)
	{
		_view.setDragEnabled(true);
		_view.setDropMode(DropMode.INSERT);
		_view.setTransferHandler(new AddItemTransferHandler(_view, onDrop, onReorder));
	}

	private void installContextMenu()
	{
		// Double-click an item to open its line editor.
		_view.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() != 2)
				{
					return;
				}

				final int index = _view.locationToIndex(e.getPoint());
				if (index >= 0)
				{
					_view.setSelectedIndex(index);
					final MultisellItem selected = _view.getSelectedValue();
					if (selected != null)
					{
						_onEdit.accept(selected);
					}
				}
			}
		});

		// Right-click menu. Edit opens the same line editor as a double-click.
		ListContextMenu.install(_view, (menu, item, index) ->
		{
			menu.item("Edit", () -> _onEdit.accept(item));
			menu.separator();
			menu.item("Move Up", () -> _onMoveUp.accept(item)).enabled(index > 0);
			menu.item("Move Down", () -> _onMoveDown.accept(item)).enabled(index < (_model.getSize() - 1));
			menu.separator();
			menu.item("Remove", () -> _onRemove.accept(item));
		});
	}
}
