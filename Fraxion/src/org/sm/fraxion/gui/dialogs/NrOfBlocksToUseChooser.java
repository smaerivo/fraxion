// -------------------------------------------
// Filename      : NrOfBlocksToUseChooser.java
// Author        : Sven Maerivoet
// Last modified : 06/11/2014
// Target        : Java VM (1.8)
// -------------------------------------------

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
import org.sm.fraxion.concurrent.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.swing.dialogs.*;

/**
 * The <CODE>NrOfBlocksToUseChooser</CODE> class provides a dialog for selecting the number of CPU cores to use.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 06/11/2014
 */
public final class NrOfBlocksToUseChooser extends JDefaultDialog implements ChangeListener
{
	// internal datastructures
	private int fNrOfBlocksToUse;
	private JSlider fNrOfBlocksToUseSlider;
	private JLabel fNrOfBlocksToUseLabel;
	private JLabel fNrOfRegionsLabel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>NrOfBlocksToUseChooser</CODE> object.
	 *
	 * @param owner            the owning frame
	 * @param nrOfBlocksToUse  the initial number of CPU cores to use
	 */
	public NrOfBlocksToUseChooser(JFrame owner, int nrOfBlocksToUse)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {nrOfBlocksToUse},
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
		if (!fNrOfBlocksToUseSlider.getValueIsAdjusting()) {
			// extract value
			fNrOfBlocksToUse = (int) fNrOfBlocksToUseSlider.getValue();
			updateGUI();
		}
	}

	/**
	 * Returns the number of blocks to use.
	 *
	 * @return the number of blocks to use
	 */
	public int getSelectedNrOfBlocksToUse()
	{
		return fNrOfBlocksToUse;
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
		fNrOfBlocksToUse = (int) parameters[0];
		if (fNrOfBlocksToUse > IteratorController.kMaxNrOfBlocksToUse) {
			fNrOfBlocksToUse = IteratorController.kMaxNrOfBlocksToUse;
		}
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.MultiThreading.NrOfBlocksToUseTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			fNrOfBlocksToUseSlider = new JSlider(JSlider.HORIZONTAL);
			fNrOfBlocksToUseSlider.setInverted(false);
			fNrOfBlocksToUseSlider.setMinimum(1);
			fNrOfBlocksToUseSlider.setMaximum(IteratorController.kMaxNrOfBlocksToUse);
			fNrOfBlocksToUseSlider.setMinorTickSpacing(10);
			fNrOfBlocksToUseSlider.setMajorTickSpacing(5);
			fNrOfBlocksToUseSlider.setPaintTicks(true);
				Hashtable<Integer,JLabel> nrOfBlocksToUseSliderLabelTable = new Hashtable<Integer,JLabel>();
				for (int nrOfBlocksToUse = 0; nrOfBlocksToUse <= IteratorController.kMaxNrOfBlocksToUse; nrOfBlocksToUse += (IteratorController.kMaxNrOfBlocksToUse / 5)) {
					if (nrOfBlocksToUse == 0) {
						nrOfBlocksToUseSliderLabelTable.put(1,new JLabel("1"));
					}
					else {
						nrOfBlocksToUseSliderLabelTable.put(nrOfBlocksToUse,new JLabel(String.valueOf(nrOfBlocksToUse)));
					}
				}
			fNrOfBlocksToUseSlider.setLabelTable(nrOfBlocksToUseSliderLabelTable);
			fNrOfBlocksToUseSlider.setPaintLabels(true);
			fNrOfBlocksToUseSlider.setPaintTrack(true);
			fNrOfBlocksToUseSlider.setValue(fNrOfBlocksToUse);
			fNrOfBlocksToUseSlider.addChangeListener(this);
		mainPanel.add(fNrOfBlocksToUseSlider);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel lowerPanel = new JPanel();
			lowerPanel.setLayout(new BoxLayout(lowerPanel,BoxLayout.X_AXIS));
			fNrOfBlocksToUseLabel = new JLabel();
			lowerPanel.add(fNrOfBlocksToUseLabel);
			lowerPanel.add(Box.createHorizontalGlue());
		mainPanel.add(lowerPanel);

		mainPanel.add(Box.createVerticalStrut(5));

			JPanel regionsPanel = new JPanel();
			regionsPanel.setLayout(new BoxLayout(regionsPanel,BoxLayout.X_AXIS));
			fNrOfRegionsLabel = new JLabel();
			regionsPanel.add(fNrOfRegionsLabel);
			regionsPanel.add(Box.createHorizontalGlue());
		mainPanel.add(regionsPanel);
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fNrOfBlocksToUseSlider.setValue(fNrOfBlocksToUse);
		fNrOfBlocksToUseLabel.setText(I18NL10N.translate("text.MultiThreading.NrOfBlocksToUseLabel",String.valueOf(fNrOfBlocksToUse)));
		fNrOfRegionsLabel.setText(I18NL10N.translate("text.MultiThreading.CorrespondingNrOfScreenRegionsLabel",String.valueOf(fNrOfBlocksToUse * fNrOfBlocksToUse)));
	}
}
