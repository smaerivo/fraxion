// --------------------------------------------------
// Filename      : OrbitAnalysesPanelSizeChooser.java
// Author        : Sven Maerivoet
// Last modified : 24/09/2014
// Target        : Java VM (1.8)
// --------------------------------------------------

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
 * The <CODE>OrbitAnalysesPanelSizeChooser</CODE> class provides a dialog for selecting the size of the orbit analyses panel.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 24/09/2014
 */
public final class OrbitAnalysesPanelSizeChooser extends JDefaultDialog implements ChangeListener
{
	// the minimum and maximum inset sizes
	private static final int kMinOrbitAnalysesPanelSize = 10;
	private static final int kMaxOrbitAnalysesPanelSize = 95;

	// internal datastructures
	private int fOrbitAnalysesPanelSize;
	private JSlider fOrbitAnalysesPanelSizeSlider;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs an <CODE>OrbitAnalysesPanelSizeChooser</CODE> object.
	 *
	 * @param owner                          the owning frame
	 * @param initialOrbitAnalysesPanelSize  the initial orbit analyses panel size
	 */
	public OrbitAnalysesPanelSizeChooser(JFrame owner, int initialOrbitAnalysesPanelSize)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {initialOrbitAnalysesPanelSize},
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
		if (!fOrbitAnalysesPanelSizeSlider.getValueIsAdjusting()) {
			// extract value
			fOrbitAnalysesPanelSize = (int) fOrbitAnalysesPanelSizeSlider.getValue();
			updateGUI();
		}
	}

	/**
	 * Returns the selected orbit analyses panel size.
	 *
	 * @return the selected orbit analyses panel size
	 */
	public int getSelectedOrbitAnalysesPanelSize()
	{
		return fOrbitAnalysesPanelSize;
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
		fOrbitAnalysesPanelSize = (Integer) parameters[0];
		if (fOrbitAnalysesPanelSize > kMaxOrbitAnalysesPanelSize) {
			fOrbitAnalysesPanelSize = kMaxOrbitAnalysesPanelSize;
		}
		else if (fOrbitAnalysesPanelSize < kMinOrbitAnalysesPanelSize) {
			fOrbitAnalysesPanelSize = kMinOrbitAnalysesPanelSize;
		}
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.Fractal.OrbitAnalysesPanelSizeTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));

		mainPanel.add(Box.createHorizontalStrut(10));

		fOrbitAnalysesPanelSizeSlider = new JSlider(JSlider.HORIZONTAL);
			fOrbitAnalysesPanelSizeSlider.setInverted(false);
			fOrbitAnalysesPanelSizeSlider.setMinimum(kMinOrbitAnalysesPanelSize);
			fOrbitAnalysesPanelSizeSlider.setMaximum(kMaxOrbitAnalysesPanelSize);
			fOrbitAnalysesPanelSizeSlider.setMinorTickSpacing((kMaxOrbitAnalysesPanelSize - kMinOrbitAnalysesPanelSize) / 16);
			fOrbitAnalysesPanelSizeSlider.setMajorTickSpacing((kMaxOrbitAnalysesPanelSize - kMinOrbitAnalysesPanelSize) / 2);
			fOrbitAnalysesPanelSizeSlider.setPaintTicks(true);
				Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
				labelTable.put(kMinOrbitAnalysesPanelSize,new JLabel(I18NL10N.translate("text.Fractal.OrbitAnalysesPanelSizeSmall")));
				labelTable.put((kMinOrbitAnalysesPanelSize + kMaxOrbitAnalysesPanelSize) / 2,new JLabel(I18NL10N.translate("text.Fractal.OrbitAnalysesPanelSizeMedium")));
				labelTable.put(kMaxOrbitAnalysesPanelSize,new JLabel(I18NL10N.translate("text.Fractal.OrbitAnalysesPanelSizeLarge")));
			fOrbitAnalysesPanelSizeSlider.setLabelTable(labelTable);
			fOrbitAnalysesPanelSizeSlider.setPaintLabels(true);
			fOrbitAnalysesPanelSizeSlider.setPaintTrack(true);
			fOrbitAnalysesPanelSizeSlider.setValue(fOrbitAnalysesPanelSize);
			fOrbitAnalysesPanelSizeSlider.addChangeListener(this);
		mainPanel.add(fOrbitAnalysesPanelSizeSlider);

		mainPanel.add(Box.createHorizontalStrut(10));
	}

	/**
	 */
	@Override
	protected void updateGUI()
	{
		fOrbitAnalysesPanelSizeSlider.setValue(fOrbitAnalysesPanelSize);
	}
}
