//
// Tom Sawyer Software
// Copyright 2007 - 2015
// All rights reserved.
//
// www.tomsawyer.com
//


package action;


import com.tomsawyer.graph.TSGraphObject;
import com.tomsawyer.graphicaldrawing.TSENode;
import com.tomsawyer.graphicaldrawing.TSEObject;
import com.tomsawyer.integrator.TSIntegrator;
import com.tomsawyer.interactive.command.editing.TSEIncrementalLayoutCommand;
import com.tomsawyer.model.TSModel;
import com.tomsawyer.model.TSModelElement;
import com.tomsawyer.util.shared.TSAttributedObject;
import com.tomsawyer.view.drawing.TSModelDrawingView;
import com.tomsawyer.model.schema.TSModelElementType;
import com.tomsawyer.integrator.jdbc.TSJDBCAttributedObjectDataSourceLocation;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * This class provides a Swing implementation performing on demand loading
 * of data in a drawing view.
 * <p/>
 * It works as follows:
 * - the user selects a model element, then invokes the action from a context
 * menu or toolbar.
 * - the action sets the value for the _Context model attribute based on the selected
 * element. It first checks if the element has an attribute whose name matches the
 * action item. If yes it uses that value. If not it checks for the following attribute
 * values in order: _Context, ID.
 * - the action then sets the values for the _X and _Y model attributes to match the
 * location of the selected element in the drawing view.
 * - the action then performs an integrator update for the integrator whose name matches
 * the name of the action item. The integrator is reset before the update.
 * - after the update is performed, the action removes the _New attribute from all the
 * model elements in the model.
 * - the action then performs an incremental layout.
 * <p/>
 * In the project, for the incremental layout update to behave properly, the element
 * types must have a _New attribute of type boolean and a default value set to true. The
 * drawing view must then check the value of the _New attribute and if true set the
 * location of the created nodes to the _X and _Y values.
 */
public class GetALTPersonAction
{
	/**
	 * Constructor for the <code>GetALTPersonAction</code>.
	 * @param drawingView The drawing view in which the action is executed.
	 * @param name The name of this action item.
	 */
	public GetALTPersonAction(
		TSModelDrawingView drawingView,
		String name)
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
	 */
	public void onAction()
	{
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

				final TSModel model = this.getDrawingView().getModel();
				model.clear();

				//model.getEventManager().setFireEvents(false);

				/*
				try
				{
					model.setAttribute("_Context", name);
				}
				finally
				{
					model.getEventManager().setFireEvents(true);
				}
				*/

				this.integrator.reset();
				this.integrator.setModel(this.getDrawingView().getModel());
				
				// Get element type for which to change the query.

				TSModelElementType deviceElementType = this.integrator.getSchema().getModelElementType("Person");

				 

				// Get location object.

				TSJDBCAttributedObjectDataSourceLocation deviceLocation = (TSJDBCAttributedObjectDataSourceLocation) this.integrator.getBindingManager().getBinding(deviceElementType).getDataSourceLocation();

				// Change the query.
				//System.out.println(FIND_PERSON_SQL + " '%" + name.toUpperCase().trim() + "%' AND ROWNUM<1000");

//				deviceLocation.setReference(FIND_PERSON_SQL + "'RICHARD JAMES HUNGERFORD' + AND ROWNUM<1000");
//					deviceLocation.setReference("select * from vw_tbl_ts_person_with_master_alt where name like '%JAMES%' limit 100");

				 

				// Update integrator.

				
				this.integrator.update();
				this.getDrawingView().updateView();
				
				/*

				boolean bo = false;
				List items = model.getModelElements();

				Collection<TSModelElement> insertedModelElements =
					this.integrator.getUpdateResult().getInsertedModelElements();

				model.getEventManager().setFireEvents(false);

				try
				{
					if (insertedModelElements.size() > 0)
					{
						bo = true;

						for (Object o : items)
						{
							TSModelElement item = (TSModelElement) o;

							if (item.hasAttribute("_New"))
							{
								item.removeAttribute("_New");
							}
						}
					}
				}
				finally
				{
					model.getEventManager().setFireEvents(true);
				}

				if (bo)
				{
					final TSEIncrementalLayoutCommand c =
						new TSEIncrementalLayoutCommand(
							this.getDrawingView().getCanvas(),
							this.getDrawingView().getServiceInputData());

					c.setFitInCanvasAfterOperation(false);
					c.setThreaded(false);
					this.getDrawingView().
						getCanvas().getCommandManager().transmit(c);
				}
				else
				{
					this.showNoResults();
				}
				*/
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	/*
		try
		{
			if (this.drawingView.getModel() == null)
			{
				return;
			}

			final List<TSGraphObject> selectedObjects =
				new LinkedList<TSGraphObject>(
					this.getDrawingView().getSelectedGraphObjects(false));

			Collection<TSModelElement> insertedModelElements;

			if (!selectedObjects.isEmpty())
			{
				if (this.integrator == null)
				{
					this.integrator = this.getDrawingView().getModule().
						getIntegrator(this.actionName);
				}

				if (this.integrator != null)
				{
					final Boolean[] found = {false};
					final Iterator iter = selectedObjects.iterator();

					final TSModel model = this.getDrawingView().getModel();
					model.clear();


					while (iter.hasNext())
					{
						TSEObject item = (TSEObject) iter.next();

						if (item instanceof TSENode)
						{
							TSAttributedObject element =
								this.getDrawingView().getMapper().
									getAttributedObject((TSENode) item);

							final double x = ((TSENode) item).getCenterX();
							final double y = ((TSENode) item).getCenterY();

							// check the attribute that matches the action and integrator
							// name.
							// if none, check the attribute called ContextValue
							// if none, check the attribute called ID
							// if none, take the graph object string name

							//Object contextValue = element.getAttributeValue(this.actionName);
							
							if( element.hasAttribute("PERSON_NUMB") )
							{
								Integer person_numb = (Integer) element.getAttributeValue("PERSON_NUMB");
								if( person_numb != null)
								{
									this.integrator.reset();
									this.integrator.setModel(this.getDrawingView().getModel());
									// Get element type for which to change the query.

									TSModelElementType deviceElementType = this.integrator.getSchema().getModelElementType("Person");
									// Get location object.

									TSJDBCAttributedObjectDataSourceLocation deviceLocation = (TSJDBCAttributedObjectDataSourceLocation) this.integrator.getBindingManager().getBinding(deviceElementType).getDataSourceLocation();

									// Change the query.
									String sql = GET_ALT_PERSON_SQL.replace("$PERSON_NUMB$", person_numb.toString());

									System.out.println(sql);
									deviceLocation.setReference(sql);

									// Update integrator.
									this.integrator.update();
									this.getDrawingView().updateView();
								}
							}

						}
					}

					//this.callLayoutIfNeeded(found[0]);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	*/
		
	}


