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
package com.l2skale.multisell.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/*
 * Reads a Lineage 2 Unreal (.utx) texture package - the client's icon files. The file
 * must already be decrypted (a plain Unreal Engine 2 package); the tool never decrypts
 * client files itself. This parses the package tables (names, imports, exports) so the
 * textures inside can be found by name.
 *
 * An item's icon value is "package.texture" (e.g. "icon.weapon_sword_i00"): the package
 * picks the .utx file, the texture is an export inside it. This first step opens a package
 * and lists its texture names; decoding the pixels comes next.
 *
 * @author Skache
 */
public class UtxReader
{
	private static final int UNREAL_TAG = 0x9E2A83C1;

	private final ByteBuffer _buf;
	private final String[] _names;
	private final Import[] _imports;
	private final Export[] _exports;

	// One entry of the package's import table (a reference to an object in another package).
	private record Import(int classPackage, int className, int outer, int objectName)
	{
	}

	// One entry of the package's export table (an object stored in this package, e.g. a texture).
	private record Export(int clazz, int superClass, int outer, int objectName, int flags, int serialSize, int serialOffset)
	{
	}

	private UtxReader(ByteBuffer buf)
	{
		_buf = buf;
		_buf.order(ByteOrder.LITTLE_ENDIAN);

		if (_buf.getInt(0) != UNREAL_TAG)
		{
			throw new IllegalStateException("Not an Unreal package (bad tag after decrypt).");
		}

		final int nameCount = _buf.getInt(12);
		final int nameOffset = _buf.getInt(16);
		final int exportCount = _buf.getInt(20);
		final int exportOffset = _buf.getInt(24);
		final int importCount = _buf.getInt(28);
		final int importOffset = _buf.getInt(32);

		_names = readNames(nameOffset, nameCount);
		_imports = readImports(importOffset, importCount);
		_exports = readExports(exportOffset, exportCount);
	}

	// Open a .utx file and parse its tables. The file is read only; if it is a "Lineage2Ver111/121"
	// encrypted client file it is decrypted into a memory copy - the file on disk is never modified.
	public static UtxReader open(File file) throws Exception
	{
		return open(Files.readAllBytes(file.toPath()), file.getName());
	}

	// Open a .utx from raw bytes (e.g. a bundled classpath resource). The name is only used to
	// compute the decryption key, so it must be the package's real file name (e.g. "Icon.utx").
	public static UtxReader open(byte[] raw, String name)
	{
		return new UtxReader(ByteBuffer.wrap(toPackage(raw, name)));
	}

	// Returns the raw Unreal package bytes. A file that is already a package is returned unchanged;
	// a "Lineage2Ver111/121" file is decrypted into a NEW byte array (the input/client file is not
	// touched). Ver 111/121 is a single-byte XOR of the body, keyed by the file name.
	private static byte[] toPackage(byte[] raw, String fileName)
	{
		if ((raw.length >= 4) && (le32(raw, 0) == UNREAL_TAG))
		{
			return raw; // already a decrypted package
		}

		final String header = new String(raw, 0, Math.min(28, raw.length), StandardCharsets.UTF_16LE);
		if (!header.startsWith("Lineage2Ver"))
		{
			throw new IllegalStateException(fileName + " is not a Lineage 2 .utx package.");
		}

		final String version = header.substring("Lineage2Ver".length(), "Lineage2Ver".length() + 3);
		if (!version.equals("111") && !version.equals("121"))
		{
			throw new IllegalStateException(fileName + ": unsupported .utx encryption version " + version);
		}

		int key = 0;
		for (char c : fileName.toLowerCase().toCharArray())
		{
			key += c;
		}
		key &= 0xFF;

		final byte[] pkg = new byte[raw.length - 28];
		for (int i = 0; i < pkg.length; i++)
		{
			pkg[i] = (byte) (raw[i + 28] ^ key);
		}
		return pkg;
	}

	private String[] readNames(int offset, int count)
	{
		_buf.position(offset);
		final String[] names = new String[count];
		for (int i = 0; i < count; i++)
		{
			names[i] = readString();
			_buf.getInt(); // Name flags - unused.
		}
		return names;
	}

	private Import[] readImports(int offset, int count)
	{
		_buf.position(offset);
		final Import[] imports = new Import[count];
		for (int i = 0; i < count; i++)
		{
			imports[i] = new Import(readIndex(), readIndex(), _buf.getInt(), readIndex());
		}
		return imports;
	}

