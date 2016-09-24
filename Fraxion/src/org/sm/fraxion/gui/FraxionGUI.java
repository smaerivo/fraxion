// -------------------------------
// Filename      : FraxionGUI.java
// Author        : Sven Maerivoet
// Last modified : 25/09/2016
// Target        : Java VM (1.8)
// -------------------------------

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

package org.sm.fraxion.gui;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;
import javax.help.*;
import javax.swing.*;
import org.apache.log4j.*;
import org.sm.fraxion.concurrent.*;
import org.sm.fraxion.fractals.*;
import org.sm.fraxion.fractals.convergent.*;
import org.sm.fraxion.fractals.divergent.*;
import org.sm.fraxion.fractals.divergent.multi.*;
import org.sm.fraxion.fractals.divergent.trigonometric.*;
import org.sm.fraxion.fractals.magnet.*;
import org.sm.fraxion.fractals.markuslyapunov.*;
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
 * @version 25/09/2016
 */
public final class FraxionGUI extends JStandardGUIApplication implements ActionListener, MouseListener, MouseMotionListener
{
	// the application's icon filename
	private static final String kApplicationIconFilename = "application-resources/images/icon.jpg";

	// the amount of time to explicitly wait during the splash screen show
	private static final int kSplashScreenStatusMessageWaitTime = 250;

	// the extension of the ZIP file to save and load fractals
	private static final String kZIPFileExtension = "zip";

	// the action commands for the menus
	private static final String kActionCommandMenuItemFileExportToPNG = "menuItem.File.ExportToPNG";
	private static final String kActionCommandMenuItemFileLoadFractal = "menuItem.File.LoadFractal";
	private static final String kActionCommandMenuItemFileSaveFractal = "menuItem.File.SaveFractal";
	private static final String kActionCommandMenuItemFileLoadFractalParameters = "menuItem.File.LoadFractalParameters";
	private static final String kActionCommandMenuItemFileSaveFractalParameters = "menuItem.File.SaveFractalParameters";
	private static final String kActionCommandMenuItemFileLoadZoomStack = "menuItem.File.LoadZoomStack";
	private static final String kActionCommandMenuItemFileSaveZoomStack = "menuItem.File.SaveZoomStack";
	private static final String kActionCommandMenuItemFileSaveZoomAnimationSequence = "menuItem.File.SaveZoomAnimationSequence";
	private static final String kActionCommandMenuItemFilePrintFractal = "menuItem.File.PrintFractal";

	private static final String kActionCommandMenuItemNavigationPanLeft = "menuItem.Navigation.PanLeft";
	private static final String kActionCommandMenuItemNavigationPanRight = "menuItem.Navigation.PanRight";
	private static final String kActionCommandMenuItemNavigationPanUp = "menuItem.Navigation.PanUp";
	private static final String kActionCommandMenuItemNavigationMenuPanDown = "menuItem.Navigation.PanDown";
	private static final String kActionCommandMenuItemNavigationKeyPanLeft = "menuItem.Navigation.KeyPanLeft";
	private static final String kActionCommandMenuItemNavigationKeyPanRight = "menuItem.Navigation.KeyPanRight";
	private static final String kActionCommandMenuItemNavigationKeyPanUp = "menuItem.Navigation.KeyPanUp";
	private static final String kActionCommandMenuItemNavigationKeyPanDown = "menuItem.Navigation.KeyPanDown";
	private static final String kActionCommandMenuItemNavigationSetPanningSize = "menuItem.Navigation.SetPanningSize";
	private static final String kActionCommandMenuItemNavigationInvertPanningDirections = "menuItem.Navigation.InvertPanningDirections";
	private static final String kActionCommandMenuItemNavigationShowZoomInformation = "menuItem.Navigation.ShowZoomInformation";
	private static final String kActionCommandMenuItemNavigationShowZoomInformationToggle = kActionCommandMenuItemNavigationShowZoomInformation + ".Toggle";
	private static final String kActionCommandMenuItemNavigationLockAspectRatio = "menuItem.Navigation.LockAspectRatio";
	private static final String kActionCommandMenuItemNavigationCentredZooming = "menuItem.Navigation.CentredZooming";
	private static final String kActionCommandMenuItemNavigationResetZoom = "menuItem.Navigation.ResetZoom";
	private static final String kActionCommandMenuItemNavigationZoomToLevelCoordinates = "menuItem.Navigation.ZoomToLevelCoordinates";
	private static final String kActionCommandMenuItemNavigationZoomToLevelGraphical = "menuItem.Navigation.ZoomToLevelGraphical";
	private static final String kActionCommandMenuItemNavigationShowAxes = "menuItem.Navigation.ShowAxes";
	private static final String kActionCommandMenuItemNavigationShowAxesToggle = kActionCommandMenuItemNavigationShowAxes + ".Toggle";
	private static final String kActionCommandMenuItemNavigationShowOverlayGrid = "menuItem.Navigation.ShowOverlayGrid";
	private static final String kActionCommandMenuItemNavigationShowOverlayGridToggle = kActionCommandMenuItemNavigationShowOverlayGrid + ".Toggle";
	private static final String kActionCommandMenuItemNavigationInvertYAxis = "menuItem.Navigation.InvertYAxis";
	private static final String kActionCommandMenuItemNavigationShowCurrentLocation = "menuItem.Navigation.ShowCurrentLocation";
	private static final String kActionCommandMenuItemNavigationShowCurrentLocationToggle = kActionCommandMenuItemNavigationShowCurrentLocation + ".Toggle";
	private static final String kActionCommandMenuItemNavigationShowMagnifyingGlass = "menuItem.Navigation.ShowMagnifyingGlass";
	private static final String kActionCommandMenuItemNavigationShowMagnifyingGlassToggle = kActionCommandMenuItemNavigationShowMagnifyingGlass + ".Toggle";
	private static final String kActionCommandMenuItemNavigationSetMagnifyingGlassSize = "menuItem.Navigation.SetMagnifyingGlassSize";
	private static final String kActionCommandMenuItemNavigationShowMainFractalOverview = "menuItem.Navigation.ShowMainFractalOverview";
	private static final String kActionCommandMenuItemNavigationSpecifyScreenBounds = "menuItem.Navigation.SpecifyScreenBounds";
	private static final String kActionCommandMenuItemNavigationSpecifyComplexBounds = "menuItem.Navigation.SpecifyComplexBounds";

	private static final String kActionCommandMenuItemFractalDoubleClickModeSwitchDualMainFractal = "menuItem.Fractal.DoubleClickModeSwitchDualMainFractal";
	private static final String kActionCommandMenuItemFractalSwitchFractalType = "menuItem.Fractal.SwitchFractalType";
	private static final String kActionCommandMenuItemFractalDoubleClickModeSetOrbitStartingPoint = "menuItem.Fractal.DoubleClickModeSetOrbitStartingPoint";
	private static final String kActionCommandMenuItemFractalResetOrbitStartingPoint = "menuItem.Fractal.ResetOrbitStartingPoint";
	private static final String kActionCommandMenuItemFractalShowInset = "menuItem.Fractal.ShowInset";
	private static final String kActionCommandMenuItemFractalShowInsetToggle = kActionCommandMenuItemFractalShowInset + ".Toggle";
	private static final String kActionCommandMenuItemFractalAutoSuppressDualFractal = "menuItem.Fractal.AutoSuppressDualFractal";
	private static final String kActionCommandMenuItemFractalAutoZoomInset = "menuItem.Fractal.AutoZoomInset";
	private static final String kActionCommandMenuItemFractalSetInsetSize = "menuItem.Fractal.SetInsetSize";
	private static final String kActionCommandMenuItemFractalInsetFractalIsDeformedMainFractal = "menuItem.Fractal.InsetFractalIsDeformedMainFractal";
	private static final String kActionCommandMenuItemFractalShowOrbits = "menuItem.Fractal.ShowOrbits";
	private static final String kActionCommandMenuItemFractalShowOrbitsToggle = kActionCommandMenuItemFractalShowOrbits + ".Toggle";
	private static final String kActionCommandMenuItemFractalShowOrbitPaths = "menuItem.Fractal.ShowOrbitPaths";
	private static final String kActionCommandMenuItemFractalScaleOrbitsToScreen = "menuItem.Fractal.ScaleOrbitsToScreen";
	private static final String kActionCommandMenuItemFractalScaleOrbitsToScreenToggle = kActionCommandMenuItemFractalScaleOrbitsToScreen + ".Toggle";
	private static final String kActionCommandMenuItemFractalShowOrbitAnalyses = "menuItem.Fractal.ShowOrbitAnalyses";
	private static final String kActionCommandMenuItemFractalShowOrbitAnalysesToggle = kActionCommandMenuItemFractalShowOrbitAnalyses + ".Toggle";
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
	private static final String kActionCommandMenuItemFractalFamilyDucksSecans = "menuItem.Fractal.Family.DucksSecans";
	private static final String kActionCommandMenuItemFractalFamilyBarnsleyTree = "menuItem.Fractal.Family.BarnsleyTree";
	private static final String kActionCommandMenuItemFractalFamilyCollatz = "menuItem.Fractal.Family.Collatz";
	private static final String kActionCommandMenuItemFractalFamilyPhoenix = "menuItem.Fractal.Family.Phoenix";
	private static final String kActionCommandMenuItemFractalFamilyManowar = "menuItem.Fractal.Family.Manowar";
	private static final String kActionCommandMenuItemFractalFamilyQuadbrot = "menuItem.Fractal.Family.Quadbrot";
	private static final String kActionCommandMenuItemFractalFamilyTetration = "menuItem.Fractal.Family.Tetration";
	private static final String kActionCommandMenuItemFractalFamilyTetrationDual = "menuItem.Fractal.Family.TetrationDual";
	private static final String kActionCommandMenuItemFractalFamilyIOfMedusa = "menuItem.Fractal.Family.IOfMedusa";
	private static final String kActionCommandMenuItemFractalFamilyIOfTheStorm = "menuItem.Fractal.Family.IOfTheStorm";
	private static final String kActionCommandMenuItemFractalFamilyAtTheCShore = "menuItem.Fractal.Family.AtTheCShore";
	private static final String kActionCommandMenuItemFractalFamilyLogarithmicJulia = "menuItem.Fractal.Family.LogarithmicJulia";
	private static final String kActionCommandMenuItemFractalFamilyHyperbolicSineJulia = "menuItem.Fractal.Family.HyperbolicSineJulia";

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
	private static final String kActionCommandMenuItemFractalAutoSelectMaxNrOfIterations = "menuItem.Fractal.AutoSelectMaxNrOfIterations";
	private static final String kActionCommandMenuItemFractalSetEscapeRadius = "menuItem.Fractal.SetEscapeRadius";
	private static final String kActionCommandMenuItemFractalCopyCoordinates = "menuItem.Fractal.CopyCoordinatesToClipboard";
	private static final String kActionCommandMenuItemFractalRefreshScreen = "menuItem.Fractal.RefreshScreen";

	private static final String kActionCommandMenuItemColorMapExteriorBone = "menuItem.ColorMap.Exterior.Bone";
	private static final String kActionCommandMenuItemColorMapExteriorCopper = "menuItem.ColorMap.Exterior.Copper";
	private static final String kActionCommandMenuItemColorMapExteriorDiscontinuousBlueWhiteGreen = "menuItem.ColorMap.Exterior.DiscontinuousBlueWhiteGreen";
	private static final String kActionCommandMenuItemColorMapExteriorDiscontinuousDarkRedYellow = "menuItem.ColorMap.Exterior.DiscontinuousDarkRedYellow";
	private static final String kActionCommandMenuItemColorMapExteriorBlackAndWhite = "menuItem.ColorMap.Exterior.BlackAndWhite";
	private static final String kActionCommandMenuItemColorMapExteriorGrayScale = "menuItem.ColorMap.Exterior.GrayScale";
	private static final String kActionCommandMenuItemColorMapExteriorGrayScaleTrimmed = "menuItem.ColorMap.Exterior.GrayScaleTrimmed";
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
	private static final String kActionCommandMenuItemColorMapExteriorRandom = "menuItem.ColorMap.Exterior.Random";
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
	private static final String kActionCommandMenuItemColorMapTigerGrayScaleTrimmed = "menuItem.ColorMap.Tiger.GrayScaleTrimmed";
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
	private static final String kActionCommandMenuItemColorMapTigerRandom = "menuItem.ColorMap.Tiger.Random";
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
	private static final String kActionCommandMenuItemColorMapInteriorGrayScaleTrimmed = "menuItem.ColorMap.Interior.GrayScaleTrimmed";
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
	private static final String kActionCommandMenuItemColorMapInteriorRandom = "menuItem.ColorMap.Interior.Random";
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
	private static final String kActionCommandMenuItemColorMapExteriorUseAverageDistance = "menuItem.ColorMap.Exterior.UseAverageDistance";
	private static final String kActionCommandMenuItemColorMapExteriorUseAngle = "menuItem.ColorMap.Exterior.UseAngle";
	private static final String kActionCommandMenuItemColorMapExteriorUseLyapunovExponent = "menuItem.ColorMap.Exterior.UseLyapunovExponent";
	private static final String kActionCommandMenuItemColorMapExteriorUseCurvature = "menuItem.ColorMap.Exterior.UseCurvature";
	private static final String kActionCommandMenuItemColorMapExteriorUseStriping = "menuItem.ColorMap.Exterior.UseStriping";
	private static final String kActionCommandMenuItemColorMapExteriorSetStripingDensity = "menuItem.ColorMap.Exterior.SetStripingDensity";
	private static final String kActionCommandMenuItemColorMapExteriorUseMinimumGaussianIntegersDistance = "menuItem.ColorMap.Exterior.UseMinimumGaussianIntegersDistance";
	private static final String kActionCommandMenuItemColorMapExteriorUseAverageGaussianIntegersDistance = "menuItem.ColorMap.Exterior.UseAverageGaussianIntegersDistance";
	private static final String kActionCommandMenuItemColorMapExteriorSetGaussianIntegersTrapFactor = "menuItem.ColorMap.Exterior.SetGaussianIntegersTrapFactor";
	private static final String kActionCommandMenuItemColorMapExteriorUseExteriorDistance = "menuItem.ColorMap.Exterior.UseExteriorDistance";
	private static final String kActionCommandMenuItemColorMapExteriorUseOrbitTrapDisk = "menuItem.ColorMap.Exterior.UseOrbitTrapDisk";
	private static final String kActionCommandMenuItemColorMapExteriorSetOrbitTrapDiskCentre = "menuItem.ColorMap.Exterior.SetOrbitTrapDiskCentre";
	private static final String kActionCommandMenuItemColorMapExteriorSetOrbitTrapDiskRadius = "menuItem.ColorMap.Exterior.SetOrbitTrapDiskRadius";
	private static final String kActionCommandMenuItemColorMapExteriorUseOrbitTrapCrossStalks = "menuItem.ColorMap.Exterior.UseOrbitTrapCrossStalks";
	private static final String kActionCommandMenuItemColorMapExteriorSetOrbitTrapCrossStalksCentre = "menuItem.ColorMap.Exterior.SetOrbitTrapCrossStalksCentre";
	private static final String kActionCommandMenuItemColorMapExteriorUseOrbitTrapSine = "menuItem.ColorMap.Exterior.UseOrbitTrapSine";
	private static final String kActionCommandMenuItemColorMapExteriorSetOrbitTrapSineParameters = "menuItem.ColorMap.Exterior.SetOrbitTrapSineParameters";
	private static final String kActionCommandMenuItemColorMapExteriorUseOrbitTrapTangens = "menuItem.ColorMap.Exterior.UseOrbitTrapTangens";
	private static final String kActionCommandMenuItemColorMapExteriorSetOrbitTrapTangensParameters = "menuItem.ColorMap.Exterior.SetOrbitTrapTangensParameters";
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
	private static final String kActionCommandMenuItemColorMapInteriorUseAverageDistance = "menuItem.ColorMap.Interior.UseAverageDistance";
	private static final String kActionCommandMenuItemColorMapInteriorUseAngle = "menuItem.ColorMap.Interior.UseAngle";
	private static final String kActionCommandMenuItemColorMapInteriorUseLyapunovExponent = "menuItem.ColorMap.Interior.UseLyapunovExponent";
	private static final String kActionCommandMenuItemColorMapInteriorUseCurvature = "menuItem.ColorMap.Interior.UseCurvature";
	private static final String kActionCommandMenuItemColorMapInteriorUseStriping = "menuItem.ColorMap.Interior.UseStriping";
	private static final String kActionCommandMenuItemColorMapInteriorSetStripingDensity = "menuItem.ColorMap.Interior.SetStripingDensity";
	private static final String kActionCommandMenuItemColorMapInteriorUseMinimumGaussianIntegersDistance = "menuItem.ColorMap.Interior.UseMinimumGaussianIntegersDistance";
	private static final String kActionCommandMenuItemColorMapInteriorUseAverageGaussianIntegersDistance = "menuItem.ColorMap.Interior.UseAverageGaussianIntegersDistance";
	private static final String kActionCommandMenuItemColorMapInteriorSetGaussianIntegersTrapFactor = "menuItem.ColorMap.Interior.SetGaussianIntegersTrapFactor";
	private static final String kActionCommandMenuItemColorMapInteriorUseExteriorDistance = "menuItem.ColorMap.Interior.UseExteriorDistance";
	private static final String kActionCommandMenuItemColorMapInteriorUseOrbitTrapDisk = "menuItem.ColorMap.Interior.UseOrbitTrapDisk";
	private static final String kActionCommandMenuItemColorMapInteriorSetOrbitTrapDiskCentre = "menuItem.ColorMap.Interior.SetOrbitTrapDiskCentre";
	private static final String kActionCommandMenuItemColorMapInteriorSetOrbitTrapDiskRadius = "menuItem.ColorMap.Interior.SetOrbitTrapDiskRadius";
	private static final String kActionCommandMenuItemColorMapInteriorUseOrbitTrapCrossStalks = "menuItem.ColorMap.Interior.UseOrbitTrapCrossStalks";
	private static final String kActionCommandMenuItemColorMapInteriorSetOrbitTrapCrossStalksCentre = "menuItem.ColorMap.Interior.SetOrbitTrapCrossStalksCentre";
	private static final String kActionCommandMenuItemColorMapInteriorUseOrbitTrapSine = "menuItem.ColorMap.Interior.UseOrbitTrapSine";
	private static final String kActionCommandMenuItemColorMapInteriorSetOrbitTrapSineParameters = "menuItem.ColorMap.Interior.SetOrbitTrapSineParameters";
	private static final String kActionCommandMenuItemColorMapInteriorUseOrbitTrapTangens = "menuItem.ColorMap.Interior.UseOrbitTrapTangens";
	private static final String kActionCommandMenuItemColorMapInteriorSetOrbitTrapTangensParameters = "menuItem.ColorMap.Interior.SetOrbitTrapTangensParameters";

