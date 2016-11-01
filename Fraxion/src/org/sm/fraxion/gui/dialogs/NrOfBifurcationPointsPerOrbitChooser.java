// ---------------------------------------------------------
// Filename      : NrOfBifurcationPointsPerOrbitChooser.java
// Author        : Sven Maerivoet
// Last modified : 01/11/2016
// Target        : Java VM (1.8)
// ---------------------------------------------------------

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

package org.sm.fraxion.gui.dialogs;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.swing.dialogs.*;

/**
 * The <CODE>NrOfBifurcationPointsPerOrbitChooser</CODE> class provides a dialog for selecting the number of bifurcation points per orbit.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 01/11/2016
 */
public final class NrOfBifurcationPointsPerOrbitChooser extends JDefaultDialog implements ChangeListener
{
	// the minimum and maximum number of bifurcation points to discard
	private static final int kMinNrOfBifurcationPointsToDiscard = 1000;
	private static final int kMaxNrOfBifurcationPointsToDiscard = 50000;

	// the minimum and maximum number of bifurcation points per orbit
	private static final int kMinNrOfBifurcationPointsPerOrbit = 100;
	private static final int kMaxNrOfBifurcationPointsPerOrbit = 25000;

	// internal datastructures
	private int fNrOfBifurcationPointsToDiscard;
	private int fNrOfBifurcationPointsPerOrbit;
	private JSlider fNrOfBifurcationPointsToDiscardSlider;
	private JSlider fNrOfBifurcationPointsPerOrbitSlider;
	private JLabel fNrOfBifurcationPointsToDiscardLabel;
	private JLabel fNrOfBifurcationPointsPerOrbitLabel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>NrOfBifurcationPointsPerOrbitChooser</CODE> object.
	 *
	 * @param owner                           the owning frame
	 * @param nrOfBifurcationPointsToDiscard  the initial number of bifurcation points to discard
	 * @param nrOfBifurcationPointsPerOrbit   the initial number of bifurcation points per orbit
	 */
	public NrOfBifurcationPointsPerOrbitChooser(JFrame owner, int nrOfBifurcationPointsToDiscard, int nrOfBifurcationPointsPerOrbit)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {nrOfBifurcationPointsToDiscard,nrOfBifurcationPointsPerOrbit},
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
		if (e.getSource() == fNrOfBifurcationPointsToDiscardSlider) {
			if (!fNrOfBifurcationPointsToDiscardSlider.getValueIsAdjusting()) {
				// extract value
				fNrOfBifurcationPointsToDiscard = (int) fNrOfBifurcationPointsToDiscardSlider.getValue();
				updateGUI();
			}
		}
		else if (e.getSource() == fNrOfBifurcationPointsPerOrbitSlider) {
			if (!fNrOfBifurcationPointsPerOrbitSlider.getValueIsAdjusting()) {
				// extract value
				fNrOfBifurcationPointsPerOrbit = (int) fNrOfBifurcationPointsPerOrbitSlider.getValue();
				updateGUI();
			}
		}
	}

	/**
	 * Returns the number of bifurcation points to discard.
	 *
	 * @return the number of bifurcation points to discard
	 */
	public int getSelectedNrOfBifurcationPointsToDiscard()
	{
		return fNrOfBifurcationPointsToDiscard;
	}

	/**
	 * Returns the number of bifurcation points per orbit.
	 *
	 * @return the number of bifurcation points per orbit
	 */
	public int getSelectedNrOfBifurcationPointsPerOrbit()
	{
		return fNrOfBifurcationPointsPerOrbit;
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
		fNrOfBifurcationPointsToDiscard = (int) parameters[0];
		fNrOfBifurcationPointsPerOrbit = (int) parameters[1];
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.Fractal.BifurcationPointsPerOrbitTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			fNrOfBifurcationPointsToDiscardLabel = new JLabel();
			fNrOfBifurcationPointsToDiscardLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(fNrOfBifurcationPointsToDiscardLabel);

			fNrOfBifurcationPointsToDiscardSlider = new JSlider(JSlider.HORIZONTAL);
			fNrOfBifurcationPointsToDiscardSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
			fNrOfBifurcationPointsToDiscardSlider.setInverted(false);
			fNrOfBifurcationPointsToDiscardSlider.setMinimum(kMinNrOfBifurcationPointsToDiscard);
			fNrOfBifurcationPointsToDiscardSlider.setMaximum(kMaxNrOfBifurcationPointsToDiscard);
			fNrOfBifurcationPointsToDiscardSlider.setMajorTickSpacing(kMaxNrOfBifurcationPointsToDiscard / 10);
			fNrOfBifurcationPointsToDiscardSlider.setMajorTickSpacing(kMaxNrOfBifurcationPointsToDiscard / 5);
			fNrOfBifurcationPointsToDiscardSlider.setPaintTicks(true);
			fNrOfBifurcationPointsToDiscardSlider.setPaintLabels(false);
			fNrOfBifurcationPointsToDiscardSlider.setPaintTrack(true);
			fNrOfBifurcationPointsToDiscardSlider.setValue(fNrOfBifurcationPointsToDiscard);
			fNrOfBifurcationPointsToDiscardSlider.addChangeListener(this);
		mainPanel.add(fNrOfBifurcationPointsToDiscardSlider);

		mainPanel.add(Box.createVerticalStrut(10));

			fNrOfBifurcationPointsPerOrbitLabel = new JLabel();
			fNrOfBifurcationPointsPerOrbitLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(fNrOfBifurcationPointsPerOrbitLabel);

			fNrOfBifurcationPointsPerOrbitSlider = new JSlider(JSlider.HORIZONTAL);
			fNrOfBifurcationPointsPerOrbitSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
			fNrOfBifurcationPointsPerOrbitSlider.setInverted(false);
			fNrOfBifurcationPointsPerOrbitSlider.setMinimum(kMinNrOfBifurcationPointsPerOrbit);
			fNrOfBifurcationPointsPerOrbitSlider.setMaximum(kMaxNrOfBifurcationPointsPerOrbit);
			fNrOfBifurcationPointsPerOrbitSlider.setMinorTickSpacing(kMaxNrOfBifurcationPointsPerOrbit / 10);
			fNrOfBifurcationPointsPerOrbitSlider.setMajorTickSpacing(kMaxNrOfBifurcationPointsPerOrbit / 5);
			fNrOfBifurcationPointsPerOrbitSlider.setPaintTicks(true);
			fNrOfBifurcationPointsPerOrbitSlider.setPaintLabels(false);
			fNrOfBifurcationPointsPerOrbitSlider.setPaintTrack(true);
			fNrOfBifurcationPointsPerOrbitSlider.setValue(fNrOfBifurcationPointsPerOrbit);
			fNrOfBifurcationPointsPerOrbitSlider.addChangeListener(this);
		mainPanel.add(fNrOfBifurcationPointsPerOrbitSlider);
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fNrOfBifurcationPointsToDiscardSlider.setValue(fNrOfBifurcationPointsToDiscard);
		fNrOfBifurcationPointsToDiscardLabel.setText(I18NL10N.translate("text.Fractal.BifurcationPointsToDiscard",String.valueOf(fNrOfBifurcationPointsToDiscard)));
		fNrOfBifurcationPointsPerOrbitSlider.setValue(fNrOfBifurcationPointsPerOrbit);
		fNrOfBifurcationPointsPerOrbitLabel.setText(I18NL10N.translate("text.Fractal.BifurcationPointsPerOrbit",String.valueOf(fNrOfBifurcationPointsPerOrbit)));
	}
}
