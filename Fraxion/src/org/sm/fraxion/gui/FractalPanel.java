// ---------------------------------
// Filename      : FractalPanel.java
// Author        : Sven Maerivoet
// Last modified : 23/06/2015
// Target        : Java VM (1.8)
// ---------------------------------

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

package org.sm.fraxion.gui;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;
import org.sm.fraxion.concurrent.*;
import org.sm.fraxion.fractals.*;
import org.sm.fraxion.fractals.convergent.*;
import org.sm.fraxion.fractals.markuslyapunov.*;
import org.sm.fraxion.fractals.util.*;
import org.sm.fraxion.gui.dialogs.*;
import org.sm.fraxion.gui.filters.*;
import org.sm.fraxion.gui.util.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.math.*;
import org.sm.smtools.math.complex.*;
import org.sm.smtools.swing.util.*;
import org.sm.smtools.util.*;

/**
 * The <CODE>FractalPanel</CODE> class provides the main GUI for the fractal iterators.
 * <P>
 * The system keeps (and calculates) a fractal result buffer (containing the raw results), which is then transformed into a fractal image buffer taking into
 * account the current settings of the colour map, drawing, and post-processing filter techniques. When the panel is repainted, the fractal image buffer is each time copied into
 * a render buffer, which is then copied onto the screen.
 * <P>
 * Drawing is controlled via the <CODE>repaint()</CODE> and {@link FractalPanel#recolor()} methods. Explicit recalculation of the fractal is handled via
 * the {@link IteratorController#recalc()} method.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 * 
 * @author  Sven Maerivoet
 * @version 23/06/2015
 */
public final class FractalPanel extends JPanel
{
	/**
	 * The different directions of panning.
	 */
	public static enum EPanDirection {kLeft, kRight, kUp, kDown};

	// the minimum horizontal and vertical screen region size (expressed in pixels) to zoom in on
	private static final int kMinimumZoomSize = 25;

	// interface specific constants
	private static final int kInsetEdgeOffset = 20;
	private static final int kMinInsetWidth = 100;
	private static final int kMinInsetHeight = kMinInsetWidth;
	private static final int kCurrentLocationOffset = 20;
	private static final double kOrbitsScreenScale = 0.9;
	private static final int kOrbitAnalysesPanelOffset = 20;
	private static final int kOrbitDiametre = 15;
	private static final float kOrbitPathWidth = 5.0f;
	private static final int kMaxNrOfIterationsToShowForDualFractal = 1000;
	private static final int kNrOfOrbitPointsToExcludeInPanelAnalysis = 2;
	private static final int kMinOrbitDiametre = 5;
	private static final int kMaxOrbitDiametre = 15;
	private static final float kMinStrokeWidth = 0.5f;
	private static final float kMaxStrokeWidth = 5.0f;
	private static final int kMainFractalOverviewDefaultLongestSide = 250;
	private static final int kMaxNrOfGridSpacesPerDimension = 8; // must be even

