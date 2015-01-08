// ----------------------------------------------------
// Filename      : SectorDecompositionRangeChooser.java
// Author        : Sven Maerivoet
// Last modified : 06/11/2014
// Target        : Java VM (1.8)
// ----------------------------------------------------

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

/**
 * The <CODE>SectorDecompositionRangeChooser</CODE> class provides a dialog for selecting the sector decomposition range.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 06/11/2014
 */
public final class SectorDecompositionRangeChooser extends JDefaultDialog implements ChangeListener
{
	// the minimum and maximum sector decomposition range
	private static final int kMinSectorDecompositionRange = 2;
	private static final int kMaxSectorDecompositionRange = 1000;

	// internal datastructures
	private int fSectorDecompositionRange;
	private JSlider fSectorDecompositionRangeSlider;
	private JLabel fSectorDecompositionRangeLabel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>SectorDecompositionRangeChooser</CODE> object.
	 *
	 * @param owner                     the owning frame
	 * @param sectorDecompositionRange  the initial sector decomposition range
	 */
	public SectorDecompositionRangeChooser(JFrame owner, int sectorDecompositionRange)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {sectorDecompositionRange},
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
		if (!fSectorDecompositionRangeSlider.getValueIsAdjusting()) {
			// extract value
			fSectorDecompositionRange = (int) fSectorDecompositionRangeSlider.getValue();
			updateGUI();
		}
	}

	/**
	 * Returns the selected sector decomposition range.
	 *
	 * @return the selected sector decomposition range
	 */
	public int getSelectedSectorDecompositionRange()
	{
		return fSectorDecompositionRange;
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
		fSectorDecompositionRange = (int) parameters[0];
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.ColorMap.SectorDecompositionRangeTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			fSectorDecompositionRangeSlider = new JSlider(JSlider.HORIZONTAL);
			fSectorDecompositionRangeSlider.setInverted(false);
			fSectorDecompositionRangeSlider.setMinimum(kMinSectorDecompositionRange);
			fSectorDecompositionRangeSlider.setMaximum(kMaxSectorDecompositionRange);
			fSectorDecompositionRangeSlider.setMinorTickSpacing(kMaxSectorDecompositionRange / 10);
			fSectorDecompositionRangeSlider.setMajorTickSpacing(kMaxSectorDecompositionRange / 5);
			fSectorDecompositionRangeSlider.setPaintTicks(true);
				Hashtable<Integer,JLabel> sectorDecompositionRangeSliderLabelTable = new Hashtable<Integer,JLabel>();
				for (int sectorDecompositionRange = 0; sectorDecompositionRange <= kMaxSectorDecompositionRange; sectorDecompositionRange += (kMaxSectorDecompositionRange / 5)) {
					if (sectorDecompositionRange == 0) {
						sectorDecompositionRangeSliderLabelTable.put(kMinSectorDecompositionRange,new JLabel(String.valueOf(kMinSectorDecompositionRange)));
					}
					else {
						sectorDecompositionRangeSliderLabelTable.put(sectorDecompositionRange,new JLabel(String.valueOf(sectorDecompositionRange)));
					}
				}
				fSectorDecompositionRangeSlider.setLabelTable(sectorDecompositionRangeSliderLabelTable);
			fSectorDecompositionRangeSlider.setPaintLabels(true);
			fSectorDecompositionRangeSlider.setPaintTrack(true);
			fSectorDecompositionRangeSlider.setValue(fSectorDecompositionRange);
			fSectorDecompositionRangeSlider.addChangeListener(this);
		mainPanel.add(fSectorDecompositionRangeSlider);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel lowerPanel = new JPanel();
			lowerPanel.setLayout(new BoxLayout(lowerPanel,BoxLayout.X_AXIS));
			fSectorDecompositionRangeLabel = new JLabel();
			lowerPanel.add(fSectorDecompositionRangeLabel);
			lowerPanel.add(Box.createHorizontalGlue());
		mainPanel.add(lowerPanel);
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fSectorDecompositionRangeSlider.setValue(fSectorDecompositionRange);
		fSectorDecompositionRangeLabel.setText(I18NL10N.translate("text.ColorMap.SectorDecompositionRangeLabel",String.valueOf(fSectorDecompositionRange)));
	}
}
