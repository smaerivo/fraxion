// ---------------------------------------
// Filename      : ColoringParameters.java
// Author        : Sven Maerivoet
// Last modified : 01/01/2015
// Target        : Java VM (1.8)
// ---------------------------------------

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

package org.sm.fraxion.fractals.util;

import java.awt.*;
import org.sm.fraxion.gui.filters.*;
import org.sm.smtools.exceptions.*;
import org.sm.smtools.swing.util.*;
import org.sm.smtools.util.*;

/**
 * The <CODE>ColoringParameters</CODE> class provides a container for holding a fractal's colouring parameters.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 * 
 * @author  Sven Maerivoet
 * @version 01/01/2015
 */
public final class ColoringParameters
{
	/**
	 * The different types of colourings.
	 */
	public static enum EColoringMethod {
		kFixedColor,
		kDiscreteLevelSets,
		kSmoothNICLevelSets,
		kSmoothEICLevelSets,
		kSectorDecomposition,
		kRealComponent,
		kImaginaryComponent,
		kModulus,
		kAngle,
		kMaxModulus,
		kTotalDistance,
		kAverageDistance,
		kTotalAngle,
		kLyapunovExponent,
		kCurvature,
		kStriping,
		kMinimumGaussianIntegersDistance,
		kAverageGaussianIntegersDistance,
		kDiscreteRoots,
		kSmoothRoots};

	/**
	 * The different types of colour map scalings.
	 */
	public static enum EColorMapScaling {kLinear, kLogarithmic, kExponential, kSqrt, kRankOrder};

	/**
	 * The different types of colour map usages.
	 */
	public static enum EColorMapUsage {kFull, kLimitedContinuous, kLimitedDiscrete};

	// public datastructures
	public JGradientColorMap fInteriorGradientColorMap;
	public JGradientColorMap fExteriorGradientColorMap;
	public boolean fInteriorColorMapInverted;
	public boolean fExteriorColorMapInverted;
	public boolean fInteriorColorMapWrappedAround;
	public boolean fExteriorColorMapWrappedAround;
	public boolean fCalculateAdvancedColoring;
	public boolean fUseTigerStripes;
	public JGradientColorMap fTigerGradientColorMap;
	public boolean fTigerUseFixedColor;
	public Color fTigerStripeFixedColor;
	public Color fInteriorColor;
	public Color fExteriorColor;
	public EColoringMethod fInteriorColoringMethod;
	public EColoringMethod fExteriorColoringMethod;
	public int fColorMapInteriorSectorDecompositionRange;
	public int fColorMapExteriorSectorDecompositionRange;
	public EColorMapScaling fColorMapScaling;
	public double fColorMapScalingFunctionMultiplier;
	public double fColorMapScalingArgumentMultiplier;
	public boolean fRankOrderRestrictHighIterationCountColors;
	public boolean fColorMapRepeatMode;
	public double fColorMapColorRepetition;
	public double fColorMapColorOffset;
	public EColorMapUsage fColorMapUsage;
	public double fColorMapContinuousColorRange;
	public int fColorMapDiscreteColorRange;
	public int fLowIterationRange;
	public int fHighIterationRange;
	public double fBrightnessFactor;
	public boolean fLockAspectRatio;
	public boolean fUsePostProcessingFilters;
	public FilterChain fPostProcessingFilterChain;

	/******************
	 * PUBLIC METHODS *
	 ******************/
	
	/**
	 * Loads the fractal colouring information from a file.
	 *
	 * @param  tfp                 a reference to the file parser
	 * @throws FileParseException  in case a parse error occurs
	 */
	public void load(TextFileParser tfp) throws FileParseException
	{
		fInteriorGradientColorMap = new JGradientColorMap(JGradientColorMap.EColorMap.valueOf(tfp.getNextString()));
		if (fInteriorGradientColorMap.getColorMap() == JGradientColorMap.EColorMap.kCustom) {
			fInteriorGradientColorMap.loadCustomColorMapComponents(tfp);
		}
		fExteriorGradientColorMap = new JGradientColorMap(JGradientColorMap.EColorMap.valueOf(tfp.getNextString()));
		if (fExteriorGradientColorMap.getColorMap() == JGradientColorMap.EColorMap.kCustom) {
			fExteriorGradientColorMap.loadCustomColorMapComponents(tfp);
		}
		fInteriorColorMapInverted = tfp.getNextBoolean();
		fExteriorColorMapInverted = tfp.getNextBoolean();
		fInteriorColorMapWrappedAround = tfp.getNextBoolean();
		fExteriorColorMapWrappedAround = tfp.getNextBoolean();
		fCalculateAdvancedColoring = tfp.getNextBoolean();
		fUseTigerStripes = tfp.getNextBoolean();
		fTigerGradientColorMap = new JGradientColorMap(JGradientColorMap.EColorMap.valueOf(tfp.getNextString()));
		if (fTigerGradientColorMap.getColorMap() == JGradientColorMap.EColorMap.kCustom) {
			fTigerGradientColorMap.loadCustomColorMapComponents(tfp);
		}
		fTigerUseFixedColor = tfp.getNextBoolean();
		fTigerStripeFixedColor = new Color(tfp.getNextInteger());
		fInteriorColor = new Color(tfp.getNextInteger());
		fExteriorColor = new Color(tfp.getNextInteger());
		fInteriorColoringMethod = EColoringMethod.valueOf(tfp.getNextString());
		fExteriorColoringMethod = EColoringMethod.valueOf(tfp.getNextString());
		fColorMapInteriorSectorDecompositionRange = tfp.getNextInteger();
		fColorMapExteriorSectorDecompositionRange = tfp.getNextInteger();
		fColorMapScaling = EColorMapScaling.valueOf(tfp.getNextString());
		fColorMapScalingFunctionMultiplier = tfp.getNextDouble();
		fColorMapScalingArgumentMultiplier = tfp.getNextDouble();
		fRankOrderRestrictHighIterationCountColors = tfp.getNextBoolean();
		fColorMapRepeatMode = tfp.getNextBoolean();
		fColorMapColorRepetition = tfp.getNextDouble();
		fColorMapColorOffset = tfp.getNextDouble();
		fColorMapUsage = EColorMapUsage.valueOf(tfp.getNextString());
		fColorMapContinuousColorRange = tfp.getNextDouble();
		fColorMapDiscreteColorRange = tfp.getNextInteger();
		fLowIterationRange = tfp.getNextInteger();
		fHighIterationRange = tfp.getNextInteger();
		fBrightnessFactor = tfp.getNextDouble();
		fLockAspectRatio = tfp.getNextBoolean();
		fUsePostProcessingFilters = tfp.getNextBoolean();
		fPostProcessingFilterChain.load(tfp);
	}

