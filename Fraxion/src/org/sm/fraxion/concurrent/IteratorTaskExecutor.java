// -----------------------------------------
// Filename      : IteratorTaskExecutor.java
// Author        : Sven Maerivoet
// Last modified : 18/01/2015
// Target        : Java VM (1.8)
// -----------------------------------------

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

package org.sm.fraxion.concurrent;

import java.util.*;
import javax.swing.*;
import org.sm.fraxion.fractals.*;
import org.sm.fraxion.fractals.util.*;
import org.sm.fraxion.gui.*;
import org.sm.smtools.application.concurrent.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.exceptions.*;
import org.sm.smtools.math.*;
import org.sm.smtools.math.complex.*;
import org.sm.smtools.math.statistics.*;
import org.sm.smtools.util.*;

/**
 * The <CODE>IteratorTaskExecutor</CODE> class provides a facility for organising partial iteration tasks.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 18/01/2015
 */
public class IteratorTaskExecutor extends TaskExecutor
{
	// interface specific constants
	private static final int kNrOfKDEPDFBins = 100;

	// internal datastructures
	private JProgressUpdateGlassPane fProgressUpdateGlassPane;
	private AFractalIterator fFractalIterator;
	private FractalPanel fFractalPanel;
	private JLabel fStatusBarCalculationTimeLabel;
	private Chrono fChrono;
	private IterationBuffer fFractalResultBuffer;
	private boolean fEstimatePDF;
	private EmpiricalDistribution fIterationsEmpiricalDistribution;
	private FunctionLookupTable fIterationsPDF;
	private JARResources fResources;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs an <CODE>IteratorTaskExecutor</CODE> object.
	 *
	 * @param parentFrame                    a reference to the parent frame (used for temporarily disabling resizing)
	 * @param progressUpdateGlassPane        a reference to the progress update glass pane 
	 * @param fractalIterator                a reference to the fractal iterator
	 * @param fractalPanel                   a reference to the fractal panel
	 * @param statusBarCalculationTimeLabel  a reference to the status bar's calculation time label
	 * @param estimatePDF                    a <CODE>boolean</CODE> specifying whether or not the PDF of the iteration count should be estimated
	 * @param resources                      a reference to the JAR resources
	 */
	public IteratorTaskExecutor(JFrame parentFrame, JProgressUpdateGlassPane progressUpdateGlassPane, AFractalIterator fractalIterator, FractalPanel fractalPanel, JLabel statusBarCalculationTimeLabel, boolean estimatePDF, JARResources resources)
	{
		super(progressUpdateGlassPane);
		fProgressUpdateGlassPane = progressUpdateGlassPane;
		fFractalIterator = fractalIterator;
		fFractalPanel = fractalPanel;
		fStatusBarCalculationTimeLabel = statusBarCalculationTimeLabel;
		fEstimatePDF = estimatePDF;
		fResources = resources;
	}

	/******************
	 * PUBLIC METHODS *
	 *****************/

	/**
	 * Returns the fractal result buffer.
	 *
	 * @return the fractal result buffer
	 */
	public IterationBuffer getFractalResultBuffer()
	{
		return fFractalResultBuffer;
	}

	/**
	 * Manually sets the fractal result buffer.
	 *
	 * @param fractalResultBuffer  the new fractal result buffer
	 */
	public void setFractalResultBuffer(IterationBuffer fractalResultBuffer)
	{
		fFractalResultBuffer = fractalResultBuffer;
	}

	/**
	 * Returns the PDF of the iterations.
	 *
	 * @return the PDF of the iteration
	 */
	public FunctionLookupTable getIterationsPDF()
	{
		return fIterationsPDF;
	}

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Allows preparation of a task before adding it to the internal task list.
	 *
	 * @param task  the task to prepare
	 */
	@Override
	public void prepareTask(ATask task)
	{
		((IteratorTask) task).installFractalIterator(fFractalIterator);
	}

	/**
	 * Performs custom initialisation before tasks are executed.
	 */
	@Override
	protected void initialise()
	{
		if (fFractalIterator instanceof AConvergentFractalIterator) {
			fStatusBarCalculationTimeLabel.setText(I18NL10N.translate("text.StatusBar.CalculationTimePendingRoots"));
		}
		else {
			fStatusBarCalculationTimeLabel.setText(I18NL10N.translate("text.StatusBar.CalculationTimePending"));
		}

		// time the calculation
		fChrono = new Chrono();
		fChrono.start();
	}

