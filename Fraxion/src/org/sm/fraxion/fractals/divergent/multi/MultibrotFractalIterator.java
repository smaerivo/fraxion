// ---------------------------------------------
// Filename      : MultibrotFractalIterator.java
// Author        : Sven Maerivoet
// Last modified : 10/10/2014
// Target        : Java VM (1.8)
// ---------------------------------------------

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

package org.sm.fraxion.fractals.divergent.multi;

import org.sm.fraxion.fractals.divergent.*;
import org.sm.smtools.math.complex.*;

/**
 * The <CODE>MultibrotFractalIterator</CODE> class provides an implementation of the Multibrot fractals.
 * 
 * @author  Sven Maerivoet
 * @version 10/10/2014
 */
public class MultibrotFractalIterator extends APowerFractalIterator
{
	/******************
	 * PUBLIC METHODS *
	 ******************/

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

	/**
	 * Returns the family name of this fractal.
	 *
	 * @return the family name of this fractal
	 */
	@Override
	public String getFamilyName()
	{
		return "Multibrot";
	}

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Returns the default power of 3.0.
	 *
	 * @return the default power of 3.0
	 */
	@Override
	protected ComplexNumber getDefaultPower()
	{
		return (new ComplexNumber(3.0));
	}

	/**
	 * Evaluates the Multibrot function for a specified complex point.
	 *
	 * @param z  the complex variable <I>z</I>
	 * @param c  the complex parameter <I>c</I>
	 * @return   the function evaluated with the given parameters
	 */
	@Override
	protected ComplexNumber evaluateFractalFunction(ComplexNumber z, ComplexNumber c)
	{
		return z.pow(fPower).add(c);
	}
}
