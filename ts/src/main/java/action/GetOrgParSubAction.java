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
public class GetOrgParSubAction
{
	/**
	 * Constructor for the <code>GetOrgParSubAction</code>.
	 * @param drawingView The drawing view in which the action is executed.
	 * @param name The name of this action item.
	 */
	public GetOrgParSubAction(
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
		final TSModel model = this.getDrawingView().getModel();
	
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
					final Iterator iter = selectedObjects.iterator();

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

							if( element.hasAttribute("ORGANISATION_NO") )
							{
								Integer org_no = (Integer) element.getAttributeValue("ORGANISATION_NO");
								if( org_no != null)
								{
									model.getEventManager().setFireEvents(false);
									this.integrator.reset();
									this.integrator.setModel(this.getDrawingView().getModel());
									// Get element type for which to change the query.

									TSModelElementType orgElementType = this.integrator.getSchema().getModelElementType("Organisation");
									// Get location object.

									TSJDBCAttributedObjectDataSourceLocation orgLocation = (TSJDBCAttributedObjectDataSourceLocation) this.integrator.getBindingManager().getBinding(orgElementType).getDataSourceLocation();

									// Change the query.
									System.out.println(GET_PS_ORG_SQL.replace("$ORG_NO$", org_no.toString()));
									orgLocation.setReference(GET_PS_ORG_SQL.replace("$ORG_NO$", org_no.toString()));

									TSModelElementType psElementType = this.integrator.getSchema().getModelElementType("Organisation_PAR_SUB");
									// Get location object.

									TSJDBCAttributedObjectDataSourceLocation psLocation = (TSJDBCAttributedObjectDataSourceLocation) this.integrator.getBindingManager().getBinding(psElementType).getDataSourceLocation();

									// Change the query.
									System.out.println(GET_PS_EDGE_SQL.replace("$ORG_NO$", org_no.toString()));
									psLocation.setReference(GET_PS_EDGE_SQL.replace("$ORG_NO$", org_no.toString()));

									
									// Update integrator.
									this.integrator.update();
									this.getDrawingView().updateView();
								}
							}

						}
					}
				}
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
	private final String GET_PS_ORG_SQL =
		"select * from ( " +
		"SELECT  " +
		  "O.*, " +
		  "G.GROUP_ID " +
		  "FROM ( " +
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
			"AND ORGANISATION_NO IN " +
			  "( SELECT  " +
				  "ORG_NUMBER_PAR ORGANISATION_NO " +
				"FROM ABR_ASIC_OWNER.ABR_ASIC_EXT_GEMS_PS_R  " +
				"WHERE " +
				  "ORG_NUMBER_UPAR IN  " +
				  "(SELECT  " +
						"ORG_NUMBER_UPAR " +
					  "FROM ABR_ASIC_OWNER.ABR_ASIC_EXT_GEMS_PS_R  " +
					  "WHERE " +
					  "ORG_NUMBER_PAR =$ORG_NO$ OR ORG_NUMBER_SUB=$ORG_NO$ " +
					  "GROUP BY ORG_NUMBER_UPAR " +
					") " +
				"UNION   " +
				"SELECT  " +
				  "ORG_NUMBER_SUB ORGANISATION_NO " +
				"FROM ABR_ASIC_OWNER.ABR_ASIC_EXT_GEMS_PS_R  " +
				"WHERE " +
				  "ORG_NUMBER_UPAR IN  " +
				  "(SELECT  " +
						"ORG_NUMBER_UPAR " +
					  "FROM ABR_ASIC_OWNER.ABR_ASIC_EXT_GEMS_PS_R  " +
					  "WHERE " +
					  "ORG_NUMBER_PAR =$ORG_NO$ OR ORG_NUMBER_SUB=$ORG_NO$ " +
					  "GROUP BY ORG_NUMBER_UPAR " +
					") " +
				")     " +
		  "GROUP BY ORGANISATION_NO " +
		  ") O " +
		"LEFT JOIN ( " +
		  "SELECT  REF, GROUP_ID " +
		  "FROM ABR_ASIC_EXT_GEMS_GFT  " +
		  "GROUP BY REF, GROUP_ID) G ON  " +
		  "O.ORGANISATION_NO=G.REF " +
		  ") " +
		"where rownum<2000 ";
	private final String GET_PS_EDGE_SQL = 
		"select * from ( " +
		  "SELECT  " +
			"ORG_NUMBER_PAR, ORG_NUMBER_SUB " +
		  "FROM ABR_ASIC_OWNER.ABR_ASIC_EXT_GEMS_PS_R  " +
		  "WHERE " +
			"ORG_NUMBER_UPAR IN  " +
			"(SELECT  " +
				  "ORG_NUMBER_UPAR " +
				"FROM ABR_ASIC_OWNER.ABR_ASIC_EXT_GEMS_PS_R  " +
				"WHERE " +
				"ORG_NUMBER_PAR =$ORG_NO$ OR ORG_NUMBER_SUB=$ORG_NO$ " +
				"GROUP BY ORG_NUMBER_UPAR " +
			  ") " +
		  "GROUP BY ORG_NUMBER_PAR, ORG_NUMBER_SUB) " +
		"where ROWNUM<2000";		  
}
