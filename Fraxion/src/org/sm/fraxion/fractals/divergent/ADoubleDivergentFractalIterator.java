// ----------------------------------------------------
// Filename      : ADoubleDivergentFractalIterator.java
// Author        : Sven Maerivoet
// Last modified : 04/11/2014
// Target        : Java VM (1.8)
// ----------------------------------------------------

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

package org.sm.fraxion.fractals.divergent;

import org.sm.smtools.math.complex.*;

/**
 * The <CODE>ADoubleDivergentFractalIterator</CODE> class provides a base class for divergent fractal iterators that use <I>z</I>(<I>n</I>) and <I>z</I>(<I>n</I> - 1).
 * 
 * @author  Sven Maerivoet
 * @version 04/11/2014
 */
public abstract class ADoubleDivergentFractalIterator extends ADivergentFractalIterator
{
	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Returns the default escape radius of 100.
	 * 
	 * @return the default escape radius of 100
	 */
	@Override
	public double getDefaultEscapeRadius()
	{
		// a high escape radius gives smoother colouring results
		return 100.0;
	}

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Returns whether or not the image should be rotated 90 degrees clockwise.
	 * <P>
	 * The default is <CODE>true</CODE>.
	 *
	 * @return a <CODE>boolean</CODE> indicating whether or not the image should be rotated 90 degrees clockwise
	 */
	@Override
	protected boolean rotateImage()
	{
		return true;
	}

	/**
	 * Evaluates the fractal function for a specified complex point.
	 *
	 * @param z          the complex variable <I>z</I> at <I>n</I>
	 * @param zPrevious  the complex variable <I>z</I> at <I>n</I> - 1
	 * @param c          the complex parameter <I>c</I>
	 * @return           the function evaluated with the given parameters
	 */
	@Override
	protected abstract ComplexNumber evaluateFractalFunction(ComplexNumber z, ComplexNumber zPrevious, ComplexNumber c);

	/**
	 * Unused.
	 *
	 * @param z  -
	 * @param c  -
	 * @return   -
	 */
	@Override
	protected ComplexNumber evaluateFractalFunction(ComplexNumber z, ComplexNumber c)
	{
		return null;
	}
}
