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
 * One attribute the server's multisell.xsd allows on a <list>, <ingredient> or
 * <production> - its name, its value shape, and whether the schema marks it required.
 * Read straight from the xsd so it always reflects THIS server's rules.
 *
 * @author Skache
 */
public class SchemaAttribute
{
	private final String _name;
	private final AttributeType _type;
	private final boolean _required;

	public SchemaAttribute(String name, AttributeType type, boolean required)
	{
		_name = name;
		_type = type;
		_required = required;
	}

	public String getName()
	{
		return _name;
	}

	public AttributeType getType()
	{
		return _type;
	}

	// True when the xsd declares use="required" (e.g. an ingredient's id and count).
	public boolean isRequired()
	{
		return _required;
	}

	@Override
	public String toString()
	{
		return _name + " (" + _type + (_required ? ", required" : "") + ")";
	}
}
