// -------------------------------
// Filename      : EdgeFilter.java
// Author        : Sven Maerivoet
// Last modified : 21/12/2014
// Target        : Java VM (1.8)
// -------------------------------

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
import org.sm.smtools.exceptions.*;
import org.sm.smtools.util.*;

/**
 * The <CODE>EdgeFilter</CODE> class provides an edge-detecting filter.
 * <P>
 * <B>Note that this class can not be subclased!</B>
 * 
 * @author  Sven Maerivoet
 * @version 21/12/2014
 */
public final class EdgeFilter extends AFilter
{
	// internal datastructures
	private double fStrength;
	private float[] fEdgeKernel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs an <CODE>EdgeFilter</CODE> object and initialises with a default strength of 9.0.
	 */
	public EdgeFilter()
	{
		setStrength(9.0);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Sets the filter's strength and calculates its elements.
	 *
	 * @param strength  the filter's strength
	 */
	public void setStrength(double strength)
	{
		fStrength = strength;

		fEdgeKernel = new float[9];
		fEdgeKernel[0] = 0.0f;
		fEdgeKernel[1] = -(float) fStrength;
		fEdgeKernel[2] = 0.0f;

		fEdgeKernel[3] = -(float) fStrength;
		fEdgeKernel[4] = 4.0f * (float) fStrength;
		fEdgeKernel[5] = -(float) fStrength;

		fEdgeKernel[6] = 0.0f;
		fEdgeKernel[7] = -(float) fStrength;
		fEdgeKernel[8] = 0.0f;
	}

	/**
	 * Returns the filter's strength.
	 *
	 * @return the filter's strength
	 */
	public double getStrength()
	{
		return fStrength;
	}

	/**
	 * Returns the filter's name.
	 *
	 * @return the filter's name
	 */
	public String getName()
	{
		return "EdgeFilter";
	}

	/**
	 * Applies the filter to a specified image and returns the result.
	 *
	 * @param  image the <CODE>BufferedImage</CODE> to apply the filter to
	 * @return the <CODE>BufferedImage</CODE> that results after applying the filter
	 */
	public BufferedImage filter(BufferedImage image)
	{
		BufferedImageOp biOp = new ConvolveOp(new Kernel(3,3,fEdgeKernel),ConvolveOp.EDGE_ZERO_FILL,null);
		return biOp.filter(image,null);
	}

	/**
	 * Loads the filter's parameters from a file.
	 * 
	 * @param  tfp                 a reference to the file parser
	 * @throws FileParseException  in case a read error occurs
	 */
	public void loadParameters(TextFileParser tfp) throws FileParseException
	{
		setStrength(tfp.getNextDouble());
	}

	/**
	 * Saves the filter's parameters to a file.
	 * 
	 * @param  tfw                 a reference to the file writer
	 * @throws FileWriteException  in case a write error occurs
	 */
	public void saveParameters(TextFileWriter tfw) throws FileWriteException
	{
		tfw.writeDouble(fStrength);
		tfw.writeLn();
	}

	/**
	 * Clones (deep copy) the current filter.
	 *
	 * @return a reference to the cloned filter
	 */
	@Override
	public AFilter clone()
	{
		EdgeFilter clonedEdgeFilter = new EdgeFilter();

		clonedEdgeFilter.setStrength(getStrength());

		return clonedEdgeFilter;
	}
}
