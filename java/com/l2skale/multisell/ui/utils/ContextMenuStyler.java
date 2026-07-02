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

import javax.swing.JMenuItem;

/*
 * @author Skache
 */
public class ContextMenuStyler
{
	private boolean _isDarkTheme;

	public ContextMenuStyler(boolean isDarkTheme)
	{
		this._isDarkTheme = isDarkTheme;
	}

	// Style a menu item.
	public void styleMenuItem(JMenuItem menuItem)
	{
		// Set background and text colors based on the theme.
		Color menuBgColor = _isDarkTheme ? Color.DARK_GRAY : Color.WHITE;
		Color menuTextColor = _isDarkTheme ? Color.WHITE : Color.BLACK;
		Font menuItemFont = new Font("Arial", Font.PLAIN, 13);

		menuItem.setBackground(menuBgColor);
		menuItem.setForeground(menuTextColor);
		menuItem.setFont(menuItemFont);
	}
}
