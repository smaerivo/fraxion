// -----------------------------------------------------------
// Filename      : MarkusLyapunovJellyfishFractalIterator.java
// Author        : Sven Maerivoet
// Last modified : 03/11/2014
// Target        : Java VM (1.8)
// -----------------------------------------------------------

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

package org.sm.fraxion.fractals.markuslyapunov;

import org.sm.smtools.math.complex.*;

/**
 * The <CODE>MarkusLyapunovJellyfishFractalIterator</CODE> class provides an implementation of the Markus-Lyapunov Jellyfish fractal.
 * 
 * @author  Sven Maerivoet
 * @version 03/11/2014
 */
public class MarkusLyapunovJellyfishFractalIterator extends MarkusLyapunovFractalIterator
{
	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>MarkusLyapunovJellyfishFractalIterator</CODE> object and initialises it with the default 'BBABA' sequence.
	 */
	public MarkusLyapunovJellyfishFractalIterator()
	{
		setRootSequence("BBABA");
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
		return "Markus / Lyapunov (Jellyfish)";
	}

	/**
	 * Returns the default upper-left corner in the complex plane.
	 * 
	 * @return the default upper-left corner in the complex plane
	 */
	@Override
	public ComplexNumber getDefaultP1()
	{
		return (new ComplexNumber(3.8225,3.8218));
	}

	/**
	 * Returns the default lower-right corner in the complex plane.
	 * 
	 * @return the default lower-right corner in the complex plane
	 */
	@Override
	public ComplexNumber getDefaultP2()
	{
		return (new ComplexNumber(3.8711,3.8607));
	}
}
