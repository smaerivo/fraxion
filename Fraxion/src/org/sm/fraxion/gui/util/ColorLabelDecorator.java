// ----------------------------------------
// Filename      : ColorLabelDecorator.java
// Author        : Sven Maerivoet
// Last modified : 06/09/2014
// Target        : Java VM (1.8)
// ----------------------------------------

/**
 * Copyright 2003-2015 Sven Maerivoet
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sm.fraxion.gui.util;

import java.awt.*;
import javax.swing.*;

/**
 * The <CODE>ColorLabelDecorator</CODE> class provides a label decorator for a menu item showing a colour.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 06/09/2014
 */
public final class ColorLabelDecorator extends ImageIcon
{
	// the icon's fixed dimensions
	private static final int kIconWidth = 154;
	private static final int kIconHeight = 20;

	// internal datastructures
	private Color fColor;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>ColorLabelDecorator</CODE> object with a default black colour.
	 */
	public ColorLabelDecorator()
	{
		setColor(Color.BLACK);
	}

	/**
	 * Constructs a <CODE>ColorLabelDecorator</CODE> object with a specified colour.
	 * 
	 * @param color  the colour to copy
	 */
	public ColorLabelDecorator(Color color)
	{
		setColor(color);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Sets the colour.
	 *
	 * @param color the colour
	 */
	public void setColor(Color color)
	{
		fColor = color;
	}

	/**
	 * Returns the icon's width.
	 *
	 * @return the icon's width
	 */
	@Override
	public int getIconWidth()
	{
		return kIconWidth;
	}

	/**
	 * Returns the icon's height.
	 *
	 * @return the icon's height
	 */
	@Override
	public int getIconHeight()
	{
		return kIconHeight;
	}

	/**
	 * Paints the icon. The top-left corner of the icon is drawn at the point (x, y) in the coordinate
	 * space of the graphics context g. If this icon has no image observer, this method uses the c component
	 * as the observer.
	 *
	 * @param c the component to be used as the observer if this icon has no image observer
	 * @param g the graphics context
	 * @param x the X coordinate of the icon's top-left corner
	 * @param y the Y coordinate of the icon's top-left corner
	 */
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		g.setColor(fColor);
		g.fillRect(x,y,kIconWidth,kIconHeight);
	}
}
