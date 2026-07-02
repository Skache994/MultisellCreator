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
package com.l2skale.multisell.ui.renders;

import java.awt.Color;

import com.l2skale.multisell.managers.ThemeManager;

/*
 * The item list's selection/highlight colours, shared so every list (items,
 * ingredients, products, entries) highlights the same nice way. Single source
 * of truth - taken from the original ItemListRenderer.
 *
 * @author Skache
 */
public final class RowColors
{
	private RowColors()
	{
	}

	// Row background: purple/light-blue when selected, else the theme's list background.
	public static Color background(boolean selected)
	{
		final boolean dark = ThemeManager.isDarkMode();
		if (selected)
		{
			return dark ? new Color(104, 93, 156) : new Color(204, 204, 255);
		}
		return dark ? new Color(50, 50, 50) : Color.WHITE;
	}

	public static Color foreground()
	{
		return ThemeManager.isDarkMode() ? Color.WHITE : Color.BLACK;
	}
}
