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
package com.l2skale.multisell.ui;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;

import com.l2skale.multisell.managers.ThemeManager;
import com.l2skale.multisell.ui.utils.ResourceIcons;

/*
 * @author Skache
 */
public class MenuBar
{
	private static final String GITHUB_URL = "https://github.com/Skache994/MultisellCreator";
	private static final String WEBSITE_URL = "http://www.l2skale.com";

	public static JMenuBar createMenuBar(JFrame parentFrame, Runnable onOpenDatapack, Runnable onNewMultisell, Runnable onOpenMultisell, Runnable onSaveMultisell, Runnable onDeleteMultisell)
	{
		JMenuBar menuBar = new JMenuBar();

		// File Menu
		JMenu fileMenu = new JMenu("File");
		JMenuItem openDatapackItem = new JMenuItem("Load Items...");
		JMenuItem newItem = new JMenuItem("New Multisell");
		JMenuItem openItem = new JMenuItem("Open Multisell...");
		JMenuItem saveItem = new JMenuItem("Save Multisell");
		JMenuItem deleteItem = new JMenuItem("Delete Multisell");
		JMenuItem exitItem = new JMenuItem("Exit");

		openDatapackItem.addActionListener(_ -> onOpenDatapack.run());
		newItem.addActionListener(_ -> onNewMultisell.run());
		openItem.addActionListener(_ -> onOpenMultisell.run());
		saveItem.addActionListener(_ -> onSaveMultisell.run());
		deleteItem.addActionListener(_ -> onDeleteMultisell.run());
		exitItem.addActionListener(_ -> System.exit(0));

		fileMenu.add(openDatapackItem);
		fileMenu.addSeparator();
		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(deleteItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);

		// Help Menu
		JMenu helpMenu = new JMenu("Help");
		JMenuItem howToItem = new JMenuItem("How to Use");
		JMenuItem githubItem = new JMenuItem("GitHub");
		JMenuItem aboutItem = new JMenuItem("About");

		howToItem.addActionListener(_ -> showHowToDialog(parentFrame));
		githubItem.addActionListener(_ -> browse(GITHUB_URL));
		aboutItem.addActionListener(_ -> showAboutDialog(parentFrame));

		helpMenu.add(howToItem);
		helpMenu.addSeparator();
		helpMenu.add(githubItem);
		helpMenu.addSeparator();
		helpMenu.add(aboutItem);

		menuBar.add(fileMenu);
		menuBar.add(helpMenu);

		return menuBar;
	}

	// A short quick-start guide.
	private static void showHowToDialog(JFrame parentFrame)
	{
		final String textColor = ThemeManager.isDarkMode() ? "#E6E6E6" : "#202020";

		final String html = "<html><body style='font-family:Arial; width:360px; color:" + textColor + ";'>" //
			+ "<h3>How to use</h3>" //
			+ "<ol>" //
			+ "<li><b>Load Items</b> - point at your server's <i>game</i> (or <i>data</i>) folder to load items.</li>" //
			+ "<li><b>Load Icons</b> (optional) - only if icons are missing, point at your Lineage 2 game folder.</li>" //
			+ "<li><b>New</b> or <b>Open</b> a multisell.</li>" //
			+ "<li><b>New Entry</b>, then add items (drag from the list or right-click) and set NPCs / options.</li>" //
			+ "<li><b>Save</b> - writes <i>data/multisell/&lt;id&gt;.xml</i> into the datapack.</li>" //
			+ "</ol>" //
			+ "<p>Tips: right-click an item or entry to remove it, drag it onto the trash bin to delete, " //
			+ "and double-click an item to change its amount.</p>" //
			+ "</body></html>";

		JOptionPane.showMessageDialog(parentFrame, htmlPane(html), "How to Use", JOptionPane.INFORMATION_MESSAGE);
	}

	// The About dialog.
	private static void showAboutDialog(JFrame parentFrame)
	{
		final boolean dark = ThemeManager.isDarkMode();
		final String textColor = dark ? "#E6E6E6" : "#202020";
		final String subColor = dark ? "#AAAAAA" : "#666666";
		final String linkColor = dark ? "#7FB3FF" : "#0066CC";

		final URL discordUrl = MenuBar.class.getResource("/images/Discord_32x32.png");
		final String discord = (discordUrl != null) ? ("<img src='" + discordUrl + "' width='16' height='16'>&nbsp;<b>skache</b>") : "<b>skache</b>";

		final String html = "<html><body style='font-family:Arial; text-align:center; color:" + textColor + ";'>" //
			+ "<h2 style='margin:2px;'>Multisell XML Creator</h2>" //
			+ "<div style='margin:2px;'>Version 1.0</div>" //
			+ "<div style='margin:2px; color:" + subColor + ";'>Create L2J Mobius multisell XML from a live datapack</div>" //
			+ "<div style='margin:8px 2px 2px 2px;'>by <b>Vlatko Pockov</b></div>" //
			+ "<div style='margin:4px;'>" + discord + "</div>" //
			+ "<div style='margin:2px;'><a href='" + WEBSITE_URL + "' style='color:" + linkColor + ";'>www.l2skale.com</a></div>" //
			+ "<div style='margin:2px;'><a href='" + GITHUB_URL + "' style='color:" + linkColor + ";'>GitHub repository</a></div>" //
			+ "</body></html>";

		final ImageIcon appIcon = ResourceIcons.loadResourceIconsIcon("MSC_64x64.png");
		JOptionPane.showMessageDialog(parentFrame, htmlPane(html), "About", JOptionPane.INFORMATION_MESSAGE, appIcon != null ? appIcon : new ImageIcon());
	}

	// A read-only, transparent HTML pane whose links open in the browser.
	private static JEditorPane htmlPane(String html)
	{
		final JEditorPane pane = new JEditorPane("text/html", html);
		pane.setEditable(false);
		pane.setFocusable(false);
		pane.setOpaque(false);
		pane.addHyperlinkListener(e ->
		{
			if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType()))
			{
				browse(e.getURL().toString());
			}
		});
		return pane;
	}

	// Open a URL in the default browser.
	private static void browse(String url)
	{
		try
		{
			Desktop.getDesktop().browse(new URI(url));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
