// -------------------------------------------------
// Filename      : ConvergenceParametersChooser.java
// Author        : Sven Maerivoet
// Last modified : 04/12/2014
// Target        : Java VM (1.8)
// -------------------------------------------------

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

package org.sm.fraxion.gui.dialogs;

import javax.swing.*;
import org.sm.fraxion.fractals.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.math.complex.*;
import org.sm.smtools.swing.dialogs.*;
import org.sm.smtools.swing.util.*;

/**
 * The <CODE>ConvergenceParametersChooser</CODE> class provides a dialog for selecting convergence parameters.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 04/12/2014
 */
public final class ConvergenceParametersChooser extends JDefaultDialog
{
	/**
	 * The various available parameter sets
	 */
	public static enum EParameterSet {kNewtonRaphsonSet, kNovaSet, kMagnetSet};

	// internal datastructures
	private EParameterSet fParameterSet;
	private JNumberInputField fDerivativeDeltaInputField;
	private JNumberInputField fRootToleranceInputField;
	private JNumberInputField fAlphaRealInputField;
	private JNumberInputField fAlphaImaginaryInputField;
	private double fDerivativeDelta;
	private double fRootTolerance;
	private ComplexNumber fAlpha;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>ConvergenceParametersChooser</CODE> object for the full parameter range (Newton-Raphson fractal).
	 *
	 * @param owner            the owning frame
	 * @param derivativeDelta  the initial derivative delta
	 * @param rootTolerance    the initial root tolerance
	 * @param alpha            the initial alpha
	 */
	public ConvergenceParametersChooser(JFrame owner, double derivativeDelta, double rootTolerance, ComplexNumber alpha)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {EParameterSet.kNewtonRaphsonSet, derivativeDelta, rootTolerance, alpha},
			JDefaultDialog.EActivation.kImmediately);
	}

	/**
	 * Constructs a <CODE>ConvergenceParametersChooser</CODE> object for a limited parameter range (Nova fractal).
	 *
	 * @param owner          the owning frame
	 * @param rootTolerance  the initial root tolerance
	 * @param alpha          the initial alpha
	 */
	public ConvergenceParametersChooser(JFrame owner, double rootTolerance, ComplexNumber alpha)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {EParameterSet.kNovaSet, rootTolerance, alpha},
			JDefaultDialog.EActivation.kImmediately);
	}

	/**
	 * Constructs a <CODE>ConvergenceParametersChooser</CODE> object for a limited parameter range (Magnet fractals).
	 *
	 * @param owner          the owning frame
	 * @param rootTolerance  the initial root tolerance
	 */
	public ConvergenceParametersChooser(JFrame owner, double rootTolerance)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {EParameterSet.kMagnetSet, rootTolerance},
			JDefaultDialog.EActivation.kImmediately);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Returns the selected derivative delta.
	 *
	 * @return the selected derivative delta
	 */
	public double getSelectedDerivativeDelta()
	{
		return fDerivativeDeltaInputField.getDoubleValue();
	}

	/**
	 * Returns the selected root tolerance.
	 *
	 * @return the selected root tolerance
	 */
	public double getSelectedRootTolerance()
	{
		return fRootToleranceInputField.getDoubleValue();
	}

	/**
	 * Returns the selected alpha.
	 *
	 * @return the selected alpha
	 */
	public ComplexNumber getSelectedAlpha()
	{
		return (new ComplexNumber(
			fAlphaRealInputField.getDoubleValue(),
			fAlphaImaginaryInputField.getDoubleValue()));
	}

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * Performs custom initialisation.
	 */
	@Override
	protected void initialiseClass(Object[] parameters)
	{
		fParameterSet = (EParameterSet) parameters[0];

		if (fParameterSet == EParameterSet.kNewtonRaphsonSet) {
			fDerivativeDelta = (Double) parameters[1];
			fRootTolerance = (Double) parameters[2];
			fAlpha = (ComplexNumber) parameters[3];
		}
		else if (fParameterSet == EParameterSet.kNovaSet) {
			fRootTolerance = (Double) parameters[1];
			fAlpha = (ComplexNumber) parameters[2];
		}
		else if (fParameterSet == EParameterSet.kMagnetSet) {
			fRootTolerance = (Double) parameters[1];
		}
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.Fractal.ConvergenceParametersTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		final int kInputFieldWidth = 10;
		final boolean kAutoCorrect = false;

		JLabel label = null;
		String toolTipText = "";

		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

		if (fParameterSet == EParameterSet.kNewtonRaphsonSet) {
				JPanel parameter1Panel = new JPanel();
				parameter1Panel.setLayout(new BoxLayout(parameter1Panel,BoxLayout.X_AXIS));
					label = new JLabel(I18NL10N.translate("text.Fractal.ConvergenceParametersChooserDerivativeDelta") + " ");
						toolTipText = "> 0";
					label.setToolTipText(toolTipText);
				parameter1Panel.add(label);
					fDerivativeDeltaInputField = new JNumberInputField(fDerivativeDelta,kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterPositiveNumber"));
					fDerivativeDeltaInputField.setNumberFilter(new PositiveNumberFilter());
					fDerivativeDeltaInputField.setToolTipText(toolTipText);
				parameter1Panel.add(fDerivativeDeltaInputField);
			mainPanel.add(parameter1Panel);

			mainPanel.add(Box.createVerticalStrut(10));
		}

			JPanel parameter2Panel = new JPanel();
			parameter2Panel.setLayout(new BoxLayout(parameter2Panel,BoxLayout.X_AXIS));
				label = new JLabel(I18NL10N.translate("text.Fractal.ConvergenceParametersChooserRootTolerance") + " ");
					toolTipText = "> 0";
				label.setToolTipText(toolTipText);
			parameter2Panel.add(label);
				fRootToleranceInputField = new JNumberInputField(fRootTolerance,kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterPositiveNumber"));
				fRootToleranceInputField.setNumberFilter(new PositiveNumberFilter());
				fRootToleranceInputField.setToolTipText(toolTipText);
			parameter2Panel.add(fRootToleranceInputField);
		mainPanel.add(parameter2Panel);

		if (fParameterSet != EParameterSet.kMagnetSet) {
			mainPanel.add(Box.createVerticalStrut(10));

				JPanel parameter3aPanel = new JPanel();
				parameter3aPanel.setLayout(new BoxLayout(parameter3aPanel,BoxLayout.X_AXIS));
					label = new JLabel(I18NL10N.translate("text.Fractal.ConvergenceParametersChooserAlphaReal") + " ");
						toolTipText = "[" + AConvergentFractalIterator.kMinAlpha.realComponent() + "," + AConvergentFractalIterator.kMaxAlpha.realComponent() + "]";
					label.setToolTipText(toolTipText);
				parameter3aPanel.add(label);
					fAlphaRealInputField = new JNumberInputField(fAlpha.realComponent(),kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterNumberWithinMargins"));
					fAlphaRealInputField.setNumberFilter(new AlphaRealRangeNumberFilter());
					fAlphaRealInputField.setToolTipText(toolTipText);
				parameter3aPanel.add(fAlphaRealInputField);
			mainPanel.add(parameter3aPanel);

			mainPanel.add(Box.createVerticalStrut(10));

				JPanel parameter3bPanel = new JPanel();
				parameter3bPanel.setLayout(new BoxLayout(parameter3bPanel,BoxLayout.X_AXIS));
					label = new JLabel(I18NL10N.translate("text.Fractal.ConvergenceParametersChooserAlphaImaginary") + " ");
						toolTipText = "[" + AConvergentFractalIterator.kMinAlpha.imaginaryComponent() + "," + AConvergentFractalIterator.kMaxAlpha.imaginaryComponent() + "]";
					label.setToolTipText(toolTipText);
				parameter3bPanel.add(label);
					fAlphaImaginaryInputField = new JNumberInputField(fAlpha.imaginaryComponent(),kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterNumberWithinMargins"));
					fAlphaImaginaryInputField.setNumberFilter(new AlphaImaginaryRangeNumberFilter());
					fAlphaImaginaryInputField.setToolTipText(toolTipText);
				parameter3bPanel.add(fAlphaImaginaryInputField);
				parameter3bPanel.add(new JLabel("i"));
			mainPanel.add(parameter3bPanel);
		}
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		if (fParameterSet == EParameterSet.kNewtonRaphsonSet) {
			fDerivativeDelta = getSelectedDerivativeDelta();
		}

		fRootTolerance = getSelectedRootTolerance();

		if (fParameterSet != EParameterSet.kMagnetSet) {
			fAlpha = getSelectedAlpha();
		}
	}

	/*****************
	 * INNER CLASSES *
	 *****************/

	/**
	 * @author  Sven Maerivoet
	 * @version 02/11/2014
	 */
	private class PositiveNumberFilter extends ANumberFilter
	{
		/******************
		 * PUBLIC METHODS *
		 ******************/

		/**
		 * @param d  -
		 */
		public boolean validateDouble(double d)
		{
			return (d > 0.0);
		}

		/**
		 * Unused.
		 *
		 * @param i  -
		 */
		public boolean validateInteger(int i)
		{
			return false;
		}
	}

	/**
	 * @author  Sven Maerivoet
	 * @version 02/11/2014
	 */
	private class AlphaRealRangeNumberFilter extends ANumberFilter
	{
		/******************
		 * PUBLIC METHODS *
		 ******************/

		/**
		 * @param d  -
		 */
		public boolean validateDouble(double d)
		{
			return ((d >= AConvergentFractalIterator.kMinAlpha.realComponent()) && (d <= AConvergentFractalIterator.kMaxAlpha.realComponent()));
		}

		/**
		 * Unused.
		 *
		 * @param i  -
		 */
		public boolean validateInteger(int i)
		{
			return false;
		}
	}

	/**
	 * @author  Sven Maerivoet
	 * @version 02/11/2014
	 */
	private class AlphaImaginaryRangeNumberFilter extends ANumberFilter
	{
		/******************
		 * PUBLIC METHODS *
		 ******************/

		/**
		 * @param d  -
		 */
		public boolean validateDouble(double d)
		{
			return ((d >= AConvergentFractalIterator.kMinAlpha.imaginaryComponent()) && (d <= AConvergentFractalIterator.kMaxAlpha.imaginaryComponent()));
		}

		/**
		 * Unused.
		 *
		 * @param i  -
		 */
		public boolean validateInteger(int i)
		{
			return false;
		}
	}
}
