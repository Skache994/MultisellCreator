package com.l2skale.multisell.ui.utils;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/*
 * @authos Skache
 */
public class Sound
{
	private static final String SOUND_DIR = "sounds/"; // Directory in the classpath (e.g., "sounds/")

	/**
	 * Plays a sound file synchronously from the classpath.
	 * 
	 * @param soundFileName The name of the sound file (must be in WAV format)
	 */
	public static void play(String soundFileName)
	{
		// Get the URL of the sound file from the classpath
		URL soundUrl = Sound.class.getClassLoader().getResource(SOUND_DIR + soundFileName);

		if (soundUrl == null)
		{
			System.out.println("Error: Sound file not found in classpath - " + soundFileName);
			return;
		}

		try
		{
			// Convert the URL to an AudioInputStream
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundUrl);
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);

			// Play the sound
			clip.start();

			// Optionally, add a listener to handle the end of the sound and cleanup
			clip.addLineListener(event ->
			{
				if (event.getType() == LineEvent.Type.STOP)
				{
					clip.close(); // Close the clip after the sound finishes
				}
			});
		}
		catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)
		{
			e.printStackTrace();
			System.out.println("Error playing sound: " + e.getMessage());
		}
	}

	/**
	 * Plays a sound asynchronously, so it does not block the UI thread.
	 * 
	 * @param soundFileName The name of the sound file (must be in WAV format)
	 */
	public static void playSound(String soundFileName)
	{
		new Thread(() -> play(soundFileName)).start();
	}
}