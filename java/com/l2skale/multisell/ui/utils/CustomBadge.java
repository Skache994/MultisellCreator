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

/*
 * The "custom" marker for items and npcs that come from a datapack custom/ subfolder: a small
 * teal dot before the name plus the name tinted the same teal. Kept small on purpose so it sits
 * next to a grade tag without crowding. Single source of truth for the custom look.
 *
 * @author Skache
 */
public final class CustomBadge
{
	// A teal that reads on both the dark and light list backgrounds, distinct from the amber
	// quest tag and the grade colors.
	public static final String HEX = "#3AAFA9";
	public static final Color COLOR = Color.decode(HEX);

	// The small dot shown before a custom name.
	private static final String DOT = "●";

	private CustomBadge()
	{
	}

	// An HTML fragment wrapping a name as custom (dot + tinted name), for use inside an <html> label.
	public static String htmlName(String name)
	{
		return "<span style='color:" + HEX + ";'>" + DOT + " " + name + "</span>";
	}

	// The "dot + name" text for a plain (non-HTML) label; pair with setForeground(COLOR).
	public static String textName(String name)
	{
		return DOT + " " + name;
	}
}