	/**
	 * Saves the current fractal colouring parameters to a file.
	 * 
	 * @param  tfw                 a reference to the file writer
	 * @throws FileWriteException  in case a write error occurs
	 */
	public void save(TextFileWriter tfw) throws FileWriteException
	{
		JGradientColorMap.EColorMap interiorColorMap = fInteriorGradientColorMap.getColorMap();
		tfw.writeString(interiorColorMap.toString());
		tfw.writeLn();
		if (interiorColorMap == JGradientColorMap.EColorMap.kCustom) {
			fInteriorGradientColorMap.saveCustomColorMapComponents(tfw);
		}

		JGradientColorMap.EColorMap exteriorColorMap = fExteriorGradientColorMap.getColorMap();
		tfw.writeString(fExteriorGradientColorMap.getColorMap().toString());
		tfw.writeLn();
		if (exteriorColorMap == JGradientColorMap.EColorMap.kCustom) {
			fExteriorGradientColorMap.saveCustomColorMapComponents(tfw);
		}

		tfw.writeBoolean(fInteriorColorMapInverted);
		tfw.writeLn();

		tfw.writeBoolean(fExteriorColorMapInverted);
		tfw.writeLn();

		tfw.writeBoolean(fInteriorColorMapWrappedAround);
		tfw.writeLn();

		tfw.writeBoolean(fExteriorColorMapWrappedAround);
		tfw.writeLn();

		tfw.writeBoolean(fCalculateAdvancedColoring);
		tfw.writeLn();

		tfw.writeBoolean(fUseTigerStripes);
		tfw.writeLn();

		JGradientColorMap.EColorMap tigerColorMap = fTigerGradientColorMap.getColorMap();
		tfw.writeString(fTigerGradientColorMap.getColorMap().toString());
		tfw.writeLn();
		if (tigerColorMap == JGradientColorMap.EColorMap.kCustom) {
			fTigerGradientColorMap.saveCustomColorMapComponents(tfw);
		}

		tfw.writeBoolean(fTigerUseFixedColor);
		tfw.writeLn();

		tfw.writeInteger(fTigerStripeFixedColor.getRGB());
		tfw.writeLn();

		tfw.writeInteger(fInteriorColor.getRGB());
		tfw.writeLn();

		tfw.writeInteger(fExteriorColor.getRGB());
		tfw.writeLn();

		tfw.writeString(fInteriorColoringMethod.toString());
		tfw.writeLn();

		tfw.writeString(fExteriorColoringMethod.toString());
		tfw.writeLn();

		tfw.writeInteger(fColorMapInteriorSectorDecompositionRange);
		tfw.writeLn();

		tfw.writeInteger(fColorMapExteriorSectorDecompositionRange);
		tfw.writeLn();

		tfw.writeString(fColorMapScaling.toString());
		tfw.writeLn();

		tfw.writeDouble(fColorMapScalingFunctionMultiplier);
		tfw.writeLn();

		tfw.writeDouble(fColorMapScalingArgumentMultiplier);
		tfw.writeLn();

		tfw.writeBoolean(fRankOrderRestrictHighIterationCountColors);
		tfw.writeLn();

		tfw.writeBoolean(fColorMapRepeatMode);
		tfw.writeLn();

		tfw.writeDouble(fColorMapColorRepetition);
		tfw.writeLn();

		tfw.writeDouble(fColorMapColorOffset);
		tfw.writeLn();

		tfw.writeString(fColorMapUsage.toString());
		tfw.writeLn();

		tfw.writeDouble(fColorMapContinuousColorRange);
		tfw.writeLn();

		tfw.writeInteger(fColorMapDiscreteColorRange);
		tfw.writeLn();

		tfw.writeInteger(fLowIterationRange);
		tfw.writeLn();

		tfw.writeInteger(fHighIterationRange);
		tfw.writeLn();

		tfw.writeDouble(fBrightnessFactor);
		tfw.writeLn();

		tfw.writeBoolean(fLockAspectRatio);
		tfw.writeLn();

		tfw.writeBoolean(fUsePostProcessingFilters);
		tfw.writeLn();

		fPostProcessingFilterChain.save(tfw);
	}
}