	private Export[] readExports(int offset, int count)
	{
		_buf.position(offset);
		final Export[] exports = new Export[count];
		for (int i = 0; i < count; i++)
		{
			final int clazz = readIndex();
			final int superClass = readIndex();
			final int outer = _buf.getInt();
			final int objectName = readIndex();
			final int flags = _buf.getInt();
			final int serialSize = readIndex();
			final int serialOffset = serialSize > 0 ? readIndex() : 0;
			exports[i] = new Export(clazz, superClass, outer, objectName, flags, serialSize, serialOffset);
		}
		return exports;
	}

	// The class name of an object reference: negative -> import, positive -> export, 0 -> a class itself.
	private String className(int objectRef)
	{
		if (objectRef < 0)
		{
			return _names[_imports[(-objectRef) - 1].objectName()];
		}
		if (objectRef > 0)
		{
			return _names[_exports[objectRef - 1].objectName()];
		}
		return "Class";
	}

	// An Unreal name: a compact-index length then the characters. A positive length is that many
	// ASCII bytes; a negative length is that many UTF-16 code units. Both include a trailing null.
	private String readString()
	{
		final int length = readIndex();
		if (length == 0)
		{
			return "";
		}

		if (length > 0)
		{
			final byte[] bytes = new byte[length];
			_buf.get(bytes);
			return new String(bytes, 0, length - 1, StandardCharsets.US_ASCII);
		}

		final int units = -length;
		final byte[] bytes = new byte[units * 2];
		_buf.get(bytes);
		return new String(bytes, 0, (units - 1) * 2, StandardCharsets.UTF_16LE);
	}

	// Unreal's FCompactIndex: a variable-length signed integer.
	private int readIndex()
	{
		int b = _buf.get() & 0xFF;
		final boolean negative = (b & 0x80) != 0;
		int value = b & 0x3F;
		if ((b & 0x40) != 0)
		{
			int shift = 6;
			do
			{
				b = _buf.get() & 0xFF;
				value |= (b & 0x7F) << shift;
				shift += 7;
			}
			while ((b & 0x80) != 0);
		}
		return negative ? -value : value;
	}

	// The array index that follows an array-flagged property: 1, 2 or 4 bytes.
	private int readArrayIndex()
	{
		final int b = _buf.get() & 0xFF;
		if ((b & 0x80) == 0)
		{
			return b;
		}
		if ((b & 0xC0) == 0x80)
		{
			return ((b & 0x3F) << 8) | (_buf.get() & 0xFF);
		}
		return ((b & 0x3F) << 24) | ((_buf.get() & 0xFF) << 16) | ((_buf.get() & 0xFF) << 8) | (_buf.get() & 0xFF);
	}

	private static int le32(byte[] b, int i)
	{
		return (b[i] & 0xFF) | ((b[i + 1] & 0xFF) << 8) | ((b[i + 2] & 0xFF) << 16) | ((b[i + 3] & 0xFF) << 24);
	}

	// Decode a texture to an image by name, or null if this package has no such texture.
	public BufferedImage readTexture(String name)
	{
		final Export export = findTexture(name);
		if (export == null)
		{
			return null;
		}

		final int[] props = readProps(export); // {format, uSize, vSize}
		final int format = props[0];
		final int width = props[1];
		final int height = props[2];

		// Mip 0 (full size) is the last mip, so its data sits right before the object's 10-byte
		// trailer (USize, VSize, UBits, VBits). Grab exactly the largest mip - no need to walk the chain.
		final int dataSize = mipDataSize(format, width, height);
		final int dataStart = (export.serialOffset() + export.serialSize()) - 10 - dataSize;
		final byte[] data = new byte[dataSize];
		_buf.position(dataStart);
		_buf.get(data);

		return decode(format, width, height, data);
	}

	// Every texture name in this package (exports whose class is "Texture").
	public List<String> textureNames()
	{
		final List<String> names = new ArrayList<>();
		for (Export e : _exports)
		{
			if ("Texture".equals(className(e.clazz())))
			{
				names.add(_names[e.objectName()]);
			}
		}
		return names;
	}

	private Export findTexture(String name)
	{
		for (Export e : _exports)
		{
			if ("Texture".equals(className(e.clazz())) && _names[e.objectName()].equals(name))
			{
				return e;
			}
		}
		return null;
	}

