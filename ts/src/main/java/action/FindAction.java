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
public class FindAction
{
	public FindAction(TSModelDrawingView drawingView, String name)
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
		// If a string was returned, say so.

		if ((name != null) && (name.length() > 0))
		{
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

					TSModelElementType deviceElementType = this.integrator.getSchema().getModelElementType("Person");

					// Get location object.

					TSJDBCAttributedObjectDataSourceLocation deviceLocation = (TSJDBCAttributedObjectDataSourceLocation) this.integrator.getBindingManager().getBinding(deviceElementType).getDataSourceLocation();

					// Change the query.
					//System.out.println(FIND_PERSON_SQL + " '%" + name.toUpperCase().trim() + "%' AND ROWNUM<1000");

					deviceLocation.setReference(FIND_PERSON_SQL + " '%" + name.toUpperCase().trim() + "%' AND ROWNUM<1000");

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
	private final String FIND_PERSON_SQL =
	"SELECT * FROM (SELECT DISTINCT " +
	"PERSON_NUMB, " +
	"BIRTH_COUNTRY, " +
	"DF_BIRTH_DATE, " +
	"BIRTH_LOCALITY, " +
	"DEFAULT_ADDRESS_NO, " + 
	"BIRTH_STATE, " +
	"NULLIF(P.given_name1 || ' ' , ' ' ) || NULLIF(P.given_name2 || ' ' , ' ' ) || NULLIF(P.given_name3 || ' ' , ' ' ) " +
	"|| COALESCE(SURNAME, '') " +
	"AS NAME, " +
	"CASE WHEN ALT_P.ALT_PERSON_NUMB IS NULL THEN 0 ELSE 1 END AS IS_ALT " +
"FROM ABR_ASIC_OWNER.ABR_ASIC_EXT_PERSON P " +
"LEFT JOIN ( " +
	"SELECT " +
	"  ALT_PERSON_NUMB " +
	"FROM ABR_ASIC_OWNER.ABR_ASIC_EXT_GEMS_ALT_P " +
	"where ALT_PERSON_NUMB is not null " +
	"group by ALT_PERSON_NUMB) ALT_P ON " +
	"P.PERSON_NUMB = ALT_P.ALT_PERSON_NUMB " +
"WHERE " +
  "REC_END_DATE ='0999999') WHERE NAME LIKE ";
  
}
