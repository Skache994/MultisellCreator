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

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.border.Border;

/*
 * A gentle "breathing" glow drawn as a colored ring around a component, to draw the eye to the
 * one action the user should take next. Call start() to pulse and stop() to restore the component's
 * original look. It is a soft attention cue - not a bounce or flash.
 *
 * @author Skache
 */
public final class AttentionPulse
{
	private static final int PERIOD_MS = 40; // ~25 fps

	private final JComponent _target;
	private final Border _originalBorder;
	private final Color _color;
	private final Timer _timer;

	private double _phase;

	public AttentionPulse(JComponent target, Color color)
	{
		_target = target;
		_originalBorder = target.getBorder();
		_color = color;
		_timer = new Timer(PERIOD_MS, _ -> tick());
	}

	public void start()
	{
		if (!_timer.isRunning())
		{
			_phase = 0;
			_timer.start();
		}
	}

	public void stop()
	{
		_timer.stop();
		_target.setBorder(_originalBorder);
	}

	private void tick()
	{
		_phase += 0.12;

		// A sine wave mapped to 0..1 gives the smooth breathe; drive the ring's opacity with it.
		final double breathe = (Math.sin(_phase) + 1) / 2;
		final int alpha = (int) (70 + (breathe * 165)); // 70..235
		final Color glow = new Color(_color.getRed(), _color.getGreen(), _color.getBlue(), alpha);
		_target.setBorder(BorderFactory.createLineBorder(glow, 2, true));
	}
}
