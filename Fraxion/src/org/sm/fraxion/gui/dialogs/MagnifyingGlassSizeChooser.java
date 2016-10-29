// -----------------------------------------------
// Filename      : MagnifyingGlassSizeChooser.java
// Author        : Sven Maerivoet
// Last modified : 24/09/2014
// Target        : Java VM (1.8)
// -----------------------------------------------

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

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.swing.dialogs.*;

/**
 * The <CODE>MagnifyingGlassSizeChooser</CODE> class provides a dialog for selecting the size of the magnifying glass.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 24/09/2014
 */
public final class MagnifyingGlassSizeChooser extends JDefaultDialog implements ChangeListener
{
	/**
	 * The default magnifying glass region.
	 */
	public static final int kDefaultRegion = 100;

	/**
	 * The default magnifying glass size.
	 */
	public static final int kDefaultSize = 400;

	// the minimum and maximum magnifying glass regions
	private static final int kMinRegion = 10;
	private static final int kMaxRegion = 250;

	// the minimum and maximum magnifying glass sizes
	private static final int kMinSize = 50;
	private static final int kMaxSize = 500;

	// internal datastructures
	private int fRegion;
	private int fSize;
	private JSlider fRegionSlider;
	private JSlider fSizeSlider;
	private JLabel fRegionLabel;
	private JLabel fSizeLabel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>MagnifyingGlassSizeChooser</CODE> object.
	 *
	 * @param owner   the owning frame
	 * @param region  the size of the region
	 * @param size    the size of the magnifying glass
	 */
	public MagnifyingGlassSizeChooser(JFrame owner, int region, int size)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {region,size},
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
		if (e.getSource() == fRegionSlider) {
			if (!fRegionSlider.getValueIsAdjusting()) {
				// extract value
				fRegion = (int) fRegionSlider.getValue();
				if (fRegion > fSize) {
					fSize = fRegion;
				}
				updateGUI();
			}
		}
		else if (e.getSource() == fSizeSlider) {
			if (!fSizeSlider.getValueIsAdjusting()) {
				// extract value
				fSize = (int) fSizeSlider.getValue();
				if (fSize < fRegion) {
					fRegion = fSize;
				}
				updateGUI();
			}
		}
	}

	/**
	 * Returns the selected region.
	 *
	 * @return the selected region
	 */
	public int getSelectedRegion()
	{
		return fRegion;
	}

	/**
	 * Returns the selected size.
	 *
	 * @return the selected size
	 */
	public int getSelectedSize()
	{
		return fSize;
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
		fRegion = (Integer) parameters[0];
		fSize = (Integer) parameters[1];
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.Fractal.MagnifyingGlassSizeTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			fRegionLabel = new JLabel();
			fRegionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(fRegionLabel);

			fRegionSlider = new JSlider(JSlider.HORIZONTAL);
			fRegionSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
			fRegionSlider.setMinimum(kMinRegion);
			fRegionSlider.setMaximum(kMaxRegion);
			fRegionSlider.setMinorTickSpacing(kMaxRegion / 10);
			fRegionSlider.setMajorTickSpacing(kMaxRegion / 5);
			fRegionSlider.setPaintTicks(true);
			fRegionSlider.setPaintLabels(false);
			fRegionSlider.setPaintTrack(true);
			fRegionSlider.setValue(fRegion);
			fRegionSlider.addChangeListener(this);
		mainPanel.add(fRegionSlider);

		mainPanel.add(Box.createVerticalStrut(10));

			fSizeLabel = new JLabel();
			fSizeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(fSizeLabel);

			fSizeSlider = new JSlider(JSlider.HORIZONTAL);
			fSizeSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
			fSizeSlider.setMinimum(kMinSize);
			fSizeSlider.setMaximum(kMaxSize);
			fSizeSlider.setMinorTickSpacing(kMaxSize / 10);
			fSizeSlider.setMajorTickSpacing(kMaxSize / 5);
			fSizeSlider.setPaintTicks(true);
			fSizeSlider.setPaintLabels(false);
			fSizeSlider.setPaintTrack(true);
			fSizeSlider.setValue(fSize);
			fSizeSlider.addChangeListener(this);
		mainPanel.add(fSizeSlider);
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fRegionSlider.setValue(fRegion);
		fRegionLabel.setText(I18NL10N.translate("text.Fractal.MagnifyingGlassSizeRegionLabel",String.valueOf(fRegion)));
		fSizeSlider.setValue(fSize);
		fSizeLabel.setText(I18NL10N.translate("text.Fractal.MagnifyingGlassSizeSizeLabel",String.valueOf(fSize)));
	}
}
