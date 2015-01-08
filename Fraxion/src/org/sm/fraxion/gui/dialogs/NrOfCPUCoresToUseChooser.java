// ---------------------------------------------
// Filename      : NrOfCPUCoresToUseChooser.java
// Author        : Sven Maerivoet
// Last modified : 24/09/2014
// Target        : Java VM (1.8)
// ---------------------------------------------

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
 * The <CODE>NrOfCPUCoresToUseChooser</CODE> class provides a dialog for selecting the number of CPU cores to use.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 24/09/2014
 */
public final class NrOfCPUCoresToUseChooser extends JDefaultDialog implements ChangeListener
{
	// internal datastructures
	private int fNrOfCPUCoresToUse;
	private JSlider fNrOfCPUCoresToUseSlider;
	private JLabel fNrOfCPUCoresToUseLabel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>NrOfCPUCoresToUseChooser</CODE> object.
	 *
	 * @param owner              the owning frame
	 * @param nrOfCPUCoresToUse  the initial number of CPU cores to use
	 */
	public NrOfCPUCoresToUseChooser(JFrame owner, int nrOfCPUCoresToUse)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {nrOfCPUCoresToUse},
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
		if (!fNrOfCPUCoresToUseSlider.getValueIsAdjusting()) {
			// extract value
			fNrOfCPUCoresToUse = (int) fNrOfCPUCoresToUseSlider.getValue();
			updateGUI();
		}
	}

	/**
	 * Returns the number of CPU cores to use.
	 *
	 * @return the number of CPU cores to use
	 */
	public int getSelectedNrOfCPUCoresToUse()
	{
		return fNrOfCPUCoresToUse;
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
		fNrOfCPUCoresToUse = (int) parameters[0];
		if (fNrOfCPUCoresToUse > MemoryStatistics.getNrOfProcessors()) {
			fNrOfCPUCoresToUse = MemoryStatistics.getNrOfProcessors();
		}
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.MultiThreading.NrOfCPUCoresToUseTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			fNrOfCPUCoresToUseSlider = new JSlider(JSlider.HORIZONTAL);
			fNrOfCPUCoresToUseSlider.setInverted(false);
			fNrOfCPUCoresToUseSlider.setMinimum(1);
			fNrOfCPUCoresToUseSlider.setMaximum(MemoryStatistics.getNrOfProcessors());
			fNrOfCPUCoresToUseSlider.setMinorTickSpacing(2);
			fNrOfCPUCoresToUseSlider.setMajorTickSpacing(1);
			fNrOfCPUCoresToUseSlider.setPaintTicks(true);
			fNrOfCPUCoresToUseSlider.setPaintLabels(true);
			fNrOfCPUCoresToUseSlider.setPaintTrack(true);
			fNrOfCPUCoresToUseSlider.addChangeListener(this);
		mainPanel.add(fNrOfCPUCoresToUseSlider);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel lowerPanel = new JPanel();
			lowerPanel.setLayout(new BoxLayout(lowerPanel,BoxLayout.X_AXIS));
			fNrOfCPUCoresToUseLabel = new JLabel();
			lowerPanel.add(fNrOfCPUCoresToUseLabel);
			lowerPanel.add(Box.createHorizontalGlue());
		mainPanel.add(lowerPanel);
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fNrOfCPUCoresToUseSlider.setValue(fNrOfCPUCoresToUse);
		fNrOfCPUCoresToUseLabel.setText(I18NL10N.translate("text.MultiThreading.NrOfCPUCoresToUseLabel",String.valueOf(fNrOfCPUCoresToUse)));
	}
}
