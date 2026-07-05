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
	private static void play(String soundFileName)
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