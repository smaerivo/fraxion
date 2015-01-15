// -----------------------------------------
// Filename      : ComplexBoundsChooser.java
// Author        : Sven Maerivoet
// Last modified : 04/12/2014
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

package org.sm.fraxion.gui.dialogs;

import java.awt.event.*;
import javax.swing.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.math.complex.*;
import org.sm.smtools.swing.dialogs.*;
import org.sm.smtools.swing.util.*;

/**
 * The <CODE>ComplexBoundsChooser</CODE> class provides a dialog for choosing zooming coordinates in the complex plane.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 04/12/2014
 */
public final class ComplexBoundsChooser extends JDefaultDialog implements ActionListener
{
	// the action commands
	private static final String kActionCommandSpecifyCircularRegion = "rb.SpecifyCircularRegion";
	private static final String kActionCommandSpecifyRectangularRegion = "rb.SpecifyRectangularRegion";

	// internal datastructures
	private JLabel fCenterOriginXLabel;
	private JLabel fCenterOriginYLabel;
	private JLabel fCenterOriginILabel;
	private JLabel fCenterRadiusLabel;
	private JNumberInputField fCenterOriginXInputField;
	private JNumberInputField fCenterOriginYInputField;
	private JNumberInputField fCenterRadiusInputField;
	private JLabel fP1XLabel;
	private JLabel fP1YLabel;
	private JLabel fP1ILabel;
	private JLabel fP2XLabel;
	private JLabel fP2YLabel;
	private JLabel fP2ILabel;
	private JNumberInputField fP1XInputField;
	private JNumberInputField fP1YInputField;
	private JNumberInputField fP2XInputField;
	private JNumberInputField fP2YInputField;
	private boolean fCircularRegionSelected;
	private ComplexNumber fCenterOrigin;
	private double fCenterRadius;
	private ComplexNumber fP1;
	private ComplexNumber fP2;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>ComplexBoundsChooser</CODE> object.
	 *
	 * @param owner         the owning frame
	 * @param centerOrigin  the origin of the circular region
	 * @param centerRadius  the radius of the circular region
	 * @param p1            the lower-left corner
	 * @param p2            the upper-right corner
	 */
	public ComplexBoundsChooser(JFrame owner, ComplexNumber centerOrigin, double centerRadius, ComplexNumber p1, ComplexNumber p2)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {centerOrigin,centerRadius,p1,p2},
			JDefaultDialog.EActivation.kImmediately);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	/**
	 * Returns the selected P1 (lower-left corner in the complex plane).
	 *
	 * @return the selected P1 (lower-left corner in the complex plane)
	 */
	public ComplexNumber getSelectedP1()
	{
		if (fCircularRegionSelected) {
			// calculate the encompassing square
			return (new ComplexNumber(
				fCenterOriginXInputField.getDoubleValue() - fCenterRadiusInputField.getDoubleValue(),
				fCenterOriginYInputField.getDoubleValue() - fCenterRadiusInputField.getDoubleValue()));
		}
		else {
			return (new ComplexNumber(
				fP1XInputField.getDoubleValue(),
				fP1YInputField.getDoubleValue()));
		}
	}

	/**
	 * Returns the selected P2 (upper-right corner in the complex plane).
	 *
	 * @return the selected P2 (upper-right corner in the complex plane)
	 */
	public ComplexNumber getSelectedP2()
	{
		if (fCircularRegionSelected) {
			// calculate the encompassing square
			return (new ComplexNumber(
				fCenterOriginXInputField.getDoubleValue() + fCenterRadiusInputField.getDoubleValue(),
				fCenterOriginYInputField.getDoubleValue() + fCenterRadiusInputField.getDoubleValue()));
		}
		else {
			return (new ComplexNumber(
				fP2XInputField.getDoubleValue(),
				fP2YInputField.getDoubleValue()));
		}
	}

	// the action-listener
	/**
	 * See {@link org.sm.smtools.application.JStandardGUIApplication}.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);

		String command = e.getActionCommand();

		if (command.equalsIgnoreCase(kActionCommandSpecifyCircularRegion)) {
			fCenterOriginXLabel.setEnabled(true);
			fCenterOriginYLabel.setEnabled(true);
			fCenterOriginILabel.setEnabled(true);
			fCenterRadiusLabel.setEnabled(true);
			fCenterOriginXInputField.setEnabled(true);
			fCenterOriginYInputField.setEnabled(true);
			fCenterRadiusInputField.setEnabled(true);
			fP1XLabel.setEnabled(false);
			fP1YLabel.setEnabled(false);
			fP1ILabel.setEnabled(false);
			fP2XLabel.setEnabled(false);
			fP2YLabel.setEnabled(false);
			fP2ILabel.setEnabled(false);
			fP1XInputField.setEnabled(false);
			fP1YInputField.setEnabled(false);
			fP2XInputField.setEnabled(false);
			fP2YInputField.setEnabled(false);
			fCircularRegionSelected = true;
		}
		else if (command.equalsIgnoreCase(kActionCommandSpecifyRectangularRegion)) {
			fCenterOriginXLabel.setEnabled(false);
			fCenterOriginYLabel.setEnabled(false);
			fCenterOriginILabel.setEnabled(false);
			fCenterRadiusLabel.setEnabled(false);
			fCenterOriginXInputField.setEnabled(false);
			fCenterOriginYInputField.setEnabled(false);
			fCenterRadiusInputField.setEnabled(false);
			fP1XLabel.setEnabled(true);
			fP1YLabel.setEnabled(true);
			fP1ILabel.setEnabled(true);
			fP2XLabel.setEnabled(true);
			fP2YLabel.setEnabled(true);
			fP2ILabel.setEnabled(true);
			fP1XInputField.setEnabled(true);
			fP1YInputField.setEnabled(true);
			fP2XInputField.setEnabled(true);
			fP2YInputField.setEnabled(true);
			fCircularRegionSelected = false;
		}
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
		fCenterOrigin = (ComplexNumber) parameters[0];
		fCenterRadius = (double) parameters[1];
		fP1 = (ComplexNumber) parameters[2];
		fP2 = (ComplexNumber) parameters[3];

		fCircularRegionSelected = true;
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.Navigation.CoordinatesChooserTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		final int kInputFieldWidth = 10;
		final boolean kAutoCorrect = false;

		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		ButtonGroup bgRegions = new ButtonGroup();

			JPanel upperTitlePanel = new JPanel();
			upperTitlePanel.setLayout(new BoxLayout(upperTitlePanel,BoxLayout.X_AXIS));

				JRadioButton rbCircularRegion = new JRadioButton(I18NL10N.translate("text.Navigation.CoordinatesChooserSpecifyCircularRegion"));
				rbCircularRegion.setSelected(true);
				rbCircularRegion.setActionCommand(kActionCommandSpecifyCircularRegion);
				rbCircularRegion.addActionListener(this);
				bgRegions.add(rbCircularRegion);
			upperTitlePanel.add(rbCircularRegion);
			upperTitlePanel.add(Box.createHorizontalGlue());
		mainPanel.add(upperTitlePanel);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel centerOriginPanel = new JPanel();
			centerOriginPanel.setLayout(new BoxLayout(centerOriginPanel,BoxLayout.X_AXIS));

				fCenterOriginXLabel = new JLabel("x" + " ");
			centerOriginPanel.add(fCenterOriginXLabel);
				fCenterOriginXInputField = new JNumberInputField(fCenterOrigin.realComponent(),kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterNumber"));
			centerOriginPanel.add(fCenterOriginXInputField);

			centerOriginPanel.add(Box.createHorizontalStrut(10));

				fCenterOriginYLabel = new JLabel("y" + " ");
			centerOriginPanel.add(fCenterOriginYLabel);
				fCenterOriginYInputField = new JNumberInputField(fCenterOrigin.imaginaryComponent(),kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterNumber"));
			centerOriginPanel.add(fCenterOriginYInputField);
				fCenterOriginILabel = new JLabel(" i");
			centerOriginPanel.add(fCenterOriginILabel);

			centerOriginPanel.add(Box.createHorizontalGlue());
		mainPanel.add(centerOriginPanel);

			JPanel centerRadiusPanel = new JPanel();
			centerRadiusPanel.setLayout(new BoxLayout(centerRadiusPanel,BoxLayout.X_AXIS));

					fCenterRadiusLabel = new JLabel(I18NL10N.translate("text.Navigation.CoordinatesChooserRadius") + " ");
					String toolTipText = "> 0";
					fCenterRadiusLabel.setToolTipText(toolTipText);
			centerRadiusPanel.add(fCenterRadiusLabel);
				fCenterRadiusInputField = new JNumberInputField(fCenterRadius,kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterPositiveNumber"));
				fCenterRadiusInputField.setNumberFilter(new RadiusNumberFilter());
				fCenterRadiusInputField.setToolTipText(toolTipText);
			centerRadiusPanel.add(fCenterRadiusInputField);

			centerRadiusPanel.add(Box.createHorizontalGlue());
		mainPanel.add(centerRadiusPanel);

		mainPanel.add(Box.createVerticalStrut(10));
		mainPanel.add(new JEtchedLine(JEtchedLine.EOrientation.kHorizontal));
		mainPanel.add(Box.createVerticalStrut(10));

			JPanel lowerTitlePanel = new JPanel();
			lowerTitlePanel.setLayout(new BoxLayout(lowerTitlePanel,BoxLayout.X_AXIS));

				JRadioButton rbRectangularRegion = new JRadioButton(I18NL10N.translate("text.Navigation.CoordinatesChooserSpecifyRectangularRegion"));
				bgRegions.add(rbRectangularRegion);
				rbRectangularRegion.setActionCommand(kActionCommandSpecifyRectangularRegion);
				rbRectangularRegion.addActionListener(this);
			lowerTitlePanel.add(rbRectangularRegion);
			lowerTitlePanel.add(Box.createHorizontalGlue());
		mainPanel.add(lowerTitlePanel);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel p1Panel = new JPanel();
			p1Panel.setLayout(new BoxLayout(p1Panel,BoxLayout.X_AXIS));

				fP1XLabel = new JLabel("x1" + " ");
				fP1XLabel.setEnabled(false);
			p1Panel.add(fP1XLabel);
				fP1XInputField = new JNumberInputField(fP1.realComponent(),kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterNumber"));
				fP1XInputField.setEnabled(false);
			p1Panel.add(fP1XInputField);

			p1Panel.add(Box.createHorizontalStrut(10));

				fP1YLabel = new JLabel("y1" + " ");
				fP1YLabel.setEnabled(false);
			p1Panel.add(fP1YLabel);
				fP1YInputField = new JNumberInputField(fP1.imaginaryComponent(),kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterNumber"));
				fP1YInputField.setEnabled(false);
			p1Panel.add(fP1YInputField);
				fP1ILabel = new JLabel(" i");
				fP1ILabel.setEnabled(false);
			p1Panel.add(fP1ILabel);

			p1Panel.add(Box.createHorizontalGlue());
		mainPanel.add(p1Panel);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel p2Panel = new JPanel();
			p2Panel.setLayout(new BoxLayout(p2Panel,BoxLayout.X_AXIS));

				fP2XLabel = new JLabel("x2" + " ");
				fP2XLabel.setEnabled(false);
			p2Panel.add(fP2XLabel);
				fP2XInputField = new JNumberInputField(fP2.realComponent(),kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterNumber"));
				fP2XInputField.setEnabled(false);
			p2Panel.add(fP2XInputField);

			p2Panel.add(Box.createHorizontalStrut(10));

				fP2YLabel = new JLabel("y2" + " ");
				fP2YLabel.setEnabled(false);
			p2Panel.add(fP2YLabel);
				fP2YInputField = new JNumberInputField(fP2.imaginaryComponent(),kInputFieldWidth,kAutoCorrect,I18NL10N.translate("error.EnterNumber"));
				fP2YInputField.setEnabled(false);
			p2Panel.add(fP2YInputField);
				fP2ILabel = new JLabel(" i");
				fP2ILabel.setEnabled(false);
			p2Panel.add(fP2ILabel);

			p2Panel.add(Box.createHorizontalGlue());
		mainPanel.add(p2Panel);
	}

	/*****************
	 * INNER CLASSES *
	 *****************/

	/**
	 * @author  Sven Maerivoet
	 * @version 05/09/2014
	 */
	private class RadiusNumberFilter extends ANumberFilter
	{
		/******************
		 * PUBLIC METHODS *
		 ******************/

		/**
		 * @param i  -
		 * @return   -
		 */
		public boolean validateInteger(int i)
		{
			return (i > 0);
		}

		/**
		 * @param d  -
		 * @return   -
		 */
		public boolean validateDouble(double d)
		{
			return (d > 0.0);
		}
	}
}
