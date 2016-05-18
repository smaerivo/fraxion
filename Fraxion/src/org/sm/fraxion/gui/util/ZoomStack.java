// ------------------------------
// Filename      : ZoomStack.java
// Author        : Sven Maerivoet
// Last modified : 17/05/2016
// Target        : Java VM (1.8)
// ------------------------------

/**
 * Copyright 2003-2016 Sven Maerivoet
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
 * @version 17/05/2016
 */
public final class ZoomStack implements Cloneable
{
	// the field separator for loading and saving
	private static final String kFieldSeparator = ",";

	// datastructures
	private ArrayDeque<ComplexNumber> fP1Stack;
	private ArrayDeque<ComplexNumber> fP2Stack;
	private boolean fIsDirty;
	private ArrayList<Image> fThumbnails;

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
		fP1Stack = new ArrayDeque<ComplexNumber>();
		fP2Stack = new ArrayDeque<ComplexNumber>();
		fIsDirty = false;
		fThumbnails = new ArrayList<Image>();
	}

	/**
	 * Checks if the zoom stack is empty (or contains 1 item or less).
	 *
	 * @return <CODE>true</CODE> if the zoom stack is empty, <CODE>false</CODE> otherwise
	 */
	public boolean isEmpty()
	{
		return ((fP1Stack == null) || (fP2Stack == null) || (fP1Stack.size() <= 1) || (fP2Stack.size() <= 1));
	}

	/**
	 * Returns the current zoom level (i.e., the number of steps zoomed in).
	 * 
	 * @return the current zoom level
	 */
	public int getZoomLevel()
	{
		if ((fP1Stack == null) || (fP2Stack == null)) {
			return 0;
		}
		else {
			return fP1Stack.size();
		}
	}

	/**
	 * Returns P1 (lower-left corner in the complex plane) at the top of the zoom stack.
	 *
	 * @return P1 (lower-left corner in the complex plane) at the top of the zoom stack
	 */
	public ComplexNumber getTopP1()
	{
		return fP1Stack.peekFirst();
	}

	/**
	 * Returns P2 (upper-right corner in the complex plane) at the top of the zoom stack.
	 *
	 * @return P2 (upper-right corner in the complex plane) at the top of the zoom stack
	 */
	public ComplexNumber getTopP2()
	{
		return fP2Stack.peekFirst();
	}

	/**
	 * Returns P1 (lower-left corner in the complex plane) from a specified zoom level.
	 *
	 * @param zoomLevel  the specified zoom level
	 * @return           P1 (lower-left corner in the complex plane) from a specified zoom level
	 */
	public ComplexNumber getP1(int zoomLevel)
	{
		ComplexNumber[] p1Stack = fP1Stack.toArray(new ComplexNumber[0]);
		return p1Stack[getZoomLevel() - zoomLevel];
	}

	/**
	 * Returns P2 (upper-right corner in the complex plane) from a specified zoom level.
	 *
	 * @param zoomLevel  the specified zoom level
	 * @return           P2 (upper-right corner in the complex plane) from a specified zoom level
	 */
	public ComplexNumber getP2(int zoomLevel)
	{
		ComplexNumber[] p2Stack = fP2Stack.toArray(new ComplexNumber[0]);
		return p2Stack[getZoomLevel() - zoomLevel];
	}

	/**
	 * Returns whether or not the zoom stack is dirty and needs a fresh thumbnail.
	 *
	 * @return a <CODE>boolean</CODE> indicating whether or not the zoom stack is dirty and needs a fresh thumbnail
	 */
	public boolean isDirty()
	{
		return fIsDirty;
	}

	/**
	 * Returns the number of available zoom levels.
	 *
	 * @return the number of available zoom levels
	 */
	public int getNrOfZoomLevels()
	{
		return fP1Stack.size();
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
		fP1Stack.pop();
		fP2Stack.pop();
		int nrOfThumbnails = fThumbnails.size();
		if (nrOfThumbnails > 0) {
			fThumbnails.remove(nrOfThumbnails - 1);
		}
	}

	/**
	 * Pushes an item on the zoom stack.
	 * 
	 * @param p1  the lower-left corner in the complex plane of the specified zoom level
	 * @param p2  the upper-right corner in the complex plane of the specified zoom level
	 */
	public void push(ComplexNumber p1, ComplexNumber p2)
	{
		fP1Stack.push((ComplexNumber) p1.clone());
		fP2Stack.push((ComplexNumber) p2.clone());
		fIsDirty = true;
	}

	/**
	 * Adds a thumbnail to the thumbnail list.
	 *
	 * @param thumbnail  the thumbnail to add to the thumbnail list
	 */
	public void addThumbnail(Image thumbnail)
	{
		if (fIsDirty) {
			fThumbnails.add(thumbnail);
			fIsDirty = false;
		}
	}

	/**
	 * Returns a thumbnail from the thumbnail list.
	 *
	 * @param zoomLevel  the zoom level to get the thumbnail from
	 * @return the thumbnail corresponding to the specified zoom level
	 */
	public Image getThumbnail(int zoomLevel)
	{
		if (fThumbnails.size() > 0) {
			return fThumbnails.get(zoomLevel - 1);
		}
		else {
			return null;
		}
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
			addThumbnail(null);
		}

		fIsDirty = false;
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

		Iterator<ComplexNumber> i1 = fP1Stack.descendingIterator();
		Iterator<ComplexNumber> i2 = fP2Stack.descendingIterator();

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

		Iterator<ComplexNumber> i1 = fP1Stack.descendingIterator();
		Iterator<ComplexNumber> i2 = fP2Stack.descendingIterator();

		while (i1.hasNext() && i2.hasNext()) {
			zoomStack.push((ComplexNumber) i1.next().clone(),(ComplexNumber) i2.next().clone());
		}

		return zoomStack;
	}
}
