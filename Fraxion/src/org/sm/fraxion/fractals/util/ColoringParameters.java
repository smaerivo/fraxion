// ---------------------------------------
// Filename      : ColoringParameters.java
// Author        : Sven Maerivoet
// Last modified : 23/06/2015
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
import java.io.*;
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
 * @version 23/06/2015
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
		kAverageDistance,
		kAngle,
		kLyapunovExponent,
		kCurvature,
		kStriping,
		kMinimumGaussianIntegersDistance,
		kAverageGaussianIntegersDistance,
		kExteriorDistance,
		kOrbitTrapDisk,
		kOrbitTrapCrossStalks,
		kOrbitTrapSine,
		kOrbitTrapTangens,
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
	 * Loads the fractal colouring information from a plain-text file.
	 *
	 * @param  tfp                 a reference to the file parser
	 * @throws FileParseException  in case a parse error occurs
	 */
	public void plainTextLoad(TextFileParser tfp) throws FileParseException
	{
		fInteriorGradientColorMap = new JGradientColorMap(JGradientColorMap.EColorMap.valueOf(tfp.getNextString()));
		if (fInteriorGradientColorMap.getColorMap() == JGradientColorMap.EColorMap.kRandom) {
			fInteriorGradientColorMap.plainTextLoadRandomColorMapComponents(tfp);
		}
		else if (fInteriorGradientColorMap.getColorMap() == JGradientColorMap.EColorMap.kCustom) {
			fInteriorGradientColorMap.plainTextLoadCustomColorMapComponents(tfp);
		}
		fExteriorGradientColorMap = new JGradientColorMap(JGradientColorMap.EColorMap.valueOf(tfp.getNextString()));
		if (fExteriorGradientColorMap.getColorMap() == JGradientColorMap.EColorMap.kRandom) {
			fExteriorGradientColorMap.plainTextLoadRandomColorMapComponents(tfp);
		}
		else if (fExteriorGradientColorMap.getColorMap() == JGradientColorMap.EColorMap.kCustom) {
			fExteriorGradientColorMap.plainTextLoadCustomColorMapComponents(tfp);
		}
		fInteriorColorMapInverted = tfp.getNextBoolean();
		fExteriorColorMapInverted = tfp.getNextBoolean();
		fInteriorColorMapWrappedAround = tfp.getNextBoolean();
		fExteriorColorMapWrappedAround = tfp.getNextBoolean();
		fCalculateAdvancedColoring = tfp.getNextBoolean();
		fUseTigerStripes = tfp.getNextBoolean();
		fTigerGradientColorMap = new JGradientColorMap(JGradientColorMap.EColorMap.valueOf(tfp.getNextString()));
		if (fTigerGradientColorMap.getColorMap() == JGradientColorMap.EColorMap.kRandom) {
			fTigerGradientColorMap.plainTextLoadRandomColorMapComponents(tfp);
		}
		else if (fTigerGradientColorMap.getColorMap() == JGradientColorMap.EColorMap.kCustom) {
			fTigerGradientColorMap.plainTextLoadCustomColorMapComponents(tfp);
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
		fPostProcessingFilterChain.plainTextLoad(tfp);
	}

	/**
	 * Loads the fractal colouring information from a file as a stream.
	 *
	 * @param  dataInputStream  a data inputstream
	 * @throws IOException      in case a parse error occurs
	 */
	public void streamLoad(DataInputStream dataInputStream) throws IOException
	{
		fInteriorGradientColorMap = new JGradientColorMap(JGradientColorMap.EColorMap.valueOf(dataInputStream.readUTF()));
		if (fInteriorGradientColorMap.getColorMap() == JGradientColorMap.EColorMap.kRandom) {
			fInteriorGradientColorMap.streamLoadRandomColorMapComponents(dataInputStream);
		}
		else if (fInteriorGradientColorMap.getColorMap() == JGradientColorMap.EColorMap.kCustom) {
			fInteriorGradientColorMap.streamLoadCustomColorMapComponents(dataInputStream);
		}
		fExteriorGradientColorMap = new JGradientColorMap(JGradientColorMap.EColorMap.valueOf(dataInputStream.readUTF()));
		if (fExteriorGradientColorMap.getColorMap() == JGradientColorMap.EColorMap.kRandom) {
			fExteriorGradientColorMap.streamLoadRandomColorMapComponents(dataInputStream);
		}
		else if (fExteriorGradientColorMap.getColorMap() == JGradientColorMap.EColorMap.kCustom) {
			fExteriorGradientColorMap.streamLoadCustomColorMapComponents(dataInputStream);
		}
		fInteriorColorMapInverted = dataInputStream.readBoolean();
		fExteriorColorMapInverted = dataInputStream.readBoolean();
		fInteriorColorMapWrappedAround = dataInputStream.readBoolean();
		fExteriorColorMapWrappedAround = dataInputStream.readBoolean();
		fCalculateAdvancedColoring = dataInputStream.readBoolean();
		fUseTigerStripes = dataInputStream.readBoolean();
		fTigerGradientColorMap = new JGradientColorMap(JGradientColorMap.EColorMap.valueOf(dataInputStream.readUTF()));
		if (fTigerGradientColorMap.getColorMap() == JGradientColorMap.EColorMap.kRandom) {
			fTigerGradientColorMap.streamLoadRandomColorMapComponents(dataInputStream);
		}
		else if (fTigerGradientColorMap.getColorMap() == JGradientColorMap.EColorMap.kCustom) {
			fTigerGradientColorMap.streamLoadCustomColorMapComponents(dataInputStream);
		}
		fTigerUseFixedColor = dataInputStream.readBoolean();
		fTigerStripeFixedColor = new Color(dataInputStream.readInt());
		fInteriorColor = new Color(dataInputStream.readInt());
		fExteriorColor = new Color(dataInputStream.readInt());
		fInteriorColoringMethod = EColoringMethod.valueOf(dataInputStream.readUTF());
		fExteriorColoringMethod = EColoringMethod.valueOf(dataInputStream.readUTF());
		fColorMapInteriorSectorDecompositionRange = dataInputStream.readInt();
		fColorMapExteriorSectorDecompositionRange = dataInputStream.readInt();
		fColorMapScaling = EColorMapScaling.valueOf(dataInputStream.readUTF());
		fColorMapScalingFunctionMultiplier = dataInputStream.readDouble();
		fColorMapScalingArgumentMultiplier = dataInputStream.readDouble();
		fRankOrderRestrictHighIterationCountColors = dataInputStream.readBoolean();
		fColorMapRepeatMode = dataInputStream.readBoolean();
		fColorMapColorRepetition = dataInputStream.readDouble();
		fColorMapColorOffset = dataInputStream.readDouble();
		fColorMapUsage = EColorMapUsage.valueOf(dataInputStream.readUTF());
		fColorMapContinuousColorRange = dataInputStream.readDouble();
		fColorMapDiscreteColorRange = dataInputStream.readInt();
		fLowIterationRange = dataInputStream.readInt();
		fHighIterationRange = dataInputStream.readInt();
		fBrightnessFactor = dataInputStream.readDouble();
		fLockAspectRatio = dataInputStream.readBoolean();
		fUsePostProcessingFilters = dataInputStream.readBoolean();
		fPostProcessingFilterChain.streamLoad(dataInputStream);
	}

	/**
	 * Saves the current fractal colouring parameters to a plain-text file.
	 * 
	 * @param  tfw                 a reference to the file writer
	 * @throws FileWriteException  in case a write error occurs
	 */
	public void plainTextSave(TextFileWriter tfw) throws FileWriteException
	{
		JGradientColorMap.EColorMap interiorColorMap = fInteriorGradientColorMap.getColorMap();
		tfw.writeString(interiorColorMap.toString());
		tfw.writeLn();
		if (interiorColorMap == JGradientColorMap.EColorMap.kRandom) {
			fInteriorGradientColorMap.plainTextSaveRandomColorMapComponents(tfw);
		}
		else if (interiorColorMap == JGradientColorMap.EColorMap.kCustom) {
			fInteriorGradientColorMap.plainTextSaveCustomColorMapComponents(tfw);
		}

		JGradientColorMap.EColorMap exteriorColorMap = fExteriorGradientColorMap.getColorMap();
		tfw.writeString(exteriorColorMap.toString());
		tfw.writeLn();
		if (exteriorColorMap == JGradientColorMap.EColorMap.kRandom) {
			fExteriorGradientColorMap.plainTextSaveRandomColorMapComponents(tfw);
		}
		else if (exteriorColorMap == JGradientColorMap.EColorMap.kCustom) {
			fExteriorGradientColorMap.plainTextSaveCustomColorMapComponents(tfw);
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
		tfw.writeString(tigerColorMap.toString());
		tfw.writeLn();
		if (tigerColorMap == JGradientColorMap.EColorMap.kRandom) {
			fTigerGradientColorMap.plainTextSaveRandomColorMapComponents(tfw);
		}
		else if (tigerColorMap == JGradientColorMap.EColorMap.kCustom) {
			fTigerGradientColorMap.plainTextSaveCustomColorMapComponents(tfw);
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

		fPostProcessingFilterChain.plainTextSave(tfw);
	}

	/**
	 * Saves the current fractal colouring parameters to a file as a stream.
	 * 
	 * @param  dataOutputStream  a data outputstream
	 * @throws IOException       in case a write error occurs
	 */
	public void streamSave(DataOutputStream dataOutputStream) throws IOException
	{
		JGradientColorMap.EColorMap interiorColorMap = fInteriorGradientColorMap.getColorMap();
		dataOutputStream.writeUTF(interiorColorMap.toString());
		if (interiorColorMap == JGradientColorMap.EColorMap.kRandom) {
			fInteriorGradientColorMap.streamSaveRandomColorMapComponents(dataOutputStream);
		}
		else if (interiorColorMap == JGradientColorMap.EColorMap.kCustom) {
			fInteriorGradientColorMap.streamSaveCustomColorMapComponents(dataOutputStream);
		}

		JGradientColorMap.EColorMap exteriorColorMap = fExteriorGradientColorMap.getColorMap();
		dataOutputStream.writeUTF(exteriorColorMap.toString());
		if (exteriorColorMap == JGradientColorMap.EColorMap.kRandom) {
			fExteriorGradientColorMap.streamSaveRandomColorMapComponents(dataOutputStream);
		}
		else if (exteriorColorMap == JGradientColorMap.EColorMap.kCustom) {
			fExteriorGradientColorMap.streamSaveCustomColorMapComponents(dataOutputStream);
		}

		dataOutputStream.writeBoolean(fInteriorColorMapInverted);
		dataOutputStream.writeBoolean(fExteriorColorMapInverted);
		dataOutputStream.writeBoolean(fInteriorColorMapWrappedAround);
		dataOutputStream.writeBoolean(fExteriorColorMapWrappedAround);
		dataOutputStream.writeBoolean(fCalculateAdvancedColoring);
		dataOutputStream.writeBoolean(fUseTigerStripes);

		JGradientColorMap.EColorMap tigerColorMap = fTigerGradientColorMap.getColorMap();
		dataOutputStream.writeUTF(tigerColorMap.toString());
		if (tigerColorMap == JGradientColorMap.EColorMap.kRandom) {
			fTigerGradientColorMap.streamSaveRandomColorMapComponents(dataOutputStream);
		}
		else if (tigerColorMap == JGradientColorMap.EColorMap.kCustom) {
			fTigerGradientColorMap.streamSaveCustomColorMapComponents(dataOutputStream);
		}

		dataOutputStream.writeBoolean(fTigerUseFixedColor);
		dataOutputStream.writeInt(fTigerStripeFixedColor.getRGB());
		dataOutputStream.writeInt(fInteriorColor.getRGB());
		dataOutputStream.writeInt(fExteriorColor.getRGB());
		dataOutputStream.writeUTF(fInteriorColoringMethod.toString());
		dataOutputStream.writeUTF(fExteriorColoringMethod.toString());
		dataOutputStream.writeInt(fColorMapInteriorSectorDecompositionRange);
		dataOutputStream.writeInt(fColorMapExteriorSectorDecompositionRange);
		dataOutputStream.writeUTF(fColorMapScaling.toString());
		dataOutputStream.writeDouble(fColorMapScalingFunctionMultiplier);
		dataOutputStream.writeDouble(fColorMapScalingArgumentMultiplier);
		dataOutputStream.writeBoolean(fRankOrderRestrictHighIterationCountColors);
		dataOutputStream.writeBoolean(fColorMapRepeatMode);
		dataOutputStream.writeDouble(fColorMapColorRepetition);
		dataOutputStream.writeDouble(fColorMapColorOffset);
		dataOutputStream.writeUTF(fColorMapUsage.toString());
		dataOutputStream.writeDouble(fColorMapContinuousColorRange);
		dataOutputStream.writeInt(fColorMapDiscreteColorRange);
		dataOutputStream.writeInt(fLowIterationRange);
		dataOutputStream.writeInt(fHighIterationRange);
		dataOutputStream.writeDouble(fBrightnessFactor);
		dataOutputStream.writeBoolean(fLockAspectRatio);
		dataOutputStream.writeBoolean(fUsePostProcessingFilters);

		fPostProcessingFilterChain.streamSave(dataOutputStream);
	}
}
