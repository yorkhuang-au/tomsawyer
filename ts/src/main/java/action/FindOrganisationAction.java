//
// Tom Sawyer Software
// Copyright 2007 - 2015
// All rights reserved.
//
// www.tomsawyer.com
//


package action;


import com.tomsawyer.integrator.TSIntegrator;
import com.tomsawyer.interactive.command.editing.TSEIncrementalLayoutCommand;
import com.tomsawyer.model.TSModel;
import com.tomsawyer.model.TSModelElement;
import com.tomsawyer.view.drawing.TSModelDrawingView;
import com.tomsawyer.model.schema.TSModelElementType;
import com.tomsawyer.integrator.jdbc.TSJDBCAttributedObjectDataSourceLocation;
//import com.tomsawyer.model.TSJDBCAttributedObjectDataSourceLocation;
import java.util.Collection;
import java.util.List;


/**
 * This class provides the implementation for loading elements that match a user
 * specified value.
 * <p/>
 * It works as follows:
 * - It pops up a dialog
 * - The value entered is used to set the value of the _Context attribute in the model.
 * - It then performs an update on the integrator whose name matches the
 * name of the action in the toolbar. The update is done after an integrator reset.
 * - The integrator must have a filter that compares the element value with the
 * "_Context" value.
 * - The integrator name must match the name of teh toolbar or context menu action.
 */
public class FindOrganisationAction
{
	public FindOrganisationAction(TSModelDrawingView drawingView, String name)
	{
		this.drawingView = drawingView;
		this.actionName = name;
	}


	/**
	 * This method returns the drawing view.
	 * @return The drawing view.
	 */
	public TSModelDrawingView getDrawingView()
	{
		return this.drawingView;
	}


	/**
	 * The main method to be performed during the execution of this action.
	 * @param name The search text user entered.
	 */
	public void onAction(String name)
	{
		System.out.println("In find org");
		// If a string was returned, say so.

		if ((name != null) && (name.length() > 0))
		{
			System.out.println( name);
			final TSModel model = this.getDrawingView().getModel();
			try
			{
				if (this.integrator == null)
				{
					this.integrator =
						this.getDrawingView().getModule().getIntegrator(this.actionName);
				}

				if (this.integrator != null)
				{
					this.getDrawingView().clearSelection(true);
					this.getDrawingView().clear();

//					final TSModel model = this.getDrawingView().getModel();
//					model.clear();

					model.getEventManager().setFireEvents(false);

					this.integrator.reset();
					this.integrator.setModel(this.getDrawingView().getModel());
					
					// Get element type for which to change the query.

					TSModelElementType deviceElementType = this.integrator.getSchema().getModelElementType("Organisation");

					// Get location object.

					TSJDBCAttributedObjectDataSourceLocation deviceLocation = (TSJDBCAttributedObjectDataSourceLocation) this.integrator.getBindingManager().getBinding(deviceElementType).getDataSourceLocation();

					// Change the query.
					System.out.println(FIND_ORGANISATION_SQL.replace("$ORG_NAME$", name.toUpperCase().trim()));

					deviceLocation.setReference(FIND_ORGANISATION_SQL.replace("$ORG_NAME$", name.toUpperCase().trim()) );

					// Update integrator.
					this.integrator.update();
					this.getDrawingView().updateView();
					
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				model.getEventManager().setFireEvents(true);
			}
		}
	}


	/**
	 * This method is called when no results were found.
	 */
	protected void showNoResults()
	{
	}
	private TSModelDrawingView drawingView;

	private String actionName;

	private TSIntegrator integrator;
	private final String FIND_ORGANISATION_SQL =
		"SELECT * FROM (SELECT O.*, G.GROUP_ID FROM ( " +
		"SELECT  " +
		  "ORGANISATION_NO, " +
		  "MAX(ORG_NAME) AS ORG_NAME, " +
		  "MAX(ORG_TYPE) AS ORG_TYPE, " +
		  "MAX(ORG_STATUS) AS ORG_STATUS, " +
		  "MAX(ORG_CLASS) AS ORG_CLASS, " +
		  "MAX(ORG_SUB_CLASS) AS ORG_SUB_CLASS " +
		"FROM ABR_ASIC_OWNER.ABR_ASIC_EXT_ORGANISATION  " +
		"WHERE " +
		  "REGN_END_DATE =999999 " +
		  "AND ORG_END_DATE =999999 " +
		  "AND ORG_NAME LIKE '%$ORG_NAME$%' " +
		"GROUP BY ORGANISATION_NO " +
		") O " +
		"LEFT JOIN ( " +
		  "SELECT  REF, GROUP_ID " +
		  "FROM ABR_ASIC_EXT_GEMS_GFT " +
		  "GROUP BY REF, GROUP_ID) G ON " +
		  "O.ORGANISATION_NO=G.REF " +
		  ") " +
		"where rownum<1000 ";
  
}
