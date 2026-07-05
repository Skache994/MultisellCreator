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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

/*
 * A FlowLayout that actually reports the height it needs when its rows wrap. Plain
 * FlowLayout always reports a single-row preferred height, so when it is placed in a
 * BorderLayout region (like the settings bar in the frame's NORTH) and the content is
 * wider than the window, the wrapped rows are simply clipped and vanish. This variant
 * measures against the current width and grows to as many rows as it takes, so every
 * control stays visible.
 *
 * @author Skache
 */
public class WrapLayout extends FlowLayout
{
	private static final long serialVersionUID = 1L;

	public WrapLayout(int align, int hgap, int vgap)
	{
		super(align, hgap, vgap);
	}

	@Override
	public Dimension preferredLayoutSize(Container target)
	{
		return layoutSize(target, true);
	}

	@Override
	public Dimension minimumLayoutSize(Container target)
	{
		final Dimension minimum = layoutSize(target, false);
		minimum.width -= (getHgap() + 1); // Let the target shrink one gap below preferred without truncating.
		return minimum;
	}

	// Measure the wrapped size against the target's current width (falling back to a single row
	// before the target has a width, so the first pass still has something sensible to report).
	private Dimension layoutSize(Container target, boolean preferred)
	{
		synchronized (target.getTreeLock())
		{
			int targetWidth = target.getSize().width;
			if (targetWidth == 0)
			{
				targetWidth = Integer.MAX_VALUE;
			}

			final int hgap = getHgap();
			final int vgap = getVgap();
			final Insets insets = target.getInsets();
			final int maxWidth = targetWidth - (insets.left + insets.right + (hgap * 2));

			final Dimension dim = new Dimension(0, 0);
			int rowWidth = 0;
			int rowHeight = 0;

			for (int i = 0; i < target.getComponentCount(); i++)
			{
				final Component member = target.getComponent(i);
				if (!member.isVisible())
				{
					continue;
				}

				final Dimension d = preferred ? member.getPreferredSize() : member.getMinimumSize();
				if ((rowWidth + d.width) > maxWidth)
				{
					addRow(dim, rowWidth, rowHeight, vgap);
					rowWidth = 0;
					rowHeight = 0;
				}

				if (rowWidth != 0)
				{
					rowWidth += hgap;
				}
				rowWidth += d.width;
				rowHeight = Math.max(rowHeight, d.height);
			}
			addRow(dim, rowWidth, rowHeight, vgap);

			dim.width += insets.left + insets.right + (hgap * 2);
			dim.height += insets.top + insets.bottom + (vgap * 2);
			return dim;
		}
	}

	private static void addRow(Dimension dim, int rowWidth, int rowHeight, int vgap)
	{
		dim.width = Math.max(dim.width, rowWidth);
		if (dim.height > 0)
		{
			dim.height += vgap;
		}
		dim.height += rowHeight;
	}
}
