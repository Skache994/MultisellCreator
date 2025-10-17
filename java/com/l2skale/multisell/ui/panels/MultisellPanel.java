package com.l2skale.multisell.ui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.l2skale.multisell.MultisellEntry;
import com.l2skale.multisell.model.MultisellList;
import com.l2skale.multisell.ui.renders.MultisellEntryRenderer;

public class MultisellPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private JList<MultisellEntry> _multisellListView;

	// Constructor now takes MultisellList as a parameter
	public MultisellPanel(MultisellList multisellListModel)
	{
		setLayout(new BorderLayout());

		// Create the label
		JLabel multisellLabel = new JLabel("Multisell List");
		multisellLabel.setFont(new Font("Arial", Font.BOLD, 14));
		add(multisellLabel, BorderLayout.NORTH);

		// Create and set up the JList for multisell
		_multisellListView = new JList<>(multisellListModel.getMultisellEntriesModel());
		MultisellEntryRenderer entryRenderer = new MultisellEntryRenderer();
		_multisellListView.setCellRenderer(entryRenderer);

		JScrollPane scrollPane = new JScrollPane(_multisellListView);
		add(scrollPane, BorderLayout.CENTER);

		// Optionally, set the preferred size here
		setPreferredSize(new Dimension(200, 200)); // Example preferred size
	}
}