	// internal datastructures
	private JViewport fViewport;
	private IteratorController fIteratorController;
	private int fInsetWidth;
	private int fInsetHeight;
	private BufferedImage fFractalImageBuffer;
	private BufferedImage fInsetFractalImageBuffer;
	private BufferedImage fRenderBuffer;
	private Graphics2D fRenderBufferGraphics;
	private boolean fRevalidating;
	private boolean fShowInset;
	private boolean fAutoSuppressDualFractal;
	private int fInsetX;
	private int fInsetY;
	private int fInsetSizePercentage;
	private boolean fAutoZoomInset;
	private boolean fInsetDirty;
	private boolean fShowDeformedMainFractal;
	private boolean fShowAxes;
	private boolean fShowOverlayGrid;
	private boolean fShowMainFractalOverview;
	private boolean fShowMagnifyingGlass;
	private int fMagnifyingGlassRegion;
	private int fMagnifyingGlassSize;
	private boolean fShowOrbits;
	private boolean fShowOrbitPaths;
	private boolean fScaleOrbitsToScreen;
	private boolean fShowOrbitAnalyses;
	private int fOrbitAnalysesPanelSizePercentage;
	private int fMaxNrOfIterationsInOrbitAnalyses;
	private boolean fShowZoomInformation;
	private boolean fShowCurrentLocation;
	private FractalIterationRangeInformation fMainFractalIterationRangeInformation;
	private FractalIterationRangeInformation fDualFractalIterationRangeInformation;
	private boolean fSelecting;
	private boolean fCentredZooming;
	private ScreenLocation fSelectionAnchor;
	private ScreenLocation fSelectionExtent;
	private ZoomStack fZoomStack;
	private boolean fAutoSelectMaxNrOfIterations;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Creates a <CODE>FractalPanel</CODE> object and initialises it with the main fractal.
	 *
	 * @param iteratorController  a reference to the iterator controller that will be used
	 */
	public FractalPanel(IteratorController iteratorController)
	{
		fIteratorController = iteratorController;
		initialise();
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Sets the viewport in which the fractal panel is displayed.
	 * 
	 * @param viewport  the viewport in which the fractal panel is displayed
	 */
	public void setViewport(JViewport viewport)
	{
		fViewport = viewport;
		repaint();
	}

	/**
	 * Sets whether or not to show the deformed main fractal (because of another initial complex point) in the inset.
	 *
	 * @param showDeformedMainFractal  a <CODE>boolean</CODE> indicating whether or not to show the deformed main fractal in the inset
	 */
	public void setDeformedMainfractal(boolean showDeformedMainFractal)
	{
		fShowDeformedMainFractal = showDeformedMainFractal;
		repaint();
	}

	/**
	 * Controls whether or not the axes should be shown.
	 *
	 * @param showAxes  a <CODE>boolean</CODE> that indicates whether or not the axes should be shown
	 */
	public void setShowAxes(boolean showAxes)
	{
		fShowAxes = showAxes;
		repaint();
	}

	/**
	 * Returns whether or not a rescaled overview version of the main fractal is shown as an inset.
	 *
	 * @return a <CODE>boolean</CODE> that indicates whether or not a rescaled overview version of the main fractal is shown as an inset
	 */
	public boolean getShowMainFractalOverview()
	{
		return fShowMainFractalOverview;
	}

	/**
	 * Controls whether or not a rescaled overview version of the main fractal is shown as an inset.
	 *
	 * @param showMainFractalOverview  a <CODE>boolean</CODE> that indicates whether or not a rescaled overview version of the main fractal is shown as an inset
	 */
	public void setShowMainFractalOverview(boolean showMainFractalOverview)
	{
		fShowMainFractalOverview = showMainFractalOverview;
		repaint();
	}

	/**
	 * Returns whether or not an overlay grid is shown.
	 *
	 * @return a <CODE>boolean</CODE> that indicates whether or not an overlay grid is shown
	 */
	public boolean getShowOverlayGrid()
	{
		return fShowOverlayGrid;
	}

	/**
	 * Controls whether or not an overlay grid is shown.
	 *
	 * @param showOverlayGrid  a <CODE>boolean</CODE> that indicates whether or not an overlay grid is shown
	 */
	public void setShowOverlayGrid(boolean showOverlayGrid)
	{
		fShowOverlayGrid = showOverlayGrid;
		repaint();
	}

	/**
	 * Controls whether or not the information of the zoom area should be shown.
	 *
	 * @param showZoomInformation  a <CODE>boolean</CODE> that indicates whether or not the information of the zoom area should be shown
	 */
	public void setShowZoomInformation(boolean showZoomInformation)
	{
		fShowZoomInformation = showZoomInformation;
		repaint();
	}

	/**
	 * Controls whether or not the current location should be shown.
	 *
	 * @param showCurrentLocation  a <CODE>boolean</CODE> that indicates whether or not the current location should be shown
	 */
	public void setShowCurrentLocation(boolean showCurrentLocation)
	{
		fShowCurrentLocation = showCurrentLocation;
		repaint();
	}

	/**
	 * Returns the region of the magnifying glass.
	 *
	 * @return the region of the magnifying glass
	 */
	public int getMagnifyingGlassRegion()
	{
		return fMagnifyingGlassRegion;
	}

	/**
	 * Returns the size of the magnifying glass.
	 *
	 * @return the size of the magnifying glass
	 */
	public int getMagnifyingGlassSize()
	{
		return fMagnifyingGlassSize;
	}

	/**
	 * Controls whether or not a magnifying glass is shown at the current location.
	 *
	 * @param showMagnifyingGlass  a <CODE>boolean</CODE> that indicates whether or not a magnifying glass is shown at the current location
	 */
	public void setShowMagnifyingGlass(boolean showMagnifyingGlass)
	{
		fShowMagnifyingGlass = showMagnifyingGlass;
		repaint();
	}

	/**
	 * Controls the region and size of the magnifying glass.
	 *
	 * @param magnifyingGlassRegion  the region of the magnifying glass
	 * @param magnifyingGlassSize    the size of the magnifying glass
	 */
	public void setMagnifyingGlassSize(int magnifyingGlassRegion, int magnifyingGlassSize)
	{
		fMagnifyingGlassRegion = magnifyingGlassRegion;
		fMagnifyingGlassSize = magnifyingGlassSize;
		repaint();
	}

	/**
	 * Returns whether or not the zooming region is centred around the selection anchor.
	 *
	 * @return a <CODE>boolean</CODE> that indicates whether or not the zooming region is centred around the selection anchor
	 */
	public boolean getCentredZooming()
	{
		return fCentredZooming;
	}

	/**
	 * Controls whether or not the zooming region is centred around the selection anchor.
	 *
	 * @param centredZooming  a <CODE>boolean</CODE> that indicates whether or not the zooming region is centred around the selection anchor
	 */
	public void setCentredZooming(boolean centredZooming)
	{
		fCentredZooming = centredZooming;
	}

	/**
	 * Returns whether or not the dual fractal inset should be shown.
	 *
	 * @return a <CODE>boolean</CODE> that indicates whether or not the dual fractal inset should be shown
	 */
	public boolean getShowInset()
	{
		return fShowInset;
	}

	/**
	 * Controls whether or not the dual fractal inset should be shown.
	 *
	 * @param showInset  a <CODE>boolean</CODE> that indicates whether or not the dual fractal inset should be shown
	 */
	public void setShowInset(boolean showInset)
	{
		fShowInset = showInset;
		repaint();
	}

	/**
	 * Returns whether or not the dual fractal is automatically supressed for high iteration counts.
	 *
	 * @return a <CODE>boolean</CODE> indicating whether or not the dual fractal is automatically supressed for high iteration counts
	 */
	public boolean getAutoSuppressDualFractal()
	{
		return fAutoSuppressDualFractal;
	}

	/**
	 * Sets whether or not the dual fractal is automatically supressed for high iteration counts.
	 *
	 * @param autoSuppressDualFractal  a <CODE>boolean</CODE> indicating whether or not the dual fractal is automatically supressed for high iteration counts
	 */
	public void setAutoSuppressDualFractal(boolean autoSuppressDualFractal)
	{
		fAutoSuppressDualFractal = autoSuppressDualFractal;
		repaint();
	}

	/**
	 * Returns whether or not the dual fractal is suppressed.
	 *
	 * @return a <CODE>boolean</CODE> indicating whether or not the dual fractal is suppressed
	 */
	public boolean isDualFractalSuppressed()
	{
		return (fAutoSuppressDualFractal && fIteratorController.getFractalIterator().getMaxNrOfIterations() > kMaxNrOfIterationsToShowForDualFractal);
	}

	/**
	 * Controls whether or not the dual fractal inset should automatically zoom with the main fractal.
	 *
	 * @param autoZoomInset  a <CODE>boolean</CODE> that indicates whether or not the dual fractal inset should automatically zoom with the main fractal 
	 */
	public void setAutoZoomInset(boolean autoZoomInset)
	{
		fAutoZoomInset = autoZoomInset;
		fInsetDirty = true;
		repaint();
	}

	/**
	 * Returns the inset size.
	 *
	 * @return the inset size
	 */
	public int getInsetSize()
	{
		return fInsetSizePercentage;
	}

	/**
	 * Sets the inset size as a percentage between 0 and 100.
	 *
	 * @param insetSize  the inset size as a percentage between 0 and 100
	 */
	public void setInsetSize(int insetSize)
	{
		fInsetSizePercentage = insetSize;
		fInsetDirty = true;
		repaint();
	}

	/**
	 * Controls whether or not the orbits should be shown.
	 *
	 * @param showOrbits  a <CODE>boolean</CODE> that indicates whether or not the orbits should be shown 
	 */
	public void setShowOrbits(boolean showOrbits)
	{
		fShowOrbits = showOrbits;
		repaint();
	}

	/**
	 * Controls whether or not the orbits should be scaled to the screen dimensions.
	 *
	 * @param scaleOrbitsToScreen  a <CODE>boolean</CODE> that indicates whether or not the orbits should be scaled to the screen dimensions 
	 */
	public void setScaleOrbitsToScreen(boolean scaleOrbitsToScreen)
	{
		fScaleOrbitsToScreen = scaleOrbitsToScreen;
		repaint();
	}

	/**
	 * Controls whether or not the orbit paths should be shown.
	 *
	 * @param showOrbitPaths  a <CODE>boolean</CODE> that indicates whether or not the orbit paths should be shown 
	 */
	public void setShowOrbitPaths(boolean showOrbitPaths)
	{
		fShowOrbitPaths = showOrbitPaths;
		repaint();
	}

	/**
	 * Controls whether or not the orbit analyses should be shown.
	 *
	 * @param showOrbitAnalyses  a <CODE>boolean</CODE> that indicates whether or not the orbit analyses should be shown 
	 */
	public void setShowOrbitAnalyses(boolean showOrbitAnalyses)
	{
		fShowOrbitAnalyses = showOrbitAnalyses;
		repaint();
	}

	/**
	 * Returns the orbit analyses panel size.
	 *
	 * @return the orbit analyses panel size
	 */
	public int getOrbitAnalysesPanelSize()
	{
		return fOrbitAnalysesPanelSizePercentage;
	}

	/**
	 * Sets the orbit analyses panel size as a percentage between 0 and 100.
	 *
	 * @param orbitAnalysesPanelSize  the orbit analyses panel size as a percentage between 0 and 100
	 */
	public void setOrbitAnalysesPanelSize(int orbitAnalysesPanelSize)
	{
		fOrbitAnalysesPanelSizePercentage = orbitAnalysesPanelSize;
		repaint();
	}

	/**
	 * Returns the maximum number of iterations in the orbit analyses panel.
	 *
	 * @return the maximum number of iterations in the orbit analyses panel
	 */
	public int getMaxNrOfIterationsInOrbitAnalyses()
	{
		return fMaxNrOfIterationsInOrbitAnalyses;
	}

	/**
	 * Sets the maximum number of iterations in the orbit analyses panel
	 *
	 * @param maxNrOfIterationsInOrbitAnalyses  the maximum number of iterations in the orbit analyses panel
	 */
	public void setMaxNrOfIterationsInOrbitAnalyses(int maxNrOfIterationsInOrbitAnalyses)
	{
		fMaxNrOfIterationsInOrbitAnalyses = maxNrOfIterationsInOrbitAnalyses;
		repaint();
	}

	/**
	 * Sets the selection anchor point.
	 *
	 * @param selectionAnchor  the selection anchor point to set
	 */
	public void setSelectionAnchor(ScreenLocation selectionAnchor)
	{
		fSelectionAnchor = selectionAnchor;
		fSelecting = true;
		fSelectionExtent = null;
	}

	/**
	 * Disables a current selection mode.
	 */
	public void disableSelecting()
	{
		fSelecting = false;
	}

	/**
	 * Returns whether or not selection mode is active.
	 *
	 * @return selectionAnchor a <CODE>boolean</CODE> indicating whether or not selection mode is active
	 */
	public boolean getSelecting()
	{
		return fSelecting;
	}

	/**
	 * Sets the selection extent and draws it on the screen.
	 *
	 * @param selectionExtent  the selection extent to set
	 */
	public void setSelectionExtent(ScreenLocation selectionExtent)
	{
		if (!fSelecting) {
			return;
		}

		if (fIteratorController.getColoringParameters().fLockAspectRatio) {
			double width = selectionExtent.fX - fSelectionAnchor.fX;
			double height = selectionExtent.fY - fSelectionAnchor.fY;
			double aspectRatio = width / height;

			double sign = +1.0;
			if (aspectRatio < 0) {
				sign = -1.0;
			}
			if (Math.abs(aspectRatio) > 1.0) {
				height = sign * width;
			}
			else {
				width = sign * height;
			}

			fSelectionExtent = new ScreenLocation(fSelectionAnchor.fX + (int) width,fSelectionAnchor.fY + (int) height);
		}
		else {
			fSelectionExtent = selectionExtent;
		}

		repaint();
	}

	/**
	 * Zooms in on the current selection.
	 */
	public void zoomToSelection()
	{
		fSelecting = false;

		if ((fSelectionAnchor != null) && (fSelectionExtent != null)) {
			Point2D.Double mouseSelectionAnchor = new Point2D.Double(fSelectionAnchor.fX,fSelectionAnchor.fY);
			Point2D.Double mouseSelectionExtent = new Point2D.Double(fSelectionExtent.fX,fSelectionExtent.fY);
			if (!fCentredZooming) {
				MathTools.forcePartialOrder(mouseSelectionAnchor,mouseSelectionExtent);
			}
			int mX1 = (int) mouseSelectionAnchor.getX();
			int mY1 = (int) mouseSelectionAnchor.getY();
			int mX2 = (int) mouseSelectionExtent.getX();
			int mY2 = (int) mouseSelectionExtent.getY();
			
			int zoomWidth = (int) Math.abs(fSelectionExtent.fX - fSelectionAnchor.fX);
			int zoomHeight = (int) Math.abs(fSelectionExtent.fY - fSelectionAnchor.fY); // because the screen's Y-axis points downwards
			int minimumZoomSize = kMinimumZoomSize;

			if (fCentredZooming) {
				zoomWidth = Math.abs(mX2 - mX1 - 1);
				zoomHeight = Math.abs(mY2 - mY1 - 1);
				mX1 = mX1 - zoomWidth;
				mY1 = mY1 - zoomHeight;
				mX2 = mX1 + ((zoomWidth + 1) * 2);
				mY2 = mY1 + ((zoomHeight + 1) * 2);
				minimumZoomSize /= 2;
			}
			fSelectionAnchor.fX = mX1;
			fSelectionAnchor.fY = mY1;
			fSelectionExtent.fX = mX2;
			fSelectionExtent.fY = mY2;
			ScreenLocation.forcePartialOrder(fSelectionAnchor,fSelectionExtent);

			// only zoom when the extent is large enough
			if ((zoomWidth > minimumZoomSize) && (zoomHeight > minimumZoomSize)) {
				ComplexNumber p1 = fIteratorController.getFractalIterator().convertScreenLocationToComplexNumber(fSelectionAnchor);
				ComplexNumber p2 = fIteratorController.getFractalIterator().convertScreenLocationToComplexNumber(fSelectionExtent);
				zoomIn(p1,p2);
			}
			else {
				// remove the selection rectangle
				repaint();
			}
		}
		else {
			// remove the selection rectangle
			repaint();
		}
	}

	/**
	 * Resets the zoom to the default region in the complex plane.
	 */
	public void resetZoom()
	{
		fZoomStack.clear();
		fZoomStack.push(fIteratorController.getFractalIterator().getDefaultP1(),fIteratorController.getFractalIterator().getDefaultP2());
		zoomToStack();
	}

	/**
	 * Zooms in on a specific region.
	 * 
	 * @param p1  the lower-left corner in the complex plane
	 * @param p2  the upper-right corner in the complex plane
	 */
	public void zoomIn(ComplexNumber p1, ComplexNumber p2)
	{
		fZoomStack.push(p1,p2);
		zoomToStack();
	}

	/**
	 * Zooms to the previous location on the zoom stack if it's not empty.
	 */
	public void zoomOut()
	{
		if (!fZoomStack.isEmpty()) {
			fZoomStack.pop();
			zoomToStack();
		}
	}

	/**
	 * Zooms to a specified existing zoom level.
	 *
	 * @param zoomLevel  the existing level to zoom to
	 */
	public void zoomToLevel(int zoomLevel)
	{
		if (fZoomStack.isEmpty()) {
			return;
		}
		else if (zoomLevel >= fZoomStack.getZoomLevel()) {
			return;
		}
		else {
			for (int level = fZoomStack.getZoomLevel(); level > zoomLevel; --level) {
				fZoomStack.pop();
			}
			zoomToStack();
		}
	}

	/**
	 * Externally sets the zoom stack.
	 *
	 * @param zoomStack  the zoom stack
	 */
	public void setZoomStack(ZoomStack zoomStack)
	{
		fZoomStack = zoomStack;
	}

	/**
	 * Returns the zoom stack.
	 *
	 * @return the zoom stack
	 */
	public ZoomStack getZoomStack()
	{
		return fZoomStack;
	}

	/**
	 * Sets the current zoom boundaries to those at the top of the zoom stack
	 * and adjusts their aspect ratio if necessary.
	 * 
	 * @param canvasSize  the optional width and height as an array of 2 <CODE>int</CODE>s
	 */
	public void zoomToStack(int ... canvasSize)
	{
		ComplexNumber p1 = new ComplexNumber(fZoomStack.getTopP1());
		ComplexNumber p2 = new ComplexNumber(fZoomStack.getTopP2());

		ComplexNumber.forcePartialOrder(p1,p2);

		AFractalIterator fractalIterator = fIteratorController.getFractalIterator();

		if (fIteratorController.getColoringParameters().fLockAspectRatio) {
			double re1 = p1.realComponent();
			double im1 = p1.imaginaryComponent();
			double re2 = p2.realComponent();
			double im2 = p2.imaginaryComponent();
			double fractalWidth = re2 - re1;
			double fractalHeight = im2 - im1;

			// adjust the aspect-ratio to fit inside that of the screen
			int width = fractalIterator.getScreenWidth();
			int height = fractalIterator.getScreenHeight();
			double screenAspectRatio = (double) width / (double) height;
			if (screenAspectRatio > 1.0) {
				// screen width > screen height => enlarge fractal width
				fractalWidth = fractalHeight * screenAspectRatio;
			}
			else {
				// screen height > screen width => enlarge fractal height
				fractalHeight = fractalWidth / screenAspectRatio;
			}

			// recentre
			re1 = ((re1 + re2) / 2.0) - (fractalWidth / 2.0);
			re2 = re1 + fractalWidth;
			im1 = ((im1 + im2) / 2.0) - (fractalHeight / 2.0);
			im2 = im1 + fractalHeight;

			p1 = new ComplexNumber(re1,im1);
			p2 = new ComplexNumber(re2,im2);
		} // fLockAspectRatio

		// notify the iterator of the new location in the complex plane and recalculate
		fractalIterator.setComplexBounds(p1,p2);

		if (fAutoSelectMaxNrOfIterations && !fractalIterator.getUseFixedNrOfIterations()) {
			int maxNrOfIterations = fractalIterator.autoDetermineMaxNrOfIterations();
			calibrateColorRange(maxNrOfIterations);
			fractalIterator.setMaxNrOfIterations(maxNrOfIterations);
		}

		fIteratorController.recalc();
	}

	/**
	 * Calibrates the range in the colouring parameters to the maximum number of iterations to use.
	 * 
	 * @param maxNrOfIterations  the maximum number of iterations to use
	 */
	public final void calibrateColorRange(int maxNrOfIterations)
	{
		ColoringParameters coloringParameters = fIteratorController.getColoringParameters();

		// update the coloring parameters
		int discreteColorRange = coloringParameters.fColorMapDiscreteColorRange;
		if (discreteColorRange == fIteratorController.getFractalIterator().getMaxNrOfIterations()) {
			discreteColorRange = maxNrOfIterations;
		}
		coloringParameters.fColorMapDiscreteColorRange = discreteColorRange;

		// 	adjust colourmap iteration range to comply with the selected maximum number of iterations
		int colorMapLowIterationRange = coloringParameters.fLowIterationRange;
		int colorMapHighIterationRange = coloringParameters.fHighIterationRange;
		if (maxNrOfIterations < fIteratorController.getFractalIterator().getMaxNrOfIterations()) {
			if (colorMapLowIterationRange > maxNrOfIterations) {
				colorMapLowIterationRange = 0;
			}
			if (colorMapHighIterationRange > maxNrOfIterations) {
				colorMapHighIterationRange = maxNrOfIterations;
			}
		}
		else {
			colorMapHighIterationRange = maxNrOfIterations;
		}

		coloringParameters.fLowIterationRange = colorMapLowIterationRange;
		coloringParameters.fHighIterationRange = colorMapHighIterationRange;
	}

	/**
	 * Pans the current fractal in the complex plane.
	 *
	 * @param panDirection    the direction to pan to
	 * @param panFactor       the percentage of the width and height to pan
	 * @param inversePanning  a <CODE>boolean</CODE> indicating whether or not the panning direction is inverted
	 */
	public void pan(EPanDirection panDirection, double panFactor, boolean inversePanning)
	{
		MathTools.clip(panFactor,0.0,1.0);
		if (inversePanning) {
			panFactor *= -1.0;
		}
		ComplexNumber p1 = fZoomStack.getTopP1();
		ComplexNumber p2 = fZoomStack.getTopP2();
		double p1X = p1.realComponent();
		double p1Y = p1.imaginaryComponent();
		double p2X = p2.realComponent();
		double p2Y = p2.imaginaryComponent();
		double horizontalDisplacement = Math.abs(p2X - p1X) * panFactor;
		double verticalDisplacement = Math.abs(p2Y - p1Y) * panFactor;

		if (panDirection == EPanDirection.kLeft) {
			p1X -= horizontalDisplacement;
			p2X -= horizontalDisplacement;
		}
		else if (panDirection == EPanDirection.kRight) {
			p1X += horizontalDisplacement;
			p2X += horizontalDisplacement;
		}
		else if (panDirection == EPanDirection.kUp) {
			// invert direction because of a negative Y-axis on screen
			p1Y += verticalDisplacement;
			p2Y += verticalDisplacement;
		}
		else if (panDirection == EPanDirection.kDown) {
			// invert direction because of a negative Y-axis on screen
			p1Y -= verticalDisplacement;
			p2Y -= verticalDisplacement;
		}

		fZoomStack.modifyTop(new ComplexNumber(p1X,p1Y),new ComplexNumber(p2X,p2Y));
		zoomToStack();
	}

	/**
	 * Exports the main fractal to a PNG file.
	 *
	 * @param filename  the name of the file to export the main fractal to
	 * @return          <CODE>true</CODE> if the export was successful, <CODE>false</CODE> otherwise
	 */
	public boolean exportMainFractal(String filename)
	{
		try {
			return ImageIO.write(fFractalImageBuffer,"PNG",new File(filename));
		}
		catch (IOException exc) {
			return false;
		}
	}

	/**
	 * Returns an image containing the last rendered main fractal.
	 *
	 * @return an image containing the last rendered main fractal
	 */
	public BufferedImage getFractalImage()
	{
		return fFractalImageBuffer;
	}

	/**
	 * Reinitialises the colour information and automatically applies the post-processing filters.
	 */
	public void recolor()
	{
		fRevalidating = false;
		prepareFractalColoringInformation(fIteratorController.getFractalResultBuffer(),fMainFractalIterationRangeInformation);
		applyPostProcessingFilters();
	}

	/**
	 * Copies the fractal result buffer to the fractal image buffer,
	 * taking into account the current settings of the colour map, drawing, and post-processing techniques.
	 * It then calls <CODE>repaint()</CODE>.
	 */
	public void applyPostProcessingFilters()
	{
		fFractalImageBuffer = colorFractal(fIteratorController.getFractalResultBuffer(),fMainFractalIterationRangeInformation);

		// fail-safe
		if (fFractalImageBuffer == null) {
			return;
		}

		fInsetDirty = true;
		repaint();
	}

	/**
	 */
	@Override
	public void revalidate()
	{
		fRevalidating = true;
		super.revalidate();
	}

	/**
	 */
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		// fail-safes
		if (fFractalImageBuffer == null) {
			return;
		}

		int width = fIteratorController.getFractalIterator().getScreenWidth();
		int height = fIteratorController.getFractalIterator().getScreenHeight();
		if ((width == 0) || (height == 0)) {
			return;
		}

		// use 2D graphics functionality
		Graphics2D fRenderBufferGraphics = (Graphics2D) g;
		fRenderBufferGraphics.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);

		// create the render buffer
		fRenderBuffer = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

		// stretch the current image as the panel is probably being resized
		if ((fRevalidating) && (fRenderBuffer != null)) {
			((Graphics2D) (fRenderBuffer.getGraphics())).drawImage(fFractalImageBuffer,0,0,width,height,null);
		}
		else {
			// copy the current fractal image to the render buffer
			fRenderBuffer.setData(fFractalImageBuffer.getData());
		}

		// supplement the render buffer with onscreen miscellaneous information
		renderSupplementalInformation();

		// copy the render buffer to the screen
		fRenderBufferGraphics.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		fRenderBufferGraphics.drawImage(fRenderBuffer,0,0,null);

