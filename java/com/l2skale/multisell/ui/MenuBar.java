package com.l2skale.multisell.ui;

import java.awt.Desktop;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;

import com.l2skale.multisell.ui.utils.ResourceIcons;

/*
 * @author Skache
 */
public class MenuBar
{
	// Method to create the JMenuBar
	public static JMenuBar createMenuBar(JFrame parentFrame)
	{
		JMenuBar menuBar = new JMenuBar();

		// File Menu
		JMenu fileMenu = new JMenu("File");
		JMenuItem loadItem = new JMenuItem("Load Multisell");
		JMenuItem saveItem = new JMenuItem("Save Multisell");
		JMenuItem exitItem = new JMenuItem("Exit");

		// Placeholder Action for load and save
		loadItem.addActionListener(e -> loadMultisell());
		saveItem.addActionListener(e -> saveMultisell());
		exitItem.addActionListener(e -> System.exit(0));

		fileMenu.add(loadItem);
		fileMenu.add(saveItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);

		// Edit Menu
		JMenu editMenu = new JMenu("Edit");
		JMenuItem deleteItem = new JMenuItem("Delete Selected");

		// Placeholder Action for delete
		deleteItem.addActionListener(e -> deleteSelectedMultisell());

		editMenu.add(deleteItem);

		// Help Menu
		JMenu helpMenu = new JMenu("Help");
		JMenuItem aboutItem = new JMenuItem("About");

		// Action for About item
		aboutItem.addActionListener(e -> showAboutDialog(parentFrame));

		helpMenu.add(aboutItem);

		// Add menus to the bar
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(helpMenu);

		return menuBar;
	}

	// Show the About dialog.
	private static void showAboutDialog(JFrame parentFrame)
	{
		// Load the Discord icon with error handling
		ImageIcon discordIcon = null;
		try
		{
			discordIcon = ResourceIcons.loadResourceIconsIcon("Discord_32x32.png");
			if (discordIcon == null)
			{
				throw new Exception("Discord icon not found.");
			}
		}
		catch (Exception e)
		{
			System.err.println("Error loading Discord icon: " + e.getMessage());
			discordIcon = new ImageIcon(); // Use a default/fallback icon if there's an error
		}

		// HTML content with embedded image
		String aboutMessage = "<html>" + "<h2>Multisell Creator</h2>" + "<p><b>Version 1.0</b></p>" + "<p>Developed by <b>Skache</b></p>" + "<p><img src='" + discordIcon.toString() + "' width='20' height='20'> " + "&nbsp;&nbsp;&nbsp;" // Adds three spaces
				+ "<a style='font-size: 12px; font-weight: bold; color: #FF00FF;'>skache</a></p>" + "</html>";

		// Load the app's icon with error handling
		ImageIcon appIcon = null;
		try
		{
			appIcon = ResourceIcons.loadResourceIconsIcon("MSC_64x64.png");
			if (appIcon == null)
			{
				throw new Exception("App icon not found.");
			}
		}
		catch (Exception e)
		{
			System.err.println("Error loading app icon: " + e.getMessage());
			appIcon = new ImageIcon(); // Use a default/fallback icon if there's an error
		}

		// Create a JEditorPane to display HTML content
		JEditorPane editorPane = new JEditorPane("text/html", aboutMessage);
		editorPane.setEditable(false);
		editorPane.setBackground(parentFrame.getBackground());
		editorPane.setFocusable(false);

		// Make the links clickable
		editorPane.addHyperlinkListener(e ->
		{
			if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType()))
			{
				try
				{
					Desktop.getDesktop().browse(e.getURL().toURI());
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});

		// Show the dialog with custom icon and editor pane
		JOptionPane.showMessageDialog(parentFrame, new JScrollPane(editorPane), "About", JOptionPane.INFORMATION_MESSAGE, appIcon);
	}

	// Placeholder for loading a multisell file
	private static void loadMultisell()
	{
		// Add logic to load the multisell data
		System.out.println("Load Multisell...");
	}

	// Placeholder for saving a multisell file
	private static void saveMultisell()
	{
		// Add logic to save the multisell data
		System.out.println("Save Multisell...");
	}

	// Placeholder for deleting selected multisell item
	private static void deleteSelectedMultisell()
	{
		// Add logic to delete selected multisell
		System.out.println("Delete Selected Multisell...");
	}
}
