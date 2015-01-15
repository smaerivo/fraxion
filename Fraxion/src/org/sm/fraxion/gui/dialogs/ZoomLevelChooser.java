// -------------------------------------
// Filename      : ZoomLevelChooser.java
// Author        : Sven Maerivoet
// Last modified : 10/10/2014
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

import java.awt.event.*;
import javax.swing.*;
import org.sm.fraxion.gui.*;
import org.sm.fraxion.gui.util.ZoomStack;
import org.sm.smtools.application.util.*;
import org.sm.smtools.swing.dialogs.*;

/**
 * The <CODE>ZoomLevelChooser</CODE> class provides a dialog for selecting a zoom level.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 *
 * @author  Sven Maerivoet
 * @version 10/10/2014
 */
public final class ZoomLevelChooser extends JDefaultDialog implements ActionListener
{
	// action commands
	private static final String kActionCommandSetMaxZoomLevelPrefix = "maxZoomLevel";

	// internal datastructures
	private ZoomStack fZoomStack;
	private int fMaxZoomLevel;
	private int fZoomLevel;
	private ButtonGroup fZoomLevelRadioButtonGroup;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * Constructs a <CODE>ZoomLevelChooser</CODE> object.
	 *
	 * @param owner      the owning frame
	 * @param zoomStack  a reference to the zoom stack
	 */
	public ZoomLevelChooser(JFrame owner, ZoomStack zoomStack)
	{
		super(owner,
			JDefaultDialog.EModality.kModal,
			JDefaultDialog.ESize.kFixedSize,
			JDefaultDialog.EType.kOkCancel,
			new Object[] {zoomStack},
			JDefaultDialog.EActivation.kImmediately);
	}

	/******************
	 * PUBLIC METHODS *
	 ******************/

	// the action-listener
	/**
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);

		String command = e.getActionCommand();

		if (command.startsWith(kActionCommandSetMaxZoomLevelPrefix)) {
			// extract value
			fZoomLevel = Integer.parseInt(command.substring(command.indexOf(".") + 1));
		}
	}

	/**
	 * Returns the selected zoom level.
	 *
	 * @return the selected zoom level
	 */
	public int getSelectedZoomLevel()
	{
		return fZoomLevel;
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
		fZoomStack = (ZoomStack) parameters[0];
		fMaxZoomLevel = fZoomStack.getZoomLevel();
		fZoomLevel = fMaxZoomLevel;
	}

	/**
	 * Returns the dialog box's title.
	 */
	@Override
	protected java.lang.String setupWindowTitle()
	{
		return I18NL10N.translate("text.Navigation.ZoomLevelTitle");
	}

	/**
	 * Creates the dialog box content area.
	 */
	@Override
	protected void setupMainPanel(JPanel mainPanel)
	{
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

		mainPanel.add(Box.createVerticalStrut(10));
		fZoomLevelRadioButtonGroup = new ButtonGroup();
		for (int zoomLevel = 1; zoomLevel <= fMaxZoomLevel; ++zoomLevel) {
			createZoomLevelRadioButton(zoomLevel,mainPanel,zoomLevel == fMaxZoomLevel);
			mainPanel.add(Box.createVerticalStrut(5));
		}
		mainPanel.add(Box.createVerticalStrut(10));
	}

	/*******************
	 * PRIVATE METHODS *
	 *******************/

	/**
	 * @param zoomLevel   -
	 * @param panel       -
	 * @param isSelected  -
	 */
	private void createZoomLevelRadioButton(int zoomLevel, JPanel panel, boolean isSelected)
	{
			JRadioButton zoomLevelRadioButton =
				new JRadioButton(
					I18NL10N.translate("text.Navigation.ZoomLevelSelected",
						String.valueOf(zoomLevel),
						fZoomStack.getP1(zoomLevel).toString(),
						fZoomStack.getP2(zoomLevel).toString()));
			fZoomLevelRadioButtonGroup.add(zoomLevelRadioButton);
			zoomLevelRadioButton.setSelected(isSelected);
			zoomLevelRadioButton.setActionCommand(kActionCommandSetMaxZoomLevelPrefix + "." + String.valueOf(zoomLevel));
			zoomLevelRadioButton.addActionListener(this);
		panel.add(zoomLevelRadioButton);
	}
}
