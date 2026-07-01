package com.l2skale.multisell.ui.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

/*
 * @author Skache
 */
public class SplashScreen extends JWindow
{
	private static final long serialVersionUID = 1L;

	private static final String SPLASH_SCREEN_DIR = "images/SplashScreen/";

	private final Image _image;

	public SplashScreen(long time, JFrame parent)
	{
		setBackground(new Color(0, 255, 0, 0));

		// Load random image from the predefined folder
		_image = getRandomImageFromClasspath(SPLASH_SCREEN_DIR);

		// Check if the image is loaded
		if (_image == null)
		{
			System.err.println("Failed to load image for splash screen.");
		}

		// Create ImageIcon to get width and height of the image
		final ImageIcon imageIcon = new ImageIcon(_image);
		setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		setVisible(true);

		// Schedule the timer to hide the splash screen after 'time' milliseconds
		new Timer().schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				// Swing work must run on the Event Dispatch Thread.
				SwingUtilities.invokeLater(() ->
				{
					setVisible(false);
					if (parent != null)
					{
						// Reveal the main window and bring it to the front.
						parent.setVisible(true);
						parent.toFront();
						parent.requestFocus();

						// Play sound when the window appears.
						Sound.playSound("inventory_open_01.wav");
					}
					dispose();
				});
			}
		}, imageIcon.getIconWidth() > 0 ? time : 100); // Show for 'time' ms, default to 100ms if no image
	}

	// Get a random image from the classpath folder
	private Image getRandomImageFromClasspath(String folderPath)
	{
		// Get the URL of the folder in the classpath (e.g., "images/SplashScreen")
		URL folderUrl = getClass().getClassLoader().getResource(folderPath);

		// Check if the folder exists
		if (folderUrl == null)
		{
			System.err.println("Folder not found: " + folderPath);
			return null;
		}

		// If the folder is a JAR file (i.e., URL starts with 'jar:')
		if (folderUrl.toString().startsWith("jar:"))
		{
			try
			{
				// Open the jar file
				JarURLConnection connection = (JarURLConnection) folderUrl.openConnection();
				JarFile jarFile = connection.getJarFile();
				Enumeration<JarEntry> entries = jarFile.entries();

				// Collect PNG files from the JAR
				List<String> imageFiles = new ArrayList<>();
				while (entries.hasMoreElements())
				{
					JarEntry entry = entries.nextElement();
					if (entry.getName().startsWith(folderPath) && entry.getName().endsWith(".png"))
					{
						imageFiles.add(entry.getName());
					}
				}

				// If no PNG files found in the JAR
				if (imageFiles.isEmpty())
				{
					System.err.println("No splash screen images found in the JAR for " + folderPath);
					return null;
				}

				// Randomly select an image from the list of image files in the JAR
				Random random = new Random();
				String selectedImage = imageFiles.get(random.nextInt(imageFiles.size()));

				// Load the image from the JAR
				InputStream inputStream = getClass().getClassLoader().getResourceAsStream(selectedImage);
				if (inputStream != null)
				{
					return ImageIO.read(inputStream);
				}
				else
				{
					System.err.println("Failed to load image from JAR: " + selectedImage);
				}
			}
			catch (IOException e)
			{
				System.err.println("Error accessing images in JAR: " + e.getMessage());
			}
		}
		else
		{
			// Code for accessing folder if it's on the filesystem (e.g., during development)
			File folder = new File(folderUrl.getFile());
			if (!folder.exists() || !folder.isDirectory())
			{
				System.err.println("Folder not found or is not a directory: " + folderPath);
				return null;
			}

			// List all PNG files in the folder
			File[] imageFiles = folder.listFiles(new FilenameFilter()
			{
				@Override
				public boolean accept(File dir, String name)
				{
					return name.toLowerCase().endsWith(".png");
				}
			});

			if (imageFiles == null || imageFiles.length == 0)
			{
				System.err.println("No splash screen images found in " + folderPath);
				return null;
			}

			// Select a random image
			Random random = new Random();
			File selectedImageFile = imageFiles[random.nextInt(imageFiles.length)];

			// Load image from file system (using its absolute path)
			return Toolkit.getDefaultToolkit().getImage(selectedImageFile.getAbsolutePath());
		}

		return null;
	}

	@Override
	public void paint(Graphics g)
	{
		if (_image != null)
		{
			g.drawImage(_image, 0, 0, null);
		}
	}
}
