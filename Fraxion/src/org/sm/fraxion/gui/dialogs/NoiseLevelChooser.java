// --------------------------------------
// Filename      : NoiseLevelChooser.java
// Author        : Sven Maerivoet
// Last modified : 24/09/2014
// Target        : Java VM (1.8)
// --------------------------------------

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

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.swing.dialogs.*;
import org.sm.smtools.util.*;

/**
 * The <CODE>NoiseLevelChooser</CODE> class provides a dialog for selecting a noise level.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 24/09/2014
 */
public final class NoiseLevelChooser extends JDefaultDialog implements ChangeListener
{
	// the maximum noise level (expressed in millionths)
	private static final int kMaxNoiseLevel = 1000000;

	// internal datastructures
	private int fNoiseLevel;
	private JSlider fNoiseLevelSlider;
	private JLabel fNoiseLevelLabel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>NoiseLevelChooser</CODE> object.
	 *
	 * @param owner       the owning frame
	 * @param noiseLevel  the initial noise level
	 */
	public NoiseLevelChooser(JFrame owner, double noiseLevel)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {noiseLevel},
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
		if (!fNoiseLevelSlider.getValueIsAdjusting()) {
			// extract value
			fNoiseLevel = (int) fNoiseLevelSlider.getValue();
			updateGUI();
		}
	}

	/**
	 * Returns the selected noise level.
	 *
	 * @return the selected noise level
	 */
	public double getSelectedNoiseLevel()
	{
		return (fNoiseLevel / 1000000.0);
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
		fNoiseLevel = (int) Math.round(((double) parameters[0]) * 1000000.0);
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.Fractal.NoiseLevelTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			fNoiseLevelSlider = new JSlider(JSlider.HORIZONTAL);
			fNoiseLevelSlider.setInverted(false);
			fNoiseLevelSlider.setMinimum(0);
			fNoiseLevelSlider.setMaximum(kMaxNoiseLevel);
			fNoiseLevelSlider.setMinorTickSpacing(kMaxNoiseLevel / 10);
			fNoiseLevelSlider.setMajorTickSpacing(kMaxNoiseLevel / 5);
			fNoiseLevelSlider.setPaintTicks(true);
				Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
				for (int noiseLevel = 0; noiseLevel <= kMaxNoiseLevel; noiseLevel += (kMaxNoiseLevel / 5)) {
					labelTable.put(noiseLevel,new JLabel(StringTools.convertDoubleToString((double) noiseLevel / 1000000.0,3)));
				}
			fNoiseLevelSlider.setLabelTable(labelTable);
			fNoiseLevelSlider.setPaintLabels(true);
			fNoiseLevelSlider.setPaintTrack(true);
			fNoiseLevelSlider.setValue(fNoiseLevel);
			fNoiseLevelSlider.addChangeListener(this);
		mainPanel.add(fNoiseLevelSlider);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel lowerPanel = new JPanel();
			lowerPanel.setLayout(new BoxLayout(lowerPanel,BoxLayout.X_AXIS));
			fNoiseLevelLabel = new JLabel();
			lowerPanel.add(fNoiseLevelLabel);
			lowerPanel.add(Box.createHorizontalGlue());
		mainPanel.add(lowerPanel);
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fNoiseLevelSlider.setValue(fNoiseLevel);
		fNoiseLevelLabel.setText(I18NL10N.translate("text.Fractal.NoiseLevelLabel",StringTools.convertDoubleToString((double) fNoiseLevel / 1000000.0,6)));
	}
}
