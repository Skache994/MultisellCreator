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
