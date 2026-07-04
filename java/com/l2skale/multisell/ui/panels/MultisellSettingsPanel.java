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

import java.awt.FlowLayout;
import java.util.StringJoiner;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.l2skale.multisell.model.multisell.Multisell;
import com.l2skale.multisell.ui.utils.ButtonFactory;

/*
 * The settings bar: shows the current multisell and edits its list-level options
 * (allowed npcs, applyTaxes, maintainEnchantment, useRate). Changes are written
 * straight into the bound Multisell.
 *
 * @author Skache
 */
public class MultisellSettingsPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final JLabel _title = new JLabel("No multisell");
	private final JLabel _npcsLabel = new JLabel("any");
	private final JButton _npcsButton = ButtonFactory.createButton("Edit NPCs...", _ -> editNpcs());
	private final JCheckBox _applyTaxes = ButtonFactory.createCheckBox("applyTaxes");
	private final JCheckBox _maintainEnchantment = ButtonFactory.createCheckBox("maintainEnchantment");
	private final JTextField _useRate = new JTextField(6);

	private Multisell _multisell;

	public MultisellSettingsPanel()
	{
		setLayout(new FlowLayout(FlowLayout.LEFT, 10, 4));

		add(_title);
		add(new JLabel("|"));
		add(new JLabel("NPCs:"));
		add(_npcsLabel);
		add(_npcsButton);
		add(new JLabel("|"));
		add(_applyTaxes);
		add(_maintainEnchantment);
		add(new JLabel("|"));
		add(new JLabel("useRate:"));
		add(_useRate);

		_applyTaxes.addActionListener(_ ->
		{
			if (_multisell != null)
			{
				_multisell.setApplyTaxes(_applyTaxes.isSelected());
			}
		});
		_maintainEnchantment.addActionListener(_ ->
		{
			if (_multisell != null)
			{
				_multisell.setMaintainEnchantment(_maintainEnchantment.isSelected());
			}
		});
		_useRate.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				update();
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				update();
			}

			private void update()
			{
				if (_multisell != null)
				{
					final String text = _useRate.getText().trim();
					_multisell.setUseRate(text.isEmpty() ? null : text);
				}
			}
		});

		setControlsEnabled(false);
	}

	// Bind the panel to a multisell (or null to clear it).
	public void setMultisell(Multisell multisell)
	{
		_multisell = multisell;
		if (multisell == null)
		{
			_title.setText("No multisell");
			setControlsEnabled(false);
			return;
		}

		_title.setText("Multisell " + (multisell.getId() > 0 ? String.valueOf(multisell.getId()) : "(new)"));
		_applyTaxes.setSelected(multisell.isApplyTaxes());
		_maintainEnchantment.setSelected(multisell.isMaintainEnchantment());
		_useRate.setText(multisell.getUseRate() == null ? "" : multisell.getUseRate());
		updateNpcsLabel();
		setControlsEnabled(true);
	}

	private void editNpcs()
	{
		if (_multisell == null)
		{
			return;
		}

		final StringJoiner current = new StringJoiner(", ");
		for (int id : _multisell.getNpcIds())
		{
			current.add(String.valueOf(id));
		}

		final String input = JOptionPane.showInputDialog(this, "NPC ids allowed to open this multisell (comma-separated, empty = any):", current.toString());
		if (input == null)
		{
			return;
		}

		_multisell.getNpcIds().clear();
		for (String part : input.split(","))
		{
			final String trimmed = part.trim();
			if (!trimmed.isEmpty())
			{
				try
				{
					_multisell.getNpcIds().add(Integer.parseInt(trimmed));
				}
				catch (NumberFormatException ignored)
				{
					// Skip anything that is not a number.
				}
			}
		}
		updateNpcsLabel();
	}

	private void updateNpcsLabel()
	{
		final int count = _multisell == null ? 0 : _multisell.getNpcIds().size();
		_npcsLabel.setText(count == 0 ? "any" : (count + " npc(s)"));
	}

	private void setControlsEnabled(boolean enabled)
	{
		_npcsButton.setEnabled(enabled);
		_applyTaxes.setEnabled(enabled);
		_maintainEnchantment.setEnabled(enabled);
		_useRate.setEnabled(enabled);
	}
}
