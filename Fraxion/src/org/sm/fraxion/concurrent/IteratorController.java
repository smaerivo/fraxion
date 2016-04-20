// ---------------------------------------
// Filename      : IteratorController.java
// Last modified : 20/04/2016
// Author        : Sven Maerivoet
// Target        : Java VM (1.8)
// ---------------------------------------

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

package org.sm.fraxion.concurrent;

import java.util.*;
import javax.swing.*;
import org.sm.fraxion.fractals.*;
import org.sm.fraxion.fractals.divergent.*;
import org.sm.fraxion.fractals.util.*;
import org.sm.fraxion.gui.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.math.*;

/**
 * The <CODE>IteratorController</CODE> class provides functionality for concurrently calculating fractals.
 * Blocks on the screen are calculated in a random order.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 20/04/2016
 */
public final class IteratorController
{
	/**
	 * The maximum number of blocks per dimension to use for the parallel calculations.
	 */
	public static final int kMaxNrOfBlocksToUse = 100;

	/**
	 * The default number of blocks per dimension to use for the parallel calculations.
	 */
	public static final int kDefaultNrOfBlocksToUse = 50;

	// internal datastructures
	private boolean fIsBusy;
	private AFractalIterator fFractalIterator;
	private ColoringParameters fColoringParameters;
	private FractalPanel fFractalPanel;
	private JFrame fParentFrame;
	private JProgressUpdateGlassPane fProgressUpdateGlassPane;
	private JLabel fStatusBarCalculationTimeLabel;
	private JARResources fResources;
	private IteratorTaskExecutor fIteratorTaskExecutor;
	private int fNrOfBlocksToUse;
	private int fNrOfThreadsToUse;
	private boolean fEstimatePDF;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>IteratorController</CODE> object.
	 */
	public IteratorController()
	{
		setBusy(false);

		// choose the default Mandelbrot/Julia fractal iterator
		setFractalIteratorFamily(new FastMandelbrotJuliaFractalIterator());

		// setup initial screen bounds
		fFractalIterator.setScreenBounds(AFractalIterator.kInitialScreenBounds.width,AFractalIterator.kInitialScreenBounds.height);

		fNrOfBlocksToUse = kDefaultNrOfBlocksToUse;

		fColoringParameters = new ColoringParameters();
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Selects the family of the fractal iterator to be used.
	 *
	 * @param fractalIterator  the fractal iterator family to be used
	 */
	public void setFractalIteratorFamily(AFractalIterator fractalIterator)
	{
		fFractalIterator = fractalIterator;
	}

	/**
	 * Sets the parent frame, the progress update glass pane, the fractal panel,
	 * and the status bar's calculation time label used for the multithreaded rendering.
	 *
	 * @param parentFrame                    the parent frame (used for temporarily disabling resizing)
	 * @param progressUpdateGlassPane        the progress update glass pane used for the multithreaded rendering
	 * @param fractalPanel                   the fractal panel used for the rendering
	 * @param statusBarCalculationTimeLabel  the status bar's calculation time label
	 * @param resources                      a reference to the JAR resources
	 */
	public void installGUIControls(JFrame parentFrame, JProgressUpdateGlassPane progressUpdateGlassPane, FractalPanel fractalPanel, JLabel statusBarCalculationTimeLabel, JARResources resources)
	{
		fParentFrame = parentFrame;
		fProgressUpdateGlassPane = progressUpdateGlassPane;
		fFractalPanel = fractalPanel;
		fStatusBarCalculationTimeLabel = statusBarCalculationTimeLabel;
		fResources = resources;
		fIteratorTaskExecutor = new IteratorTaskExecutor(
			fParentFrame,
			fProgressUpdateGlassPane,
			fFractalIterator,
			fFractalPanel,
			fStatusBarCalculationTimeLabel,
			fEstimatePDF,
			fResources);

		int nrOfProcessors = SystemInformation.getNrOfProcessors();
		if (nrOfProcessors > 1) {
			setNrOfThreadsToUse(nrOfProcessors - 1);
		}
	}

	/**
	 * Sets the number of blocks to use for each screen dimension.
	 * 
	 * @param nrOfBlocksToUse  the number of blocks to use for each screen dimension
	 */
	public void setNrOfBlocksToUse(int nrOfBlocksToUse)
	{
		fNrOfBlocksToUse = nrOfBlocksToUse;
	}

	/**
	 * Returns the number of blocks to use for each screen dimension.
	 * 
	 * @return the number of blocks to use for each screen dimension
	 */
	public final int getNrOfBlocksToUse()
	{
		return fNrOfBlocksToUse;
	}

	/**
	 * Sets the number of threads to use by creating a fixed thread pool.
	 * <P>
	 * Note that this number is bound by the number of available processor cores in the system.
	 * 
	 * @param nrOfThreadsToUse  the number of threads to use for the fixed thread pool
	 */
	public void setNrOfThreadsToUse(int nrOfThreadsToUse)
	{
		fNrOfThreadsToUse = nrOfThreadsToUse;
		fIteratorTaskExecutor.setNrOfThreadsToUse(nrOfThreadsToUse);
	}

	/**
	 * Returns the number of threads that is used.
	 * 
	 * @return the number of threads that is used
	 */
	public final int getNrOfThreadsToUse()
	{
		fNrOfThreadsToUse = fIteratorTaskExecutor.getNrOfThreadsToUse();
		return fNrOfThreadsToUse;
	}

	/**
	 * Returns the fractal iterator.
	 *
	 * @return the fractal iterator
	 */
	public AFractalIterator getFractalIterator()
	{
		return fFractalIterator;
	}

	/**
	 * Returns the colouring parameters.
	 *
	 * @return the colouring parameters
	 */
	public ColoringParameters getColoringParameters()
	{
		return fColoringParameters;
	}

	/**
	 * Specifies whether or not the PDF of the iteration count should be estimated.
	 * 
	 * @param estimatePDF  a <CODE>boolean</CODE> specifying whether or not the PDF of the iteration count should be estimated
	 */
	public void setEstimatePDF(boolean estimatePDF)
	{
		fEstimatePDF = estimatePDF;
	}

	/**
	 * Triggers a multithreaded recalculation of the current fractal.
	 */
	public void recalc()
	{
		if (fFractalPanel == null) {
			// early bail-out if the application is not yet fully initialised
			return;
		}

//		if ((fIteratorTaskExecutor == null) || ((fIteratorTaskExecutor != null) && (!fIteratorTaskExecutor.isBusy()))) {
		if ((fIteratorTaskExecutor == null) || (!fIteratorTaskExecutor.isBusy())) {
			int canvasWidth = fFractalIterator.getScreenWidth();
			int canvasHeight = fFractalIterator.getScreenHeight();

			// divide the screen in regions
			int nrOfRowBlocks = fNrOfBlocksToUse;
			int nrOfColumnBlocks = fNrOfBlocksToUse;

			fIteratorTaskExecutor = new IteratorTaskExecutor(
				fParentFrame,
				fProgressUpdateGlassPane,
				fFractalIterator,
				fFractalPanel,
				fStatusBarCalculationTimeLabel,
				fEstimatePDF,
				fResources);

			if (fNrOfThreadsToUse == 0) {
				fNrOfThreadsToUse = fIteratorTaskExecutor.getNrOfThreadsToUse();
			}
			else {
				fIteratorTaskExecutor.setNrOfThreadsToUse(fNrOfThreadsToUse);
			}

			// randomise blocks for a more generic spread across the screen when executing the tasks
			ArrayList<TaskBlock> blocks = new ArrayList<TaskBlock>();
			for (int rowBlock = 0; rowBlock < nrOfRowBlocks; ++rowBlock) {
				for (int columnBlock = 0; columnBlock < nrOfColumnBlocks; ++columnBlock) {
					blocks.add(new TaskBlock(rowBlock,columnBlock));
				}
			}
			// the shuffling helps to estimate the remaining time more correctly
			Collections.shuffle(blocks);

			final double kBlockWidth = (double) canvasWidth / (double) nrOfColumnBlocks;
			final double kBlockHeight = (double) canvasHeight / (double) nrOfRowBlocks;

			for (int block = 0; block < blocks.size(); ++block) {
				TaskBlock taskBlock = blocks.get(block);
				int rowBlock = taskBlock.getRowBlock();
				int columnBlock = taskBlock.getColumnBlock();
				ScreenLocation s1 = new ScreenLocation((int) (rowBlock * kBlockWidth),(int) (columnBlock * kBlockHeight));
				ScreenLocation s2 = new ScreenLocation((int) (((rowBlock + 1) * kBlockWidth) - 1),(int) (((columnBlock + 1) * kBlockHeight) - 1));
				fIteratorTaskExecutor.addTask(new IteratorTask(s1,s2));
			}

			fIteratorTaskExecutor.execute();
		}
	}

	/**
	 * Returns the fractal result buffer.
	 *
	 * @return the fractal result buffer
	 */
	public IterationBuffer getFractalResultBuffer()
	{
		return fIteratorTaskExecutor.getFractalResultBuffer();
	}

	/**
	 * Manually sets the fractal result buffer.
	 *
	 * @param fractalResultBuffer  the new fractal result buffer
	 */
	public void setFractalResultBuffer(IterationBuffer fractalResultBuffer)
	{
		fIteratorTaskExecutor.setFractalResultBuffer(fractalResultBuffer);
	}

	/**
	 * Returns the PDF of the iterations.
	 *
	 * @return the PDF of the iteration
	 */
	public FunctionLookupTable getIterationsPDF()
	{
		return fIteratorTaskExecutor.getIterationsPDF();
	}

	/**
	 * Checks whether or not we are in the middle of a calculation.
	 * 
	 * @return a <CODE>boolean</CODE> indicating whether or not we are in the middle of a calculation
	 */
	public boolean isBusy()
	{
		return (fIteratorTaskExecutor.isBusy() || fIsBusy);
	}

	/**
	 * Sets the busy flag.
	 *
	 * @param isBusy  a <CODE>boolean</CODE> associated with the busy flag
	 */
	public void setBusy(boolean isBusy)
	{
		fIsBusy = isBusy;
	}

	/********************
	 * INTERNAL CLASSES *
	 ********************/

	/**
	 * @author  Sven Maerivoet
	 * @version 13/07/2014
	 */
	private final class TaskBlock
	{
		// internal datastructures
		private int fRowBlock;
		private int fColumnBlock;
		
		/****************
		 * CONSTRUCTORS *
		 ****************/

		/**
		 * Creates a <CODE>TaskBlock</CODE> object and initialises it.
		 *
		 * @param rowBlock     the row block
		 * @param columnBlock  the column block
		 */
		public TaskBlock(int rowBlock, int columnBlock)
		{
			fRowBlock = rowBlock;
			fColumnBlock = columnBlock;
		}

		/******************
		 * PUBLIC METHODS *
		 ******************/

		/**
		 * Returns the row block.
		 *
		 * @return the row block
		 */
		public int getRowBlock()
		{
			return fRowBlock;
		}

		/**
		 * Returns the column block.
		 *
		 * @return the column block
		 */
		public int getColumnBlock()
		{
			return fColumnBlock;
		}
	}
}