	// Reads the object's property list, returning {Format, USize, VSize}.
	private int[] readProps(Export export)
	{
		_buf.position(export.serialOffset());
		int format = -1;
		int uSize = 0;
		int vSize = 0;

		while (true)
		{
			final String pname = _names[readIndex()];
			if ("None".equals(pname))
			{
				break;
			}

			final int info = _buf.get() & 0xFF;
			final int type = info & 0x0F;
			final int sizeCode = (info >> 4) & 0x07;
			if (type == 3) // bool - an array-flagged bool carries an index byte; otherwise nothing follows
			{
				if ((info & 0x80) != 0)
				{
					readArrayIndex();
				}
				continue;
			}
			if (type == 10) // struct - a struct name precedes the value
			{
				readIndex();
			}

			final int size = switch (sizeCode)
			{
				case 0 -> 1;
				case 1 -> 2;
				case 2 -> 4;
				case 3 -> 12;
				case 4 -> 16;
				case 5 -> _buf.get() & 0xFF;
				case 6 -> _buf.getShort() & 0xFFFF;
				default -> _buf.getInt();
			};

			if ((info & 0x80) != 0) // array element - an array index precedes the value
			{
				readArrayIndex();
			}

			final int valuePos = _buf.position();
			int value = 0;
			if (size <= 4)
			{
				for (int i = 0; i < size; i++)
				{
					value |= (_buf.get() & 0xFF) << (8 * i);
				}
			}
			_buf.position(valuePos + size);

			switch (pname)
			{
				case "Format" -> format = value;
				case "USize" -> uSize = value;
				case "VSize" -> vSize = value;
				default ->
				{
					// other properties are not needed
				}
			}
		}
		return new int[]
		{
			format,
			uSize,
			vSize
		};
	}

	// Bytes of one full-size mip for the given texture format.
	private static int mipDataSize(int format, int width, int height)
	{
		final int blocksW = Math.max(1, (width + 3) / 4);
		final int blocksH = Math.max(1, (height + 3) / 4);
		return switch (format)
		{
			case 3 -> blocksW * blocksH * 8; // DXT1
			case 7, 8 -> blocksW * blocksH * 16; // DXT3 / DXT5
			case 5 -> width * height * 4; // RGBA8
			default -> throw new IllegalStateException("Unsupported texture format " + format);
		};
	}

	private static BufferedImage decode(int format, int width, int height, byte[] data)
	{
		final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		switch (format)
		{
			case 3 -> decodeDxt1(data, width, height, image);
			case 5 -> decodeRgba8(data, width, height, image);
			case 7 -> decodeDxt3(data, width, height, image);
			case 8 -> decodeDxt5(data, width, height, image);
			default -> throw new IllegalStateException("Unsupported texture format " + format);
		}
		return image;
	}

	// DXT1 (BC1): 4x4 blocks, 8 bytes each - two 565 colors then 2-bit per-pixel indices.
	private static void decodeDxt1(byte[] data, int width, int height, BufferedImage image)
	{
		int pos = 0;
		for (int by = 0; by < height; by += 4)
		{
			for (int bx = 0; bx < width; bx += 4)
			{
				final int c0 = (data[pos] & 0xFF) | ((data[pos + 1] & 0xFF) << 8);
				final int c1 = (data[pos + 2] & 0xFF) | ((data[pos + 3] & 0xFF) << 8);
				final int bits = (data[pos + 4] & 0xFF) | ((data[pos + 5] & 0xFF) << 8) | ((data[pos + 6] & 0xFF) << 16) | ((data[pos + 7] & 0xFF) << 24);
				pos += 8;

				final int[] colors = new int[4];
				colors[0] = rgb565(c0);
				colors[1] = rgb565(c1);
				if (c0 > c1)
				{
					colors[2] = mix(colors[0], colors[1], 2, 1);
					colors[3] = mix(colors[0], colors[1], 1, 2);
				}
				else
				{
					colors[2] = mix(colors[0], colors[1], 1, 1);
					colors[3] = 0x00000000; // transparent
				}

				for (int py = 0; py < 4; py++)
				{
					for (int px = 0; px < 4; px++)
					{
						final int x = bx + px;
						final int y = by + py;
						if ((x < width) && (y < height))
						{
							final int index = (bits >> (2 * ((py * 4) + px))) & 3;
							image.setRGB(x, y, colors[index]);
						}
					}
				}
			}
		}
	}

	// DXT3 (BC2): 16-byte blocks - 8 bytes of 4-bit explicit alpha, then a 4-colour DXT1 colour block.
	private static void decodeDxt3(byte[] data, int width, int height, BufferedImage image)
	{
		int pos = 0;
		for (int by = 0; by < height; by += 4)
		{
			for (int bx = 0; bx < width; bx += 4)
			{
				long alpha = 0;
				for (int i = 0; i < 8; i++)
				{
					alpha |= (long) (data[pos + i] & 0xFF) << (8 * i);
				}
				final int[] colors = colorBlock(data, pos + 8);
				final int bits = blockBits(data, pos + 12);
				pos += 16;

				for (int py = 0; py < 4; py++)
				{
					for (int px = 0; px < 4; px++)
					{
						final int x = bx + px;
						final int y = by + py;
						if ((x < width) && (y < height))
						{
							final int i = (py * 4) + px;
							final int a = (int) ((alpha >> (4 * i)) & 0xF) * 17;
							image.setRGB(x, y, (a << 24) | (colors[(bits >> (2 * i)) & 3] & 0xFFFFFF));
						}
					}
				}
			}
		}
	}

