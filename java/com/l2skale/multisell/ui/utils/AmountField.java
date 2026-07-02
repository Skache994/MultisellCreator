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
package com.l2skale.multisell.ui.utils;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

/*
 * A text field for entering an amount that live-formats with dot thousands
 * separators as the user types (type 1000000, see 1.000.000).
 *
 * @author Skache
 */
public class AmountField extends JTextField
{
	private static final long serialVersionUID = 1L;

	public AmountField()
	{
		super(14);
		((AbstractDocument) getDocument()).setDocumentFilter(new GroupingFilter());
	}

	public void setValue(int value)
	{
		setText(Numbers.format(value));
	}

	public int getValue()
	{
		return Numbers.parse(getText());
	}

	// Reformats the whole field on every edit, keeping the caret at the end.
	private static class GroupingFilter extends DocumentFilter
	{
		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException
		{
			replace(fb, offset, 0, string, attr);
		}

		@Override
		public void remove(FilterBypass fb, int offset, int length) throws BadLocationException
		{
			replace(fb, offset, length, "", null);
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException
		{
			final Document doc = fb.getDocument();
			final StringBuilder sb = new StringBuilder(doc.getText(0, doc.getLength()));
			sb.replace(offset, offset + length, text == null ? "" : text);

			String digits = sb.toString().replaceAll("[^0-9]", "");
			if (digits.length() > 15)
			{
				digits = digits.substring(0, 15);
			}

			final String formatted = digits.isEmpty() ? "" : Numbers.format(Long.parseLong(digits));
			fb.replace(0, doc.getLength(), formatted, attrs);
		}
	}
}
