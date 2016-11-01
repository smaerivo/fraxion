// --------------------------------------------------------------
// Filename      : BifurcationOutlierPercentileOffsetChooser.java
// Author        : Sven Maerivoet
// Last modified : 29/10/2016
// Target        : Java VM (1.8)
// --------------------------------------------------------------

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

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.swing.dialogs.*;
import org.sm.smtools.util.*;

/**
 * The <CODE>BifurcationOutlierPercentileOffsetChooser</CODE> class provides a dialog for selecting the bifurcation outlier percentile offset.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 29/10/2016
 */
public final class BifurcationOutlierPercentileOffsetChooser extends JDefaultDialog implements ChangeListener
{
	// the maximum bifurcation oulier percentile offset (expressed in tenths)
	private static final int kMaxBifurcationOutlierPercentileOffset = 49 * 10;

	// internal datastructures
	private int fBifurcationOutlierPercentileOffset;
	private JSlider fBifurcationOutlierPercentileOffsetSlider;
	private JLabel fBifurcationOutlierPercentileOffsetLabel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>BifurcationOutlierPercentileOffsetChooser</CODE> object.
	 *
	 * @param owner                               the owning frame
	 * @param bifurcationOutlierPercentileOffset  the initial bifurcation outlier percentile offset
	 */
	public BifurcationOutlierPercentileOffsetChooser(JFrame owner, double bifurcationOutlierPercentileOffset)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {bifurcationOutlierPercentileOffset},
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
		if (!fBifurcationOutlierPercentileOffsetSlider.getValueIsAdjusting()) {
			// extract value
			fBifurcationOutlierPercentileOffset = (int) fBifurcationOutlierPercentileOffsetSlider.getValue();
			updateGUI();
		}
	}

	/**
	 * Returns the bifurcation outlier percentile offset.
	 *
	 * @return the bifurcation outlier percentile offset
	 */
	public double getSelectedBifurcationOutlierPercentileOffset()
	{
		return (fBifurcationOutlierPercentileOffset / 10.0);
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
		 fBifurcationOutlierPercentileOffset = (int) Math.round(((double) parameters[0]) * 10.0);
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.Fractal.BifurcationOutlierPercentileOffsetTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			fBifurcationOutlierPercentileOffsetSlider = new JSlider(JSlider.HORIZONTAL);
			fBifurcationOutlierPercentileOffsetSlider.setInverted(false);
			fBifurcationOutlierPercentileOffsetSlider.setMinimum(0);
			fBifurcationOutlierPercentileOffsetSlider.setMaximum(kMaxBifurcationOutlierPercentileOffset);
			fBifurcationOutlierPercentileOffsetSlider.setMinorTickSpacing(kMaxBifurcationOutlierPercentileOffset / 10);
			fBifurcationOutlierPercentileOffsetSlider.setMajorTickSpacing(kMaxBifurcationOutlierPercentileOffset / 5);
			fBifurcationOutlierPercentileOffsetSlider.setPaintTicks(true);
				Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
				for (int bifurcationOutlierPercentileOffset = 0; bifurcationOutlierPercentileOffset <= kMaxBifurcationOutlierPercentileOffset; bifurcationOutlierPercentileOffset += (kMaxBifurcationOutlierPercentileOffset / 5)) {
					labelTable.put(bifurcationOutlierPercentileOffset,new JLabel(StringTools.convertDoubleToString((double) bifurcationOutlierPercentileOffset / 10.0,1)));
				}
			fBifurcationOutlierPercentileOffsetSlider.setLabelTable(labelTable);
			fBifurcationOutlierPercentileOffsetSlider.setPaintLabels(true);
			fBifurcationOutlierPercentileOffsetSlider.setPaintTrack(true);
			fBifurcationOutlierPercentileOffsetSlider.setValue(fBifurcationOutlierPercentileOffset);
			fBifurcationOutlierPercentileOffsetSlider.addChangeListener(this);
		mainPanel.add(fBifurcationOutlierPercentileOffsetSlider);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel lowerPanel = new JPanel();
			lowerPanel.setLayout(new BoxLayout(lowerPanel,BoxLayout.X_AXIS));
			fBifurcationOutlierPercentileOffsetLabel = new JLabel();
			lowerPanel.add(fBifurcationOutlierPercentileOffsetLabel);
			lowerPanel.add(Box.createHorizontalGlue());
		mainPanel.add(lowerPanel);
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fBifurcationOutlierPercentileOffsetSlider.setValue(fBifurcationOutlierPercentileOffset);
		fBifurcationOutlierPercentileOffsetLabel.setText(I18NL10N.translate("text.Fractal.BifurcationOutlierPercentileOffset",StringTools.convertDoubleToString(getSelectedBifurcationOutlierPercentileOffset(),1)));
	}
}