	private static final String kActionCommandMenuItemColorMapUseLinearScaling = "menuItem.ColorMap.UseLinearScaling";
	private static final String kActionCommandMenuItemColorMapUseLogarithmicScaling = "menuItem.ColorMap.UseLogarithmicScaling";
	private static final String kActionCommandMenuItemColorMapUseExponentialScaling = "menuItem.ColorMap.UseExponentialScaling";
	private static final String kActionCommandMenuItemColorMapUseSqrtScaling = "menuItem.ColorMap.UseSqrtScaling";
	private static final String kActionCommandMenuItemColorMapSetScalingParameters = "menuItem.ColorMap.SetScalingParameters";
	private static final String kActionCommandMenuItemColorMapUseRankOrderScaling = "menuItem.ColorMap.UseRankOrderScaling";
	private static final String kActionCommandMenuItemColorMapUseRankOrderScalingToggle = kActionCommandMenuItemColorMapUseRankOrderScaling + ".Toggle";
	private static final String kActionCommandMenuItemColorMapRestrictHighIterationCountColors = "menuItem.ColorMap.RestrictHighIterationCountColors";
	private static final String kActionCommandMenuItemColorMapRestrictHighIterationCountColorsToggle = kActionCommandMenuItemColorMapRestrictHighIterationCountColors + ".Toggle";
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
	private static final String kActionCommandMenuItemMultithreadingInterrupt = "menuItem.MultiThreading.Interrupt";

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
	private JPanel fContentPane;
	private JScrollPane fFractalScrollPane;
	private FractalPanel fFractalPanel;
	private JToolBar fToolBar;
	private int fColorCyclingDelay;
	private double fColorCyclingSmoothness;
	private boolean fColorCyclingDirectionForward;
	private javax.swing.Timer fColorCyclingTimer;
	private JLabel fStatusBarCalculationTimeLabel;
	private JProgressUpdateGlassPane fProgressUpdateGlassPane;
	private HashMap<String,JMenuItem> fMenuItems;
	private HashMap<String,AbstractButton> fToolBarToggles;
	private String fLastSelectedFractal;
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
	private ArrayList<StoredScreenSize> fStoredScreenSizes;

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

		// post initialisation
		fMenuItems.get(kActionCommandMenuItemNavigationInvertYAxis).setSelected(fIteratorController.getFractalIterator().getInvertYAxis());
		adjustMenusToFractal();

		// install key bindings for navigation
		try {
			JComponent contentPane = ((JComponent) getContentPane());
			InputMap inputMap = contentPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			ActionMap actionMap = contentPane.getActionMap();
			installKeyBindings(inputMap,actionMap);

			// reassign toolbar key bindings
			inputMap = fToolBar.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			actionMap = fToolBar.getActionMap();
			installKeyBindings(inputMap,actionMap);
		}
		catch (ClassCastException exc) {
			// ignore
		}
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

		// explicitly ignore input when we are in zoom thumbnail selection mode
		if (fFractalPanel.getZoomThumbnailSelectionMode()) {
			return;
		}

		String command = e.getActionCommand();
		AFractalIterator fractalIterator = fIteratorController.getFractalIterator();
		ColoringParameters coloringParameters = fIteratorController.getColoringParameters();

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
				fileChooser.setFileFilter(new JFileFilter(kZIPFileExtension.toUpperCase(),I18NL10N.translate("text.File.FraxionZIPDescription")));
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
			fileChooser.setFileFilter(new JFileFilter(kZIPFileExtension.toUpperCase(),I18NL10N.translate("text.File.FraxionZIPDescription")));
			fileChooser.setSelectedFile(new File(createDefaultFilename(kZIPFileExtension,false)));

			if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				String filename = fileChooser.getSelectedFile().getPath();
				fLastOpenedFolder = filename.substring(0,filename.lastIndexOf(File.separator));

