package com.l2skale.multisell.ui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.l2skale.multisell.model.Item;
import com.l2skale.multisell.model.multisell.Entry;
import com.l2skale.multisell.model.multisell.Multisell;
import com.l2skale.multisell.ui.renders.EntryRowRenderer;

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
	private final JList<Entry> _view = new JList<>(_model);
	private final EntryRowRenderer _renderer = new EntryRowRenderer();
	private final JLabel _title = new JLabel("Entries");

	private Multisell _multisell;

	public EntriesPanel()
	{
		setLayout(new BorderLayout());

		_title.setFont(new Font("Arial", Font.BOLD, 14));
		add(_title, BorderLayout.NORTH);

		_view.setCellRenderer(_renderer);
		add(new JScrollPane(_view), BorderLayout.CENTER);

		setPreferredSize(new Dimension(200, 200));
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
		final String id = ((_multisell != null) && (_multisell.getId() > 0)) ? ("#" + _multisell.getId()) : "(new)";
		_title.setText("Entries  -  multisell " + id + "  (" + _model.getSize() + ")");
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
}
