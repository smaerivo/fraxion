// -------------------------------------
// Filename      : InsetSizeChooser.java
// Author        : Sven Maerivoet
// Last modified : 24/09/2014
// Target        : Java VM (1.8)
// -------------------------------------

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
 * The <CODE>InsetSizeChooser</CODE> class provides a dialog for selecting the size of the inset.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 24/09/2014
 */
public final class InsetSizeChooser extends JDefaultDialog implements ChangeListener
{
	// the minimum and maximum inset sizes
	private static final int kMinInsetSize = 10;
	private static final int kMaxInsetSize = 90;

	// internal datastructures
	private int fInsetSize;
	private JSlider fInsetSizeSlider;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs an <CODE>InsetSizeChooser</CODE> object.
	 *
	 * @param owner             the owning frame
	 * @param initialInsetSize  the initial inset size
	 */
	public InsetSizeChooser(JFrame owner, int initialInsetSize)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {initialInsetSize},
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
		if (!fInsetSizeSlider.getValueIsAdjusting()) {
			fInsetSize = (int) fInsetSizeSlider.getValue();
			updateGUI();
		}
	}

	/**
	 * Returns the selected inset size.
	 *
	 * @return the selected inset size
	 */
	public int getSelectedInsetSize()
	{
		return fInsetSize;
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
		fInsetSize = (Integer) parameters[0];
		if (fInsetSize > kMaxInsetSize) {
			fInsetSize = kMaxInsetSize;
		}
		else if (fInsetSize < kMinInsetSize) {
			fInsetSize = kMinInsetSize;
		}
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.Fractal.InsetSizeTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));

		mainPanel.add(Box.createHorizontalStrut(10));

		fInsetSizeSlider = new JSlider(JSlider.HORIZONTAL);
			fInsetSizeSlider.setInverted(false);
			fInsetSizeSlider.setMinimum(kMinInsetSize);
			fInsetSizeSlider.setMaximum(kMaxInsetSize);
			fInsetSizeSlider.setMinorTickSpacing((kMaxInsetSize - kMinInsetSize) / 16);
			fInsetSizeSlider.setMajorTickSpacing((kMaxInsetSize - kMinInsetSize) / 2);
			fInsetSizeSlider.setPaintTicks(true);
				Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
				labelTable.put(kMinInsetSize,new JLabel(I18NL10N.translate("text.Fractal.InsetSizeSmall")));
				labelTable.put((kMinInsetSize + kMaxInsetSize) / 2,new JLabel(I18NL10N.translate("text.Fractal.InsetSizeMedium")));
				labelTable.put(kMaxInsetSize,new JLabel(I18NL10N.translate("text.Fractal.InsetSizeLarge")));
			fInsetSizeSlider.setLabelTable(labelTable);
			fInsetSizeSlider.setPaintLabels(true);
			fInsetSizeSlider.setPaintTrack(true);
			fInsetSizeSlider.setValue(fInsetSize);
			fInsetSizeSlider.addChangeListener(this);
		mainPanel.add(fInsetSizeSlider);

		mainPanel.add(Box.createHorizontalStrut(10));
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fInsetSizeSlider.setValue(fInsetSize);
	}
}
