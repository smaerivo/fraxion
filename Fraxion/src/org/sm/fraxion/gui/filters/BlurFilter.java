// -------------------------------
// Filename      : BlurFilter.java
// Author        : Sven Maerivoet
// Last modified : 23/06/2015
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
import java.io.*;
import org.sm.smtools.exceptions.*;
import org.sm.smtools.util.*;

/**
 * The <CODE>BlurFilter</CODE> class provides a blurring filter.
 * <P>
 * <B>Note that this class can not be subclased!</B>
 * 
 * @author  Sven Maerivoet
 * @version 23/06/2015
 */
public final class BlurFilter extends AFilter
{
	// internal datastructures
	private int fKernelSize;
	private float[] fBlurKernel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>BlurFilter</CODE> object and initialises with a default kernel size of 3.
	 */
	public BlurFilter()
	{
		setKernelSize(3);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Sets the size of the kernel and calculates its elements.
	 * <P>
	 * If necessary, the kernel is made larger than 3.
	 *
	 * @param kernelSize  the size of the kernel
	 */
	public void setKernelSize(int kernelSize)
	{
		fKernelSize = kernelSize;
		if (fKernelSize < 3) {
			fKernelSize = 3;
		}

		int n = fKernelSize * fKernelSize;
		fBlurKernel = new float[n];
		for (int i = 0; i < n; ++i) {
			fBlurKernel[i] = 1.0f / (float) n;
		}
	}

	/**
	 * Returns the size of the kernel.
	 *
	 * @return the size of the kernel
	 */
	public int getKernelSize()
	{
		return fKernelSize;
	}

	/**
	 * Returns the filter's name.
	 *
	 * @return the filter's name
	 */
	public String getName()
	{
		return "BlurFilter";
	}

	/**
	 * Applies the filter to a specified image and returns the result.
	 *
	 * @param  image the <CODE>BufferedImage</CODE> to apply the filter to
	 * @return the <CODE>BufferedImage</CODE> that results after applying the filter
	 */
	public BufferedImage filter(BufferedImage image)
	{
		BufferedImageOp biOp = new ConvolveOp(new Kernel(fKernelSize,fKernelSize,fBlurKernel),ConvolveOp.EDGE_ZERO_FILL,null);
		return biOp.filter(image,null);
	}

	/**
	 * Loads the filter's parameters from a plain-text file.
	 * 
	 * @param  tfp                 a reference to the file parser
	 * @throws FileParseException  in case a read error occurs
	 */
	@Override
	public void plainTextLoadParameters(TextFileParser tfp) throws FileParseException
	{
		setKernelSize(tfp.getNextInteger());
	}

	/**
	 * Loads the filter's parameters from a file.
	 * 
	 * @param  dataInputStream  a data inputstream
	 * @throws IOException      in case a parse error occurs
	 */
	@Override
	public void streamLoadParameters(DataInputStream dataInputStream) throws IOException
	{
		setKernelSize(dataInputStream.readInt());
	}

	/**
	 * Saves the filter's parameters to a plain-text file.
	 * 
	 * @param  tfw                 a reference to the file writer
	 * @throws FileWriteException  in case a write error occurs
	 */
	@Override
	public void plainTextSaveParameters(TextFileWriter tfw) throws FileWriteException
	{
		tfw.writeInteger(fKernelSize);
		tfw.writeLn();
	}

	/**
	 * Saves the filter's parameters to a file as a stream.
	 * 
	 * @param  dataOutputStream  a data outputstream
	 * @throws IOException       in case a write error occurs
	 */
	@Override
	public void streamSaveParameters(DataOutputStream dataOutputStream) throws IOException
	{
		dataOutputStream.writeInt(fKernelSize);
	}

	/**
	 * Clones (deep copy) the current filter.
	 *
	 * @return a reference to the cloned filter
	 */
	@Override
	public AFilter clone()
	{
		BlurFilter clonedBlurFilter = new BlurFilter();

		clonedBlurFilter.setKernelSize(getKernelSize());

		return clonedBlurFilter;
	}
}