	/**
	 * Collects all the partial iteration results and creates the fractal result buffer, as well
	 * as an estimation of the PDF of the number of iterations required.
	 */
	@Override
	protected void finishTasks()
	{
		// assembling partial iteration results into the fractal result buffer
		int width = fFractalIterator.getScreenWidth();
		int height = fFractalIterator.getScreenHeight();
		fFractalResultBuffer = new IterationBuffer(width,height);
		// assemble partial iteration results into the fractal result buffer
		for (ATask aTask : getTasks()) {
			IteratorTask task = (IteratorTask) aTask;
			IterationBuffer partialResult = task.getResult();

			ScreenLocation s1 = task.getS1();
			ScreenLocation s2 = task.getS2();
			for (int x = s1.fX; x <= s2.fX; ++x) {
				for (int y = s1.fY; y <= s2.fY; ++y) {
					int index = x + (y * width);
					int lookupIndex = (x - s1.fX) + ((y - s1.fY) * partialResult.fWidth);
					fFractalResultBuffer.fBuffer[index] = partialResult.fBuffer[lookupIndex];
				}
			}
		}

//XXX
/*
		fProgressUpdateGlassPane = new JProgressUpdateGlassPane();
		fProgressUpdateGlassPane.done();
		fProgressUpdateGlassPane.reset();
		TE te = new TE(fProgressUpdateGlassPane);
//System.out.println("Start executing...");
		te.execute();
*/
		
		// autodetect roots of convergent fractals
		if (fFractalIterator instanceof AConvergentFractalIterator) {

			if (((AConvergentFractalIterator) fFractalIterator).getAutomaticRootDetectionEnabled()) {
//XXX
System.out.println("Autodetect roots of convergent fractals...");
Chrono chrono = new Chrono();
chrono.start();

				ArrayList<ComplexNumber> roots = new ArrayList<ComplexNumber>();
				double maxObservedExponentialIterationCount = 0.0;
				double rootTolerance = ((AConvergentFractalIterator) fFractalIterator).getRootTolerance();

				for (int index = 0; index < fFractalResultBuffer.fBuffer.length; ++index) {
					IterationResult iterationResult = fFractalResultBuffer.fBuffer[index];

					// did we converge on a root?
					if ((iterationResult != null) && (iterationResult.fRootIndex > 0)) {
						ComplexNumber z = new ComplexNumber(iterationResult.fRealComponent,iterationResult.fImaginaryComponent);

						// try to find root
						boolean rootFound = false;
						for (int rootIndex = 0; rootIndex < roots.size(); ++rootIndex) {
							ComplexNumber candidateRoot = roots.get(rootIndex);
							if (z.subtract(candidateRoot).modulus() < rootTolerance) {
								iterationResult.fRootIndex = rootIndex + 1;
								if (iterationResult.fExponentialIterationCount > maxObservedExponentialIterationCount) {
									maxObservedExponentialIterationCount = iterationResult.fExponentialIterationCount;
								}
								rootFound = true;
							}
						}
						if (!rootFound) {
							roots.add(z);
						}
					} // if ((iterationResult != null) && (iterationResult.fRootIndex > 0))
				} // for index

				((AConvergentFractalIterator) fFractalIterator).setMaxObservedExponentialIterationCount(maxObservedExponentialIterationCount);
System.out.println("  Done: " + String.valueOf(chrono.getElapsedTimeInMilliseconds()) + "ms needed");
			} // if (((AConvergentFractalIterator) fFractalIterator).getAutomaticRootDetectionEnabled())
		} // if (fFractalIterator instanceof AConvergentFractalIterator)

		if (fEstimatePDF) {
			// estimate PDF of the iterations
			double maxNrOfIterations = fFractalIterator.getMaxNrOfIterations();
			double[] fIterationsRawPDFData = new double[width * height];

			for (int index = 0; index < fIterationsRawPDFData.length; ++index) {
				if (fFractalResultBuffer.fBuffer[index] != null) {
					if (fFractalResultBuffer.fBuffer[index].fNrOfIterations < IterationResult.kInfinity) {
						fIterationsRawPDFData[index] = fFractalResultBuffer.fBuffer[index].fNrOfIterations;
					}
					else {
					fIterationsRawPDFData[index] = maxNrOfIterations;
					}
				}
			} // for index

			// estimate PDF for the number of iterations needed
			fIterationsEmpiricalDistribution = new EmpiricalDistribution(fIterationsRawPDFData);
			fIterationsEmpiricalDistribution.estimateKDEPDF(
				MathTools.EKernelType.kEpanechnikov,
				fIterationsEmpiricalDistribution.calculateKDEPDFBandwidth(MathTools.EKernelType.kEpanechnikov),
				kNrOfKDEPDFBins,
				0,maxNrOfIterations);
			fIterationsPDF = fIterationsEmpiricalDistribution.getFullKDEPDF();
		} // if (fEstimatePDF)

		// render fractal to screen
		fFractalPanel.recolor();

		// update status bar
		int nrOfProcessors = getNrOfThreadsToUse();
		if (nrOfProcessors == 1) {
			fStatusBarCalculationTimeLabel.setText(
				I18NL10N.translate("text.StatusBar.CalculationTimeSingular",
					(new TimeStamp(fChrono.getElapsedTimeInMilliseconds())).getHMSString()));
		}
		else {
			fStatusBarCalculationTimeLabel.setText(
				I18NL10N.translate("text.StatusBar.CalculationTimePlural",
					(new TimeStamp(fChrono.getElapsedTimeInMilliseconds())).getHMSString(),
					String.valueOf(nrOfProcessors)));
		}

		// play a "calculation-finished" sound
		if (MP3Player.systemSoundsEnabled()) {
			try {
				MP3Player mp3Player = new MP3Player(fResources.getInputStream("application-resources/sounds/calculation-finished.mp3"));
				mp3Player.play(MP3Player.EPlaying.kUnblocked);
			}
			catch (FileDoesNotExistException exc) {
				// ignore
			}
			catch (SoundPlayingException exc) {
				// ignore
			}
		}
	}

//XXX
/*
	private class TE extends TaskExecutor
	{
		public TE(JProgressUpdateGlassPane progressUpdateGlassPane)
		{
			super(progressUpdateGlassPane);
			System.out.println("TE::ctor()");
			setNrOfThreadsToUse(1);
			for (int i = 0; i < 100; ++i) {
				T t = new T();
				addTask(t);
			}
		}
		protected void finishTasks()
		{
			System.out.println("TE::finishTasks()");
		}
	}
	
	private class T extends ATask
	{
		public T()
		{
			System.out.println("  T::ctor()");
		}
		protected void executeTask()
		{
			System.out.println("  T::executeTask()");
			Chrono.wait(10);
		}
		protected void finishTask()
		{
			System.out.println("  T::finishTask()");
		}
	}
*/	
}