	/**
	 * This method invokes incremental layout if adjacent elements were found.
	 * @param found A boolean indicating whether elements were found or not.
	 */
	private void callLayoutIfNeeded(Boolean found)
	{
		if (!found)
		{
			this.showNoResults();
		}
		else
		{
			final TSEIncrementalLayoutCommand c =
				new TSEIncrementalLayoutCommand(
					this.getDrawingView().getCanvas(),
					this.getDrawingView().getServiceInputData());

			c.setFitInCanvasAfterOperation(true);
			c.setThreaded(false);
			this.getDrawingView().getCanvas().getCommandManager().transmit(c);
		}
	}


	/**
	 *  This method is called when no results were found.
	 */
	protected void showNoResults()
	{
	}


	String actionName;


	TSModelDrawingView drawingView;

	TSIntegrator integrator;
	private final String GET_ALT_PERSON_SQL =
		"SELECT DISTINCT P.PERSON_NUMB, BIRTH_COUNTRY, DF_BIRTH_DATE, BIRTH_LOCALITY, DEFAULT_ADDRESS_NO, BIRTH_STATE, " +
			"NULLIF(P.given_name1 || ' ' , ' ' ) || NULLIF(P.given_name2 || ' ' , ' ' ) || NULLIF(P.given_name3 || ' ' , ' ' ) || COALESCE(SURNAME, '') AS NAME, " +
			"IS_ALT " +
		"FROM ABR_ASIC_OWNER.ABR_ASIC_EXT_PERSON P " +
		"JOIN ( " +
		  "SELECT  " +
			"PERSON_NUMB, ALT_PERSON_NUMB, " +
			"CASE WHEN PERSON_NUMB=ALT_PERSON_NUMB THEN 1 ELSE 0 END AS IS_ALT " +
		  "FROM ABR_ASIC_OWNER.ABR_ASIC_EXT_GEMS_ALT_P  " +
		  "where ALT_PERSON_NUMB = (SELECT " +
			"ALT_PERSON_NUMB FROM ABR_ASIC_OWNER.ABR_ASIC_EXT_GEMS_ALT_P  " +
			"WHERE PERSON_NUMB=$PERSON_NUMB$ " +
		  ") " +
		  "group by PERSON_NUMB, ALT_PERSON_NUMB " +
		") ALT_P ON " +
			"P.PERSON_NUMB = ALT_P.PERSON_NUMB " +
		"WHERE " +
		  "REC_END_DATE ='0999999' ";

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
