package com.l2skale.multisell.ui.panels;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import com.l2skale.multisell.model.Item;
import com.l2skale.multisell.model.multisell.MultisellItem;
import com.l2skale.multisell.ui.dnd.AddItemTransferHandler;
import com.l2skale.multisell.ui.renders.MultisellItemRenderer;
import com.l2skale.multisell.ui.utils.HintList;

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

	private Consumer<MultisellItem> _onRemove;
	private Consumer<MultisellItem> _onEditAmount;

	public EntrySidePanel(String title)
	{
		setLayout(new BorderLayout());

		JLabel label = new JLabel(title);
		label.setFont(new Font("Arial", Font.BOLD, 14));
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

	// Called when the user double-clicks an item (to change its amount).
	public void setOnEditAmount(Consumer<MultisellItem> onEditAmount)
	{
		_onEditAmount = onEditAmount;
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

	// Allow items dragged from the item list to be dropped here, and allow the
	// list's own items to be dragged out (e.g. onto the trash bin).
	public void enableItemDrop(Consumer<Item> onDrop)
	{
		_view.setDragEnabled(true);
		_view.setDropMode(DropMode.ON);
		_view.setTransferHandler(new AddItemTransferHandler(_view, onDrop));
	}

	private void installContextMenu()
	{
		_view.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				showPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				showPopup(e);
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2)
				{
					final int index = _view.locationToIndex(e.getPoint());
					if (index >= 0)
					{
						_view.setSelectedIndex(index);
						final MultisellItem selected = _view.getSelectedValue();
						if ((selected != null) && (_onEditAmount != null))
						{
							_onEditAmount.accept(selected);
						}
					}
				}
			}
		});
	}

	private void showPopup(MouseEvent e)
	{
		if (!e.isPopupTrigger())
		{
			return;
		}

		final int index = _view.locationToIndex(e.getPoint());
		if (index < 0)
		{
			return;
		}

		_view.setSelectedIndex(index);
		final MultisellItem selected = _view.getSelectedValue();
		if (selected == null)
		{
			return;
		}

		final JPopupMenu menu = new JPopupMenu();
		final JMenuItem remove = new JMenuItem("Remove");
		remove.addActionListener(_ ->
		{
			if (_onRemove != null)
			{
				_onRemove.accept(selected);
			}
		});
		menu.add(remove);
		menu.show(_view, e.getX(), e.getY());
	}
}
