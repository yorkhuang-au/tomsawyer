//
// Tom Sawyer Software
// Copyright 1992 - 2015
// All rights reserved.
//
// www.tomsawyer.com
//


import com.tomsawyer.integrator.TSIntegratorException;
import com.tomsawyer.licensing.TSLicenseManager;
import com.tomsawyer.model.TSModel;
import com.tomsawyer.model.defaultmodel.TSDefaultModel;
import com.tomsawyer.project.TSProject;
import com.tomsawyer.project.xml.TSProjectXMLReader;
import com.tomsawyer.util.shared.TSUserAgent;
import com.tomsawyer.util.swing.TSTomSawyerApplications;
import com.tomsawyer.view.drawing.swing.TSSwingDrawingView;
import java.io.IOException;

import javax.swing.*;




/**
 * This application opens the project file for the RDF Incremental
 * Integrator Tutorial. It displays a subset of the Network Map in a drawing
 * view as described by the project file. It provides two custom actions,
 * one to retrieve network devices by name and one to retrieve the adjacent
 * devices of selected devices.
 */
public class GEMSApplication extends JFrame
{
	/**
	 * Constructor.
	 */
	public GEMSApplication()
	{
		// Set the current user and initialize Tom Sawyer Licensing. This is
		// required before using any Tom Sawyer classes.

		TSLicenseManager.setUserName(System.getProperty("user.name"));
		TSLicenseManager.initTSSLicensing();
	}


	/**
	 * This method loads the project file.
	 *
	 * @param filename The path of the project file to load.
	 * @throws java.io.IOException exception thrown if there is a problem
	 * reading the project file.
	 */
	private void loadProject(String filename) throws IOException
	{
		this.networkProject = new TSProject();
		TSProjectXMLReader reader = new TSProjectXMLReader(filename);
		reader.setProject(this.networkProject);
		reader.read();
	}


	/**
	 * This method creates and initializes a data model.
	 */
	private void initModel()
	{
		// Create an instance of a model to be used by the integrator and the
		// drawing view. Then initialize the model with the appropriate
		// schema from the project

		this.networkDataModel = new TSDefaultModel();

		this.networkProject.getSchema("GEMS").initModel(
			this.networkDataModel);
	}


	/**
	 * This method initializes the user interface by placing the Network Map
	 * drawing view in the content pane of the application frame.
	 */
	private void initGUI()
	{
		TSTomSawyerApplications.setLookAndFeel(this);
		TSTomSawyerApplications.setIcon(this);

		this.setSize(800, 600);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("GEMS-Tom Sawyer");

		this.networkMap =
			(TSSwingDrawingView) this.networkProject.newView(
				"GEMS",
				"GEMS",
				TSUserAgent.Swing);

		this.networkMap.setModel(this.networkDataModel);
		this.getContentPane().add(this.networkMap.getComponent());
	}



	/**
	 * Main method of the application.
	 *
	 * @param args program arguments
	 * @throws java.io.IOException exception thrown if there was a problem
	 * reading a file.
	 * @throws com.tomsawyer.integrator.TSIntegratorException exception thrown
	 * if the integrator has a problem loading the data into the model.
	 */
	public static void main(String[] args)
		throws IOException, TSIntegratorException
	{
		GEMSApplication application =
			new GEMSApplication();

		application.loadProject("project/GEMS.tsp");
		application.initModel();
		application.initGUI();
		application.setVisible(true);
	}


	private TSProject networkProject;

	private TSModel networkDataModel;

	private TSSwingDrawingView networkMap;
}


