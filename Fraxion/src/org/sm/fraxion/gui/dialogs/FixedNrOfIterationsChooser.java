// -----------------------------------------------
// Filename      : FixedNrOfIterationsChooser.java
// Author        : Sven Maerivoet
// Last modified : 03/10/2014
// Target        : Java VM (1.8)
// -----------------------------------------------

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
import javax.swing.event.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.swing.dialogs.*;

/**
 * The <CODE>FixedNrOfIterationsChooser</CODE> class provides a dialog for selecting the fixed number of iterations.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 03/10/2014
 */
public final class FixedNrOfIterationsChooser extends JDefaultDialog implements ChangeListener
{
	// the maximum number of fixed iterations to allow
	private static final int kMaxNrOfFixedIterations = 70;

	// internal datastructures
	private int fFixedNrOfIterations;
	private JLabel fFixedNrOfIterationsLabel;
	private JSlider fFixedNrOfIterationsSlider;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>MaxNrOfIterationsInOrbitAnalysesChooser</CODE> object.
	 *
	 * @param owner                the owning frame
	 * @param fixedNrOfIterations  the initial fixed number of iterations
	 */
	public FixedNrOfIterationsChooser(JFrame owner, int fixedNrOfIterations)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {fixedNrOfIterations},
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
		if (!fFixedNrOfIterationsSlider.getValueIsAdjusting()) {
			// extract value
			fFixedNrOfIterations = (int) fFixedNrOfIterationsSlider.getValue();
			updateGUI();
		}
	}

	/**
	 * Returns the maximum number of fixed iterations.
	 *
	 * @return the maximum number of fixed iterations
	 */
	public int getSelectedFixedNrOfIterations()
	{
		return fFixedNrOfIterations;
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
		fFixedNrOfIterations = (int) parameters[0];
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.Fractal.FixedNrOfIterationsTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			fFixedNrOfIterationsSlider = new JSlider(JSlider.HORIZONTAL);
			fFixedNrOfIterationsSlider.setInverted(false);
			fFixedNrOfIterationsSlider.setMinimum(1);
			fFixedNrOfIterationsSlider.setMaximum(kMaxNrOfFixedIterations);
			fFixedNrOfIterationsSlider.setMinorTickSpacing((int) Math.round((double) kMaxNrOfFixedIterations / 10.0));
			fFixedNrOfIterationsSlider.setMajorTickSpacing((int) Math.round((double) kMaxNrOfFixedIterations / 5.0));
			fFixedNrOfIterationsSlider.setPaintTicks(true);
			fFixedNrOfIterationsSlider.setPaintLabels(true);
			fFixedNrOfIterationsSlider.setPaintTrack(true);
			fFixedNrOfIterationsSlider.setValue(fFixedNrOfIterations);
			fFixedNrOfIterationsSlider.addChangeListener(this);
		mainPanel.add(fFixedNrOfIterationsSlider);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel lowerPanel = new JPanel();
			lowerPanel.setLayout(new BoxLayout(lowerPanel,BoxLayout.X_AXIS));
			fFixedNrOfIterationsLabel = new JLabel();
			lowerPanel.add(fFixedNrOfIterationsLabel);
			lowerPanel.add(Box.createHorizontalGlue());
		mainPanel.add(lowerPanel);
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fFixedNrOfIterationsSlider.setValue(fFixedNrOfIterations);
		fFixedNrOfIterationsLabel.setText(I18NL10N.translate("text.Fractal.FixedNrOfIterationsLabel",String.valueOf(fFixedNrOfIterations)));
	}
}
