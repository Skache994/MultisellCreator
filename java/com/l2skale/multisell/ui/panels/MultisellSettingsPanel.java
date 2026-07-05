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

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.l2skale.multisell.model.multisell.AttributeType;
import com.l2skale.multisell.model.multisell.Multisell;
import com.l2skale.multisell.model.multisell.MultisellSchema;
import com.l2skale.multisell.model.multisell.SchemaAttribute;
import com.l2skale.multisell.ui.dialogs.NpcEditorDialog;
import com.l2skale.multisell.ui.utils.ButtonFactory;
import com.l2skale.multisell.ui.utils.TextFields;
import com.l2skale.multisell.ui.utils.WrapLayout;

/*
 * The settings bar: shows the current multisell and edits its list-level options.
 * Those options are not hardcoded - they are built from the loaded pack's own
 * multisell.xsd (a checkbox for each boolean flag, a small field for each number/
 * token), so an Interlude pack shows useRate while a modern pack shows the
 * multipliers and isChanceMultisell. Changes are written straight into the bound
 * Multisell by attribute name.
 *
 * @author Skache
 */
public class MultisellSettingsPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	// Fallback when a pack ships no xsd: the two flags every chronicle shares.
	private static final List<SchemaAttribute> DEFAULT_OPTIONS = List.of(new SchemaAttribute("applyTaxes", AttributeType.BOOLEAN, false), new SchemaAttribute("maintainEnchantment", AttributeType.BOOLEAN, false));

	private final JLabel _title = new JLabel("No multisell");
	private final JLabel _npcsLabel = new JLabel("any");
	private final JButton _npcsButton = ButtonFactory.createButton("Edit NPCs...", _ -> editNpcs());

	// The option controls currently on the bar (checkboxes/fields built from the schema). Tracked
	// so a rebuild removes exactly these and leaves the fixed title/NPCs controls in place.
	private final List<Component> _optionControls = new ArrayList<>();

	private Multisell _multisell;
	private MultisellSchema _schema; // The loaded pack's rules; null falls back to DEFAULT_OPTIONS.

	// Resolves an npc id to its name for the editor (set once the datapack npcs are loaded; may be null).
	private IntFunction<String> _npcNameLookup;

	// Tells the editor whether an npc id is custom (from stats/npcs/custom); may be null.
	private IntPredicate _npcCustomLookup;

	public MultisellSettingsPanel()
	{
		// WrapLayout (not plain FlowLayout) so the bar grows to a second row instead of clipping
		// its options when a modern pack's controls are wider than the window.
		setLayout(new WrapLayout(FlowLayout.LEFT, 10, 4));

		add(_title);
		add(new JLabel("|"));
		add(new JLabel("NPCs:"));
		add(_npcsLabel);
		add(_npcsButton);
		add(new JLabel("|"));

		setControlsEnabled(false);
	}

	// Provide the pack's multisell rules (call after the datapack loads its xsd). The option
	// controls rebuild from this the next time a multisell is bound.
	public void setSchema(MultisellSchema schema)
	{
		_schema = schema;
	}

	// Provide the npc lookups used to label ids in the editor (call after npcs load).
	public void setNpcLookups(IntFunction<String> nameLookup, IntPredicate customLookup)
	{
		_npcNameLookup = nameLookup;
		_npcCustomLookup = customLookup;
	}

	// Bind the panel to a multisell (or null to clear it).
	public void setMultisell(Multisell multisell)
	{
		_multisell = multisell;
		rebuildOptions();

		if (multisell == null)
		{
			_title.setText("No multisell");
			setControlsEnabled(false);
			return;
		}

		_title.setText("Multisell " + (!multisell.getId().isEmpty() ? multisell.getId() : "(new)"));
		updateNpcsLabel();
		setControlsEnabled(true);
	}

	// Rebuild the option controls (checkbox per flag, field per number/token) from the schema,
	// each bound to read/write its attribute on the current multisell by name.
	private void rebuildOptions()
	{
		for (Component control : _optionControls)
		{
			remove(control);
		}
		_optionControls.clear();

		if (_multisell != null)
		{
			for (SchemaAttribute attribute : optionAttributes())
			{
				final JComponent control = createControl(attribute);
				add(control);
				_optionControls.add(control);
			}
		}

		revalidate();
		repaint();
	}

	// The list-level attributes to offer: the pack's own if we have an xsd, else the shared defaults.
	private List<SchemaAttribute> optionAttributes()
	{
		return (_schema != null) ? _schema.getListAttributes() : DEFAULT_OPTIONS;
	}

	private JComponent createControl(SchemaAttribute attribute)
	{
		final String name = attribute.getName();
		if (attribute.getType() == AttributeType.BOOLEAN)
		{
			final JCheckBox box = ButtonFactory.createCheckBox(name);
			box.setSelected(_multisell.getListAttributes().getBoolean(name));
			box.addActionListener(_ -> _multisell.getListAttributes().setBoolean(name, box.isSelected()));
			return box;
		}

		// Numbers/tokens (useRate, ingredientMultiplier, ...): a small labeled field.
		final JPanel holder = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
		holder.setOpaque(false);
		holder.add(new JLabel(name + ":"));

		final JTextField field = new JTextField(6);
		field.setText(nullToEmpty(_multisell.getListAttributes().get(name)));
		TextFields.onChange(field, text -> _multisell.getListAttributes().set(name, text.trim()));
		holder.add(field);
		return holder;
	}

	private void editNpcs()
	{
		if (_multisell == null)
		{
			return;
		}

		final Window owner = SwingUtilities.getWindowAncestor(this);
		final String title = "Edit NPCs" + (!_multisell.getId().isEmpty() ? " - Multisell " + _multisell.getId() : "");
		final List<Integer> result = NpcEditorDialog.edit(owner, title, _multisell.getNpcIds(), _npcNameLookup, _npcCustomLookup);
		if (result == null)
		{
			return; // Cancelled - leave the list untouched.
		}

		_multisell.getNpcIds().clear();
		_multisell.getNpcIds().addAll(result);
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
	}

	private static String nullToEmpty(String value)
	{
		return value == null ? "" : value;
	}
}
