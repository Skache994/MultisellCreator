package com.l2skale.multisell;

import java.awt.Dimension;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

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
		setTitle("Multisell XML Creator");
		setMinimumSize(new Dimension(850, 650));
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

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