				if (!filename.endsWith("." + kZIPFileExtension)) {
					filename += "." + kZIPFileExtension;
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
						else if (familyName.equalsIgnoreCase((new DucksSecansFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new DucksSecansFractalIterator());
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
						else if (familyName.equalsIgnoreCase((new QuadbrotFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new QuadbrotFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new TetrationFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new TetrationFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new TetrationDualFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new TetrationDualFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new IOfMedusaFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new IOfMedusaFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new IOfTheStormFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new IOfTheStormFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new AtTheCShoreFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new AtTheCShoreFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new LogarithmicJuliaFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new LogarithmicJuliaFractalIterator());
						}
						else if (familyName.equalsIgnoreCase((new HyperbolicSineJuliaFractalIterator()).getFamilyName())) {
							fIteratorController.setFractalIteratorFamily(new HyperbolicSineJuliaFractalIterator());
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

						fractalIterator = fIteratorController.getFractalIterator();

						// if necessary switch to the dual fractal
						if ((fractalIterator instanceof GlynnFractalIterator) ||
								(fractalIterator instanceof BarnsleyTreeFractalIterator) ||
								(fractalIterator instanceof PhoenixFractalIterator) ||
								(fractalIterator instanceof TetrationDualFractalIterator) ||
								(fractalIterator instanceof IOfMedusaFractalIterator) ||
								(fractalIterator instanceof IOfTheStormFractalIterator) ||
								(fractalIterator instanceof AtTheCShoreFractalIterator)) {
							fractalIterator.setFractalType(AFractalIterator.EFractalType.kDualFractal);
						}

						// load fractal parameters
						fractalIterator.plainTextLoadParameters(tfp);

						// load fractal colouring parameters
						coloringParameters.plainTextLoad(tfp);
						fractalIterator.setCalculateAdvancedColoring(coloringParameters.fCalculateAdvancedColoring);

						// adjust the zoom stack
						fFractalPanel.getZoomStack().clear();
						fFractalPanel.getZoomStack().push(fractalIterator.getDefaultP1(),fractalIterator.getDefaultP2());
						fFractalPanel.getZoomStack().push(fractalIterator.getP1(),fractalIterator.getP2());

						adjustMenusToFractal();

						// adjust canvas dimensions
						fFractalPanel.revalidate();
						fIteratorController.recalc();
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
						fractalIterator.plainTextSaveParameters(tfw);

						// save fractal colouring parameters
						coloringParameters.plainTextSave(tfw);

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
			JIncompleteWarningDialog.warn(this,"GUIApplication::actionPerformed()");
			// resetZoom();
			// push new coords(); => zoomToStack()
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFilePrintFractal)) {
			PrinterJob printerJob = PrinterJob.getPrinterJob();

			PageFormat pageFormat = printerJob.defaultPage();
			if (fFractalPanel.isLandscapeOriented()) {
				pageFormat.setOrientation(PageFormat.LANDSCAPE);
			}

			printerJob.setPrintable(fFractalPanel,pageFormat);
			boolean ok = printerJob.printDialog();
			if (ok) {
				try {
					printerJob.print();
				}
				catch (PrinterException exc) {
					kLogger.error(exc.getMessage());
				}
			}
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
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationMenuPanDown)) {
			fFractalPanel.pan(FractalPanel.EPanDirection.kDown,fNavigationPanningSize,fMenuItems.get(kActionCommandMenuItemNavigationInvertPanningDirections).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationSetPanningSize)) {
			NavigationPanningSizeChooser navigationPanningSizeChooser = new NavigationPanningSizeChooser(this,fNavigationPanningSize);
			if (!navigationPanningSizeChooser.isCancelled()) {
				fNavigationPanningSize = navigationPanningSizeChooser.getSelectedPanningSize();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationInvertPanningDirections)) {
			// ignore (this is automatically handled via the KeyListener)
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationShowZoomInformation)) {
			fFractalPanel.setShowZoomInformation(fMenuItems.get(kActionCommandMenuItemNavigationShowZoomInformation).isSelected());
			fToolBarToggles.get(kActionCommandMenuItemNavigationShowZoomInformationToggle).setSelected(fMenuItems.get(kActionCommandMenuItemNavigationShowZoomInformation).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationShowZoomInformationToggle)) {
			fFractalPanel.setShowZoomInformation(fToolBarToggles.get(kActionCommandMenuItemNavigationShowZoomInformationToggle).isSelected());
			fMenuItems.get(kActionCommandMenuItemNavigationShowZoomInformation).setSelected(fToolBarToggles.get(kActionCommandMenuItemNavigationShowZoomInformationToggle).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationLockAspectRatio)) {
			coloringParameters.fLockAspectRatio = fMenuItems.get(kActionCommandMenuItemNavigationLockAspectRatio).isSelected();
			fFractalPanel.zoomToStack();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationCentredZooming)) {
			fFractalPanel.setCentredZooming(fMenuItems.get(kActionCommandMenuItemNavigationCentredZooming).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationResetZoom)) {
			fFractalPanel.resetZoom();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationZoomToLevelCoordinates)) {
			ZoomLevelChooser zoomLevelChooser = new ZoomLevelChooser(this,fFractalPanel.getZoomStack());
			if (!zoomLevelChooser.isCancelled()) {
				fFractalPanel.zoomToLevel(zoomLevelChooser.getSelectedZoomLevel());
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationZoomToLevelGraphical)) {
			// toggle showing and selecting the zoom thumbnails on a single right click
			fFractalPanel.setZoomThumbnailSelectionMode(true);
			hideMenusAndToolBar();
			fFractalPanel.repaint();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationShowAxes)) {
			fFractalPanel.setShowAxes(fMenuItems.get(kActionCommandMenuItemNavigationShowAxes).isSelected());
			fToolBarToggles.get(kActionCommandMenuItemNavigationShowAxesToggle).setSelected(fMenuItems.get(kActionCommandMenuItemNavigationShowAxes).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationShowAxesToggle)) {
			fFractalPanel.setShowAxes(fToolBarToggles.get(kActionCommandMenuItemNavigationShowAxesToggle).isSelected());
			fMenuItems.get(kActionCommandMenuItemNavigationShowAxes).setSelected(fToolBarToggles.get(kActionCommandMenuItemNavigationShowAxesToggle).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationShowOverlayGrid)) {
			fFractalPanel.setShowOverlayGrid(fMenuItems.get(kActionCommandMenuItemNavigationShowOverlayGrid).isSelected());
			fToolBarToggles.get(kActionCommandMenuItemNavigationShowOverlayGridToggle).setSelected(fMenuItems.get(kActionCommandMenuItemNavigationShowOverlayGrid).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationShowOverlayGridToggle)) {
			fFractalPanel.setShowOverlayGrid(fToolBarToggles.get(kActionCommandMenuItemNavigationShowOverlayGridToggle).isSelected());
			fMenuItems.get(kActionCommandMenuItemNavigationShowOverlayGrid).setSelected(fToolBarToggles.get(kActionCommandMenuItemNavigationShowOverlayGridToggle).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationInvertYAxis)) {
			fractalIterator.setInvertYAxis(fMenuItems.get(kActionCommandMenuItemNavigationInvertYAxis).isSelected());
			fIteratorController.recalc();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationShowCurrentLocation)) {
			fFractalPanel.setShowCurrentLocation(fMenuItems.get(kActionCommandMenuItemNavigationShowCurrentLocation).isSelected());
			fToolBarToggles.get(kActionCommandMenuItemNavigationShowCurrentLocationToggle).setSelected(fMenuItems.get(kActionCommandMenuItemNavigationShowCurrentLocation).isSelected());
			changeLocationMouseCursor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationShowCurrentLocationToggle)) {
			fFractalPanel.setShowCurrentLocation(fToolBarToggles.get(kActionCommandMenuItemNavigationShowCurrentLocationToggle).isSelected());
			fMenuItems.get(kActionCommandMenuItemNavigationShowCurrentLocation).setSelected(fToolBarToggles.get(kActionCommandMenuItemNavigationShowCurrentLocationToggle).isSelected());
			changeLocationMouseCursor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationShowMagnifyingGlass)) {
			fFractalPanel.setShowMagnifyingGlass(fMenuItems.get(kActionCommandMenuItemNavigationShowMagnifyingGlass).isSelected());
			fToolBarToggles.get(kActionCommandMenuItemNavigationShowMagnifyingGlassToggle).setSelected(fMenuItems.get(kActionCommandMenuItemNavigationShowMagnifyingGlass).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationShowMagnifyingGlassToggle)) {
			fFractalPanel.setShowMagnifyingGlass(fToolBarToggles.get(kActionCommandMenuItemNavigationShowMagnifyingGlassToggle).isSelected());
			fMenuItems.get(kActionCommandMenuItemNavigationShowMagnifyingGlass).setSelected(fToolBarToggles.get(kActionCommandMenuItemNavigationShowMagnifyingGlassToggle).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationSetMagnifyingGlassSize)) {
			MagnifyingGlassSizeChooser magnifyingGlassSizeChooser = new MagnifyingGlassSizeChooser(this,fFractalPanel.getMagnifyingGlassRegion(),fFractalPanel.getMagnifyingGlassSize());
			if (!magnifyingGlassSizeChooser.isCancelled()) {
				fFractalPanel.setMagnifyingGlassSize(magnifyingGlassSizeChooser.getSelectedRegion(),magnifyingGlassSizeChooser.getSelectedSize());
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationShowMainFractalOverview)) {
			fFractalPanel.setShowMainFractalOverview(fMenuItems.get(kActionCommandMenuItemNavigationShowMainFractalOverview).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationSpecifyScreenBounds)) {
			Insets screenInsets = getScreenInsets();
			Insets mainInsets = getInsets();
			Insets scrollInsets = fFractalScrollPane.getInsets();
			int currentWindowWidth = getWidth() - screenInsets.left - screenInsets.right - mainInsets.left - mainInsets.right - scrollInsets.left - scrollInsets.right - fFractalScrollPane.getVerticalScrollBar().getPreferredSize().width;
			int currentWindowHeight = getHeight() - screenInsets.top - screenInsets.bottom - mainInsets.top - mainInsets.bottom - scrollInsets.top - scrollInsets.bottom - fFractalScrollPane.getHorizontalScrollBar().getPreferredSize().height;

			// subtract the toolbar's size if it is not floating
			BorderLayout layout = (BorderLayout) fContentPane.getLayout();
			if ((fToolBar == layout.getLayoutComponent(BorderLayout.EAST)) || (fToolBar == layout.getLayoutComponent(BorderLayout.WEST))) {
				currentWindowWidth -= fToolBar.getWidth();
			}
			else if ((fToolBar == layout.getLayoutComponent(BorderLayout.NORTH)) || (fToolBar == layout.getLayoutComponent(BorderLayout.SOUTH))) {
				currentWindowHeight -= fToolBar.getHeight();
			}

			ScreenBoundsChooser screenBoundsChooser = new ScreenBoundsChooser(
				this,
				fractalIterator.getScreenWidth(),
				fractalIterator.getScreenHeight(),
				currentWindowWidth,
				currentWindowHeight,
				fStoredScreenSizes);
			if (!screenBoundsChooser.isCancelled()) {
				if (!screenBoundsChooser.isProjectedMemoryUsageAvailable()) {
					JWarningDialog.warn(this,I18NL10N.translate("error.NotEnoughMemoryAvailable"));
				}
				else {
					int newWidth = screenBoundsChooser.getSelectedScreenWidth();
					int newHeight = screenBoundsChooser.getSelectedScreenHeight();
					fStoredScreenSizes = screenBoundsChooser.getSelectedStoredScreenSizes();
					fractalIterator.setScreenBounds(newWidth,newHeight);
					fFractalPanel.revalidate();
					fFractalPanel.zoomToStack(newWidth,newHeight);
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemNavigationSpecifyComplexBounds)) {
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
					ZoomStack zoomStack = fFractalPanel.getZoomStack();
					zoomStack.clear();
					zoomStack.push(fractalIterator.getDefaultP1(),fractalIterator.getDefaultP2());
					zoomStack.addThumbnail(null);
					fFractalPanel.zoomIn(complexBoundsChooser.getSelectedP1(),complexBoundsChooser.getSelectedP2());
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalDoubleClickModeSwitchDualMainFractal)) {
			fDoubleClickMode = EDoubleClickMode.kSwitchMainDualFractal;
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalSwitchFractalType)) {
			switchMainDualFractal();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalDoubleClickModeSetOrbitStartingPoint)) {
			fDoubleClickMode = EDoubleClickMode.kChangeOrbitStartingPoint;
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalResetOrbitStartingPoint)) {
			fractalIterator.resetMainFractalOrbitStartingPoint();
			fIteratorController.recalc();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalShowInset)) {
			fFractalPanel.setShowInset(fMenuItems.get(kActionCommandMenuItemFractalShowInset).isSelected());
			fToolBarToggles.get(kActionCommandMenuItemFractalShowInsetToggle).setSelected(fMenuItems.get(kActionCommandMenuItemFractalShowInset).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalShowInsetToggle)) {
			fFractalPanel.setShowInset(fToolBarToggles.get(kActionCommandMenuItemFractalShowInsetToggle).isSelected());
			fMenuItems.get(kActionCommandMenuItemFractalShowInset).setSelected(fToolBarToggles.get(kActionCommandMenuItemFractalShowInsetToggle).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalAutoSuppressDualFractal)) {
			fFractalPanel.setAutoSuppressDualFractal(fMenuItems.get(kActionCommandMenuItemFractalAutoSuppressDualFractal).isSelected());
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
			fToolBarToggles.get(kActionCommandMenuItemFractalShowOrbitsToggle).setSelected(fMenuItems.get(kActionCommandMenuItemFractalShowOrbits).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalShowOrbitsToggle)) {
			fFractalPanel.setShowOrbits(fToolBarToggles.get(kActionCommandMenuItemFractalShowOrbitsToggle).isSelected());
			fMenuItems.get(kActionCommandMenuItemFractalShowOrbits).setSelected(fToolBarToggles.get(kActionCommandMenuItemFractalShowOrbitsToggle).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalShowOrbitPaths)) {
			fFractalPanel.setShowOrbitPaths(fMenuItems.get(kActionCommandMenuItemFractalShowOrbitPaths).isSelected());
			// also show orbits simultaneously
			if (fMenuItems.get(kActionCommandMenuItemFractalShowOrbitPaths).isSelected()) {
				fMenuItems.get(kActionCommandMenuItemFractalShowOrbits).setSelected(true);
				fFractalPanel.setShowOrbits(true);
				fToolBarToggles.get(kActionCommandMenuItemFractalShowOrbitsToggle).setSelected(fMenuItems.get(kActionCommandMenuItemFractalShowOrbits).isSelected());
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalScaleOrbitsToScreen)) {
			fFractalPanel.setScaleOrbitsToScreen(fMenuItems.get(kActionCommandMenuItemFractalScaleOrbitsToScreen).isSelected());
			// also show orbits simultaneously
			if (fMenuItems.get(kActionCommandMenuItemFractalScaleOrbitsToScreen).isSelected()) {
				fMenuItems.get(kActionCommandMenuItemFractalShowOrbits).setSelected(true);
				fFractalPanel.setShowOrbits(true);
				fToolBarToggles.get(kActionCommandMenuItemFractalShowOrbitsToggle).setSelected(fMenuItems.get(kActionCommandMenuItemFractalShowOrbits).isSelected());
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalScaleOrbitsToScreenToggle)) {
			fFractalPanel.setScaleOrbitsToScreen(fToolBarToggles.get(kActionCommandMenuItemFractalScaleOrbitsToScreenToggle).isSelected());
			fMenuItems.get(kActionCommandMenuItemFractalScaleOrbitsToScreen).setSelected(fToolBarToggles.get(kActionCommandMenuItemFractalScaleOrbitsToScreenToggle).isSelected());
			// also show orbits simultaneously
			if (fMenuItems.get(kActionCommandMenuItemFractalScaleOrbitsToScreen).isSelected()) {
				fMenuItems.get(kActionCommandMenuItemFractalShowOrbits).setSelected(true);
				fFractalPanel.setShowOrbits(true);
				fToolBarToggles.get(kActionCommandMenuItemFractalShowOrbitsToggle).setSelected(fMenuItems.get(kActionCommandMenuItemFractalShowOrbits).isSelected());
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalShowOrbitAnalyses)) {
			fFractalPanel.setShowOrbitAnalyses(fMenuItems.get(kActionCommandMenuItemFractalShowOrbitAnalyses).isSelected());
			fToolBarToggles.get(kActionCommandMenuItemFractalShowOrbitAnalysesToggle).setSelected(fMenuItems.get(kActionCommandMenuItemFractalShowOrbitAnalyses).isSelected());
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalShowOrbitAnalysesToggle)) {
			fFractalPanel.setShowOrbitAnalyses(fToolBarToggles.get(kActionCommandMenuItemFractalShowOrbitAnalysesToggle).isSelected());
			fMenuItems.get(kActionCommandMenuItemFractalShowOrbitAnalyses).setSelected(fToolBarToggles.get(kActionCommandMenuItemFractalShowOrbitAnalysesToggle).isSelected());
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
			MaxNrOfIterationsInOrbitAnalysesChooser maxNrOfIterationsInOrbitAnalysesChooser = new MaxNrOfIterationsInOrbitAnalysesChooser(this,fFractalPanel.getMaxNrOfIterationsInOrbitAnalyses(),fractalIterator.getMaxNrOfIterations());
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
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyDucksSecans) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyBarnsleyTree) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyCollatz) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyPhoenix) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyManowar) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyQuadbrot) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTetration) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTetrationDual) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyIOfMedusa) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyIOfTheStorm) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyAtTheCShore) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyLogarithmicJulia) ||
						command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyHyperbolicSineJulia) ||
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
				Dimension screenBounds = fractalIterator.getScreenBounds();

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
						coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kAverageDistance;
						adjustMenusToFractal();
						break;
					case kActionCommandMenuItemFractalFamilyDucksSecans:
						fIteratorController.setFractalIteratorFamily(new DucksSecansFractalIterator());
						coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kAverageDistance;
						adjustMenusToFractal();
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
					case kActionCommandMenuItemFractalFamilyQuadbrot:
						fIteratorController.setFractalIteratorFamily(new QuadbrotFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyTetration:
						fIteratorController.setFractalIteratorFamily(new TetrationFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyTetrationDual:
						fIteratorController.setFractalIteratorFamily(new TetrationDualFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyIOfMedusa:
						fIteratorController.setFractalIteratorFamily(new IOfMedusaFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyIOfTheStorm:
						fIteratorController.setFractalIteratorFamily(new IOfTheStormFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyAtTheCShore:
						fIteratorController.setFractalIteratorFamily(new AtTheCShoreFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyLogarithmicJulia:
						fIteratorController.setFractalIteratorFamily(new LogarithmicJuliaFractalIterator());
						break;
					case kActionCommandMenuItemFractalFamilyHyperbolicSineJulia:
						fIteratorController.setFractalIteratorFamily(new HyperbolicSineJuliaFractalIterator());
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
					default: break;
				}

				fractalIterator = fIteratorController.getFractalIterator();

				// if necessary switch to the dual fractal
				if ((fractalIterator instanceof GlynnFractalIterator) ||
						(fractalIterator instanceof BarnsleyTreeFractalIterator) ||
						(fractalIterator instanceof PhoenixFractalIterator) ||
						(fractalIterator instanceof TetrationDualFractalIterator) ||
						(fractalIterator instanceof IOfMedusaFractalIterator) ||
						(fractalIterator instanceof IOfTheStormFractalIterator) ||
						(fractalIterator instanceof AtTheCShoreFractalIterator)) {
					fractalIterator.setFractalType(AFractalIterator.EFractalType.kDualFractal);
				}

				// if necessary activate advanced colouring
				if ((fractalIterator instanceof TetrationDualFractalIterator) ||
						(fractalIterator instanceof IOfMedusaFractalIterator) ||
						(fractalIterator instanceof IOfTheStormFractalIterator) ||
						(fractalIterator instanceof AtTheCShoreFractalIterator)) {
					fractalIterator.setCalculateAdvancedColoring(true);
				}

				adjustMenusToFractal();
				setupMarkusLyapunovFractal();
				fLastSelectedFractal = command;

				// reset zoomstack and create new top
				fFractalPanel.getZoomStack().clear();
				fractalIterator.setScreenBounds(screenBounds);
				fFractalPanel.zoomIn(fractalIterator.getDefaultP1(),fractalIterator.getDefaultP2());
			} // if (proceed)
			else {
				fMenuItems.get(fLastSelectedFractal).setSelected(true);
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyRandelbrotSetNoiseLevel)) {
			if (fractalIterator instanceof RandelbrotFractalIterator) {
				NoiseLevelChooser noiseLevelChooser = new NoiseLevelChooser(this,((RandelbrotFractalIterator) fractalIterator).getNoiseLevel());
				if (!noiseLevelChooser.isCancelled()) {
					double noiseLevel = noiseLevelChooser.getSelectedNoiseLevel();
					((RandelbrotFractalIterator) (fractalIterator)).setNoiseLevel(noiseLevel);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyGlynnSetPower)) {
			if (fractalIterator instanceof GlynnFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((GlynnFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((GlynnFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibrotSetPower)) {
			if (fractalIterator instanceof MultibrotFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultibrotFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultibrotFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibrotPolynomialSetPower)) {
			if (fractalIterator instanceof MultibrotPolynomialFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultibrotPolynomialFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultibrotPolynomialFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibrotParameterSetPower)) {
			if (fractalIterator instanceof MultibrotParameterFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultibrotParameterFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultibrotParameterFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibrotInvertedParameterSetPower)) {
			if (fractalIterator instanceof MultibrotInvertedParameterFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultibrotInvertedParameterFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultibrotInvertedParameterFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibarSetPower)) {
			if (fractalIterator instanceof MultibarFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultibarFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultibarFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibarPolynomialSetPower)) {
			if (fractalIterator instanceof MultibarPolynomialFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultibarPolynomialFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultibarPolynomialFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibarParameterSetPower)) {
			if (fractalIterator instanceof MultibarParameterFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultibarParameterFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultibarParameterFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultibarInvertedParameterSetPower)) {
			if (fractalIterator instanceof MultibarInvertedParameterFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultibarInvertedParameterFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultibarInvertedParameterFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyBurningMultiShipSetPower)) {
			if (fractalIterator instanceof BurningMultiShipFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((BurningMultiShipFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((BurningMultiShipFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultiProductExpelbrotSetPower)) {
			if (fractalIterator instanceof MultiProductExpelbrotFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultiProductExpelbrotFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultiProductExpelbrotFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultiSumExpelbrotSetPower)) {
			if (fractalIterator instanceof MultiSumExpelbrotFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultiSumExpelbrotFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultiSumExpelbrotFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultiProductExparbrotSetPower)) {
			if (fractalIterator instanceof MultiProductExparbrotFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultiProductExparbrotFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultiProductExparbrotFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMultiSumExparbrotSetPower)) {
			if (fractalIterator instanceof MultiSumExparbrotFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((MultiSumExparbrotFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((MultiSumExparbrotFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerSineSetPower)) {
			if (fractalIterator instanceof TrigonometricPowerSineFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((TrigonometricPowerSineFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((TrigonometricPowerSineFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerCosineSetPower)) {
			if (fractalIterator instanceof TrigonometricPowerCosineFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((TrigonometricPowerCosineFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((TrigonometricPowerCosineFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerTangentSetPower)) {
			if (fractalIterator instanceof TrigonometricPowerTangentFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((TrigonometricPowerTangentFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((TrigonometricPowerTangentFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerCotangentSetPower)) {
			if (fractalIterator instanceof TrigonometricPowerCotangentFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((TrigonometricPowerCotangentFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((TrigonometricPowerCotangentFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiSineSetPower)) {
			if (fractalIterator instanceof TrigonometricPowerMultiSineFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((TrigonometricPowerMultiSineFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((TrigonometricPowerMultiSineFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCosineSetPower)) {
			if (fractalIterator instanceof TrigonometricPowerMultiCosineFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((TrigonometricPowerMultiCosineFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((TrigonometricPowerMultiCosineFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiTangentSetPower)) {
			if (fractalIterator instanceof TrigonometricPowerMultiTangentFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((TrigonometricPowerMultiTangentFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((TrigonometricPowerMultiTangentFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyTrigonometricPowerMultiCotangentSetPower)) {
			if (fractalIterator instanceof TrigonometricPowerMultiCotangentFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((TrigonometricPowerMultiCotangentFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((TrigonometricPowerMultiCotangentFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonSetConvergenceParameters)) {
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
			if (fractalIterator instanceof AConvergentFractalIterator) {
				((AConvergentFractalIterator) fractalIterator).setAutomaticRootDetectionEnabled(fMenuItems.get(kActionCommandMenuItemFractalFamilyNewtonRaphsonAutomaticRootDetectionEnabled).isSelected());
				if (((AConvergentFractalIterator) fractalIterator).getAutomaticRootDetectionEnabled()) {
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonPowerSetPower)) {
			if (fractalIterator instanceof NewtonRaphsonPowerFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((NewtonRaphsonPowerFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((NewtonRaphsonPowerFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonPowerPolynomialSetPower)) {
			if (fractalIterator instanceof NewtonRaphsonPowerPolynomialFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((NewtonRaphsonPowerPolynomialFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((NewtonRaphsonPowerPolynomialFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSineSetPower)) {
			if (fractalIterator instanceof NewtonRaphsonTrigonometricPowerSineFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((NewtonRaphsonTrigonometricPowerSineFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((NewtonRaphsonTrigonometricPowerSineFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSineSetPower)) {
			if (fractalIterator instanceof NewtonRaphsonTrigonometricPowerMultiSineFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((NewtonRaphsonTrigonometricPowerMultiSineFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((NewtonRaphsonTrigonometricPowerMultiSineFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerSineOffsetSetPower)) {
			if (fractalIterator instanceof NewtonRaphsonTrigonometricPowerSineOffsetFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((NewtonRaphsonTrigonometricPowerSineOffsetFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((NewtonRaphsonTrigonometricPowerSineOffsetFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyNewtonRaphsonTrigonometricPowerMultiSineOffsetSetPower)) {
			if (fractalIterator instanceof NewtonRaphsonTrigonometricPowerMultiSineOffsetFractalIterator) {
				PowerChooser powerChooser = new PowerChooser(this,((NewtonRaphsonTrigonometricPowerMultiSineOffsetFractalIterator) fractalIterator).getPower());
				if (!powerChooser.isCancelled()) {
					ComplexNumber power = powerChooser.getSelectedPower();
					((NewtonRaphsonTrigonometricPowerMultiSineOffsetFractalIterator) (fractalIterator)).setPower(power);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalFamilyMagnetSetConvergenceParameters)) {
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
			if (fractalIterator instanceof MarkusLyapunovFractalIterator) {
				RootSequenceChooser rootSequenceChooser = new RootSequenceChooser(this,((MarkusLyapunovFractalIterator) fractalIterator).getRootSequence());
				if (!rootSequenceChooser.isCancelled()) {
					String rootSequence = rootSequenceChooser.getSelectedRootSequence();
					((MarkusLyapunovFractalIterator) (fractalIterator)).setRootSequence(rootSequence);
					fIteratorController.recalc();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalSetMaxNrOfIterations)) {
			MaxNrOfIterationsChooser maxNrOfIterationsChooser = new MaxNrOfIterationsChooser(this,fractalIterator.getMaxNrOfIterations());
			if (!maxNrOfIterationsChooser.isCancelled()) {
				int maxNrOfIterations = maxNrOfIterationsChooser.getSelectedMaxNrOfIterations();
				fFractalPanel.calibrateColorRange(maxNrOfIterations);
				fractalIterator.setMaxNrOfIterations(maxNrOfIterations);
				fIteratorController.recalc();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalAutoSelectMaxNrOfIterations)) {
			boolean autoSelectMaxNrOfIterations = fMenuItems.get(kActionCommandMenuItemFractalAutoSelectMaxNrOfIterations).isSelected();
			fMenuItems.get(kActionCommandMenuItemFractalSetMaxNrOfIterations).setEnabled(!autoSelectMaxNrOfIterations);
			fFractalPanel.setAutoSelectMaxNrOfIterations(autoSelectMaxNrOfIterations);
			if (autoSelectMaxNrOfIterations) {
				int maxNrOfIterations = fractalIterator.autoDetermineMaxNrOfIterations();
				fFractalPanel.calibrateColorRange(maxNrOfIterations);
				fractalIterator.setMaxNrOfIterations(maxNrOfIterations);
				fIteratorController.recalc();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalSetEscapeRadius)) {
			EscapeRadiusChooser escapeRadiusChooser = new EscapeRadiusChooser(this,fractalIterator.getEscapeRadius());
			if (!escapeRadiusChooser.isCancelled()) {
				fractalIterator.setEscapeRadius(escapeRadiusChooser.getSelectedEscapeRadius());
				fIteratorController.recalc();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalCopyCoordinates)) {
			Point p = fFractalPanel.getMousePosition();
			if (p != null) {		
				ComplexNumber c = fractalIterator.convertScreenLocationToComplexNumber(new ScreenLocation(p.x,p.y));
				StringSelection clipboardContents = new StringSelection(c.toString());
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(clipboardContents,null);
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemFractalRefreshScreen)) {
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorBone)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kBone);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorCopper)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kCopper);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorDiscontinuousBlueWhiteGreen)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kDiscontinuousBlueWhiteGreen);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorDiscontinuousDarkRedYellow)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kDiscontinuousDarkRedYellow);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorBlackAndWhite)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kBlackAndWhite);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorGrayScale)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kGrayScale);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorGrayScaleTrimmed)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kGrayScaleTrimmed);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorGreenRedDiverging)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kGreenRedDiverging);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorHot)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kHot);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorJet)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kJet);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorHueSaturationBrightness)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kHueSaturationBrightness);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorSeparatedRGB)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kSeparatedRGB);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorRed)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kRed);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorGreen)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kGreen);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorBlue)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kBlue);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorYellow)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kYellow);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorCyan)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kCyan);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorMagenta)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kMagenta);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUltraLightPastel)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kUltraLightPastel);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorLightPastel)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kLightPastel);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorDarkPastel)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kDarkPastel);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorGreens)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kGreens);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorBlues)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kBlues);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorYellowBrowns)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kYellowBrowns);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorVioletPurples)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kVioletPurples);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorDeepSpace)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kDeepSpace);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorCustom)) {
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kCustom);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorSetCustomColorMap)) {
			JCustomColorMapChooser exteriorCustomColorMapChooser = new JCustomColorMapChooser(this,kNrOfCustomColorMapColors,coloringParameters.fExteriorGradientColorMap.getAllCustomColorMapComponents());
			if (!exteriorCustomColorMapChooser.isCancelled()) {
				coloringParameters.fExteriorGradientColorMap.setAllCustomColorMapComponents(exteriorCustomColorMapChooser.getSelectedCustomColorMapComponents());
				coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kCustom);
				fMenuItems.get(kActionCommandMenuItemColorMapExteriorCustom).setSelected(true);
				fFractalPanel.recolor();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorRandom)) {
			coloringParameters.fExteriorGradientColorMap.setRandomColorMap(kNrOfCustomColorMapColors);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorConvertCurrentColorMapToCustomColorMap)) {
			if (!fMenuItems.get(kActionCommandMenuItemColorMapExteriorCustom).isSelected()) {
				if (JConfirmationDialog.confirm(this,I18NL10N.translate("text.ColorMap.OverwriteCustomColorMap"))) {
					coloringParameters.fExteriorGradientColorMap.setAllCustomColorMapComponents(coloringParameters.fExteriorGradientColorMap.convertToComponents(kNrOfCustomColorMapColors));
					coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kCustom);
					fMenuItems.get(kActionCommandMenuItemColorMapExteriorCustom).setSelected(true);
					actionPerformed(new ActionEvent(this,ActionEvent.ACTION_LAST+1,kActionCommandMenuItemColorMapExteriorSetCustomColorMap));
					fFractalPanel.recolor();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorInvertColorMap)) {
			coloringParameters.fExteriorColorMapInverted = fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).isSelected();
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap)) {
			coloringParameters.fExteriorColorMapWrappedAround = fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).isSelected();
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseTigerStripes)) {
			coloringParameters.fUseTigerStripes = fMenuItems.get(kActionCommandMenuItemColorMapUseTigerStripes).isSelected();
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerBone)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kBone);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerCopper)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kCopper);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerDiscontinuousBlueWhiteGreen)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kDiscontinuousBlueWhiteGreen);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerDiscontinuousDarkRedYellow)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kDiscontinuousDarkRedYellow);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerBlackAndWhite)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kBlackAndWhite);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerGrayScale)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kGrayScale);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerGrayScaleTrimmed)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kGrayScaleTrimmed);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerGreenRedDiverging)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kGreenRedDiverging);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerHot)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kHot);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerJet)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kJet);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerHueSaturationBrightness)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kHueSaturationBrightness);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerSeparatedRGB)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kSeparatedRGB);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerRed)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kRed);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerGreen)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kGreen);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerBlue)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kBlue);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerYellow)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kYellow);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerCyan)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kCyan);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerMagenta)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kMagenta);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerUltraLightPastel)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kUltraLightPastel);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerLightPastel)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kDarkPastel);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerDarkPastel)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kDarkPastel);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerGreens)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kGreens);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerBlues)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kBlues);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerYellowBrowns)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kYellowBrowns);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerVioletPurples)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kVioletPurples);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerDeepSpace)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kDeepSpace);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerRandom)) {
			coloringParameters.fTigerGradientColorMap.setRandomColorMap(kNrOfCustomColorMapColors);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerCustom)) {
			coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kCustom);
			coloringParameters.fTigerUseFixedColor = false;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerSetCustomColorMap)) {
			JCustomColorMapChooser tigerCustomColorMapChooser = new JCustomColorMapChooser(this,kNrOfCustomColorMapColors,coloringParameters.fTigerGradientColorMap.getAllCustomColorMapComponents());
			if (!tigerCustomColorMapChooser.isCancelled()) {
				coloringParameters.fTigerGradientColorMap.setAllCustomColorMapComponents(tigerCustomColorMapChooser.getSelectedCustomColorMapComponents());
				coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kCustom);
				coloringParameters.fTigerUseFixedColor = false;
				fMenuItems.get(kActionCommandMenuItemColorMapTigerCustom).setSelected(true);
				fFractalPanel.recolor();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerConvertCurrentColorMapToCustomColorMap)) {
			if (!fMenuItems.get(kActionCommandMenuItemColorMapTigerCustom).isSelected()) {
				if (JConfirmationDialog.confirm(this,I18NL10N.translate("text.ColorMap.OverwriteCustomColorMap"))) {
					coloringParameters.fTigerGradientColorMap.setAllCustomColorMapComponents(coloringParameters.fTigerGradientColorMap.convertToComponents(kNrOfCustomColorMapColors));
					coloringParameters.fTigerGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kCustom);
					coloringParameters.fTigerUseFixedColor = false;
					fMenuItems.get(kActionCommandMenuItemColorMapTigerCustom).setSelected(true);
					actionPerformed(new ActionEvent(this,ActionEvent.ACTION_LAST+1,kActionCommandMenuItemColorMapTigerSetCustomColorMap));
					fFractalPanel.recolor();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerUseFixedColor)) {
			coloringParameters.fTigerUseFixedColor = true;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapTigerSetFixedColor)) {
			try {
				Color tigerStripeFixedColor = JColorChooser.showDialog(this,I18NL10N.translate(kActionCommandMenuItemColorMapTigerSetFixedColor),coloringParameters.fTigerStripeFixedColor);
				if (tigerStripeFixedColor != null) {
					coloringParameters.fTigerStripeFixedColor = tigerStripeFixedColor;
					coloringParameters.fTigerUseFixedColor = true;
					fTigerStripeColorLabelDecorator.setColor(tigerStripeFixedColor);
					fFractalPanel.recolor();
				}
			}
			catch (HeadlessException exc) {
				// ignore
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorBone)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kBone);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorCopper)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kCopper);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorDiscontinuousBlueWhiteGreen)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kDiscontinuousBlueWhiteGreen);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorDiscontinuousDarkRedYellow)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kDiscontinuousDarkRedYellow);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorBlackAndWhite)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kBlackAndWhite);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorGrayScale)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kGrayScale);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorGrayScaleTrimmed)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kGrayScaleTrimmed);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorGreenRedDiverging)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kGreenRedDiverging);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorHot)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kHot);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorJet)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kJet);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorHueSaturationBrightness)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kHueSaturationBrightness);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorSeparatedRGB)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kSeparatedRGB);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorRed)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kRed);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorGreen)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kGreen);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorBlue)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kBlue);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorYellow)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kYellow);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorCyan)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kCyan);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorMagenta)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kMagenta);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUltraLightPastel)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kUltraLightPastel);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorLightPastel)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kLightPastel);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorDarkPastel)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kDarkPastel);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorGreens)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kGreens);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorBlues)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kBlues);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorYellowBrowns)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kYellowBrowns);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorVioletPurples)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kVioletPurples);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorDeepSpace)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kDeepSpace);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorRandom)) {
			coloringParameters.fInteriorGradientColorMap.setRandomColorMap(kNrOfCustomColorMapColors);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorCustom)) {
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kCustom);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorSetCustomColorMap)) {
			JCustomColorMapChooser interiorCustomColorMapChooser = new JCustomColorMapChooser(this,kNrOfCustomColorMapColors,coloringParameters.fInteriorGradientColorMap.getAllCustomColorMapComponents());
			if (!interiorCustomColorMapChooser.isCancelled()) {
				coloringParameters.fInteriorGradientColorMap.setAllCustomColorMapComponents(interiorCustomColorMapChooser.getSelectedCustomColorMapComponents());
				coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kCustom);
				fMenuItems.get(kActionCommandMenuItemColorMapInteriorCustom).setSelected(true);
				fFractalPanel.recolor();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorConvertCurrentColorMapToCustomColorMap)) {
			if (!fMenuItems.get(kActionCommandMenuItemColorMapInteriorCustom).isSelected()) {
				if (JConfirmationDialog.confirm(this,I18NL10N.translate("text.ColorMap.OverwriteCustomColorMap"))) {
					coloringParameters.fInteriorGradientColorMap.setAllCustomColorMapComponents(coloringParameters.fInteriorGradientColorMap.convertToComponents(kNrOfCustomColorMapColors));
					coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kCustom);
					fMenuItems.get(kActionCommandMenuItemColorMapInteriorCustom).setSelected(true);
					actionPerformed(new ActionEvent(this,ActionEvent.ACTION_LAST+1,kActionCommandMenuItemColorMapInteriorSetCustomColorMap));
					fFractalPanel.recolor();
				}
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorInvertColorMap)) {
			coloringParameters.fInteriorColorMapInverted = fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).isSelected();
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap)) {
			coloringParameters.fInteriorColorMapWrappedAround = fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).isSelected();
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseFixedColor)) {
			coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kFixedColor;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorSetFixedColor)) {
			try {
				Color interiorColor = JColorChooser.showDialog(this,I18NL10N.translate(kActionCommandMenuItemColorMapInteriorSetFixedColor),coloringParameters.fInteriorColor);
				if (interiorColor != null) {
					coloringParameters.fInteriorColor = interiorColor;
					fInteriorColorLabelDecorator.setColor(interiorColor);
					coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kFixedColor;
					fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseFixedColor).setSelected(true);
					fFractalPanel.recolor();
				}
			}
			catch (HeadlessException exc) {
				// ignore
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseExponentiallySmoothedLevelSets)) {
			coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kSmoothEICLevelSets;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseSectorDecomposition)) {
			coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kSectorDecomposition;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorSetDecompositionSectorRange)) {
			SectorDecompositionRangeChooser sectorDecompositionRangeChooser = new SectorDecompositionRangeChooser(this,coloringParameters.fColorMapInteriorSectorDecompositionRange);
			if (!sectorDecompositionRangeChooser.isCancelled()) {
				coloringParameters.fColorMapInteriorSectorDecompositionRange = sectorDecompositionRangeChooser.getSelectedSectorDecompositionRange();
				// automatically select the binary decomposition
				coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kSectorDecomposition;
				adjustMenusToFractal();
				fFractalPanel.recolor();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseRealComponent)) {
			coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kRealComponent;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseImaginaryComponent)) {
			coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kImaginaryComponent;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseModulus)) {
			coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kModulus;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseAngle)) {
			coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kAngle;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseAverageDistance)) {
			coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kAverageDistance;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseLyapunovExponent)) {
			coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kLyapunovExponent;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseCurvature)) {
			coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kCurvature;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseStriping)) {
			coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kStriping;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorSetStripingDensity)) {
			StripingDensityChooser stripingDensityChooser = new StripingDensityChooser(this,fractalIterator.getInteriorStripingDensity());
			if (!stripingDensityChooser.isCancelled()) {
				fractalIterator.setInteriorStripingDensity(stripingDensityChooser.getSelectedStripingDensity());
				fIteratorController.recalc();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseMinimumGaussianIntegersDistance)) {
			coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kMinimumGaussianIntegersDistance;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseAverageGaussianIntegersDistance)) {
			coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kAverageGaussianIntegersDistance;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorSetGaussianIntegersTrapFactor)) {
			GaussianIntegersTrapFactorChooser gaussianIntegersTrapFactorChooser = new GaussianIntegersTrapFactorChooser(this,fractalIterator.getInteriorGaussianIntegersTrapFactor());
			if (!gaussianIntegersTrapFactorChooser.isCancelled()) {
				fractalIterator.setInteriorGaussianIntegersTrapFactor(gaussianIntegersTrapFactorChooser.getSelectedGaussianIntegersTrapFactor());
				fIteratorController.recalc();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseExteriorDistance)) {
			coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kExteriorDistance;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseOrbitTrapDisk)) {
			coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kOrbitTrapDisk;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorSetOrbitTrapDiskCentre)) {
			OrbitTrapCentreChooser orbitTrapDiskCentreChooser = new OrbitTrapCentreChooser(this,fractalIterator.getInteriorOrbitTrapDiskCentre());
			if (!orbitTrapDiskCentreChooser.isCancelled()) {
				fractalIterator.setInteriorOrbitTrapDiskCentre(orbitTrapDiskCentreChooser.getSelectedCentre());
				fIteratorController.recalc();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorSetOrbitTrapDiskRadius)) {
			OrbitTrapRadiusChooser orbitTrapRadiusChooser = new OrbitTrapRadiusChooser(this,fractalIterator.getInteriorOrbitTrapDiskRadius());
			if (!orbitTrapRadiusChooser.isCancelled()) {
				fractalIterator.setInteriorOrbitTrapDiskRadius(orbitTrapRadiusChooser.getSelectedRadius());
				fIteratorController.recalc();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseOrbitTrapCrossStalks)) {
			coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kOrbitTrapCrossStalks;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorSetOrbitTrapCrossStalksCentre)) {
			OrbitTrapCentreChooser orbitTrapCrossStalksCentreChooser = new OrbitTrapCentreChooser(this,fractalIterator.getInteriorOrbitTrapCrossStalksCentre());
			if (!orbitTrapCrossStalksCentreChooser.isCancelled()) {
				fractalIterator.setInteriorOrbitTrapCrossStalksCentre(orbitTrapCrossStalksCentreChooser.getSelectedCentre());
				fIteratorController.recalc();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseOrbitTrapSine)) {
			coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kOrbitTrapSine;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorSetOrbitTrapSineParameters)) {
			OrbitTrapTrigonometricParametersChooser orbitTrapSineParametersChooser = new OrbitTrapTrigonometricParametersChooser(this,fractalIterator.getInteriorOrbitTrapSineMultiplicativeFactor(),fractalIterator.getInteriorOrbitTrapSineAdditiveFactor());
			if (!orbitTrapSineParametersChooser.isCancelled()) {
				fractalIterator.setInteriorOrbitTrapSineMultiplicativeFactor(orbitTrapSineParametersChooser.getSelectedMultiplicativeFactor());
				fractalIterator.setInteriorOrbitTrapSineAdditiveFactor(orbitTrapSineParametersChooser.getSelectedAdditiveFactor());
				fIteratorController.recalc();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorUseOrbitTrapTangens)) {
			coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kOrbitTrapTangens;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapInteriorSetOrbitTrapTangensParameters)) {
			OrbitTrapTrigonometricParametersChooser orbitTrapTangensParametersChooser = new OrbitTrapTrigonometricParametersChooser(this,fractalIterator.getInteriorOrbitTrapTangensMultiplicativeFactor(),fractalIterator.getInteriorOrbitTrapTangensAdditiveFactor());
			if (!orbitTrapTangensParametersChooser.isCancelled()) {
				fractalIterator.setInteriorOrbitTrapTangensMultiplicativeFactor(orbitTrapTangensParametersChooser.getSelectedMultiplicativeFactor());
				fractalIterator.setInteriorOrbitTrapTangensAdditiveFactor(orbitTrapTangensParametersChooser.getSelectedAdditiveFactor());
				fIteratorController.recalc();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseFixedColor)) {
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kFixedColor;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorSetFixedColor)) {
			try {
				Color exteriorColor = JColorChooser.showDialog(this,I18NL10N.translate(kActionCommandMenuItemColorMapExteriorSetFixedColor),coloringParameters.fExteriorColor);
				if (exteriorColor != null) {
					coloringParameters.fExteriorColor = exteriorColor;
					fExteriorColorLabelDecorator.setColor(exteriorColor);
					coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kFixedColor;
					fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseFixedColor).setSelected(true);
					fFractalPanel.recolor();
				}
			}
			catch (HeadlessException exc) {
				// ignore
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseDiscreteLevelSets)) {
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kDiscreteLevelSets;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseNormalisedLevelSets)) {
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kSmoothNICLevelSets;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseExponentiallySmoothedLevelSets)) {
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kSmoothEICLevelSets;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseSectorDecomposition)) {
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kSectorDecomposition;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorSetDecompositionSectorRange)) {
			SectorDecompositionRangeChooser sectorDecompositionRangeChooser = new SectorDecompositionRangeChooser(this,coloringParameters.fColorMapExteriorSectorDecompositionRange);
			if (!sectorDecompositionRangeChooser.isCancelled()) {
				coloringParameters.fColorMapExteriorSectorDecompositionRange = sectorDecompositionRangeChooser.getSelectedSectorDecompositionRange();
				// automatically select the binary decomposition
				coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kSectorDecomposition;
				adjustMenusToFractal();
				fFractalPanel.recolor();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseRealComponent)) {
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kRealComponent;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseImaginaryComponent)) {
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kImaginaryComponent;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseModulus)) {
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kModulus;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseAverageDistance)) {
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kAverageDistance;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseAngle)) {
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kAngle;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseLyapunovExponent)) {
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kLyapunovExponent;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapCalculateAdvancedColoring)) {
			boolean calculateAdvancedColoring = fMenuItems.get(kActionCommandMenuItemColorMapCalculateAdvancedColoring).isSelected();
			coloringParameters.fCalculateAdvancedColoring = calculateAdvancedColoring;
			fractalIterator.setCalculateAdvancedColoring(calculateAdvancedColoring);
			adjustMenusToFractal();
			if (calculateAdvancedColoring) {
				fIteratorController.recalc();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseCurvature)) {
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kCurvature;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseStriping)) {
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kStriping;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorSetStripingDensity)) {
			StripingDensityChooser stripingDensityChooser = new StripingDensityChooser(this,fractalIterator.getExteriorStripingDensity());
			if (!stripingDensityChooser.isCancelled()) {
				fractalIterator.setExteriorStripingDensity(stripingDensityChooser.getSelectedStripingDensity());
				fIteratorController.recalc();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseMinimumGaussianIntegersDistance)) {
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kMinimumGaussianIntegersDistance;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseAverageGaussianIntegersDistance)) {
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kAverageGaussianIntegersDistance;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorSetGaussianIntegersTrapFactor)) {
			GaussianIntegersTrapFactorChooser gaussianIntegersTrapFactorChooser = new GaussianIntegersTrapFactorChooser(this,fractalIterator.getExteriorGaussianIntegersTrapFactor());
			if (!gaussianIntegersTrapFactorChooser.isCancelled()) {
				fractalIterator.setExteriorGaussianIntegersTrapFactor(gaussianIntegersTrapFactorChooser.getSelectedGaussianIntegersTrapFactor());
				fIteratorController.recalc();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseExteriorDistance)) {
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kExteriorDistance;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseOrbitTrapDisk)) {
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kOrbitTrapDisk;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorSetOrbitTrapDiskCentre)) {
			OrbitTrapCentreChooser orbitTrapDiskCentreChooser = new OrbitTrapCentreChooser(this,fractalIterator.getExteriorOrbitTrapDiskCentre());
			if (!orbitTrapDiskCentreChooser.isCancelled()) {
				fractalIterator.setExteriorOrbitTrapDiskCentre(orbitTrapDiskCentreChooser.getSelectedCentre());
				fIteratorController.recalc();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorSetOrbitTrapDiskRadius)) {
			OrbitTrapRadiusChooser orbitTrapRadiusChooser = new OrbitTrapRadiusChooser(this,fractalIterator.getExteriorOrbitTrapDiskRadius());
			if (!orbitTrapRadiusChooser.isCancelled()) {
				fractalIterator.setExteriorOrbitTrapDiskRadius(orbitTrapRadiusChooser.getSelectedRadius());
				fIteratorController.recalc();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseOrbitTrapCrossStalks)) {
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kOrbitTrapCrossStalks;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorSetOrbitTrapCrossStalksCentre)) {
			OrbitTrapCentreChooser orbitTrapCrossStalksCentreChooser = new OrbitTrapCentreChooser(this,fractalIterator.getExteriorOrbitTrapCrossStalksCentre());
			if (!orbitTrapCrossStalksCentreChooser.isCancelled()) {
				fractalIterator.setExteriorOrbitTrapCrossStalksCentre(orbitTrapCrossStalksCentreChooser.getSelectedCentre());
				fIteratorController.recalc();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseOrbitTrapSine)) {
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kOrbitTrapSine;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorSetOrbitTrapSineParameters)) {
			OrbitTrapTrigonometricParametersChooser orbitTrapSineParametersChooser = new OrbitTrapTrigonometricParametersChooser(this,fractalIterator.getExteriorOrbitTrapSineMultiplicativeFactor(),fractalIterator.getExteriorOrbitTrapSineAdditiveFactor());
			if (!orbitTrapSineParametersChooser.isCancelled()) {
				fractalIterator.setExteriorOrbitTrapSineMultiplicativeFactor(orbitTrapSineParametersChooser.getSelectedMultiplicativeFactor());
				fractalIterator.setExteriorOrbitTrapSineAdditiveFactor(orbitTrapSineParametersChooser.getSelectedAdditiveFactor());
				fIteratorController.recalc();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseOrbitTrapTangens)) {
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kOrbitTrapTangens;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorSetOrbitTrapTangensParameters)) {
			OrbitTrapTrigonometricParametersChooser orbitTrapTangensParametersChooser = new OrbitTrapTrigonometricParametersChooser(this,fractalIterator.getExteriorOrbitTrapTangensMultiplicativeFactor(),fractalIterator.getExteriorOrbitTrapTangensAdditiveFactor());
			if (!orbitTrapTangensParametersChooser.isCancelled()) {
				fractalIterator.setExteriorOrbitTrapTangensMultiplicativeFactor(orbitTrapTangensParametersChooser.getSelectedMultiplicativeFactor());
				fractalIterator.setExteriorOrbitTrapTangensAdditiveFactor(orbitTrapTangensParametersChooser.getSelectedAdditiveFactor());
				fIteratorController.recalc();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseDiscreteRoots)) {
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kDiscreteRoots;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorUseSmoothRoots)) {
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kSmoothRoots;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapExteriorSetBrightnessFactor)) {
			BrightnessFactorChooser brightnessFactorChooser = new BrightnessFactorChooser(this,coloringParameters.fBrightnessFactor);
			if (!brightnessFactorChooser.isCancelled()) {
				coloringParameters.fBrightnessFactor = brightnessFactorChooser.getSelectedBrightnessFactor();
				fFractalPanel.recolor();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseBinaryDecomposition)) {
			// setup exterior colouring
			coloringParameters.fExteriorColorMapInverted = false;
			coloringParameters.fExteriorColorMapWrappedAround = false;
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kBlackAndWhite);
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kSectorDecomposition;
			coloringParameters.fColorMapExteriorSectorDecompositionRange = 2;
			fToolBarToggles.get(kActionCommandMenuItemColorMapUseRankOrderScalingToggle).setSelected(false);

			// disable tiger striping
			coloringParameters.fUseTigerStripes = false;

			// setup interior colouring
			coloringParameters.fInteriorColorMapInverted = false;
			coloringParameters.fInteriorColorMapWrappedAround = false;
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kBlackAndWhite);
			coloringParameters.fColorMapInteriorSectorDecompositionRange = 2;
			coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kFixedColor;
			coloringParameters.fInteriorColor = Color.BLACK;
			fInteriorColorLabelDecorator.setColor(Color.BLACK);

			// setup scaling
			coloringParameters.fColorMapScaling = ColoringParameters.EColorMapScaling.kLinear;
			coloringParameters.fColorMapScalingFunctionMultiplier = 1.0;
			coloringParameters.fColorMapScalingArgumentMultiplier = 1.0;

			// setup iteration range
			coloringParameters.fLowIterationRange = 0;
			coloringParameters.fHighIterationRange = fractalIterator.getMaxNrOfIterations();

			// disable colour repetition and offset
			coloringParameters.fColorMapRepeatMode = false;
			coloringParameters.fColorMapColorOffset = 0.0;

			// disable colour cycling
			fColorCyclingTimer.stop();
			fMenuItems.get(kActionCommandMenuItemColorMapCycleColors).setSelected(false);

			// setup colour map usage
			coloringParameters.fColorMapUsage = ColoringParameters.EColorMapUsage.kFull;

			// disable post-processing filters
			coloringParameters.fUsePostProcessingFilters = false;

			adjustMenusToFractal();
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseContours)) {
			// reset post-processing filter chain to a single edge-detection filter
			coloringParameters.fPostProcessingFilterChain = new FilterChain();
			coloringParameters.fPostProcessingFilterChain.addFilter(new EdgeFilter());
			coloringParameters.fUsePostProcessingFilters = true;
			adjustMenusToFractal();
			fFractalPanel.finaliseFractalImage();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseDarkSofteningFilter)) {
			// reset post-processing filter chain to a dark softening filter cascade
			coloringParameters.fPostProcessingFilterChain = new FilterChain();
			coloringParameters.fPostProcessingFilterChain.addFilter(new SharpenFilter());
			coloringParameters.fPostProcessingFilterChain.addFilter(new EdgeFilter());
			coloringParameters.fPostProcessingFilterChain.addFilter(new BlurFilter());
			coloringParameters.fPostProcessingFilterChain.addFilter(new PosteriseFilter());
			coloringParameters.fUsePostProcessingFilters = true;
			adjustMenusToFractal();
			fFractalPanel.finaliseFractalImage();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapResetToDefault)) {
			// setup exterior colouring
			coloringParameters.fExteriorColorMapInverted = false;
			coloringParameters.fExteriorColorMapWrappedAround = false;
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kJet);
			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kSmoothNICLevelSets;
			fToolBarToggles.get(kActionCommandMenuItemColorMapUseRankOrderScalingToggle).setSelected(false);

			// disable tiger striping
			coloringParameters.fUseTigerStripes = false;

			// setup interior colouring
			coloringParameters.fInteriorColorMapInverted = false;
			coloringParameters.fInteriorColorMapWrappedAround = false;
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kJet);
			coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kFixedColor;
			coloringParameters.fInteriorColor = Color.BLACK;
			fInteriorColorLabelDecorator.setColor(Color.BLACK);

			// setup scaling
			coloringParameters.fColorMapScaling = ColoringParameters.EColorMapScaling.kLinear;
			coloringParameters.fColorMapScalingFunctionMultiplier = 1.0;
			coloringParameters.fColorMapScalingArgumentMultiplier = 1.0;

			// setup iteration range
			coloringParameters.fLowIterationRange = 0;
			coloringParameters.fHighIterationRange = fractalIterator.getMaxNrOfIterations();

			// disable colour repetition and offset
			coloringParameters.fColorMapRepeatMode = false;
			coloringParameters.fColorMapColorOffset = 0.0;

			// disable colour cycling
			fColorCyclingTimer.stop();
			fMenuItems.get(kActionCommandMenuItemColorMapCycleColors).setSelected(false);

			// setup colour map usage
			coloringParameters.fColorMapUsage = ColoringParameters.EColorMapUsage.kFull;
			
			// disable post-processing filters
			coloringParameters.fUsePostProcessingFilters = false;

			adjustMenusToFractal();
			setupMarkusLyapunovFractal();
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseLinearScaling)) {
			coloringParameters.fColorMapScaling = ColoringParameters.EColorMapScaling.kLinear;
			fMenuItems.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColors).setEnabled(false);
			fToolBarToggles.get(kActionCommandMenuItemColorMapUseRankOrderScalingToggle).setSelected(false);
			fToolBarToggles.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColorsToggle).setEnabled(false);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseLogarithmicScaling)) {
			coloringParameters.fColorMapScaling = ColoringParameters.EColorMapScaling.kLogarithmic;
			fMenuItems.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColors).setEnabled(false);
			fToolBarToggles.get(kActionCommandMenuItemColorMapUseRankOrderScalingToggle).setSelected(false);
			fToolBarToggles.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColorsToggle).setEnabled(false);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseExponentialScaling)) {
			coloringParameters.fColorMapScaling = ColoringParameters.EColorMapScaling.kExponential;
			fMenuItems.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColors).setEnabled(false);
			fToolBarToggles.get(kActionCommandMenuItemColorMapUseRankOrderScalingToggle).setSelected(false);
			fToolBarToggles.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColorsToggle).setEnabled(false);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseSqrtScaling)) {
			coloringParameters.fColorMapScaling = ColoringParameters.EColorMapScaling.kSqrt;
			fMenuItems.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColors).setEnabled(false);
			fToolBarToggles.get(kActionCommandMenuItemColorMapUseRankOrderScalingToggle).setSelected(false);
			fToolBarToggles.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColorsToggle).setEnabled(false);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapSetScalingParameters)) {
			ColorMapScalingParametersChooser colorMapScalingParametersChooser = new ColorMapScalingParametersChooser(this,coloringParameters.fColorMapScalingFunctionMultiplier,coloringParameters.fColorMapScalingArgumentMultiplier);
			if (!colorMapScalingParametersChooser.isCancelled()) {
				coloringParameters.fColorMapScalingFunctionMultiplier = colorMapScalingParametersChooser.getSelectedFunctionMultiplier();
				coloringParameters.fColorMapScalingArgumentMultiplier = colorMapScalingParametersChooser.getSelectedArgumentMultiplier();
				fFractalPanel.recolor();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseRankOrderScaling)) {
			coloringParameters.fColorMapScaling = ColoringParameters.EColorMapScaling.kRankOrder;
			fMenuItems.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColors).setEnabled(true);
			fToolBarToggles.get(kActionCommandMenuItemColorMapUseRankOrderScalingToggle).setSelected(true);
			fToolBarToggles.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColorsToggle).setEnabled(true);
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseRankOrderScalingToggle)) {
			if (fToolBarToggles.get(kActionCommandMenuItemColorMapUseRankOrderScalingToggle).isSelected()) {
				coloringParameters.fColorMapScaling = ColoringParameters.EColorMapScaling.kRankOrder;
				fMenuItems.get(kActionCommandMenuItemColorMapUseRankOrderScaling).setSelected(true);
				fMenuItems.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColors).setEnabled(true);
				fToolBarToggles.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColorsToggle).setEnabled(true);
			}
			else {
				coloringParameters.fColorMapScaling = ColoringParameters.EColorMapScaling.kLinear;
				fMenuItems.get(kActionCommandMenuItemColorMapUseLinearScaling).setSelected(true);
				fMenuItems.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColors).setEnabled(false);
				fToolBarToggles.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColorsToggle).setEnabled(false);
			}
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapRestrictHighIterationCountColors)) {
			coloringParameters.fRankOrderRestrictHighIterationCountColors = fMenuItems.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColors).isSelected();
			fToolBarToggles.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColorsToggle).setSelected(fMenuItems.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColors).isSelected());
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapRestrictHighIterationCountColorsToggle)) {
			coloringParameters.fRankOrderRestrictHighIterationCountColors = fToolBarToggles.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColorsToggle).isSelected();
			fMenuItems.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColors).setSelected(fToolBarToggles.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColorsToggle).isSelected());
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapSetIterationRange)) {
			IterationRangeChooser iterationRangeChooser = new IterationRangeChooser(this,coloringParameters.fLowIterationRange,coloringParameters.fHighIterationRange,fractalIterator.getMaxNrOfIterations());
			if (!iterationRangeChooser.isCancelled()) {
				coloringParameters.fLowIterationRange = iterationRangeChooser.getSelectedLowIterationRange();
				coloringParameters.fHighIterationRange = iterationRangeChooser.getSelectedHighIterationRange();
				fFractalPanel.recolor();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapRepeatColors)) {
			coloringParameters.fColorMapRepeatMode = fMenuItems.get(kActionCommandMenuItemColorMapRepeatColors).isSelected();
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapSetColorRepetition)) {
			ColorMapRepetitionChooser colorMapRepetitionChooser = new ColorMapRepetitionChooser(this,coloringParameters.fColorMapColorRepetition);
			if (!colorMapRepetitionChooser.isCancelled()) {
				coloringParameters.fColorMapColorRepetition = colorMapRepetitionChooser.getSelectedColorRepetition();
				coloringParameters.fColorMapRepeatMode = true;
				fMenuItems.get(kActionCommandMenuItemColorMapRepeatColors).setSelected(true);
				fFractalPanel.recolor();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapSetColorOffset)) {
			ColorMapOffsetChooser colorMapOffsetChooser = new ColorMapOffsetChooser(this,coloringParameters.fColorMapColorOffset);
			if (!colorMapOffsetChooser.isCancelled()) {
				coloringParameters.fColorMapColorOffset = colorMapOffsetChooser.getSelectedColorOffset();
				fFractalPanel.finaliseFractalImage();
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
			coloringParameters.fColorMapUsage = ColoringParameters.EColorMapUsage.kFull;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseLimitedContinuousColorRange)) {
			coloringParameters.fColorMapUsage = ColoringParameters.EColorMapUsage.kLimitedContinuous;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapSetLimitedContinuousColorRange)) {
			ColorMapContinuousRangeChooser colorMapContinuousRangeChooser = new ColorMapContinuousRangeChooser(this,coloringParameters.fColorMapContinuousColorRange);
			if (!colorMapContinuousRangeChooser.isCancelled()) {
				coloringParameters.fColorMapContinuousColorRange = colorMapContinuousRangeChooser.getSelectedRange();
				coloringParameters.fColorMapUsage = ColoringParameters.EColorMapUsage.kLimitedContinuous;
				fMenuItems.get(kActionCommandMenuItemColorMapUseLimitedContinuousColorRange).setSelected(true);
				fFractalPanel.recolor();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUseLimitedDiscreteColorRange)) {
			coloringParameters.fColorMapUsage = ColoringParameters.EColorMapUsage.kLimitedDiscrete;
			fFractalPanel.recolor();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapSetLimitedDiscreteColorRange)) {
			ColorMapDiscreteRangeChooser colorMapDiscreteRangeChooser = new ColorMapDiscreteRangeChooser(this,coloringParameters.fColorMapDiscreteColorRange,fractalIterator.getMaxNrOfIterations());
			if (!colorMapDiscreteRangeChooser.isCancelled()) {
				coloringParameters.fColorMapDiscreteColorRange = colorMapDiscreteRangeChooser.getSelectedRange();
				coloringParameters.fColorMapUsage = ColoringParameters.EColorMapUsage.kLimitedDiscrete;
				fMenuItems.get(kActionCommandMenuItemColorMapUseLimitedDiscreteColorRange).setSelected(true);
				fFractalPanel.recolor();
			}
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorMapUsePostProcessingFilters)) {
			coloringParameters.fUsePostProcessingFilters = fMenuItems.get(kActionCommandMenuItemColorMapUsePostProcessingFilters).isSelected();
			fFractalPanel.finaliseFractalImage();
		}
		else if (command.equalsIgnoreCase(kActionCommandMenuItemColorSetupPostProcessingFilters)) {
			FilterSetupChooser filterSetupChooser = new FilterSetupChooser(this,fFractalPanel,fIteratorController);
			if (!filterSetupChooser.isCancelled()) {
				coloringParameters.fPostProcessingFilterChain = filterSetupChooser.getSelectedFilterChain();
				coloringParameters.fUsePostProcessingFilters = true;
				adjustMenusToFractal();
			}
			fFractalPanel.finaliseFractalImage();
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
		int clickCount = e.getClickCount();
		int modifiers = e.getModifiersEx();
		boolean shiftKeyPressed = ((modifiers & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK);
		boolean consumed =  e.isConsumed();
		e.consume();

		if (fFractalPanel.getZoomThumbnailSelectionMode()) {
			if ((e.getButton() == MouseEvent.BUTTON1) && (clickCount == 1) && (!consumed)) {
				// select zoom level image to zoom to
				int selectedZoomLevel = fFractalPanel.getSelectedZoomLevel();
				if (selectedZoomLevel > 0) {
					fFractalPanel.zoomToLevel(selectedZoomLevel);
					fFractalPanel.setZoomThumbnailSelectionMode(false);
					showMenusAndToolBar();
					fFractalPanel.repaint();
				}
			}
			else if ((e.getButton() == MouseEvent.BUTTON3) && (clickCount == 1) && (!consumed)) {
				// toggle showing and selecting the zoom thumbnails on a single right click
				fFractalPanel.setZoomThumbnailSelectionMode(false);
				showMenusAndToolBar();
				fFractalPanel.repaint();
			}
		}
		else {
			if ((e.getButton() == MouseEvent.BUTTON1) && (clickCount == 2) && (!consumed)) {
				if (fDoubleClickMode == EDoubleClickMode.kSwitchMainDualFractal) {
					// switch main and inset fractal types on a double click
					switchMainDualFractal();
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
			else if ((e.getButton() == MouseEvent.BUTTON3) && (clickCount == 2) && (!consumed)) {
				// zoom out on a double right click
				fFractalPanel.zoomOut();
				fFractalPanel.setZoomThumbnailSelectionMode(false);
				showMenusAndToolBar();
				fFractalPanel.repaint();
			}
			else if ((e.getButton() == MouseEvent.BUTTON3) && (clickCount == 1) && (!consumed) && shiftKeyPressed) {
				// toggle showing and selecting the zoom thumbnails on a single right click
				fFractalPanel.setZoomThumbnailSelectionMode(true);
				hideMenusAndToolBar();
				fFractalPanel.repaint();
			}
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
		changeLocationMouseCursor();
	}

	/**
	 */
	@Override
	public void mousePressed(MouseEvent e)
	{
		if (!fFractalPanel.getZoomThumbnailSelectionMode()) {
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
	}

	/**
	 */
	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (!fFractalPanel.getZoomThumbnailSelectionMode()) {
			changeLocationMouseCursor();
			if (fFractalPanel.getSelecting()) {
				fFractalPanel.zoomToSelection();
				updateStatusBar();
			}
		}
	}

	// the mouse-motion-listener
	/**
	 */
	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (!fFractalPanel.getZoomThumbnailSelectionMode()) {
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
		fLastSelectedFractal = kActionCommandMenuItemFractalFamilyDefaultMandelbrotJulia;

		fNavigationPanningSize = 0.25;

		fDoubleClickMode = EDoubleClickMode.kSwitchMainDualFractal;

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
				fIteratorController.getColoringParameters().fColorMapColorOffset = MathTools.frac(fIteratorController.getColoringParameters().fColorMapColorOffset + (sign * fColorCyclingSmoothness));;
				fFractalPanel.finaliseFractalImage();
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
		catch (HelpSetException exc) {
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
		// retain a reference to the embedded contentpane
		fContentPane = contentPane;

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
			fFractalScrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE); // enable smooth scrolling when updating the various panels
			fFractalPanel.setViewport(fFractalScrollPane.getViewport());
		contentPane.add(fFractalScrollPane,BorderLayout.CENTER);

		contentPane.add(getToolBar(),BorderLayout.NORTH);
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
				menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,0));
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
				menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,0));
				menuItem.addActionListener(this);
			menu.add(menuItem);
				menuItem = constructMenuItem(kActionCommandMenuItemFileSaveFractalParameters,false);
				menuItem.setActionCommand(kActionCommandMenuItemFileSaveFractalParameters);
				menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,0));
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

