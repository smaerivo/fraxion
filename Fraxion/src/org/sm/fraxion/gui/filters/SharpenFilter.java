// ----------------------------------
// Filename      : SharpenFilter.java
// Author        : Sven Maerivoet
// Last modified : 21/12/2014
// Target        : Java VM (1.8)
// ----------------------------------

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

package org.sm.fraxion.gui.filters;

import java.awt.image.*;

/**
 * The <CODE>SharpenFilter</CODE> class provides a sharpening filter.
 * <P>
 * <B>Note that this class can not be subclased!</B>
 * 
 * @author  Sven Maerivoet
 * @version 21/12/2014
 */
public final class SharpenFilter extends AFilter
{
	// internal datastructures
	private float[] fSharpenKernel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>SharpenFilter</CODE> object.
	 */
	public SharpenFilter()
	{
		fSharpenKernel = new float[9];
		fSharpenKernel[0] = 0.0f;
		fSharpenKernel[1] = -1.0f;
		fSharpenKernel[2] = 0.0f;

		fSharpenKernel[3] = -1.0f;
		fSharpenKernel[4] = 5.0f;
		fSharpenKernel[5] = -1.0f;

		fSharpenKernel[6] = 0.0f;
		fSharpenKernel[7] = -1.0f;
		fSharpenKernel[8] = 0.0f;
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Returns the filter's name.
	 *
	 * @return the filter's name
	 */
	public String getName()
	{
		return "SharpenFilter";
	}

	/**
	 * Applies the filter to a specified image and returns the result.
	 *
	 * @param  image the <CODE>BufferedImage</CODE> to apply the filter to
	 * @return the <CODE>BufferedImage</CODE> that results after applying the filter
	 */
	public BufferedImage filter(BufferedImage image)
	{
		BufferedImageOp biOp = new ConvolveOp(new Kernel(3,3,fSharpenKernel),ConvolveOp.EDGE_ZERO_FILL,null);
		return biOp.filter(image,null);
	}

	/**
	 * Clones (deep copy) the current filter.
	 *
	 * @return a reference to the cloned filter
	 */
	@Override
	public AFilter clone()
	{
		return (new SharpenFilter());
	}
}
