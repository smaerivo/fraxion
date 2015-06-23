// -------------------------------------------
// Filename      : ManowarFractalIterator.java
// Author        : Sven Maerivoet
// Last modified : 28/12/2014
// Target        : Java VM (1.8)
// -------------------------------------------

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
 * The <CODE>ManowarFractalIterator</CODE> class provides an implementation of the Man o' War fractal.
 * 
 * @author  Sven Maerivoet
 * @version 28/12/2014
 */
public class ManowarFractalIterator extends ADoubleDivergentFractalIterator
{
	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Returns the family name of this fractal.
	 *
	 * @return the family name of this fractal
	 */
	@Override
	public String getFamilyName()
	{
		return "Man o' War";
	}

	/**
	 * Returns the default upper-left corner in the complex plane.
	 * 
	 * @return the default upper-left corner in the complex plane
	 */
	@Override
	public ComplexNumber getDefaultP1()
	{
		return (new ComplexNumber(-2.01,-1.51));
	}

	/**
	 * Returns the default lower-right corner in the complex plane.
	 * 
	 * @return the default lower-right corner in the complex plane
	 */
	@Override
	public ComplexNumber getDefaultP2()
	{
		return (new ComplexNumber(+2.01,+1.51));
	}

	/**
	 * Returns the default dual parameter in the complex plane.
	 * 
	 * @return the default dual parameter in the complex plane
	 */
	@Override
	public ComplexNumber getDefaultDualParameter()
	{
		// the default dual parameter is 0 + 0i
		return (new ComplexNumber());
	}

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Returns that the image should not be rotated 90 degrees clockwise.
	 *
	 * @return <CODE>false</CODE>
	 */
	@Override
	protected boolean rotateImage()
	{
		return false;
	}

	/**
	 * Evaluates the Manowar function for a specified complex point.
	 *
	 * @param z          the complex variable <I>z</I> at <I>n</I>
	 * @param zPrevious  the complex variable <I>z</I> at <I>n</I> - 1
	 * @param c          the complex parameter <I>c</I>
	 * @return           the function evaluated with the given parameters
	 */
	@Override
	protected ComplexNumber evaluateFractalFunction(ComplexNumber z, ComplexNumber zPrevious, ComplexNumber c)
	{
		return z.sqr().add(zPrevious).add(c);
	}
}