	// DXT5 (BC3): 16-byte blocks - two alpha endpoints + 3-bit alpha indices, then a 4-colour block.
	private static void decodeDxt5(byte[] data, int width, int height, BufferedImage image)
	{
		int pos = 0;
		for (int by = 0; by < height; by += 4)
		{
			for (int bx = 0; bx < width; bx += 4)
			{
				final int a0 = data[pos] & 0xFF;
				final int a1 = data[pos + 1] & 0xFF;
				long aIndex = 0;
				for (int i = 0; i < 6; i++)
				{
					aIndex |= (long) (data[pos + 2 + i] & 0xFF) << (8 * i);
				}
				final int[] colors = colorBlock(data, pos + 8);
				final int bits = blockBits(data, pos + 12);
				pos += 16;

				for (int py = 0; py < 4; py++)
				{
					for (int px = 0; px < 4; px++)
					{
						final int x = bx + px;
						final int y = by + py;
						if ((x < width) && (y < height))
						{
							final int i = (py * 4) + px;
							final int a = dxt5Alpha(a0, a1, (int) ((aIndex >> (3 * i)) & 7));
							image.setRGB(x, y, (a << 24) | (colors[(bits >> (2 * i)) & 3] & 0xFFFFFF));
						}
					}
				}
			}
		}
	}

	// The four colours of a DXT colour block (always the 4-colour interpolation - alpha comes separately).
	private static int[] colorBlock(byte[] data, int pos)
	{
		final int c0 = (data[pos] & 0xFF) | ((data[pos + 1] & 0xFF) << 8);
		final int c1 = (data[pos + 2] & 0xFF) | ((data[pos + 3] & 0xFF) << 8);
		final int a = rgb565(c0);
		final int b = rgb565(c1);
		return new int[]
		{
			a,
			b,
			mix(a, b, 2, 1),
			mix(a, b, 1, 2)
		};
	}

	private static int blockBits(byte[] data, int pos)
	{
		return (data[pos] & 0xFF) | ((data[pos + 1] & 0xFF) << 8) | ((data[pos + 2] & 0xFF) << 16) | ((data[pos + 3] & 0xFF) << 24);
	}

	private static int dxt5Alpha(int a0, int a1, int i)
	{
		if (i == 0)
		{
			return a0;
		}
		if (i == 1)
		{
			return a1;
		}
		if (a0 > a1)
		{
			return (((8 - i) * a0) + ((i - 1) * a1)) / 7;
		}
		if (i == 6)
		{
			return 0;
		}
		if (i == 7)
		{
			return 255;
		}
		return (((6 - i) * a0) + ((i - 1) * a1)) / 5;
	}

	// Unreal stores TEXF_RGBA8 as B, G, R, A bytes per pixel.
	private static void decodeRgba8(byte[] data, int width, int height, BufferedImage image)
	{
		int pos = 0;
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				final int b = data[pos] & 0xFF;
				final int g = data[pos + 1] & 0xFF;
				final int r = data[pos + 2] & 0xFF;
				final int a = data[pos + 3] & 0xFF;
				pos += 4;
				image.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
			}
		}
	}

	private static int rgb565(int c)
	{
		final int r = (c >> 11) & 0x1F;
		final int g = (c >> 5) & 0x3F;
		final int b = c & 0x1F;
		return 0xFF000000 | (((r * 255) / 31) << 16) | (((g * 255) / 63) << 8) | ((b * 255) / 31);
	}

	// Weighted blend of two opaque colors: (a*wa + b*wb) / (wa+wb).
	private static int mix(int a, int b, int wa, int wb)
	{
		final int total = wa + wb;
		final int r = ((((a >> 16) & 0xFF) * wa) + (((b >> 16) & 0xFF) * wb)) / total;
		final int g = ((((a >> 8) & 0xFF) * wa) + (((b >> 8) & 0xFF) * wb)) / total;
		final int bl = (((a & 0xFF) * wa) + ((b & 0xFF) * wb)) / total;
		return 0xFF000000 | (r << 16) | (g << 8) | bl;
	}

}
