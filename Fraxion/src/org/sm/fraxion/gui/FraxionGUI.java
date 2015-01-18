// -------------------------------
// Filename      : FraxionGUI.java
// Author        : Sven Maerivoet
// Last modified : 18/01/2015
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

package org.sm.fraxion.gui;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.help.*;
import javax.swing.*;
import org.apache.log4j.*;
import org.sm.fraxion.concurrent.*;
import org.sm.fraxion.fractals.*;
import org.sm.fraxion.fractals.util.*;
import org.sm.fraxion.gui.dialogs.*;
import org.sm.fraxion.gui.filters.*;
import org.sm.fraxion.gui.util.*;
import org.sm.smtools.application.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.exceptions.*;
import org.sm.smtools.math.*;
import org.sm.smtools.math.complex.*;
import org.sm.smtools.swing.dialogs.*;
import org.sm.smtools.swing.util.*;
import org.sm.smtools.util.*;

/**
 * The <CODE>FraxionGUI</CODE> class provides the main GUI for the fractal exploration application.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 18/01/2015
 */
public final class FraxionGUI extends JStandardGUIApplication implements ActionListener, MouseListener, MouseMotionListener, KeyListener
{
	// the application's icon filename
	private static final String kApplicationIconFilename = "application-resources/images/icon.jpg";

	// the amount of time to explicitly wait during the splash screen show
	private static final int kSplashScreenStatusMessageWaitTime = 250;

	// the action commands for the menus
	private static final String kActionCommandMenuItemFileExportToPNG = "menuItem.File.ExportToPNG";
	private static final String kActionCommandMenuItemFileLoadFractal = "menuItem.File.LoadFractal";
	private static final String kActionCommandMenuItemFileSaveFractal = "menuItem.File.SaveFractal";
	private static final String kActionCommandMenuItemFileLoadFractalParameters = "menuItem.File.LoadFractalParameters";
	private static final String kActionCommandMenuItemFileSaveFractalParameters = "menuItem.File.SaveFractalParameters";
	private static final String kActionCommandMenuItemFileLoadZoomStack = "menuItem.File.LoadZoomStack";
	private static final String kActionCommandMenuItemFileSaveZoomStack = "menuItem.File.SaveZoomStack";
	private static final String kActionCommandMenuItemFileSaveZoomAnimationSequence = "menuItem.File.SaveZoomAnimationSequence";

	private static final String kActionCommandMenuItemNavigationPanLeft = "menuItem.Navigation.PanLeft";
	private static final String kActionCommandMenuItemNavigationPanRight = "menuItem.Navigation.PanRight";
	private static final String kActionCommandMenuItemNavigationPanUp = "menuItem.Navigation.PanUp";
	private static final String kActionCommandMenuItemNavigationPanDown = "menuItem.Navigation.PanDown";
	private static final String kActionCommandMenuItemNavigationSetPanningSize = "menuItem.Navigation.SetPanningSize";
	private static final String kActionCommandMenuItemNavigationInvertPanningDirections = "menuItem.Navigation.InvertPanningDirections";
	private static final String kActionCommandMenuItemNavigationShowZoomInformation = "menuItem.Navigation.ShowZoomInformation";
	private static final String kActionCommandMenuItemNavigationLockAspectRatio = "menuItem.Navigation.LockAspectRatio";
	private static final String kActionCommandMenuItemNavigationCentredZooming = "menuItem.Navigation.CentredZooming";
	private static final String kActionCommandMenuItemNavigationResetZoom = "menuItem.Navigation.ResetZoom";
	private static final String kActionCommandMenuItemNavigationZoomToLevel = "menuItem.Navigation.ZoomToLevel";
	private static final String kActionCommandMenuItemNavigationShowAxes = "menuItem.Navigation.ShowAxes";
	private static final String kActionCommandMenuItemNavigationInvertYAxis = "menuItem.Navigation.InvertYAxis";
	private static final String kActionCommandMenuItemNavigationShowCurrentLocation = "menuItem.Navigation.ShowCurrentLocation";
	private static final String kActionCommandMenuItemNavigationShowMagnifyingGlass = "menuItem.Navigation.ShowMagnifyingGlass";
	private static final String kActionCommandMenuItemNavigationSetMagnifyingGlassSize = "menuItem.Navigation.SetMagnifyingGlassSize";
	private static final String kActionCommandMenuItemNavigationSpecifiyScreenBounds = "menuItem.Navigation.SpecifyScreenBounds";
	private static final String kActionCommandMenuItemNavigationSpecifyCoordinates = "menuItem.Navigation.SpecifyCoordinates";

	private static final String kActionCommandMenuItemFractalDoubleClickModeSwitchDualMainFractal = "menuItem.Fractal.DoubleClickModeSwitchDualMainFractal";
	private static final String kActionCommandMenuItemFractalSwitchFractalType = "menuItem.Fractal.SwitchFractalType";
	private static final String kActionCommandMenuItemFractalDoubleClickModeSetOrbitStartingPoint = "menuItem.Fractal.DoubleClickModeSetOrbitStartingPoint";
	private static final String kActionCommandMenuItemFractalResetOrbitStartingPoint = "menuItem.Fractal.ResetOrbitStartingPoint";
	private static final String kActionCommandMenuItemFractalShowInset = "menuItem.Fractal.ShowInset";
	private static final String kActionCommandMenuItemFractalAutoZoomInset = "menuItem.Fractal.AutoZoomInset";
	private static final String kActionCommandMenuItemFractalSetInsetSize = "menuItem.Fractal.SetInsetSize";
	private static final String kActionCommandMenuItemFractalInsetFractalIsDeformedMainFractal = "menuItem.Fractal.InsetFractalIsDeformedMainFractal";
	private static final String kActionCommandMenuItemFractalShowOrbits = "menuItem.Fractal.ShowOrbits";
	private static final String kActionCommandMenuItemFractalShowOrbitPaths = "menuItem.Fractal.ShowOrbitPaths";
	private static final String kActionCommandMenuItemFractalScaleOrbitsToScreen = "menuItem.Fractal.ScaleOrbitsToScreen";
	private static final String kActionCommandMenuItemFractalShowOrbitAnalyses = "menuItem.Fractal.ShowOrbitAnalyses";
	private static final String kActionCommandMenuItemFractalShowIterationDistribution = "menuItem.Fractal.ShowIterationDistribution";
	private static final String kActionCommandMenuItemFractalSetOrbitAnalysesPanelSize = "menuItem.Fractal.SetOrbitAnalysesPanelSize";
	private static final String kActionCommandMenuItemFractalSetMaxNrOfIterationsInOrbitAnalyses = "menuItem.Fractal.SetMaxNrOfIterationsInOrbitAnalyses";
	private static final String kActionCommandMenuItemFractalSpecifyFractalFamily = "menu.Fractal.SpecifyFractalFamily";

	private static final String kActionCommandMenuItemFractalFamilyDefaultMandelbrotJulia = "menuItem.Fractal.Family.DefaultMandelbrotJulia";
	private static final String kActionCommandMenuItemFractalFamilyMandelbar = "menuItem.Fractal.Family.Mandelbar";
	private static final String kActionCommandMenuItemFractalFamilyRandelbrot = "menuItem.Fractal.Family.Randelbrot";
	private static final String kActionCommandMenuItemFractalFamilyOriginalJulia = "menuItem.Fractal.Family.OriginalJulia";
	private static final String kActionCommandMenuItemFractalFamilyLambda = "menuItem.Fractal.Family.Lambda";
	private static final String kActionCommandMenuItemFractalFamilyInverseLambda = "menuItem.Fractal.Family.InverseLambda";
	private static final String kActionCommandMenuItemFractalFamilyBurningShip = "menuItem.Fractal.Family.BurningShip";
	private static final String kActionCommandMenuItemFractalFamilyBirdOfPrey = "menuItem.Fractal.Family.BirdOfPrey";
	private static final String kActionCommandMenuItemFractalFamilyGlynn = "menuItem.Fractal.Family.Glynn";
	private static final String kActionCommandMenuItemFractalFamilySpider = "menuItem.Fractal.Family.Spider";
	private static final String kActionCommandMenuItemFractalFamilyMultibrot = "menuItem.Fractal.Family.Multibrot";
	private static final String kActionCommandMenuItemFractalFamilyMultibrotPolynomial = "menuItem.Fractal.Family.MultibrotPolynomial";
	private static final String kActionCommandMenuItemFractalFamilyMultibrotParameter = "menuItem.Fractal.Family.MultibrotParameter";
	private static final String kActionCommandMenuItemFractalFamilyMultibrotInvertedParameter = "menuItem.Fractal.Family.MultibrotInvertedParameter";
	private static final String kActionCommandMenuItemFractalFamilyMultibar = "menuItem.Fractal.Family.Multibar";
	private static final String kActionCommandMenuItemFractalFamilyMultibarPolynomial = "menuItem.Fractal.Family.MultibarPolynomial";
	private static final String kActionCommandMenuItemFractalFamilyMultibarParameter = "menuItem.Fractal.Family.MultibarParameter";
	private static final String kActionCommandMenuItemFractalFamilyMultibarInvertedParameter = "menuItem.Fractal.Family.MultibarInvertedParameter";
	private static final String kActionCommandMenuItemFractalFamilyBurningMultiShip = "menuItem.Fractal.Family.BurningMultiShip";
	private static final String kActionCommandMenuItemFractalFamilyMultiProductExpelbrot = "menuItem.Fractal.Family.MultiProductExpelbrot";
	private static final String kActionCommandMenuItemFractalFamilyMultiSumExpelbrot = "menuItem.Fractal.Family.MultiSumExpelbrot";
	private static final String kActionCommandMenuItemFractalFamilyMultiProductExparbrot = "menuItem.Fractal.Family.MultiProductExparbrot";
	private static final String kActionCommandMenuItemFractalFamilyMultiSumExparbrot = "menuItem.Fractal.Family.MultiSumExparbrot";
	private static final String kActionCommandMenuItemFractalFamilyTrigonometricPowerSine = "menuItem.Fractal.Family.TrigonometricPowerSine";
	private static final String kActionCommandMenuItemFractalFamilyTrigonometricPowerCosine = "menuItem.Fractal.Family.TrigonometricPowerCosine";
	private static final String kActionCommandMenuItemFractalFamilyTrigonometricPowerTangent = "menuItem.Fractal.Family.TrigonometricPowerTangent";
	private static final String kActionCommandMenuItemFractalFamilyTrigonometricPowerCotangent = "menuItem.Fractal.Family.TrigonometricPowerCotangent";
	private static final String kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiSine = "menuItem.Fractal.Family.TrigonometricPowerMultiSine";
	private static final String kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCosine = "menuItem.Fractal.Family.TrigonometricPowerMultiCosine";
	private static final String kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiTangent = "menuItem.Fractal.Family.TrigonometricPowerMultiTangent";
	private static final String kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCotangent = "menuItem.Fractal.Family.TrigonometricPowerMultiCotangent";
	private static final String kActionCommandMenuItemFractalFamilyCactus = "menuItem.Fractal.Family.Cactus";
	private static final String kActionCommandMenuItemFractalFamilyBeauty1 = "menuItem.Fractal.Family.Beauty1";
	private static final String kActionCommandMenuItemFractalFamilyBeauty2 = "menuItem.Fractal.Family.Beauty2";
	private static final String kActionCommandMenuItemFractalFamilyDucks = "menuItem.Fractal.Family.Ducks";
	private static final String kActionCommandMenuItemFractalFamilyBarnsleyTree = "menuItem.Fractal.Family.BarnsleyTree";
	private static final String kActionCommandMenuItemFractalFamilyCollatz = "menuItem.Fractal.Family.Collatz";
	private static final String kActionCommandMenuItemFractalFamilyPhoenix = "menuItem.Fractal.Family.Phoenix";
	private static final String kActionCommandMenuItemFractalFamilyManowar = "menuItem.Fractal.Family.Manowar";
	private static final String kActionCommandMenuItemFractalFamilyNewtonRaphsonPower = "menuItem.Fractal.Family.NewtonRaphsonPower";
	private static final String kActionCommandMenuItemFractalFamilyNewtonRaphsonPowerPolynomial = "menuItem.Fractal.Family.NewtonRaphsonPowerPolynomial";
	private static final String kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial1 = "menuItem.Fractal.Family.NewtonRaphsonFixedPolynomial1";
	private static final String kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial2 = "menuItem.Fractal.Family.NewtonRaphsonFixedPolynomial2";
	private static final String kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial3 = "menuItem.Fractal.Family.NewtonRaphsonFixedPolynomial3";
	private static final String kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial4 = "menuItem.Fractal.Family.NewtonRaphsonFixedPolynomial4";
	private static final String kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSine = "menuItem.Fractal.Family.NewtonRaphsonTrigonometricPowerSine";
	private static final String kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSine = "menuItem.Fractal.Family.NewtonRaphsonTrigonometricPowerMultiSine";
	private static final String kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSineOffset = "menuItem.Fractal.Family.NewtonRaphsonTrigonometricPowerSineOffset";
	private static final String kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSineOffset = "menuItem.Fractal.Family.NewtonRaphsonTrigonometricPowerMultiSineOffset";
	private static final String kActionCommandMenuItemFractalFamilyNova = "menuItem.Fractal.Family.Nova";
	private static final String kActionCommandMenuItemFractalFamilyMagnetTypeI = "menuItem.Fractal.Family.MagnetTypeI";
	private static final String kActionCommandMenuItemFractalFamilyMagnetTypeII = "menuItem.Fractal.Family.MagnetTypeII";
	private static final String kActionCommandMenuItemFractalFamilyMarkusLyapunov = "menuItem.Fractal.Family.MarkusLyapunov";
	private static final String kActionCommandMenuItemFractalFamilyMarkusLyapunovLogisticBifurcation = "menuItem.Fractal.Family.MarkusLyapunovLogisticBifurcation";
	private static final String kActionCommandMenuItemFractalFamilyMarkusLyapunovJellyfish = "menuItem.Fractal.Family.MarkusLyapunovJellyfish";
	private static final String kActionCommandMenuItemFractalFamilyMarkusLyapunovZirconZity = "menuItem.Fractal.Family.MarkusLyapunovZirconZity";

	private static final String kActionCommandMenuItemFractalFamilyRandelbrotSetNoiseLevel = "menuItem.Fractal.Family.Randelbrot.SetNoiseLevel";
	private static final String kActionCommandMenuItemFractalFamilyGlynnSetPower = "menuItem.Fractal.Family.Glynn.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyMultibrotSetPower = "menuItem.Fractal.Family.Multibrot.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyMultibrotPolynomialSetPower = "menuItem.Fractal.Family.MultibrotPolynomial.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyMultibrotParameterSetPower = "menuItem.Fractal.Family.MultibrotParameter.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyMultibrotInvertedParameterSetPower = "menuItem.Fractal.Family.MultibrotInvertedParameter.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyMultibarSetPower = "menuItem.Fractal.Family.Multibar.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyMultibarPolynomialSetPower = "menuItem.Fractal.Family.MultibarPolynomial.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyMultibarParameterSetPower = "menuItem.Fractal.Family.MultibarParameter.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyMultibarInvertedParameterSetPower = "menuItem.Fractal.Family.MultibarInvertedParameter.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyBurningMultiShipSetPower = "menuItem.Fractal.Family.BurningMultiShip.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyMultiProductExpelbrotSetPower = "menuItem.Fractal.Family.MultiProductExpelbrot.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyMultiSumExpelbrotSetPower = "menuItem.Fractal.Family.MultiSumExpelbrot.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyMultiProductExparbrotSetPower = "menuItem.Fractal.Family.MultiProductExparbrot.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyMultiSumExparbrotSetPower = "menuItem.Fractal.Family.MultiSumExparbrot.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyTrigonometricPowerSineSetPower = "menuItem.Fractal.Family.TrigonometricPowerSine.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyTrigonometricPowerCosineSetPower = "menuItem.Fractal.Family.TrigonometricPowerCosine.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyTrigonometricPowerTangentSetPower = "menuItem.Fractal.Family.TrigonometricPowerTangent.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyTrigonometricPowerCotangentSetPower = "menuItem.Fractal.Family.TrigonometricPowerCotangent.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiSineSetPower = "menuItem.Fractal.Family.TrigonometricPowerMultiSine.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCosineSetPower = "menuItem.Fractal.Family.TrigonometricPowerMultiCosine.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiTangentSetPower = "menuItem.Fractal.Family.TrigonometricPowerMultiTangent.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCotangentSetPower = "menuItem.Fractal.Family.TrigonometricPowerMultiCotangent.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyDucksSetFixedNrOfIterations = "menuItem.Fractal.Family.Ducks.SetFixedNrOfIterations";
	private static final String kActionCommandMenuItemFractalFamilyNewtonRaphsonSetConvergenceParameters = "menuItem.Fractal.Family.NewtonRaphson.SetConvergenceParameters";
	private static final String kActionCommandMenuItemFractalFamilyNewtonRaphsonAutomaticRootDetectionEnabled = "menuItem.Fractal.Family.NewtonRaphson.AutomaticRootDetectionEnabled";
	private static final String kActionCommandMenuItemFractalFamilyNewtonRaphsonPowerSetPower = "menuItem.Fractal.Family.NewtonRaphsonPower.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyNewtonRaphsonPowerPolynomialSetPower = "menuItem.Fractal.Family.NewtonRaphsonPowerPolynomial.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSineSetPower = "menuItem.Fractal.Family.NewtonRaphsonTrigonometricPowerSine.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSineSetPower = "menuItem.Fractal.Family.NewtonRaphsonTrigonometricPowerMultiSine.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSineOffsetSetPower = "menuItem.Fractal.Family.NewtonRaphsonTrigonometricPowerSineOffset.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSineOffsetSetPower = "menuItem.Fractal.Family.NewtonRaphsonTrigonometricPowerMultiSineOffset.SetPower";
	private static final String kActionCommandMenuItemFractalFamilyMagnetSetConvergenceParameters = "menuItem.Fractal.Family.Magnet.SetConvergenceParameters";
	private static final String kActionCommandMenuItemFractalFamilyMarkusLyapunovSetRootSequence = "menuItem.Fractal.Family.MarkusLyapunov.SetRootSequence";

	private static final String kActionCommandMenuItemFractalSetMaxNrOfIterations = "menuItem.Fractal.SetMaxNrOfIterations";
	private static final String kActionCommandMenuItemFractalSetEscapeRadius = "menuItem.Fractal.SetEscapeRadius";
	private static final String kActionCommandMenuItemFractalCopyCoordinates = "menuItem.Fractal.CopyCoordinatesToClipboard";
	private static final String kActionCommandMenuItemFractalRefreshScreen = "menuItem.Fractal.RefreshScreen";

	private static final String kActionCommandMenuItemColorMapExteriorBone = "menuItem.ColorMap.Exterior.Bone";
	private static final String kActionCommandMenuItemColorMapExteriorCopper = "menuItem.ColorMap.Exterior.Copper";
	private static final String kActionCommandMenuItemColorMapExteriorDiscontinuousBlueWhiteGreen = "menuItem.ColorMap.Exterior.DiscontinuousBlueWhiteGreen";
	private static final String kActionCommandMenuItemColorMapExteriorDiscontinuousDarkRedYellow = "menuItem.ColorMap.Exterior.DiscontinuousDarkRedYellow";
	private static final String kActionCommandMenuItemColorMapExteriorBlackAndWhite = "menuItem.ColorMap.Exterior.BlackAndWhite";
	private static final String kActionCommandMenuItemColorMapExteriorGrayScale = "menuItem.ColorMap.Exterior.GrayScale";
	private static final String kActionCommandMenuItemColorMapExteriorGreenRedDiverging = "menuItem.ColorMap.Exterior.GreenRedDiverging";
	private static final String kActionCommandMenuItemColorMapExteriorHot = "menuItem.ColorMap.Exterior.Hot";
	private static final String kActionCommandMenuItemColorMapExteriorJet = "menuItem.ColorMap.Exterior.Jet";
	private static final String kActionCommandMenuItemColorMapExteriorHueSaturationBrightness = "menuItem.ColorMap.Exterior.HueSaturationBrightness";
	private static final String kActionCommandMenuItemColorMapExteriorSeparatedRGB = "menuItem.ColorMap.Exterior.SeparatedRGB";
	private static final String kActionCommandMenuItemColorMapExteriorRed = "menuItem.ColorMap.Exterior.Red";
	private static final String kActionCommandMenuItemColorMapExteriorGreen = "menuItem.ColorMap.Exterior.Green";
	private static final String kActionCommandMenuItemColorMapExteriorBlue = "menuItem.ColorMap.Exterior.Blue";
	private static final String kActionCommandMenuItemColorMapExteriorYellow = "menuItem.ColorMap.Exterior.Yellow";
	private static final String kActionCommandMenuItemColorMapExteriorCyan = "menuItem.ColorMap.Exterior.Cyan";
	private static final String kActionCommandMenuItemColorMapExteriorMagenta = "menuItem.ColorMap.Exterior.Magenta";	
	private static final String kActionCommandMenuItemColorMapExteriorUltraLightPastel = "menuItem.ColorMap.Exterior.UltraLightPastel";
	private static final String kActionCommandMenuItemColorMapExteriorLightPastel = "menuItem.ColorMap.Exterior.LightPastel";
	private static final String kActionCommandMenuItemColorMapExteriorDarkPastel = "menuItem.ColorMap.Exterior.DarkPastel";
	private static final String kActionCommandMenuItemColorMapExteriorGreens = "menuItem.ColorMap.Exterior.Greens";
	private static final String kActionCommandMenuItemColorMapExteriorBlues = "menuItem.ColorMap.Exterior.Blues";
	private static final String kActionCommandMenuItemColorMapExteriorYellowBrowns = "menuItem.ColorMap.Exterior.YellowBrowns";
	private static final String kActionCommandMenuItemColorMapExteriorVioletPurples = "menuItem.ColorMap.Exterior.VioletPurples";
	private static final String kActionCommandMenuItemColorMapExteriorDeepSpace = "menuItem.ColorMap.Exterior.DeepSpace";
	private static final String kActionCommandMenuItemColorMapExteriorCustom = "menuItem.ColorMap.Exterior.Custom";
	private static final String kActionCommandMenuItemColorMapExteriorSetCustomColorMap = "menuItem.ColorMap.Exterior.SetCustomColorMap";
	private static final String kActionCommandMenuItemColorMapExteriorConvertCurrentColorMapToCustomColorMap = "menuItem.ColorMap.Exterior.ConvertCurrentColorMapToCustomColorMap";
	private static final String kActionCommandMenuItemColorMapExteriorInvertColorMap = "menuItem.ColorMap.Exterior.InvertColorMap";
	private static final String kActionCommandMenuItemColorMapExteriorWrapAroundColorMap = "menuItem.ColorMap.Exterior.WrapAroundColorMap";
	private static final String kActionCommandMenuItemColorMapCalculateAdvancedColoring = "menuItem.ColorMap.CalculateAdvancedColoring";

	private static final String kActionCommandMenuItemColorMapUseTigerStripes = "menuItem.ColorMap.UseTigerStripes";
	private static final String kActionCommandMenuItemColorMapTigerBone = "menuItem.ColorMap.Tiger.Bone";
	private static final String kActionCommandMenuItemColorMapTigerCopper = "menuItem.ColorMap.Tiger.Copper";
	private static final String kActionCommandMenuItemColorMapTigerDiscontinuousBlueWhiteGreen = "menuItem.ColorMap.Tiger.DiscontinuousBlueWhiteGreen";
	private static final String kActionCommandMenuItemColorMapTigerDiscontinuousDarkRedYellow = "menuItem.ColorMap.Tiger.DiscontinuousDarkRedYellow";
	private static final String kActionCommandMenuItemColorMapTigerBlackAndWhite = "menuItem.ColorMap.Tiger.BlackAndWhite";
	private static final String kActionCommandMenuItemColorMapTigerGrayScale = "menuItem.ColorMap.Tiger.GrayScale";
	private static final String kActionCommandMenuItemColorMapTigerGreenRedDiverging = "menuItem.ColorMap.Tiger.GreenRedDiverging";
	private static final String kActionCommandMenuItemColorMapTigerHot = "menuItem.ColorMap.Tiger.Hot";
	private static final String kActionCommandMenuItemColorMapTigerJet = "menuItem.ColorMap.Tiger.Jet";
	private static final String kActionCommandMenuItemColorMapTigerHueSaturationBrightness = "menuItem.ColorMap.Tiger.HueSaturationBrightness";
	private static final String kActionCommandMenuItemColorMapTigerSeparatedRGB = "menuItem.ColorMap.Tiger.SeparatedRGB";
	private static final String kActionCommandMenuItemColorMapTigerRed = "menuItem.ColorMap.Tiger.Red";
	private static final String kActionCommandMenuItemColorMapTigerGreen = "menuItem.ColorMap.Tiger.Green";
	private static final String kActionCommandMenuItemColorMapTigerBlue = "menuItem.ColorMap.Tiger.Blue";
	private static final String kActionCommandMenuItemColorMapTigerYellow = "menuItem.ColorMap.Tiger.Yellow";
	private static final String kActionCommandMenuItemColorMapTigerCyan = "menuItem.ColorMap.Tiger.Cyan";
	private static final String kActionCommandMenuItemColorMapTigerMagenta = "menuItem.ColorMap.Tiger.Magenta";
	private static final String kActionCommandMenuItemColorMapTigerUltraLightPastel = "menuItem.ColorMap.Tiger.UltraLightPastel";
	private static final String kActionCommandMenuItemColorMapTigerLightPastel = "menuItem.ColorMap.Tiger.LightPastel";
	private static final String kActionCommandMenuItemColorMapTigerDarkPastel = "menuItem.ColorMap.Tiger.DarkPastel";
	private static final String kActionCommandMenuItemColorMapTigerGreens = "menuItem.ColorMap.Tiger.Greens";
	private static final String kActionCommandMenuItemColorMapTigerBlues = "menuItem.ColorMap.Tiger.Blues";
	private static final String kActionCommandMenuItemColorMapTigerYellowBrowns = "menuItem.ColorMap.Tiger.YellowBrowns";
	private static final String kActionCommandMenuItemColorMapTigerVioletPurples = "menuItem.ColorMap.Tiger.VioletPurples";
	private static final String kActionCommandMenuItemColorMapTigerDeepSpace = "menuItem.ColorMap.Tiger.DeepSpace";
	private static final String kActionCommandMenuItemColorMapTigerCustom = "menuItem.ColorMap.Tiger.Custom";
	private static final String kActionCommandMenuItemColorMapTigerSetCustomColorMap = "menuItem.ColorMap.Tiger.SetCustomColorMap";
	private static final String kActionCommandMenuItemColorMapTigerConvertCurrentColorMapToCustomColorMap = "menuItem.ColorMap.Tiger.ConvertCurrentColorMapToCustomColorMap";
	private static final String kActionCommandMenuItemColorMapTigerUseFixedColor = "menuItem.ColorMap.Tiger.UseFixedColor";
	private static final String kActionCommandMenuItemColorMapTigerSetFixedColor = "menuItem.ColorMap.Tiger.SetFixedColor";

	private static final String kActionCommandMenuItemColorMapInteriorBone = "menuItem.ColorMap.Interior.Bone";
	private static final String kActionCommandMenuItemColorMapInteriorCopper = "menuItem.ColorMap.Interior.Copper";
	private static final String kActionCommandMenuItemColorMapInteriorDiscontinuousBlueWhiteGreen = "menuItem.ColorMap.Interior.DiscontinuousBlueWhiteGreen";
	private static final String kActionCommandMenuItemColorMapInteriorDiscontinuousDarkRedYellow = "menuItem.ColorMap.Interior.DiscontinuousDarkRedYellow";
	private static final String kActionCommandMenuItemColorMapInteriorBlackAndWhite = "menuItem.ColorMap.Interior.BlackAndWhite";
	private static final String kActionCommandMenuItemColorMapInteriorGrayScale = "menuItem.ColorMap.Interior.GrayScale";
	private static final String kActionCommandMenuItemColorMapInteriorGreenRedDiverging = "menuItem.ColorMap.Interior.GreenRedDiverging";
	private static final String kActionCommandMenuItemColorMapInteriorHot = "menuItem.ColorMap.Interior.Hot";
	private static final String kActionCommandMenuItemColorMapInteriorJet = "menuItem.ColorMap.Interior.Jet";
	private static final String kActionCommandMenuItemColorMapInteriorHueSaturationBrightness = "menuItem.ColorMap.Interior.HueSaturationBrightness";
	private static final String kActionCommandMenuItemColorMapInteriorSeparatedRGB = "menuItem.ColorMap.Interior.SeparatedRGB";
	private static final String kActionCommandMenuItemColorMapInteriorRed = "menuItem.ColorMap.Interior.Red";
	private static final String kActionCommandMenuItemColorMapInteriorGreen = "menuItem.ColorMap.Interior.Green";
	private static final String kActionCommandMenuItemColorMapInteriorBlue = "menuItem.ColorMap.Interior.Blue";
	private static final String kActionCommandMenuItemColorMapInteriorYellow = "menuItem.ColorMap.Interior.Yellow";
	private static final String kActionCommandMenuItemColorMapInteriorCyan = "menuItem.ColorMap.Interior.Cyan";
	private static final String kActionCommandMenuItemColorMapInteriorMagenta = "menuItem.ColorMap.Interior.Magenta";
	private static final String kActionCommandMenuItemColorMapInteriorUltraLightPastel = "menuItem.ColorMap.Interior.UltraLightPastel";
	private static final String kActionCommandMenuItemColorMapInteriorLightPastel = "menuItem.ColorMap.Interior.LightPastel";
	private static final String kActionCommandMenuItemColorMapInteriorDarkPastel = "menuItem.ColorMap.Interior.DarkPastel";
	private static final String kActionCommandMenuItemColorMapInteriorGreens = "menuItem.ColorMap.Interior.Greens";
	private static final String kActionCommandMenuItemColorMapInteriorBlues = "menuItem.ColorMap.Interior.Blues";
	private static final String kActionCommandMenuItemColorMapInteriorYellowBrowns = "menuItem.ColorMap.Interior.YellowBrowns";
	private static final String kActionCommandMenuItemColorMapInteriorVioletPurples = "menuItem.ColorMap.Interior.VioletPurples";
	private static final String kActionCommandMenuItemColorMapInteriorDeepSpace = "menuItem.ColorMap.Interior.DeepSpace";
	private static final String kActionCommandMenuItemColorMapInteriorCustom = "menuItem.ColorMap.Interior.Custom";
	private static final String kActionCommandMenuItemColorMapInteriorSetCustomColorMap = "menuItem.ColorMap.Interior.SetCustomColorMap";
	private static final String kActionCommandMenuItemColorMapInteriorConvertCurrentColorMapToCustomColorMap = "menuItem.ColorMap.Interior.ConvertCurrentColorMapToCustomColorMap";
	private static final String kActionCommandMenuItemColorMapInteriorInvertColorMap = "menuItem.ColorMap.Interior.InvertColorMap";
	private static final String kActionCommandMenuItemColorMapInteriorWrapAroundColorMap = "menuItem.ColorMap.Interior.WrapAroundColorMap";

