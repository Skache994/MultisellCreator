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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JList;
import javax.swing.ListModel;

/*
 * A JList that paints a centered, gray hint when its model is empty - an
 * empty-state guide. The hint may contain \n for multiple lines.
 *
 * @author Skache
 */
public class HintList<T> extends JList<T>
{
	private static final long serialVersionUID = 1L;

	private String _hint = "";

	public HintList(ListModel<T> model)
	{
		super(model);
	}

	public void setHint(String hint)
	{
		_hint = hint == null ? "" : hint;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		final ListModel<T> model = getModel();
		if (!_hint.isEmpty() && ((model == null) || (model.getSize() == 0)))
		{
			paintHint((Graphics2D) g);
		}
	}

	private void paintHint(Graphics2D g)
	{
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(new Color(140, 140, 140));
		g.setFont(getFont().deriveFont(Font.ITALIC, 13f));

		final FontMetrics fm = g.getFontMetrics();
		final String[] lines = _hint.split("\n");
		final int lineHeight = fm.getHeight();
		int y = ((getHeight() - (lineHeight * lines.length)) / 2) + fm.getAscent();
		for (String line : lines)
		{
			final int x = (getWidth() - fm.stringWidth(line)) / 2;
			g.drawString(line, x, y);
			y += lineHeight;
		}
	}
}
