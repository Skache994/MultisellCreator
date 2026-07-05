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
package com.l2skale.multisell.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import com.l2skale.multisell.ui.renders.RowColors;
import com.l2skale.multisell.ui.utils.ButtonFactory;
import com.l2skale.multisell.ui.utils.CustomBadge;
import com.l2skale.multisell.ui.utils.MessageUtils;
import com.l2skale.multisell.ui.utils.ResourceIcons;

/*
 * The "Edit NPCs" popup. Manages the small list of npc ids a multisell is attached to:
 * type an id to add it (its name is looked up from the datapack as confirmation), and
 * remove any row with the red button. The special id -1 means "works from everywhere"
 * (Community Board). Open with NpcEditorDialog.edit(...) - it returns the edited ids on
 * Save, or null on Cancel.
 *
 * @author Skache
 */
public class NpcEditorDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	// The id used by the community board / "works from everywhere" multisells.
	private static final int EVERYWHERE_ID = -1;

	private final IntFunction<String> _nameLookup;
	private final IntPredicate _customLookup;

	// Insertion order preserved, duplicates ignored.
	private final Set<Integer> _ids = new LinkedHashSet<>();

	private final JPanel _rowsPanel = new JPanel();
	private final JTextField _idField = new JTextField(8);

	private boolean _saved = false;

	private NpcEditorDialog(Window owner, String title, Collection<Integer> currentIds, IntFunction<String> nameLookup, IntPredicate customLookup)
	{
		super(owner, title, ModalityType.APPLICATION_MODAL);
		_nameLookup = nameLookup;
		_customLookup = customLookup;
		_ids.addAll(currentIds);

		setLayout(new BorderLayout(0, 8));
		add(buildAddBar(), BorderLayout.NORTH);
		add(buildCenter(), BorderLayout.CENTER);
		add(buildButtons(), BorderLayout.SOUTH);

		rebuildRows();

		setMinimumSize(new Dimension(360, 300));
		pack();
		setLocationRelativeTo(owner);
	}

	// Shows the dialog and returns the edited ids on Save, or null if the user cancelled.
	public static List<Integer> edit(Window owner, String title, Collection<Integer> currentIds, IntFunction<String> nameLookup, IntPredicate customLookup)
	{
		final NpcEditorDialog dialog = new NpcEditorDialog(owner, title, currentIds, nameLookup, customLookup);
		dialog.setVisible(true);
		return dialog._saved ? new ArrayList<>(dialog._ids) : null;
	}

	// "Add npc id: [____] [Add]" - Enter in the field also adds.
	private JPanel buildAddBar()
	{
		final JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
		bar.add(new JLabel("Add npc id:"));
		bar.add(_idField);

		final JButton addButton = ButtonFactory.createButton("Add", _ -> addTypedId());
		bar.add(addButton);

		_idField.addActionListener(_ -> addTypedId());
		return bar;
	}

	// The list area: a column header on top of the scrolling rows, both column-aligned.
	private JComponent buildCenter()
	{
		final JPanel center = new JPanel(new BorderLayout());
		center.add(buildHeader(), BorderLayout.NORTH);
		center.add(buildRowsScroll(), BorderLayout.CENTER);
		return center;
	}

	// "ID   Name   Remove" header, laid out like a row so the columns line up with it.
	private JComponent buildHeader()
	{
		final JPanel header = new JPanel(new BorderLayout(8, 0));
		header.setOpaque(true);
		header.setBackground(RowColors.background(false));
		header.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, RowColors.separator()), BorderFactory.createEmptyBorder(4, 10, 4, 10)));

		header.add(headerLabel("ID", 60, SwingConstants.LEFT), BorderLayout.WEST);
		header.add(headerLabel("Name", 0, SwingConstants.LEFT), BorderLayout.CENTER);
		header.add(headerLabel("Remove", 0, SwingConstants.RIGHT), BorderLayout.EAST);
		return header;
	}

	// A bold column label; width > 0 fixes it (the ID column, to match the id cells).
	private static JLabel headerLabel(String text, int width, int align)
	{
		final JLabel label = new JLabel(text, align);
		if (width > 0)
		{
			label.setPreferredSize(new Dimension(width, 20));
		}
		label.setForeground(RowColors.foreground());
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		return label;
	}

	private JScrollPane buildRowsScroll()
	{
		_rowsPanel.setLayout(new BoxLayout(_rowsPanel, BoxLayout.Y_AXIS));
		_rowsPanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

		// Paint the list area like the app's other lists instead of the default L&F gray.
		_rowsPanel.setOpaque(true);
		_rowsPanel.setBackground(RowColors.background(false));

		final JScrollPane scroll = new JScrollPane(_rowsPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBorder(BorderFactory.createEmptyBorder()); // no border, so the header and rows share the same left edge
		scroll.getViewport().setBackground(RowColors.background(false));
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		return scroll;
	}

	private JPanel buildButtons()
	{
		final JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
		bar.add(ButtonFactory.createButton("Save", _ ->
		{
			_saved = true;
			dispose();
		}));
		bar.add(ButtonFactory.createButton("Cancel", _ -> dispose()));
		return bar;
	}

	// Parse the field and add the id (dedup), then clear and refocus the field.
	private void addTypedId()
	{
		final String text = _idField.getText().trim();
		if (text.isEmpty())
		{
			return;
		}

		final int id;
		try
		{
			id = Integer.parseInt(text);
		}
		catch (NumberFormatException ex)
		{
			MessageUtils.showWarningMessage(this, "\"" + text + "\" is not a valid npc id.", "Invalid id");
			return;
		}

		_ids.add(id);
		_idField.setText("");
		_idField.requestFocusInWindow();
		rebuildRows();
	}

	// Rebuild the whole row list from the current ids - simple and cheap for a small list.
	private void rebuildRows()
	{
		_rowsPanel.removeAll();

		if (_ids.isEmpty())
		{
			final JLabel empty = new JLabel("No npcs - this multisell can be opened from anywhere.");
			empty.setAlignmentX(Component.LEFT_ALIGNMENT);
			empty.setForeground(RowColors.foreground());
			empty.setBorder(BorderFactory.createEmptyBorder(6, 2, 6, 2));
			_rowsPanel.add(empty);
		}
		else
		{
			for (int id : _ids)
			{
				_rowsPanel.add(buildRow(id));
			}
		}

		_rowsPanel.revalidate();
		_rowsPanel.repaint();
	}

	// One "id   Name   [remove]" row, painted to match the app's list rows.
	private JPanel buildRow(int id)
	{
		final Color foreground = RowColors.foreground();

		final JPanel row = new JPanel(new BorderLayout(8, 0));
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		row.setOpaque(true);
		row.setBackground(RowColors.background(false));
		row.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, RowColors.separator()), BorderFactory.createEmptyBorder(4, 2, 4, 2)));
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

		final JLabel idLabel = new JLabel(String.valueOf(id));
		idLabel.setPreferredSize(new Dimension(60, 20));
		idLabel.setForeground(foreground);
		row.add(idLabel, BorderLayout.WEST);

		final boolean custom = (_customLookup != null) && _customLookup.test(id);
		final JLabel nameLabel = new JLabel(custom ? CustomBadge.textName(displayName(id)) : displayName(id));
		nameLabel.setForeground(custom ? CustomBadge.COLOR : foreground);
		row.add(nameLabel, BorderLayout.CENTER);

		row.add(buildRemoveButton(id), BorderLayout.EAST);
		return row;
	}

	// The same red delete_button texture used by the item search's clear button.
	private JButton buildRemoveButton(int id)
	{
		ImageIcon icon = ResourceIcons.loadResourceIconsIcon("delete_button.png");
		if (icon != null)
		{
			icon = new ImageIcon(icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
		}

		final JButton button = ButtonFactory.createIconButton(icon, _ ->
		{
			_ids.remove(id);
			rebuildRows();
		});
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setContentAreaFilled(false);
		button.setFocusPainted(false);
		button.setToolTipText("Remove this npc");
		if (icon != null)
		{
			button.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
		}
		return button;
	}

	// The confirmation text shown next to an id: real name, the CB marker for -1, or a
	// clear "(not in datapack)" so a mistyped id stands out.
	private String displayName(int id)
	{
		if (id == EVERYWHERE_ID)
		{
			return "works everywhere (CB)";
		}

		final String name = (_nameLookup == null) ? null : _nameLookup.apply(id);
		return ((name == null) || name.isEmpty()) ? "(not in datapack)" : name;
	}
}