		fRenderBufferGraphics.dispose();
		g.dispose();
	}

	/**
	 */
	@Override
	public Dimension getPreferredSize()
	{
		return fIteratorController.getFractalIterator().getScreenBounds();
	}

	/**
	 */
	@Override
	public Dimension getMinimumSize()
	{
		return fIteratorController.getFractalIterator().getScreenBounds();
	}

	/**
	 */
	@Override
	public Dimension getMaximumSize()
	{
		return fIteratorController.getFractalIterator().getScreenBounds();
	}

	/**
	 * Returns <CODE>true</CODE> if the mouse cursor is currently inside the complex plane, <CODE>false</CODE> otherwise.
	 *
	 * @return a <CODE>boolean</CODE> indicating whether or not the mouse cursor is currently inside the complex plane
	 */
	public boolean isMouseInsideComplexPlane()
	{
		boolean inside = false;
		try {
			Point m = getMousePosition();
			inside = (m != null);
		}
		catch (HeadlessException exc) {
			// ignore
		}

		return inside;
	}

	/*******************
	 * PRIVATE METHODS *
	 *******************/

	/**
	 */
	private void initialise()
	{
		fShowInset = true;
		fAutoSuppressDualFractal = true;
		fInsetSizePercentage = 25;
		fAutoZoomInset = false;
		fInsetDirty = true;
		fShowDeformedMainFractal = false;
		fShowAxes = false;
		fShowOverlayGrid = false;
		fShowMagnifyingGlass = false;
		fMagnifyingGlassRegion = MagnifyingGlassSizeChooser.kDefaultRegion;
		fMagnifyingGlassSize = MagnifyingGlassSizeChooser.kDefaultSize;
		fShowMainFractalOverview = false;
		fShowOrbits = false;
		fShowOrbitPaths = true;
		fScaleOrbitsToScreen = false;
		fShowOrbitAnalyses = false;
		fIteratorController.setEstimatePDF(false);
		fOrbitAnalysesPanelSizePercentage = 45;
		fMaxNrOfIterationsInOrbitAnalyses = 100;
		fShowZoomInformation = true;
		fShowCurrentLocation = true;

		ColoringParameters coloringParameters = fIteratorController.getColoringParameters();
		coloringParameters.fInteriorGradientColorMap = new JGradientColorMap();
		coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kJet);
		coloringParameters.fExteriorGradientColorMap = new JGradientColorMap();
		coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kJet);
		coloringParameters.fInteriorColorMapInverted = false;
		coloringParameters.fInteriorColorMapWrappedAround = false;
		coloringParameters.fExteriorColorMapInverted = false;
		coloringParameters.fExteriorColorMapWrappedAround = false;
		coloringParameters.fCalculateAdvancedColoring = false;
		coloringParameters.fUseTigerStripes = false;
		coloringParameters.fTigerGradientColorMap = new JGradientColorMap();
		coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kJet);
		coloringParameters.fTigerUseFixedColor = true;
		coloringParameters.fTigerStripeFixedColor = Color.BLACK;
		coloringParameters.fInteriorColor = Color.BLACK;
		coloringParameters.fExteriorColor = Color.WHITE;
		coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kFixedColor;
		coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kSmoothNICLevelSets;
		coloringParameters.fColorMapInteriorSectorDecompositionRange = 1000;
		coloringParameters.fColorMapExteriorSectorDecompositionRange = coloringParameters.fColorMapInteriorSectorDecompositionRange;
		coloringParameters.fColorMapScaling = ColoringParameters.EColorMapScaling.kLinear;
		coloringParameters.fColorMapScalingFunctionMultiplier = 1.0;
		coloringParameters.fColorMapScalingArgumentMultiplier = 1.0;
		coloringParameters.fRankOrderRestrictHighIterationCountColors = true;
		coloringParameters.fColorMapRepeatMode = false;
		coloringParameters.fColorMapColorRepetition = 5.0;
		coloringParameters.fColorMapColorOffset = 0.0;
		coloringParameters.fColorMapUsage = ColoringParameters.EColorMapUsage.kFull;
		coloringParameters.fColorMapContinuousColorRange = 10.0;
		coloringParameters.fColorMapDiscreteColorRange = fIteratorController.getFractalIterator().getMaxNrOfIterations();
		coloringParameters.fLowIterationRange = 0;
		coloringParameters.fHighIterationRange = fIteratorController.getFractalIterator().getMaxNrOfIterations();
		coloringParameters.fBrightnessFactor = 5.0;
		coloringParameters.fUsePostProcessingFilters = false;
		coloringParameters.fPostProcessingFilterChain = new FilterChain();
		coloringParameters.fLockAspectRatio = true;
		
		fMainFractalIterationRangeInformation = new FractalIterationRangeInformation();
		fDualFractalIterationRangeInformation = new FractalIterationRangeInformation();

		fSelecting = false;
		fCentredZooming = true;
		fZoomStack = new ZoomStack();

		setAutoSelectMaxNrOfIterations(true);
	}

	/**
	 * Sets whether or not the maximum number of iterations should be auto selected.
	 *
	 * @param autoSelectMaxNrOfIterations  a <CODE>boolean</CODE> indicating whether or not the maximum number of iterations should be auto selected
	 */
	public void setAutoSelectMaxNrOfIterations(boolean autoSelectMaxNrOfIterations)
	{
		fAutoSelectMaxNrOfIterations = autoSelectMaxNrOfIterations;
	}

	/**
	 * Prepares the colouring based on the specified fractal result buffer and current colour map settings.
	 * Drawing uses a triple-pass algorithm:<BR>
	 *   First pass to determine minimum and maximum iteration counts<BR>
 	 *   Second pass to optionally construct histogram<BR>
	 *   Third pass to determine colour-mapped iteration counts (= recolor)
	 *
	 * @param fractalResultBuffer               the buffer containing the fractal iteration results
	 * @param fractalIterationRangeInformation  the precalculated iteration range information
	 */
	private void prepareFractalColoringInformation(IterationBuffer fractalResultBuffer, FractalIterationRangeInformation fractalIterationRangeInformation)
	{
		// fail-safe
		if (fractalResultBuffer == null) {
			return;
		}

		ColoringParameters coloringParameters = fIteratorController.getColoringParameters();

		fractalIterationRangeInformation.fInteriorMinNrOfIterations = IterationResult.kInfinity;
		fractalIterationRangeInformation.fInteriorMaxNrOfIterations = -IterationResult.kInfinity;
		fractalIterationRangeInformation.fExteriorMinNrOfIterations = IterationResult.kInfinity;
		fractalIterationRangeInformation.fExteriorMaxNrOfIterations = -IterationResult.kInfinity;
		fractalIterationRangeInformation.fExteriorMaxNrOfIntegralIterations = fractalIterationRangeInformation.fExteriorMaxNrOfIterations;

		// manual code optimisation: arrays provide a fast store and lookup mechanism; we construct them with the maximum possible size needed
		double[] interiorRankColoringHistogram = new double[fractalResultBuffer.fWidth * fractalResultBuffer.fHeight];
		double[] exteriorRankColoringHistogram = new double[fractalResultBuffer.fWidth * fractalResultBuffer.fHeight];
		fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints = 0;
		fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints = 0;

		// first pass: determine minimum and maximum iteration counts (for interior and exterior colouring)
		for (int index = 0; index < fractalResultBuffer.fBuffer.length; ++index) {
			if (fractalResultBuffer.fBuffer[index] != null) {

				if (fractalResultBuffer.fBuffer[index].liesInInterior()) {
					// determine extrema for interior colouring
					if (coloringParameters.fInteriorColoringMethod != ColoringParameters.EColoringMethod.kFixedColor) {
						if (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kSmoothEICLevelSets) {
							fractalIterationRangeInformation.fInteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fInteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fExponentialIterationCount);
							fractalIterationRangeInformation.fInteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fInteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fExponentialIterationCount);
							interiorRankColoringHistogram[fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fExponentialIterationCount;
						}
						else if (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kSectorDecomposition) {
							fractalIterationRangeInformation.fInteriorMinNrOfIterations = 1.0;
							fractalIterationRangeInformation.fInteriorMaxNrOfIterations = coloringParameters.fColorMapInteriorSectorDecompositionRange;
							interiorRankColoringHistogram[fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].getSector(coloringParameters.fColorMapInteriorSectorDecompositionRange);
						}
						else if (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kRealComponent) {
							fractalIterationRangeInformation.fInteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fInteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fRealComponent);
							fractalIterationRangeInformation.fInteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fInteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fRealComponent);
							interiorRankColoringHistogram[fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fRealComponent;
						}
						else if (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kImaginaryComponent) {
							fractalIterationRangeInformation.fInteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fInteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fImaginaryComponent);
							fractalIterationRangeInformation.fInteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fInteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fImaginaryComponent);
							interiorRankColoringHistogram[fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fImaginaryComponent;
						}
						else if (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kModulus) {
							fractalIterationRangeInformation.fInteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fInteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fModulus);
							fractalIterationRangeInformation.fInteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fInteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fModulus);
							interiorRankColoringHistogram[fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fModulus;
						}
						else if (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kAverageDistance) {
							fractalIterationRangeInformation.fInteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fInteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fAverageDistance);
							fractalIterationRangeInformation.fInteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fInteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fAverageDistance);
							interiorRankColoringHistogram[fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fAverageDistance;
						}
						else if (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kAngle) {
							fractalIterationRangeInformation.fInteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fInteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fAngle);
							fractalIterationRangeInformation.fInteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fInteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fAngle);
							interiorRankColoringHistogram[fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fAngle;
						}
						else if (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kLyapunovExponent) {
							fractalIterationRangeInformation.fInteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fInteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fLyapunovExponent);
							fractalIterationRangeInformation.fInteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fInteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fLyapunovExponent);
							interiorRankColoringHistogram[fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fLyapunovExponent;
						}
						else if (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kCurvature) {
							fractalIterationRangeInformation.fInteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fInteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fCurvature);
							fractalIterationRangeInformation.fInteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fInteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fCurvature);
							interiorRankColoringHistogram[fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fCurvature;
						}
						else if (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kStriping) {
							fractalIterationRangeInformation.fInteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fInteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fStriping);
							fractalIterationRangeInformation.fInteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fInteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fStriping);
							interiorRankColoringHistogram[fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fStriping;
						}
						else if (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kMinimumGaussianIntegersDistance) {
							fractalIterationRangeInformation.fInteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fInteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fMinimumGaussianIntegersDistance);
							fractalIterationRangeInformation.fInteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fInteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fMinimumGaussianIntegersDistance);
							interiorRankColoringHistogram[fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fMinimumGaussianIntegersDistance;
						}
						else if (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kAverageGaussianIntegersDistance) {
							fractalIterationRangeInformation.fInteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fInteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fAverageGaussianIntegersDistance);
							fractalIterationRangeInformation.fInteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fInteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fAverageGaussianIntegersDistance);
							interiorRankColoringHistogram[fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fAverageGaussianIntegersDistance;
						}
						else if (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kExteriorDistance) {
							fractalIterationRangeInformation.fInteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fInteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fExteriorDistance);
							fractalIterationRangeInformation.fInteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fInteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fExteriorDistance);
							interiorRankColoringHistogram[fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fExteriorDistance;
						}
						else if (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kOrbitTrapDisk) {
							fractalIterationRangeInformation.fInteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fInteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fOrbitTrapDiskDistance);
							fractalIterationRangeInformation.fInteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fInteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fOrbitTrapDiskDistance);
							interiorRankColoringHistogram[fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fOrbitTrapDiskDistance;
						}
						else if (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kOrbitTrapCrossStalks) {
							fractalIterationRangeInformation.fInteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fInteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fOrbitTrapCrossStalksDistance);
							fractalIterationRangeInformation.fInteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fInteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fOrbitTrapCrossStalksDistance);
							interiorRankColoringHistogram[fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fOrbitTrapCrossStalksDistance;
						}
						else if (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kOrbitTrapSine) {
							fractalIterationRangeInformation.fInteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fInteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fOrbitTrapSineDistance);
							fractalIterationRangeInformation.fInteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fInteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fOrbitTrapSineDistance);
							interiorRankColoringHistogram[fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fOrbitTrapSineDistance;
						}
						else if (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kOrbitTrapTangens) {
							fractalIterationRangeInformation.fInteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fInteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fOrbitTrapTangensDistance);
							fractalIterationRangeInformation.fInteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fInteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fOrbitTrapTangensDistance);
							interiorRankColoringHistogram[fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fOrbitTrapTangensDistance;
						}
					} // if (fInteriorColoringMethod != EColoringMethod.kFixedColor)
				} // if (fractalResultBuffer.fBuffer[index].liesInInterior())
				else {
					// determine extrema for exterior colouring
					fractalIterationRangeInformation.fExteriorMaxNrOfIntegralIterations = Math.max(fractalIterationRangeInformation.fExteriorMaxNrOfIntegralIterations,fractalResultBuffer.fBuffer[index].fNrOfIterations);

					if (coloringParameters.fExteriorColoringMethod != ColoringParameters.EColoringMethod.kFixedColor) {
						if (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kDiscreteLevelSets) {
							fractalIterationRangeInformation.fExteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fExteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fNrOfIterations);
							fractalIterationRangeInformation.fExteriorMaxNrOfIterations = fractalIterationRangeInformation.fExteriorMaxNrOfIntegralIterations;
							exteriorRankColoringHistogram[fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fNrOfIterations;
						}
						else if (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kSmoothNICLevelSets) {
							fractalIterationRangeInformation.fExteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fExteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fNormalisedIterationCount);
							fractalIterationRangeInformation.fExteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fExteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fNormalisedIterationCount);
							exteriorRankColoringHistogram[fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fNormalisedIterationCount;
						}
						else if (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kSmoothEICLevelSets) {
							fractalIterationRangeInformation.fExteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fExteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fExponentialIterationCount);
							fractalIterationRangeInformation.fExteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fExteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fExponentialIterationCount);
							exteriorRankColoringHistogram[fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fExponentialIterationCount;
						}
						else if (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kSectorDecomposition) {
							fractalIterationRangeInformation.fExteriorMinNrOfIterations = 1.0;
							fractalIterationRangeInformation.fExteriorMaxNrOfIterations = coloringParameters.fColorMapExteriorSectorDecompositionRange;
							exteriorRankColoringHistogram[fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints++] = (double) fractalResultBuffer.fBuffer[index].getSector(coloringParameters.fColorMapExteriorSectorDecompositionRange);
						}
						else if (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kRealComponent) {
							fractalIterationRangeInformation.fExteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fExteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fRealComponent);
							fractalIterationRangeInformation.fExteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fExteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fRealComponent);
							exteriorRankColoringHistogram[fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fRealComponent;
						}
						else if (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kImaginaryComponent) {
							fractalIterationRangeInformation.fExteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fExteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fImaginaryComponent);
							fractalIterationRangeInformation.fExteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fExteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fImaginaryComponent);
							exteriorRankColoringHistogram[fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fImaginaryComponent;
						}
						else if (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kModulus) {
							fractalIterationRangeInformation.fExteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fExteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fModulus);
							fractalIterationRangeInformation.fExteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fExteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fModulus);
							exteriorRankColoringHistogram[fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fModulus;
						}
						else if (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kAverageDistance) {
							fractalIterationRangeInformation.fExteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fExteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fAverageDistance);
							fractalIterationRangeInformation.fExteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fExteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fAverageDistance);
							exteriorRankColoringHistogram[fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fAverageDistance;
						}
						else if (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kAngle) {
							fractalIterationRangeInformation.fExteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fExteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fAngle);
							fractalIterationRangeInformation.fExteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fExteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fAngle);
							exteriorRankColoringHistogram[fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fAngle;
						}
						else if (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kLyapunovExponent) {
							fractalIterationRangeInformation.fExteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fExteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fLyapunovExponent);
							fractalIterationRangeInformation.fExteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fExteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fLyapunovExponent);
							exteriorRankColoringHistogram[fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fLyapunovExponent;
						}
						else if (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kCurvature) {
							fractalIterationRangeInformation.fExteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fExteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fCurvature);
							fractalIterationRangeInformation.fExteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fExteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fCurvature);
							exteriorRankColoringHistogram[fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fCurvature;
						}
						else if (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kStriping) {
							fractalIterationRangeInformation.fExteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fExteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fStriping);
							fractalIterationRangeInformation.fExteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fExteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fStriping);
							exteriorRankColoringHistogram[fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fStriping;
						}
						else if (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kMinimumGaussianIntegersDistance) {
							fractalIterationRangeInformation.fExteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fExteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fMinimumGaussianIntegersDistance);
							fractalIterationRangeInformation.fExteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fExteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fMinimumGaussianIntegersDistance);
							exteriorRankColoringHistogram[fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fMinimumGaussianIntegersDistance;
						}
						else if (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kAverageGaussianIntegersDistance) {
							fractalIterationRangeInformation.fExteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fExteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fAverageGaussianIntegersDistance);
							fractalIterationRangeInformation.fExteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fExteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fAverageGaussianIntegersDistance);
							exteriorRankColoringHistogram[fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fAverageGaussianIntegersDistance;
						}
						else if (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kExteriorDistance) {
							fractalIterationRangeInformation.fExteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fExteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fExteriorDistance);
							fractalIterationRangeInformation.fExteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fExteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fExteriorDistance);
							exteriorRankColoringHistogram[fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fExteriorDistance;
						}
						else if (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kOrbitTrapDisk) {
							fractalIterationRangeInformation.fExteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fExteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fOrbitTrapDiskDistance);
							fractalIterationRangeInformation.fExteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fExteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fOrbitTrapDiskDistance);
							exteriorRankColoringHistogram[fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fOrbitTrapDiskDistance;
						}
						else if (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kOrbitTrapCrossStalks) {
							fractalIterationRangeInformation.fExteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fExteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fOrbitTrapCrossStalksDistance);
							fractalIterationRangeInformation.fExteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fExteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fOrbitTrapCrossStalksDistance);
							exteriorRankColoringHistogram[fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fOrbitTrapCrossStalksDistance;
						}
						else if (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kOrbitTrapSine) {
							fractalIterationRangeInformation.fExteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fExteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fOrbitTrapSineDistance);
							fractalIterationRangeInformation.fExteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fExteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fOrbitTrapSineDistance);
							exteriorRankColoringHistogram[fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fOrbitTrapSineDistance;
						}
						else if (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kOrbitTrapTangens) {
							fractalIterationRangeInformation.fExteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fExteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fOrbitTrapTangensDistance);
							fractalIterationRangeInformation.fExteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fExteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fOrbitTrapTangensDistance);
							exteriorRankColoringHistogram[fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fOrbitTrapTangensDistance;
						}
						else if ((coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kDiscreteRoots) ||
										 (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kSmoothRoots)) {
							fractalIterationRangeInformation.fExteriorMinNrOfIterations = Math.min(fractalIterationRangeInformation.fExteriorMinNrOfIterations,fractalResultBuffer.fBuffer[index].fRootIndex);
							fractalIterationRangeInformation.fExteriorMaxNrOfIterations = Math.max(fractalIterationRangeInformation.fExteriorMaxNrOfIterations,fractalResultBuffer.fBuffer[index].fRootIndex);
							exteriorRankColoringHistogram[fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints++] = fractalResultBuffer.fBuffer[index].fRootIndex;
						}
					} // if (fExteriorColoringMethod != EColoringMethod.kFixedColor)
				} // if (!fractalResultBuffer.fBuffer[index].liesInInterior())
			} // if (fractalResultBuffer.fBuffer[index] != null)
		} // for index

		// second pass: construct histogram for ranked colours (containing values between 0 and 1)
		if (coloringParameters.fColorMapScaling == ColoringParameters.EColorMapScaling.kRankOrder) {

			if ((coloringParameters.fInteriorColoringMethod != ColoringParameters.EColoringMethod.kFixedColor) && (fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints > 0)) {
				// manual code optimisation: we copy the retained part of the histogram and sort it
				fractalIterationRangeInformation.fInteriorRankColoringHistogramLookupTable = new double[fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints];
				System.arraycopy(interiorRankColoringHistogram,0,fractalIterationRangeInformation.fInteriorRankColoringHistogramLookupTable,0,fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints);
				Arrays.parallelSort(fractalIterationRangeInformation.fInteriorRankColoringHistogramLookupTable);
			}

			if ((coloringParameters.fExteriorColoringMethod != ColoringParameters.EColoringMethod.kFixedColor) && (fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints > 0)) {
				// manual code optimisation: we copy the retained part of the histogram and sort it
				fractalIterationRangeInformation.fExteriorRankColoringHistogramLookupTable = new double[fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints];
				System.arraycopy(exteriorRankColoringHistogram,0,fractalIterationRangeInformation.fExteriorRankColoringHistogramLookupTable,0,fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints);
				Arrays.parallelSort(fractalIterationRangeInformation.fExteriorRankColoringHistogramLookupTable);
			}
		}
	}

	/**
	 * Applies the current colour map, drawing and smoothing techniques based on the specified fractal result buffer.
	 *
	 * @param fractalResultBuffer               the buffer containing the fractal iteration results
	 * @param fractalIterationRangeInformation  the precalculated iteration range information
	 * @return                                  an image containing the coloured fractal
	 */
	private BufferedImage colorFractal(IterationBuffer fractalResultBuffer, FractalIterationRangeInformation fractalIterationRangeInformation)
	{
		// prevent problems when in colour-cycling mode
		if ((fractalResultBuffer == null) || ((fractalResultBuffer != null) && (fractalResultBuffer.fBuffer == null))) {
			return null;
		}

		// 	third pass to determine colour-mapped iteration counts
		ColoringParameters coloringParameters = fIteratorController.getColoringParameters();

		// initialise rendering buffer
		BufferedImage fractalImageBuffer = new BufferedImage(fractalResultBuffer.fWidth,fractalResultBuffer.fHeight,BufferedImage.TYPE_INT_RGB);

		// we use the image's raster to speedup drawing (as opposed to setRGB() calls)
		WritableRaster imageRaster = fractalImageBuffer.getRaster();
		int[] imageBuffer = ((DataBufferInt) imageRaster.getDataBuffer()).getData();

		double maxObservedExponentialIterationCount = 0.0;
		if (fIteratorController.getFractalIterator() instanceof AConvergentFractalIterator) {
			maxObservedExponentialIterationCount = ((AConvergentFractalIterator) fIteratorController.getFractalIterator()).getMaxObservedExponentialIterationCount();
		}

		boolean useFixedNrOfIterations = fIteratorController.getFractalIterator().getUseFixedNrOfIterations();

		// third pass: draw all colour-mapped iteration counts to the rendering buffer
		for (int index = 0; index < (fractalResultBuffer.fWidth * fractalResultBuffer.fHeight); ++index) {

			// set the default colour to black
			Color color = Color.BLACK;

			IterationResult iterationResult = fractalResultBuffer.fBuffer[index];
			if (iterationResult != null) {

				// early check for fixed colouring
				if (iterationResult.liesInInterior() && (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kFixedColor)) {
					color = coloringParameters.fInteriorColor;
				}
				else if (!iterationResult.liesInInterior() && (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kFixedColor)) {
					color = coloringParameters.fExteriorColor;
				}
				else {
					// determine values based on selected colouring method
					double nrOfIterations = 0.0;
					double minNrOfIterations = fractalIterationRangeInformation.fExteriorMinNrOfIterations;
					double maxNrOfIterations = fractalIterationRangeInformation.fExteriorMaxNrOfIterations;
					if (iterationResult.liesInInterior()) {
						minNrOfIterations = fractalIterationRangeInformation.fInteriorMinNrOfIterations;
						maxNrOfIterations = fractalIterationRangeInformation.fInteriorMaxNrOfIterations;
					}

					if (!iterationResult.liesInInterior() && (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kDiscreteLevelSets)) {
						nrOfIterations = iterationResult.fNrOfIterations;
					}
					else if (!iterationResult.liesInInterior() && (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kSmoothNICLevelSets)) {
						nrOfIterations = iterationResult.fNormalisedIterationCount;
					}
					else if ((iterationResult.liesInInterior() && (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kSmoothEICLevelSets)) ||
									(!iterationResult.liesInInterior() && (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kSmoothEICLevelSets))) {
						nrOfIterations = iterationResult.fExponentialIterationCount;
					}
					else if (iterationResult.liesInInterior() && (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kSectorDecomposition)) {
						nrOfIterations = iterationResult.getSector(coloringParameters.fColorMapInteriorSectorDecompositionRange);
					}
					else if (!iterationResult.liesInInterior() && (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kSectorDecomposition)) {
						nrOfIterations = iterationResult.getSector(coloringParameters.fColorMapExteriorSectorDecompositionRange);
					}
					else if ((iterationResult.liesInInterior() && (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kRealComponent)) ||
									(!iterationResult.liesInInterior() && (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kRealComponent))) {
						nrOfIterations = iterationResult.fRealComponent;
					}
					else if ((iterationResult.liesInInterior() && (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kImaginaryComponent)) ||
									(!iterationResult.liesInInterior() && (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kImaginaryComponent))) {
						nrOfIterations = iterationResult.fImaginaryComponent;
					}
					else if ((iterationResult.liesInInterior() && (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kModulus)) ||
									(!iterationResult.liesInInterior() && (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kModulus))) {
						nrOfIterations = iterationResult.fModulus;
					}
					else if ((iterationResult.liesInInterior() && (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kAverageDistance)) ||
									(!iterationResult.liesInInterior() && (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kAverageDistance))) {
						nrOfIterations = iterationResult.fAverageDistance;
					}
					else if ((iterationResult.liesInInterior() && (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kAngle)) ||
									(!iterationResult.liesInInterior() && (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kAngle))) {
						nrOfIterations = iterationResult.fAngle;
					}
					else if ((iterationResult.liesInInterior() && (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kLyapunovExponent)) ||
									(!iterationResult.liesInInterior() && (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kLyapunovExponent))) {
						nrOfIterations = iterationResult.fLyapunovExponent;
					}
					else if ((iterationResult.liesInInterior() && (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kCurvature)) ||
									(!iterationResult.liesInInterior() && (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kCurvature))) {
						nrOfIterations = iterationResult.fCurvature;
					}
					else if ((iterationResult.liesInInterior() && (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kStriping)) ||
									(!iterationResult.liesInInterior() && (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kStriping))) {
						nrOfIterations = iterationResult.fStriping;
					}
					else if ((iterationResult.liesInInterior() && (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kMinimumGaussianIntegersDistance)) ||
									(!iterationResult.liesInInterior() && (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kMinimumGaussianIntegersDistance))) {
						nrOfIterations = iterationResult.fMinimumGaussianIntegersDistance;
					}
					else if ((iterationResult.liesInInterior() && (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kAverageGaussianIntegersDistance)) ||
									(!iterationResult.liesInInterior() && (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kAverageGaussianIntegersDistance))) {
						nrOfIterations = iterationResult.fAverageGaussianIntegersDistance;
					}
					else if ((iterationResult.liesInInterior() && (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kExteriorDistance)) ||
									(!iterationResult.liesInInterior() && (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kExteriorDistance))) {
						nrOfIterations = iterationResult.fExteriorDistance;
					}
					else if ((iterationResult.liesInInterior() && (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kOrbitTrapDisk)) ||
									(!iterationResult.liesInInterior() && (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kOrbitTrapDisk))) {
						nrOfIterations = iterationResult.fOrbitTrapDiskDistance;
					}
					else if ((iterationResult.liesInInterior() && (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kOrbitTrapCrossStalks)) ||
									(!iterationResult.liesInInterior() && (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kOrbitTrapCrossStalks))) {
						nrOfIterations = iterationResult.fOrbitTrapCrossStalksDistance;
					}
					else if ((iterationResult.liesInInterior() && (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kOrbitTrapSine)) ||
									(!iterationResult.liesInInterior() && (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kOrbitTrapSine))) {
						nrOfIterations = iterationResult.fOrbitTrapSineDistance;
					}
					else if ((iterationResult.liesInInterior() && (coloringParameters.fInteriorColoringMethod == ColoringParameters.EColoringMethod.kOrbitTrapTangens)) ||
									(!iterationResult.liesInInterior() && (coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kOrbitTrapTangens))) {
						nrOfIterations = iterationResult.fOrbitTrapTangensDistance;
					}
					else if (!iterationResult.liesInInterior() && ((coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kDiscreteRoots) ||
																													(coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kSmoothRoots))) {
						nrOfIterations = iterationResult.fRootIndex;
					}

					// bound the shown iterations (irrespective of the minimum and maximum observed)
					if (iterationResult.liesInInterior() ||
							(!iterationResult.liesInInterior() &&
							 (iterationResult.fNrOfIterations <= fractalIterationRangeInformation.fExteriorMaxNrOfIntegralIterations) &&
							  (!useFixedNrOfIterations &&
							   (iterationResult.fNrOfIterations >= coloringParameters.fLowIterationRange) &&
							   (iterationResult.fNrOfIterations <= coloringParameters.fHighIterationRange))) ||
							  useFixedNrOfIterations) {
						// apply colour map scaling (transform the argument and function by using the multipliers)
						double colorMapDiscreteColorRange = (double) coloringParameters.fColorMapDiscreteColorRange;
						if (coloringParameters.fColorMapScaling == ColoringParameters.EColorMapScaling.kLinear) {
							coloringParameters.fColorMapContinuousColorRange = coloringParameters.fColorMapScalingArgumentMultiplier * coloringParameters.fColorMapContinuousColorRange;
							colorMapDiscreteColorRange = coloringParameters.fColorMapScalingArgumentMultiplier * colorMapDiscreteColorRange;
							nrOfIterations = coloringParameters.fColorMapScalingArgumentMultiplier * nrOfIterations;
						}
						else if (coloringParameters.fColorMapScaling == ColoringParameters.EColorMapScaling.kLogarithmic) {
							coloringParameters.fColorMapContinuousColorRange = coloringParameters.fColorMapScalingFunctionMultiplier * Math.log(coloringParameters.fColorMapScalingArgumentMultiplier * coloringParameters.fColorMapContinuousColorRange);
							colorMapDiscreteColorRange = coloringParameters.fColorMapScalingFunctionMultiplier * Math.log(coloringParameters.fColorMapScalingArgumentMultiplier * colorMapDiscreteColorRange);
							nrOfIterations = coloringParameters.fColorMapScalingFunctionMultiplier * Math.log(coloringParameters.fColorMapScalingArgumentMultiplier * nrOfIterations);
							minNrOfIterations = Math.log(minNrOfIterations);
							maxNrOfIterations = Math.log(maxNrOfIterations);
						}
						else if (coloringParameters.fColorMapScaling == ColoringParameters.EColorMapScaling.kExponential) {
							coloringParameters.fColorMapContinuousColorRange = coloringParameters.fColorMapScalingFunctionMultiplier * Math.exp(coloringParameters.fColorMapScalingArgumentMultiplier * coloringParameters.fColorMapContinuousColorRange);
							colorMapDiscreteColorRange = coloringParameters.fColorMapScalingFunctionMultiplier * Math.exp(coloringParameters.fColorMapScalingArgumentMultiplier * colorMapDiscreteColorRange);
							nrOfIterations = coloringParameters.fColorMapScalingFunctionMultiplier * Math.exp(coloringParameters.fColorMapScalingArgumentMultiplier * nrOfIterations);
							minNrOfIterations = Math.exp(minNrOfIterations);
							maxNrOfIterations = Math.exp(maxNrOfIterations);
						}
						else if (coloringParameters.fColorMapScaling == ColoringParameters.EColorMapScaling.kSqrt) {
							coloringParameters.fColorMapContinuousColorRange = coloringParameters.fColorMapScalingFunctionMultiplier * Math.sqrt(coloringParameters.fColorMapScalingArgumentMultiplier * coloringParameters.fColorMapContinuousColorRange);
							colorMapDiscreteColorRange = coloringParameters.fColorMapScalingFunctionMultiplier * Math.sqrt(coloringParameters.fColorMapScalingArgumentMultiplier * colorMapDiscreteColorRange);
							nrOfIterations = coloringParameters.fColorMapScalingFunctionMultiplier * Math.sqrt(coloringParameters.fColorMapScalingArgumentMultiplier * nrOfIterations);
							minNrOfIterations = Math.sqrt(minNrOfIterations);
							maxNrOfIterations = Math.sqrt(maxNrOfIterations);
						}

						// determine default colorIndex
						double colorIndex = 0.0;

						if (coloringParameters.fColorMapScaling == ColoringParameters.EColorMapScaling.kRankOrder) {
							if (iterationResult.liesInInterior()) {
								// interior colouring
								if (fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints > 0) {
									int lookupIndex = Arrays.binarySearch(fractalIterationRangeInformation.fInteriorRankColoringHistogramLookupTable,nrOfIterations);
									if (lookupIndex >= 0) {
										// manual code optimisation: calculate the rank directly
										colorIndex = (double) lookupIndex / ((double) fractalIterationRangeInformation.fInteriorRankColoringHistogramNrOfPoints - 1.0);
									}
								}
							}
							else if (fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints > 0) {
								// exterior colouring
								int lookupIndex = Arrays.binarySearch(fractalIterationRangeInformation.fExteriorRankColoringHistogramLookupTable,nrOfIterations);
								if (lookupIndex >= 0) {
									// manual code optimisation: calculate the rank directly
									colorIndex = (double) lookupIndex / ((double) fractalIterationRangeInformation.fExteriorRankColoringHistogramNrOfPoints - 1.0);
								}
							}

							// restrict high iteration counts if necessary
							if (coloringParameters.fRankOrderRestrictHighIterationCountColors) {
								colorIndex = 1.0 + (1.0 / (Math.log(1.0 - colorIndex) - 1.0));
							}
						} // if (fColorMapScaling == EColorMapScaling.kRankOrder)
						else {
							// no rank order colour map scaling
							colorIndex = MathTools.clip((nrOfIterations - minNrOfIterations) / (maxNrOfIterations - minNrOfIterations),0.0,1.0);
							if (((Double) colorIndex).isNaN()) {
								colorIndex = 0.0;
							}
						} // if (fColorMapScaling != EColorMapScaling.kRankOrder)

						// apply colour map cycling
						if (coloringParameters.fColorMapColorOffset != 0.0) {
							colorIndex = MathTools.frac(colorIndex + coloringParameters.fColorMapColorOffset);
						}

						// apply colour map wrapping
						if (iterationResult.liesInInterior()) {
							// interior colouring
							if (coloringParameters.fInteriorColorMapWrappedAround) {
								if (colorIndex < 0.5) {
									colorIndex *= 2.0;
								}
								else {
									colorIndex = 2.0 * (1.0 - colorIndex);
								}
							}
						}
						else {
							// exterior colouring
							if (coloringParameters.fExteriorColorMapWrappedAround) {
								if (colorIndex < 0.5) {
									colorIndex *= 2.0;
								}
								else {
									colorIndex = 2.0 * (1.0 - colorIndex);
								}
							}
						}

						// apply colour map inversion
						if (iterationResult.liesInInterior()) {
							// interior colouring
							if (coloringParameters.fInteriorColorMapInverted) {
								colorIndex = 1.0 - colorIndex;
							}
						}
						else {
							// exterior colouring
							if (coloringParameters.fExteriorColorMapInverted) {
								colorIndex = 1.0 - colorIndex;
							}
						}

						if (coloringParameters.fColorMapRepeatMode) {
							colorIndex = MathTools.frac(colorIndex * coloringParameters.fColorMapColorRepetition);
						}

						if (coloringParameters.fColorMapUsage == ColoringParameters.EColorMapUsage.kFull) {
							// keep colorIndex as-is
						}
						else if (coloringParameters.fColorMapUsage == ColoringParameters.EColorMapUsage.kLimitedContinuous) {
							// restrict all colours to the selected ones
							double nrOfColors = (double) coloringParameters.fColorMapContinuousColorRange;
							if (colorIndex < 1.0) {
								if (nrOfColors > 1.0) {
									colorIndex = Math.floor(colorIndex / (1.0 / nrOfColors)) * (1.0 / (nrOfColors - 1.0));
								}
								else {
									colorIndex = 0.0;
								}
							}
						}
						else if (coloringParameters.fColorMapUsage == ColoringParameters.EColorMapUsage.kLimitedDiscrete) {
							// limit all colours by repeating them
							if (colorMapDiscreteColorRange > (maxNrOfIterations - minNrOfIterations)) {
								colorMapDiscreteColorRange = maxNrOfIterations - minNrOfIterations;
							}
							colorIndex = MathTools.clip(((nrOfIterations - minNrOfIterations) % colorMapDiscreteColorRange) / (colorMapDiscreteColorRange - 1.0),0.0,1.0);							
							if (((Double) colorIndex).isNaN()) {
								colorIndex = 0.0;
							}
						}

						// convert colour map index to a colour using the specified colour map
						JGradientColorMap gcm = coloringParameters.fExteriorGradientColorMap;

						// tiger stripes are not available for interior colouring
						boolean useTigerStripes = (!iterationResult.liesInInterior() && coloringParameters.fUseTigerStripes && (MathTools.isOdd((int) iterationResult.fNrOfIterations)));
						if (iterationResult.liesInInterior()) {
							gcm = coloringParameters.fInteriorGradientColorMap;
						}
						else if (useTigerStripes) {
							gcm = coloringParameters.fTigerGradientColorMap;
						}

						if (useTigerStripes && coloringParameters.fTigerUseFixedColor) {
							color = coloringParameters.fTigerStripeFixedColor;
						}
						else {
							color = gcm.interpolate(colorIndex);
						}

						// artificially brighten smooth root colours
						if ((fIteratorController.getFractalIterator() instanceof AConvergentFractalIterator) &&
								!iterationResult.liesInInterior() &&
								(coloringParameters.fExteriorColoringMethod == ColoringParameters.EColoringMethod.kSmoothRoots)) {
							// linearly scale all RGB components
							double fraction = (double) iterationResult.fExponentialIterationCount / maxObservedExponentialIterationCount;
							int red = (int) Math.floor(MathTools.clip((double) color.getRed() * fraction * coloringParameters.fBrightnessFactor,0.0,255.0));
							int green = (int) Math.floor(MathTools.clip((double) color.getGreen() * fraction * coloringParameters.fBrightnessFactor,0.0,255.0));
							int blue = (int) Math.floor(MathTools.clip((double) color.getBlue() * fraction * coloringParameters.fBrightnessFactor,0.0,255.0));
							int alpha = color.getAlpha();

							color = new Color(red,green,blue,alpha);
						}

					} // bound the shown iterations
				} // non-fixed interior or exterior colouring method
			} // if (iterationResult != null)

			// draw a pixel
			imageBuffer[index] = color.getRGB();
		} // for index

		imageRaster.setPixels(0,0,0,0,imageBuffer);

		if (coloringParameters.fUsePostProcessingFilters) {
			for (int filterIndex = 0; filterIndex < coloringParameters.fPostProcessingFilterChain.size(); ++filterIndex) {
				AFilter filter = coloringParameters.fPostProcessingFilterChain.getFilter(filterIndex); 
				fractalImageBuffer = filter.filter(fractalImageBuffer);
			}
		}

		return fractalImageBuffer;
	}

	/**
	 */
	private void renderSupplementalInformation()
	{
		// prevent unwanted changes when calculating
		if (fIteratorController.isBusy()) {
			return;
		}

		AFractalIterator fractalIterator = fIteratorController.getFractalIterator();
		AFractalIterator.EFractalType fractalType = fractalIterator.getFractalType();
		int screenWidth = fractalIterator.getScreenWidth();
		int screenHeight = fractalIterator.getScreenHeight();

		IterationResult iterationResult = null;

		// determine the viewport position and size
		Point	viewportPosition = fViewport.getViewPosition();
		int vpX1 = viewportPosition.x;
		int vpY1 = viewportPosition.y;
		Dimension	viewportExtentSize = fViewport.getExtentSize();
		int vpWidth = viewportExtentSize.width;
		int vpHeight = viewportExtentSize.height;
		int vpX2 = vpX1 + vpWidth - 1;
		int vpY2 = vpY1 + vpHeight - 1;

		// adjust used viewport settings in case the viewport area is larger than the fractal screen area
		if (vpWidth > screenWidth) {
			vpWidth = screenWidth;
			vpX2 = vpWidth;
		}
		if (vpHeight > screenHeight) {
			vpHeight = screenHeight;
			vpY2 = vpHeight;
		}

		Image rescaledMainFractalImage = null;
		final int kRescaledMainFractalXOffset = 20;
		final int kRescaledMainFractalYOffset = 50;
		int rescaledMainFractalWidth = kMainFractalOverviewDefaultLongestSide;
		int rescaledMainFractalHeight = kMainFractalOverviewDefaultLongestSide;

		// calculate the screen dimensions of the inset fractal
		double insetSizeFactor = (100.0 - (double) fInsetSizePercentage) / 100.0;
		fInsetWidth = (vpWidth - ((int) Math.round(insetSizeFactor * (double) vpWidth))) - kInsetEdgeOffset;
		fInsetHeight = (vpHeight - ((int) Math.round(insetSizeFactor * (double) vpHeight))) - kInsetEdgeOffset;
		if (fInsetWidth < kMinInsetWidth) {
			fInsetWidth = kMinInsetWidth;
		}
		if (fInsetHeight < kMinInsetHeight) {
			fInsetHeight = kMinInsetHeight;
		}

		ComplexNumber p1 = new ComplexNumber(fractalIterator.getP1());
		ComplexNumber p2 = new ComplexNumber(fractalIterator.getP2());
		ComplexNumber previousDeformedParameter = fractalIterator.getMainFractalOrbitStartingPoint();
		ComplexNumber deformedParameter = new ComplexNumber();

		fRenderBufferGraphics = (Graphics2D) fRenderBuffer.createGraphics();
		fRenderBufferGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		fRenderBufferGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		// capture the current main fractal's image
		if (fShowMainFractalOverview) {
			try {
				// determine the longest side of the screen bounds
				double ratio = (double) screenWidth / (double) screenHeight;
				if (ratio > 1.0) {
					// width is the longest side
					rescaledMainFractalHeight = (int) Math.round((double) rescaledMainFractalWidth / ratio);
				}
				else {
					// height is the longest side
					rescaledMainFractalWidth = (int) Math.round((double) rescaledMainFractalHeight * ratio);
				}

				// get a fast rescaled version of the main fractal
				rescaledMainFractalImage = fRenderBuffer.getScaledInstance(rescaledMainFractalWidth,rescaledMainFractalHeight,Image.SCALE_FAST);
			}
			catch (HeadlessException exc) {
				// ignore
			}
			catch (RasterFormatException exc) {
				// ignore
			}

			fRenderBufferGraphics.drawImage(rescaledMainFractalImage,vpX1 + kRescaledMainFractalXOffset,vpY2 - kRescaledMainFractalYOffset - rescaledMainFractalHeight,null);

			// draw a rectangle indicating the currently visible area
			int rescaledVPX1 = (int) Math.round((double) vpX1 * ((double) rescaledMainFractalWidth / (double) screenWidth));
			int rescaledVPY1 = (int) Math.round((double) vpY1 * ((double) rescaledMainFractalHeight / (double) screenHeight));
			int rescaledVPWidth = (int) Math.round((double) vpWidth * ((double) rescaledMainFractalWidth / (double) screenWidth));
			int rescaledVPHeight = (int) Math.round((double) vpHeight * ((double) rescaledMainFractalHeight / (double) screenHeight));
			fRenderBufferGraphics.setXORMode(Color.RED);
			fRenderBufferGraphics.drawRect(
				vpX1 + kRescaledMainFractalXOffset + rescaledVPX1,
				vpY2 - kRescaledMainFractalYOffset - rescaledMainFractalHeight + rescaledVPY1,
				rescaledVPWidth,rescaledVPHeight);
			fRenderBufferGraphics.drawRect(
				vpX1 + kRescaledMainFractalXOffset + rescaledVPX1 + 1,
				vpY2 - kRescaledMainFractalYOffset - rescaledMainFractalHeight + rescaledVPY1 + 1,
				rescaledVPWidth - 2,rescaledVPHeight - 2);
			fRenderBufferGraphics.setPaintMode();
	
			fRenderBufferGraphics.setColor(Color.BLACK);
			fRenderBufferGraphics.drawRect(vpX1 + kRescaledMainFractalXOffset,vpY2 - kRescaledMainFractalYOffset - rescaledMainFractalHeight,rescaledMainFractalWidth,rescaledMainFractalHeight);
		} // if (fShowMainFractalOverview)

		// overlay the selection rectangle if necessary
		if (fSelecting && (fSelectionAnchor != null) && (fSelectionExtent != null)) {
			Point2D.Double mouseSelectionAnchor = new Point2D.Double(fSelectionAnchor.fX,fSelectionAnchor.fY);
			Point2D.Double mouseSelectionExtent = new Point2D.Double(fSelectionExtent.fX,fSelectionExtent.fY);
			if (!fCentredZooming) {
				MathTools.forcePartialOrder(mouseSelectionAnchor,mouseSelectionExtent);
			}
			int mX1 = (int) mouseSelectionAnchor.getX();
			int mY1 = (int) mouseSelectionAnchor.getY();
			int mX2 = (int) mouseSelectionExtent.getX();
			int mY2 = (int) mouseSelectionExtent.getY();

			int zoomWidth = (int) Math.abs(fSelectionExtent.fX - fSelectionAnchor.fX);
			int zoomHeight = (int) Math.abs(fSelectionExtent.fY - fSelectionAnchor.fY); // because the screen's Y-axis points downwards
			int minimumZoomSize = kMinimumZoomSize;
			if (fCentredZooming) {
				zoomWidth = Math.abs(mX2 - mX1 - 1);
				zoomHeight = Math.abs(mY2 - mY1 - 1);
				mX1 = mX1 - zoomWidth;
				mY1 = mY1 - zoomHeight;
				mX2 = mX1 + ((zoomWidth + 1) * 2);
				mY2 = mY1 + ((zoomHeight + 1) * 2);
				minimumZoomSize /= 2;
			}
			boolean selectionOk = ((zoomWidth > minimumZoomSize) && (zoomHeight > minimumZoomSize));

			fRenderBufferGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
			if (selectionOk) {
				fRenderBufferGraphics.setColor(Color.BLUE.darker().darker());
			}
			else {
				fRenderBufferGraphics.setColor(Color.RED);
			}
			fRenderBufferGraphics.fillRect(mX1,mY1,mX2 - mX1 - 1,mY2 - mY1 - 1);

			fRenderBufferGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
			if (selectionOk) {
				fRenderBufferGraphics.setColor(Color.CYAN.brighter());
			}
			else {
				fRenderBufferGraphics.setColor(Color.RED.brighter());
			}
			fRenderBufferGraphics.drawRect(mX1,mY1,mX2 - mX1 - 1,mY2 - mY1 - 1);
			fRenderBufferGraphics.drawRect(mX1 + 1,mY1 + 1,mX2 - mX1 - 1 - 2,mY2 - mY1 - 1 - 2);
			
			int kHalfCornerSize = 3;
			fRenderBufferGraphics.fillRect(mX1 - kHalfCornerSize + 1,mY1 - kHalfCornerSize + 1,2 * kHalfCornerSize,2 * kHalfCornerSize);
			fRenderBufferGraphics.fillRect(mX2 - kHalfCornerSize - 1,mY1 - kHalfCornerSize + 1,2 * kHalfCornerSize,2 * kHalfCornerSize);
			fRenderBufferGraphics.fillRect(mX1 - kHalfCornerSize + 1,mY2 - kHalfCornerSize - 1,2 * kHalfCornerSize,2 * kHalfCornerSize);
			fRenderBufferGraphics.fillRect(mX2 - kHalfCornerSize - 1,mY2 - kHalfCornerSize - 1,2 * kHalfCornerSize,2 * kHalfCornerSize);
			fRenderBufferGraphics.drawLine((mX1 + mX2) / 2,mY1 - (kHalfCornerSize / 2),(mX1 + mX2) / 2,mY2 + (kHalfCornerSize / 2));
			fRenderBufferGraphics.drawLine(mX1 - (kHalfCornerSize / 2),(mY1 + mY2) / 2,mX2 + (kHalfCornerSize / 2),(mY1 + mY2) / 2);
		} // if (fSelecting && (fSelectionAnchor != null) && (fSelectionExtent != null))

		if (fShowInset) {
			boolean showInsetFractal = true;

			if (fractalType == AFractalIterator.EFractalType.kMainFractal) {
				if (isDualFractalSuppressed()) {
					FontMetrics fontMetrics = fRenderBufferGraphics.getFontMetrics();
					String dualFractalSuppressedStr = "(" + I18NL10N.translate("text.Fractal.DualFractalSuppressed") + ")";
					int strWidth =  fontMetrics.stringWidth(dualFractalSuppressedStr);
					int strHeight = fontMetrics.getHeight();
					final int kTextInsetSize = 5;
					int x = vpX2 - strWidth - kTextInsetSize;
					int y = vpY2 - strHeight;
					fRenderBufferGraphics.setColor(Color.RED);
					fRenderBufferGraphics.drawString(dualFractalSuppressedStr,x,y);
					showInsetFractal = false;
				}
			}

			if (showInsetFractal) {
				fInsetX = vpX2 - fInsetWidth - kInsetEdgeOffset;
				fInsetY = vpY2 - fInsetHeight - kInsetEdgeOffset;
				fRenderBufferGraphics.setColor(Color.WHITE);
				fRenderBufferGraphics.drawRect((int) fInsetX - 1,(int) fInsetY - 1,(int) fInsetWidth + 1,(int) fInsetHeight + 1);
				fRenderBufferGraphics.drawRect((int) fInsetX - 2,(int) fInsetY - 2,(int) fInsetWidth + 3,(int) fInsetHeight + 3);

				if (fractalType == AFractalIterator.EFractalType.kMainFractal) {
					// use the default dual parameter for the dual fractal in case the mouse is outside the screen area
					ComplexNumber dualParameter = fractalIterator.getDefaultDualParameter();

					// obtain the current complex parameter as the inset is the dual fractal
					try {
						Point m = getMousePosition();
						if (m != null) {
							dualParameter = fractalIterator.convertScreenLocationToComplexNumber(new ScreenLocation(m.x,m.y));
							if (fShowDeformedMainFractal) {
								deformedParameter = fractalIterator.convertScreenLocationToComplexNumber(new ScreenLocation(m.x,m.y));
								fractalIterator.setMainFractalOrbitStartingPoint(deformedParameter);
							}
							else {
								fractalIterator.setDualParameter(dualParameter);
							}
						}
					}
					catch (HeadlessException exc) {
						// ignore
					}

					// calculate fractal on the fly
					if (!fAutoZoomInset) {
						fractalIterator.setComplexBounds(fractalIterator.getDefaultP1(),fractalIterator.getDefaultP2());
					}
					IterationBuffer insetFractalResultBuffer = new IterationBuffer(fInsetWidth,fInsetHeight);
					for (int x = 0; x < fInsetWidth; ++x) {
						for (int y = 0; y < fInsetHeight; ++y) {
							int index = x + (y * fInsetWidth);
							if (fShowDeformedMainFractal) {
								insetFractalResultBuffer.fBuffer[index] = fractalIterator.iterateMainFractal(new ScreenLocation(x,y),false,fInsetWidth,fInsetHeight);
							}
							else {
								insetFractalResultBuffer.fBuffer[index] = fractalIterator.iterateDualFractal(new ScreenLocation(x,y),dualParameter,false,fInsetWidth,fInsetHeight);
							}
						}
					}
					// restore main fractal zooming parameters
					if (!fAutoZoomInset) {
						fractalIterator.setComplexBounds(p1,p2);
					}

					prepareFractalColoringInformation(insetFractalResultBuffer,fDualFractalIterationRangeInformation);
					fInsetFractalImageBuffer = colorFractal(insetFractalResultBuffer,fDualFractalIterationRangeInformation);

					if (fShowDeformedMainFractal) {
						fractalIterator.setMainFractalOrbitStartingPoint(previousDeformedParameter);
					}

					fInsetDirty = true;
				} // if (mainFractalType == FractalParameters.EFractalType.kMainFractal)
				else {
					// mainFractalType == FractalIterator.EFractalType.kDualFractal
					if (fInsetDirty) {
						if (!fAutoZoomInset) {
							fractalIterator.setComplexBounds(fractalIterator.getDefaultP1(),fractalIterator.getDefaultP2());
						}

						// calculate fractal on the fly
						if (!fAutoZoomInset) {
							fractalIterator.setComplexBounds(fractalIterator.getDefaultP1(),fractalIterator.getDefaultP2());
						}
						IterationBuffer insetFractalResultBuffer = new IterationBuffer(fInsetWidth,fInsetHeight);
						for (int x = 0; x < fInsetWidth; ++x) {
							for (int y = 0; y < fInsetHeight; ++y) {
								int index = x + (y * fInsetWidth);
								insetFractalResultBuffer.fBuffer[index] = fractalIterator.iterateMainFractal(new ScreenLocation(x,y),false,fInsetWidth,fInsetHeight);
							}
						}
						// restore main fractal zooming parameters
						if (!fAutoZoomInset) {
							fractalIterator.setComplexBounds(p1,p2);
						}

						prepareFractalColoringInformation(insetFractalResultBuffer,fDualFractalIterationRangeInformation);
						fInsetFractalImageBuffer = colorFractal(insetFractalResultBuffer,fDualFractalIterationRangeInformation);

						fInsetDirty = false;
					}
				}

				// colour the fractal and copy the fractal image buffer to the render buffer
				Graphics renderBufferGraphics = fRenderBuffer.createGraphics();
				renderBufferGraphics.drawImage(fInsetFractalImageBuffer,fInsetX,fInsetY,null);
				renderBufferGraphics.dispose();

				//  if shown, indicate the dual complex parameter on the main fractal
				if (fractalType == AFractalIterator.EFractalType.kDualFractal) {
					if (!fAutoZoomInset) {
						fractalIterator.setComplexBounds(fractalIterator.getDefaultP1(),fractalIterator.getDefaultP2());
					}
					ScreenLocation dualParameter = fractalIterator.convertComplexNumberToScreenLocation(fractalIterator.getDualParameter(),fInsetWidth,fInsetHeight);
					if (!fAutoZoomInset) {
						fractalIterator.setComplexBounds(p1,p2);
					}

					int diametre = (int) Math.round((fInsetWidth / 100.0) * 4.0);
					// preserve minimum diametre
					if (diametre < 5) {
						diametre = 5;
					}
					int jcx = fInsetX + ((int) dualParameter.fX - (int) Math.round(diametre / 2.0));
					int jcy = fInsetY + ((int) dualParameter.fY - (int) Math.round(diametre / 2.0));

					// check if the point lies in the inset
					if ((jcx >= fInsetX) && (jcx < (fInsetX + fInsetWidth)) && (jcy >= fInsetY) && (jcy < (fInsetY + fInsetHeight))) {
						fRenderBufferGraphics.setColor(Color.WHITE);
						fRenderBufferGraphics.fillOval(jcx,jcy,(int) diametre,(int) diametre);
						fRenderBufferGraphics.setColor(Color.BLACK);
						fRenderBufferGraphics.drawOval(jcx,jcy,(int) diametre,(int) diametre);
						fRenderBufferGraphics.drawOval(jcx + 1,jcy + 1,(int) (diametre - 2.0),(int) (diametre - 2.0));
					}
				}
			} // if (showInsetFractal)
		} // if (fShowInset)


		if (fShowAxes) {
			// draw X and Y axes in the complex plane for the main fractal
			ScreenLocation origin = fractalIterator.convertComplexNumberToScreenLocation(new ComplexNumber());
			fRenderBufferGraphics.setColor(Color.WHITE);
			if ((origin.fY >= vpY1) && (origin.fY <= vpY2)) {
				fRenderBufferGraphics.drawLine(vpX1,origin.fY,vpX2,origin.fY);
			}
			if ((origin.fX >= vpX1) && (origin.fX <= vpX2)) {
				fRenderBufferGraphics.drawLine(origin.fX,vpY1,origin.fX,vpY2);
			}

			// draw X and Y axes in the complex plane for the inset fractal
			if (fShowInset && !isDualFractalSuppressed()) {
				if (!fAutoZoomInset) {
					fractalIterator.setComplexBounds(fractalIterator.getDefaultP1(),fractalIterator.getDefaultP2());
				}
				origin = fractalIterator.convertComplexNumberToScreenLocation(new ComplexNumber(),fInsetWidth,fInsetHeight);
				fRenderBufferGraphics.setColor(Color.WHITE);
				if ((origin.fY >= 0) && (origin.fY <= fInsetHeight)) {
					fRenderBufferGraphics.drawLine(fInsetX,fInsetY + origin.fY,fInsetX + fInsetWidth - 1,fInsetY + origin.fY);
				}
				if ((origin.fX >= 0) && (origin.fX <= fInsetWidth)) {
					fRenderBufferGraphics.drawLine(fInsetX + origin.fX,fInsetY,fInsetX + origin.fX,fInsetY + fInsetHeight - 1);
				}
				if (!fAutoZoomInset) {
					fractalIterator.setComplexBounds(p1,p2);
				}
			}
		} // if (fShowAxes)

		if (fShowOverlayGrid) {
			// GUI specific
			final int kCenterDotRadius = 7;
			final int kTextInsetSize = 10;

			// determine orientation
			// grid lines are shown relative to the screen size of the fractal (and not just the viewport size)
			double gridDelta = 0;
			double ratio = (double) screenWidth / (double) screenHeight;
			if (ratio > 1.0) {
				// width is the longest side
				gridDelta = (double) screenWidth / (double) kMaxNrOfGridSpacesPerDimension;
			}
			else {
				// height is the longest side
				gridDelta = (double) screenHeight / (double) kMaxNrOfGridSpacesPerDimension;
			}

			int centerX = screenWidth / 2;
			int centerY = screenHeight / 2;

			fRenderBufferGraphics.setColor(Color.DARK_GRAY);
			for (int i = 0; i < kMaxNrOfGridSpacesPerDimension; ++i) {
				// draw horizontal grid lines
				int xLeft = (int) Math.round((double) centerX + ((gridDelta / 2.0) * (double) i));
				fRenderBufferGraphics.drawLine(xLeft,0,xLeft,screenHeight - 1);
				int xRight = (int) Math.round((double) centerX - ((gridDelta / 2.0) * (double) i));
				fRenderBufferGraphics.drawLine(xRight,0,xRight,screenHeight - 1);

				// draw vertical grid lines
				int yTop = (int) Math.round((double) centerY + ((gridDelta / 2.0) * (double) i));
				fRenderBufferGraphics.drawLine(0,yTop,screenWidth - 1,yTop);
				int yBottom = (int) Math.round((double) centerY - ((gridDelta / 2.0) * (double) i));
				fRenderBufferGraphics.drawLine(0,yBottom,screenWidth - 1,yBottom);
			} // for (int i = 0; i <= kMaxNrOfGridSpacesPerDimension; ++i)

			fRenderBufferGraphics.setColor(Color.LIGHT_GRAY);
			for (int i = 0; i < (kMaxNrOfGridSpacesPerDimension / 2); ++i) {
				// draw horizontal grid lines
				int xLeft = (int) Math.round((double) centerX + (gridDelta * (double) i));
				fRenderBufferGraphics.drawLine(xLeft,0,xLeft,screenHeight - 1);
				int xRight = (int) Math.round((double) centerX - (gridDelta * (double) i));
				fRenderBufferGraphics.drawLine(xRight,0,xRight,screenHeight - 1);

				// draw vertical grid lines
				int yTop = (int) Math.round((double) centerY + (gridDelta * (double) i));
				fRenderBufferGraphics.drawLine(0,yTop,screenWidth - 1,yTop);
				int yBottom = (int) Math.round((double) centerY - (gridDelta * (double) i));
				fRenderBufferGraphics.drawLine(0,yBottom,screenWidth - 1,yBottom);
			} // for (int i = 0; i <= kMaxNrOfGridSpacesPerDimension; ++i)

			// draw a cross and dot in the centre of the screen
			fRenderBufferGraphics.setColor(Color.WHITE);
			fRenderBufferGraphics.drawLine(centerX,0,centerX,screenHeight - 1);
			fRenderBufferGraphics.drawLine(0,centerY,screenWidth - 1,centerY);
			fRenderBufferGraphics.fillOval(centerX - kCenterDotRadius,centerY - kCenterDotRadius,2 * kCenterDotRadius,2 * kCenterDotRadius);

			// show size of the visible extent in the complex plane
			double realWidth = Math.abs(p2.realComponent() - p1.realComponent());
			double realHeight = Math.abs(p2.imaginaryComponent() - p1.imaginaryComponent());
			String visibleExtent = String.valueOf(realWidth) + " x " + String.valueOf(realHeight);
			FontMetrics fontMetrics = fRenderBufferGraphics.getFontMetrics();
			int textWidth = fontMetrics.stringWidth(visibleExtent);
			int textHeight = fontMetrics.getHeight();
			int locationX = centerX - (textWidth / 2) - kTextInsetSize;
			int locationY = centerY + textHeight + kTextInsetSize;
			int locationWidth = textWidth + (2 * kTextInsetSize);
			int locationHeight = textHeight + (2 * (kTextInsetSize / 2));

			fRenderBufferGraphics.setColor(Color.WHITE);
			fRenderBufferGraphics.fillRect(locationX,locationY,locationWidth,locationHeight);

			fRenderBufferGraphics.setColor(Color.BLACK);
			fRenderBufferGraphics.drawRect(locationX,locationY,locationWidth,locationHeight);
			fRenderBufferGraphics.drawString(visibleExtent,locationX + kTextInsetSize,locationY + textHeight);
		} // if (fShowOverlayGrid)
//*/

///*
		if (fShowOrbits || fShowOrbitAnalyses) {
			try {
				Point m = getMousePosition();
				if (m != null) {
					int mX = (int) m.getX();
					int mY = (int) m.getY();

					// obtain the current orbit
					if (fractalType == AFractalIterator.EFractalType.kMainFractal) {
						iterationResult = fractalIterator.iterateMainFractal(new ScreenLocation(mX,mY),true);
					}
					else {
						iterationResult = fractalIterator.iterateDualFractal(new ScreenLocation(mX,mY),fractalIterator.getDualParameter(),true);
					}

					if (iterationResult != null) {
						int nrOfIterations = (int) iterationResult.fNrOfIterations;
						if (nrOfIterations > fractalIterator.getMaxNrOfIterations()) {
							nrOfIterations = fractalIterator.getMaxNrOfIterations();
						}
						if ((nrOfIterations > 0) && (iterationResult.fScreenOrbit != null)) {

							// show the orbit's individual points
							double[] moduli = new double[nrOfIterations];
							double[] angles = new double[nrOfIterations];
							double maxModulus = 0.0;
							int prevX = iterationResult.fScreenOrbit[0].fX;
							int prevY = iterationResult.fScreenOrbit[0].fY;

							// determine the range of the orbit
							double zOrbitX1 = IterationResult.kInfinity;
							double zOrbitY1 = IterationResult.kInfinity;
							double zOrbitX2 = -IterationResult.kInfinity;
							double zOrbitY2 = -IterationResult.kInfinity;
							for (int iteration = 0; iteration < nrOfIterations; ++iteration) {
								if (iterationResult.fComplexOrbit[iteration].realComponent() < zOrbitX1) {
									zOrbitX1 = iterationResult.fComplexOrbit[iteration].realComponent();
								}
								if (iterationResult.fComplexOrbit[iteration].imaginaryComponent() < zOrbitY1) {
									zOrbitY1 = iterationResult.fComplexOrbit[iteration].imaginaryComponent();
								}
								if (iterationResult.fComplexOrbit[iteration].realComponent() > zOrbitX2) {
									zOrbitX2 = iterationResult.fComplexOrbit[iteration].realComponent();
								}
								if (iterationResult.fComplexOrbit[iteration].imaginaryComponent() > zOrbitY2) {
									zOrbitY2 = iterationResult.fComplexOrbit[iteration].imaginaryComponent();
								}
							}
							double zOrbitWidth = zOrbitX2 - zOrbitX1;
							double zOrbitHeight = zOrbitY2 - zOrbitY1;
							if (zOrbitWidth < 1) {
								zOrbitWidth = 1;
							}
							if (zOrbitHeight < 1) {
								zOrbitHeight = 1;
							}

							for (int iteration = 0; iteration < nrOfIterations; ++iteration) {
								double zX = iterationResult.fComplexOrbit[iteration].realComponent();
								double zY = iterationResult.fComplexOrbit[iteration].imaginaryComponent();
								int sX = iterationResult.fScreenOrbit[iteration].fX - (int) Math.round(kOrbitDiametre / 2.0);
								int sY = iterationResult.fScreenOrbit[iteration].fY - (int) Math.round(kOrbitDiametre / 2.0);

								if (fScaleOrbitsToScreen) {
									if (zOrbitWidth != 0.0) {
										double vpWidthScaled = kOrbitsScreenScale * ((double) vpWidth);
										double vpWidthScaledComplement = (1.0 - kOrbitsScreenScale) * ((double) vpWidth);
										sX = (int) Math.round((double) vpX1 + (vpWidthScaledComplement / 2.0) + ((zX - zOrbitX1) * (vpWidthScaled / zOrbitWidth)));
									}
									if (zOrbitHeight != 0.0) {
										// invert Y-axis
										double vpHeightScaled = kOrbitsScreenScale * ((double) vpHeight);
										double vpHeightScaledComplement = (1.0 - kOrbitsScreenScale) * ((double) vpHeight);
										sY = (int) Math.round((double) vpY2 - (vpHeightScaledComplement / 2.0) - ((zY - zOrbitY1) * (vpHeightScaled / zOrbitHeight)));
									}
								}

								moduli[iteration] = iterationResult.fComplexOrbit[iteration].modulus();
								if (moduli[iteration] > maxModulus) {
									maxModulus = moduli[iteration];
								}
								angles[iteration] = iterationResult.fComplexOrbit[iteration].argument();

								// check if a fixed-point attractor was reached
								if (moduli[iteration] == 0.0) {
									nrOfIterations = iteration;
									break;
								}

								if (fShowOrbits) {
									Stroke stroke = fRenderBufferGraphics.getStroke();
									fRenderBufferGraphics.setStroke(new BasicStroke(kOrbitPathWidth));

									// increase the orbit's points' colours from medium to bright yellow or green, depending on the orbit finality
									float intensity = 0.5f + ((float) iteration / (float) nrOfIterations) / 2.0f;
									if (iterationResult.liesInInterior()) {
										fRenderBufferGraphics.setColor(new Color(intensity,intensity,0.0f));
									}
									else {
										fRenderBufferGraphics.setColor(new Color(0.0f,intensity,0.0f));
									}

									// enlarge the orbit points in case no Y-coordinates are used
									double orbitResizeFactor = 1.0;
									if (fractalIterator instanceof MarkusLyapunovFractalIterator) {
										orbitResizeFactor = 3.0;
									}
									fRenderBufferGraphics.fillOval((int) Math.round(sX - ((kOrbitDiametre * orbitResizeFactor) / 2.0)),(int) Math.round(sY - ((kOrbitDiametre * orbitResizeFactor) / 2.0)),(int) Math.round(kOrbitDiametre * orbitResizeFactor),(int) Math.round(kOrbitDiametre * orbitResizeFactor));

									if (fShowOrbitPaths) {
										// increase the orbit's paths' colors from medium to bright white
										fRenderBufferGraphics.setColor(new Color(intensity,intensity,intensity));
										fRenderBufferGraphics.drawLine(prevX,prevY,sX,sY);
										prevX = sX;
										prevY = sY;
									}
									fRenderBufferGraphics.setStroke(stroke);
								}
							} // for (int iteration = 0; iteration < nrOfIterations; ++iteration)

							// show the panel containing the orbit analyses
							if (fShowOrbitAnalyses) {
								final int kOrbitPanelXOffset = kCurrentLocationOffset;
								final int kOrbitPanelYOffset = 100;
								int panelX1 = mX + kOrbitPanelXOffset;
								int panelY1 = mY + kOrbitPanelYOffset;
								int panelWidth = (int) (((double) fOrbitAnalysesPanelSizePercentage / 100.0) * vpWidth);
								int panelHeight = (int) (((double) fOrbitAnalysesPanelSizePercentage / 100.0) * vpHeight);
								int halfPanelHeight = panelHeight / 2;

								// snap the panel if the edge of the screen is reached
								if ((panelX1 + panelWidth + kOrbitAnalysesPanelOffset)  > vpX2) {
									panelX1 = vpX2 - panelWidth - kOrbitAnalysesPanelOffset;
								}
								if ((panelY1 + panelHeight + kOrbitAnalysesPanelOffset)  > vpY2) {
									panelY1 = vpY2 - panelHeight - kOrbitAnalysesPanelOffset;
								}

								// make the panel's background transparent
								Color colorWhite = Color.WHITE;
								colorWhite = new Color(colorWhite.getRed(),colorWhite.getGreen(),colorWhite.getBlue(),128);
								fRenderBufferGraphics.setColor(colorWhite);
								fRenderBufferGraphics.fillRect(panelX1,panelY1,panelWidth,panelHeight);

								// show the KDE PDF estimation for the number of iterations
								FunctionLookupTable iterationsPDF = fIteratorController.getIterationsPDF();
								if (iterationsPDF != null) {
									int nrOfPDFBins = iterationsPDF.fX.length;
									if (nrOfPDFBins > 0) {
										double maxPDFBin = MathTools.findMaximum(iterationsPDF.fY);
										int[] pdfGraphX = new int[nrOfPDFBins];
										int[] pdfGraphY = new int[nrOfPDFBins];
										int binScreenWidth = (int) ((1.0 / nrOfPDFBins) * panelWidth);
										for (int binNr = 0; binNr < nrOfPDFBins; ++binNr) {
											pdfGraphX[binNr] = panelX1 + (int) (((double) binNr / (double) nrOfPDFBins) * panelWidth);
											pdfGraphY[binNr] = (int) (((double) iterationsPDF.fY[binNr] / (double) maxPDFBin) * panelHeight);
											fRenderBufferGraphics.setColor(Color.CYAN);
											fRenderBufferGraphics.fillRect(
												pdfGraphX[binNr],panelY1 + panelHeight - 1 - pdfGraphY[binNr],
												binScreenWidth,pdfGraphY[binNr]);
										}
									}
								}

								// draw line for maxMod/4, maxMod/2 and 3maxMod/4
								fRenderBufferGraphics.setColor(Color.DARK_GRAY);
								int offsetHeight = halfPanelHeight;
								if (fractalIterator instanceof MarkusLyapunovFractalIterator) {
									offsetHeight = panelHeight;
								}
								fRenderBufferGraphics.drawLine(
									panelX1,panelY1 + (offsetHeight / 2) - (offsetHeight / 4),
									panelX1 + panelWidth - 1,panelY1 + (offsetHeight / 2) - (offsetHeight / 4));
								fRenderBufferGraphics.drawLine(
									panelX1,panelY1 + (offsetHeight / 2),
									panelX1 + panelWidth - 1,panelY1 + (offsetHeight / 2));
								fRenderBufferGraphics.drawLine(
									panelX1,panelY1 + (offsetHeight / 2) + (offsetHeight / 4),
									panelX1 + panelWidth - 1,panelY1 + (offsetHeight / 2) + (offsetHeight / 4));

								// draw lines for PI/2, PI and 3PI/2
								if (!(fractalIterator instanceof MarkusLyapunovFractalIterator)) {
									fRenderBufferGraphics.setColor(Color.LIGHT_GRAY);
									fRenderBufferGraphics.drawLine(
										panelX1,panelY1 + halfPanelHeight + (halfPanelHeight / 2) - (halfPanelHeight / 4),
										panelX1 + panelWidth - 1,panelY1 + halfPanelHeight + (halfPanelHeight / 2) - (halfPanelHeight / 4));
									fRenderBufferGraphics.drawLine(
										panelX1,panelY1 + halfPanelHeight + (halfPanelHeight / 2),
										panelX1 + panelWidth - 1,panelY1 + halfPanelHeight + (halfPanelHeight / 2));
									fRenderBufferGraphics.drawLine(
										panelX1,panelY1 + halfPanelHeight + (halfPanelHeight / 2) + (halfPanelHeight / 4),
										panelX1 + panelWidth - 1,panelY1 + halfPanelHeight + (halfPanelHeight / 2) + (halfPanelHeight / 4));
								}

								// if requested, bound the number of iterations shown
								if ((fMaxNrOfIterationsInOrbitAnalyses > 0) && (nrOfIterations > fMaxNrOfIterationsInOrbitAnalyses)) {
									nrOfIterations = fMaxNrOfIterationsInOrbitAnalyses;
								}

								// remove the last iterations as they dominate the figure
								int nrOfIterationsToShow = nrOfIterations;
								double maxModulusToShow = maxModulus;
								if (nrOfIterations > 10) {
									nrOfIterationsToShow -= kNrOfOrbitPointsToExcludeInPanelAnalysis;
									maxModulusToShow = 0.0;
									for (int iteration = 0; iteration < nrOfIterationsToShow; ++iteration) {
										if (moduli[iteration] > maxModulusToShow) {
											maxModulusToShow = moduli[iteration];
										}
									}
								}

								// determine orbit diametre and stroke width (allow autosizing)
								int orbitDiametre = (int) Math.round((double) panelWidth / (3.0 * (double) nrOfIterationsToShow));
								if (orbitDiametre < kMinOrbitDiametre) {
									orbitDiametre = kMinOrbitDiametre;
								}
								else if (orbitDiametre > kMaxOrbitDiametre) {
										orbitDiametre = kMaxOrbitDiametre;
								}

								float strokeWidth = orbitDiametre / 2.0f;
								if (strokeWidth < kMinStrokeWidth) {
									strokeWidth = kMinStrokeWidth;
								}
								else if (strokeWidth > kMaxStrokeWidth) {
									strokeWidth = kMaxStrokeWidth;
								}

								// calculate positions of points in the graphs
								int[] graphX = new int[nrOfIterationsToShow];
								int[] modulusGraphY = new int[nrOfIterationsToShow];
								int[] angleGraphY = new int[nrOfIterationsToShow];
								fRenderBufferGraphics.setColor(Color.RED);
								for (int iteration = 0; iteration < nrOfIterationsToShow; ++iteration) {
									graphX[iteration] = panelX1 + (int) (((double) iteration / (double) nrOfIterationsToShow) * panelWidth);

									// take the double-log to accentuate the orbit's behaviour (add 1 to avoid a negative logarithm)
									double factor = Math.log(1.0 + Math.log(1.0 + (double) moduli[iteration])) / Math.log(1.0 + Math.log(1.0 + (double) maxModulusToShow));
									if (fractalIterator instanceof MarkusLyapunovFractalIterator) {
										modulusGraphY[iteration] = panelY1 + panelHeight - (int) (factor * (halfPanelHeight * 2));
									}
									else {
										modulusGraphY[iteration] = panelY1 + halfPanelHeight - (int) (factor * halfPanelHeight);
									}

									// make the angle between 0 and 2PI
									if (angles[iteration] < 0.0) {
										angles[iteration] += (2.0 * Math.PI);
									}
									angleGraphY[iteration] = panelY1 + panelHeight - (int) (((double) angles[iteration] / (2.0 * Math.PI)) * halfPanelHeight);
								}

								// show the modulus and angle graphs in the upper and lower half of the panel, respectively
								Stroke stroke = fRenderBufferGraphics.getStroke();
								fRenderBufferGraphics.setStroke(new BasicStroke(strokeWidth,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
								fRenderBufferGraphics.drawPolyline(graphX,modulusGraphY,nrOfIterationsToShow);
								if (!(fractalIterator instanceof MarkusLyapunovFractalIterator)) {
									fRenderBufferGraphics.drawPolyline(graphX,angleGraphY,nrOfIterationsToShow);
								}
								fRenderBufferGraphics.setStroke(stroke);

								// draw the orbits
								if (iterationResult.liesInInterior()) {
									fRenderBufferGraphics.setColor(Color.YELLOW);
								}
								else {
									fRenderBufferGraphics.setColor(Color.GREEN);
								}
								for (int iteration = 0; iteration < nrOfIterationsToShow; ++iteration) {
									fRenderBufferGraphics.fillOval(graphX[iteration] - (orbitDiametre / 2),modulusGraphY[iteration] - (orbitDiametre / 2),orbitDiametre,orbitDiametre);
									if (!(fractalIterator instanceof MarkusLyapunovFractalIterator)) {
										fRenderBufferGraphics.fillOval(graphX[iteration] - (orbitDiametre / 2),angleGraphY[iteration] - (orbitDiametre / 2),orbitDiametre,orbitDiametre);
									}
								}

								// label graphs
								String modulusDesc = I18NL10N.translate("text.Fractal.OrbitModulusSequence");
								FontMetrics fontMetrics = fRenderBufferGraphics.getFontMetrics();
								int textWidth = fontMetrics.stringWidth(modulusDesc);
								int textHeight = fontMetrics.getHeight();
								final int kTextInsetSize = 10;
								fRenderBufferGraphics.setColor(Color.BLACK);
								fRenderBufferGraphics.drawString(
									modulusDesc,
									panelX1 + panelWidth - kTextInsetSize - textWidth,
									panelY1 + textHeight + kTextInsetSize);
								fRenderBufferGraphics.drawString("0.0",panelX1 + kTextInsetSize,panelY1 + offsetHeight - kTextInsetSize);
								fRenderBufferGraphics.drawString(String.valueOf(maxModulus / 4.0),panelX1 + kTextInsetSize,panelY1 + (offsetHeight / 2) + (offsetHeight / 4) - 1);
								fRenderBufferGraphics.drawString(String.valueOf(maxModulus / 2.0),panelX1 + kTextInsetSize,panelY1 + (offsetHeight / 2) - 1);
								fRenderBufferGraphics.drawString(String.valueOf((3.0 / 4.0) * maxModulus),panelX1 + kTextInsetSize,panelY1 + (offsetHeight / 2) - (offsetHeight / 4) - 1);
								fRenderBufferGraphics.drawString(String.valueOf(maxModulus),panelX1 + kTextInsetSize,panelY1 + textHeight);

								if (!(fractalIterator instanceof MarkusLyapunovFractalIterator)) {
									String angleDesc = I18NL10N.translate("text.Fractal.OrbitAngleSequence");
									textWidth = fontMetrics.stringWidth(angleDesc);
									fRenderBufferGraphics.drawString(
										angleDesc,
										panelX1 + panelWidth - kTextInsetSize - textWidth,
										panelY1 + halfPanelHeight + textHeight + kTextInsetSize);
									fRenderBufferGraphics.drawString("0",panelX1 + kTextInsetSize,panelY1 + panelHeight - kTextInsetSize);
									fRenderBufferGraphics.drawString("PI/2",panelX1 + kTextInsetSize,panelY1 + halfPanelHeight + (halfPanelHeight / 2) + (halfPanelHeight / 4) - 1);
									fRenderBufferGraphics.drawString("PI",panelX1 + kTextInsetSize,panelY1 + panelHeight - (halfPanelHeight / 2) - 1);
									fRenderBufferGraphics.drawString("3PI/2",panelX1 + kTextInsetSize,panelY1 + halfPanelHeight + (halfPanelHeight / 2) - (halfPanelHeight / 4) - 1);
									fRenderBufferGraphics.drawString("2PI",panelX1 + kTextInsetSize,panelY1 + panelHeight - halfPanelHeight + textHeight);
								}

								// draw the panel's border and division
								fRenderBufferGraphics.drawRect(panelX1 - 1,panelY1 - 1,panelWidth + 1,panelHeight + 1);
								fRenderBufferGraphics.drawRect(panelX1 - 2,panelY1 - 2,panelWidth + 3,panelHeight + 3);
								if (!(fractalIterator instanceof MarkusLyapunovFractalIterator)) {
									fRenderBufferGraphics.drawLine(panelX1 - 1,panelY1 + halfPanelHeight - 1,panelX1 + panelWidth,panelY1 + halfPanelHeight - 1);
									fRenderBufferGraphics.drawLine(panelX1 - 1,panelY1 + halfPanelHeight,panelX1 + panelWidth,panelY1 + halfPanelHeight);
								}
							}
						}
					}
				}
			}
			catch (HeadlessException exc) {
				// ignore
			}
		} // if (fShowOrbits || fShowOrbitAnalyses)

		if (fShowMagnifyingGlass) {
			// drawing the magnifying glass here, so that the selection rectangle is also shown correctly
			try {
				Point m = getMousePosition();
				if (m != null) {
					int mX = (int) m.getX();
					int mY = (int) m.getY();

					int mgX1 = mX - (fMagnifyingGlassSize / 2);
					int mgY1 = mY - (fMagnifyingGlassSize / 2);
					if (fShowMagnifyingGlass) {
						int mgrX1 = mX - (fMagnifyingGlassRegion / 2);
						int mgrY1 = mY - (fMagnifyingGlassRegion / 2);
						BufferedImage subImage = fRenderBuffer.getSubimage(mgrX1,mgrY1,fMagnifyingGlassRegion,fMagnifyingGlassRegion);

						Image scaledSubImage = subImage.getScaledInstance(fMagnifyingGlassSize,fMagnifyingGlassSize,Image.SCALE_AREA_AVERAGING);
						fRenderBufferGraphics.drawImage(scaledSubImage,mgX1,mgY1,null);

						fRenderBufferGraphics.setColor(Color.BLACK);
						fRenderBufferGraphics.drawRect(mgX1,mgY1,fMagnifyingGlassSize,fMagnifyingGlassSize);
					}
				}
			}
			catch (HeadlessException exc) {
				// ignore
			}
			catch (RasterFormatException exc) {
				// ignore
			}
		} // if (fShowMagnifyingGlass)

		if (fShowZoomInformation) {
			String fractalDesc = I18NL10N.translate("text.Fractal.Fractal",fractalIterator.getFamilyName());
			String lowerLeftDesc = I18NL10N.translate("text.Fractal.LowerLeft") + ": " + fractalIterator.getP1();
			String upperRightDesc = I18NL10N.translate("text.Fractal.UpperRight") + ": " + fractalIterator.getP2();
			FontMetrics fontMetrics = fRenderBufferGraphics.getFontMetrics();
			int textWidth = (int) Math.max(fontMetrics.stringWidth(lowerLeftDesc),fontMetrics.stringWidth(upperRightDesc));
			textWidth = (int) Math.max(textWidth,fontMetrics.stringWidth(fractalDesc));
			int textHeight = fontMetrics.getHeight();
			int textDescent = fontMetrics.getMaxDescent();
			final int kLineHeight = textHeight - textDescent - 1;
			final int kPanelOffset = 20;
			final int kTextOffset = 10;

			String zoomLevelDesc = I18NL10N.translate("text.Fractal.ZoomLevel") + " (" + String.valueOf(fZoomStack.getZoomLevel()) + "): ";
			String zoomLevelTimes = I18NL10N.translate("text.Fractal.ZoomLevelTimes");
			long zoomLevel = fractalIterator.getCurrentZoomLevel();
			boolean zoomUnstable = false;
			if (zoomLevel <= 1) {
				zoomLevelDesc += I18NL10N.translate("text.Fractal.ZoomLevelDefault");
			}
			else {
				NumberFormat nf = new DecimalFormat("###,###,###,###,###,###");
				zoomLevelDesc += (nf.format(zoomLevel) + " " + zoomLevelTimes);

				String zoomLevelOrderDesc = "10" + String.valueOf(Math.round(Math.log10(zoomLevel)));
				if (zoomLevel >= 1000000000000000L) {
					zoomLevelOrderDesc += (", " + I18NL10N.translate("text.Fractal.ZoomLevelQuadrillions"));
				}
				else if (zoomLevel >= 1000000000000L) {
					zoomLevelOrderDesc += (", " + I18NL10N.translate("text.Fractal.ZoomLevelTrillions"));
				}
				else if (zoomLevel >= 1000000000L) {
					zoomLevelOrderDesc += (", " + I18NL10N.translate("text.Fractal.ZoomLevelBillions"));
				}
				else if (zoomLevel >= 1000000L) {
					zoomLevelOrderDesc += (", " + I18NL10N.translate("text.Fractal.ZoomLevelMillions"));
				}
				else if (zoomLevel >= 1000L) {
					zoomLevelOrderDesc += (", " + I18NL10N.translate("text.Fractal.ZoomLevelThousands"));
				}
				else if (zoomLevel >= 100L) {
					zoomLevelOrderDesc += (", " + I18NL10N.translate("text.Fractal.ZoomLevelHundreds"));
				}
				else if (zoomLevel >= 10L) {
					zoomLevelOrderDesc += (", " + I18NL10N.translate("text.Fractal.ZoomLevelTens"));
				}
				else if (zoomLevel >= 1L) {
					zoomLevelOrderDesc += (", " + I18NL10N.translate("text.Fractal.ZoomLevelOnes"));
				}

				if (zoomLevelOrderDesc != "") {
					zoomLevelDesc += " (" + zoomLevelOrderDesc + ")";
				}

				// based on a double's precision
				if (zoomLevel > 10000000000000L) {
					zoomUnstable = true;
				}
			}
			textWidth = (int) Math.max(textWidth,fontMetrics.stringWidth(zoomLevelDesc));

			fRenderBufferGraphics.setColor(Color.WHITE);
			fRenderBufferGraphics.fillRect(
				vpX1 + kPanelOffset,
				vpY1 + kPanelOffset,
				textWidth + kTextOffset + kTextOffset,
				kTextOffset + (4 * (kTextOffset + kLineHeight)) + textDescent);
			fRenderBufferGraphics.setColor(Color.BLACK);
			fRenderBufferGraphics.drawRect(
				vpX1 + kPanelOffset,
				vpY1 + kPanelOffset,
				textWidth + kTextOffset + kTextOffset,
				kTextOffset + (4 * (kTextOffset + kLineHeight)) + textDescent);
			fRenderBufferGraphics.drawString(
				fractalDesc,
				vpX1 + kPanelOffset + kTextOffset,
				vpY1 + kPanelOffset + (kTextOffset + kLineHeight));
			fRenderBufferGraphics.drawString(
				lowerLeftDesc,
				vpX1 + kPanelOffset + kTextOffset,
				vpY1 + kPanelOffset + (2 * (kTextOffset + kLineHeight)));
			fRenderBufferGraphics.drawString(
				upperRightDesc,
				vpX1 + kPanelOffset + kTextOffset,
				vpY1 + kPanelOffset + (3 * (kTextOffset + kLineHeight)));

			if (zoomUnstable) {
				fRenderBufferGraphics.setColor(Color.RED);
			}
			if (zoomLevel <= 1) {
				fRenderBufferGraphics.drawString(
					zoomLevelDesc,
					vpX1 + kPanelOffset + kTextOffset,
					vpY1 + kPanelOffset + (4 * (kTextOffset + kLineHeight)));
			}
			else {
				AttributedString attributedZoomLevelDesc = new AttributedString(zoomLevelDesc);
				int exponentPos = zoomLevelDesc.indexOf(zoomLevelTimes + " (10") + zoomLevelTimes.length() + 4;
				int exponentLength = zoomLevelDesc.indexOf(", ") - exponentPos;
				attributedZoomLevelDesc.addAttribute(TextAttribute.SUPERSCRIPT,TextAttribute.SUPERSCRIPT_SUPER,exponentPos,exponentPos + exponentLength);
				fRenderBufferGraphics.drawString(
					attributedZoomLevelDesc.getIterator(),
					vpX1 + kPanelOffset + kTextOffset,
					vpY1 + kPanelOffset + (4 * (kTextOffset + kLineHeight)));
			}
		} // if (fShowZoomInformation)

		// indicate whether or not the Y-axis is inverted
		if (fractalIterator.getInvertYAxis()) {
			FontMetrics fontMetrics = fRenderBufferGraphics.getFontMetrics();
			String yAxisInvertedStr = "(" + I18NL10N.translate("text.Fractal.InvertedYAxis") + ")";
			int strWidth =  fontMetrics.stringWidth(yAxisInvertedStr);
			int strHeight = fontMetrics.getHeight();
			final int kTextInsetSize = 5;
			int x = vpX2 - strWidth - kTextInsetSize;
			int y = vpY1 + strHeight + kTextInsetSize;
			fRenderBufferGraphics.setColor(Color.RED);
			fRenderBufferGraphics.drawString(yAxisInvertedStr,x,y);
		}

		// indicate the starting point for orbit calculations of the main fractal
		ComplexNumber z0 = fractalIterator.getMainFractalOrbitStartingPoint();
		if (((fractalType == AFractalIterator.EFractalType.kMainFractal) && ((z0.realComponent() != 0.0) || (z0.imaginaryComponent() != 0.0))) ||
			(fShowDeformedMainFractal && fShowInset)) {
			if (fShowDeformedMainFractal) {
				z0 = deformedParameter;
			}
			FontMetrics fontMetrics = fRenderBufferGraphics.getFontMetrics();
			String z0Str = "z0 = " + z0;
			int strHeight = fontMetrics.getHeight();
			final int kTextInsetSize = 5;
			int x = vpX1 + kTextInsetSize;
			int y = vpY2 - strHeight;
			fRenderBufferGraphics.setColor(Color.RED);
			fRenderBufferGraphics.drawString(z0Str,x,y);
		}

		if (fShowCurrentLocation) {
			try {
				Point m = getMousePosition();
				if (m != null) {
					int mX = (int) m.getX();
					int mY = (int) m.getY();

					// draw a window-wide cross at the current location
					fRenderBufferGraphics.setColor(Color.YELLOW);
					fRenderBufferGraphics.drawLine(mX,vpY1,mX,vpY2);
					fRenderBufferGraphics.drawLine(vpX1,mY,vpX2,mY);

					// show the location in the complex plane
					ComplexNumber location = fractalIterator.convertScreenLocationToComplexNumber(new ScreenLocation(mX,mY));
					String locationStr = StringTools.convertComplexNumberToString(location,MathTools.kNrOfDoubleDecimals);

					// optionally indicate the number of iterations
					if ((fShowOrbits || fShowOrbitAnalyses) && (iterationResult != null)) {
						if (fractalIterator instanceof MarkusLyapunovFractalIterator) {						
							if (iterationResult.liesInInterior()) {
								locationStr += " (" + I18NL10N.translate("text.Fractal.ChaoticSequence") + ")";
							}
							else {
								locationStr += " (" + I18NL10N.translate("text.Fractal.StableSequence") + ")";
							}
						}
						else {
							if (iterationResult.liesInInterior()) {
								locationStr += " (" + I18NL10N.translate("text.Fractal.BoundOrbit") + ")";
							}
							else {
								locationStr += " (" + String.valueOf((int) iterationResult.fNrOfIterations) + " " + I18NL10N.translate("text.Fractal.Iterations") + ")";
							}
						}
					}
					FontMetrics fontMetrics = fRenderBufferGraphics.getFontMetrics();
					int locationStrWidth =  fontMetrics.stringWidth(locationStr);
					int locationStrHeight = fontMetrics.getHeight();
					final int kTextInsetSize = 5;
					int locationX = mX + kCurrentLocationOffset;
					int locationY = mY - kCurrentLocationOffset - locationStrHeight - (2 * kTextInsetSize);
					int locationWidth = locationStrWidth + (2 * kTextInsetSize); 
					int locationHeight = locationStrHeight + (2 * kTextInsetSize); 

					// adjust the label's location to the top of the magnifying glass when shown
					if (fShowMagnifyingGlass) {
						int mgX1 = mX - (fMagnifyingGlassSize / 2);
						int mgY1 = mY - (fMagnifyingGlassSize / 2);
						locationX = mgX1;
						locationY = mgY1 - kCurrentLocationOffset - locationStrHeight - (2 * kTextInsetSize);
					}

					// determine if the edge of the screen is reached
					if ((locationX - kCurrentLocationOffset) < vpX1) {
						locationX = vpX1 + kCurrentLocationOffset;
					}
					if ((locationY - kCurrentLocationOffset) < vpY1) {
						locationY = vpY1 + kCurrentLocationOffset;
					}
					if ((locationX + locationWidth + kCurrentLocationOffset) > vpX2) {
						locationX = vpX2 - locationWidth - kCurrentLocationOffset;
					}
					if ((locationY + locationHeight + kCurrentLocationOffset) < vpY1) {
						locationY = vpY1 - locationHeight - kCurrentLocationOffset;
					}

					fRenderBufferGraphics.setColor(Color.WHITE);
					fRenderBufferGraphics.fillRect(locationX,locationY,locationWidth,locationHeight);

					fRenderBufferGraphics.setColor(Color.BLACK);
					fRenderBufferGraphics.drawRect(locationX,locationY,locationWidth,locationHeight);
					fRenderBufferGraphics.drawString(locationStr,locationX + kTextInsetSize,locationY + locationStrHeight);
				}
			}
			catch (HeadlessException exc) {
				// ignore
			}
		} // if (fShowCurrentLocation)

		// draw a black rectangle around the fractal screen
		fRenderBufferGraphics.setColor(Color.BLACK);
		fRenderBufferGraphics.drawRect(0,0,screenWidth - 1,screenHeight - 1);

		fRenderBufferGraphics.dispose();
	}

	/*****************
	 * INNER CLASSES *
	 *****************/

	/**
	 * @author  Sven Maerivoet
	 * @version 14/01/2015
	 */
	private final class FractalIterationRangeInformation
	{
		// public datastructures
		public double fInteriorMinNrOfIterations;
		public double fInteriorMaxNrOfIterations;
		public double fExteriorMinNrOfIterations;
		public double fExteriorMaxNrOfIterations;
		public double fExteriorMaxNrOfIntegralIterations;
		public double[] fInteriorRankColoringHistogramLookupTable;
		public int fInteriorRankColoringHistogramNrOfPoints;
		public double[] fExteriorRankColoringHistogramLookupTable;
		public int fExteriorRankColoringHistogramNrOfPoints;
	}
}
