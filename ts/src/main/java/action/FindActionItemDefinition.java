//
// Tom Sawyer Software
// Copyright 2007 - 2015
// All rights reserved.
//
// www.tomsawyer.com
//


package action;


import com.tomsawyer.graphicaldrawing.ui.TSImageLoader;
import com.tomsawyer.util.shared.TSUserAgent;
import com.tomsawyer.view.TSModelView;
import com.tomsawyer.view.action.TSDrawingViewActionItemDefinition;
import com.tomsawyer.view.action.swing.TSSwingDrawingViewActionItem;
import com.tomsawyer.view.behavior.*;
import com.tomsawyer.view.drawing.swing.TSSwingDrawingView;
import com.tomsawyer.web.client.view.data.TSWebViewClientCommandData;
import com.tomsawyer.web.server.action.TSWebDrawingViewActionItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;


/**
 * This class provides the definition for finding elements given a string value.
 */
public class FindActionItemDefinition
	extends TSDrawingViewActionItemDefinition
	implements TSDesktopActionItemImplementer, TSWebActionItemImplementer,
		TSWebClientSideActionItemImplementer
{
	/**
	 * Constructor.
	 */
	public FindActionItemDefinition()
	{
		try
		{
			this.setIcon(TSImageLoader.getImage(this.getClass(),
				"images/find.png"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * This method returns whether, in the current state of the given view,
	 * this action item should be enabled.
	 *
	 * @param view Given view.
	 * @return Whether action item should be enabled.
	 */
	@Override
	public boolean shouldBeEnabled(TSModelView view)
	{
		return true;
	}


	/**
	 * This method returns a desktop implementation of this action.
	 */
	@Override
	public TSViewBehaviorObject newDesktopImplementation(TSUserAgent userAgent)
	{
		return new TSSwingDrawingViewActionItem(this)
		{
			@Override
			public void onAction()
			{
				if (this.getView().getModel() == null)
				{
					return;
				}

				// Popup a dialog or something

				String name = (String) JOptionPane.showInputDialog(
					this.getDrawingView().getComponent(),
					"Name:",
					"Find",
					JOptionPane.PLAIN_MESSAGE,
					null,
					null,
					"");

				new FindAction(getDrawingView(),
					FindActionItemDefinition.this.getText())
				{
					@Override
					protected void showNoResults()
					{
						JOptionPane.showMessageDialog(
							((TSSwingDrawingView) this.getDrawingView()).getComponent(),
							"No results found.",
							"No Results Found",
							JOptionPane.PLAIN_MESSAGE);
					}
				}.onAction(name);
			}
		};
	}


	/**
	 * This method returns a web implementation of this action.
	 */
	@Override
	public TSWebActionItem newWebImplementation()
	{
		return new TSWebDrawingViewActionItem()
		{
			@Override
			public Serializable onAction(TSAbstractItemDefinition itemDef)
			{
				TSWebViewClientCommandData result;

				// create the web view client command data
				result = new TSWebViewClientCommandData();

				// set the name of the custom JavaScript function to be called
				result.setCommand("FindAction");

				List<Serializable> commandArguments = new ArrayList<Serializable>(1);
				commandArguments.add(FindActionItemDefinition.this.getText());
				result.setCommandData(commandArguments);

				return result;
			}
		};
	}
}
