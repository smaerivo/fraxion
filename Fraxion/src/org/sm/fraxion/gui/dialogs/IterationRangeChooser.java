// ------------------------------------------
// Filename      : IterationRangeChooser.java
// Author        : Sven Maerivoet
// Last modified : 24/09/2014
// Target        : Java VM (1.8)
// ------------------------------------------

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

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.swing.dialogs.*;

/**
 * The <CODE>IterationRangeChooser</CODE> class provides a dialog for selecting the range of the iterations.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 24/09/2014
 */
public final class IterationRangeChooser extends JDefaultDialog implements ChangeListener
{
	// internal datastructures
	private int fLowIterationRange;
	private int fHighIterationRange;
	private int fMaxNrOfIterations;
	private JSlider fLowIterationRangeSlider;
	private JSlider fHighIterationRangeSlider;
	private JLabel fLowIterationRangeLabel;
	private JLabel fHighIterationRangeLabel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs an <CODE>IterationRangeChooser</CODE> object.
	 *
	 * @param owner               the owning frame
	 * @param lowIterationRange   the low iteration range
	 * @param highIterationRange  the high iteration range
	 * @param maxNrOfIterations   the maximum number of iterations
	 */
	public IterationRangeChooser(JFrame owner, int lowIterationRange, int highIterationRange, int maxNrOfIterations)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {lowIterationRange,highIterationRange,maxNrOfIterations},
			JDefaultDialog.EActivation.kImmediately);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	// the change-listener
	/**
	 */
	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == fLowIterationRangeSlider) {
			if (!fLowIterationRangeSlider.getValueIsAdjusting()) {
				// extract value
				fLowIterationRange = (int) fLowIterationRangeSlider.getValue();
				if (fLowIterationRange > fHighIterationRange) {
					fHighIterationRange = fLowIterationRange;
				}
				updateGUI();
			}
		}
		else if (e.getSource() == fHighIterationRangeSlider) {
			if (!fHighIterationRangeSlider.getValueIsAdjusting()) {
				// extract value
				fHighIterationRange = (int) fHighIterationRangeSlider.getValue();
				if (fHighIterationRange < fLowIterationRange) {
					fLowIterationRange = fHighIterationRange;
				}
				updateGUI();
			}
		}
	}

	/**
	 * Returns the low iteration range.
	 *
	 * @return the low iteration range
	 */
	public int getSelectedLowIterationRange()
	{
		return fLowIterationRange;
	}

	/**
	 * Returns the high iteration range.
	 *
	 * @return the low iteration range
	 */
	public int getSelectedHighIterationRange()
	{
		return fHighIterationRange;
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
		fLowIterationRange = (Integer) parameters[0];
		fHighIterationRange = (Integer) parameters[1];
		fMaxNrOfIterations = (Integer) parameters[2];
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.ColorMap.IterationRangeTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			fLowIterationRangeLabel = new JLabel();
			fLowIterationRangeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(fLowIterationRangeLabel);

			fLowIterationRangeSlider = new JSlider(JSlider.HORIZONTAL);
			fLowIterationRangeSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
			fLowIterationRangeSlider.setMaximum(fMaxNrOfIterations);
			fLowIterationRangeSlider.setMinorTickSpacing((int) Math.round((double) fMaxNrOfIterations / 10.0));
			fLowIterationRangeSlider.setMajorTickSpacing((int) Math.round((double) fMaxNrOfIterations / 5.0));
			fLowIterationRangeSlider.setPaintTicks(true);
			fLowIterationRangeSlider.setPaintLabels(false);
			fLowIterationRangeSlider.setPaintTrack(true);
			fLowIterationRangeSlider.setValue(fLowIterationRange);
			fLowIterationRangeSlider.addChangeListener(this);
		mainPanel.add(fLowIterationRangeSlider);

		mainPanel.add(Box.createVerticalStrut(10));

			fHighIterationRangeLabel = new JLabel();
			fHighIterationRangeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(fHighIterationRangeLabel);

			fHighIterationRangeSlider = new JSlider(JSlider.HORIZONTAL);
			fHighIterationRangeSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
			fHighIterationRangeSlider.setMaximum(fMaxNrOfIterations);
			fHighIterationRangeSlider.setMinorTickSpacing((int) Math.round((double) fMaxNrOfIterations / 10.0));
			fHighIterationRangeSlider.setMajorTickSpacing((int) Math.round((double) fMaxNrOfIterations / 5.0));
			fHighIterationRangeSlider.setPaintTicks(true);
			fHighIterationRangeSlider.setPaintLabels(false);
			fHighIterationRangeSlider.setPaintTrack(true);
			fHighIterationRangeSlider.setValue(fHighIterationRange);
			fHighIterationRangeSlider.addChangeListener(this);
		mainPanel.add(fHighIterationRangeSlider);
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fLowIterationRangeSlider.setValue(fLowIterationRange);
		fHighIterationRangeSlider.setValue(fHighIterationRange);
		fLowIterationRangeLabel.setText(I18NL10N.translate("text.ColorMap.IterationRangeLowRangeLabel",String.valueOf(fLowIterationRange)));
		fHighIterationRangeLabel.setText(I18NL10N.translate("text.ColorMap.IterationRangeHighRangeLabel",String.valueOf(fHighIterationRange)));
	}
}
