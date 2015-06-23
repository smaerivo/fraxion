// ------------------------------
// Filename      : AboutBox.java
// Author        : Sven Maerivoet
// Last modified : 23/06/2015
// Target        : Java VM (1.8)
// ------------------------------

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

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import org.sm.smtools.application.util.*;
import org.sm.smtools.exceptions.*;
import org.sm.smtools.swing.dialogs.*;

/**
 * This class contains the application's about box.
 * <P>
 * <B>Note that this class cannot be subclassed!</B>
 * 
 * @author  Sven Maerivoet
 * @version 23/06/2015
 */
public final class AboutBox extends JAboutBox
{
	// the application's version number
	private static final String kVersionNumber = "1.3";

	/****************
	 * CONSTRUCTORS *
	 ****************/

	/**
	 * @param owner      the owner
	 * @param resources  a reference to the JAR resources
	 */
	public AboutBox(JFrame owner, JARResources resources)
	{
		super(owner,resources);
	}

	/*********************
	 * PROTECTED METHODS *
	 *********************/

	/**
	 * @return the logo
	 */
	@Override
	protected JLabel setupLogo()
	{
		try {
			return (new JLabel(new ImageIcon(fResources.getImage("application-resources/images/aboutbox-banner.jpg"))));
		}
		catch (FileDoesNotExistException exc) {
			return null;
		}
	}

	/**
	 * @return the logo's position
	 */
	@Override
	protected ELogoPosition setupLogoPosition()
	{
		return JAboutBox.ELogoPosition.kTop;
	}

	/**
	 * @return the about text
	 */
	@Override
	protected String setupAboutText()
	{
		return
		("<B>Fraxion v" + kVersionNumber + "</B><BR />" +
			"Copyright 2003-2015 Sven Maerivoet");
	}

	/**
	 * @return the copyright content
	 */
	@Override
	protected StringBuilder setupCopyrightContent()
	{
		try {
			return fResources.getText("application-resources/licence/copyright.txt");
		}
		catch (FileDoesNotExistException exc) {
			return null;
		}
	}

	/**
	 * @return the licence content
	 */
	@Override
	protected StringBuilder setupLicenceContent()
	{
		try {
			return fResources.getText("application-resources/licence/apache-licence.txt");
		}
		catch (FileDoesNotExistException exc) {
			return null;
		}
	}

	/**
	 * @return the affiliations labels
	 */
	@Override
	protected ArrayList<JLabel> setupAffiliationsLabels()
	{
		ArrayList<JLabel> affiliationsLabels = new ArrayList<JLabel>();
		JLabel affiliationLabel = null;

			affiliationLabel = new JLabel("",SwingConstants.CENTER);
			try {
				affiliationLabel.setIcon(new ImageIcon(fResources.getImage("application-resources/images/aboutbox-banner.jpg")));
			}
			catch (FileDoesNotExistException exc) {
			}
			affiliationLabel.setToolTipText(I18NL10N.translate("tooltip.AboutBox.ClickForBrowser"));
		affiliationsLabels.add(affiliationLabel);

			affiliationLabel = new JLabel(
				"<html>" +
					"<b>Sven Maerivoet</b>" +
				"</html>");
		affiliationsLabels.add(affiliationLabel);

			affiliationLabel = new JLabel(
				"<html>" +
					"E-mail: sven.maerivoet@gmail.com" +
				"</html>");
			affiliationLabel.setToolTipText(I18NL10N.translate("tooltip.AboutBox.ClickForEmailClient"));
		affiliationsLabels.add(affiliationLabel);

			affiliationLabel = new JLabel(
				"<html>" +
					"Website: http://fraxion.maerivoet.org/" +
				"</html>");
			affiliationLabel.setToolTipText(I18NL10N.translate("tooltip.AboutBox.ClickForBrowser"));
		affiliationsLabels.add(affiliationLabel);

		for (JLabel label : affiliationsLabels) { 
			label.setHorizontalTextPosition(SwingConstants.CENTER);
			label.setHorizontalAlignment(SwingConstants.LEFT);
			label.setVerticalTextPosition(SwingConstants.BOTTOM);
			label.setVerticalAlignment(SwingConstants.BOTTOM);
		}

		// install mouse listeners
		MouseListener browserLauncher = new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				JDesktopAccess.executeBrowseApplication("http://www.maerivoet.org/");
			} 
			@Override
			public void mouseEntered(MouseEvent e)
			{
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			} 
			@Override
			public void mouseExited(MouseEvent e)
			{
				setCursor(Cursor.getDefaultCursor());							
			}
			@Override
			public void mousePressed(MouseEvent e) { }
			@Override
			public void mouseReleased(MouseEvent e) { } 
		};

		MouseListener emailLauncher = new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				JDesktopAccess.executeMailApplication("sven.maerivoet@gmail.com","Request for information on Mandelbrot / Julia Explorer");
			}
			@Override
			public void mouseEntered(MouseEvent e)
			{
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			@Override
			public void mouseExited(MouseEvent e)
			{
				setCursor(Cursor.getDefaultCursor());
			}
			@Override
			public void mousePressed(MouseEvent e) { }
			@Override
			public void mouseReleased(MouseEvent e) { }
		};

		affiliationsLabels.get(0).addMouseListener(browserLauncher);
		affiliationsLabels.get(2).addMouseListener(emailLauncher);
		affiliationsLabels.get(3).addMouseListener(browserLauncher);

		return affiliationsLabels;
	}

	/**
	 * @return a <CODE>String</CODE> describing the application's used libraries
	 */
	protected String setupUsedLibrariesDescriptions()
	{
		return "JavaHelp 2.0";
	}
}
