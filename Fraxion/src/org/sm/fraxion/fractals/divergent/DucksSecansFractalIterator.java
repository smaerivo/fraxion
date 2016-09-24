// -----------------------------------------------
// Filename      : DucksSecansFractalIterator.java
// Author        : Sven Maerivoet
// Last modified : 24/08/2016
// Target        : Java VM (1.8)
// -----------------------------------------------

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

package org.sm.fraxion.fractals.divergent;

import org.sm.smtools.math.complex.*;

/**
 * The <CODE>DucksSecansFractalIterator</CODE> class provides an implementation of the Ducks (secans) fractal.
 * <P>
 * This fractal is specifically designed for Julia sets coloured using the total average distance with the Bone colourmap.
 * 
 * @author  Sven Maerivoet
 * @version 24/08/2016
 */
public class DucksSecansFractalIterator extends MandelbrotJuliaFractalIterator
{
	// the default number of fixed iterations
	private static final int kDefaultMaxNrOfIterations = 50;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Creates a <CODE>DucksSecansFractalIterator</CODE> object.
	 */
	public DucksSecansFractalIterator()
	{
		setMaxNrOfIterations(kDefaultMaxNrOfIterations);
		setUseFixedNrOfIterations(true);
	}

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
		return "Ducks (secans)";
	}

	/**
	 * Returns the default upper-left corner in the complex plane.
	 * 
	 * @return the default upper-left corner in the complex plane
	 */
	@Override
	public ComplexNumber getDefaultP1()
	{
		return (new ComplexNumber(-3.01,-4.01));
	}

	/**
	 * Returns the default lower-right corner in the complex plane.
	 * 
	 * @return the default lower-right corner in the complex plane
	 */
	@Override
	public ComplexNumber getDefaultP2()
	{
		return (new ComplexNumber(+2.01,+1.01));
	}

	/**
	 * Returns the default escape radius of 2.
	 * 
	 * @return the default escape radius of 2
	 */
	@Override
	public double getDefaultEscapeRadius()
	{
		return 100.0;
	}

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Evaluates the Ducky secans function for a specified complex point.
	 *
	 * @param z  the complex variable <I>z</I>
	 * @param c  the complex parameter <I>c</I>
	 * @return   the function evaluated with the given parameters
	 */
	@Override
	protected ComplexNumber evaluateFractalFunction(ComplexNumber z, ComplexNumber c)
	{
		if (z.imaginaryComponent() < 0.0) { 
			z = z.conjugate();
		}

		z = z.add(c);
		z = z.subtract(z.sec());
		z = z.ln();

		return z;
	}
}
