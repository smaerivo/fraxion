// ------------------------------
// Filename      : ZoomStack.java
// Author        : Sven Maerivoet
// Last modified : 10/10/2014
// Target        : Java VM (1.8)
// ------------------------------

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

import java.util.*;
import org.sm.smtools.exceptions.*;
import org.sm.smtools.math.complex.*;
import org.sm.smtools.util.*;

/**
 * The <CODE>ZoomStack</CODE> class provides a container accessing, loading and saving the zoom stack.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 * 
 * @author  Sven Maerivoet
 * @version 10/10/2014
 */
public final class ZoomStack implements Cloneable
{
	// the field separator for loading and saving
	private static final String kFieldSeparator = ",";

	// datastructures
	private ArrayDeque<ComplexNumber> fZoomP1Stack;
	private ArrayDeque<ComplexNumber> fZoomP2Stack;

	/*****************
	 * CONSTRUCTORS  *
	 *****************/

	/**
	 * Constructs a <CODE>ZoomStack</CODE> object.
	 */
	public ZoomStack()
	{
		clear();
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Initialises the zoom stack.
	 */
	public void clear()
	{
		fZoomP1Stack = new ArrayDeque<ComplexNumber>();
		fZoomP2Stack = new ArrayDeque<ComplexNumber>();
	}

	/**
	 * Checks if the zoom stack is empty (or contains 1 item or less).
	 *
	 * @return <CODE>true</CODE> if the zoom stack is empty, <CODE>false</CODE> otherwise
	 */
	public boolean isEmpty()
	{
		return ((fZoomP1Stack == null) || (fZoomP2Stack == null) || (fZoomP1Stack.size() <= 1) || (fZoomP2Stack.size() <= 1));
	}

	/**
	 * Returns the current zoom level (i.e., the number of steps zoomed in).
	 * 
	 * @return the current zoom level
	 */
	public int getZoomLevel()
	{
		if ((fZoomP1Stack == null) || (fZoomP2Stack == null)) {
			return 0;
		}
		else {
			return fZoomP1Stack.size();
		}
	}

	/**
	 * Returns P1 (lower-left corner in the complex plane) at the top of the zoom stack.
	 *
	 * @return P1 (lower-left corner in the complex plane) at the top of the zoom stack
	 */
	public ComplexNumber getTopP1()
	{
		return fZoomP1Stack.peekFirst();
	}

	/**
	 * Returns P2 (upper-right corner in the complex plane) at the top of the zoom stack.
	 *
	 * @return P2 (upper-right corner in the complex plane) at the top of the zoom stack
	 */
	public ComplexNumber getTopP2()
	{
		return fZoomP2Stack.peekFirst();
	}

	/**
	 * Returns P1 (lower-left corner in the complex plane) from a specified zoom level.
	 *
	 * @param zoomLevel  the specified zoom level
	 * @return           P1 (lower-left corner in the complex plane) from a specified zoom level
	 */
	public ComplexNumber getP1(int zoomLevel)
	{
		ComplexNumber[] zoomP1Stack = fZoomP1Stack.toArray(new ComplexNumber[0]);
		return zoomP1Stack[getZoomLevel() - zoomLevel];
	}

	/**
	 * Returns P2 (upper-right corner in the complex plane) from a specified zoom level.
	 *
	 * @param zoomLevel  the specified zoom level
	 * @return           P2 (upper-right corner in the complex plane) from a specified zoom level
	 */
	public ComplexNumber getP2(int zoomLevel)
	{
		ComplexNumber[] zoomP2Stack = fZoomP2Stack.toArray(new ComplexNumber[0]);
		return zoomP2Stack[getZoomLevel() - zoomLevel];
	}

	/**
	 * Modifies the top of the zoom stack.
	 * 
	 * @param p1  the new lower-left corner in the complex plane
	 * @param p2  the new upper-right corner in the complex plane
	 */
	public void modifyTop(ComplexNumber p1, ComplexNumber p2)
	{
		pop();
		push(p1,p2);
	}

	/**
	 * Pops an item of the zoom stack.
	 */
	public void pop()
	{
		fZoomP1Stack.pop();
		fZoomP2Stack.pop();
	}

	/**
	 * Pushes an item on the zoom stack.
	 * 
	 * @param p1  the lower-left corner in the complex plane of the specified zoom level
	 * @param p2  the upper-right corner in the complex plane of the specified zoom level
	 */
	public void push(ComplexNumber p1, ComplexNumber p2)
	{
		fZoomP1Stack.push((ComplexNumber) p1.clone());
		fZoomP2Stack.push((ComplexNumber) p2.clone());
	}

	/**
	 * Loads the zoom stack from a CSV-file.
	 *
	 * @param filename  the name of the file to load the zoom stack from
	 * @throws          FileDoesNotExistException -
	 * @throws          FileParseException        -
	 * @throws          NumberFormatException     -
	 */
	public void load(String filename) throws FileDoesNotExistException, FileParseException, NumberFormatException
	{
		TextFileParser tfp = new TextFileParser(filename);

		clear();
		while (!tfp.endOfFileReached()) {
			String[] zoomComponentsDesc = tfp.getNextCSV();
			if (zoomComponentsDesc.length < 4) {
				throw (new FileParseException(filename,"",tfp.getLastReadLineNr()));
			}

			String p1XDesc = zoomComponentsDesc[0];
			double p1X = Double.parseDouble(p1XDesc);
			String p1YDesc = zoomComponentsDesc[1];
			double p1Y = Double.parseDouble(p1YDesc);
			String p2XDesc = zoomComponentsDesc[2];
			double p2X = Double.parseDouble(p2XDesc);
			String p2YDesc = zoomComponentsDesc[3];
			double p2Y = Double.parseDouble(p2YDesc);

			push(new ComplexNumber(p1X,p1Y),new ComplexNumber(p2X,p2Y));
		}
	}

	/**
	 * Saves the zoom stack to a CSV-file.
	 *
	 * @param filename the name of the file to save the zoom stack to
	 * @throws         FileCantBeCreatedException -
	 * @throws         FileWriteException         -
	 */
	public void save(String filename) throws FileCantBeCreatedException, FileWriteException
	{
		TextFileWriter tfw = new TextFileWriter(filename);

		Iterator<ComplexNumber> i1 = fZoomP1Stack.descendingIterator();
		Iterator<ComplexNumber> i2 = fZoomP2Stack.descendingIterator();

		while (i1.hasNext() && i2.hasNext()) {
			ComplexNumber p1 = i1.next();
			ComplexNumber p2 = i2.next();

			tfw.writeDouble(p1.realComponent());
			tfw.writeString(kFieldSeparator);
			tfw.writeDouble(p1.imaginaryComponent());
			tfw.writeString(kFieldSeparator);
			tfw.writeDouble(p2.realComponent());
			tfw.writeString(kFieldSeparator);
			tfw.writeDouble(p2.imaginaryComponent());
			tfw.writeLn();
		}
	}

	/**
	 * Clones (deep copy) the current zoom stack.
	 *
	 * @return a reference to the cloned zoom stack
	 */
	@Override
	public Object clone()
	{
		ZoomStack zoomStack = new ZoomStack();

		Iterator<ComplexNumber> i1 = fZoomP1Stack.descendingIterator();
		Iterator<ComplexNumber> i2 = fZoomP2Stack.descendingIterator();

		while (i1.hasNext() && i2.hasNext()) {
			zoomStack.push((ComplexNumber) i1.next().clone(),(ComplexNumber) i2.next().clone());
		}

		return zoomStack;
	}
}
