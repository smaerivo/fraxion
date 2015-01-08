// -----------------------------------
// Filename      : ScreenLocation.java
// Author        : Sven Maerivoet
// Last modified : 12/10/2014
// Target        : Java VM (1.8)
// -----------------------------------

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

package org.sm.fraxion.fractals.util;

/**
 * The <CODE>ScreenLocation</CODE> class provides a structure that holds a location on the screen.
 * <P>
 * <B>Note that this class can not be subclassed!</B>
 * 
 * @author  Sven Maerivoet
 * @version 12/10/2014
 */
public final class ScreenLocation
{
	/**
	 * The column of the screen location.
	 */
	public int fX;

	/**
	 * The row of the screen location.
	 */
	public int fY;

	/*****************
	 * CONSTRUCTORS  *
	 *****************/

	/**
	 * Constructs a <CODE>ScreenLocation</CODE> object equal to (0,0).
	 */
	public ScreenLocation()
	{
	}

	/**
	 * Constructs a <CODE>ScreenLocation</CODE> object equal to the specified location.
	 *
	 * @param x  the column corresponding to the screen location
	 * @param y  the row corresponding to the screen location
	 */
	public ScreenLocation(int x, int y)
	{
		set(x,y);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Sets the value for this <CODE>ScreenLocation</CODE> to a specified location.
	 *
	 * @param x  the column corresponding to the screen location
	 * @param y  the row corresponding to the screen location
	 */
	public void set(int x, int y)
	{
		fX = x;
		fY = y;
	}

	/**
	 * Returns a <CODE>String</CODE> representation of this <CODE>ScreenLocation</CODE>
	 *
	 * @return a <CODE>String</CODE> representation of this <CODE>ScreenLocation</CODE>
	 */
	public String toString()
	{
		return ("(" + String.valueOf(fX) + "," + String.valueOf(fY) + ")");
	}

	/**
	 * Forces a partial order on two screen locations s1 and s2 such that (s1X,s1Y) &le; (s2X,s2Y).
	 *
	 * @param s1  the first screen location
	 * @param s2  the second screen location
	 */	
	public static void forcePartialOrder(ScreenLocation s1, ScreenLocation s2)
	{
		if (s1.fX > s2.fX) {
			int temp = s1.fX;
			s1.fX = s2.fX;
			s2.fX = temp;
		}

		if (s1.fY > s2.fY) {
			int temp = s1.fY;
			s1.fY = s2.fY;
			s2.fY = temp;
		}

		s1.set(s1.fX,s1.fY);
		s2.set(s2.fX,s2.fY);
	}
}