	private static final String kActionCommandMenuItemColorMapExteriorUseFixedColor = "menuItem.ColorMap.Exterior.UseFixedColor";
	private static final String kActionCommandMenuItemColorMapExteriorSetFixedColor = "menuItem.ColorMap.Exterior.SetFixedColor";
	private static final String kActionCommandMenuItemColorMapExteriorUseDiscreteLevelSets = "menuItem.ColorMap.Exterior.UseDiscreteLevelSets";
	private static final String kActionCommandMenuItemColorMapExteriorUseNormalisedLevelSets = "menuItem.ColorMap.Exterior.UseNormalisedLevelSets";
	private static final String kActionCommandMenuItemColorMapExteriorUseExponentiallySmoothedLevelSets = "menuItem.ColorMap.Exterior.UseExponentiallySmoothedLevelSets";
	private static final String kActionCommandMenuItemColorMapExteriorUseSectorDecomposition = "menuItem.ColorMap.Exterior.UseSectorDecomposition";
	private static final String kActionCommandMenuItemColorMapExteriorSetDecompositionSectorRange = "menuItem.ColorMap.Exterior.SetDecompositionSectorRange";
	private static final String kActionCommandMenuItemColorMapExteriorUseRealComponent = "menuItem.ColorMap.Exterior.UseRealComponent";
	private static final String kActionCommandMenuItemColorMapExteriorUseImaginaryComponent = "menuItem.ColorMap.Exterior.UseImaginaryComponent";
	private static final String kActionCommandMenuItemColorMapExteriorUseModulus = "menuItem.ColorMap.Exterior.UseModulus";
	private static final String kActionCommandMenuItemColorMapExteriorUseAngle = "menuItem.ColorMap.Exterior.UseAngle";
	private static final String kActionCommandMenuItemColorMapExteriorUseMaxModulus = "menuItem.ColorMap.Exterior.UseMaxModulus";
	private static final String kActionCommandMenuItemColorMapExteriorUseTotalDistance = "menuItem.ColorMap.Exterior.UseTotalDistance";
	private static final String kActionCommandMenuItemColorMapExteriorUseAverageDistance = "menuItem.ColorMap.Exterior.UseAverageDistance";
	private static final String kActionCommandMenuItemColorMapExteriorUseTotalAngle = "menuItem.ColorMap.Exterior.UseTotalAngle";
	private static final String kActionCommandMenuItemColorMapExteriorUseLyapunovExponent = "menuItem.ColorMap.Exterior.UseLyapunovExponent";
	private static final String kActionCommandMenuItemColorMapExteriorUseCurvature = "menuItem.ColorMap.Exterior.UseCurvature";
	private static final String kActionCommandMenuItemColorMapExteriorUseStriping = "menuItem.ColorMap.Exterior.UseStriping";
	private static final String kActionCommandMenuItemColorMapExteriorSetStripingDensity = "menuItem.ColorMap.Exterior.SetStripingDensity";
	private static final String kActionCommandMenuItemColorMapExteriorUseMinimumGaussianIntegersDistance = "menuItem.ColorMap.Exterior.UseMinimumGaussianIntegersDistance";
	private static final String kActionCommandMenuItemColorMapExteriorUseAverageGaussianIntegersDistance = "menuItem.ColorMap.Exterior.UseAverageGaussianIntegersDistance";
	private static final String kActionCommandMenuItemColorMapExteriorSetGaussianIntegersTrapFactor = "menuItem.ColorMap.Exterior.SetGaussianIntegersTrapFactor";
	private static final String kActionCommandMenuItemColorMapExteriorUseDiscreteRoots = "menuItem.ColorMap.Exterior.UseDiscreteRoots";
	private static final String kActionCommandMenuItemColorMapExteriorUseSmoothRoots = "menuItem.ColorMap.Exterior.UseSmoothRoots";
	private static final String kActionCommandMenuItemColorMapExteriorSetBrightnessFactor = "menuItem.ColorMap.Exterior.SetBrightnessFactor";

	private static final String kActionCommandMenuItemColorMapInteriorUseFixedColor = "menuItem.ColorMap.Interior.UseFixedColor";
	private static final String kActionCommandMenuItemColorMapInteriorSetFixedColor = "menuItem.ColorMap.Interior.SetFixedColor";
	private static final String kActionCommandMenuItemColorMapInteriorUseExponentiallySmoothedLevelSets = "menuItem.ColorMap.Interior.UseExponentiallySmoothedLevelSets";
	private static final String kActionCommandMenuItemColorMapInteriorUseSectorDecomposition = "menuItem.ColorMap.Interior.UseSectorDecomposition";
	private static final String kActionCommandMenuItemColorMapInteriorSetDecompositionSectorRange = "menuItem.ColorMap.Interior.SetDecompositionSectorRange";
	private static final String kActionCommandMenuItemColorMapInteriorUseRealComponent = "menuItem.ColorMap.Interior.UseRealComponent";
	private static final String kActionCommandMenuItemColorMapInteriorUseImaginaryComponent = "menuItem.ColorMap.Interior.UseImaginaryComponent";
	private static final String kActionCommandMenuItemColorMapInteriorUseModulus = "menuItem.ColorMap.Interior.UseModulus";
	private static final String kActionCommandMenuItemColorMapInteriorUseAngle = "menuItem.ColorMap.Interior.UseAngle";
	private static final String kActionCommandMenuItemColorMapInteriorUseMaxModulus = "menuItem.ColorMap.Interior.UseMaxModulus";
	private static final String kActionCommandMenuItemColorMapInteriorUseTotalDistance = "menuItem.ColorMap.Interior.UseTotalDistance";
	private static final String kActionCommandMenuItemColorMapInteriorUseAverageDistance = "menuItem.ColorMap.Interior.UseAverageDistance";
	private static final String kActionCommandMenuItemColorMapInteriorUseTotalAngle = "menuItem.ColorMap.Interior.UseTotalAngle";
	private static final String kActionCommandMenuItemColorMapInteriorUseLyapunovExponent = "menuItem.ColorMap.Interior.UseLyapunovExponent";
	private static final String kActionCommandMenuItemColorMapInteriorUseCurvature = "menuItem.ColorMap.Interior.UseCurvature";
	private static final String kActionCommandMenuItemColorMapInteriorUseStriping = "menuItem.ColorMap.Interior.UseStriping";
	private static final String kActionCommandMenuItemColorMapInteriorSetStripingDensity = "menuItem.ColorMap.Interior.SetStripingDensity";
	private static final String kActionCommandMenuItemColorMapInteriorUseMinimumGaussianIntegersDistance = "menuItem.ColorMap.Interior.UseMinimumGaussianIntegersDistance";
	private static final String kActionCommandMenuItemColorMapInteriorUseAverageGaussianIntegersDistance = "menuItem.ColorMap.Interior.UseAverageGaussianIntegersDistance";
	private static final String kActionCommandMenuItemColorMapInteriorSetGaussianIntegersTrapFactor = "menuItem.ColorMap.Interior.SetGaussianIntegersTrapFactor";

	private static final String kActionCommandMenuItemColorMapUseLinearScaling = "menuItem.ColorMap.UseLinearScaling";
	private static final String kActionCommandMenuItemColorMapUseLogarithmicScaling = "menuItem.ColorMap.UseLogarithmicScaling";
	private static final String kActionCommandMenuItemColorMapUseExponentialScaling = "menuItem.ColorMap.UseExponentialScaling";
	private static final String kActionCommandMenuItemColorMapUseSqrtScaling = "menuItem.ColorMap.UseSqrtScaling";
	private static final String kActionCommandMenuItemColorMapSetScalingParameters = "menuItem.ColorMap.SetScalingParameters";
	private static final String kActionCommandMenuItemColorMapUseRankOrderScaling = "menuItem.ColorMap.UseRankOrderScaling";
	private static final String kActionCommandMenuItemColorMapRestrictHighIterationCountColors = "menuItem.ColorMap.RestrictHighIterationCountColors";
	private static final String kActionCommandMenuItemColorMapUseBinaryDecomposition = "menuItem.ColorMap.UseBinaryDecomposition";
	private static final String kActionCommandMenuItemColorMapUseContours = "menuItem.ColorMap.UseContours";
	private static final String kActionCommandMenuItemColorMapUseDarkSofteningFilter = "menuItem.ColorMap.UseDarkSofteningFilter";
	private static final String kActionCommandMenuItemColorMapResetToDefault = "menuItem.ColorMap.ResetToDefault";
	private static final String kActionCommandMenuItemColorMapSetIterationRange = "menuItem.ColorMap.SetIterationRange";
	private static final String kActionCommandMenuItemColorMapRepeatColors = "menuItem.ColorMap.RepeatColors";
	private static final String kActionCommandMenuItemColorMapSetColorRepetition = "menuItem.ColorMap.SetColorRepetition";
	private static final String kActionCommandMenuItemColorMapSetColorOffset = "menuItem.ColorMap.SetColorOffset";
	private static final String kActionCommandMenuItemColorMapCycleColors = "menuItem.ColorMap.CycleColors";
	private static final String kActionCommandMenuItemColorMapSetColorCyclingParameters = "menuItem.ColorMap.SetColorCyclingParameters";
	private static final String kActionCommandMenuItemColorMapFullColorRange = "menuItem.ColorMap.FullColorRange";
	private static final String kActionCommandMenuItemColorMapUseLimitedContinuousColorRange = "menuItem.ColorMap.UseLimitedContinuousColorRange";
	private static final String kActionCommandMenuItemColorMapSetLimitedContinuousColorRange = "menuItem.ColorMap.SetLimitedContinuousColorRange";
	private static final String kActionCommandMenuItemColorMapUseLimitedDiscreteColorRange = "menuItem.ColorMap.UseLimitedDiscreteColorRange";
	private static final String kActionCommandMenuItemColorMapSetLimitedDiscreteColorRange = "menuItem.ColorMap.SetLimitedDiscreteColorRange";
	private static final String kActionCommandMenuItemColorMapUsePostProcessingFilters = "menuItem.ColorMap.UsePostProcessingFilters";
	private static final String kActionCommandMenuItemColorSetupPostProcessingFilters = "menuItem.ColorMap.SetupPostProcessingFilters";

	private static final String kActionCommandMenuItemMultithreadingRecalculate = "menuItem.MultiThreading.Recalculate";
	private static final String kActionCommandMenuItemMultithreadingSetNrOfCPUCoresToUse = "menuItem.MultiThreading.SetNrOfCPUCoresToUse";
	private static final String kActionCommandMenuItemMultithreadingSetNrOfBlocksToUse = "menuItem.MultiThreading.SetNrOfBlocksToUse";
	private static final String kActionCommandMenuItemMultithreadingProgressIndicatorBar = "menuItem.MultiThreading.ProgressIndicatorBar";
	private static final String kActionCommandMenuItemMultithreadingProgressIndicatorCircles = "menuItem.MultiThreading.ProgressIndicatorCircles";
	private static final String kActionCommandMenuItemMultithreadingProgressIndicatorFixedSector = "menuItem.MultiThreading.ProgressIndicatorFixedSector";
	private static final String kActionCommandMenuItemMultithreadingProgressIndicatorRotatingSector = "menuItem.MultiThreading.ProgressIndicatorRotatingSector";

	private static final String kActionCommandMenuItemHelpColoringSchemes = "menuItem.Help.ColoringSchemes";
	private static final String kActionCommandMenuItemHelpFractalTypes = "menuItem.Help.FractalTypes";
	private static final String kActionCommandMenuItemHelpGeneralInformation = "menuItem.Help.GeneralInformation";
	private static final String kActionCommandMenuItemHelpGettingStarted = "menuItem.Help.GettingStarted";
	private static final String kActionCommandMenuItemHelpKeyboardShortcuts = "menuItem.Help.KeyboardShortcuts";
	private static final String kActionCommandMenuItemHelpResizingForQualityPrinting = "menuItem.Help.ResizingForQualityPrinting";

	// the block increment for the scrollbars
	private static final int kScrollbarBlockIncrement = 100;

	// the minimum window size
	private static final Dimension kMinWindowSize = new Dimension(640,480);

	// the indentation for some menu items
	private static final String kMenuItemIndentation = "    ";

	// the maximum number of components for a custom colour map
	private static final int kNrOfCustomColorMapColors = 10;

	// the double-click mode
	private static enum EDoubleClickMode {kSwitchMainDualFractal, kChangeOrbitStartingPoint};

	// the colour cycling's initial parameters
	private static final int kInitialColorCyclingDelay = 10;
	private static final double kInitialColorCyclingSmoothness = 0.01;
	private static final boolean kInitialColorCyclingDirectionForward = true;

	// the help topics
	private static enum EHelpTopic {kGeneralInformation, kGettingStarted, kColoringSchemes, kResizingForQualityPrinting, kKeyboardShortcuts, kFractalTypes};

	// access point to the Log4j logging facility
	private static final Logger kLogger = Logger.getLogger(FraxionGUI.class.getName());

	// internal datastructures
	private IteratorController fIteratorController;
	private JScrollPane fFractalScrollPane;
	private FractalPanel fFractalPanel;
	private int fColorCyclingDelay;
	private double fColorCyclingSmoothness;
	private boolean fColorCyclingDirectionForward;
	private javax.swing.Timer fColorCyclingTimer;
	private JLabel fStatusBarCalculationTimeLabel;
	private JProgressUpdateGlassPane fProgressUpdateGlassPane;
	private HashMap<String,JMenuItem> fMenuItems;
	private ArrayList<String> fFractalFamilyMenuItems;
	private double fNavigationPanningSize;
	private ColorLabelDecorator fInteriorColorLabelDecorator;
	private ColorLabelDecorator fExteriorColorLabelDecorator;
	private ColorLabelDecorator fTigerStripeColorLabelDecorator;
	private EDoubleClickMode fDoubleClickMode;
	private HelpSet fHelpSet;
	private HelpBroker fHelpBroker;
	private Hashtable<EHelpTopic,javax.help.Map.ID> fHelpMapIDs;
	private String fLastOpenedFolder;

	/*************************
	 * STATIC INITIALISATION *
	 *************************/

