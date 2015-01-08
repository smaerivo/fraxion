// ------------------------------------------------------------
// Filename      : MaxNrOfIterationsInOrbitAnalysesChooser.java
// Author        : Sven Maerivoet
// Last modified : 29/10/2014
// Target        : Java VM (1.8)
// ------------------------------------------------------------

/**
 * Copyright 2003-2014 Sven Maerivoet
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

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.swing.dialogs.*;

/**
 * The <CODE>MaxNrOfIterationsInOrbitAnalysesChooser</CODE> class provides a dialog for selecting the maximum number of iterations shown in the orbit analyses panel.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 29/10/2014
 */
public final class MaxNrOfIterationsInOrbitAnalysesChooser extends JDefaultDialog implements ChangeListener
{
	// internal datastructures
	private int fMaxNrOfIterationsInOrbitAnalyses;
	private int fMaxNrOfIterations;
	private JSlider fMaxNrOfIterationsInOrbitAnalysesSlider;
	private JLabel fMaxNrOfIterationsInOrbitAnalysesLabel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>MaxNrOfIterationsInOrbitAnalysesChooser</CODE> object.
	 *
	 * @param owner                             the owning frame
	 * @param maxNrOfIterationsInOrbitAnalyses  the initial maximum number of iterations shown in the orbit analyses panel
	 * @param maxNrOfIterations                 the maximum number of iterations used for calculations
	 */
	public MaxNrOfIterationsInOrbitAnalysesChooser(JFrame owner, int maxNrOfIterationsInOrbitAnalyses, int maxNrOfIterations)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {maxNrOfIterationsInOrbitAnalyses,maxNrOfIterations},
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
		if (!fMaxNrOfIterationsInOrbitAnalysesSlider.getValueIsAdjusting()) {
			// extract value
			fMaxNrOfIterationsInOrbitAnalyses = (int) fMaxNrOfIterationsInOrbitAnalysesSlider.getValue();
			updateGUI();
		}
	}

	/**
	 * Returns the maximum number of iterations shown in the orbit analyses panel.
	 *
	 * @return the maximum number of iterations shown in the orbit analyses panel
	 */
	public int getSelectedMaxNrOfIterationsInOrbitAnalyses()
	{
		return fMaxNrOfIterationsInOrbitAnalyses;
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
		fMaxNrOfIterationsInOrbitAnalyses = (int) parameters[0];
		fMaxNrOfIterations = (int) parameters[1];
		if (fMaxNrOfIterationsInOrbitAnalyses > fMaxNrOfIterations) {
			fMaxNrOfIterationsInOrbitAnalyses = fMaxNrOfIterations;
		}
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.Fractal.MaxNrOfIterationsInOrbitAnalysesTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			fMaxNrOfIterationsInOrbitAnalysesSlider = new JSlider(JSlider.HORIZONTAL);
			fMaxNrOfIterationsInOrbitAnalysesSlider.setInverted(false);
			fMaxNrOfIterationsInOrbitAnalysesSlider.setMinimum(0);
			fMaxNrOfIterationsInOrbitAnalysesSlider.setMaximum(fMaxNrOfIterations);
			fMaxNrOfIterationsInOrbitAnalysesSlider.setMinorTickSpacing(0);
				int nrOfMajorTicks = 5;
				int majorTickSpacing = (int) Math.round((double) fMaxNrOfIterations / (double) nrOfMajorTicks);
			fMaxNrOfIterationsInOrbitAnalysesSlider.setMajorTickSpacing(majorTickSpacing);
			fMaxNrOfIterationsInOrbitAnalysesSlider.setPaintTicks(true);
				Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
				labelTable.put(0,new JLabel("+Inf"));
				for (int tick = 1; tick < nrOfMajorTicks; ++tick) {
					int value = (int) Math.round((double) tick * ((double) fMaxNrOfIterations / (double) nrOfMajorTicks));
					labelTable.put(value,new JLabel(String.valueOf(value)));
				}
			fMaxNrOfIterationsInOrbitAnalysesSlider.setLabelTable(labelTable);
			fMaxNrOfIterationsInOrbitAnalysesSlider.setPaintLabels(true);
			fMaxNrOfIterationsInOrbitAnalysesSlider.setPaintTrack(true);
			fMaxNrOfIterationsInOrbitAnalysesSlider.setValue(fMaxNrOfIterationsInOrbitAnalyses);
			fMaxNrOfIterationsInOrbitAnalysesSlider.addChangeListener(this);
		mainPanel.add(fMaxNrOfIterationsInOrbitAnalysesSlider);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel lowerPanel = new JPanel();
			lowerPanel.setLayout(new BoxLayout(lowerPanel,BoxLayout.X_AXIS));
			fMaxNrOfIterationsInOrbitAnalysesLabel = new JLabel();
			lowerPanel.add(fMaxNrOfIterationsInOrbitAnalysesLabel);
			lowerPanel.add(Box.createHorizontalGlue());
		mainPanel.add(lowerPanel);
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fMaxNrOfIterationsInOrbitAnalysesSlider.setValue(fMaxNrOfIterationsInOrbitAnalyses);
		if (fMaxNrOfIterationsInOrbitAnalyses == 0) {
			fMaxNrOfIterationsInOrbitAnalysesLabel.setText(I18NL10N.translate("text.Fractal.MaxNrOfIterationsInOrbitAnalysesLabel","+Inf"));
		}
		else {
			fMaxNrOfIterationsInOrbitAnalysesLabel.setText(I18NL10N.translate("text.Fractal.MaxNrOfIterationsInOrbitAnalysesLabel",String.valueOf(fMaxNrOfIterationsInOrbitAnalyses) + "   "));
		}
	}
}
