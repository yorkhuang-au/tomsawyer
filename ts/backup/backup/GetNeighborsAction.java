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
public class GetNeighborsAction
{
	/**
	 * Constructor for the <code>GetNeighborsAction</code>.
	 * @param drawingView The drawing view in which the action is executed.
	 * @param name The name of this action item.
	 */
	public GetNeighborsAction(
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

						Object contextValue = element.getAttributeValue(this.actionName);

						if (contextValue instanceof List)
						{
							// special case. We'll generate ContextValue1..n

							List values = (List) contextValue;

							final TSModel model = this.getDrawingView().getModel();

							model.getEventManager().setFireEvents(false);

							try
							{
								int i = 1;

								for (Object value : values)
								{
									model.setAttribute("_Context" + i++, value);
								}
							}
							finally
							{
								model.getEventManager().setFireEvents(true);
							}
						}
						else
						{
							if (contextValue == null)
							{
								contextValue = element.getAttributeValue("_Context");
							}

							if (contextValue == null)
							{
								contextValue = element.getAttributeValue("ID");
							}

							if (contextValue == null)
							{
								contextValue = item.getName().toString();
							}
						}

						if (contextValue != null)
						{
							final TSModel model = this.getDrawingView().getModel();
							model.getEventManager().setFireEvents(false);

							try
							{
								model.setAttribute("_Context", contextValue);
								model.setAttribute("_X", x);
								model.setAttribute("_Y", y);
							}
							finally
							{
								model.getEventManager().setFireEvents(true);
							}

							try
							{
								this.integrator.reset();
								this.integrator.setModel(model);
								this.integrator.update();

								this.getDrawingView().updateView();

								boolean bo = false;
								List items = model.getModelElements();

								insertedModelElements =
									this.integrator.getUpdateResult().
										getInsertedModelElements();

								model.getEventManager().setFireEvents(false);

								try
								{
									if (insertedModelElements.size() > 0)
									{
										found[0] = true;

										for (Object o : items)
										{
											TSModelElement item2 = (TSModelElement) o;

											if (item2.hasAttribute("_New"))
											{
												item2.removeAttribute("_New");
											}
										}
									}
								}
								finally
								{
									model.getEventManager().setFireEvents(true);
								}
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					}
				}

				this.callLayoutIfNeeded(found[0]);
			}
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
}
