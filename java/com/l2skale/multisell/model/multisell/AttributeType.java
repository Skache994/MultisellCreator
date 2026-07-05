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
package com.l2skale.multisell.model.multisell;

/*
 * The kind of value a multisell attribute holds, boiled down from the XSD type to
 * the few shapes the UI cares about: a checkbox for booleans, a number field for
 * integers/decimals, a plain text field for anything else (e.g. useRate's token,
 * which can be a number OR a config field name). This is the bridge from "what the
 * server's xsd declares" to "what widget we show".
 *
 * @author Skache
 */
public enum AttributeType
{
	BOOLEAN,
	INTEGER,
	DECIMAL,
	TEXT;

	// Map an XSD type (e.g. "xs:boolean", "xs:positiveInteger", "xs:double", "xs:token") to our shape.
	// The prefix is ignored; unknown types fall back to TEXT so an unusual xsd still yields an editor.
	public static AttributeType fromXsd(String xsdType)
	{
		if (xsdType == null)
		{
			return TEXT;
		}

		final int colon = xsdType.indexOf(':');
		final String type = (colon >= 0 ? xsdType.substring(colon + 1) : xsdType).toLowerCase();

		if (type.equals("boolean"))
		{
			return BOOLEAN;
		}
		if (type.equals("double") || type.equals("decimal") || type.equals("float"))
		{
			return DECIMAL;
		}
		if (type.contains("int") || type.equals("long") || type.equals("short"))
		{
			return INTEGER; // integer, positiveInteger, nonNegativeInteger, int, ...
		}
		return TEXT; // token, string, NMTOKEN, ...
	}
}