/*
			menu.addSeparator();

				menuItem = constructMenuItem(kActionCommandMenuItemFileSaveZoomAnimationSequence,false);
				menuItem.setActionCommand(kActionCommandMenuItemFileSaveZoomAnimationSequence);
				menuItem.addActionListener(this);
			menu.add(menuItem);
*/
			menu.addSeparator();

				menuItem = constructMenuItem(kActionCommandMenuItemFilePrintFractal,false);
				menuItem.setActionCommand(kActionCommandMenuItemFilePrintFractal);
				menuItem.addActionListener(this);
			menu.add(menuItem);
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
				menuItem = constructMenuItem(kActionCommandMenuItemNavigationMenuPanDown,false);
				menuItem.setActionCommand(kActionCommandMenuItemNavigationMenuPanDown);
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
				checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,0));
				checkBoxMenuItem.addActionListener(this);
				fMenuItems.put(kActionCommandMenuItemNavigationCentredZooming,checkBoxMenuItem);
			menu.add(checkBoxMenuItem);
				menuItem = constructMenuItem(kActionCommandMenuItemNavigationResetZoom,false);
				menuItem.setActionCommand(kActionCommandMenuItemNavigationResetZoom);
				menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,ActionEvent.CTRL_MASK));
				menuItem.addActionListener(this);
			menu.add(menuItem);
				menuItem = constructMenuItem(kActionCommandMenuItemNavigationZoomToLevelCoordinates,false);
				menuItem.setActionCommand(kActionCommandMenuItemNavigationZoomToLevelCoordinates);
				menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK));
				menuItem.addActionListener(this);
			menu.add(menuItem);
				menuItem = constructMenuItem(kActionCommandMenuItemNavigationZoomToLevelGraphical,false);
				menuItem.setActionCommand(kActionCommandMenuItemNavigationZoomToLevelGraphical);
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
				checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemNavigationShowOverlayGrid,false);
				checkBoxMenuItem.setSelected(false);
				checkBoxMenuItem.setActionCommand(kActionCommandMenuItemNavigationShowOverlayGrid);
				checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,0));
				checkBoxMenuItem.addActionListener(this);
				fMenuItems.put(kActionCommandMenuItemNavigationShowOverlayGrid,checkBoxMenuItem);
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
				checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemNavigationShowMainFractalOverview,false);
				checkBoxMenuItem.setSelected(false);
				checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,ActionEvent.CTRL_MASK));
				checkBoxMenuItem.setActionCommand(kActionCommandMenuItemNavigationShowMainFractalOverview);
				checkBoxMenuItem.addActionListener(this);
				fMenuItems.put(kActionCommandMenuItemNavigationShowMainFractalOverview,checkBoxMenuItem);
			menu.add(checkBoxMenuItem);

			menu.addSeparator();

				menuItem = constructMenuItem(kActionCommandMenuItemNavigationSpecifyScreenBounds,false);
				menuItem.setActionCommand(kActionCommandMenuItemNavigationSpecifyScreenBounds);
				menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,0));
				menuItem.addActionListener(this);
			menu.add(menuItem);
				menuItem = constructMenuItem(kActionCommandMenuItemNavigationSpecifyComplexBounds,false);
				menuItem.setActionCommand(kActionCommandMenuItemNavigationSpecifyComplexBounds);
				menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,0));
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
				checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemFractalAutoSuppressDualFractal,false);
				checkBoxMenuItem.setSelected(true);
				checkBoxMenuItem.setActionCommand(kActionCommandMenuItemFractalAutoSuppressDualFractal);
				checkBoxMenuItem.addActionListener(this);
				fMenuItems.put(kActionCommandMenuItemFractalAutoSuppressDualFractal,checkBoxMenuItem);
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

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyDucksSecans,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyDucksSecans);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyDucksSecans,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);

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

					subSubMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyQuadbrot,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyQuadbrot);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyQuadbrot,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);

					subSubMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyTetration,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyTetration);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyTetration,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyTetrationDual,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyTetrationDual);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyTetrationDual,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);

				subSubMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyIOfMedusa,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyIOfMedusa);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyIOfMedusa,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyIOfTheStorm,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyIOfTheStorm);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyIOfTheStorm,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyAtTheCShore,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyAtTheCShore);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyAtTheCShore,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);

					subSubMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyLogarithmicJulia,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyLogarithmicJulia);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyLogarithmicJulia,radioButtonMenuItem);
					subSubMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemFractalFamilyHyperbolicSineJulia,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemFractalFamilyHyperbolicSineJulia);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemFractalFamilyHyperbolicSineJulia,radioButtonMenuItem);
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
				menuItem.setEnabled(true);
				fMenuItems.put(kActionCommandMenuItemFractalSetMaxNrOfIterations,menuItem);
			menu.add(menuItem);
				checkBoxMenuItem = constructCheckBoxMenuItem(kMenuItemIndentation + kActionCommandMenuItemFractalAutoSelectMaxNrOfIterations,false);
				checkBoxMenuItem.setSelected(false);
				checkBoxMenuItem.setActionCommand(kActionCommandMenuItemFractalAutoSelectMaxNrOfIterations);
				checkBoxMenuItem.addActionListener(this);
				fMenuItems.put(kActionCommandMenuItemFractalAutoSelectMaxNrOfIterations,checkBoxMenuItem);
			menu.add(checkBoxMenuItem);
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
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorGrayScaleTrimmed,false);
						radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-grayscale-trimmed.png")));
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorGrayScaleTrimmed);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorGrayScaleTrimmed,radioButtonMenuItem);
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

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorRandom,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorRandom);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorRandom,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
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
					checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,0));
					checkBoxMenuItem.addActionListener(this);
					fMenuItems.put(kActionCommandMenuItemColorMapExteriorInvertColorMap,checkBoxMenuItem);
				menu.add(checkBoxMenuItem);
					checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap,false);
					checkBoxMenuItem.setSelected(false);
					checkBoxMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap);
					checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,0));
					checkBoxMenuItem.addActionListener(this);
					fMenuItems.put(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap,checkBoxMenuItem);
				menu.add(checkBoxMenuItem);
					checkBoxMenuItem = constructCheckBoxMenuItem(kActionCommandMenuItemColorMapCalculateAdvancedColoring,false);
					checkBoxMenuItem.setSelected(false);
					checkBoxMenuItem.setActionCommand(kActionCommandMenuItemColorMapCalculateAdvancedColoring);
					checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,0));
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
							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerGrayScaleTrimmed,false);
							radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-grayscale-trimmed.png")));
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerGrayScaleTrimmed);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerGrayScaleTrimmed,radioButtonMenuItem);
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

							radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapTigerRandom,false);
							radioButtonMenuItem.setSelected(false);
							radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapTigerRandom);
							radioButtonMenuItem.addActionListener(this);
							buttonGroup.add(radioButtonMenuItem);
							fMenuItems.put(kActionCommandMenuItemColorMapTigerRandom,radioButtonMenuItem);
						subMenu.add(radioButtonMenuItem);
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
						buttonGroup = new ButtonGroup();
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseFixedColor,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseFixedColor);
						radioButtonMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,ActionEvent.CTRL_MASK));
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
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
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseDiscreteLevelSets,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseNormalisedLevelSets,false);
						radioButtonMenuItem.setSelected(true);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseNormalisedLevelSets);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseNormalisedLevelSets,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseExponentiallySmoothedLevelSets,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseExponentiallySmoothedLevelSets);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseExponentiallySmoothedLevelSets,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseSectorDecomposition,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseSectorDecomposition);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
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
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseRealComponent,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseImaginaryComponent,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseImaginaryComponent);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseImaginaryComponent,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseModulus,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseModulus);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseModulus,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseAverageDistance,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseAverageDistance);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseAverageDistance,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseAngle,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseAngle);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseAngle,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseLyapunovExponent,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseLyapunovExponent);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseLyapunovExponent,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);

					subMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseCurvature,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseCurvature);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseCurvature,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseStriping,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseStriping);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseStriping,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapExteriorSetStripingDensity,false);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorSetStripingDensity);
						menuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorSetStripingDensity,menuItem);
					subMenu.add(menuItem);

					subMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseMinimumGaussianIntegersDistance,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseMinimumGaussianIntegersDistance);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseMinimumGaussianIntegersDistance,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseAverageGaussianIntegersDistance,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseAverageGaussianIntegersDistance);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseAverageGaussianIntegersDistance,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapExteriorSetGaussianIntegersTrapFactor,false);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorSetGaussianIntegersTrapFactor);
						menuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorSetGaussianIntegersTrapFactor,menuItem);
					subMenu.add(menuItem);

					subMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseExteriorDistance,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseExteriorDistance);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseExteriorDistance,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);

					subMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseOrbitTrapDisk,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseOrbitTrapDisk);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseOrbitTrapDisk,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapExteriorSetOrbitTrapDiskCentre,false);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorSetOrbitTrapDiskCentre);
						menuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorSetOrbitTrapDiskCentre,menuItem);
					subMenu.add(menuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapExteriorSetOrbitTrapDiskRadius,false);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorSetOrbitTrapDiskRadius);
						menuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorSetOrbitTrapDiskRadius,menuItem);
					subMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseOrbitTrapCrossStalks,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseOrbitTrapCrossStalks);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseOrbitTrapCrossStalks,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapExteriorSetOrbitTrapCrossStalksCentre,false);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorSetOrbitTrapCrossStalksCentre);
						menuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorSetOrbitTrapCrossStalksCentre,menuItem);
					subMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseOrbitTrapSine,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseOrbitTrapSine);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseOrbitTrapSine,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapExteriorSetOrbitTrapSineParameters,false);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorSetOrbitTrapSineParameters);
						menuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorSetOrbitTrapSineParameters,menuItem);
					subMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseOrbitTrapTangens,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseOrbitTrapTangens);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseOrbitTrapTangens,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapExteriorSetOrbitTrapTangensParameters,false);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorSetOrbitTrapTangensParameters);
						menuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorSetOrbitTrapTangensParameters,menuItem);
					subMenu.add(menuItem);

					subMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseDiscreteRoots,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseDiscreteRoots);
						radioButtonMenuItem.addActionListener(this);
						radioButtonMenuItem.setEnabled(false);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapExteriorUseDiscreteRoots,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapExteriorUseSmoothRoots,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapExteriorUseSmoothRoots);
						radioButtonMenuItem.addActionListener(this);
						radioButtonMenuItem.setEnabled(false);
						buttonGroup.add(radioButtonMenuItem);
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
									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorGrayScaleTrimmed,false);
									radioButtonMenuItem.setIcon(new ImageIcon(fResources.getImage("application-resources/images/gradient-color-map-grayscale-trimmed.png")));
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorGrayScaleTrimmed);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorGrayScaleTrimmed,radioButtonMenuItem);
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

									radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorRandom,false);
									radioButtonMenuItem.setSelected(false);
									radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorRandom);
									radioButtonMenuItem.addActionListener(this);
									buttonGroup.add(radioButtonMenuItem);
									fMenuItems.put(kActionCommandMenuItemColorMapInteriorRandom,radioButtonMenuItem);
								subSubMenu.add(radioButtonMenuItem);
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
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseAverageDistance,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseAverageDistance);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseAverageDistance,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseAngle,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseAngle);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseAngle,radioButtonMenuItem);
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

					subMenu.addSeparator();

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

					subMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseExteriorDistance,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseExteriorDistance);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseExteriorDistance,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);

					subMenu.addSeparator();

						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseOrbitTrapDisk,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseOrbitTrapDisk);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseOrbitTrapDisk,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapInteriorSetOrbitTrapDiskCentre,false);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorSetOrbitTrapDiskCentre);
						menuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorSetOrbitTrapDiskCentre,menuItem);
					subMenu.add(menuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapInteriorSetOrbitTrapDiskRadius,false);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorSetOrbitTrapDiskRadius);
						menuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorSetOrbitTrapDiskRadius,menuItem);
					subMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseOrbitTrapCrossStalks,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseOrbitTrapCrossStalks);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseOrbitTrapCrossStalks,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapInteriorSetOrbitTrapCrossStalksCentre,false);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorSetOrbitTrapCrossStalksCentre);
						menuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorSetOrbitTrapCrossStalksCentre,menuItem);
					subMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseOrbitTrapSine,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseOrbitTrapSine);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseOrbitTrapSine,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapInteriorSetOrbitTrapSineParameters,false);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorSetOrbitTrapSineParameters);
						menuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorSetOrbitTrapSineParameters,menuItem);
					subMenu.add(menuItem);
						radioButtonMenuItem = constructRadioButtonMenuItem(kActionCommandMenuItemColorMapInteriorUseOrbitTrapTangens,false);
						radioButtonMenuItem.setSelected(false);
						radioButtonMenuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorUseOrbitTrapTangens);
						radioButtonMenuItem.addActionListener(this);
						buttonGroup.add(radioButtonMenuItem);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorUseOrbitTrapTangens,radioButtonMenuItem);
					subMenu.add(radioButtonMenuItem);
						menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorMapInteriorSetOrbitTrapTangensParameters,false);
						menuItem.setActionCommand(kActionCommandMenuItemColorMapInteriorSetOrbitTrapTangensParameters);
						menuItem.addActionListener(this);
						fMenuItems.put(kActionCommandMenuItemColorMapInteriorSetOrbitTrapTangensParameters,menuItem);
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
					menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,0));
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
					checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,0));
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
					checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,0));
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
					checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,0));
					checkBoxMenuItem.addActionListener(this);
					fMenuItems.put(kActionCommandMenuItemColorMapUsePostProcessingFilters,checkBoxMenuItem);
				menu.add(checkBoxMenuItem);
					menuItem = constructMenuItem(kMenuItemIndentation + kActionCommandMenuItemColorSetupPostProcessingFilters,false);
					menuItem.setActionCommand(kActionCommandMenuItemColorSetupPostProcessingFilters);
					menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,0));
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
				menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,0));
				menuItem.addActionListener(this);
				// disable in case only single threading is possible
				if (SystemInformation.getNrOfProcessors() == 1) {
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

		// update registry
		systemRegistry.addObject("fLastOpenedFolder",fLastOpenedFolder);
		systemRegistry.addObject("fStoredScreenSizes",fStoredScreenSizes);
	}

	/*******************
	 * PRIVATE METHODS *
	 *******************/

	/**
	 * Installs a specified key binding.
	 *
	 * @param inputMap       -
	 * @param actionMap      -
	 */
	private void installKeyBindings(InputMap inputMap, ActionMap actionMap)
	{
		installKeyBinding(inputMap,actionMap,"LEFT",kActionCommandMenuItemNavigationKeyPanLeft);
		installKeyBinding(inputMap,actionMap,"RIGHT",kActionCommandMenuItemNavigationKeyPanRight);
		installKeyBinding(inputMap,actionMap,"UP",kActionCommandMenuItemNavigationKeyPanUp);
		installKeyBinding(inputMap,actionMap,"DOWN",kActionCommandMenuItemNavigationKeyPanDown);
		installKeyBinding(inputMap,actionMap,"ESCAPE",kActionCommandMenuItemMultithreadingInterrupt);
	}

	/**
	 * Installs a specified key binding.
	 *
	 * @param inputMap       -
	 * @param actionMap      -
	 * @param keyStrokeName  -
	 * @param bindingAction  -
	 */
	private void installKeyBinding(InputMap inputMap, ActionMap actionMap, String keyStrokeName, String bindingAction)
	{
		NavigationAction navigationAction = new NavigationAction(bindingAction);
		KeyStroke keyStroke = KeyStroke.getKeyStroke(keyStrokeName);
		inputMap.put(keyStroke,keyStrokeName);
		actionMap.put(keyStrokeName,navigationAction);
	}

	/**
	 * Loads the registry.
	 */
	@SuppressWarnings("unchecked")
	private void loadRegistry()
	{
		// obtain a local reference to the system registry
		Registry systemRegistry = Registry.getInstance();

		// load last opened folder
		Object lastOpenedFolderEntry = systemRegistry.getObject("fLastOpenedFolder");
		if (lastOpenedFolderEntry != null) {
			fLastOpenedFolder = (String) lastOpenedFolderEntry;
		}
		else {
			// setup and store default as the current folder
			fLastOpenedFolder = ".";
			systemRegistry.addObject("fLastOpenedFolder",fLastOpenedFolder);
		}

		// load stored screen sizes
		Object storedScreenSizesEntry = systemRegistry.getObject("fStoredScreenSizes");
		if (storedScreenSizesEntry != null) {
			fStoredScreenSizes = (ArrayList<StoredScreenSize>) storedScreenSizesEntry;
		}
		else {
			// setup and store default as the stored screen sizes
			fStoredScreenSizes = new ArrayList<StoredScreenSize>();
			for (int i = 0; i < ScreenBoundsChooser.kMaxNrOfStoredScreenSizes; ++i) {
				fStoredScreenSizes.add(new StoredScreenSize(false,"",800,600));
			}
			systemRegistry.addObject("fStoredScreenSizes",fStoredScreenSizes);
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

		defaultFilename += "_iter=" + String.valueOf(fractalIterator.getMaxNrOfIterations());

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
	 * @return -
	 */
	private JToolBar getToolBar()
	{
		JButton button = null;
		JToggleButton toggleButton = null;
		final int kSeparatorSpacing = 5;
		fToolBarToggles = new HashMap<String,AbstractButton>();
			fToolBar = new JToolBar(I18NL10N.translate("toolBar.QuickAccess.Title"));
			try {
					button = new JButton (new ImageIcon(fResources.getImage("application-resources/icons/load-parameters-icon.png")));
					button.setToolTipText(I18NL10N.translate(kActionCommandMenuItemFileLoadFractalParameters));
					button.setActionCommand(kActionCommandMenuItemFileLoadFractalParameters);
					button.addActionListener(this);
				fToolBar.add(button);
					button = new JButton (new ImageIcon(fResources.getImage("application-resources/icons/save-parameters-icon.png")));
					button.setToolTipText(I18NL10N.translate(kActionCommandMenuItemFileSaveFractalParameters));
					button.setActionCommand(kActionCommandMenuItemFileSaveFractalParameters);
					button.addActionListener(this);
				fToolBar.add(button);
					button = new JButton (new ImageIcon(fResources.getImage("application-resources/icons/export-to-png-icon.png")));
					button.setToolTipText(I18NL10N.translate(kActionCommandMenuItemFileExportToPNG));
					button.setActionCommand(kActionCommandMenuItemFileExportToPNG);
					button.addActionListener(this);
				fToolBar.add(button);

				fToolBar.add(Box.createRigidArea(new Dimension(kSeparatorSpacing,0)));
				fToolBar.addSeparator();
				fToolBar.add(Box.createRigidArea(new Dimension(kSeparatorSpacing,0)));

					button = new JButton (new ImageIcon(fResources.getImage("application-resources/icons/print-icon.png")));
					button.setToolTipText(I18NL10N.translate(kActionCommandMenuItemFilePrintFractal));
					button.setActionCommand(kActionCommandMenuItemFilePrintFractal);
					button.addActionListener(this);
				fToolBar.add(button);

				fToolBar.add(Box.createRigidArea(new Dimension(kSeparatorSpacing,0)));
				fToolBar.addSeparator();
				fToolBar.add(Box.createRigidArea(new Dimension(kSeparatorSpacing,0)));

					toggleButton = new JToggleButton(new ImageIcon(fResources.getImage("application-resources/icons/toggle-zoom-information-icon.png")));
					toggleButton.setToolTipText(I18NL10N.translate(kActionCommandMenuItemNavigationShowZoomInformation));
					toggleButton.setSelected(true);
					toggleButton.setActionCommand(kActionCommandMenuItemNavigationShowZoomInformationToggle);
					toggleButton.addActionListener(this);
					fToolBarToggles.put(kActionCommandMenuItemNavigationShowZoomInformationToggle,toggleButton);
				fToolBar.add(toggleButton);
					toggleButton = new JToggleButton(new ImageIcon(fResources.getImage("application-resources/icons/toggle-axes-icon.png")));
					toggleButton.setToolTipText(I18NL10N.translate(kActionCommandMenuItemNavigationShowAxes));
					toggleButton.setActionCommand(kActionCommandMenuItemNavigationShowAxesToggle);
					toggleButton.addActionListener(this);
					fToolBarToggles.put(kActionCommandMenuItemNavigationShowAxesToggle,toggleButton);
				fToolBar.add(toggleButton);
					toggleButton = new JToggleButton(new ImageIcon(fResources.getImage("application-resources/icons/toggle-overlay-grid-icon.png")));
					toggleButton.setToolTipText(I18NL10N.translate(kActionCommandMenuItemNavigationShowOverlayGrid));
					toggleButton.setActionCommand(kActionCommandMenuItemNavigationShowOverlayGridToggle);
					toggleButton.addActionListener(this);
					fToolBarToggles.put(kActionCommandMenuItemNavigationShowOverlayGridToggle,toggleButton);
				fToolBar.add(toggleButton);
					toggleButton = new JToggleButton(new ImageIcon(fResources.getImage("application-resources/icons/toggle-current-location-icon.png")));
					toggleButton.setToolTipText(I18NL10N.translate(kActionCommandMenuItemNavigationShowCurrentLocation));
					toggleButton.setSelected(true);
					toggleButton.setActionCommand(kActionCommandMenuItemNavigationShowCurrentLocationToggle);
					toggleButton.addActionListener(this);
					fToolBarToggles.put(kActionCommandMenuItemNavigationShowCurrentLocationToggle,toggleButton);
				fToolBar.add(toggleButton);
					toggleButton = new JToggleButton(new ImageIcon(fResources.getImage("application-resources/icons/toggle-magnifying-glass-icon.png")));
					toggleButton.setToolTipText(I18NL10N.translate(kActionCommandMenuItemNavigationShowMagnifyingGlass));
					toggleButton.setActionCommand(kActionCommandMenuItemNavigationShowMagnifyingGlassToggle);
					toggleButton.addActionListener(this);
					fToolBarToggles.put(kActionCommandMenuItemNavigationShowMagnifyingGlassToggle,toggleButton);
				fToolBar.add(toggleButton);

				fToolBar.add(Box.createRigidArea(new Dimension(kSeparatorSpacing,0)));
				fToolBar.addSeparator();
				fToolBar.add(Box.createRigidArea(new Dimension(kSeparatorSpacing,0)));

					button = new JButton (new ImageIcon(fResources.getImage("application-resources/icons/specify-screen-bounds-icon.png")));
					button.setToolTipText(I18NL10N.translate(kActionCommandMenuItemNavigationSpecifyScreenBounds));
					button.setActionCommand(kActionCommandMenuItemNavigationSpecifyScreenBounds);
					button.addActionListener(this);
				fToolBar.add(button);
					button = new JButton (new ImageIcon(fResources.getImage("application-resources/icons/specify-complex-bounds-icon.png")));
					button.setToolTipText(I18NL10N.translate(kActionCommandMenuItemNavigationSpecifyComplexBounds));
					button.setActionCommand(kActionCommandMenuItemNavigationSpecifyComplexBounds);
					button.addActionListener(this);
				fToolBar.add(button);

				fToolBar.add(Box.createRigidArea(new Dimension(kSeparatorSpacing,0)));
				fToolBar.addSeparator();
				fToolBar.add(Box.createRigidArea(new Dimension(kSeparatorSpacing,0)));

					toggleButton = new JToggleButton(new ImageIcon(fResources.getImage("application-resources/icons/toggle-inset-icon.png")));
					toggleButton.setToolTipText(I18NL10N.translate(kActionCommandMenuItemFractalShowInset));
					toggleButton.setSelected(true);
					toggleButton.setActionCommand(kActionCommandMenuItemFractalShowInsetToggle);
					toggleButton.addActionListener(this);
					fToolBarToggles.put(kActionCommandMenuItemFractalShowInsetToggle,toggleButton);
				fToolBar.add(toggleButton);
					toggleButton = new JToggleButton(new ImageIcon(fResources.getImage("application-resources/icons/toggle-orbits-icon.png")));
					toggleButton.setToolTipText(I18NL10N.translate(kActionCommandMenuItemFractalShowOrbits));
					toggleButton.setActionCommand(kActionCommandMenuItemFractalShowOrbitsToggle);
					toggleButton.addActionListener(this);
					fToolBarToggles.put(kActionCommandMenuItemFractalShowOrbitsToggle,toggleButton);
				fToolBar.add(toggleButton);
					toggleButton = new JToggleButton(new ImageIcon(fResources.getImage("application-resources/icons/toggle-orbits-scaled-icon.png")));
					toggleButton.setToolTipText(I18NL10N.translate(kActionCommandMenuItemFractalScaleOrbitsToScreen));
					toggleButton.setActionCommand(kActionCommandMenuItemFractalScaleOrbitsToScreenToggle);
					toggleButton.addActionListener(this);
					fToolBarToggles.put(kActionCommandMenuItemFractalScaleOrbitsToScreenToggle,toggleButton);
				fToolBar.add(toggleButton);
					toggleButton = new JToggleButton(new ImageIcon(fResources.getImage("application-resources/icons/toggle-orbit-analyses-icon.png")));
					toggleButton.setToolTipText(I18NL10N.translate(kActionCommandMenuItemFractalShowOrbitAnalyses));
					toggleButton.setActionCommand(kActionCommandMenuItemFractalShowOrbitAnalysesToggle);
					toggleButton.addActionListener(this);
					fToolBarToggles.put(kActionCommandMenuItemFractalShowOrbitAnalysesToggle,toggleButton);
				fToolBar.add(toggleButton);

				fToolBar.add(Box.createRigidArea(new Dimension(kSeparatorSpacing,0)));
				fToolBar.addSeparator();
				fToolBar.add(Box.createRigidArea(new Dimension(kSeparatorSpacing,0)));

					button = new JButton (new ImageIcon(fResources.getImage("application-resources/icons/specify-number-of-iterations-icon.png")));
					button.setToolTipText(I18NL10N.translate(kActionCommandMenuItemFractalSetMaxNrOfIterations));
					button.setActionCommand(kActionCommandMenuItemFractalSetMaxNrOfIterations);
					button.addActionListener(this);
				fToolBar.add(button);
					button = new JButton (new ImageIcon(fResources.getImage("application-resources/icons/specify-escape-radius-icon.png")));
					button.setToolTipText(I18NL10N.translate(kActionCommandMenuItemFractalSetEscapeRadius));
					button.setActionCommand(kActionCommandMenuItemFractalSetEscapeRadius);
					button.addActionListener(this);
				fToolBar.add(button);

				fToolBar.add(Box.createRigidArea(new Dimension(kSeparatorSpacing,0)));
				fToolBar.addSeparator();
				fToolBar.add(Box.createRigidArea(new Dimension(kSeparatorSpacing,0)));

					button = new JButton (new ImageIcon(fResources.getImage("application-resources/icons/use-binary-decomposition-icon.png")));
					button.setToolTipText(I18NL10N.translate(kActionCommandMenuItemColorMapUseBinaryDecomposition));
					button.setActionCommand(kActionCommandMenuItemColorMapUseBinaryDecomposition);
					button.addActionListener(this);
				fToolBar.add(button);
					button = new JButton (new ImageIcon(fResources.getImage("application-resources/icons/reset-to-default-icon.png")));
					button.setToolTipText(I18NL10N.translate(kActionCommandMenuItemColorMapResetToDefault));
					button.setActionCommand(kActionCommandMenuItemColorMapResetToDefault);
					button.addActionListener(this);
				fToolBar.add(button);
					toggleButton = new JToggleButton(new ImageIcon(fResources.getImage("application-resources/icons/toggle-rank-order-scaling-icon.png")));
					toggleButton.setToolTipText(I18NL10N.translate(kActionCommandMenuItemColorMapUseRankOrderScaling));
					toggleButton.setActionCommand(kActionCommandMenuItemColorMapUseRankOrderScalingToggle);
					toggleButton.addActionListener(this);
					fToolBarToggles.put(kActionCommandMenuItemColorMapUseRankOrderScalingToggle,toggleButton);
				fToolBar.add(toggleButton);
					toggleButton = new JToggleButton(new ImageIcon(fResources.getImage("application-resources/icons/toggle-restrict-high-iteration-counts-icon.png")));
					toggleButton.setToolTipText(I18NL10N.translate(kActionCommandMenuItemColorMapRestrictHighIterationCountColors));
					toggleButton.setActionCommand(kActionCommandMenuItemColorMapRestrictHighIterationCountColorsToggle);
					toggleButton.addActionListener(this);
					toggleButton.setSelected(true);
					toggleButton.setEnabled(false);
					fToolBarToggles.put(kActionCommandMenuItemColorMapRestrictHighIterationCountColorsToggle,toggleButton);
				fToolBar.add(toggleButton);
					button = new JButton (new ImageIcon(fResources.getImage("application-resources/icons/set-random-exterior-colormap-icon.png")));
					button.setToolTipText(I18NL10N.translate(kActionCommandMenuItemColorMapExteriorRandom));
					button.setActionCommand(kActionCommandMenuItemColorMapExteriorRandom);
					button.addActionListener(this);
				fToolBar.add(button);
					button = new JButton (new ImageIcon(fResources.getImage("application-resources/icons/setup-post-processing-filters-icon.png")));
					button.setToolTipText(I18NL10N.translate(kActionCommandMenuItemColorSetupPostProcessingFilters));
					button.setActionCommand(kActionCommandMenuItemColorSetupPostProcessingFilters);
					button.addActionListener(this);
				fToolBar.add(button);

				fToolBar.add(Box.createRigidArea(new Dimension(kSeparatorSpacing,0)));
				fToolBar.addSeparator();
				fToolBar.add(Box.createRigidArea(new Dimension(kSeparatorSpacing,0)));
				
					button = new JButton (new ImageIcon(fResources.getImage("application-resources/icons/help-general-information-icon.png")));
					button.setToolTipText(I18NL10N.translate(kActionCommandMenuItemHelpGeneralInformation));
					button.setActionCommand(kActionCommandMenuItemHelpGeneralInformation);
					button.addActionListener(this);
				fToolBar.add(button);
			}
			catch (FileDoesNotExistException exc) {
				// ignore
			}
			fToolBar.setFloatable(true);
			fToolBar.setRollover(true);

		return fToolBar;
	}

	/**
	 */
	private void adjustMenusToFractal()
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
		else if (familyName.equalsIgnoreCase((new DucksSecansFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyDucksSecans;
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
		else if (familyName.equalsIgnoreCase((new QuadbrotFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyQuadbrot;
		}
		else if (familyName.equalsIgnoreCase((new TetrationFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyTetration;
		}
		else if (familyName.equalsIgnoreCase((new TetrationDualFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyTetrationDual;
		}
		else if (familyName.equalsIgnoreCase((new IOfMedusaFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyIOfMedusa;
		}
		else if (familyName.equalsIgnoreCase((new IOfTheStormFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyIOfTheStorm;
		}
		else if (familyName.equalsIgnoreCase((new AtTheCShoreFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyAtTheCShore;
		}
		else if (familyName.equalsIgnoreCase((new LogarithmicJuliaFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyLogarithmicJulia;
		}
		else if (familyName.equalsIgnoreCase((new HyperbolicSineJuliaFractalIterator()).getFamilyName())) {
			familyMenuItem = kActionCommandMenuItemFractalFamilyHyperbolicSineJulia;
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
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseAverageDistance).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseAngle).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseCurvature).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseStriping).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorSetStripingDensity).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseMinimumGaussianIntegersDistance).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseAverageGaussianIntegersDistance).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorSetGaussianIntegersTrapFactor).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseExteriorDistance).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseOrbitTrapDisk).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorSetOrbitTrapDiskCentre).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorSetOrbitTrapDiskRadius).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseOrbitTrapCrossStalks).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorSetOrbitTrapCrossStalksCentre).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseOrbitTrapSine).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorSetOrbitTrapSineParameters).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseOrbitTrapTangens).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapInteriorSetOrbitTrapTangensParameters).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseDiscreteLevelSets).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseNormalisedLevelSets).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseExponentiallySmoothedLevelSets).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseSectorDecomposition).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorSetDecompositionSectorRange).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseRealComponent).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseImaginaryComponent).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseModulus).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseAverageDistance).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseAngle).setEnabled(!isMarkusLyapunovFractalIterator);
		fMenuItems.get(kActionCommandMenuItemColorMapCalculateAdvancedColoring).setSelected(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseCurvature).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseStriping).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorSetStripingDensity).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseMinimumGaussianIntegersDistance).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseAverageGaussianIntegersDistance).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorSetGaussianIntegersTrapFactor).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseExteriorDistance).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseOrbitTrapDisk).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorSetOrbitTrapDiskCentre).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorSetOrbitTrapDiskRadius).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseOrbitTrapCrossStalks).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorSetOrbitTrapCrossStalksCentre).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseOrbitTrapSine).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorSetOrbitTrapSineParameters).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseOrbitTrapTangens).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
		fMenuItems.get(kActionCommandMenuItemColorMapExteriorSetOrbitTrapTangensParameters).setEnabled(!isMarkusLyapunovFractalIterator && calculateAdvancedColoring);
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

		// if necessary, disable auto select the maximum number of iterations for fractals that use a fixed number of iterations
		if (fractalIterator.getUseFixedNrOfIterations()) {
			fMenuItems.get(kActionCommandMenuItemFractalAutoSelectMaxNrOfIterations).setSelected(false);
			fFractalPanel.setAutoSelectMaxNrOfIterations(false);
			fMenuItems.get(kActionCommandMenuItemFractalAutoSelectMaxNrOfIterations).setEnabled(false);
			fMenuItems.get(kActionCommandMenuItemFractalSetMaxNrOfIterations).setEnabled(true);
		}
		else {
			fMenuItems.get(kActionCommandMenuItemFractalAutoSelectMaxNrOfIterations).setEnabled(true);
		}

		// setup the remaining menu items related to the colouring parameters
		ColoringParameters coloringParameters = fIteratorController.getColoringParameters();

		JGradientColorMap.EColorMap interiorColorMap = coloringParameters.fInteriorGradientColorMap.getColorMap();
		switch (interiorColorMap) {
			case kBone: fMenuItems.get(kActionCommandMenuItemColorMapInteriorBone).setSelected(true); break;
			case kCopper: fMenuItems.get(kActionCommandMenuItemColorMapInteriorCopper).setSelected(true); break;
			case kDiscontinuousBlueWhiteGreen: fMenuItems.get(kActionCommandMenuItemColorMapInteriorDiscontinuousBlueWhiteGreen).setSelected(true); break;
			case kDiscontinuousDarkRedYellow: fMenuItems.get(kActionCommandMenuItemColorMapInteriorDiscontinuousDarkRedYellow).setSelected(true); break;
			case kBlackAndWhite: fMenuItems.get(kActionCommandMenuItemColorMapInteriorBlackAndWhite).setSelected(true); break;
			case kGrayScale: fMenuItems.get(kActionCommandMenuItemColorMapInteriorGrayScale).setSelected(true); break;
			case kGrayScaleTrimmed: fMenuItems.get(kActionCommandMenuItemColorMapInteriorGrayScaleTrimmed).setSelected(true); break;
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
			case kRandom: fMenuItems.get(kActionCommandMenuItemColorMapInteriorRandom).setSelected(true); break;
			case kCustom: fMenuItems.get(kActionCommandMenuItemColorMapInteriorCustom).setSelected(true); break;
			default: break;
		}

		JGradientColorMap.EColorMap exteriorColorMap = coloringParameters.fExteriorGradientColorMap.getColorMap();
		switch (exteriorColorMap) {
			case kBone: fMenuItems.get(kActionCommandMenuItemColorMapExteriorBone).setSelected(true); break;
			case kCopper: fMenuItems.get(kActionCommandMenuItemColorMapExteriorCopper).setSelected(true); break;
			case kDiscontinuousBlueWhiteGreen: fMenuItems.get(kActionCommandMenuItemColorMapExteriorDiscontinuousBlueWhiteGreen).setSelected(true); break;
			case kDiscontinuousDarkRedYellow: fMenuItems.get(kActionCommandMenuItemColorMapExteriorDiscontinuousDarkRedYellow).setSelected(true); break;
			case kBlackAndWhite: fMenuItems.get(kActionCommandMenuItemColorMapExteriorBlackAndWhite).setSelected(true); break;
			case kGrayScale: fMenuItems.get(kActionCommandMenuItemColorMapExteriorGrayScale).setSelected(true); break;
			case kGrayScaleTrimmed: fMenuItems.get(kActionCommandMenuItemColorMapExteriorGrayScaleTrimmed).setSelected(true); break;
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
			case kRandom: fMenuItems.get(kActionCommandMenuItemColorMapExteriorRandom).setSelected(true); break;
			case kCustom: fMenuItems.get(kActionCommandMenuItemColorMapExteriorCustom).setSelected(true); break;
			default: break;
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
			case kGrayScaleTrimmed: fMenuItems.get(kActionCommandMenuItemColorMapTigerGrayScaleTrimmed).setSelected(true); break;
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
			case kRandom: fMenuItems.get(kActionCommandMenuItemColorMapTigerRandom).setSelected(true); break;
			case kCustom: fMenuItems.get(kActionCommandMenuItemColorMapTigerCustom).setSelected(true); break;
			default: break;
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
			case kAverageDistance: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseAverageDistance).setSelected(true); break;
			case kAngle: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseAngle).setSelected(true); break;
			case kLyapunovExponent: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseLyapunovExponent).setSelected(true); break;
			case kCurvature: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseCurvature).setSelected(true); break;
			case kStriping: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseStriping).setSelected(true); break;
			case kMinimumGaussianIntegersDistance: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseMinimumGaussianIntegersDistance).setSelected(true); break;
			case kAverageGaussianIntegersDistance: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseAverageGaussianIntegersDistance).setSelected(true); break;
			case kExteriorDistance: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseExteriorDistance).setSelected(true); break;
			case kOrbitTrapDisk: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseOrbitTrapDisk).setSelected(true); break;
			case kOrbitTrapCrossStalks: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseOrbitTrapCrossStalks).setSelected(true); break;
			case kOrbitTrapSine: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseOrbitTrapSine).setSelected(true); break;
			case kOrbitTrapTangens: fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseOrbitTrapTangens).setSelected(true); break;
			case kDiscreteRoots: break; // not applicable
			case kSmoothRoots: break; // not applicable
			default: break;
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
			case kAverageDistance: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseAverageDistance).setSelected(true); break;
			case kAngle: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseAngle).setSelected(true); break;
			case kLyapunovExponent: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseLyapunovExponent).setSelected(true); break;
			case kCurvature: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseCurvature).setSelected(true); break;
			case kStriping: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseStriping).setSelected(true); break;
			case kMinimumGaussianIntegersDistance: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseMinimumGaussianIntegersDistance).setSelected(true); break;
			case kAverageGaussianIntegersDistance: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseAverageGaussianIntegersDistance).setSelected(true); break;
			case kExteriorDistance: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseExteriorDistance).setSelected(true); break;
			case kOrbitTrapDisk: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseOrbitTrapDisk).setSelected(true); break;
			case kOrbitTrapCrossStalks: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseOrbitTrapCrossStalks).setSelected(true); break;
			case kOrbitTrapSine: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseOrbitTrapSine).setSelected(true); break;
			case kOrbitTrapTangens: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseOrbitTrapTangens).setSelected(true); break;
			case kDiscreteRoots: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseDiscreteRoots).setSelected(true); break;
			case kSmoothRoots: fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseSmoothRoots).setSelected(true); break;
			default: break;
		}
		
		ColoringParameters.EColorMapScaling colorMapScaling = coloringParameters.fColorMapScaling;
		switch (colorMapScaling) {
			case kLinear: fMenuItems.get(kActionCommandMenuItemColorMapUseLinearScaling).setSelected(true); break;
			case kLogarithmic: fMenuItems.get(kActionCommandMenuItemColorMapUseLogarithmicScaling).setSelected(true); break;
			case kExponential: fMenuItems.get(kActionCommandMenuItemColorMapUseExponentialScaling).setSelected(true); break;
			case kSqrt: fMenuItems.get(kActionCommandMenuItemColorMapUseSqrtScaling).setSelected(true); break;
			case kRankOrder: fMenuItems.get(kActionCommandMenuItemColorMapUseRankOrderScaling).setSelected(true); break;
			default: break;
		}

		fMenuItems.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColors).setSelected(coloringParameters.fRankOrderRestrictHighIterationCountColors);
		fMenuItems.get(kActionCommandMenuItemColorMapRepeatColors).setSelected(coloringParameters.fColorMapRepeatMode);

		ColoringParameters.EColorMapUsage colorMapUsage = coloringParameters.fColorMapUsage;
		switch (colorMapUsage) {
			case kFull: fMenuItems.get(kActionCommandMenuItemColorMapFullColorRange).setSelected(true); break;
			case kLimitedContinuous: fMenuItems.get(kActionCommandMenuItemColorMapUseLimitedContinuousColorRange).setSelected(true); break;
			case kLimitedDiscrete: fMenuItems.get(kActionCommandMenuItemColorMapUseLimitedDiscreteColorRange).setSelected(true); break;
		}

		fMenuItems.get(kActionCommandMenuItemColorMapRestrictHighIterationCountColors).setEnabled(coloringParameters.fColorMapScaling == ColoringParameters.EColorMapScaling.kRankOrder);

		fMenuItems.get(kActionCommandMenuItemColorMapUsePostProcessingFilters).setSelected(coloringParameters.fUsePostProcessingFilters);

		fMenuItems.get(kActionCommandMenuItemNavigationLockAspectRatio).setSelected(coloringParameters.fLockAspectRatio);
	}

	/**
	 */
	private void hideMenusAndToolBar()
	{
		getJMenuBar().setVisible(false);
		fToolBar.setVisible(false);
	}

	/**
	 */
	private void showMenusAndToolBar()
	{
		getJMenuBar().setVisible(true);
		fToolBar.setVisible(true);
	}

	/**
	 */
	private void setupMarkusLyapunovFractal()
	{
		AFractalIterator fractalIterator = fIteratorController.getFractalIterator();

		if (fractalIterator instanceof MarkusLyapunovFractalIterator) {
			ColoringParameters coloringParameters = fIteratorController.getColoringParameters();

			coloringParameters.fExteriorColoringMethod = ColoringParameters.EColoringMethod.kLyapunovExponent;
			fMenuItems.get(kActionCommandMenuItemColorMapExteriorUseLyapunovExponent).setSelected(true);
			coloringParameters.fInteriorColoringMethod = ColoringParameters.EColoringMethod.kLyapunovExponent;
			fMenuItems.get(kActionCommandMenuItemColorMapInteriorUseLyapunovExponent).setSelected(true);

			coloringParameters.fExteriorColorMapInverted = false;
			coloringParameters.fExteriorColorMapWrappedAround = false;
			coloringParameters.fExteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kCopper);
			fMenuItems.get(kActionCommandMenuItemColorMapExteriorCopper).setSelected(true);
			fMenuItems.get(kActionCommandMenuItemColorMapExteriorInvertColorMap).setSelected(false);
			fMenuItems.get(kActionCommandMenuItemColorMapExteriorWrapAroundColorMap).setSelected(false);

			coloringParameters.fInteriorColorMapInverted = false;
			coloringParameters.fInteriorColorMapWrappedAround = false;
			coloringParameters.fInteriorGradientColorMap.setColorMap(JGradientColorMap.EColorMap.kBlue);
			fMenuItems.get(kActionCommandMenuItemColorMapInteriorBlue).setSelected(true);
			fMenuItems.get(kActionCommandMenuItemColorMapInteriorInvertColorMap).setSelected(false);
			fMenuItems.get(kActionCommandMenuItemColorMapInteriorWrapAroundColorMap).setSelected(false);

			coloringParameters.fColorMapScaling = ColoringParameters.EColorMapScaling.kRankOrder;
			fMenuItems.get(kActionCommandMenuItemColorMapUseRankOrderScaling).setSelected(true);
		}
	}

	/**
	 */
	private void switchMainDualFractal()
	{
		AFractalIterator.EFractalType fractalType = fIteratorController.getFractalIterator().getFractalType();

		if (fractalType == AFractalIterator.EFractalType.kMainFractal) {
			// obtain the dual parameter
			Point p = fFractalPanel.getMousePosition();
			if (p != null) {		
				ComplexNumber c = fIteratorController.getFractalIterator().convertScreenLocationToComplexNumber(new ScreenLocation(p.x,p.y));
				fIteratorController.getFractalIterator().setDualParameter(c);
				fIteratorController.getFractalIterator().setFractalType(AFractalIterator.EFractalType.kDualFractal);
				fIteratorController.recalc();
			}
		}
		else if (fractalType == AFractalIterator.EFractalType.kDualFractal) {
			fIteratorController.getFractalIterator().setFractalType(AFractalIterator.EFractalType.kMainFractal);
			fIteratorController.recalc();
		}
	}

	/**
	 */
	private void changeLocationMouseCursor()
	{
		if (!fFractalPanel.getZoomThumbnailSelectionMode() && fMenuItems.get(kActionCommandMenuItemNavigationShowCurrentLocation).isSelected() && fFractalPanel.isMouseInsideComplexPlane()) {
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
		catch (InvalidHelpSetContextException exc) {
			kLogger.error(I18NL10N.translate("error.HelpInformationNotFound"));
		}
	}

	/*****************
	 * INNER CLASSES *
	 *****************/

	/**
	 * @author  Sven Maerivoet
	 * @version 20/04/2016
	 */
	private class NavigationAction extends AbstractAction
	{
		// internal datastructures
		private String fBindingAction;

		/****************
		 * CONSTRUCTORS *
		 ****************/
		
		/**
		 * Constructs a <CODE>NavigationAction</CODE> object.
		 *
		 * @param bindingAction  the coded name for the binding action
		 */
		public NavigationAction(String bindingAction)
		{
			fBindingAction = bindingAction;
		}

		/******************
		 * PUBLIC METHODS *
		 ******************/

		// the action-listener
		/**
		 * @param e  -
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (fBindingAction.equalsIgnoreCase(kActionCommandMenuItemNavigationKeyPanLeft)) {
				if (fFractalPanel.getShowMainFractalOverview()) {
					Point vp = fFractalScrollPane.getViewport().getViewPosition();
					vp.x -= kScrollbarBlockIncrement;
					if (vp.x < 0) {
						vp.x = 0;
					}
					fFractalScrollPane.getViewport().setViewPosition(vp);
				}
				else {
					fFractalPanel.pan(FractalPanel.EPanDirection.kLeft,fNavigationPanningSize,fMenuItems.get(kActionCommandMenuItemNavigationInvertPanningDirections).isSelected());
				}
			}
			else if (fBindingAction.equalsIgnoreCase(kActionCommandMenuItemNavigationKeyPanRight)) {
				if (fFractalPanel.getShowMainFractalOverview()) {
					Point vp = fFractalScrollPane.getViewport().getViewPosition();
					Dimension vs = fFractalScrollPane.getViewport().getViewSize();
					Dimension es = fFractalScrollPane.getViewport().getExtentSize();
					vp.x += kScrollbarBlockIncrement;
					if ((vp.x + es.width) > vs.width) {
						vp.x = vs.width - es.width + 1;
					}
					fFractalScrollPane.getViewport().setViewPosition(vp);
				}
				else {
					fFractalPanel.pan(FractalPanel.EPanDirection.kRight,fNavigationPanningSize,fMenuItems.get(kActionCommandMenuItemNavigationInvertPanningDirections).isSelected());
				}
			}
			else if (fBindingAction.equalsIgnoreCase(kActionCommandMenuItemNavigationKeyPanUp)) {
				if (fFractalPanel.getShowMainFractalOverview()) {
					Point vp = fFractalScrollPane.getViewport().getViewPosition();
					vp.y -= kScrollbarBlockIncrement;
					if (vp.y < 0) {
						vp.y = 0;
					}
					fFractalScrollPane.getViewport().setViewPosition(vp);
				}
				else {
					fFractalPanel.pan(FractalPanel.EPanDirection.kUp,fNavigationPanningSize,fMenuItems.get(kActionCommandMenuItemNavigationInvertPanningDirections).isSelected());
				}
			}
			else if (fBindingAction.equalsIgnoreCase(kActionCommandMenuItemNavigationKeyPanDown)) {
				if (fFractalPanel.getShowMainFractalOverview()) {
					Point vp = fFractalScrollPane.getViewport().getViewPosition();
					Dimension vs = fFractalScrollPane.getViewport().getViewSize();
					Dimension es = fFractalScrollPane.getViewport().getExtentSize();
					vp.y += kScrollbarBlockIncrement;
					if ((vp.y + es.height) > vs.height) {
						vp.y = vs.height - es.height + 1;
					}
					fFractalScrollPane.getViewport().setViewPosition(vp);
				}
				else {
					fFractalPanel.pan(FractalPanel.EPanDirection.kDown,fNavigationPanningSize,fMenuItems.get(kActionCommandMenuItemNavigationInvertPanningDirections).isSelected());
				}
			}
			else if (fBindingAction.equalsIgnoreCase(kActionCommandMenuItemHelpGeneralInformation)) {
				showHelpTopic(EHelpTopic.kGeneralInformation);
			}
			else if (fBindingAction.equalsIgnoreCase(kActionCommandMenuItemMultithreadingInterrupt)) {
				if (fIteratorController.isBusy()) {
					JIncompleteWarningDialog.warn(fFractalPanel,"GUIApplication");
				}
			}
		}
	}

	/**
	 * @author  Sven Maerivoet
	 * @version 23/06/2015
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
				FileInputStream fileInputStream = new FileInputStream(fFilename);
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				ZipInputStream zipInputStream = new ZipInputStream(bufferedInputStream);
				DataInputStream dataInputStream = new DataInputStream(zipInputStream);

				// prepare to read the compressed outputstream
				zipInputStream.getNextEntry();

				// load fractal family name
				fIteratorController.setBusy(true);
				String familyName = dataInputStream.readUTF();

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
				else if (familyName.equalsIgnoreCase((new DucksSecansFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new DucksSecansFractalIterator());
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
				else if (familyName.equalsIgnoreCase((new QuadbrotFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new QuadbrotFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new TetrationFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new TetrationFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new TetrationDualFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new TetrationDualFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new IOfMedusaFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new IOfMedusaFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new IOfTheStormFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new IOfTheStormFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new AtTheCShoreFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new AtTheCShoreFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new LogarithmicJuliaFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new LogarithmicJuliaFractalIterator());
				}
				else if (familyName.equalsIgnoreCase((new HyperbolicSineJuliaFractalIterator()).getFamilyName())) {
					fIteratorController.setFractalIteratorFamily(new HyperbolicSineJuliaFractalIterator());
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
					// cleanup
					dataInputStream.close();
					zipInputStream.close();
					bufferedInputStream.close();
					fileInputStream.close();

					throw (new UnsupportedFractalException(fFilename,familyName));
				}

				AFractalIterator fractalIterator = fIteratorController.getFractalIterator();

				// if necessary switch to the dual fractal
				if ((fractalIterator instanceof GlynnFractalIterator) ||
						(fractalIterator instanceof BarnsleyTreeFractalIterator) ||
						(fractalIterator instanceof PhoenixFractalIterator) ||
						(fractalIterator instanceof TetrationDualFractalIterator) ||
						(fractalIterator instanceof IOfMedusaFractalIterator) ||
						(fractalIterator instanceof IOfTheStormFractalIterator) ||
						(fractalIterator instanceof AtTheCShoreFractalIterator)) {
					fractalIterator.setFractalType(AFractalIterator.EFractalType.kDualFractal);
				}

				// load fractal parameters
				fractalIterator.streamLoadParameters(dataInputStream);

				// load fractal colouring parameters
				fIteratorController.getColoringParameters().streamLoad(dataInputStream);
				fractalIterator.setCalculateAdvancedColoring(fIteratorController.getColoringParameters().fCalculateAdvancedColoring);

				// load iteration buffer
				fProgressUpdateGlassPane.setVisualisationType(JProgressUpdateGlassPane.EVisualisationType.kBar);
				fProgressUpdateGlassPane.reset();

				int width = fIteratorController.getFractalIterator().getScreenWidth();
				int height = fIteratorController.getFractalIterator().getScreenHeight();
				IterationBuffer fractalResultBuffer = new IterationBuffer(width,height);
				fProgressUpdateGlassPane.setTotalNrOfProgressUpdates(width * height);

				for (int index = 0; index < fractalResultBuffer.fBuffer.length; ++index) {
					fractalResultBuffer.fBuffer[index] = new IterationResult();
					fractalResultBuffer.fBuffer[index].streamLoad(dataInputStream);
					publish(1);
				} // for index

				// cleanup
				dataInputStream.close();
				zipInputStream.close();
				bufferedInputStream.close();
				fileInputStream.close();

				// install loaded fractal
				fIteratorController.setFractalResultBuffer(fractalResultBuffer);

				// adjust the zoom stack
				ZoomStack zoomStack = fFractalPanel.getZoomStack();
				zoomStack.clear();
				zoomStack.push(fractalIterator.getDefaultP1(),fractalIterator.getDefaultP2());
				zoomStack.addThumbnail(null);
				zoomStack.push(fractalIterator.getP1(),fractalIterator.getP2());

				adjustMenusToFractal();

				// adjust canvas dimensions
				fFractalPanel.revalidate();
				fFractalPanel.recolor();
			}
			catch (IOException | UnsupportedFractalException exc) {
				fException = exc;
				System.out.println("X => " + exc);
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
	 * @version 23/06/2015
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
				FileOutputStream fileOutputStream = new FileOutputStream(fFilename);
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
				ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);
				DataOutputStream dataOutputStream = new DataOutputStream(zipOutputStream);

				// prepare to write the outputstream at the highest compression level
				zipOutputStream.setLevel(9);
				zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
				ZipEntry zipEntry = new ZipEntry("fractal-data.bin");
				zipOutputStream.putNextEntry(zipEntry);

				// save fractal parameters
				fIteratorController.getFractalIterator().streamSaveParameters(dataOutputStream);

				// save fractal colouring parameters
				fIteratorController.getColoringParameters().streamSave(dataOutputStream);

				// save iteration buffer
				fProgressUpdateGlassPane.setVisualisationType(JProgressUpdateGlassPane.EVisualisationType.kBar);
				fProgressUpdateGlassPane.reset();
				int width = fIteratorController.getFractalIterator().getScreenWidth();
				int height = fIteratorController.getFractalIterator().getScreenHeight();
				IterationBuffer fractalResultBuffer = fIteratorController.getFractalResultBuffer();
				fProgressUpdateGlassPane.setTotalNrOfProgressUpdates(width * height);
				for (int index = 0; index < fractalResultBuffer.fBuffer.length; ++index) {
					if (fractalResultBuffer.fBuffer[index] == null) {
						(new IterationResult()).streamSave(dataOutputStream);
					}
					else{
						fractalResultBuffer.fBuffer[index].streamSave(dataOutputStream);
					}
					publish(1);
				} // for index

				// cleanup
				dataOutputStream.close();
				zipOutputStream.close();
				bufferedOutputStream.close();
				fileOutputStream.close();
			}
			catch (IOException exc) {
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
				else if ((fException instanceof FileCantBeCreatedException) || (fException instanceof FileWriteException) || (fException instanceof IOException)) {
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
