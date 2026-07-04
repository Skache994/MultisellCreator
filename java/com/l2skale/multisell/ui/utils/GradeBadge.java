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

import com.l2skale.multisell.model.Item;

/*
 * Builds the small grade tag (D/C/B/A/S) shown right after an item name, like the game does. This
 * is the single place the grade look lives; today it is a colored letter and can be swapped for
 * image badges (an <img> of resources/images/grades/<grade>.png) without touching the renderers.
 *
 * @author Skache
 */
public final class GradeBadge
{
	private GradeBadge()
	{
	}

	// An HTML fragment to append after an item name inside an <html> label, or "" for no grade.
	public static String htmlTag(Item item)
	{
		final String grade = item.getGrade();
		if (grade == null)
		{
			return "";
		}
		return " <b style='color:" + color(grade) + ";'>[" + grade + "]</b>";
	}

	private static String color(String grade)
	{
		return switch (grade)
		{
			case "D" -> "#9FC5E8"; // pale blue
			case "C" -> "#93C47D"; // green
			case "B" -> "#E06666"; // red
			case "A" -> "#B07CE0"; // purple
			default -> "#E6B800"; // S / S80 / S84 - gold
		};
	}
}