	static {
		DevelopMode.deactivate();

		// hack for JDK7 and above
		System.setProperty("java.util.Arrays.useLegacyMergeSort","true");
	}

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>FraxionGUI</CODE> object.
	 *
	 * @param argv an array of strings containing the <B>command-line</B> parameters
	 */
	public FraxionGUI(String argv[])
	{
		super(argv,null);

		// post initalisation
		fMenuItems.get(kActionCommandMenuItemNavigationInvertYAxis).setSelected(fIteratorController.getFractalIterator().getInvertYAxis());
		adjustGUIToFractal();
		setupMarkusLyapunovFractal();
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * The application's entry point.
	 * 
	 * @param argv  the application's command-line arguments
	 */
	public static void main(String[] argv)
	{
		new FraxionGUI(argv);
	}

	// the action-listener
	/**
	 * See {@link JStandardGUIApplication}.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);

		// explicitly ignore input when we are in the middle of a calculation
		if (fIteratorController.isBusy()) {
			return;
		}

		String command = e.getActionCommand();

		/*****************
		 * MENU COMMANDS *
		 *****************/

		if (command.equalsIgnoreCase(kActionCommandMenuItemFileExportToPNG)) {
			JFileChooser fileChooser = new JFileChooser(fLastOpenedFolder);
			fileChooser.setDialogTitle(I18NL10N.translate("text.File.ExportToPNGTitle"));
			fileChooser.setFileFilter(new JFileFilter("PNG",I18NL10N.translate("text.File.PNGDescription")));
			fileChooser.setSelectedFile(new File(createDefaultFilename("png",false)));

			if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				String filename = fileChooser.getSelectedFile().getPath();
				fLastOpenedFolder = filename.substring(0,filename.lastIndexOf(File.separator));

				if (!filename.endsWith(".png")) {
					filename += ".png";
				}

				File file = new File(filename);
				boolean proceed = true;
				if (file.exists()) {
					proceed = JConfirmationDialog.confirm(this,I18NL10N.translate("text.File.OverwriteFile"));
				}
				if (proceed) {
					if (fFractalPanel.exportMainFractal(file.getPath())) {
						JMessageDialog.show(this,I18NL10N.translate("text.File.CurrentFractalExported"));
					}
					else {
						JWarningDialog.warn(this,I18NL10N.translate("error.File.ErrorExportingToPNG"));
					}
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFileLoadFractal)) {
			boolean proceed = true;
			if (fFractalPanel.getZoomStack().getZoomLevel() > 1) {
				proceed = JConfirmationDialog.confirm(this,I18NL10N.translate("text.Navigation.ZoomStack.OverwriteZoomStack"));
			}

			if (proceed) {
				JFileChooser fileChooser = new JFileChooser(fLastOpenedFolder);
				fileChooser.setDialogTitle(I18NL10N.translate("text.File.Fractal.Load"));
				fileChooser.setFileFilter(new JFileFilter("CSV",I18NL10N.translate("text.File.CSVDescription")));
				if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
					String filename = fileChooser.getSelectedFile().getPath();
					fLastOpenedFolder = filename.substring(0,filename.lastIndexOf(File.separator));

					FractalLoaderTask fractalLoaderTask = new FractalLoaderTask(filename,this);
					fractalLoaderTask.execute();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFileSaveFractal)) {
			JFileChooser fileChooser = new JFileChooser(fLastOpenedFolder);
			fileChooser.setDialogTitle(I18NL10N.translate("text.File.Fractal.Save"));
			fileChooser.setFileFilter(new JFileFilter("CSV",I18NL10N.translate("text.File.CSVDescription")));
			fileChooser.setSelectedFile(new File(createDefaultFilename("csv",false)));

			if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				String filename = fileChooser.getSelectedFile().getPath();
				fLastOpenedFolder = filename.substring(0,filename.lastIndexOf(File.separator));

				if (!filename.endsWith(".csv")) {
					filename += ".csv";
				}

				File file = new File(filename);
				boolean proceed = true;
				if (file.exists()) {
					proceed = JConfirmationDialog.confirm(this,I18NL10N.translate("text.File.OverwriteFile"));
				}
				if (proceed) {
					FractalSaverTask fractalSaverTask = new FractalSaverTask(filename,this);
					fractalSaverTask.execute();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFileLoadFractalParameters)) {
			boolean proceed = true;
			if (fFractalPanel.getZoomStack().getZoomLevel() > 1) {
				proceed = JConfirmationDialog.confirm(this,I18NL10N.translate("text.Navigation.ZoomStack.OverwriteZoomStack"));
			}

			if (proceed) {
				JFileChooser fileChooser = new JFileChooser(fLastOpenedFolder);
				fileChooser.setDialogTitle(I18NL10N.translate("text.File.FractalParameters.Load"));
				fileChooser.setFileFilter(new JFileFilter("CSV",I18NL10N.translate("text.File.CSVDescription")));
				if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
					String filename = fileChooser.getSelectedFile().getPath();
					fLastOpenedFolder = filename.substring(0,filename.lastIndexOf(File.separator));

					try {
						TextFileParser tfp = new TextFileParser(filename);

						// load fractal family name
						String familyName = tfp.getNextString();

						// create fractal
						if (familyName.equalsIgnoreCase((new FastMandelbrotJuliaFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new FastMandelbrotJuliaFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new MandelbarFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new MandelbarFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new RandelbrotFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new RandelbrotFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new OriginalJuliaFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new OriginalJuliaFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new LambdaFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new LambdaFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new InverseLambdaFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new InverseLambdaFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new BurningShipFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new BurningShipFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new BirdOfPreyFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new BirdOfPreyFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new GlynnFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new GlynnFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new SpiderFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new SpiderFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new MultibrotFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new MultibrotFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new MultibrotPolynomialFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new MultibrotPolynomialFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new MultibrotParameterFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new MultibrotParameterFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new MultibrotInvertedParameterFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new MultibrotInvertedParameterFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new MultibarFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new MultibarFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new MultibarPolynomialFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new MultibarPolynomialFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new MultibarParameterFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new MultibarParameterFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new MultibarInvertedParameterFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new MultibarInvertedParameterFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new BurningMultiShipFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new BurningMultiShipFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new MultiProductExpelbrotFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new MultiProductExpelbrotFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new MultiSumExpelbrotFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new MultiSumExpelbrotFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new MultiProductExparbrotFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new MultiProductExparbrotFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new MultiSumExparbrotFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new MultiSumExparbrotFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new TrigonometricPowerSineFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new TrigonometricPowerSineFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new TrigonometricPowerCosineFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new TrigonometricPowerCosineFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new TrigonometricPowerTangentFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new TrigonometricPowerTangentFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new TrigonometricPowerCotangentFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new TrigonometricPowerCotangentFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new TrigonometricPowerMultiSineFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new TrigonometricPowerMultiSineFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new TrigonometricPowerMultiCosineFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new TrigonometricPowerMultiCosineFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new TrigonometricPowerMultiTangentFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new TrigonometricPowerMultiTangentFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new TrigonometricPowerMultiCotangentFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new TrigonometricPowerMultiCotangentFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new CactusFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new CactusFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new Beauty1FractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new Beauty1FractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new Beauty2FractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new Beauty2FractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new DucksFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new DucksFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new BarnsleyTreeFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new BarnsleyTreeFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new CollatzFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new CollatzFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new PhoenixFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new PhoenixFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new ManowarFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new ManowarFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new NewtonRaphsonPowerFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new NewtonRaphsonPowerFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new NewtonRaphsonPowerPolynomialFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new NewtonRaphsonPowerPolynomialFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new NewtonRaphsonFixedPolynomial1FractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new NewtonRaphsonFixedPolynomial1FractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new NewtonRaphsonFixedPolynomial2FractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new NewtonRaphsonFixedPolynomial2FractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new NewtonRaphsonFixedPolynomial3FractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new NewtonRaphsonFixedPolynomial3FractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new NewtonRaphsonFixedPolynomial4FractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new NewtonRaphsonFixedPolynomial4FractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new NewtonRaphsonTrigonometricPowerSineFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new NewtonRaphsonTrigonometricPowerSineFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new NewtonRaphsonTrigonometricPowerMultiSineFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new NewtonRaphsonTrigonometricPowerMultiSineFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new NewtonRaphsonTrigonometricPowerSineOffsetFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new NewtonRaphsonTrigonometricPowerSineOffsetFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new NewtonRaphsonTrigonometricPowerMultiSineOffsetFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new NewtonRaphsonTrigonometricPowerMultiSineOffsetFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new NovaFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new NovaFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new MagnetTypeIFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new MagnetTypeIFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new MagnetTypeIIFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new MagnetTypeIIFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new MarkusLyapunovFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new MarkusLyapunovFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new MarkusLyapunovLogisticBifurcationFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new MarkusLyapunovLogisticBifurcationFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new MarkusLyapunovJellyfishFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new MarkusLyapunovJellyfishFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new MarkusLyapunovZirconZityFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new MarkusLyapunovZirconZityFractalIterator());
						}
						else {
							throw (new UnsupportedFractalException(filename,familyName));
						}

						// if necessary switch to the dual fractal
						AFractalIterator fractalIterator = fIteratorController.getFractalIterator();
						if ((fractalIterator instanceof GlynnFractalIterator) ||
								(fractalIterator instanceof BarnsleyTreeFractalIterator) ||
								(fractalIterator instanceof PhoenixFractalIterator)) {
							fIteratorController.getFractalIterator().setFractalType(AFractalIterator.EFractalType.kDualFractal);
						}

						// load fractal parameters
						fIteratorController.getFractalIterator().loadParameters(tfp);

						// load fractal colouring parameters
						fFractalPanel.getColoringParameters().load(tfp);

						// adjust the zoom stack
						fFractalPanel.getZoomStack().clear();
						fFractalPanel.getZoomStack().push(fIteratorController.getFractalIterator().getDefaultP1(),fIteratorController.getFractalIterator().getDefaultP2());
						fFractalPanel.getZoomStack().push(fIteratorController.getFractalIterator().getP1(),fIteratorController.getFractalIterator().getP2());

						// adjust canvas dimensions
						fFractalPanel.revalidate();
						fIteratorController.recalc();

						adjustGUIToFractal();
					}
					catch (FileDoesNotExistException exc) {
						JWarningDialog.warn(this,I18NL10N.translate("error.File.FractalParameters.ErrorLoadingFractalParameters"));
					}
					catch (FileParseException exc) {
						JWarningDialog.warn(this,I18NL10N.translate("error.File.FractalParameters.ErrorParsingFractalParameters",String.valueOf(((FileParseException) exc).getLineNr()),((FileParseException) exc).getValue()));
					}
					catch (UnsupportedFractalException exc) {
						JWarningDialog.warn(this,I18NL10N.translate("error.File.FractalParameters.UnsupportedFractalParameters",((UnsupportedFractalException) exc).getFamilyName()));
					}
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFileSaveFractalParameters)) {
			JFileChooser fileChooser = new JFileChooser(fLastOpenedFolder);
			fileChooser.setDialogTitle(I18NL10N.translate("text.File.FractalParameters.Save"));
			fileChooser.setFileFilter(new JFileFilter("CSV",I18NL10N.translate("text.File.CSVDescription")));
			fileChooser.setSelectedFile(new File(createDefaultFilename("csv",true)));

			if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				String filename = fileChooser.getSelectedFile().getPath();
				fLastOpenedFolder = filename.substring(0,filename.lastIndexOf(File.separator));

				if (!filename.endsWith(".csv")) {
					filename += ".csv";
				}

				File file = new File(filename);
				boolean proceed = true;
				if (file.exists()) {
					proceed = JConfirmationDialog.confirm(this,I18NL10N.translate("text.File.OverwriteFile"));
				}
				if (proceed) {
					try {
						TextFileWriter tfw = new TextFileWriter(filename);

						// save fractal parameters
						fIteratorController.getFractalIterator().saveParameters(tfw);

						// save fractal colouring parameters
						fFractalPanel.getColoringParameters().save(tfw);

						JMessageDialog.show(this,I18NL10N.translate("text.File.FractalParameters.Saved"));
					}
					catch (FileCantBeCreatedException | FileWriteException exc) {
						JWarningDialog.warn(this,I18NL10N.translate("error.File.FractalParameters.ErrorSavingFractalParameters"));
					}
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFileLoadZoomStack)) {
			JFileChooser fileChooser = new JFileChooser(fLastOpenedFolder);
			fileChooser.setDialogTitle(I18NL10N.translate("text.File.ZoomStack.Load"));
			fileChooser.setFileFilter(new JFileFilter("CSV",I18NL10N.translate("text.File.CSVDescription")));
			if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				String filename = fileChooser.getSelectedFile().getPath();
				fLastOpenedFolder = filename.substring(0,filename.lastIndexOf(File.separator));

				// retain the current zoom stack
				ZoomStack currentZoomStack = fFractalPanel.getZoomStack();
				ZoomStack previousZoomStack = (ZoomStack) currentZoomStack.clone();

				try {
					currentZoomStack.load(filename);
			 		if (fFractalPanel.getZoomStack().isEmpty()) {
						// initialise zoom
						fFractalPanel.resetZoom();
					}
					else {
						// adjust zoom
						fFractalPanel.zoomToStack();
					}
				}
				catch (FileDoesNotExistException exc) {
					JWarningDialog.warn(this,I18NL10N.translate("error.File.ZoomStack.ErrorLoadingZoomStack"));
					fFractalPanel.setZoomStack(previousZoomStack);
				}
				catch (FileParseException exc) {
					JWarningDialog.warn(this,I18NL10N.translate("error.File.ZoomStack.ErrorParsingZoomStack",String.valueOf(exc.getLineNr()),exc.getValue()));
					fFractalPanel.setZoomStack(previousZoomStack);
				}
				catch (NumberFormatException exc) {
					JWarningDialog.warn(this,I18NL10N.translate("error.File.ZoomStack.ErrorParsingZoomStackComponent",String.valueOf(exc.getMessage())));
					fFractalPanel.setZoomStack(previousZoomStack);
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFileSaveZoomStack)) {
			JFileChooser fileChooser = new JFileChooser(fLastOpenedFolder);
			fileChooser.setDialogTitle(I18NL10N.translate("text.File.ZoomStack.Save"));
			fileChooser.setFileFilter(new JFileFilter("CSV",I18NL10N.translate("text.File.CSVDescription")));
			if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				String filename = fileChooser.getSelectedFile().getPath();
				fLastOpenedFolder = filename.substring(0,filename.lastIndexOf(File.separator));

				if (!filename.endsWith(".csv")) {
					filename += ".csv";
				}

				File file = new File(filename);
				boolean proceed = true;
				if (file.exists()) {
					proceed = JConfirmationDialog.confirm(this,I18NL10N.translate("text.File.OverwriteFile"));
				}
				if (proceed) {
					try {
						fFractalPanel.getZoomStack().save(filename);
						JMessageDialog.show(this,I18NL10N.translate("text.File.ZoomStack.Saved"));
					}
					catch (FileCantBeCreatedException exc) {
						JWarningDialog.warn(this,I18NL10N.translate("error.File.ZoomStack.ErrorSavingZoomStack"));
					}
					catch (FileWriteException exc) {
						JWarningDialog.warn(this,I18NL10N.translate("error.File.ZoomStack.ErrorSavingZoomStack"));
					}
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFileSaveZoomAnimationSequence)) {
//XXX
			JIncompleteWarningDialog.warn(this,"GUIApplication::actionPerformed()");
			// resetZoom();
			// push new coords(); => zoomToStack()
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationPanLeft)) {
			fFractalPanel.pan(FractalPanel.EPanDirection.kLeft,fNavigationPanningSize,fMenuItems.get(kActionCommandMenuItemNavigationInvertPanningDirections).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationPanRight)) {
			fFractalPanel.pan(FractalPanel.EPanDirection.kRight,fNavigationPanningSize,fMenuItems.get(kActionCommandMenuItemNavigationInvertPanningDirections).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationPanUp)) {
			fFractalPanel.pan(FractalPanel.EPanDirection.kUp,fNavigationPanningSize,fMenuItems.get(kActionCommandMenuItemNavigationInvertPanningDirections).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationPanDown)) {
			fFractalPanel.pan(FractalPanel.EPanDirection.kDown,fNavigationPanningSize,fMenuItems.get(kActionCommandMenuItemNavigationInvertPanningDirections).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationSetPanningSize)) {
			NavigationPanningSizeChooser navigationPanningSizeChooser = new NavigationPanningSizeChooser(this,fNavigationPanningSize);
			if (!navigationPanningSizeChooser.isCancelled()) {
				fNavigationPanningSize = navigationPanningSizeChooser.getSelectedPanningSize();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationInvertPanningDirections)) {
			// ignore (this is handled via the KeyListener)
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationShowZoomInformation)) {
			fFractalPanel.setShowZoomInformation(fMenuItems.get(kActionCommandMenuItemNavigationShowZoomInformation).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationLockAspectRatio)) {
			fFractalPanel.setLockAspectRatio(fMenuItems.get(kActionCommandMenuItemNavigationLockAspectRatio).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationCentredZooming)) {
			fFractalPanel.setCentredZooming(fMenuItems.get(kActionCommandMenuItemNavigationCentredZooming).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationResetZoom)) {
			fFractalPanel.resetZoom();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationZoomToLevel)) {
			ZoomLevelChooser zoomLevelChooser = new ZoomLevelChooser(this,fFractalPanel.getZoomStack());
			if (!zoomLevelChooser.isCancelled()) {
				fFractalPanel.zoomToLevel(zoomLevelChooser.getSelectedZoomLevel());
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationShowAxes)) {
			fFractalPanel.setShowAxes(fMenuItems.get(kActionCommandMenuItemNavigationShowAxes).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationInvertYAxis)) {
			fIteratorController.getFractalIterator().setInvertYAxis(fMenuItems.get(kActionCommandMenuItemNavigationInvertYAxis).isSelected());
			fIteratorController.recalc();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationShowCurrentLocation)) {
			fFractalPanel.setShowCurrentLocation(fMenuItems.get(kActionCommandMenuItemNavigationShowCurrentLocation).isSelected());
			changeLocationMouseCursor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationShowMagnifyingGlass)) {
			fFractalPanel.setShowMagnifyingGlass(fMenuItems.get(kActionCommandMenuItemNavigationShowMagnifyingGlass).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationSetMagnifyingGlassSize)) {
			MagnifyingGlassSizeChooser magnifyingGlassSizeChooser = new MagnifyingGlassSizeChooser(this,fFractalPanel.getMagnifyingGlassRegion(),fFractalPanel.getMagnifyingGlassSize());
			if (!magnifyingGlassSizeChooser.isCancelled()) {
				fFractalPanel.setMagnifyingGlassSize(magnifyingGlassSizeChooser.getSelectedRegion(),magnifyingGlassSizeChooser.getSelectedSize());
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationSpecifiyScreenBounds)) {
			ScreenBoundsChooser screenBoundsChooser = new ScreenBoundsChooser(this,
				fIteratorController.getFractalIterator().getScreenWidth(),
				fIteratorController.getFractalIterator().getScreenHeight(),
				getWidth(),getHeight(),
				getScreenInsets(),getInsets(),fFractalScrollPane.getInsets(),
				fFractalScrollPane.getVerticalScrollBar().getPreferredSize().width,
				fFractalScrollPane.getHorizontalScrollBar().getPreferredSize().height);
			if (!screenBoundsChooser.isCancelled()) {
				if (!screenBoundsChooser.isProjectedMemoryUsageAvailable()) {
					JWarningDialog.warn(this,I18NL10N.translate("error.NotEnoughMemoryAvailable"));
				}
				else {
					int newWidth = screenBoundsChooser.getSelectedScreenWidth();
					int newHeight = screenBoundsChooser.getSelectedScreenHeight();
					fIteratorController.getFractalIterator().setScreenBounds(newWidth,newHeight);
					fFractalPanel.revalidate();
					fFractalPanel.zoomToStack(newWidth,newHeight);
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationSpecifyCoordinates)) {
			ComplexNumber p1 = fFractalPanel.getZoomStack().getTopP1();
			ComplexNumber p2 = fFractalPanel.getZoomStack().getTopP2();
			ComplexNumber centerOrigin = new ComplexNumber(
				(p1.realComponent() + p2.realComponent()) / 2.0,
				(p1.imaginaryComponent() + p2.imaginaryComponent()) / 2.0);
			double centerRadiusX = (p2.realComponent() - p1.realComponent()) / 2.0;
			double centerRadiusY = (p2.imaginaryComponent() - p1.imaginaryComponent()) / 2.0;
			double centerRadius = centerRadiusX;
			if (centerRadiusY > centerRadiusX) {
				centerRadius = centerRadiusY;
			}

			ComplexBoundsChooser complexBoundsChooser = new ComplexBoundsChooser(this,centerOrigin,centerRadius,p1,p2);
			if (!complexBoundsChooser.isCancelled()) {
				boolean proceed = true;
				if (fFractalPanel.getZoomStack().getZoomLevel() > 1) {
					proceed = JConfirmationDialog.confirm(this,I18NL10N.translate("text.Navigation.ZoomStack.OverwriteZoomStack"));
				}

				if (proceed) {
					// reset zoomstack and create new top
					fFractalPanel.getZoomStack().clear();
					fFractalPanel.getZoomStack().push(fIteratorController.getFractalIterator().getDefaultP1(),fIteratorController.getFractalIterator().getDefaultP2());
					fFractalPanel.zoomIn(complexBoundsChooser.getSelectedP1(),complexBoundsChooser.getSelectedP2());
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalDoubleClickModeSwitchDualMainFractal)) {
			fDoubleClickMode = EDoubleClickMode.kSwitchMainDualFractal;
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalSwitchFractalType)) {
			fFractalPanel.switchMainDualFractal();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalDoubleClickModeSetOrbitStartingPoint)) {
			fDoubleClickMode = EDoubleClickMode.kChangeOrbitStartingPoint;
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalResetOrbitStartingPoint)) {
			fIteratorController.getFractalIterator().resetMainFractalOrbitStartingPoint();
			fIteratorController.recalc();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalShowInset)) {
			fFractalPanel.setShowInset(fMenuItems.get(kActionCommandMenuItemFractalShowInset).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalAutoZoomInset)) {
			fFractalPanel.setAutoZoomInset(fMenuItems.get(kActionCommandMenuItemFractalAutoZoomInset).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalSetInsetSize)) {
			InsetSizeChooser insetSizeChooser = new InsetSizeChooser(this,fFractalPanel.getInsetSize());
			if (!insetSizeChooser.isCancelled()) {
				fFractalPanel.setInsetSize(insetSizeChooser.getSelectedInsetSize());
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalInsetFractalIsDeformedMainFractal)) {
			fFractalPanel.setDeformedMainfractal(fMenuItems.get(kActionCommandMenuItemFractalInsetFractalIsDeformedMainFractal).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalShowOrbits)) {
			fFractalPanel.setShowOrbits(fMenuItems.get(kActionCommandMenuItemFractalShowOrbits).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalShowOrbitPaths)) {
			fFractalPanel.setShowOrbitPaths(fMenuItems.get(kActionCommandMenuItemFractalShowOrbitPaths).isSelected());
			// also show orbits simultaneously
			if (fMenuItems.get(kActionCommandMenuItemFractalShowOrbitPaths).isSelected()) {
				fMenuItems.get(kActionCommandMenuItemFractalShowOrbits).setSelected(true);
				fFractalPanel.setShowOrbits(true);
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalScaleOrbitsToScreen)) {
			fFractalPanel.setScaleOrbitsToScreen(fMenuItems.get(kActionCommandMenuItemFractalScaleOrbitsToScreen).isSelected());
			// also show orbits simultaneously
			if (fMenuItems.get(kActionCommandMenuItemFractalScaleOrbitsToScreen).isSelected()) {
				fMenuItems.get(kActionCommandMenuItemFractalShowOrbits).setSelected(true);
				fFractalPanel.setShowOrbits(true);
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalShowOrbitAnalyses)) {
			fFractalPanel.setShowOrbitAnalyses(fMenuItems.get(kActionCommandMenuItemFractalShowOrbitAnalyses).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalShowIterationDistribution)) {
			fIteratorController.setEstimatePDF(fMenuItems.get(kActionCommandMenuItemFractalShowIterationDistribution).isSelected());
			// also show orbit analyses simultaneously
			if (fMenuItems.get(kActionCommandMenuItemFractalShowIterationDistribution).isSelected()) {
				fMenuItems.get(kActionCommandMenuItemFractalShowOrbitAnalyses).setSelected(true);
				fFractalPanel.setShowOrbitAnalyses(true);
				fIteratorController.recalc();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalSetOrbitAnalysesPanelSize)) {
			OrbitAnalysesPanelSizeChooser orbitAnalysesPanelSizeChooser = new OrbitAnalysesPanelSizeChooser(this,fFractalPanel.getOrbitAnalysesPanelSize());
			if (!orbitAnalysesPanelSizeChooser.isCancelled()) {
				fFractalPanel.setOrbitAnalysesPanelSize(orbitAnalysesPanelSizeChooser.getSelectedOrbitAnalysesPanelSize());
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalSetMaxNrOfIterationsInOrbitAnalyses)) {
			MaxNrOfIterationsInOrbitAnalysesChooser maxNrOfIterationsInOrbitAnalysesChooser = new MaxNrOfIterationsInOrbitAnalysesChooser(this,fFractalPanel.getMaxNrOfIterationsInOrbitAnalyses(),fIteratorController.getFractalIterator().getMaxNrOfIterations());
			if (!maxNrOfIterationsInOrbitAnalysesChooser.isCancelled()) {
				fFractalPanel.setMaxNrOfIterationsInOrbitAnalyses(maxNrOfIterationsInOrbitAnalysesChooser.getSelectedMaxNrOfIterationsInOrbitAnalyses());
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyDefaultMandelbrotJulia) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMandelbar) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyRandelbrot) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyOriginalJulia) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyLambda) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyInverseLambda) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyBurningShip) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyBirdOfPrey) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyGlynn) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilySpider) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibrot) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibrotPolynomial) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibrotParameter) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibrotInvertedParameter) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibar) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibarPolynomial) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibarParameter) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibarInvertedParameter) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyBurningMultiShip) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultiProductExpelbrot) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultiSumExpelbrot) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultiProductExparbrot) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultiSumExparbrot) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerSine) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerCosine) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerTangent) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerCotangent) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiSine) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCosine) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiTangent) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCotangent) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyCactus) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyBeauty1) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyBeauty2) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyDucks) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyBarnsleyTree) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyCollatz) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyPhoenix) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyManowar) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonPower) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonPowerPolynomial) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial1) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial2) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial3) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial4) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSine) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSine) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSineOffset) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSineOffset) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNova) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMagnetTypeI) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMagnetTypeII) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMarkusLyapunov) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMarkusLyapunovLogisticBifurcation) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMarkusLyapunovJellyfish) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMarkusLyapunovZirconZity)) {
			boolean proceed = true;
			if (fFractalPanel.getZoomStack().getZoomLevel() > 1) {
				proceed = JConfirmationDialog.confirm(this,I18NL10N.translate("text.Navigation.ZoomStack.OverwriteZoomStack"));
			}

			if (proceed) {
				// obtain current screen bounds
				Dimension screenBounds = fIteratorController.getFractalIterator().getScreenBounds();

				switch (command) {
					case kActionCommandMenuItemFractalFamilyDefaultMandelbrotJulia:
						fIteratorController.setFractalIteratorFamily(new FastMandelbrotJuliaFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyMandelbar:
						fIteratorController.setFractalIteratorFamily(new MandelbarFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyRandelbrot:
						fIteratorController.setFractalIteratorFamily(new RandelbrotFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyOriginalJulia:
						fIteratorController.setFractalIteratorFamily(new OriginalJuliaFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyLambda:
						fIteratorController.setFractalIteratorFamily(new LambdaFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyInverseLambda:
						fIteratorController.setFractalIteratorFamily(new InverseLambdaFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyBurningShip:
						fIteratorController.setFractalIteratorFamily(new BurningShipFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyBirdOfPrey:
						fIteratorController.setFractalIteratorFamily(new BirdOfPreyFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyGlynn:
						fIteratorController.setFractalIteratorFamily(new GlynnFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilySpider:
						fIteratorController.setFractalIteratorFamily(new SpiderFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyMultibrot:
						fIteratorController.setFractalIteratorFamily(new MultibrotFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyMultibrotPolynomial:
						fIteratorController.setFractalIteratorFamily(new MultibrotPolynomialFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyMultibrotParameter:
						fIteratorController.setFractalIteratorFamily(new MultibrotParameterFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyMultibrotInvertedParameter:
						fIteratorController.setFractalIteratorFamily(new MultibrotInvertedParameterFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyMultibar:
						fIteratorController.setFractalIteratorFamily(new MultibarFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyMultibarPolynomial:
						fIteratorController.setFractalIteratorFamily(new MultibarPolynomialFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyMultibarParameter:
						fIteratorController.setFractalIteratorFamily(new MultibarParameterFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyMultibarInvertedParameter:
						fIteratorController.setFractalIteratorFamily(new MultibarInvertedParameterFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyBurningMultiShip:
						fIteratorController.setFractalIteratorFamily(new BurningMultiShipFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyMultiProductExpelbrot:
						fIteratorController.setFractalIteratorFamily(new MultiProductExpelbrotFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyMultiSumExpelbrot:
						fIteratorController.setFractalIteratorFamily(new MultiSumExpelbrotFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyMultiProductExparbrot:
						fIteratorController.setFractalIteratorFamily(new MultiProductExparbrotFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyMultiSumExparbrot:
						fIteratorController.setFractalIteratorFamily(new MultiSumExparbrotFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyTrigonometricPowerSine:
						fIteratorController.setFractalIteratorFamily(new TrigonometricPowerSineFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyTrigonometricPowerCosine:
						fIteratorController.setFractalIteratorFamily(new TrigonometricPowerCosineFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyTrigonometricPowerTangent:
						fIteratorController.setFractalIteratorFamily(new TrigonometricPowerTangentFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyTrigonometricPowerCotangent:
						fIteratorController.setFractalIteratorFamily(new TrigonometricPowerCotangentFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiSine:
						fIteratorController.setFractalIteratorFamily(new TrigonometricPowerMultiSineFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCosine:
						fIteratorController.setFractalIteratorFamily(new TrigonometricPowerMultiCosineFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiTangent:
						fIteratorController.setFractalIteratorFamily(new TrigonometricPowerMultiTangentFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCotangent:
						fIteratorController.setFractalIteratorFamily(new TrigonometricPowerMultiCotangentFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyCactus:
						fIteratorController.setFractalIteratorFamily(new CactusFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyBeauty1:
						fIteratorController.setFractalIteratorFamily(new Beauty1FractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyBeauty2:
						fIteratorController.setFractalIteratorFamily(new Beauty2FractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyDucks:
						fIteratorController.setFractalIteratorFamily(new DucksFractalIterator());
						fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kAverageDistance);
						fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseAverageDistance).setSelected(true);
						break;
					case kActionCommandMenuItemFractalFamilyBarnsleyTree:
						fIteratorController.setFractalIteratorFamily(new BarnsleyTreeFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyCollatz:
						fIteratorController.setFractalIteratorFamily(new CollatzFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyPhoenix:
						fIteratorController.setFractalIteratorFamily(new PhoenixFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyManowar:
						fIteratorController.setFractalIteratorFamily(new ManowarFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyNewtonRaphsonPower:
						fIteratorController.setFractalIteratorFamily(new NewtonRaphsonPowerFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyNewtonRaphsonPowerPolynomial:
						fIteratorController.setFractalIteratorFamily(new NewtonRaphsonPowerPolynomialFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial1:
						fIteratorController.setFractalIteratorFamily(new NewtonRaphsonFixedPolynomial1FractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial2:
						fIteratorController.setFractalIteratorFamily(new NewtonRaphsonFixedPolynomial2FractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial3:
						fIteratorController.setFractalIteratorFamily(new NewtonRaphsonFixedPolynomial3FractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial4:
						fIteratorController.setFractalIteratorFamily(new NewtonRaphsonFixedPolynomial4FractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSine:
						fIteratorController.setFractalIteratorFamily(new NewtonRaphsonTrigonometricPowerSineFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSine:
						fIteratorController.setFractalIteratorFamily(new NewtonRaphsonTrigonometricPowerMultiSineFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSineOffset:
						fIteratorController.setFractalIteratorFamily(new NewtonRaphsonTrigonometricPowerSineOffsetFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSineOffset:
						fIteratorController.setFractalIteratorFamily(new NewtonRaphsonTrigonometricPowerMultiSineOffsetFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyNova:
						fIteratorController.setFractalIteratorFamily(new NovaFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyMagnetTypeI:
						fIteratorController.setFractalIteratorFamily(new MagnetTypeIFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyMagnetTypeII:
						fIteratorController.setFractalIteratorFamily(new MagnetTypeIIFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyMarkusLyapunov:
						fIteratorController.setFractalIteratorFamily(new MarkusLyapunovFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyMarkusLyapunovLogisticBifurcation:
						fIteratorController.setFractalIteratorFamily(new MarkusLyapunovLogisticBifurcationFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyMarkusLyapunovJellyfish:
						fIteratorController.setFractalIteratorFamily(new MarkusLyapunovJellyfishFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyMarkusLyapunovZirconZity:
						fIteratorController.setFractalIteratorFamily(new MarkusLyapunovZirconZityFractalIterator());
						break;
				}

				AFractalIterator fractalIterator = fIteratorController.getFractalIterator();

				adjustGUIToFractal();
				setupMarkusLyapunovFractal();

				// if necessary switch to the dual fractal
				if ((fractalIterator instanceof GlynnFractalIterator) ||
						(fractalIterator instanceof BarnsleyTreeFractalIterator) ||
						(fractalIterator instanceof PhoenixFractalIterator)) {
					fIteratorController.getFractalIterator().setFractalType(AFractalIterator.EFractalType.kDualFractal);
				}

				// reset zoomstack and create new top
				fFractalPanel.getZoomStack().clear();
				fIteratorController.getFractalIterator().setScreenBounds(screenBounds);
				fFractalPanel.zoomIn(fIteratorController.getFractalIterator().getDefaultP1(),fIteratorController.getFractalIterator().getDefaultP2());
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyRandelbrotSetNoiseLevel)) {
			if (fIteratorController.getFractalIterator() instanceof RandelbrotFractalIterator) {
				NoiseLevelChooser noiseLevelChooser = new NoiseLevelChooser(this,((RandelbrotFractalIterator) fIteratorController.getFractalIterator()).getNoiseLevel());
				if (!noiseLevelChooser.isCancelled()) {
					double noiseLevel = noiseLevelChooser.getSelectedNoiseLevel();
					((RandelbrotFractalIterator) (fIteratorController.getFractalIterator())).setNoiseLevel(noiseLevel);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyGlynnSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof GlynnFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((GlynnFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((GlynnFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibrotSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof MultibrotFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultibrotFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultibrotFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibrotPolynomialSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof MultibrotPolynomialFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultibrotPolynomialFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultibrotPolynomialFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibrotParameterSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof MultibrotParameterFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultibrotParameterFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultibrotParameterFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibrotInvertedParameterSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof MultibrotInvertedParameterFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultibrotInvertedParameterFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultibrotInvertedParameterFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibarSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof MultibarFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultibarFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultibarFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibarPolynomialSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof MultibarPolynomialFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultibarPolynomialFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultibarPolynomialFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibarParameterSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof MultibarParameterFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultibarParameterFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultibarParameterFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibarInvertedParameterSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof MultibarInvertedParameterFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultibarInvertedParameterFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultibarInvertedParameterFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyBurningMultiShipSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof BurningMultiShipFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((BurningMultiShipFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((BurningMultiShipFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultiProductExpelbrotSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof MultiProductExpelbrotFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultiProductExpelbrotFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultiProductExpelbrotFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultiSumExpelbrotSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof MultiSumExpelbrotFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultiSumExpelbrotFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultiSumExpelbrotFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultiProductExparbrotSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof MultiProductExparbrotFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultiProductExparbrotFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultiProductExparbrotFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultiSumExparbrotSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof MultiSumExparbrotFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultiSumExparbrotFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultiSumExparbrotFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerSineSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof TrigonometricPowerSineFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((TrigonometricPowerSineFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((TrigonometricPowerSineFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerCosineSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof TrigonometricPowerCosineFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((TrigonometricPowerCosineFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((TrigonometricPowerCosineFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerTangentSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof TrigonometricPowerTangentFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((TrigonometricPowerTangentFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((TrigonometricPowerTangentFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerCotangentSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof TrigonometricPowerCotangentFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((TrigonometricPowerCotangentFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((TrigonometricPowerCotangentFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiSineSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof TrigonometricPowerMultiSineFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((TrigonometricPowerMultiSineFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((TrigonometricPowerMultiSineFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCosineSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof TrigonometricPowerMultiCosineFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((TrigonometricPowerMultiCosineFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((TrigonometricPowerMultiCosineFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiTangentSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof TrigonometricPowerMultiTangentFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((TrigonometricPowerMultiTangentFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((TrigonometricPowerMultiTangentFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCotangentSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof TrigonometricPowerMultiCotangentFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((TrigonometricPowerMultiCotangentFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((TrigonometricPowerMultiCotangentFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyDucksSetFixedNrOfIterations)) {
			if (fIteratorController.getFractalIterator() instanceof DucksFractalIterator) {
				FixedNrOfIterationsChooser fixedNrOfIterationsChooser = new FixedNrOfIterationsChooser(this,fIteratorController.getFractalIterator().getFixedNrOfIterations());
				if (!fixedNrOfIterationsChooser.isCancelled()) {
					int fixedNrOfIterations = fixedNrOfIterationsChooser.getSelectedFixedNrOfIterations();
					fIteratorController.getFractalIterator().setFixedNrOfIterations(fixedNrOfIterations);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonSetConvergenceParameters)) {
			AFractalIterator fractalIterator = fIteratorController.getFractalIterator();
			if (fractalIterator instanceof NovaFractalIterator) {
				ConvergenceParametersChooser convergenceParametersChooser = new ConvergenceParametersChooser(this,
					((AConvergentFractalIterator) fractalIterator).getRootTolerance(),
					((AConvergentFractalIterator) fractalIterator).getAlpha());
				if (!convergenceParametersChooser.isCancelled()) {
					double rootTolerance = convergenceParametersChooser.getSelectedRootTolerance();
					ComplexNumber alpha = convergenceParametersChooser.getSelectedAlpha();
					((AConvergentFractalIterator) fractalIterator).setRootTolerance(rootTolerance);
					((AConvergentFractalIterator) fractalIterator).setAlpha(alpha);
					fIteratorController.recalc();
				}
			}
			else if (fractalIterator instanceof AConvergentFractalIterator) {
				ConvergenceParametersChooser convergenceParametersChooser = new ConvergenceParametersChooser(this,
					((AConvergentFractalIterator) fractalIterator).getDerivativeDelta(),
					((AConvergentFractalIterator) fractalIterator).getRootTolerance(),
					((AConvergentFractalIterator) fractalIterator).getAlpha());
				if (!convergenceParametersChooser.isCancelled()) {
					double derivativeDelta = convergenceParametersChooser.getSelectedDerivativeDelta();
					double rootTolerance = convergenceParametersChooser.getSelectedRootTolerance();
					ComplexNumber alpha = convergenceParametersChooser.getSelectedAlpha();
					((AConvergentFractalIterator) fractalIterator).setDerivativeDelta(derivativeDelta);
					((AConvergentFractalIterator) fractalIterator).setRootTolerance(rootTolerance);
					((AConvergentFractalIterator) fractalIterator).setAlpha(alpha);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonAutomaticRootDetectionEnabled)) {
			if (fIteratorController.getFractalIterator() instanceof AConvergentFractalIterator) {
				((AConvergentFractalIterator) fIteratorController.getFractalIterator()).setAutomaticRootDetectionEnabled(fMenuItems.get(kActionCommandMenuItemFractalFamilyNewtonRaphsonAutomaticRootDetectionEnabled).isSelected());
				if (((AConvergentFractalIterator) fIteratorController.getFractalIterator()).getAutomaticRootDetectionEnabled()) {
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonPowerSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof NewtonRaphsonPowerFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((NewtonRaphsonPowerFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((NewtonRaphsonPowerFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonPowerPolynomialSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof NewtonRaphsonPowerPolynomialFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((NewtonRaphsonPowerPolynomialFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((NewtonRaphsonPowerPolynomialFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSineSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof NewtonRaphsonTrigonometricPowerSineFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((NewtonRaphsonTrigonometricPowerSineFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((NewtonRaphsonTrigonometricPowerSineFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSineSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof NewtonRaphsonTrigonometricPowerMultiSineFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((NewtonRaphsonTrigonometricPowerMultiSineFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((NewtonRaphsonTrigonometricPowerMultiSineFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSineOffsetSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof NewtonRaphsonTrigonometricPowerSineOffsetFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((NewtonRaphsonTrigonometricPowerSineOffsetFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((NewtonRaphsonTrigonometricPowerSineOffsetFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSineOffsetSetPower)) {
			if (fIteratorController.getFractalIterator() instanceof NewtonRaphsonTrigonometricPowerMultiSineOffsetFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((NewtonRaphsonTrigonometricPowerMultiSineOffsetFractalIterator) fIteratorController.getFractalIterator()).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((NewtonRaphsonTrigonometricPowerMultiSineOffsetFractalIterator) (fIteratorController.getFractalIterator())).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMagnetSetConvergenceParameters)) {
			AFractalIterator fractalIterator = fIteratorController.getFractalIterator();
			if (fractalIterator instanceof AMagnetFractalIterator) {
				ConvergenceParametersChooser convergenceParametersChooser = new ConvergenceParametersChooser(this,
					((AMagnetFractalIterator) fractalIterator).getRootTolerance());
				if (!convergenceParametersChooser.isCancelled()) {
					double rootTolerance = convergenceParametersChooser.getSelectedRootTolerance();
					((AMagnetFractalIterator) fractalIterator).setRootTolerance(rootTolerance);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMarkusLyapunovSetRootSequence)) {
			if (fIteratorController.getFractalIterator() instanceof MarkusLyapunovFractalIterator) {
				RootSequenceChooser rootSequenceChooser = new RootSequenceChooser(this,((MarkusLyapunovFractalIterator) fIteratorController.getFractalIterator()).getRootSequence());
				if (!rootSequenceChooser.isCancelled()) {
					String rootSequence = rootSequenceChooser.getSelectedRootSequence();
					((MarkusLyapunovFractalIterator) (fIteratorController.getFractalIterator())).setRootSequence(rootSequence);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalSetMaxNrOfIterations)) {
			MaxNrOfIterationsChooser maxNrOfIterationsChooser = new MaxNrOfIterationsChooser(this,fIteratorController.getFractalIterator().getMaxNrOfIterations());
			if (!maxNrOfIterationsChooser.isCancelled()) {
				int maxNrOfIterations = maxNrOfIterationsChooser.getSelectedMaxNrOfIterations();
				int discreteColorRange = fFractalPanel.getColorMapDiscreteColorRange();
				if (discreteColorRange == fIteratorController.getFractalIterator().getMaxNrOfIterations()) {
					discreteColorRange = maxNrOfIterations;
				}
				fFractalPanel.getColoringParameters().fColorMapDiscreteColorRange = discreteColorRange;

				// 	adjust colourmap iteration range to comply with the selected maximum number of iterations
				int colorMapIterationLowRange = fFractalPanel.getColorMapIterationLowRange();
				int colorMapIterationHighRange = fFractalPanel.getColorMapIterationHighRange();
				if (maxNrOfIterations < fIteratorController.getFractalIterator().getMaxNrOfIterations()) {
					if (colorMapIterationLowRange > maxNrOfIterations) {
						colorMapIterationLowRange = 0;
					}
					if (colorMapIterationHighRange > maxNrOfIterations) {
						colorMapIterationHighRange = maxNrOfIterations;
					}
				}
				else {
					colorMapIterationHighRange = maxNrOfIterations;
				}

				fFractalPanel.getColoringParameters().fLowIterationRange = colorMapIterationLowRange;
				fFractalPanel.getColoringParameters().fHighIterationRange = colorMapIterationHighRange;
				fFractalPanel.setMaxNrOfIterations(maxNrOfIterations);
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalSetEscapeRadius)) {
			EscapeRadiusChooser escapeRadiusChooser = new EscapeRadiusChooser(this,fIteratorController.getFractalIterator().getEscapeRadius());
			if (!escapeRadiusChooser.isCancelled()) {
				fFractalPanel.setEscapeRadius(escapeRadiusChooser.getSelectedEscapeRadius());
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalCopyCoordinates)) {
			Point p = fFractalPanel.getMousePosition();
			if (p != null) {		
				ComplexNumber c = fIteratorController.getFractalIterator().convertScreenLocationToComplexNumber(new ScreenLocation(p.x,p.y));
				StringSelection clipboardContents = new StringSelection(c.toString());
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(clipboardContents,null);
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalRefreshScreen)) {
			fFractalPanel.repaint();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorBone)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kBone,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorCopper)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kCopper,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorDiscontinuousBlueWhiteGreen)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kDiscontinuousBlueWhiteGreen,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorDiscontinuousDarkRedYellow)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kDiscontinuousDarkRedYellow,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorBlackAndWhite)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kBlackAndWhite,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorGrayScale)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kGrayScale,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorGreenRedDiverging)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kGreenRedDiverging,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorHot)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kHot,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorJet)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kJet,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorHueSaturationBrightness)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kHueSaturationBrightness,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorSeparatedRGB)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kSeparatedRGB,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorRed)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kRed,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorGreen)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kGreen,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorBlue)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kBlue,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorYellow)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kYellow,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorCyan)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kCyan,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorMagenta)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kMagenta,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUltraLightPastel)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kUltraLightPastel,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorLightPastel)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kLightPastel,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorDarkPastel)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kDarkPastel,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorGreens)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kGreens,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorBlues)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kBlues,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorYellowBrowns)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kYellowBrowns,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorVioletPurples)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kVioletPurples,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorDeepSpace)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kDeepSpace,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorCustom)) {
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kCustom,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorSetCustomColorMap)) {
			JCustomColorMapChooser customColorMapChooser = new JCustomColorMapChooser(this,kNrOfCustomColorMapColors,fFractalPanel.getExteriorCustomColorMapComponents());
			if (!customColorMapChooser.isCancelled()) {
				fFractalPanel.setExteriorCustomColorMapComponents(customColorMapChooser.getSelectedCustomColorMapComponents());
				fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kCustom,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
				fMenuItems.get(kActionCommandMenuItemColorMapExteriorCustom).setSelected(true);
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorConvertCurrentColorMapToCustomColorMap)) {
			if (!fMenuItems.get(kActionCommandMenuItemColorMapExteriorCustom).isSelected()) {
				if (JConfirmationDialog.confirm(this,I18NL10N.translate("text.ColorMap.OverwriteCustomColorMap"))) {
					fFractalPanel.setExteriorCustomColorMapComponents(new JGradientColorMap(fFractalPanel.getExteriorColorMap()).convertToComponents(kNrOfCustomColorMapColors));
					fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kCustom,fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
					fMenuItems.get(kActionCommandMenuItemColorMapExteriorCustom).setSelected(true);
					actionPerformed(new ActionEvent(this,ActionEvent.ACTION_LAST+1,kActionCommandMenuItemColorMapExteriorSetCustomColorMap));
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorInvertColorMap)) {
			fFractalPanel.setExteriorColorMap(fFractalPanel.getExteriorColorMap(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap)) {
			fFractalPanel.setExteriorColorMap(fFractalPanel.getExteriorColorMap(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseTigerStripes)) {
			fFractalPanel.setUseTigerStripes(fMenuItems.get(kActionCommandMenuItemColorMapUseTigerStripes).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerBone)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kBone);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerCopper)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kCopper);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerDiscontinuousBlueWhiteGreen)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kDiscontinuousBlueWhiteGreen);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerDiscontinuousDarkRedYellow)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kDiscontinuousDarkRedYellow);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerBlackAndWhite)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kBlackAndWhite);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerGrayScale)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kGrayScale);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerGreenRedDiverging)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kGreenRedDiverging);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerHot)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kHot);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerJet)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kJet);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerHueSaturationBrightness)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kHueSaturationBrightness);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerSeparatedRGB)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kSeparatedRGB);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerRed)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kRed);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerGreen)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kGreen);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerBlue)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kBlue);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerYellow)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kYellow);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerCyan)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kCyan);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerMagenta)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kMagenta);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerUltraLightPastel)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kUltraLightPastel);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerLightPastel)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kLightPastel);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerDarkPastel)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kDarkPastel);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerGreens)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kGreens);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerBlues)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kBlues);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerYellowBrowns)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kYellowBrowns);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerVioletPurples)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kVioletPurples);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerDeepSpace)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kDeepSpace);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerCustom)) {
			fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kCustom);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerSetCustomColorMap)) {
			JCustomColorMapChooser tigerCustomColorMapChooser = new JCustomColorMapChooser(this,kNrOfCustomColorMapColors,fFractalPanel.getTigerCustomColorMapComponents());
			if (!tigerCustomColorMapChooser.isCancelled()) {
				fFractalPanel.setTigerCustomColorMapComponents(tigerCustomColorMapChooser.getSelectedCustomColorMapComponents());
				fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kCustom);
				fMenuItems.get(kActionCommandMenuItemColorMapTigerCustom).setSelected(true);
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerConvertCurrentColorMapToCustomColorMap)) {
			if (!fMenuItems.get(kActionCommandMenuItemColorMapTigerCustom).isSelected()) {
				if (JConfirmationDialog.confirm(this,I18NL10N.translate("text.ColorMap.OverwriteCustomColorMap"))) {
					fFractalPanel.setTigerCustomColorMapComponents(new JGradientColorMap(fFractalPanel.getTigerColorMap()).convertToComponents(kNrOfCustomColorMapColors));
					fFractalPanel.setTigerColorMap(JGradientColorMap.EColorMap.kCustom);
					fMenuItems.get(kActionCommandMenuItemColorMapTigerCustom).setSelected(true);
					actionPerformed(new ActionEvent(this,ActionEvent.ACTION_LAST+1,kActionCommandMenuItemColorMapTigerSetCustomColorMap));
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerUseFixedColor)) {
			fFractalPanel.setTigerUseFixedColor(true);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerSetFixedColor)) {
			try {
				Color tigerStripeFixedColor = JColorChooser.showDialog(this,I18NL10N.translate(kActionCommandMenuItemColorMapTigerSetFixedColor),fFractalPanel.getTigerStripeFixedColor());
				if (tigerStripeFixedColor != null) {
					fFractalPanel.setTigerStripeFixedColor(tigerStripeFixedColor);
					fTigerStripeColorLabelDecorator.setColor(tigerStripeFixedColor);
				}
			}
			catch (HeadlessException exc) {
				// ignore
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorBone)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kBone,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorCopper)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kCopper,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorDiscontinuousBlueWhiteGreen)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kDiscontinuousBlueWhiteGreen,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorDiscontinuousDarkRedYellow)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kDiscontinuousDarkRedYellow,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorBlackAndWhite)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kBlackAndWhite,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorGrayScale)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kGrayScale,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorGreenRedDiverging)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kGreenRedDiverging,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorHot)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kHot,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorJet)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kJet,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorHueSaturationBrightness)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kHueSaturationBrightness,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorSeparatedRGB)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kSeparatedRGB,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorRed)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kRed,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorGreen)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kGreen,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorBlue)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kBlue,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorYellow)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kYellow,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorCyan)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kCyan,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorMagenta)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kMagenta,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUltraLightPastel)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kUltraLightPastel,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorLightPastel)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kLightPastel,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorDarkPastel)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kDarkPastel,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorGreens)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kGreens,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorBlues)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kBlues,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorYellowBrowns)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kYellowBrowns,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorVioletPurples)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kVioletPurples,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorDeepSpace)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kDeepSpace,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorCustom)) {
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kCustom,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorSetCustomColorMap)) {
			JCustomColorMapChooser interiorCustomColorMapChooser = new JCustomColorMapChooser(this,kNrOfCustomColorMapColors,fFractalPanel.getInteriorCustomColorMapComponents());
			if (!interiorCustomColorMapChooser.isCancelled()) {
				fFractalPanel.setInteriorCustomColorMapComponents(interiorCustomColorMapChooser.getSelectedCustomColorMapComponents());
				fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kCustom,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
				fMenuItems.get(kActionCommandMenuItemColorMapInteriorCustom).setSelected(true);
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorConvertCurrentColorMapToCustomColorMap)) {
			if (!fMenuItems.get(kActionCommandMenuItemColorMapInteriorCustom).isSelected()) {
				if (JConfirmationDialog.confirm(this,I18NL10N.translate("text.ColorMap.OverwriteCustomColorMap"))) {
					fFractalPanel.setInteriorCustomColorMapComponents(new JGradientColorMap(fFractalPanel.getInteriorColorMap()).convertToComponents(kNrOfCustomColorMapColors));
					fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kCustom,fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
					fMenuItems.get(kActionCommandMenuItemColorMapInteriorCustom).setSelected(true);
					actionPerformed(new ActionEvent(this,ActionEvent.ACTION_LAST+1,kActionCommandMenuItemColorMapInteriorSetCustomColorMap));
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorInvertColorMap)) {
			fFractalPanel.setInteriorColorMap(fFractalPanel.getInteriorColorMap(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap)) {
			fFractalPanel.setInteriorColorMap(fFractalPanel.getInteriorColorMap(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected(),fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseFixedColor)) {
			fFractalPanel.setInteriorColoringMethod(ColoringParameters.EColoringMethod.kFixedColor);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorSetFixedColor)) {
			try {
				Color interiorColor = JColorChooser.showDialog(this,I18NL10N.translate(kActionCommandMenuItemColorMapInteriorSetFixedColor),fFractalPanel.getInteriorColor());
				if (interiorColor != null) {
					fFractalPanel.setInteriorColor(interiorColor);
					fInteriorColorLabelDecorator.setColor(interiorColor);
					fFractalPanel.setInteriorColoringMethod(ColoringParameters.EColoringMethod.kFixedColor);
					fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseFixedColor).setSelected(true);
				}
			}
			catch (HeadlessException exc) {
				// ignore
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseExponentiallySmoothedLevelSets)) {
			fFractalPanel.setInteriorColoringMethod(ColoringParameters.EColoringMethod.kSmoothEICLevelSets);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseSectorDecomposition)) {
			fFractalPanel.setInteriorColoringMethod(ColoringParameters.EColoringMethod.kSectorDecomposition);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorSetDecompositionSectorRange)) {
			SectorDecompositionRangeChooser sectorDecompositionRangeChooser = new SectorDecompositionRangeChooser(this,fFractalPanel.getColorMapInteriorSectorDecompositionRange());
			if (!sectorDecompositionRangeChooser.isCancelled()) {
				fFractalPanel.setColorMapInteriorSectorDecompositionRange(sectorDecompositionRangeChooser.getSelectedSectorDecompositionRange());
				// automatically select the binary decomposition
				fFractalPanel.setInteriorColoringMethod(ColoringParameters.EColoringMethod.kSectorDecomposition);
				adjustGUIToFractal();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseRealComponent)) {
			fFractalPanel.setInteriorColoringMethod(ColoringParameters.EColoringMethod.kRealComponent);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseImaginaryComponent)) {
			fFractalPanel.setInteriorColoringMethod(ColoringParameters.EColoringMethod.kImaginaryComponent);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseModulus)) {
			fFractalPanel.setInteriorColoringMethod(ColoringParameters.EColoringMethod.kModulus);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseAngle)) {
			fFractalPanel.setInteriorColoringMethod(ColoringParameters.EColoringMethod.kAngle);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseMaxModulus)) {
			fFractalPanel.setInteriorColoringMethod(ColoringParameters.EColoringMethod.kMaxModulus);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseTotalDistance)) {
			fFractalPanel.setInteriorColoringMethod(ColoringParameters.EColoringMethod.kTotalDistance);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseAverageDistance)) {
			fFractalPanel.setInteriorColoringMethod(ColoringParameters.EColoringMethod.kAverageDistance);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseTotalAngle)) {
			fFractalPanel.setInteriorColoringMethod(ColoringParameters.EColoringMethod.kTotalAngle);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseLyapunovExponent)) {
			fFractalPanel.setInteriorColoringMethod(ColoringParameters.EColoringMethod.kLyapunovExponent);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseCurvature)) {
			fFractalPanel.setInteriorColoringMethod(ColoringParameters.EColoringMethod.kCurvature);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseStriping)) {
			fFractalPanel.setInteriorColoringMethod(ColoringParameters.EColoringMethod.kStriping);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseMinimumGaussianIntegersDistance)) {
			fFractalPanel.setInteriorColoringMethod(ColoringParameters.EColoringMethod.kMinimumGaussianIntegersDistance);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseAverageGaussianIntegersDistance)) {
			fFractalPanel.setInteriorColoringMethod(ColoringParameters.EColoringMethod.kAverageGaussianIntegersDistance);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseFixedColor)) {
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kFixedColor);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorSetFixedColor)) {
			try {
				Color exteriorColor = JColorChooser.showDialog(this,I18NL10N.translate(kActionCommandMenuItemColorMapExteriorSetFixedColor),fFractalPanel.getExteriorColor());
				if (exteriorColor != null) {
					fFractalPanel.setExteriorColor(exteriorColor);
					fExteriorColorLabelDecorator.setColor(exteriorColor);
					fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kFixedColor);
					fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseFixedColor).setSelected(true);
				}
			}
			catch (HeadlessException exc) {
				// ignore
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseDiscreteLevelSets)) {
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kDiscreteLevelSets);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseNormalisedLevelSets)) {
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kSmoothNICLevelSets);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseExponentiallySmoothedLevelSets)) {
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kSmoothEICLevelSets);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseSectorDecomposition)) {
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kSectorDecomposition);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorSetDecompositionSectorRange)) {
			SectorDecompositionRangeChooser sectorDecompositionRangeChooser = new SectorDecompositionRangeChooser(this,fFractalPanel.getColorMapExteriorSectorDecompositionRange());
			if (!sectorDecompositionRangeChooser.isCancelled()) {
				fFractalPanel.setColorMapExteriorSectorDecompositionRange(sectorDecompositionRangeChooser.getSelectedSectorDecompositionRange());
				// automatically select the binary decomposition
				fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kSectorDecomposition);
				adjustGUIToFractal();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseRealComponent)) {
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kRealComponent);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseImaginaryComponent)) {
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kImaginaryComponent);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseModulus)) {
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kModulus);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseAngle)) {
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kAngle);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseMaxModulus)) {
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kMaxModulus);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseTotalDistance)) {
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kTotalDistance);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseAverageDistance)) {
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kAverageDistance);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseTotalAngle)) {
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kTotalAngle);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseLyapunovExponent)) {
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kLyapunovExponent);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapCalculateAdvancedColoring)) {
			boolean calculateAdvancedColoring = fMenuItems.get(kActionCommandMenuItemColorMapCalculateAdvancedColoring).isSelected();
			fFractalPanel.getColoringParameters().fCalculateAdvancedColoring = calculateAdvancedColoring;
			fIteratorController.getFractalIterator().setCalculateAdvancedColoring(calculateAdvancedColoring);
			if (calculateAdvancedColoring) {
				fIteratorController.recalc();
			}
			fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseCurvature).setEnabled(calculateAdvancedColoring);
			fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseStriping).setEnabled(calculateAdvancedColoring);
			fMenuItems.get(kActionCommandMenuItemColorMapExteriorSetStripingDensity).setEnabled(calculateAdvancedColoring);
			fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseMinimumGaussianIntegersDistance).setEnabled(calculateAdvancedColoring);
			fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseAverageGaussianIntegersDistance).setEnabled(calculateAdvancedColoring);
			fMenuItems.get(kActionCommandMenuItemColorMapExteriorSetGaussianIntegersTrapFactor).setEnabled(calculateAdvancedColoring);
			fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseCurvature).setEnabled(calculateAdvancedColoring);
			fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseStriping).setEnabled(calculateAdvancedColoring);
			fMenuItems.get(kActionCommandMenuItemColorMapInteriorSetStripingDensity).setEnabled(calculateAdvancedColoring);
			fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseMinimumGaussianIntegersDistance).setEnabled(calculateAdvancedColoring);
			fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseAverageGaussianIntegersDistance).setEnabled(calculateAdvancedColoring);
			fMenuItems.get(kActionCommandMenuItemColorMapInteriorSetGaussianIntegersTrapFactor).setEnabled(calculateAdvancedColoring);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseCurvature)) {
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kCurvature);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseStriping)) {
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kStriping);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorSetStripingDensity) ||
						command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorSetStripingDensity)) {
			StripingDensityChooser stripingDensityChooser = new StripingDensityChooser(this,fIteratorController.getFractalIterator().getStripingDensity());
			if (!stripingDensityChooser.isCancelled()) {
				fIteratorController.getFractalIterator().setStripingDensity(stripingDensityChooser.getSelectedStripingDensity());
				fIteratorController.recalc();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseMinimumGaussianIntegersDistance)) {
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kMinimumGaussianIntegersDistance);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseAverageGaussianIntegersDistance)) {
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kAverageGaussianIntegersDistance);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorSetGaussianIntegersTrapFactor) ||
						command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorSetGaussianIntegersTrapFactor)) {
			GaussianIntegersTrapFactorChooser gaussianIntegersTrapFactorChooser = new GaussianIntegersTrapFactorChooser(this,fIteratorController.getFractalIterator().getGaussianIntegersTrapFactor());
			if (!gaussianIntegersTrapFactorChooser.isCancelled()) {
				fIteratorController.getFractalIterator().setGaussianIntegersTrapFactor(gaussianIntegersTrapFactorChooser.getSelectedGaussianIntegersTrapFactor());
				fIteratorController.recalc();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseDiscreteRoots)) {
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kDiscreteRoots);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseSmoothRoots)) {
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kSmoothRoots);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorSetBrightnessFactor)) {
			BrightnessFactorChooser brightnessFactorChooser = new BrightnessFactorChooser(this,fFractalPanel.getBrightnessFactor());
			if (!brightnessFactorChooser.isCancelled()) {
				fFractalPanel.setBrightnessFactor(brightnessFactorChooser.getSelectedBrightnessFactor());
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseBinaryDecomposition)) {
			// setup exterior colouring
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kBlackAndWhite,false,false);
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kSectorDecomposition);
			fFractalPanel.setColorMapExteriorSectorDecompositionRange(2);

			// disable tiger striping
			fFractalPanel.setUseTigerStripes(false);

			// setup interior colouring
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kBlackAndWhite,false,false);
			fFractalPanel.setColorMapInteriorSectorDecompositionRange(2);
			fFractalPanel.setInteriorColoringMethod(ColoringParameters.EColoringMethod.kFixedColor);
			fFractalPanel.setInteriorColor(Color.BLACK);
			fInteriorColorLabelDecorator.setColor(Color.BLACK);

			// setup scaling
			fFractalPanel.setColorMapScaling(ColoringParameters.EColorMapScaling.kLinear);
			fFractalPanel.setColorMapScalingParameters(1.0,1.0);

			// setup iteration range
			fFractalPanel.setColorMapIterationRange(0,fIteratorController.getFractalIterator().getMaxNrOfIterations());

			// disable colour repetition and offset
			fFractalPanel.setColorMapRepeatMode(false);

			fFractalPanel.cycleToColorMapColorOffset(0.0);

			// disable colour cycling
			fColorCyclingTimer.stop();
			fMenuItems.get(kActionCommandMenuItemColorMapCycleColors).setSelected(false);

			// setup colour map usage
			fFractalPanel.setColorMapUsage(ColoringParameters.EColorMapUsage.kFull);

			// disable post-processing filters
			fFractalPanel.setUsePostProcessingFilters(false);

			adjustGUIToFractal();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseContours)) {
			// reset post-processing filter chain to a single edge-detection filter
			ColoringParameters coloringParameters = fFractalPanel.getColoringParameters();
			coloringParameters.fPostProcessingFilterChain = new FilterChain();
			coloringParameters.fPostProcessingFilterChain.addFilter(new EdgeFilter());
			fFractalPanel.setUsePostProcessingFilters(true);

			adjustGUIToFractal();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseDarkSofteningFilter)) {
			// reset post-processing filter chain to a dark softening filter cascade
			ColoringParameters coloringParameters = fFractalPanel.getColoringParameters();
			coloringParameters.fPostProcessingFilterChain = new FilterChain();
			coloringParameters.fPostProcessingFilterChain.addFilter(new SharpenFilter());
			coloringParameters.fPostProcessingFilterChain.addFilter(new EdgeFilter());
			coloringParameters.fPostProcessingFilterChain.addFilter(new BlurFilter());
			coloringParameters.fPostProcessingFilterChain.addFilter(new PosteriseFilter());
			fFractalPanel.setUsePostProcessingFilters(true);

			adjustGUIToFractal();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapResetToDefault)) {
			// setup exterior colouring
			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kJet,false,false);
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kSmoothNICLevelSets);

			// disable tiger striping
			fFractalPanel.setUseTigerStripes(false);

			// setup interior colouring
			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kJet,false,false);
			fFractalPanel.setInteriorColoringMethod(ColoringParameters.EColoringMethod.kFixedColor);
			fFractalPanel.setInteriorColor(Color.BLACK);
			fInteriorColorLabelDecorator.setColor(Color.BLACK);

			// setup scaling
			fFractalPanel.setColorMapScaling(ColoringParameters.EColorMapScaling.kLinear);
			fFractalPanel.setColorMapScalingParameters(1.0,1.0);

			// setup iteration range
			fFractalPanel.setColorMapIterationRange(0,fIteratorController.getFractalIterator().getMaxNrOfIterations());

			// disable colour repetition and offset
			fFractalPanel.setColorMapRepeatMode(false);

			fFractalPanel.cycleToColorMapColorOffset(0.0);

			// disable colour cycling
			fColorCyclingTimer.stop();
			fMenuItems.get(kActionCommandMenuItemColorMapCycleColors).setSelected(false);

			// setup colour map usage
			fFractalPanel.setColorMapUsage(ColoringParameters.EColorMapUsage.kFull);
			
			// disable post-processing filters
			fFractalPanel.setUsePostProcessingFilters(false);

			adjustGUIToFractal();
			setupMarkusLyapunovFractal();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseLinearScaling)) {
			fFractalPanel.setColorMapScaling(ColoringParameters.EColorMapScaling.kLinear);
			fMenuItems.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColors).setEnabled(fFractalPanel.getColoringParameters().fColorMapScaling == ColoringParameters.EColorMapScaling.kRankOrder);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseLogarithmicScaling)) {
			fFractalPanel.setColorMapScaling(ColoringParameters.EColorMapScaling.kLogarithmic);
			fMenuItems.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColors).setEnabled(fFractalPanel.getColoringParameters().fColorMapScaling == ColoringParameters.EColorMapScaling.kRankOrder);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseExponentialScaling)) {
			fFractalPanel.setColorMapScaling(ColoringParameters.EColorMapScaling.kExponential);
			fMenuItems.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColors).setEnabled(fFractalPanel.getColoringParameters().fColorMapScaling == ColoringParameters.EColorMapScaling.kRankOrder);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseSqrtScaling)) {
			fFractalPanel.setColorMapScaling(ColoringParameters.EColorMapScaling.kSqrt);
			fMenuItems.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColors).setEnabled(fFractalPanel.getColoringParameters().fColorMapScaling == ColoringParameters.EColorMapScaling.kRankOrder);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapSetScalingParameters)) {
			ColorMapScalingParametersChooser colorMapScalingParametersChooser = new ColorMapScalingParametersChooser(this,fFractalPanel.getColorMapScalingFunctionMultiplier(),fFractalPanel.getColorMapScalingArgumentMultiplier());
			if (!colorMapScalingParametersChooser.isCancelled()) {
				fFractalPanel.setColorMapScalingParameters(colorMapScalingParametersChooser.getSelectedFunctionMultiplier(),colorMapScalingParametersChooser.getSelectedArgumentMultiplier());
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseRankOrderScaling)) {
			fFractalPanel.setColorMapScaling(ColoringParameters.EColorMapScaling.kRankOrder);
			fMenuItems.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColors).setEnabled(fFractalPanel.getColoringParameters().fColorMapScaling == ColoringParameters.EColorMapScaling.kRankOrder);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapRestrictHighIterationCountColors)) {
			fFractalPanel.setRankOrderRestrictHighIterationCountColors(fMenuItems.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColors).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapSetIterationRange)) {
			IterationRangeChooser iterationRangeChooser = new IterationRangeChooser(this,fFractalPanel.getColorMapIterationLowRange(),fFractalPanel.getColorMapIterationHighRange(),fIteratorController.getFractalIterator().getMaxNrOfIterations());
			if (!iterationRangeChooser.isCancelled()) {
				fFractalPanel.setColorMapIterationRange(iterationRangeChooser.getSelectedLowIterationRange(),iterationRangeChooser.getSelectedHighIterationRange());
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapRepeatColors)) {
			fFractalPanel.setColorMapRepeatMode(fMenuItems.get(kActionCommandMenuItemColorMapRepeatColors).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapSetColorRepetition)) {
			ColorMapRepetitionChooser colorMapRepetitionChooser = new ColorMapRepetitionChooser(this,fFractalPanel.getColorMapColorRepetition());
			if (!colorMapRepetitionChooser.isCancelled()) {
				fFractalPanel.setColorMapColorRepetition(colorMapRepetitionChooser.getSelectedColorRepetition());
				fFractalPanel.setColorMapRepeatMode(true);
				fMenuItems.get(kActionCommandMenuItemColorMapRepeatColors).setSelected(true);
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapSetColorOffset)) {
			ColorMapOffsetChooser colorMapOffsetChooser = new ColorMapOffsetChooser(this,fFractalPanel.getColorMapColorOffset());
			if (!colorMapOffsetChooser.isCancelled()) {
				fFractalPanel.cycleToColorMapColorOffset(colorMapOffsetChooser.getSelectedColorOffset());
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapCycleColors)) {
			if (fMenuItems.get(kActionCommandMenuItemColorMapCycleColors).isSelected()) {
				fColorCyclingTimer.restart();
			}
			else {
				fColorCyclingTimer.stop();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapSetColorCyclingParameters)) {
			ColorMapCyclingParametersChooser colorMapCyclingParametersChooser = new ColorMapCyclingParametersChooser(this,fColorCyclingDelay,fColorCyclingSmoothness,fColorCyclingDirectionForward);
			if (!colorMapCyclingParametersChooser.isCancelled()) {
				fColorCyclingDelay = colorMapCyclingParametersChooser.getSelectedColorCyclingDelay();
				fColorCyclingTimer.setInitialDelay(fColorCyclingDelay);
				fColorCyclingTimer.setDelay(fColorCyclingDelay);
				fColorCyclingSmoothness = colorMapCyclingParametersChooser.getSelectedColorCyclingSmoothness();
				fColorCyclingDirectionForward = colorMapCyclingParametersChooser.getSelectedColorCyclingDirectionForward();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapFullColorRange)) {
			fFractalPanel.setColorMapUsage(ColoringParameters.EColorMapUsage.kFull);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseLimitedContinuousColorRange)) {
			fFractalPanel.setColorMapUsage(ColoringParameters.EColorMapUsage.kLimitedContinuous);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapSetLimitedContinuousColorRange)) {
			ColorMapContinuousRangeChooser colorMapContinuousRangeChooser = new ColorMapContinuousRangeChooser(this,fFractalPanel.getColorMapContinuousColorRange());
			if (!colorMapContinuousRangeChooser.isCancelled()) {
				fFractalPanel.setColorMapContinuousColorRange(colorMapContinuousRangeChooser.getSelectedRange());
				fFractalPanel.setColorMapUsage(ColoringParameters.EColorMapUsage.kLimitedContinuous);
				fMenuItems.get(kActionCommandMenuItemColorMapUseLimitedContinuousColorRange).setSelected(true);
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseLimitedDiscreteColorRange)) {
			fFractalPanel.setColorMapUsage(ColoringParameters.EColorMapUsage.kLimitedDiscrete);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapSetLimitedDiscreteColorRange)) {
			ColorMapDiscreteRangeChooser colorMapDiscreteRangeChooser = new ColorMapDiscreteRangeChooser(this,fFractalPanel.getColorMapDiscreteColorRange(),fIteratorController.getFractalIterator().getMaxNrOfIterations());
			if (!colorMapDiscreteRangeChooser.isCancelled()) {
				fFractalPanel.setColorMapDiscreteColorRange(colorMapDiscreteRangeChooser.getSelectedRange());
				fFractalPanel.setColorMapUsage(ColoringParameters.EColorMapUsage.kLimitedDiscrete);
				fMenuItems.get(kActionCommandMenuItemColorMapUseLimitedDiscreteColorRange).setSelected(true);
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUsePostProcessingFilters)) {
			fFractalPanel.setUsePostProcessingFilters(fMenuItems.get(kActionCommandMenuItemColorMapUsePostProcessingFilters).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorSetupPostProcessingFilters)) {
			FilterSetupChooser filterSetupChooser = new FilterSetupChooser(this,fFractalPanel);
			if (!filterSetupChooser.isCancelled()) {
				fFractalPanel.getColoringParameters().fPostProcessingFilterChain = filterSetupChooser.getSelectedFilterChain();
				fFractalPanel.setUsePostProcessingFilters(true);

				adjustGUIToFractal();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemMultithreadingRecalculate)) {
			fIteratorController.recalc();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemMultithreadingSetNrOfCPUCoresToUse)) {
			NrOfCPUCoresToUseChooser nrOfCPUCoresToUseChooser = new NrOfCPUCoresToUseChooser(this,fIteratorController.getNrOfThreadsToUse());
			if (!nrOfCPUCoresToUseChooser.isCancelled()) {
				fIteratorController.setNrOfThreadsToUse(nrOfCPUCoresToUseChooser.getSelectedNrOfCPUCoresToUse());
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemMultithreadingSetNrOfBlocksToUse)) {
			NrOfBlocksToUseChooser nrOfBlocksToUseChooser = new NrOfBlocksToUseChooser(this,fIteratorController.getNrOfBlocksToUse());
			if (!nrOfBlocksToUseChooser.isCancelled()) {
				fIteratorController.setNrOfBlocksToUse(nrOfBlocksToUseChooser.getSelectedNrOfBlocksToUse());
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemMultithreadingProgressIndicatorBar)) {
			fProgressUpdateGlassPane.setVisualisationType(JProgressUpdateGlassPane.EVisualisationType.kBar);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemMultithreadingProgressIndicatorCircles)) {
			fProgressUpdateGlassPane.setVisualisationType(JProgressUpdateGlassPane.EVisualisationType.kCircles);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemMultithreadingProgressIndicatorFixedSector)) {
			fProgressUpdateGlassPane.setVisualisationType(JProgressUpdateGlassPane.EVisualisationType.kFixedSector);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemMultithreadingProgressIndicatorRotatingSector)) {
			fProgressUpdateGlassPane.setVisualisationType(JProgressUpdateGlassPane.EVisualisationType.kRotatingSector);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemHelpGeneralInformation)) {
			showHelpTopic(EHelpTopic.kGeneralInformation);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemHelpGettingStarted)) {
			showHelpTopic(EHelpTopic.kGettingStarted);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemHelpColoringSchemes)) {
			showHelpTopic(EHelpTopic.kColoringSchemes);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemHelpResizingForQualityPrinting)) {
			showHelpTopic(EHelpTopic.kResizingForQualityPrinting);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemHelpKeyboardShortcuts)) {
			showHelpTopic(EHelpTopic.kKeyboardShortcuts);
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemHelpFractalTypes)) {
			showHelpTopic(EHelpTopic.kFractalTypes);
		}

		fFractalPanel.repaint();
	}

	// the mouse-listener
	/**
	 */
	@Override
	public void mouseClicked(MouseEvent e)
	{
		if ((e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() == 2)) {
			if (fDoubleClickMode == EDoubleClickMode.kSwitchMainDualFractal) {
				// switch main and inset fractal types on a double click
				fFractalPanel.switchMainDualFractal();
			}
			else if (fDoubleClickMode == 	EDoubleClickMode.kChangeOrbitStartingPoint) {
				Point mousePosition = fFractalPanel.getMousePosition();
				if (mousePosition != null) {
					double mouseX = mousePosition.getX();
					double mouseY = mousePosition.getY();
					ComplexNumber z0 = fIteratorController.getFractalIterator().convertScreenLocationToComplexNumber(new ScreenLocation((int) mouseX,(int) mouseY));
					fIteratorController.getFractalIterator().setMainFractalOrbitStartingPoint(z0);
					fIteratorController.recalc();
				}
			}
		}
		else if ((e.getButton() == MouseEvent.BUTTON3) && (e.getClickCount() == 2)) {
			// zoom out on a single right click
			fFractalPanel.zoomOut();
		}
	}

	/**
	 */
	@Override
	public void mouseEntered(MouseEvent e)
	{
		changeLocationMouseCursor();
	}

	/**
	 */
	@Override
	public void mouseExited(MouseEvent e)
	{
		fFractalPanel.repaint();
		getStatusBar().clearStatusText();
//		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		changeLocationMouseCursor();
	}

	/**
	 */
	@Override
	public void mousePressed(MouseEvent e)
	{
		changeLocationMouseCursor();
		if (e.getButton() == MouseEvent.BUTTON1) {
			// initiate selection rectangle
			setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			Point mousePosition = fFractalPanel.getMousePosition();
			if (mousePosition != null) {
				double mouseX = mousePosition.getX();
				double mouseY = mousePosition.getY();
				fFractalPanel.setSelectionAnchor(new ScreenLocation((int) mouseX,(int) mouseY));
			}
		}

		// disable selecting if the right mouse button was clicked
		if (e.getButton() == MouseEvent.BUTTON3) {
			fFractalPanel.disableSelecting();
			fFractalPanel.repaint();
		}
	}

	/**
	 */
	@Override
	public void mouseReleased(MouseEvent e)
	{
		changeLocationMouseCursor();
		if (fFractalPanel.getSelecting()) {
			fFractalPanel.zoomToSelection();
			updateStatusBar();
		}
	}

	// the mouse-motion-listener
	/**
	 */
	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (fFractalPanel.getSelecting()) {
			// resize selection rectangle
			Point mousePosition = fFractalPanel.getMousePosition();
			if (mousePosition != null) {
				double mouseX = mousePosition.getX();
				double mouseY = mousePosition.getY();
				fFractalPanel.setSelectionExtent(new ScreenLocation((int) mouseX,(int) mouseY));
			}
		}
		updateStatusBar();
	}

	/**
	 */
	@Override
	public void mouseMoved(MouseEvent e)
	{
		changeLocationMouseCursor();
		fFractalPanel.repaint();
		updateStatusBar();
	}

	// the key-listener
	/**
	 */
	@Override
	public void keyTyped(KeyEvent e)
	{
		// ignore
	}

	/**
	 */
	@Override
	public void keyPressed(KeyEvent e)
	{
		// ignore
	}

	/**
	 */
	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			actionPerformed(new ActionEvent(this,ActionEvent.ACTION_LAST+1,kActionCommandMenuItemNavigationPanLeft));
		}
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			actionPerformed(new ActionEvent(this,ActionEvent.ACTION_LAST+1,kActionCommandMenuItemNavigationPanRight));
		}
		else if (e.getKeyCode() == KeyEvent.VK_UP) {
			actionPerformed(new ActionEvent(this,ActionEvent.ACTION_LAST+1,kActionCommandMenuItemNavigationPanUp));
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			actionPerformed(new ActionEvent(this,ActionEvent.ACTION_LAST+1,kActionCommandMenuItemNavigationPanDown));
		}
