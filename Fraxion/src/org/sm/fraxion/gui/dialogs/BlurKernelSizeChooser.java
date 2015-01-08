// ------------------------------------------
// Filename      : BlurKernelSizeChooser.java
// Author        : Sven Maerivoet
// Last modified : 21/12/2014
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

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.swing.dialogs.*;

/**
 * The <CODE>BlurKernelSizeChooser</CODE> class provides a dialog for selecting the size of a blur filter kernel.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 21/12/2014
 */
public final class BlurKernelSizeChooser extends JDefaultDialog implements ChangeListener
{
	// the minimum and maximum kernel size
	private static final int kMinKernelSize = 3;
	private static final int kMaxKernelSize = 50;

	// internal datastructures
	private int fKernelSize;
	private JSlider fKernelSizeSlider;
	private JLabel fKernelSizeLabel;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs an <CODE>BlurKernelSizeChooser</CODE> object.
	 *
	 * @param owner       the owning window
	 * @param kernelSize  the initial kernel size
	 */
	public BlurKernelSizeChooser(JFrame owner, int kernelSize)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {kernelSize},
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
		if (!fKernelSizeSlider.getValueIsAdjusting()) {
			// extract value
			fKernelSize = (int) fKernelSizeSlider.getValue();
			updateGUI();
		}
	}

	/**
	 * Returns the selected kernel size.
	 *
	 * @return the selected kernel size
	 */
	public int getSelectedKernelSize()
	{
		return fKernelSize;
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
		fKernelSize = (int) parameters[0];
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.Filters.Modify.BlurKernelSizeTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

			fKernelSizeSlider = new JSlider(JSlider.HORIZONTAL);
			fKernelSizeSlider.setInverted(false);
			fKernelSizeSlider.setMinimum(kMinKernelSize);
			fKernelSizeSlider.setMaximum(kMaxKernelSize);
			fKernelSizeSlider.setMinorTickSpacing(kMaxKernelSize / 10);
			fKernelSizeSlider.setMajorTickSpacing(kMaxKernelSize / 5);
			fKernelSizeSlider.setPaintTicks(true);
				Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
				for (int kernelSize = 0; kernelSize <= kMaxKernelSize; kernelSize += (kMaxKernelSize / 5)) {
					if (kernelSize == 0) {
						labelTable.put(kMinKernelSize,new JLabel(String.valueOf(kMinKernelSize)));
					}
					else {
						labelTable.put(kernelSize,new JLabel(String.valueOf(kernelSize)));
					}
				}
			fKernelSizeSlider.setLabelTable(labelTable);
			fKernelSizeSlider.setPaintLabels(true);
			fKernelSizeSlider.setPaintTrack(true);
			fKernelSizeSlider.setValue(fKernelSize);
			fKernelSizeSlider.addChangeListener(this);
		mainPanel.add(fKernelSizeSlider);

		mainPanel.add(Box.createVerticalStrut(10));

			JPanel lowerPanel = new JPanel();
			lowerPanel.setLayout(new BoxLayout(lowerPanel,BoxLayout.X_AXIS));
			fKernelSizeLabel = new JLabel();
			lowerPanel.add(fKernelSizeLabel);
			lowerPanel.add(Box.createHorizontalGlue());
		mainPanel.add(lowerPanel);
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fKernelSizeSlider.setValue(fKernelSize);
		fKernelSizeLabel.setText(I18NL10N.translate("text.Filters.Modify.BlurKernelSizeLabel",String.valueOf(fKernelSize),String.valueOf(fKernelSize * fKernelSize)));
	}
}
