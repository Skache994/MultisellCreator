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
package com.l2skale.multisell;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.l2skale.multisell.managers.SettingsManager;
import com.l2skale.multisell.managers.ThemeManager;
import com.l2skale.multisell.ui.Gui;
import com.l2skale.multisell.ui.utils.SplashScreen;

/*
 * @author Skache
 */
public class MultisellCreator extends JFrame
{
	private static final long serialVersionUID = 1L;

	public MultisellCreator()
	{
		// Load saved user settings (last datapack path, etc.) before building the UI.
		SettingsManager.load();

		setTitle("Multisell XML Creator");
		setMinimumSize(new Dimension(850, 650));
		setSize(SettingsManager.getWindowWidth(850), SettingsManager.getWindowHeight(650));
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		// Remember the window size when the user closes the app.
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				SettingsManager.setWindowSize(getWidth(), getHeight());
			}
		});

		// Set initial Dark theme.
		ThemeManager.applyInitialTheme();

		// Show the splash screen before initializing the main window.
		new SplashScreen(3000, this);

		// Add icons.
		final List<Image> icons = new ArrayList<>();
		icons.add(new ImageIcon(getClass().getResource("/images/MSC_16x16.png")).getImage());
		icons.add(new ImageIcon(getClass().getResource("/images/MSC_32x32.png")).getImage());
		icons.add(new ImageIcon(getClass().getResource("/images/MSC_48x48.png")).getImage());
		icons.add(new ImageIcon(getClass().getResource("/images/MSC_64x64.png")).getImage());
		icons.add(new ImageIcon(getClass().getResource("/images/MSC_128x128.png")).getImage());
		setIconImages(icons);

		new Gui(this);
	}

	public static void main(String[] args)
	{
		// Construct the window; the splash screen reveals it when it finishes.
		SwingUtilities.invokeLater(() -> new MultisellCreator());
	}
}