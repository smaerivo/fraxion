// ----------------------------------------
// Filename      : NovaFractalIterator.java
// Author        : Sven Maerivoet
// Last modified : 03/11/2014
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

package org.sm.fraxion.fractals;

import org.sm.smtools.math.complex.*;

/**
 * The <CODE>NovaFractalIterator</CODE> class provides an implementation of the classic Nova fractal.
 * 
 * @author  Sven Maerivoet
 * @version 03/11/2014
 */
public class NovaFractalIterator extends AConvergentFractalIterator
{
	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs an <CODE>NovaFractalIterator</CODE> object.
	 */
	public NovaFractalIterator()
	{
		setMainFractalOrbitStartingPoint(ComplexNumber.kOne);
		setAutomaticRootDetectionEnabled(false);
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
		return "Nova";
	}

	/**
	 * Returns the default upper-left corner in the complex plane.
	 * 
	 * @return the default upper-left corner in the complex plane
	 */
	@Override
	public ComplexNumber getDefaultP1()
	{
		return (new ComplexNumber(-0.8,-1.15));
	}

	/**
	 * Returns the default lower-right corner in the complex plane.
	 * 
	 * @return the default lower-right corner in the complex plane
	 */
	@Override
	public ComplexNumber getDefaultP2()
	{
		return (new ComplexNumber(+0.8,+1.15));
	}

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Returns the default of 0 fixed iterations to be used (so it is by default disabled).
	 * 
	 * @return the default of 0 fixed iterations to be used
	 */
	@Override
	protected int getDefaultFixedNrOfIterations()
	{
		return 0;
	}

	/**
	 * Initialises the first iteration.
	 * <P>
	 * This method returns <I>z</I> by default (thus allowing user-specified initial starting values).
	 *
	 * @param z  the complex variable <I>z</I>
	 * @param c  the complex parameter <I>c</I>
	 * @return   the initialised complex variable <I>z</I>
	 */
	@Override
	protected ComplexNumber initialiseIterations(ComplexNumber z, ComplexNumber c)
	{
		return z;
	}

	/**
	 * Evaluates the orbit of the specified complex variable <I>z</I>.
	 * <P>
	 * This method performs relaxed Newton-Raphson root finding by using a fixed fractal function and derivative.
	 *
	 * @param z  the complex variable <I>z</I>
	 * @param c  the complex parameter <I>c</I>
	 * @return   the evaluated complex variable <I>z</I>
	 */
	@Override
	protected ComplexNumber evaluateOrbit(ComplexNumber z, ComplexNumber c)
	{
		// perform relaxed Newton-Raphson root finding iteration
		return z.subtract(
						getAlpha().multiply(z.cube().subtract(ComplexNumber.kOne)).divide(
						ComplexNumber.kThree.multiply(z.sqr()))).add(c);
	}

	/**
	 * Unused because we use a fixed fractal function and derivative.
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
