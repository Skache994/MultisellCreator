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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/*
 * Formats and parses amounts with dot thousands separators (1.000.000). Used for
 * display and input only - saved XML keeps the raw numbers.
 *
 * @author Skache
 */
public final class Numbers
{
	private static final DecimalFormat FORMAT;
	static
	{
		final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setGroupingSeparator('.');
		FORMAT = new DecimalFormat("#,##0", symbols);
	}

	private Numbers()
	{
	}

	// 1000000 -> "1.000.000"
	public static String format(long value)
	{
		return FORMAT.format(value);
	}

	// The display convention for item counts: 1000 -> " (1.000)", but 1 (or less) -> ""
	// (a single item needs no count). Shared by every place that shows an item count.
	public static String countSuffix(long count)
	{
		return count > 1 ? " (" + format(count) + ")" : "";
	}

	// "1.000.000" or "1000000" -> 1000000 (any non-digit separators are ignored).
	public static int parse(String text)
	{
		final String digits = text.replaceAll("[^0-9]", "");
		if (digits.isEmpty())
		{
			return 0;
		}

		try
		{
			final long value = Long.parseLong(digits);
			return value > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
		}
		catch (NumberFormatException e)
		{
			return Integer.MAX_VALUE;
		}
	}
}
