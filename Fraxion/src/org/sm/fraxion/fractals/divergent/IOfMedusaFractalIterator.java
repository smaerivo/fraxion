// ---------------------------------------------
// Filename      : IOfMedusaFractalIterator.java
// Author        : Sven Maerivoet
// Last modified : 08/04/2016
// Target        : Java VM (1.8)
// ---------------------------------------------

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
 * The <CODE>IOfMedusaFractalIterator</CODE> class provides an implementation of the i of Medusa fractal.
 * 
 * @author  Sven Maerivoet
 * @version 08/04/2016
 */
public class IOfMedusaFractalIterator extends MandelbrotJuliaFractalIterator
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
		return "i of Medusa";
	}

	/**
	 * Returns the default escape radius of 2.
	 * 
	 * @return the default escape radius of 2
	 */
	@Override
	public double getDefaultEscapeRadius()
	{
		return 2.0;
	}

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Evaluates the i of Medusa function for a specified complex point.
	 *
	 * @param z  the complex variable <I>z</I>
	 * @param c  the complex parameter <I>c</I>
	 * @return   the function evaluated with the given parameters
	 */
	@Override
	protected ComplexNumber evaluateFractalFunction(ComplexNumber z, ComplexNumber c)
	{
		return
			ComplexNumber.kOne.subtract(ComplexNumber.kI).multiply(z.pow(6.0)).add(
				new ComplexNumber(7.0).add(ComplexNumber.kI).multiply(z)).divide(
			z.pow(5.0).multiply(ComplexNumber.kTwo).add(new ComplexNumber(6.0))).ln();
	}
}