//XXX
/*
		else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if (fIteratorController.isBusy()) {
				JIncompleteWarningDialog.warn(this,"GUIApplication");
			}
		}
*/
		else if (e.getKeyCode() == KeyEvent.VK_F1) {
			showHelpTopic(EHelpTopic.kGeneralInformation);
		}
	}

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * See {@link JStandardGUIApplication}.
	 */
	@Override
	protected String setupApplicationResourceArchiveFilename()
	{
		return "application-resources.zip";
	}

	/**
	 * See {@link JStandardGUIApplication}.
	 */
	@Override
	protected String setupApplicationLocalePrefix()
	{
		return "application-resources/locales/locale-";
	}

	/**
	 * See {@link JStandardGUIApplication}.
	 */
	@Override
	protected void initialise(java.lang.Object[] parameters)
	{
		loadRegistry();

		fIteratorController = new IteratorController();
		fFractalPanel = new FractalPanel(fIteratorController);
		fProgressUpdateGlassPane = new JProgressUpdateGlassPane();
		fStatusBarCalculationTimeLabel = new JLabel();
		fIteratorController.installGUIControls(this,fProgressUpdateGlassPane,fFractalPanel,fStatusBarCalculationTimeLabel,fResources);

		fFractalFamilyMenuItems = new ArrayList<String>();

		fNavigationPanningSize = 0.25;

		fDoubleClickMode = EDoubleClickMode.kSwitchMainDualFractal;

		addKeyListener(this);

		getSplashScreen().setStatusMessageWaitTime(kSplashScreenStatusMessageWaitTime);

		// install the colour cycler
		getSplashScreen().setStatusMessage(I18NL10N.translate("text.Splash.InstallingColorCycler"));

		fColorCyclingDelay = kInitialColorCyclingDelay;
		fColorCyclingSmoothness = kInitialColorCyclingSmoothness;
		fColorCyclingDirectionForward = kInitialColorCyclingDirectionForward;

		ActionListener colorCycleActionListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				double sign = +1.0;
				if (!fColorCyclingDirectionForward) {
					sign = -1.0;
				}
				double newOffset = MathTools.frac(fFractalPanel.getColorMapColorOffset() + (sign * fColorCyclingSmoothness));
				fFractalPanel.cycleToColorMapColorOffset(newOffset);
			}
		};

		fColorCyclingTimer = new javax.swing.Timer(fColorCyclingDelay,colorCycleActionListener);
		fColorCyclingTimer.setInitialDelay(fColorCyclingDelay);
		fColorCyclingTimer.setDelay(fColorCyclingDelay);
		fColorCyclingTimer.setRepeats(true);

		setMinimumSize(kMinWindowSize);

		// initialise zoom
		fFractalPanel.resetZoom();

		// install help system
		try {
			ClassLoader cl = this.getClass().getClassLoader();
			URL hsURL = HelpSet.findHelpSet(cl,"help/fraxion-" + I18NL10N.getCurrentLocale() + ".hs");
			fHelpSet = new HelpSet(null,hsURL);
			fHelpSet.setTitle("Fraxion Help");
			fHelpBroker = fHelpSet.createHelpBroker();
			fHelpBroker.setSize(new Dimension(800,600));

			// setup map IDs
			fHelpMapIDs = new Hashtable<EHelpTopic,javax.help.Map.ID>();
			fHelpMapIDs.put(EHelpTopic.kGeneralInformation,javax.help.Map.ID.create("general_information_overview",fHelpSet));
			fHelpMapIDs.put(EHelpTopic.kGettingStarted,javax.help.Map.ID.create("general_information_getting_started",fHelpSet));
			fHelpMapIDs.put(EHelpTopic.kColoringSchemes,javax.help.Map.ID.create("coloring_schemes",fHelpSet));
			fHelpMapIDs.put(EHelpTopic.kResizingForQualityPrinting,javax.help.Map.ID.create("resizing_for_quality_printing",fHelpSet));
			fHelpMapIDs.put(EHelpTopic.kKeyboardShortcuts,javax.help.Map.ID.create("keyboard_shortcuts",fHelpSet));
			fHelpMapIDs.put(EHelpTopic.kFractalTypes,javax.help.Map.ID.create("fractal_types_mandelbrot_julia",fHelpSet));
		}
		catch (Exception exc) {
			kLogger.error(I18NL10N.translate("error.HelpInformationNotFound"));
		}
	}

	/**
	 * See {@link JStandardGUIApplication}.
	 */
	@Override
	protected JLabel setupSplashScreenContent()
	{
		try {
			JLabel label = new JLabel(new ImageIcon(fResources.getImage("application-resources/images/splashscreen.png")));
			return label;
		}
		catch (FileDoesNotExistException exc) {
			return null;
		}
	}

	/**
	 * See {@link JStandardGUIApplication}.
	 */
	@Override
	protected java.lang.String setupInitialLookAndFeel()
	{
		return klafNimbus;
	}

	/**
	 * See {@link JStandardGUIApplication}.
	 */
	@Override
	protected final Dimension setupInitialGUISize()
	{
		return (new Dimension(JStandardGUIApplication.kFullScreenGUI,JStandardGUIApplication.kFullScreenGUI));
	}

	/**
	 * See {@link JStandardGUIApplication}.
	 */
	@Override
	protected Image setupIcon()
	{
		try {
			return fResources.getImage(kApplicationIconFilename);
		}
		catch (FileDoesNotExistException exc) {
			return null;
		}
	}

	/**
	 * See {@link JStandardGUIApplication}.
	 */
	@Override
	protected String setupWindowTitle()
	{
		return I18NL10N.translate("text.Window.Title");
	}

	/**
	 * See {@link JStandardGUIApplication}.
	 */
	@Override
	protected void setupContentPane(JPanel contentPane)
	{
		contentPane.setLayout(new BorderLayout());
				// create a panel containing a background drop
				try {
						JImagePanel backgroundPanel = new JImagePanel(fResources.getImage("application-resources/images/application-background.png"));
						// center the fractal panel inside the background panel
						backgroundPanel.setLayout(new GridBagLayout());
						backgroundPanel.add(fFractalPanel);
					fFractalScrollPane = new JScrollPane(backgroundPanel,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				}
				catch (FileDoesNotExistException exc) {
					// ignore
				}
			fFractalScrollPane.setWheelScrollingEnabled(true);
			fFractalScrollPane.getHorizontalScrollBar().setBlockIncrement(kScrollbarBlockIncrement);
			fFractalScrollPane.getHorizontalScrollBar().setUnitIncrement(kScrollbarBlockIncrement);
			fFractalScrollPane.getVerticalScrollBar().setBlockIncrement(kScrollbarBlockIncrement);
			fFractalScrollPane.getVerticalScrollBar().setUnitIncrement(kScrollbarBlockIncrement);
			fFractalScrollPane.addMouseListener(this);
			fFractalScrollPane.addMouseMotionListener(this);
			fFractalScrollPane.addMouseMotionListener(this);
			fFractalScrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE); // enable smooth scrolling when updating the various panels
			fFractalPanel.setViewport(fFractalScrollPane.getViewport());
		contentPane.add(fFractalScrollPane,BorderLayout.CENTER);
	}

	/**
	 * See {@link JStandardGUIApplication}.
	 */
	@Override
	protected ArrayList<JMenu> setupMenus()
	{
		ArrayList<JMenu> menus = new ArrayList<JMenu>();
		JMenu menu = null;
		JMenu subMenu = null;
		JMenu subSubMenu = null;
		JMenuItem menuItem = null;
		JRadioButtonMenuItem radioButtonMenuItem = null;
		JCheckBoxMenuItem checkBoxMenuItem = null;
		ButtonGroup buttonGroup = null;
		fMenuItems = new HashMap<String,JMenuItem>();

			// file menu
			menu = new JMenu(I18NL10N.translate("menu.File"));
			menu.setMnemonic(I18NL10N.translateMnemonic(I18NL10N.translate("menu.File.Mnemonic")));

				menuItem = constructMenuItem(kActionCommandMenuItemFileExportToPNG,false);
				menuItem.setActionCommand(kActionCommandMenuItemFileExportToPNG);
				menuItem.addActionListener(this);
			menu.add(menuItem);

			menu.addSeparator();

				menuItem = constructMenuItem(kActionCommandMenuItemFileLoadFractal,false);
				menuItem.setActionCommand(kActionCommandMenuItemFileLoadFractal);
				menuItem.addActionListener(this);
			menu.add(menuItem);
				menuItem = constructMenuItem(kActionCommandMenuItemFileSaveFractal,false);
				menuItem.setActionCommand(kActionCommandMenuItemFileSaveFractal);
				menuItem.addActionListener(this);
			menu.add(menuItem);

			menu.addSeparator();

				menuItem = constructMenuItem(kActionCommandMenuItemFileLoadFractalParameters,false);
				menuItem.setActionCommand(kActionCommandMenuItemFileLoadFractalParameters);
				menuItem.addActionListener(this);
			menu.add(menuItem);
				menuItem = constructMenuItem(kActionCommandMenuItemFileSaveFractalParameters,false);
				menuItem.setActionCommand(kActionCommandMenuItemFileSaveFractalParameters);
				menuItem.addActionListener(this);
			menu.add(menuItem);

			menu.addSeparator();

				menuItem = constructMenuItem(kActionCommandMenuItemFileLoadZoomStack,false);
				menuItem.setActionCommand(kActionCommandMenuItemFileLoadZoomStack);
				menuItem.addActionListener(this);
			menu.add(menuItem);
				menuItem = constructMenuItem(kActionCommandMenuItemFileSaveZoomStack,false);
				menuItem.setActionCommand(kActionCommandMenuItemFileSaveZoomStack);
				menuItem.addActionListener(this);
			menu.add(menuItem);

//XXX
/*
			menu.addSeparator();

				menuItem = constructMenuItem(kActionCommandMenuItemFileSaveZoomAnimationSequence,false);
				menuItem.setActionCommand(kActionCommandMenuItemFileSaveZoomAnimationSequence);
				menuItem.addActionListener(this);
			menu.add(menuItem);
*/
		menus.add(menu);

			// navigation menu
			menu = new JMenu(I18NL10N.translate("menu.Navigation"));
			menu.setMnemonic(I18NL10N.translateMnemonic(I18NL10N.translate("menu.Navigation.Mnemonic")));
				menuItem = constructMenuItem(kActionCommandMenuItemNavigationPanLeft,false);
				menuItem.setActionCommand(kActionCommandMenuItemNavigationPanLeft);
				menuItem.addActionListener(this);
			menu.add(menuItem);
				menuItem = constructMenuItem(kActionCommandMenuItemNavigationPanRight,false);
				menuItem.setActionCommand(kActionCommandMenuItemNavigationPanRight);
				menuItem.addActionListener(this);
			menu.add(menuItem);
				menuItem = constructMenuItem(kActionCommandMenuItemNavigationPanUp,false);
				menuItem.setActionCommand(kActionCommandMenuItemNavigationPanUp);
				menuItem.addActionListener(this);
			menu.add(menuItem);
				menuItem = constructMenuItem(kActionCommandMenuItemNavigationPanDown,false);
				menuItem.setActionCommand(kActionCommandMenuItemNavigationPanDown);
				menuItem.addActionListener(this);
			menu.add(menuItem);
				menuItem = constructMenuItem(kActionCommandMenuItemNavigationSetPanningSize,false);
				menuItem.setActionCommand(kActionCommandMenuItemNavigationSetPanningSize);
				menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,ActionEvent.CTRL_MASK));
				menuItem.addActionListener(this);
			menu.add(menuItem);
				checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemNavigationInvertPanningDirections,false);
				checkBoxMenuItem.setSelected(false);
				checkBoxMenuItem.setActionCommand(kActionCommandMenuItemNavigationInvertPanningDirections);
				checkBoxMenuItem.addActionListener(this);
				fMenuItems.put(kActionCommandMenuItemNavigationInvertPanningDirections,checkBoxMenuItem);
			menu.add(checkBoxMenuItem);

			menu.addSeparator();

				checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemNavigationShowZoomInformation,false);
				checkBoxMenuItem.setSelected(true);
				checkBoxMenuItem.setActionCommand(kActionCommandMenuItemNavigationShowZoomInformation);
				checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,ActionEvent.CTRL_MASK));
				checkBoxMenuItem.addActionListener(this);
				fMenuItems.put(kActionCommandMenuItemNavigationShowZoomInformation,checkBoxMenuItem);
			menu.add(checkBoxMenuItem);
				checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemNavigationLockAspectRatio,false);
				checkBoxMenuItem.setSelected(true);
				checkBoxMenuItem.setActionCommand(kActionCommandMenuItemNavigationLockAspectRatio);
				checkBoxMenuItem.addActionListener(this);
				fMenuItems.put(kActionCommandMenuItemNavigationLockAspectRatio,checkBoxMenuItem);
			menu.add(checkBoxMenuItem);
				checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemNavigationCentredZooming,false);
				checkBoxMenuItem.setSelected(true);
				checkBoxMenuItem.setActionCommand(kActionCommandMenuItemNavigationCentredZooming);
				checkBoxMenuItem.addActionListener(this);
				fMenuItems.put(kActionCommandMenuItemNavigationCentredZooming,checkBoxMenuItem);
			menu.add(checkBoxMenuItem);
				menuItem = constructMenuItem(kActionCommandMenuItemNavigationResetZoom,false);
				menuItem.setActionCommand(kActionCommandMenuItemNavigationResetZoom);
				menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,ActionEvent.CTRL_MASK));
				menuItem.addActionListener(this);
			menu.add(menuItem);
				menuItem = constructMenuItem(kActionCommandMenuItemNavigationZoomToLevel,false);
				menuItem.setActionCommand(kActionCommandMenuItemNavigationZoomToLevel);
				menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK));
				menuItem.addActionListener(this);
			menu.add(menuItem);

			menu.addSeparator();

				checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemNavigationShowAxes,false);
				checkBoxMenuItem.setSelected(false);
				checkBoxMenuItem.setActionCommand(kActionCommandMenuItemNavigationShowAxes);
				checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,ActionEvent.CTRL_MASK));
				checkBoxMenuItem.addActionListener(this);
				fMenuItems.put(kActionCommandMenuItemNavigationShowAxes,checkBoxMenuItem);
			menu.add(checkBoxMenuItem);
				checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemNavigationInvertYAxis,false);
				checkBoxMenuItem.setSelected(false);
				checkBoxMenuItem.setActionCommand(kActionCommandMenuItemNavigationInvertYAxis);
				checkBoxMenuItem.addActionListener(this);
				fMenuItems.put(kActionCommandMenuItemNavigationInvertYAxis,checkBoxMenuItem);
			menu.add(checkBoxMenuItem);
				checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemNavigationShowCurrentLocation,false);
				checkBoxMenuItem.setSelected(true);
				checkBoxMenuItem.setActionCommand(kActionCommandMenuItemNavigationShowCurrentLocation);
				checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,ActionEvent.CTRL_MASK));
				checkBoxMenuItem.addActionListener(this);
				fMenuItems.put(kActionCommandMenuItemNavigationShowCurrentLocation,checkBoxMenuItem);
			menu.add(checkBoxMenuItem);
				checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemNavigationShowMagnifyingGlass,false);
				checkBoxMenuItem.setSelected(false);
				checkBoxMenuItem.setActionCommand(kActionCommandMenuItemNavigationShowMagnifyingGlass);
				checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,ActionEvent.CTRL_MASK));
				checkBoxMenuItem.addActionListener(this);
				fMenuItems.put(kActionCommandMenuItemNavigationShowMagnifyingGlass,checkBoxMenuItem);
			menu.add(checkBoxMenuItem);
				menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemNavigationSetMagnifyingGlassSize,false);
				menuItem.setActionCommand(kActionCommandMenuItemNavigationSetMagnifyingGlassSize);
				menuItem.addActionListener(this);
			menu.add(menuItem);

			menu.addSeparator();

				menuItem = constructMenuItem(kActionCommandMenuItemNavigationSpecifiyScreenBounds,false);
				menuItem.setActionCommand(kActionCommandMenuItemNavigationSpecifiyScreenBounds);
				menuItem.addActionListener(this);
			menu.add(menuItem);
				menuItem = constructMenuItem(kActionCommandMenuItemNavigationSpecifyCoordinates,false);
				menuItem.setActionCommand(kActionCommandMenuItemNavigationSpecifyCoordinates);
				menuItem.addActionListener(this);
			menu.add(menuItem);
		menus.add(menu);

			// fractal menu
			menu = new JMenu(I18NL10N.translate("menu.Fractal"));
			menu.setMnemonic(I18NL10N.translateMnemonic(I18NL10N.translate("menu.Fractal.Mnemonic")));

				buttonGroup = new ButtonGroup();
				radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalDoubleClickModeSwitchDualMainFractal,false);
				radioButtonMenuItem.setSelected(true);
				radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalDoubleClickModeSwitchDualMainFractal);
				radioButtonMenuItem.addActionListener(this);
				buttonGroup.add(radioButtonMenuItem);
				fMenuItems.put(kActionCommandMenuItemFractalDoubleClickModeSwitchDualMainFractal,radioButtonMenuItem);
			menu.add(radioButtonMenuItem);
				menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalSwitchFractalType,false);
				menuItem.setActionCommand(kActionCommandMenuItemFractalSwitchFractalType);
				menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK));
				menuItem.addActionListener(this);
			menu.add(menuItem);
				radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalDoubleClickModeSetOrbitStartingPoint,false);
				radioButtonMenuItem.setSelected(false);
				radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalDoubleClickModeSetOrbitStartingPoint);
				radioButtonMenuItem.addActionListener(this);
				buttonGroup.add(radioButtonMenuItem);
				fMenuItems.put(kActionCommandMenuItemFractalDoubleClickModeSetOrbitStartingPoint,radioButtonMenuItem);
			menu.add(radioButtonMenuItem);
				menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalResetOrbitStartingPoint,false);
				menuItem.setActionCommand(kActionCommandMenuItemFractalResetOrbitStartingPoint);
				menuItem.addActionListener(this);
			menu.add(menuItem);

			menu.addSeparator();

				checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemFractalShowInset,false);
				checkBoxMenuItem.setSelected(true);
				checkBoxMenuItem.setActionCommand(kActionCommandMenuItemFractalShowInset);
				checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,ActionEvent.CTRL_MASK));
				checkBoxMenuItem.addActionListener(this);
				fMenuItems.put(kActionCommandMenuItemFractalShowInset,checkBoxMenuItem);
			menu.add(checkBoxMenuItem);
				menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalSetInsetSize,false);
				menuItem.setActionCommand(kActionCommandMenuItemFractalSetInsetSize);
				menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U,ActionEvent.CTRL_MASK));
				menuItem.addActionListener(this);
			menu.add(menuItem);
				checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemFractalAutoZoomInset,false);
				checkBoxMenuItem.setSelected(false);
				checkBoxMenuItem.setActionCommand(kActionCommandMenuItemFractalAutoZoomInset);
				checkBoxMenuItem.addActionListener(this);
				fMenuItems.put(kActionCommandMenuItemFractalAutoZoomInset,checkBoxMenuItem);
			menu.add(checkBoxMenuItem);
				checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemFractalInsetFractalIsDeformedMainFractal,false);
				checkBoxMenuItem.setSelected(false);
				checkBoxMenuItem.setActionCommand(kActionCommandMenuItemFractalInsetFractalIsDeformedMainFractal);
				checkBoxMenuItem.addActionListener(this);
				fMenuItems.put(kActionCommandMenuItemFractalInsetFractalIsDeformedMainFractal,checkBoxMenuItem);
			menu.add(checkBoxMenuItem);

			menu.addSeparator();

				checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemFractalShowOrbits,false);
				checkBoxMenuItem.setSelected(false);
				checkBoxMenuItem.setActionCommand(kActionCommandMenuItemFractalShowOrbits);
				checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.CTRL_MASK));
				checkBoxMenuItem.addActionListener(this);
				fMenuItems.put(kActionCommandMenuItemFractalShowOrbits,checkBoxMenuItem);
			menu.add(checkBoxMenuItem);
				checkBoxMenuItem = constructCheckBoxMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalShowOrbitPaths,false);
				checkBoxMenuItem.setSelected(true);
				checkBoxMenuItem.setActionCommand(kActionCommandMenuItemFractalShowOrbitPaths);
				checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,ActionEvent.CTRL_MASK));
				checkBoxMenuItem.addActionListener(this);
				fMenuItems.put(kActionCommandMenuItemFractalShowOrbitPaths,checkBoxMenuItem);
			menu.add(checkBoxMenuItem);
				checkBoxMenuItem = constructCheckBoxMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalScaleOrbitsToScreen,false);
				checkBoxMenuItem.setSelected(false);
				checkBoxMenuItem.setActionCommand(kActionCommandMenuItemFractalScaleOrbitsToScreen);
				checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,ActionEvent.CTRL_MASK));
				checkBoxMenuItem.addActionListener(this);
				fMenuItems.put(kActionCommandMenuItemFractalScaleOrbitsToScreen,checkBoxMenuItem);
			menu.add(checkBoxMenuItem);
				checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemFractalShowOrbitAnalyses,false);
				checkBoxMenuItem.setSelected(false);
				checkBoxMenuItem.setActionCommand(kActionCommandMenuItemFractalShowOrbitAnalyses);
				checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,ActionEvent.CTRL_MASK));
				checkBoxMenuItem.addActionListener(this);
				fMenuItems.put(kActionCommandMenuItemFractalShowOrbitAnalyses,checkBoxMenuItem);
			menu.add(checkBoxMenuItem);
				checkBoxMenuItem = constructCheckBoxMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalShowIterationDistribution,false);
				checkBoxMenuItem.setSelected(false);
				checkBoxMenuItem.setActionCommand(kActionCommandMenuItemFractalShowIterationDistribution);
				checkBoxMenuItem.addActionListener(this);
				fMenuItems.put(kActionCommandMenuItemFractalShowIterationDistribution,checkBoxMenuItem);
			menu.add(checkBoxMenuItem);
				menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalSetOrbitAnalysesPanelSize,false);
				menuItem.setActionCommand(kActionCommandMenuItemFractalSetOrbitAnalysesPanelSize);
				menuItem.addActionListener(this);
			menu.add(menuItem);
				menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalSetMaxNrOfIterationsInOrbitAnalyses,false);
				menuItem.setActionCommand(kActionCommandMenuItemFractalSetMaxNrOfIterationsInOrbitAnalyses);
				menuItem.addActionListener(this);
			menu.add(menuItem);

			menu.addSeparator();

				subMenu = new JMenu(I18NL10N.translate(kActionCommandMenuItemFractalSpecifyFractalFamily));
				buttonGroup = new ButtonGroup();

					subSubMenu = new JMenu(I18NL10N.translate("menuItem.Fractal.Family.Classic"));
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyDefaultMandelbrotJulia,false);
						radioButtonMenuItem.setSelected(true);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyDefaultMandelbrotJulia);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyDefaultMandelbrotJulia,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyMandelbar,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMandelbar);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMandelbar,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyRandelbrot,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyRandelbrot);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyRandelbrot,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyRandelbrotSetNoiseLevel,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyRandelbrotSetNoiseLevel);
						menuItem.addActionListener(this);
						menuItem.setEnabled(false);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyRandelbrotSetNoiseLevel,menuItem);
					subSubMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyOriginalJulia,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyOriginalJulia);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyOriginalJulia,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);

					subSubMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyLambda,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyLambda);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyLambda,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyInverseLambda,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyInverseLambda);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyInverseLambda,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);

					subSubMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyBurningShip,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyBurningShip);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyBurningShip,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyBirdOfPrey,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyBirdOfPrey);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyBirdOfPrey,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyGlynn,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyGlynn);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyGlynn,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyGlynnSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyGlynnSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyGlynn);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyGlynnSetPower,menuItem);
					subSubMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilySpider,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilySpider);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilySpider,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
				subMenu.add(subSubMenu);

					subSubMenu = new JMenu(I18NL10N.translate("menuItem.Fractal.Family.Powers"));
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyMultibrot,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultibrot);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultibrot,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyMultibrotSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultibrotSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyMultibrot);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultibrotSetPower,menuItem);
					subSubMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyMultibrotPolynomial,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultibrotPolynomial);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultibrotPolynomial,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyMultibrotPolynomialSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultibrotPolynomialSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyMultibrotPolynomial);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultibrotPolynomialSetPower,menuItem);
					subSubMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyMultibrotParameter,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultibrotParameter);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultibrotParameter,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyMultibrotParameterSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultibrotParameterSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyMultibrotParameter);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultibrotParameterSetPower,menuItem);
					subSubMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyMultibrotInvertedParameter,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultibrotInvertedParameter);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultibrotInvertedParameter,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyMultibrotInvertedParameterSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultibrotInvertedParameterSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyMultibrotInvertedParameter);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultibrotInvertedParameterSetPower,menuItem);
					subSubMenu.add(menuItem);

					subSubMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyMultibar,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultibar);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultibar,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyMultibarSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultibarSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyMultibar);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultibarSetPower,menuItem);
					subSubMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyMultibarPolynomial,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultibarPolynomial);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultibarPolynomial,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyMultibarPolynomialSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultibarPolynomialSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyMultibarPolynomial);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultibarPolynomialSetPower,menuItem);
					subSubMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyMultibarParameter,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultibarParameter);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultibarParameter,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyMultibarParameterSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultibarParameterSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyMultibarParameter);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultibarParameterSetPower,menuItem);
					subSubMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyMultibarInvertedParameter,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultibarInvertedParameter);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultibarInvertedParameter,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyMultibarInvertedParameterSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultibarInvertedParameterSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyMultibarInvertedParameter);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultibarInvertedParameterSetPower,menuItem);
					subSubMenu.add(menuItem);

					subSubMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyBurningMultiShip,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyBurningMultiShip);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyBurningMultiShip,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyBurningMultiShipSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyBurningMultiShipSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyBurningMultiShip);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyBurningMultiShipSetPower,menuItem);
					subSubMenu.add(menuItem);

					subSubMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyMultiProductExpelbrot,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultiProductExpelbrot);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultiProductExpelbrot,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyMultiProductExpelbrotSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultiProductExpelbrotSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyMultiProductExpelbrot);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultiProductExpelbrotSetPower,menuItem);
					subSubMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyMultiSumExpelbrot,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultiSumExpelbrot);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultiSumExpelbrot,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyMultiSumExpelbrotSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultiSumExpelbrotSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyMultiSumExpelbrot);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultiSumExpelbrotSetPower,menuItem);
					subSubMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyMultiProductExparbrot,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultiProductExparbrot);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultiProductExparbrot,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyMultiProductExparbrotSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultiProductExparbrotSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyMultiProductExparbrot);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultiProductExparbrotSetPower,menuItem);
					subSubMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyMultiSumExparbrot,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultiSumExparbrot);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultiSumExparbrot,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyMultiSumExparbrotSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMultiSumExparbrotSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyMultiSumExparbrot);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMultiSumExparbrotSetPower,menuItem);
					subSubMenu.add(menuItem);
				subMenu.add(subSubMenu);

					subSubMenu = new JMenu(I18NL10N.translate("menuItem.Fractal.Family.Trigonometric"));
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyTrigonometricPowerSine,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyTrigonometricPowerSine);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyTrigonometricPowerSine,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyTrigonometricPowerSineSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyTrigonometricPowerSineSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyTrigonometricPowerSine);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyTrigonometricPowerSineSetPower,menuItem);
					subSubMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyTrigonometricPowerCosine,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyTrigonometricPowerCosine);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyTrigonometricPowerCosine,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyTrigonometricPowerCosineSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyTrigonometricPowerCosineSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyTrigonometricPowerCosine);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyTrigonometricPowerCosineSetPower,menuItem);
					subSubMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyTrigonometricPowerTangent,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyTrigonometricPowerTangent);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyTrigonometricPowerTangent,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyTrigonometricPowerTangentSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyTrigonometricPowerTangentSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyTrigonometricPowerTangent);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyTrigonometricPowerTangentSetPower,menuItem);
					subSubMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyTrigonometricPowerCotangent,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyTrigonometricPowerCotangent);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyTrigonometricPowerCotangent,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyTrigonometricPowerCotangentSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyTrigonometricPowerCotangentSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyTrigonometricPowerCotangent);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyTrigonometricPowerCotangentSetPower,menuItem);
					subSubMenu.add(menuItem);

					subSubMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiSine,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiSine);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiSine,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiSineSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiSineSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiSine);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiSineSetPower,menuItem);
					subSubMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCosine,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCosine);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCosine,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCosineSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCosineSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCosine);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCosineSetPower,menuItem);
					subSubMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiTangent,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiTangent);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiTangent,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiTangentSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiTangentSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiTangent);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiTangentSetPower,menuItem);
					subSubMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCotangent,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCotangent);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCotangent,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCotangentSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCotangentSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCotangent);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCotangentSetPower,menuItem);
					subSubMenu.add(menuItem);

				subMenu.add(subSubMenu);

					subSubMenu = new JMenu(I18NL10N.translate("menuItem.Fractal.Family.Special"));
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyCactus,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyCactus);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyCactus,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyBeauty1,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyBeauty1);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyBeauty1,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyBeauty2,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyBeauty2);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyBeauty2,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);

					subSubMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyDucks,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyDucks);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyDucks,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyDucksSetFixedNrOfIterations,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyDucksSetFixedNrOfIterations);
						menuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyDucksSetFixedNrOfIterations,menuItem);
					subSubMenu.add(menuItem);

					subSubMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyBarnsleyTree,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyBarnsleyTree);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyBarnsleyTree,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);

					subSubMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyCollatz,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyCollatz);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyCollatz,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);

					subSubMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyPhoenix,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyPhoenix);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyPhoenix,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyManowar,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyManowar);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyManowar,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
				subMenu.add(subSubMenu);

					subSubMenu = new JMenu(I18NL10N.translate("menuItem.Fractal.Family.NewtonRaphson"));
						menuItem = constructMenuItem(kActionCommandMenuItemFractalFamilyNewtonRaphsonSetConvergenceParameters,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyNewtonRaphsonSetConvergenceParameters);
						menuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyNewtonRaphsonSetConvergenceParameters,menuItem);
					subSubMenu.add(menuItem);
						checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemFractalFamilyNewtonRaphsonAutomaticRootDetectionEnabled,false);
						checkBoxMenuItem.setSelected(true);
						checkBoxMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyNewtonRaphsonAutomaticRootDetectionEnabled);
						checkBoxMenuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyNewtonRaphsonAutomaticRootDetectionEnabled,checkBoxMenuItem);
					subSubMenu.add(checkBoxMenuItem);

					subSubMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyNewtonRaphsonPower,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyNewtonRaphsonPower);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyNewtonRaphsonPower,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyNewtonRaphsonPowerSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyNewtonRaphsonPowerSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyNewtonRaphsonPower);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyNewtonRaphsonPowerSetPower,menuItem);
					subSubMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyNewtonRaphsonPowerPolynomial,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyNewtonRaphsonPowerPolynomial);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyNewtonRaphsonPowerPolynomial,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyNewtonRaphsonPowerPolynomialSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyNewtonRaphsonPowerPolynomialSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyNewtonRaphsonPowerPolynomial);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyNewtonRaphsonPowerPolynomialSetPower,menuItem);
					subSubMenu.add(menuItem);

					subSubMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial1,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial1);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial1,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial2,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial2);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial2,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial3,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial3);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial3,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial4,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial4);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial4,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);

					subSubMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSine,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSine);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSine,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSineSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSineSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSine);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSineSetPower,menuItem);
					subSubMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSine,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSine);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSine,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSineSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSineSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSine);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSineSetPower,menuItem);
					subSubMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSineOffset,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSineOffset);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSineOffset,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSineOffsetSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSineOffsetSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSineOffset);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSineOffsetSetPower,menuItem);
					subSubMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSineOffset,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSineOffset);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSineOffset,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSineOffsetSetPower,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSineOffsetSetPower);
						menuItem.addActionListener(this);
						fFractalFamilyMenuItems.add(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSineOffset);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSineOffsetSetPower,menuItem);
					subSubMenu.add(menuItem);

					subSubMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyNova,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyNova);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyNova,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
				subMenu.add(subSubMenu);

					subSubMenu = new JMenu(I18NL10N.translate("menuItem.Fractal.Family.Magnet"));
						menuItem = constructMenuItem(kActionCommandMenuItemFractalFamilyMagnetSetConvergenceParameters,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMagnetSetConvergenceParameters);
						menuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMagnetSetConvergenceParameters,menuItem);
					subSubMenu.add(menuItem);

					subSubMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyMagnetTypeI,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMagnetTypeI);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMagnetTypeI,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyMagnetTypeII,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMagnetTypeII);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMagnetTypeII,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
				subMenu.add(subSubMenu);

					subSubMenu = new JMenu(I18NL10N.translate("menuItem.Fractal.Family.MarkusLyapunov"));
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyMarkusLyapunov,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMarkusLyapunov);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMarkusLyapunov,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalFamilyMarkusLyapunovSetRootSequence,false);
						menuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMarkusLyapunovSetRootSequence);
						menuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMarkusLyapunovSetRootSequence,menuItem);
					subSubMenu.add(menuItem);

					subSubMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyMarkusLyapunovLogisticBifurcation,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMarkusLyapunovLogisticBifurcation);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMarkusLyapunovLogisticBifurcation,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyMarkusLyapunovJellyfish,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMarkusLyapunovJellyfish);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMarkusLyapunovJellyfish,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyMarkusLyapunovZirconZity,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyMarkusLyapunovZirconZity);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyMarkusLyapunovZirconZity,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
				subMenu.add(subSubMenu);

			menu.add(subMenu);

				menuItem = constructMenuItem(kActionCommandMenuItemFractalSetMaxNrOfIterations,false);
				menuItem.setActionCommand(kActionCommandMenuItemFractalSetMaxNrOfIterations);
				menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,ActionEvent.CTRL_MASK));
				menuItem.addActionListener(this);
			menu.add(menuItem);
				menuItem = constructMenuItem(kActionCommandMenuItemFractalSetEscapeRadius,false);
				menuItem.setActionCommand(kActionCommandMenuItemFractalSetEscapeRadius);
				menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,ActionEvent.CTRL_MASK));
				menuItem.addActionListener(this);
			menu.add(menuItem);

			menu.addSeparator();

				menuItem = constructMenuItem(kActionCommandMenuItemFractalCopyCoordinates,false);
				menuItem.setActionCommand(kActionCommandMenuItemFractalCopyCoordinates);
				menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK));
				menuItem.addActionListener(this);
			menu.add(menuItem);
				menuItem = constructMenuItem(kActionCommandMenuItemFractalRefreshScreen,false);
				menuItem.setActionCommand(kActionCommandMenuItemFractalRefreshScreen);
				menuItem.addActionListener(this);
			menu.add(menuItem);
		menus.add(menu);

			// colour map menu
			menu = new JMenu(I18NL10N.translate("menu.ColorMap"));
			menu.setMnemonic(I18NL10N.translateMnemonic(I18NL10N.translate("menu.ColorMap.Mnemonic")));

				subMenu = new JMenu(I18NL10N.translate("menu.ColorMap.SpecifyColorMap"));
					try {
						buttonGroup = new ButtonGroup();
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorBone,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-bone.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorBone);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorBone,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorCopper,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-copper.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorCopper);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorCopper,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorDiscontinuousBlueWhiteGreen,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-discontinuousbluewhitegreen.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorDiscontinuousBlueWhiteGreen);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorDiscontinuousBlueWhiteGreen,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorDiscontinuousDarkRedYellow,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-discontinuousdarkredyellow.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorDiscontinuousDarkRedYellow);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorDiscontinuousDarkRedYellow,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorBlackAndWhite,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-blackandwhite.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorBlackAndWhite);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorBlackAndWhite,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorGrayScale,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-grayscale.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorGrayScale);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorGrayScale,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorGreenRedDiverging,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-greenreddiverging.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorGreenRedDiverging);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorGreenRedDiverging,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorHot,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-hot.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorHot);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorHot,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorJet,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-jet.png")));
						radioButtonMenuItem.setSelected(true);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorJet);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorJet,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorHueSaturationBrightness,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-huesaturationbrightness.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorHueSaturationBrightness);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorHueSaturationBrightness,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorSeparatedRGB,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-separatedredgreenblue.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorSeparatedRGB);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorSeparatedRGB,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorRed,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-red.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorRed);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorRed,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorGreen,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-green.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorGreen);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorGreen,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorBlue,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-blue.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorBlue);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorBlue,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorYellow,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-yellow.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorYellow);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorYellow,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorCyan,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-cyan.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorCyan);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorCyan,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorMagenta,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-magenta.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorMagenta);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorMagenta,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUltraLightPastel,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-ultralightpastel.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUltraLightPastel);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUltraLightPastel,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorLightPastel,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-lightpastel.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorLightPastel);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorLightPastel,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorDarkPastel,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-darkpastel.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorDarkPastel);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorDarkPastel,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorGreens,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-greens.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorGreens);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorGreens,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorBlues,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-blues.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorBlues);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorBlues,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorYellowBrowns,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-yellowbrowns.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorYellowBrowns);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorYellowBrowns,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorVioletPurples,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-violetpurples.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorVioletPurples);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorVioletPurples,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorDeepSpace,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-deepspace.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorDeepSpace);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorDeepSpace,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);

					subMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorCustom,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorCustom);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorCustom,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);

						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapExteriorSetCustomColorMap,false);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorSetCustomColorMap);
						menuItem.addActionListener(this);
					subMenu.add(menuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapExteriorConvertCurrentColorMapToCustomColorMap,false);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorConvertCurrentColorMapToCustomColorMap);
						menuItem.addActionListener(this);
					subMenu.add(menuItem);
				}
				catch (FileDoesNotExistException exc) {
					// ignore
				}
				menu.add(subMenu);

					checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemColorMapExteriorInvertColorMap,false);
					checkBoxMenuItem.setSelected(false);
					checkBoxMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorInvertColorMap);
					checkBoxMenuItem.addActionListener(this);
					fMenuItems.put(kActionCommandMenuItemColorMapExteriorInvertColorMap,checkBoxMenuItem);
				menu.add(checkBoxMenuItem);
					checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap,false);
					checkBoxMenuItem.setSelected(false);
					checkBoxMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap);
					checkBoxMenuItem.addActionListener(this);
					fMenuItems.put(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap,checkBoxMenuItem);
				menu.add(checkBoxMenuItem);
					checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemColorMapCalculateAdvancedColoring,false);
					checkBoxMenuItem.setSelected(true);
					checkBoxMenuItem.setActionCommand(kActionCommandMenuItemColorMapCalculateAdvancedColoring);
					checkBoxMenuItem.addActionListener(this);
					fMenuItems.put(kActionCommandMenuItemColorMapCalculateAdvancedColoring,checkBoxMenuItem);
				menu.add(checkBoxMenuItem);
					checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemColorMapUseTigerStripes,false);
					checkBoxMenuItem.setSelected(false);
					checkBoxMenuItem.setActionCommand(kActionCommandMenuItemColorMapUseTigerStripes);
					checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,ActionEvent.CTRL_MASK));
					checkBoxMenuItem.addActionListener(this);
					fMenuItems.put(kActionCommandMenuItemColorMapUseTigerStripes,checkBoxMenuItem);
				menu.add(checkBoxMenuItem);

				try {
					subMenu = new JMenu(I18NL10N.translate("menu.ColorMap.SpecifyTigerColorMap"));
						buttonGroup = new ButtonGroup();
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerBone,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-bone.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerBone);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerBone,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerCopper,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-copper.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerCopper);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerCopper,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerDiscontinuousBlueWhiteGreen,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-discontinuousbluewhitegreen.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerDiscontinuousBlueWhiteGreen);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerDiscontinuousBlueWhiteGreen,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerDiscontinuousDarkRedYellow,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-discontinuousdarkredyellow.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerDiscontinuousDarkRedYellow);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerDiscontinuousDarkRedYellow,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerBlackAndWhite,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-blackandwhite.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerBlackAndWhite);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerBlackAndWhite,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerGrayScale,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-grayscale.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerGrayScale);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerGrayScale,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerGreenRedDiverging,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-greenreddiverging.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerGreenRedDiverging);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerGreenRedDiverging,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerHot,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-hot.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerHot);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerHot,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerJet,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-jet.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerJet);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerJet,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerHueSaturationBrightness,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-huesaturationbrightness.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerHueSaturationBrightness);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerHueSaturationBrightness,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerSeparatedRGB,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-separatedredgreenblue.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerSeparatedRGB);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerSeparatedRGB,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerRed,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-red.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerRed);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerRed,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerGreen,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-green.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerGreen);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerGreen,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerBlue,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-blue.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerBlue);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerBlue,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerYellow,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-yellow.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerYellow);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerYellow,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerCyan,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-cyan.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerCyan);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerCyan,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerMagenta,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-magenta.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerMagenta);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerMagenta,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerUltraLightPastel,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-ultralightpastel.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerUltraLightPastel);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerUltraLightPastel,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerLightPastel,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-lightpastel.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerLightPastel);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerLightPastel,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerDarkPastel,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-darkpastel.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerDarkPastel);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerDarkPastel,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerGreens,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-greens.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerGreens);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerGreens,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerBlues,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-blues.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerBlues);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerBlues,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerYellowBrowns,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-yellowbrowns.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerYellowBrowns);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerYellowBrowns,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerVioletPurples,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-violetpurples.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerVioletPurples);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerVioletPurples,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerDeepSpace,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-deepspace.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerDeepSpace);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerDeepSpace,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);

						subMenu.addSeparator();

							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerCustom,false);
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerCustom);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerCustom,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapTigerSetCustomColorMap,false);
							menuItem.setActionCommand(kActionCommandMenuItemColorMapTigerSetCustomColorMap);
							menuItem.addActionListener(this);
						subMenu.add(menuItem);
							menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapTigerConvertCurrentColorMapToCustomColorMap,false);
							menuItem.setActionCommand(kActionCommandMenuItemColorMapTigerConvertCurrentColorMapToCustomColorMap);
							menuItem.addActionListener(this);
						subMenu.add(menuItem);

						subMenu.addSeparator();

							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerUseFixedColor,false);
							radioButtonMenuItem.setSelected(true);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerUseFixedColor);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerUseFixedColor,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
							menuItem = constructMenuItem(kActionCommandMenuItemColorMapTigerSetFixedColor,false);
							fTigerStripeColorLabelDecorator = new ColorLabelDecorator(Color.BLACK);
							menuItem.setIcon(fTigerStripeColorLabelDecorator);
							menuItem.setActionCommand(kActionCommandMenuItemColorMapTigerSetFixedColor);
							menuItem.addActionListener(this);
						subMenu.add(menuItem);
					}
					catch (FileDoesNotExistException exc) {
						// ignore
					}
				menu.add(subMenu);

					subMenu = new JMenu(I18NL10N.translate("menu.ColorMap.SetupExteriorColorMap"));
						ButtonGroup bgColorMapUsage = new ButtonGroup();
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseFixedColor,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseFixedColor);
						radioButtonMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,ActionEvent.CTRL_MASK));
						radioButtonMenuItem.addActionListener(this);
						bgColorMapUsage.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseFixedColor,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapExteriorSetFixedColor,false);
						fExteriorColorLabelDecorator = new ColorLabelDecorator(Color.WHITE);
						menuItem.setIcon(fExteriorColorLabelDecorator);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorSetFixedColor);
						menuItem.addActionListener(this);
					subMenu.add(menuItem);

					subMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseDiscreteLevelSets,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseDiscreteLevelSets);
						radioButtonMenuItem.addActionListener(this);
						bgColorMapUsage.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseDiscreteLevelSets,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseNormalisedLevelSets,false);
						radioButtonMenuItem.setSelected(true);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseNormalisedLevelSets);
						radioButtonMenuItem.addActionListener(this);
						bgColorMapUsage.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseNormalisedLevelSets,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseExponentiallySmoothedLevelSets,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseExponentiallySmoothedLevelSets);
						radioButtonMenuItem.addActionListener(this);
						bgColorMapUsage.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseExponentiallySmoothedLevelSets,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseSectorDecomposition,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseSectorDecomposition);
						radioButtonMenuItem.addActionListener(this);
						bgColorMapUsage.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseSectorDecomposition,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapExteriorSetDecompositionSectorRange,false);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorSetDecompositionSectorRange);
						menuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorSetDecompositionSectorRange,menuItem);
					subMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseRealComponent,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseRealComponent);
						radioButtonMenuItem.addActionListener(this);
						bgColorMapUsage.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseRealComponent,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseImaginaryComponent,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseImaginaryComponent);
						radioButtonMenuItem.addActionListener(this);
						bgColorMapUsage.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseImaginaryComponent,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseModulus,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseModulus);
						radioButtonMenuItem.addActionListener(this);
						bgColorMapUsage.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseModulus,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseAngle,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseAngle);
						radioButtonMenuItem.addActionListener(this);
						bgColorMapUsage.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseAngle,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseMaxModulus,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseMaxModulus);
						radioButtonMenuItem.addActionListener(this);
						bgColorMapUsage.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseMaxModulus,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseTotalDistance,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseTotalDistance);
						radioButtonMenuItem.addActionListener(this);
						bgColorMapUsage.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseTotalDistance,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseAverageDistance,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseAverageDistance);
						radioButtonMenuItem.addActionListener(this);
						bgColorMapUsage.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseAverageDistance,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseTotalAngle,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseTotalAngle);
						radioButtonMenuItem.addActionListener(this);
						bgColorMapUsage.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseTotalAngle,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseLyapunovExponent,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseLyapunovExponent);
						radioButtonMenuItem.addActionListener(this);
						bgColorMapUsage.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseLyapunovExponent,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);

					subMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseCurvature,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseCurvature);
						radioButtonMenuItem.addActionListener(this);
						bgColorMapUsage.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseCurvature,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseStriping,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseStriping);
						radioButtonMenuItem.addActionListener(this);
						bgColorMapUsage.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseStriping,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapExteriorSetStripingDensity,false);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorSetStripingDensity);
						menuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorSetStripingDensity,menuItem);
					subMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseMinimumGaussianIntegersDistance,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseMinimumGaussianIntegersDistance);
						radioButtonMenuItem.addActionListener(this);
						bgColorMapUsage.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseMinimumGaussianIntegersDistance,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseAverageGaussianIntegersDistance,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseAverageGaussianIntegersDistance);
						radioButtonMenuItem.addActionListener(this);
						bgColorMapUsage.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseAverageGaussianIntegersDistance,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapExteriorSetGaussianIntegersTrapFactor,false);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorSetGaussianIntegersTrapFactor);
						menuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorSetGaussianIntegersTrapFactor,menuItem);
					subMenu.add(menuItem);

					subMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseDiscreteRoots,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseDiscreteRoots);
						radioButtonMenuItem.addActionListener(this);
						radioButtonMenuItem.setEnabled(false);
						bgColorMapUsage.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseDiscreteRoots,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseSmoothRoots,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseSmoothRoots);
						radioButtonMenuItem.addActionListener(this);
						radioButtonMenuItem.setEnabled(false);
						bgColorMapUsage.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseSmoothRoots,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapExteriorSetBrightnessFactor,false);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorSetBrightnessFactor);
						menuItem.addActionListener(this);
						menuItem.setEnabled(false);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorSetBrightnessFactor,menuItem);
					subMenu.add(menuItem);
				menu.add(subMenu);

					subMenu = new JMenu(I18NL10N.translate("menu.ColorMap.SetupInteriorColorMap"));
						try {
							subSubMenu = new JMenu(I18NL10N.translate("menu.ColorMap.SpecifyInteriorColorMap"));
								buttonGroup = new ButtonGroup();
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorBone,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-bone.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorBone);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorBone,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorCopper,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-copper.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorCopper);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorCopper,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorDiscontinuousBlueWhiteGreen,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-discontinuousbluewhitegreen.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorDiscontinuousBlueWhiteGreen);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorDiscontinuousBlueWhiteGreen,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorDiscontinuousDarkRedYellow,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-discontinuousdarkredyellow.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorDiscontinuousDarkRedYellow);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorDiscontinuousDarkRedYellow,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorBlackAndWhite,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-blackandwhite.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorBlackAndWhite);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorBlackAndWhite,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorGrayScale,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-grayscale.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorGrayScale);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorGrayScale,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorGreenRedDiverging,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-greenreddiverging.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorGreenRedDiverging);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorGreenRedDiverging,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorHot,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-hot.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorHot);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorHot,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorJet,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-jet.png")));
									radioButtonMenuItem.setSelected(true);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorJet);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorJet,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorHueSaturationBrightness,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-huesaturationbrightness.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorHueSaturationBrightness);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorHueSaturationBrightness,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorSeparatedRGB,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-separatedredgreenblue.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorSeparatedRGB);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorSeparatedRGB,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorRed,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-red.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorRed);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorRed,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorGreen,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-green.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorGreen);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorGreen,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorBlue,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-blue.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorBlue);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorBlue,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorYellow,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-yellow.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorYellow);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorYellow,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorCyan,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-cyan.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorCyan);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorCyan,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorMagenta,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-magenta.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorMagenta);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorMagenta,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUltraLightPastel,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-ultralightpastel.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUltraLightPastel);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorUltraLightPastel,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorLightPastel,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-lightpastel.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorLightPastel);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorLightPastel,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorDarkPastel,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-darkpastel.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorDarkPastel);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorDarkPastel,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorGreens,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-greens.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorGreens);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorGreens,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorBlues,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-blues.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorBlues);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorBlues,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorYellowBrowns,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-yellowbrowns.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorYellowBrowns);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorYellowBrowns,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorVioletPurples,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-violetpurples.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorVioletPurples);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorVioletPurples,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorDeepSpace,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-deepspace.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorDeepSpace);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorDeepSpace,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);

								subSubMenu.addSeparator();

									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorCustom,false);
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorCustom);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorCustom,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
									menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapInteriorSetCustomColorMap,false);
									menuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorSetCustomColorMap);
									menuItem.addActionListener(this);
								subSubMenu.add(menuItem);
									menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapInteriorConvertCurrentColorMapToCustomColorMap,false);
									menuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorConvertCurrentColorMapToCustomColorMap);
									menuItem.addActionListener(this);
								subSubMenu.add(menuItem);
						}
						catch (FileDoesNotExistException exc) {
							// ignore
						}
					subMenu.add(subSubMenu);

						checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemColorMapInteriorInvertColorMap,false);
						checkBoxMenuItem.setSelected(false);
						checkBoxMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorInvertColorMap);
						checkBoxMenuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorInvertColorMap,checkBoxMenuItem);
					subMenu.add(checkBoxMenuItem);
						checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap,false);
						checkBoxMenuItem.setSelected(false);
						checkBoxMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap);
						checkBoxMenuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap,checkBoxMenuItem);
					subMenu.add(checkBoxMenuItem);

					subMenu.addSeparator();

					buttonGroup = new ButtonGroup();
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseFixedColor,false);
						radioButtonMenuItem.setSelected(true);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseFixedColor);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseFixedColor,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapInteriorSetFixedColor,false);
						fInteriorColorLabelDecorator = new ColorLabelDecorator();
						menuItem.setIcon(fInteriorColorLabelDecorator);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorSetFixedColor);
						menuItem.addActionListener(this);
					subMenu.add(menuItem);

					subMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseExponentiallySmoothedLevelSets,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseExponentiallySmoothedLevelSets);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseExponentiallySmoothedLevelSets,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseSectorDecomposition,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseSectorDecomposition);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseSectorDecomposition,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapInteriorSetDecompositionSectorRange,false);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorSetDecompositionSectorRange);
						menuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorSetDecompositionSectorRange,menuItem);
					subMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseRealComponent,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseRealComponent);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseRealComponent,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseImaginaryComponent,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseImaginaryComponent);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseImaginaryComponent,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseModulus,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseModulus);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseModulus,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseAngle,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseAngle);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseAngle,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseMaxModulus,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseMaxModulus);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseMaxModulus,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseTotalDistance,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseTotalDistance);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseTotalDistance,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseAverageDistance,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseAverageDistance);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseAverageDistance,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseTotalAngle,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseTotalAngle);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseTotalAngle,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseLyapunovExponent,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseLyapunovExponent);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseLyapunovExponent,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);

					subMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseCurvature,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseCurvature);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseCurvature,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseStriping,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseStriping);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseStriping,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapInteriorSetStripingDensity,false);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorSetStripingDensity);
						menuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorSetStripingDensity,menuItem);
					subMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseMinimumGaussianIntegersDistance,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseMinimumGaussianIntegersDistance);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseMinimumGaussianIntegersDistance,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseAverageGaussianIntegersDistance,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseAverageGaussianIntegersDistance);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseAverageGaussianIntegersDistance,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapInteriorSetGaussianIntegersTrapFactor,false);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorSetGaussianIntegersTrapFactor);
						menuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorSetGaussianIntegersTrapFactor,menuItem);
					subMenu.add(menuItem);
				menu.add(subMenu);

				menu.addSeparator();

					menuItem = constructMenuItem(kActionCommandMenuItemColorMapUseBinaryDecomposition,false);
					menuItem.setActionCommand(kActionCommandMenuItemColorMapUseBinaryDecomposition);
					menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,ActionEvent.CTRL_MASK));
					menuItem.addActionListener(this);
				menu.add(menuItem);
					menuItem = constructMenuItem(kActionCommandMenuItemColorMapUseContours,false);
					menuItem.setActionCommand(kActionCommandMenuItemColorMapUseContours);
					menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,ActionEvent.CTRL_MASK));
					menuItem.addActionListener(this);
				menu.add(menuItem);
					menuItem = constructMenuItem(kActionCommandMenuItemColorMapUseDarkSofteningFilter,false);
					menuItem.setActionCommand(kActionCommandMenuItemColorMapUseDarkSofteningFilter);
					menuItem.addActionListener(this);
				menu.add(menuItem);
					menuItem = constructMenuItem(kActionCommandMenuItemColorMapResetToDefault,false);
					menuItem.setActionCommand(kActionCommandMenuItemColorMapResetToDefault);
					menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J,ActionEvent.CTRL_MASK));
					menuItem.addActionListener(this);
				menu.add(menuItem);

				menu.addSeparator();

					buttonGroup = new ButtonGroup();
	 				radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapUseLinearScaling,false);
	 				radioButtonMenuItem.setSelected(true);
	 				radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapUseLinearScaling);
	 				radioButtonMenuItem.addActionListener(this);
	 				buttonGroup.add(radioButtonMenuItem);
					fMenuItems.put(kActionCommandMenuItemColorMapUseLinearScaling,radioButtonMenuItem);
				menu.add(radioButtonMenuItem);
					radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapUseLogarithmicScaling,false);
					radioButtonMenuItem.setSelected(false);
					radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapUseLogarithmicScaling);
					radioButtonMenuItem.addActionListener(this);
					buttonGroup.add(radioButtonMenuItem);
					fMenuItems.put(kActionCommandMenuItemColorMapUseLogarithmicScaling,radioButtonMenuItem);
				menu.add(radioButtonMenuItem);
					radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapUseExponentialScaling,false);
					radioButtonMenuItem.setSelected(false);
					radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapUseExponentialScaling);
					radioButtonMenuItem.addActionListener(this);
					buttonGroup.add(radioButtonMenuItem);
					fMenuItems.put(kActionCommandMenuItemColorMapUseExponentialScaling,radioButtonMenuItem);
				menu.add(radioButtonMenuItem);
					radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapUseSqrtScaling,false);
					radioButtonMenuItem.setSelected(false);
					radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapUseSqrtScaling);
					radioButtonMenuItem.addActionListener(this);
					buttonGroup.add(radioButtonMenuItem);
					fMenuItems.put(kActionCommandMenuItemColorMapUseSqrtScaling,radioButtonMenuItem);
				menu.add(radioButtonMenuItem);
					menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapSetScalingParameters,false);
					menuItem.setActionCommand(kActionCommandMenuItemColorMapSetScalingParameters);
					menuItem.addActionListener(this);
				menu.add(menuItem);
					radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapUseRankOrderScaling,false);
					radioButtonMenuItem.setSelected(false);
					radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapUseRankOrderScaling);
					radioButtonMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,ActionEvent.CTRL_MASK));
					radioButtonMenuItem.addActionListener(this);
					buttonGroup.add(radioButtonMenuItem);
					fMenuItems.put(kActionCommandMenuItemColorMapUseRankOrderScaling,radioButtonMenuItem);
				menu.add(radioButtonMenuItem);
					checkBoxMenuItem = constructCheckBoxMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapRestrictHighIterationCountColors,false);
					checkBoxMenuItem.setSelected(true);
					checkBoxMenuItem.setActionCommand(kActionCommandMenuItemColorMapRestrictHighIterationCountColors);
					checkBoxMenuItem.addActionListener(this);
					fMenuItems.put(kActionCommandMenuItemColorMapRestrictHighIterationCountColors,checkBoxMenuItem);
				menu.add(checkBoxMenuItem);

				menu.addSeparator();

					menuItem = constructMenuItem(kActionCommandMenuItemColorMapSetIterationRange,false);
					menuItem.setActionCommand(kActionCommandMenuItemColorMapSetIterationRange);
					menuItem.addActionListener(this);
				menu.add(menuItem);
					checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemColorMapRepeatColors,false);
					checkBoxMenuItem.setSelected(false);
					checkBoxMenuItem.setActionCommand(kActionCommandMenuItemColorMapRepeatColors);
					checkBoxMenuItem.addActionListener(this);
					fMenuItems.put(kActionCommandMenuItemColorMapRepeatColors,checkBoxMenuItem);
				menu.add(checkBoxMenuItem);
					menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapSetColorRepetition,false);
					menuItem.setActionCommand(kActionCommandMenuItemColorMapSetColorRepetition);
					menuItem.addActionListener(this);
				menu.add(menuItem);
					menuItem = constructMenuItem(kActionCommandMenuItemColorMapSetColorOffset,false);
					menuItem.setActionCommand(kActionCommandMenuItemColorMapSetColorOffset);
					menuItem.addActionListener(this);
				menu.add(menuItem);
					checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemColorMapCycleColors,false);
					checkBoxMenuItem.setSelected(false);
					checkBoxMenuItem.setActionCommand(kActionCommandMenuItemColorMapCycleColors);
					checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K,ActionEvent.CTRL_MASK));
					checkBoxMenuItem.addActionListener(this);
					fMenuItems.put(kActionCommandMenuItemColorMapCycleColors,checkBoxMenuItem);
				menu.add(checkBoxMenuItem);
					menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapSetColorCyclingParameters,false);
					menuItem.setActionCommand(kActionCommandMenuItemColorMapSetColorCyclingParameters);
					menuItem.addActionListener(this);
				menu.add(menuItem);

				menu.addSeparator();

					buttonGroup = new ButtonGroup();
					radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapFullColorRange,false);
					radioButtonMenuItem.setSelected(true);
					radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapFullColorRange);
					radioButtonMenuItem.addActionListener(this);
					buttonGroup.add(radioButtonMenuItem);
					fMenuItems.put(kActionCommandMenuItemColorMapFullColorRange,radioButtonMenuItem);
				menu.add(radioButtonMenuItem);
					radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapUseLimitedContinuousColorRange,false);
					radioButtonMenuItem.setSelected(false);
					radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapUseLimitedContinuousColorRange);
					radioButtonMenuItem.addActionListener(this);
					buttonGroup.add(radioButtonMenuItem);
					fMenuItems.put(kActionCommandMenuItemColorMapUseLimitedContinuousColorRange,radioButtonMenuItem);
				menu.add(radioButtonMenuItem);
					menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapSetLimitedContinuousColorRange,false);
					menuItem.setActionCommand(kActionCommandMenuItemColorMapSetLimitedContinuousColorRange);
					menuItem.addActionListener(this);
				menu.add(menuItem);
					radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapUseLimitedDiscreteColorRange,false);
					radioButtonMenuItem.setSelected(false);
					radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapUseLimitedDiscreteColorRange);
					radioButtonMenuItem.addActionListener(this);
					buttonGroup.add(radioButtonMenuItem);
					fMenuItems.put(kActionCommandMenuItemColorMapUseLimitedDiscreteColorRange,radioButtonMenuItem);
				menu.add(radioButtonMenuItem);
					menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapSetLimitedDiscreteColorRange,false);
					menuItem.setActionCommand(kActionCommandMenuItemColorMapSetLimitedDiscreteColorRange);
					menuItem.addActionListener(this);
					fMenuItems.put(kActionCommandMenuItemColorMapSetLimitedDiscreteColorRange,menuItem);
				menu.add(menuItem);

				menu.addSeparator();

					checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemColorMapUsePostProcessingFilters,false);
					checkBoxMenuItem.setSelected(false);
					checkBoxMenuItem.setActionCommand(kActionCommandMenuItemColorMapUsePostProcessingFilters);
					checkBoxMenuItem.addActionListener(this);
					fMenuItems.put(kActionCommandMenuItemColorMapUsePostProcessingFilters,checkBoxMenuItem);
				menu.add(checkBoxMenuItem);
					menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorSetupPostProcessingFilters,false);
					menuItem.setActionCommand(kActionCommandMenuItemColorSetupPostProcessingFilters);
					menuItem.addActionListener(this);
					fMenuItems.put(kActionCommandMenuItemColorSetupPostProcessingFilters,menuItem);
				menu.add(menuItem);

		menus.add(menu);

			// multithreading menu
			menu = new JMenu(I18NL10N.translate("menu.MultiThreading"));
			menu.setMnemonic(I18NL10N.translateMnemonic(I18NL10N.translate("menu.MultiThreading")));
				menuItem = constructMenuItem(kActionCommandMenuItemMultithreadingRecalculate,false);
				menuItem.setActionCommand(kActionCommandMenuItemMultithreadingRecalculate);
				menuItem.addActionListener(this);
			menu.add(menuItem);
				menuItem = constructMenuItem(kActionCommandMenuItemMultithreadingSetNrOfCPUCoresToUse,false);
				menuItem.setActionCommand(kActionCommandMenuItemMultithreadingSetNrOfCPUCoresToUse);
				menuItem.addActionListener(this);
				// disable in case only single threading is possible
				if (MemoryStatistics.getNrOfProcessors() == 1) {
					menuItem.setEnabled(false);
				}
			menu.add(menuItem);
				menuItem = constructMenuItem(kActionCommandMenuItemMultithreadingSetNrOfBlocksToUse,false);
				menuItem.setActionCommand(kActionCommandMenuItemMultithreadingSetNrOfBlocksToUse);
				menuItem.addActionListener(this);
			menu.add(menuItem);

			menu.addSeparator();

				ButtonGroup bgProgressIndicator = new ButtonGroup();
					radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemMultithreadingProgressIndicatorBar,false);
				radioButtonMenuItem.setSelected(false);
				radioButtonMenuItem.setActionCommand(kActionCommandMenuItemMultithreadingProgressIndicatorBar);
				radioButtonMenuItem.addActionListener(this);
				bgProgressIndicator.add(radioButtonMenuItem);
				fMenuItems.put(kActionCommandMenuItemMultithreadingProgressIndicatorBar,radioButtonMenuItem);
			menu.add(radioButtonMenuItem);
				radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemMultithreadingProgressIndicatorCircles,false);
				radioButtonMenuItem.setSelected(false);
				radioButtonMenuItem.setActionCommand(kActionCommandMenuItemMultithreadingProgressIndicatorCircles);
				radioButtonMenuItem.addActionListener(this);
				bgProgressIndicator.add(radioButtonMenuItem);
				fMenuItems.put(kActionCommandMenuItemMultithreadingProgressIndicatorCircles,radioButtonMenuItem);
			menu.add(radioButtonMenuItem);
				radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemMultithreadingProgressIndicatorFixedSector,false);
				radioButtonMenuItem.setSelected(false);
				radioButtonMenuItem.setActionCommand(kActionCommandMenuItemMultithreadingProgressIndicatorFixedSector);
				radioButtonMenuItem.addActionListener(this);
				bgProgressIndicator.add(radioButtonMenuItem);
				fMenuItems.put(kActionCommandMenuItemMultithreadingProgressIndicatorFixedSector,radioButtonMenuItem);
			menu.add(radioButtonMenuItem);
				radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemMultithreadingProgressIndicatorRotatingSector,false);
				radioButtonMenuItem.setSelected(true);
				radioButtonMenuItem.setActionCommand(kActionCommandMenuItemMultithreadingProgressIndicatorRotatingSector);
				radioButtonMenuItem.addActionListener(this);
				bgProgressIndicator.add(radioButtonMenuItem);
				fMenuItems.put(kActionCommandMenuItemMultithreadingProgressIndicatorRotatingSector,radioButtonMenuItem);
			menu.add(radioButtonMenuItem);

		menus.add(menu);

		return menus;
	}

	/**
	 * See {@link JStandardGUIApplication}.
	 */
	@Override
	protected JMenu setupRightHandMenu()
	{
		JMenu rightHandMenu = null;
		JMenuItem menuItem = null;

		rightHandMenu = new JMenu(I18NL10N.translate("menu.Help"));
		rightHandMenu.setMnemonic(I18NL10N.translateMnemonic(I18NL10N.translate("menu.Help.Mnemonic")));

			menuItem = constructMenuItem(kActionCommandMenuItemHelpGeneralInformation,false);
			menuItem.setActionCommand(kActionCommandMenuItemHelpGeneralInformation);
			menuItem.addActionListener(this);
		rightHandMenu.add(menuItem);
			menuItem = constructMenuItem(kActionCommandMenuItemHelpGettingStarted,false);
			menuItem.setActionCommand(kActionCommandMenuItemHelpGettingStarted);
			menuItem.addActionListener(this);
		rightHandMenu.add(menuItem);

		rightHandMenu.addSeparator();

			menuItem = constructMenuItem(kActionCommandMenuItemHelpColoringSchemes,false);
			menuItem.setActionCommand(kActionCommandMenuItemHelpColoringSchemes);
			menuItem.addActionListener(this);
		rightHandMenu.add(menuItem);
			menuItem = constructMenuItem(kActionCommandMenuItemHelpResizingForQualityPrinting,false);
			menuItem.setActionCommand(kActionCommandMenuItemHelpResizingForQualityPrinting);
			menuItem.addActionListener(this);
		rightHandMenu.add(menuItem);

		rightHandMenu.addSeparator();

			menuItem = constructMenuItem(kActionCommandMenuItemHelpKeyboardShortcuts,false);
			menuItem.setActionCommand(kActionCommandMenuItemHelpKeyboardShortcuts);
			menuItem.addActionListener(this);
		rightHandMenu.add(menuItem);

		rightHandMenu.addSeparator();

			menuItem = constructMenuItem(kActionCommandMenuItemHelpFractalTypes,false);
			menuItem.setActionCommand(kActionCommandMenuItemHelpFractalTypes);
			menuItem.addActionListener(this);
		rightHandMenu.add(menuItem);

		return rightHandMenu;
	}

	/**
	 * See {@link JStandardGUIApplication}.
	 */
	protected ArrayList<JLabel> setupStatusBarCustomLabels()
	{
		ArrayList<JLabel> customLabels = new ArrayList<JLabel>();
			customLabels.add(fStatusBarCalculationTimeLabel);
		return customLabels;
	}

	/**
	 * See {@link JStandardGUIApplication}.
	 */
	@Override
	protected JPanel setupGlassPane()
	{
		fProgressUpdateGlassPane.setBlocking(true);
		fProgressUpdateGlassPane.setShowTimeEstimation(true);
		fProgressUpdateGlassPane.setVisualisationType(JProgressUpdateGlassPane.EVisualisationType.kRotatingSector);
		return fProgressUpdateGlassPane;
	}

	/**
	 * See {@link JStandardGUIApplication}.
	 */
	@Override
	protected JAboutBox setupAboutBox()
	{
		return (new AboutBox(this,fResources));
	}

	/**
	 * See {@link JStandardGUIApplication}.
	 */
	@Override
	public void windowActivated(WindowEvent e)
	{
		fFractalPanel.repaint();
	}

	/**
	 * See {@link JStandardGUIApplication}.
	 */
	@Override
	protected void shutdown()
	{
		// obtain a local reference to the system registry
		Registry systemRegistry = Registry.getInstance();

		systemRegistry.addObject("fLastOpenedFolder",fLastOpenedFolder);
	}

	/*******************
	 * PRIVATE METHODS *
	 *******************/

	/**
	 * Loads the registry.
	 */
	private void loadRegistry()
	{
		// obtain a local reference to the system registry
		Registry systemRegistry = Registry.getInstance();

		Object rawObject = systemRegistry.getObject("fLastOpenedFolder");
		if (rawObject != null) {
			fLastOpenedFolder = (String) rawObject;
		}
		else {
			// setup and store default as the current folder
			fLastOpenedFolder = ".";
			systemRegistry.addObject("fLastOpenedFolder",fLastOpenedFolder);
		}
	}

	/**
	 * Provides a default filename that contains the most relevant characteristics of the current fractal iterator.
	 * 
	 * @param extension       the default extension for the filename
	 * @param justParameters  a <CODE>boolean</CODE> indicating whether or not only the parameters are concerned
	 * @return                a default filename that contains the most relevant characteristics of the current fractal iterator
	 */
	private String createDefaultFilename(String extension, boolean justParameters)
	{
		AFractalIterator fractalIterator = fIteratorController.getFractalIterator();
		String defaultFilename = fractalIterator.getFamilyName();

		if (fractalIterator.getFractalType() == AFractalIterator.EFractalType.kDualFractal) {
			defaultFilename += "_dual";
		}

		defaultFilename +=
			"_x1=" + String.valueOf(fractalIterator.getP1().realComponent()) +
			"_y1=" + String.valueOf(fractalIterator.getP1().imaginaryComponent()) +
			"_x2=" + String.valueOf(fractalIterator.getP2().realComponent()) +
			"_y2=" + String.valueOf(fractalIterator.getP2().imaginaryComponent());

		if ((fractalIterator instanceof AConvergentFractalIterator) ||
				(fractalIterator instanceof DucksFractalIterator)) {
			defaultFilename += "_iter=" + String.valueOf(fractalIterator.getFixedNrOfIterations());
		}
		else {
			defaultFilename += "_iter=" + String.valueOf(fractalIterator.getMaxNrOfIterations());
		}

		if (fractalIterator.getFractalType() == AFractalIterator.EFractalType.kDualFractal) {
			defaultFilename +=
				"_dualparamx=" + String.valueOf(fractalIterator.getDualParameter().realComponent()) +
				"_dualparamy=" + String.valueOf(fractalIterator.getDualParameter().imaginaryComponent());
		}

		defaultFilename += fractalIterator.getCustomFilenamePart();

		if (justParameters) {
			defaultFilename += "_parameters";
		}

		defaultFilename += ("." + extension);

		// remove all special characters
		defaultFilename = defaultFilename.replace(" / ","_");
		defaultFilename = defaultFilename.replace("(","");
		defaultFilename = defaultFilename.replace(")","");
		defaultFilename = defaultFilename.replace("/","");
		defaultFilename = defaultFilename.replace("'","");
		defaultFilename = defaultFilename.replace(" ","_");
		defaultFilename = defaultFilename.toLowerCase();

		return defaultFilename;
	}

	/**
	 */
	private void adjustGUIToFractal()
	{
		AFractalIterator fractalIterator = fIteratorController.getFractalIterator();

		// check if the Y-axis should be inverted
		fMenuItems.get(kActionCommandMenuItemNavigationInvertYAxis).setSelected(fIteratorController.getFractalIterator().getInvertYAxis());

		// select the correct fractal
		String familyName = fIteratorController.getFractalIterator().getFamilyName();
		String familyMenuItem = "";

		if (familyName.equalsIgnoreCase((new FastMandelbrotJuliaFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyDefaultMandelbrotJulia;
		}
		else if (familyName.equalsIgnoreCase((new MandelbarFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyMandelbar;
		}
		else if (familyName.equalsIgnoreCase((new RandelbrotFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyRandelbrot;
		}
		else if (familyName.equalsIgnoreCase((new OriginalJuliaFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyOriginalJulia;
		}
		else if (familyName.equalsIgnoreCase((new LambdaFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyLambda;
		}
		else if (familyName.equalsIgnoreCase((new InverseLambdaFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyInverseLambda;
		}
		else if (familyName.equalsIgnoreCase((new BurningShipFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyBurningShip;
		}
		else if (familyName.equalsIgnoreCase((new BirdOfPreyFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyBirdOfPrey;
		}
		else if (familyName.equalsIgnoreCase((new GlynnFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyGlynn;
		}
		else if (familyName.equalsIgnoreCase((new SpiderFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilySpider;
		}
		else if (familyName.equalsIgnoreCase((new MultibrotFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyMultibrot;
		}
		else if (familyName.equalsIgnoreCase((new MultibrotPolynomialFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyMultibrotPolynomial;
		}
		else if (familyName.equalsIgnoreCase((new MultibrotParameterFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyMultibrotParameter;
		}
		else if (familyName.equalsIgnoreCase((new MultibrotInvertedParameterFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyMultibrotInvertedParameter;
		}
		else if (familyName.equalsIgnoreCase((new MultibarFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyMultibar;
		}
		else if (familyName.equalsIgnoreCase((new MultibarPolynomialFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyMultibarPolynomial;
		}
		else if (familyName.equalsIgnoreCase((new MultibarParameterFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyMultibarParameter;
		}
		else if (familyName.equalsIgnoreCase((new MultibarInvertedParameterFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyMultibarInvertedParameter;
		}
		else if (familyName.equalsIgnoreCase((new BurningMultiShipFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyBurningMultiShip;
		}
		else if (familyName.equalsIgnoreCase((new MultiProductExpelbrotFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyMultiProductExpelbrot;
		}
		else if (familyName.equalsIgnoreCase((new MultiSumExpelbrotFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyMultiSumExpelbrot;
		}
		else if (familyName.equalsIgnoreCase((new MultiProductExparbrotFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyMultiProductExparbrot;
		}
		else if (familyName.equalsIgnoreCase((new MultiSumExparbrotFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyMultiSumExparbrot;
		}
		else if (familyName.equalsIgnoreCase((new TrigonometricPowerSineFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyTrigonometricPowerSine;
		}
		else if (familyName.equalsIgnoreCase((new TrigonometricPowerCosineFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyTrigonometricPowerCosine;
		}
		else if (familyName.equalsIgnoreCase((new TrigonometricPowerTangentFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyTrigonometricPowerTangent;
		}
		else if (familyName.equalsIgnoreCase((new TrigonometricPowerCotangentFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyTrigonometricPowerCotangent;
		}
		else if (familyName.equalsIgnoreCase((new TrigonometricPowerMultiSineFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiSine;
		}
		else if (familyName.equalsIgnoreCase((new TrigonometricPowerMultiCosineFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCosine;
		}
		else if (familyName.equalsIgnoreCase((new TrigonometricPowerMultiTangentFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiTangent;
		}
		else if (familyName.equalsIgnoreCase((new TrigonometricPowerMultiCotangentFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCotangent;
		}
		else if (familyName.equalsIgnoreCase((new CactusFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyCactus;
		}
		else if (familyName.equalsIgnoreCase((new Beauty1FractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyBeauty1;
		}
		else if (familyName.equalsIgnoreCase((new Beauty2FractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyBeauty2;
		}
		else if (familyName.equalsIgnoreCase((new DucksFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyDucks;
		}
		else if (familyName.equalsIgnoreCase((new BarnsleyTreeFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyBarnsleyTree;
		}
		else if (familyName.equalsIgnoreCase((new CollatzFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyCollatz;
		}
		else if (familyName.equalsIgnoreCase((new PhoenixFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyPhoenix;
		}
		else if (familyName.equalsIgnoreCase((new ManowarFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyManowar;
		}
		else if (familyName.equalsIgnoreCase((new NewtonRaphsonPowerFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyNewtonRaphsonPower;
		}
		else if (familyName.equalsIgnoreCase((new NewtonRaphsonPowerPolynomialFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyNewtonRaphsonPowerPolynomial;
		}
		else if (familyName.equalsIgnoreCase((new NewtonRaphsonFixedPolynomial1FractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial1;
		}
		else if (familyName.equalsIgnoreCase((new NewtonRaphsonFixedPolynomial2FractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial2;
		}
		else if (familyName.equalsIgnoreCase((new NewtonRaphsonFixedPolynomial3FractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial3;
		}
		else if (familyName.equalsIgnoreCase((new NewtonRaphsonFixedPolynomial4FractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyNewtonRaphsonFixedPolynomial4;
		}
		else if (familyName.equalsIgnoreCase((new NewtonRaphsonTrigonometricPowerSineFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSine;
		}
		else if (familyName.equalsIgnoreCase((new NewtonRaphsonTrigonometricPowerMultiSineFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSine;
		}
		else if (familyName.equalsIgnoreCase((new NewtonRaphsonTrigonometricPowerSineOffsetFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSineOffset;
		}
		else if (familyName.equalsIgnoreCase((new NewtonRaphsonTrigonometricPowerMultiSineOffsetFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSineOffset;
		}
		else if (familyName.equalsIgnoreCase((new NovaFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyNova;
		}
		else if (familyName.equalsIgnoreCase((new MagnetTypeIFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyMagnetTypeI;
		}
		else if (familyName.equalsIgnoreCase((new MagnetTypeIIFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyMagnetTypeII;
		}
		else if (familyName.equalsIgnoreCase((new MarkusLyapunovFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyMarkusLyapunov;
		}
		else if (familyName.equalsIgnoreCase((new MarkusLyapunovLogisticBifurcationFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyMarkusLyapunovLogisticBifurcation;
		}
		else if (familyName.equalsIgnoreCase((new MarkusLyapunovJellyfishFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyMarkusLyapunovJellyfish;
		}
		else if (familyName.equalsIgnoreCase((new MarkusLyapunovZirconZityFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyMarkusLyapunovZirconZity;
		}

		if (familyMenuItem.length() > 0) {
			fMenuItems.get(familyMenuItem).setSelected(true);
		}

		for (String fractalFamily : fFractalFamilyMenuItems) {
			fMenuItems.get(fractalFamily + ".SetPower").setEnabled(fractalFamily.equalsIgnoreCase(familyMenuItem));
		}
		fMenuItems.get(kActionCommandMenuItemFractalFamilyRandelbrotSetNoiseLevel).setEnabled(familyMenuItem.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyRandelbrot));
		fMenuItems.get(kActionCommandMenuItemFractalFamilyDucksSetFixedNrOfIterations).setEnabled(familyMenuItem.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyDucks));
		fMenuItems.get(kActionCommandMenuItemFractalFamilyMarkusLyapunovSetRootSequence).setEnabled(familyMenuItem.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMarkusLyapunov));

		boolean isAConvergentFractalIterator = fractalIterator instanceof AConvergentFractalIterator;
		boolean isNovaFractalIterator = fractalIterator instanceof NovaFractalIterator;
		boolean isMarkusLyapunovFractalIterator = fractalIterator instanceof MarkusLyapunovFractalIterator;
		boolean calculateAdvancedColoring = fIteratorController.getFractalIterator().getCalculateAdvancedColoring();

		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseExponentiallySmoothedLevelSets).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseSectorDecomposition).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorSetDecompositionSectorRange).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseRealComponent).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseImaginaryComponent).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseModulus).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseAngle).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseMaxModulus).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseTotalDistance).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseAverageDistance).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseTotalAngle).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseCurvature).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseStriping).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorSetStripingDensity).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseMinimumGaussianIntegersDistance).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseAverageGaussianIntegersDistance).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorSetGaussianIntegersTrapFactor).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseDiscreteLevelSets).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseNormalisedLevelSets).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseExponentiallySmoothedLevelSets).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseSectorDecomposition).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorSetDecompositionSectorRange).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseRealComponent).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseImaginaryComponent).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseModulus).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseAngle).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseMaxModulus).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseTotalDistance).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseAverageDistance).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseTotalAngle).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapCalculateAdvancedColoring).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapCalculateAdvancedColoring).setSelected(calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseCurvature).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseStriping).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorSetStripingDensity).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseMinimumGaussianIntegersDistance).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseAverageGaussianIntegersDistance).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorSetGaussianIntegersTrapFactor).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseDiscreteRoots).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseSmoothRoots).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorSetBrightnessFactor).setEnabled(!isMarkusLyapunovFractalIterator);

		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseDiscreteRoots).setEnabled(isAConvergentFractalIterator && !isNovaFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseSmoothRoots).setEnabled(isAConvergentFractalIterator && !isNovaFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorSetBrightnessFactor).setEnabled(isAConvergentFractalIterator && !isNovaFractalIterator);
		fMenuItems.get(kActionCommandMenuItemFractalFamilyNewtonRaphsonSetConvergenceParameters).setEnabled(isAConvergentFractalIterator);
		fMenuItems.get(kActionCommandMenuItemFractalFamilyNewtonRaphsonAutomaticRootDetectionEnabled).setEnabled(isAConvergentFractalIterator && !isNovaFractalIterator);
		fMenuItems.get(kActionCommandMenuItemFractalFamilyNewtonRaphsonAutomaticRootDetectionEnabled).setSelected(isAConvergentFractalIterator && !isNovaFractalIterator);

		fMenuItems.get(kActionCommandMenuItemFractalFamilyMagnetSetConvergenceParameters).setEnabled(fractalIterator instanceof AMagnetFractalIterator);

		// check if the inset should be disabled
		if ((fractalIterator instanceof SpiderFractalIterator) ||
				(fractalIterator instanceof CollatzFractalIterator) ||
				((fractalIterator instanceof AConvergentFractalIterator) && !(fractalIterator instanceof NovaFractalIterator)) ||
				(fractalIterator instanceof MarkusLyapunovFractalIterator)) {
			fMenuItems.get(kActionCommandMenuItemFractalShowInset).setSelected(false);
			fFractalPanel.setShowInset(fMenuItems.get(kActionCommandMenuItemFractalShowInset).isSelected());
		}

		// setup the remaining menu items related to the colouring parameters
		ColoringParameters coloringParameters = fFractalPanel.getColoringParameters();

		JGradientColorMap.EColorMap interiorColorMap = coloringParameters.fInteriorGradientColorMap.getColorMap();
		switch (interiorColorMap) {
			case kBone: fMenuItems.get(kActionCommandMenuItemColorMapInteriorBone).setSelected(true); break;
			case kCopper: fMenuItems.get(kActionCommandMenuItemColorMapInteriorCopper).setSelected(true); break;
			case kDiscontinuousBlueWhiteGreen: fMenuItems.get(kActionCommandMenuItemColorMapInteriorDiscontinuousBlueWhiteGreen).setSelected(true); break;
			case kDiscontinuousDarkRedYellow: fMenuItems.get(kActionCommandMenuItemColorMapInteriorDiscontinuousDarkRedYellow).setSelected(true); break;
			case kBlackAndWhite: fMenuItems.get(kActionCommandMenuItemColorMapInteriorBlackAndWhite).setSelected(true); break;
			case kGrayScale: fMenuItems.get(kActionCommandMenuItemColorMapInteriorGrayScale).setSelected(true); break;
			case kGreenRedDiverging: fMenuItems.get(kActionCommandMenuItemColorMapInteriorGreenRedDiverging).setSelected(true); break;
			case kHot: fMenuItems.get(kActionCommandMenuItemColorMapInteriorHot).setSelected(true); break;
			case kJet: fMenuItems.get(kActionCommandMenuItemColorMapInteriorJet).setSelected(true); break;
			case kHueSaturationBrightness: fMenuItems.get(kActionCommandMenuItemColorMapInteriorHueSaturationBrightness).setSelected(true); break;
			case kSeparatedRGB: fMenuItems.get(kActionCommandMenuItemColorMapInteriorSeparatedRGB).setSelected(true); break;
			case kRed: fMenuItems.get(kActionCommandMenuItemColorMapInteriorRed).setSelected(true); break;
			case kGreen: fMenuItems.get(kActionCommandMenuItemColorMapInteriorGreen).setSelected(true); break;
			case kBlue: fMenuItems.get(kActionCommandMenuItemColorMapInteriorBlue).setSelected(true); break;
			case kYellow: fMenuItems.get(kActionCommandMenuItemColorMapInteriorYellow).setSelected(true); break;
			case kCyan: fMenuItems.get(kActionCommandMenuItemColorMapInteriorCyan).setSelected(true); break;
			case kMagenta: fMenuItems.get(kActionCommandMenuItemColorMapInteriorMagenta).setSelected(true); break;
			case kUltraLightPastel: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUltraLightPastel).setSelected(true); break;
			case kLightPastel: fMenuItems.get(kActionCommandMenuItemColorMapInteriorLightPastel).setSelected(true); break;
			case kDarkPastel: fMenuItems.get(kActionCommandMenuItemColorMapInteriorDarkPastel).setSelected(true); break;
			case kGreens: fMenuItems.get(kActionCommandMenuItemColorMapInteriorGreens).setSelected(true); break;
			case kBlues: fMenuItems.get(kActionCommandMenuItemColorMapInteriorBlues).setSelected(true); break;
			case kYellowBrowns: fMenuItems.get(kActionCommandMenuItemColorMapInteriorYellowBrowns).setSelected(true); break;
			case kVioletPurples: fMenuItems.get(kActionCommandMenuItemColorMapInteriorVioletPurples).setSelected(true); break;
			case kDeepSpace: fMenuItems.get(kActionCommandMenuItemColorMapInteriorDeepSpace).setSelected(true); break;
			case kCustom: fMenuItems.get(kActionCommandMenuItemColorMapInteriorCustom).setSelected(true); break;
		}

		JGradientColorMap.EColorMap exteriorColorMap = coloringParameters.fExteriorGradientColorMap.getColorMap();
		switch (exteriorColorMap) {
			case kBone: fMenuItems.get(kActionCommandMenuItemColorMapExteriorBone).setSelected(true); break;
			case kCopper: fMenuItems.get(kActionCommandMenuItemColorMapExteriorCopper).setSelected(true); break;
			case kDiscontinuousBlueWhiteGreen: fMenuItems.get(kActionCommandMenuItemColorMapExteriorDiscontinuousBlueWhiteGreen).setSelected(true); break;
			case kDiscontinuousDarkRedYellow: fMenuItems.get(kActionCommandMenuItemColorMapExteriorDiscontinuousDarkRedYellow).setSelected(true); break;
			case kBlackAndWhite: fMenuItems.get(kActionCommandMenuItemColorMapExteriorBlackAndWhite).setSelected(true); break;
			case kGrayScale: fMenuItems.get(kActionCommandMenuItemColorMapExteriorGrayScale).setSelected(true); break;
			case kGreenRedDiverging: fMenuItems.get(kActionCommandMenuItemColorMapExteriorGreenRedDiverging).setSelected(true); break;
			case kHot: fMenuItems.get(kActionCommandMenuItemColorMapExteriorHot).setSelected(true); break;
			case kJet: fMenuItems.get(kActionCommandMenuItemColorMapExteriorJet).setSelected(true); break;
			case kHueSaturationBrightness: fMenuItems.get(kActionCommandMenuItemColorMapExteriorHueSaturationBrightness).setSelected(true); break;
			case kSeparatedRGB: fMenuItems.get(kActionCommandMenuItemColorMapExteriorSeparatedRGB).setSelected(true); break;
			case kRed: fMenuItems.get(kActionCommandMenuItemColorMapExteriorRed).setSelected(true); break;
			case kGreen: fMenuItems.get(kActionCommandMenuItemColorMapExteriorGreen).setSelected(true); break;
			case kBlue: fMenuItems.get(kActionCommandMenuItemColorMapExteriorBlue).setSelected(true); break;
			case kYellow: fMenuItems.get(kActionCommandMenuItemColorMapExteriorYellow).setSelected(true); break;
			case kCyan: fMenuItems.get(kActionCommandMenuItemColorMapExteriorCyan).setSelected(true); break;
			case kMagenta: fMenuItems.get(kActionCommandMenuItemColorMapExteriorMagenta).setSelected(true); break;
			case kUltraLightPastel: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUltraLightPastel).setSelected(true); break;
			case kLightPastel: fMenuItems.get(kActionCommandMenuItemColorMapExteriorLightPastel).setSelected(true); break;
			case kDarkPastel: fMenuItems.get(kActionCommandMenuItemColorMapExteriorDarkPastel).setSelected(true); break;
			case kGreens: fMenuItems.get(kActionCommandMenuItemColorMapExteriorGreens).setSelected(true); break;
			case kBlues: fMenuItems.get(kActionCommandMenuItemColorMapExteriorBlues).setSelected(true); break;
			case kYellowBrowns: fMenuItems.get(kActionCommandMenuItemColorMapExteriorYellowBrowns).setSelected(true); break;
			case kVioletPurples: fMenuItems.get(kActionCommandMenuItemColorMapExteriorVioletPurples).setSelected(true); break;
			case kDeepSpace: fMenuItems.get(kActionCommandMenuItemColorMapExteriorDeepSpace).setSelected(true); break;
			case kCustom: fMenuItems.get(kActionCommandMenuItemColorMapExteriorCustom).setSelected(true); break;
		}

		fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).setSelected(coloringParameters.fInteriorColorMapInverted);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).setSelected(coloringParameters.fExteriorColorMapInverted);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).setSelected(coloringParameters.fInteriorColorMapWrappedAround);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).setSelected(coloringParameters.fExteriorColorMapWrappedAround);
		fMenuItems.get(kActionCommandMenuItemColorMapUseTigerStripes).setSelected(coloringParameters.fUseTigerStripes);

		JGradientColorMap.EColorMap tigerColorMap = coloringParameters.fTigerGradientColorMap.getColorMap();
		switch (tigerColorMap) {
			case kBone: fMenuItems.get(kActionCommandMenuItemColorMapTigerBone).setSelected(true); break;
			case kCopper: fMenuItems.get(kActionCommandMenuItemColorMapTigerCopper).setSelected(true); break;
			case kDiscontinuousBlueWhiteGreen: fMenuItems.get(kActionCommandMenuItemColorMapTigerDiscontinuousBlueWhiteGreen).setSelected(true); break;
			case kDiscontinuousDarkRedYellow: fMenuItems.get(kActionCommandMenuItemColorMapTigerDiscontinuousDarkRedYellow).setSelected(true); break;
			case kBlackAndWhite: fMenuItems.get(kActionCommandMenuItemColorMapTigerBlackAndWhite).setSelected(true); break;
			case kGrayScale: fMenuItems.get(kActionCommandMenuItemColorMapTigerGrayScale).setSelected(true); break;
			case kGreenRedDiverging: fMenuItems.get(kActionCommandMenuItemColorMapTigerGreenRedDiverging).setSelected(true); break;
			case kHot: fMenuItems.get(kActionCommandMenuItemColorMapTigerHot).setSelected(true); break;
			case kJet: fMenuItems.get(kActionCommandMenuItemColorMapTigerJet).setSelected(true); break;
			case kHueSaturationBrightness: fMenuItems.get(kActionCommandMenuItemColorMapTigerHueSaturationBrightness).setSelected(true); break;
			case kSeparatedRGB: fMenuItems.get(kActionCommandMenuItemColorMapTigerSeparatedRGB).setSelected(true); break;
			case kRed: fMenuItems.get(kActionCommandMenuItemColorMapTigerRed).setSelected(true); break;
			case kGreen: fMenuItems.get(kActionCommandMenuItemColorMapTigerGreen).setSelected(true); break;
			case kBlue: fMenuItems.get(kActionCommandMenuItemColorMapTigerBlue).setSelected(true); break;
			case kYellow: fMenuItems.get(kActionCommandMenuItemColorMapTigerYellow).setSelected(true); break;
			case kCyan: fMenuItems.get(kActionCommandMenuItemColorMapTigerCyan).setSelected(true); break;
			case kMagenta: fMenuItems.get(kActionCommandMenuItemColorMapTigerMagenta).setSelected(true); break;
			case kUltraLightPastel: fMenuItems.get(kActionCommandMenuItemColorMapTigerUltraLightPastel).setSelected(true); break;
			case kLightPastel: fMenuItems.get(kActionCommandMenuItemColorMapTigerLightPastel).setSelected(true); break;
			case kDarkPastel: fMenuItems.get(kActionCommandMenuItemColorMapTigerDarkPastel).setSelected(true); break;
			case kGreens: fMenuItems.get(kActionCommandMenuItemColorMapTigerGreens).setSelected(true); break;
			case kBlues: fMenuItems.get(kActionCommandMenuItemColorMapTigerBlues).setSelected(true); break;
			case kYellowBrowns: fMenuItems.get(kActionCommandMenuItemColorMapTigerYellowBrowns).setSelected(true); break;
			case kVioletPurples: fMenuItems.get(kActionCommandMenuItemColorMapTigerVioletPurples).setSelected(true); break;
			case kDeepSpace: fMenuItems.get(kActionCommandMenuItemColorMapTigerDeepSpace).setSelected(true); break;
			case kCustom: fMenuItems.get(kActionCommandMenuItemColorMapTigerCustom).setSelected(true); break;
		}

		fMenuItems.get(kActionCommandMenuItemColorMapTigerUseFixedColor).setSelected(coloringParameters.fTigerUseFixedColor);

		fInteriorColorLabelDecorator.setColor(coloringParameters.fInteriorColor);
		fExteriorColorLabelDecorator.setColor(coloringParameters.fExteriorColor);
		fTigerStripeColorLabelDecorator.setColor(coloringParameters.fTigerStripeFixedColor);

		ColoringParameters.EColoringMethod interiorColoringMethod = coloringParameters.fInteriorColoringMethod;
		switch (interiorColoringMethod) {
			case kFixedColor: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseFixedColor).setSelected(true); break;
			case kDiscreteLevelSets: break; // not applicable
			case kSmoothNICLevelSets: break; // not applicable
			case kSmoothEICLevelSets: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseExponentiallySmoothedLevelSets).setSelected(true); break;
			case kSectorDecomposition: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseSectorDecomposition).setSelected(true); break;
			case kRealComponent: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseRealComponent).setSelected(true); break;
			case kImaginaryComponent: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseImaginaryComponent).setSelected(true); break;
			case kModulus: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseModulus).setSelected(true); break;
			case kAngle: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseAngle).setSelected(true); break;
			case kMaxModulus: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseMaxModulus).setSelected(true); break;
			case kTotalDistance: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseTotalDistance).setSelected(true); break;
			case kAverageDistance: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseAverageDistance).setSelected(true); break;
			case kTotalAngle: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseTotalAngle).setSelected(true); break;
			case kLyapunovExponent: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseLyapunovExponent).setSelected(true); break;
			case kCurvature: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseCurvature).setSelected(true); break;
			case kStriping: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseStriping).setSelected(true); break;
			case kMinimumGaussianIntegersDistance: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseMinimumGaussianIntegersDistance).setSelected(true); break;
			case kAverageGaussianIntegersDistance: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseAverageGaussianIntegersDistance).setSelected(true); break;
			case kDiscreteRoots: break; // not applicable
			case kSmoothRoots: break; // not applicable
		}

		ColoringParameters.EColoringMethod exteriorColoringMethod = coloringParameters.fExteriorColoringMethod;
		switch (exteriorColoringMethod) {
			case kFixedColor: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseFixedColor).setSelected(true); break;
			case kDiscreteLevelSets: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseDiscreteLevelSets).setSelected(true); break;
			case kSmoothNICLevelSets: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseNormalisedLevelSets).setSelected(true); break;
			case kSmoothEICLevelSets: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseExponentiallySmoothedLevelSets).setSelected(true); break;
			case kSectorDecomposition: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseSectorDecomposition).setSelected(true); break;
			case kRealComponent: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseRealComponent).setSelected(true); break;
			case kImaginaryComponent: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseImaginaryComponent).setSelected(true); break;
			case kModulus: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseModulus).setSelected(true); break;
			case kAngle: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseAngle).setSelected(true); break;
			case kMaxModulus: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseMaxModulus).setSelected(true); break;
			case kTotalDistance: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseTotalDistance).setSelected(true); break;
			case kAverageDistance: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseAverageDistance).setSelected(true); break;
			case kTotalAngle: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseTotalAngle).setSelected(true); break;
			case kLyapunovExponent: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseLyapunovExponent).setSelected(true); break;
			case kCurvature: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseCurvature).setSelected(true); break;
			case kStriping: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseStriping).setSelected(true); break;
			case kMinimumGaussianIntegersDistance: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseMinimumGaussianIntegersDistance).setSelected(true); break;
			case kAverageGaussianIntegersDistance: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseAverageGaussianIntegersDistance).setSelected(true); break;
			case kDiscreteRoots: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseDiscreteRoots).setSelected(true); break;
			case kSmoothRoots: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseSmoothRoots).setSelected(true); break;
		}
		
		ColoringParameters.EColorMapScaling colorMapScaling = coloringParameters.fColorMapScaling;
		switch (colorMapScaling) {
			case kLinear: fMenuItems.get(kActionCommandMenuItemColorMapUseLinearScaling).setSelected(true); break;
			case kLogarithmic: fMenuItems.get(kActionCommandMenuItemColorMapUseLogarithmicScaling).setSelected(true); break;
			case kExponential: fMenuItems.get(kActionCommandMenuItemColorMapUseExponentialScaling).setSelected(true); break;
			case kSqrt: fMenuItems.get(kActionCommandMenuItemColorMapUseSqrtScaling).setSelected(true); break;
			case kRankOrder: fMenuItems.get(kActionCommandMenuItemColorMapUseRankOrderScaling).setSelected(true); break;
		}

		fMenuItems.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColors).setSelected(coloringParameters.fRankOrderRestrictHighIterationCountColors);
		fMenuItems.get(kActionCommandMenuItemColorMapRepeatColors).setSelected(coloringParameters.fColorMapRepeatMode);

		ColoringParameters.EColorMapUsage colorMapUsage = coloringParameters.fColorMapUsage;
		switch (colorMapUsage) {
			case kFull: fMenuItems.get(kActionCommandMenuItemColorMapFullColorRange).setSelected(true); break;
			case kLimitedContinuous: fMenuItems.get(kActionCommandMenuItemColorMapUseLimitedContinuousColorRange).setSelected(true); break;
			case kLimitedDiscrete: fMenuItems.get(kActionCommandMenuItemColorMapUseLimitedDiscreteColorRange).setSelected(true); break;
		}

		fMenuItems.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColors).setEnabled(fFractalPanel.getColoringParameters().fColorMapScaling == ColoringParameters.EColorMapScaling.kRankOrder);

		fMenuItems.get(kActionCommandMenuItemColorMapUsePostProcessingFilters).setSelected(coloringParameters.fUsePostProcessingFilters);

		fMenuItems.get(kActionCommandMenuItemNavigationLockAspectRatio).setSelected(coloringParameters.fLockAspectRatio);
	}

	/**
	 */
	private void setupMarkusLyapunovFractal()
	{
		AFractalIterator fractalIterator = fIteratorController.getFractalIterator();

		if (fractalIterator instanceof MarkusLyapunovFractalIterator) {
			fFractalPanel.setInteriorColoringMethod(ColoringParameters.EColoringMethod.kLyapunovExponent);
			fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseLyapunovExponent).setSelected(true);
			fFractalPanel.setExteriorColoringMethod(ColoringParameters.EColoringMethod.kLyapunovExponent);
			fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseLyapunovExponent).setSelected(true);

			fFractalPanel.setExteriorColorMap(JGradientColorMap.EColorMap.kCopper,false,false);
			fMenuItems.get(kActionCommandMenuItemColorMapExteriorCopper).setSelected(true);
			fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).setSelected(false);
			fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).setSelected(false);

			fFractalPanel.setInteriorColorMap(JGradientColorMap.EColorMap.kBlue,false,false);
			fMenuItems.get(kActionCommandMenuItemColorMapInteriorBlue).setSelected(true);
			fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).setSelected(false);
			fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).setSelected(false);

			fFractalPanel.setColorMapScaling(ColoringParameters.EColorMapScaling.kRankOrder);
			fMenuItems.get(kActionCommandMenuItemColorMapUseRankOrderScaling).setSelected(true);
		}
	}

	/**
	 */
	private void changeLocationMouseCursor()
	{
		if (fMenuItems.get(kActionCommandMenuItemNavigationShowCurrentLocation).isSelected() && fFractalPanel.isMouseInsideComplexPlane()) {
			hideMouseCursor();
		}
		else {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	/**
	 */
	private void updateStatusBar()
	{
		Point p = fFractalPanel.getMousePosition();
		if (p != null) {
			String statusText = "";
			if (fIteratorController.getFractalIterator().getFractalType() == AFractalIterator.EFractalType.kMainFractal) {
				if (fFractalPanel.getShowInset()) {
					if (!fFractalPanel.isDualFractalSuppressed()) {
						ComplexNumber c = fIteratorController.getFractalIterator().convertScreenLocationToComplexNumber(new ScreenLocation(p.x,p.y));
						statusText = I18NL10N.translate("text.StatusBar.DualFractalRendered",StringTools.convertComplexNumberToString(c,MathTools.kNrOfDoubleDecimals)) + "  |  ";
					}
				}
			}
			else {
				ComplexNumber c = fIteratorController.getFractalIterator().convertScreenLocationToComplexNumber(new ScreenLocation(p.x,p.y));
				statusText =
					I18NL10N.translate("text.StatusBar.DualFractalRendered",fIteratorController.getFractalIterator().getDualParameter().toString()) +
					"   |   " +
					I18NL10N.translate("text.StatusBar.CurrentLocation",StringTools.convertComplexNumberToString(c,MathTools.kNrOfDoubleDecimals)) + "  |  ";
			}

			statusText += (I18NL10N.translate("text.StatusBar.NrOfIterations") + " = " + fIteratorController.getFractalIterator().getMaxNrOfIterations());

			getStatusBar().setStatusText(statusText);
		}
		else {
			getStatusBar().clearStatusText();
		}
	}

	/**
	 * @param helpTopic  -
	 */
	private void showHelpTopic(EHelpTopic helpTopic)
	{
		try {
			fHelpBroker.setCurrentID(fHelpMapIDs.get(helpTopic));
			fHelpBroker.setDisplayed(true);
		}
		catch (Exception exc) {
			kLogger.error(I18NL10N.translate("error.HelpInformationNotFound"));
		}
	}

	/*****************
	 * INNER CLASSES *
	 *****************/

	/**
	 * @author  Sven Maerivoet
	 * @version 14/01/2015
	 */
	private final class FractalLoaderTask extends SwingWorker<Void,Integer>
	{
		// internal datastructures
		private String fFilename;
		private JFrame fOwner;
		private JProgressUpdateGlassPane.EVisualisationType fPreviousVisualisationType;
		private boolean fPreviousShowFractions;
		private Exception fException;

		/****************
		 * CONSTRUCTORS *
		 ****************/

		/**
		 * Constructs a <CODE>FractalLoaderTask</CODE> object.
		 *
		 * @param filename  the name of the file to load the fractal from
		 * @param owner     a reference to the owning frame
		 */
		public FractalLoaderTask(String filename, JFrame owner)
		{
			fFilename = filename;
			fOwner = owner;
			fPreviousVisualisationType = fProgressUpdateGlassPane.getVisualisationType();
			fPreviousShowFractions = fProgressUpdateGlassPane.getShowFractions();
			fProgressUpdateGlassPane.setShowFractions(false);
		}

		/******************
		 * PUBLIC METHODS *
		 ******************/

		/**
		 */
		@Override
		public Void doInBackground()
		{
			try {
				TextFileParser tfp = new TextFileParser(fFilename);

				// load fractal family name
				fIteratorController.setBusy(true);
				String familyName = tfp.getNextString();

				// create fractal
				if (familyName.equalsIgnoreCase((new FastMandelbrotJuliaFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new FastMandelbrotJuliaFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new MandelbarFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new MandelbarFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new RandelbrotFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new RandelbrotFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new OriginalJuliaFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new OriginalJuliaFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new LambdaFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new LambdaFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new InverseLambdaFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new InverseLambdaFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new BurningShipFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new BurningShipFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new BirdOfPreyFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new BirdOfPreyFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new GlynnFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new GlynnFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new SpiderFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new SpiderFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new MultibrotFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new MultibrotFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new MultibrotPolynomialFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new MultibrotPolynomialFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new MultibrotParameterFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new MultibrotParameterFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new MultibrotInvertedParameterFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new MultibrotInvertedParameterFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new MultibarFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new MultibarFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new MultibarPolynomialFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new MultibarPolynomialFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new MultibarParameterFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new MultibarParameterFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new MultibarInvertedParameterFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new MultibarInvertedParameterFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new BurningMultiShipFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new BurningMultiShipFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new MultiProductExpelbrotFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new MultiProductExpelbrotFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new MultiSumExpelbrotFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new MultiSumExpelbrotFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new MultiProductExparbrotFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new MultiProductExparbrotFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new MultiSumExparbrotFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new MultiSumExparbrotFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new TrigonometricPowerSineFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new TrigonometricPowerSineFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new TrigonometricPowerCosineFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new TrigonometricPowerCosineFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new TrigonometricPowerTangentFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new TrigonometricPowerTangentFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new TrigonometricPowerCotangentFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new TrigonometricPowerCotangentFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new TrigonometricPowerMultiSineFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new TrigonometricPowerMultiSineFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new TrigonometricPowerMultiCosineFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new TrigonometricPowerMultiCosineFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new TrigonometricPowerMultiTangentFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new TrigonometricPowerMultiTangentFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new TrigonometricPowerMultiCotangentFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new TrigonometricPowerMultiCotangentFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new CactusFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new CactusFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new Beauty1FractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new Beauty1FractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new Beauty2FractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new Beauty2FractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new DucksFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new DucksFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new BarnsleyTreeFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new BarnsleyTreeFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new CollatzFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new CollatzFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new PhoenixFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new PhoenixFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new ManowarFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new ManowarFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new NewtonRaphsonPowerFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new NewtonRaphsonPowerFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new NewtonRaphsonPowerPolynomialFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new NewtonRaphsonPowerPolynomialFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new NewtonRaphsonFixedPolynomial1FractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new NewtonRaphsonFixedPolynomial1FractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new NewtonRaphsonFixedPolynomial2FractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new NewtonRaphsonFixedPolynomial2FractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new NewtonRaphsonFixedPolynomial3FractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new NewtonRaphsonFixedPolynomial3FractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new NewtonRaphsonFixedPolynomial4FractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new NewtonRaphsonFixedPolynomial4FractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new NewtonRaphsonTrigonometricPowerSineFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new NewtonRaphsonTrigonometricPowerSineFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new NewtonRaphsonTrigonometricPowerMultiSineFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new NewtonRaphsonTrigonometricPowerMultiSineFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new NewtonRaphsonTrigonometricPowerSineOffsetFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new NewtonRaphsonTrigonometricPowerSineOffsetFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new NewtonRaphsonTrigonometricPowerMultiSineOffsetFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new NewtonRaphsonTrigonometricPowerMultiSineOffsetFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new NovaFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new NovaFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new MagnetTypeIFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new MagnetTypeIFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new MagnetTypeIIFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new MagnetTypeIIFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new MarkusLyapunovFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new MarkusLyapunovFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new MarkusLyapunovLogisticBifurcationFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new MarkusLyapunovLogisticBifurcationFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new MarkusLyapunovJellyfishFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new MarkusLyapunovJellyfishFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new MarkusLyapunovZirconZityFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new MarkusLyapunovZirconZityFractalIterator());
				}
				else {
					throw (new UnsupportedFractalException(fFilename,familyName));
				}

				// if necessary switch to the dual fractal
				AFractalIterator fractalIterator = fIteratorController.getFractalIterator();
				if ((fractalIterator instanceof GlynnFractalIterator) ||
						(fractalIterator instanceof BarnsleyTreeFractalIterator) ||
						(fractalIterator instanceof PhoenixFractalIterator)) {
					fIteratorController.getFractalIterator().setFractalType(AFractalIterator.EFractalType.kDualFractal);
				}

				// load fractal parameters
				fIteratorController.getFractalIterator().loadParameters(tfp);

				// load fractal colouring parameters
				fFractalPanel.getColoringParameters().load(tfp);

				// load iteration buffer
				fProgressUpdateGlassPane.setVisualisationType(JProgressUpdateGlassPane.EVisualisationType.kBar);
				fProgressUpdateGlassPane.reset();

				int width = fIteratorController.getFractalIterator().getScreenWidth();
				int height = fIteratorController.getFractalIterator().getScreenHeight();
				IterationBuffer fractalResultBuffer = new IterationBuffer(width,height);
				fProgressUpdateGlassPane.setTotalNrOfProgressUpdates(width * height);

				for (int index = 0; index < fractalResultBuffer.fBuffer.length; ++index) {
					fractalResultBuffer.fBuffer[index] = new IterationResult();
					boolean resultAvailable = fractalResultBuffer.fBuffer[index].load(tfp);
					if (!resultAvailable) {
						fractalResultBuffer.fBuffer[index] = null;
					}
					publish(1);
				} // for index

				// install loaded fractal
				fIteratorController.setFractalResultBuffer(fractalResultBuffer);

				// adjust the zoom stack
				fFractalPanel.getZoomStack().clear();
				fFractalPanel.getZoomStack().push(fIteratorController.getFractalIterator().getDefaultP1(),fIteratorController.getFractalIterator().getDefaultP2());
				fFractalPanel.getZoomStack().push(fIteratorController.getFractalIterator().getP1(),fIteratorController.getFractalIterator().getP2());

				// adjust canvas dimensions
				fFractalPanel.revalidate();
				fFractalPanel.recolor();

				adjustGUIToFractal();
			}
			catch (FileDoesNotExistException | FileParseException | UnsupportedFractalException exc) {
				fException = exc;
			}

			return null;
		}

		/**
		 */
		@Override
		public void done()
		{
			if (fProgressUpdateGlassPane != null) {
				fProgressUpdateGlassPane.done();
				fProgressUpdateGlassPane.setVisualisationType(fPreviousVisualisationType);
				fProgressUpdateGlassPane.setShowFractions(fPreviousShowFractions);
				if (fException == null) {
					JMessageDialog.show(fOwner,I18NL10N.translate("text.File.Fractal.Loaded"));
				}
				else if (fException instanceof FileDoesNotExistException) {
					JWarningDialog.warn(fOwner,I18NL10N.translate("error.File.Fractal.ErrorLoadingFractal"));
				}
				else if (fException instanceof FileParseException) {
					JWarningDialog.warn(fOwner,I18NL10N.translate("error.File.Fractal.ErrorParsingFractal",String.valueOf(((FileParseException) fException).getLineNr()),((FileParseException) fException).getValue()));
				}
				else if (fException instanceof UnsupportedFractalException) {
					JWarningDialog.warn(fOwner,I18NL10N.translate("error.File.Fractal.UnsupportedFractal",((UnsupportedFractalException) fException).getFamilyName()));
				}
				fIteratorController.setBusy(false);
			}
		}

		/*********************
		 * PROTECTED METHODS *
		 *********************/

		/**
		 * @param chunks  -
		 */
		@Override
		@SuppressWarnings("unused")
		protected final void process(java.util.List<Integer> chunks)
		{
			if (fProgressUpdateGlassPane != null) {
				for (int dummy : chunks) {
					fProgressUpdateGlassPane.signalProgressUpdate();
				}
			}
		}
	}

	/**
	 * @author  Sven Maerivoet
	 * @version 06/12/2014
	 */
	private final class FractalSaverTask extends SwingWorker<Void,Integer>
	{
		// internal datastructures
		private String fFilename;
		private JFrame fOwner;
		private JProgressUpdateGlassPane.EVisualisationType fPreviousVisualisationType;
		private boolean fPreviousShowFractions;
		private Exception fException;

		/****************
		 * CONSTRUCTORS *
		 ****************/

		/**
		 * Constructs a <CODE>FractalSaverTask</CODE> object.
		 *
		 * @param filename  the name of the file to save the fractal to
		 * @param owner     a reference to the owning frame
		 */
		public FractalSaverTask(String filename, JFrame owner)
		{
			fFilename = filename;
			fOwner = owner;
			fPreviousVisualisationType = fProgressUpdateGlassPane.getVisualisationType();
			fPreviousShowFractions = fProgressUpdateGlassPane.getShowFractions();
			fProgressUpdateGlassPane.setShowFractions(false);
		}

		/******************
		 * PUBLIC METHODS *
		 ******************/

		/**
		 */
		@Override
		public Void doInBackground()
		{
			try {
				TextFileWriter tfw = new TextFileWriter(fFilename);

				// save fractal parameters
				fIteratorController.getFractalIterator().saveParameters(tfw);

				// save fractal colouring parameters
				fFractalPanel.getColoringParameters().save(tfw);

				// save iteration buffer
				fProgressUpdateGlassPane.setVisualisationType(JProgressUpdateGlassPane.EVisualisationType.kBar);
				fProgressUpdateGlassPane.reset();
				int width = fIteratorController.getFractalIterator().getScreenWidth();
				int height = fIteratorController.getFractalIterator().getScreenHeight();
				IterationBuffer fractalResultBuffer = fIteratorController.getFractalResultBuffer();
				fProgressUpdateGlassPane.setTotalNrOfProgressUpdates(width * height);
				for (int index = 0; index < fractalResultBuffer.fBuffer.length; ++index) {
					if (fractalResultBuffer.fBuffer[index] == null) {
						tfw.writeString("null");
						tfw.writeLn();
					}
					else{
						fractalResultBuffer.fBuffer[index].save(tfw);
					}
					publish(1);
				} // for index
			}
			catch (FileCantBeCreatedException | FileWriteException exc) {
				fException = exc;
			}

			return null;
		}

		/**
		 */
		@Override
		public void done()
		{
			if (fProgressUpdateGlassPane != null) {
				fProgressUpdateGlassPane.done();
				fProgressUpdateGlassPane.setVisualisationType(fPreviousVisualisationType);
				fProgressUpdateGlassPane.setShowFractions(fPreviousShowFractions);
				if (fException == null) {
					JMessageDialog.show(fOwner,I18NL10N.translate("text.File.Fractal.Saved"));
				}
				else if ((fException instanceof FileCantBeCreatedException) || (fException instanceof FileWriteException)) {
					JWarningDialog.warn(fOwner,I18NL10N.translate("error.File.Fractal.ErrorSavingFractal"));
				}
			}
		}

		/*********************
		 * PROTECTED METHODS *
		 *********************/

		/**
		 * @param chunks  -
		 */
		@Override
		@SuppressWarnings("unused")
		protected final void process(java.util.List<Integer> chunks)
		{
			if (fProgressUpdateGlassPane != null) {
				for (int dummy : chunks) {
					fProgressUpdateGlassPane.signalProgressUpdate();
				}
			}
		}
	}
}
