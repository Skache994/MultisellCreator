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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.l2skale.multisell.model.multisell.AttributeType;
import com.l2skale.multisell.model.multisell.MultisellItem;
import com.l2skale.multisell.model.multisell.SchemaAttribute;
import com.l2skale.multisell.ui.utils.AmountField;
import com.l2skale.multisell.ui.utils.MessageUtils;

/*
 * The editor for a single ingredient/production line. Count is always present; the
 * rest of the fields are built from the pack's schema for that row type - a checkbox
 * for each boolean flag (maintainIngredient), a field for each number (enchantmentLevel,
 * chance). So an Interlude ingredient offers "keep", a modern production offers "chance",
 * and neither shows a field the server would not accept. Applies to the item on OK.
 *
 * @author Skache
 */
public final class LineEditorDialog
{
	private LineEditorDialog()
	{
	}

	// Edit the item's count and its schema-allowed extras. Returns true when applied (OK + valid).
	public static boolean edit(Window owner, String itemName, MultisellItem item, List<SchemaAttribute> rowAttributes)
	{
		final JPanel panel = new JPanel(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(4, 4, 4, 4);
		c.anchor = GridBagConstraints.WEST;
		int row = 0;

		// Count is always present and required.
		final AmountField countField = new AmountField();
		countField.setValue(item.getCount());
		addRow(panel, c, row++, "count", countField);

		// A control per allowed extra (skip id and count - id is the item itself, count is above).
		final List<AttrControl> controls = new ArrayList<>();
		for (SchemaAttribute attribute : rowAttributes)
		{
			final String name = attribute.getName();
			if (name.equals("id") || name.equals("count"))
			{
				continue;
			}

			final JComponent control = createControl(attribute, item);
			addRow(panel, c, row++, name, control);
			controls.add(new AttrControl(attribute, control));
		}

		final String title = "Edit " + (itemName == null ? "item" : itemName);
		final int result = JOptionPane.showConfirmDialog(owner, panel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result != JOptionPane.OK_OPTION)
		{
			return false;
		}

		final int count = countField.getValue();
		if (count <= 0)
		{
			MessageUtils.showErrorMessage(owner, "Enter a count greater than 0.", "Input Error");
			return false;
		}

		item.setCount(count);
		for (AttrControl control : controls)
		{
			control.apply(item);
		}
		return true;
	}

	// A checkbox for a boolean flag, otherwise a text field pre-filled with the current value.
	private static JComponent createControl(SchemaAttribute attribute, MultisellItem item)
	{
		final String name = attribute.getName();
		if (attribute.getType() == AttributeType.BOOLEAN)
		{
			final JCheckBox box = new JCheckBox();
			box.setSelected(item.getBooleanExtra(name));
			return box;
		}

		final JTextField field = new JTextField(8);
		final String current = item.getExtra(name);
		field.setText(current == null ? "" : current);
		return field;
	}

	private static void addRow(JPanel panel, GridBagConstraints c, int row, String label, JComponent field)
	{
		c.gridx = 0;
		c.gridy = row;
		c.weightx = 0;
		panel.add(new JLabel(label + ":"), c);

		c.gridx = 1;
		c.weightx = 1;
		panel.add(field, c);
	}

	// Pairs a schema attribute with its editor so its value can be written back on OK. An empty
	// field (or an unticked box) clears the attribute, so it is simply left off the saved line.
	private static final class AttrControl
	{
		private final SchemaAttribute _attribute;
		private final JComponent _control;

		private AttrControl(SchemaAttribute attribute, JComponent control)
		{
			_attribute = attribute;
			_control = control;
		}

		private void apply(MultisellItem item)
		{
			final String name = _attribute.getName();
			if (_control instanceof JCheckBox box)
			{
				item.setExtra(name, box.isSelected() ? "true" : null);
			}
			else if (_control instanceof JTextField field)
			{
				item.setExtra(name, field.getText().trim());
			}
		}
	}
}
