//
// Tom Sawyer Software
// Copyright 2007 - 2015
// All rights reserved.
//
// www.tomsawyer.com
//


package action;


import com.tomsawyer.model.TSModel;
import com.tomsawyer.util.gwtclient.command.TSServiceCommand;
import com.tomsawyer.util.shared.TSServiceException;
import com.tomsawyer.view.drawing.TSModelDrawingView;
import com.tomsawyer.web.client.command.TSCustomCommand;
import com.tomsawyer.web.server.TSProjectSessionInfo;
import com.tomsawyer.web.server.command.TSServiceCommandImpl;
import com.tomsawyer.web.server.service.TSPerspectivesViewService;

import java.io.Serializable;
import java.util.List;


/**
 * This class provides the implementation for loading elements that match a user
 * specified value.
 */
public class FindOrganisationActionCommandImpl implements TSServiceCommandImpl
{
	/**
	 * This method implements the server side component of New Role custom
	 * action.
	 *
	 * @param service the view service.
	 * @param command the service command.
	 * @return A serializable result or null.
	 * @throws com.tomsawyer.util.shared.TSServiceException
	 *          The service exception if an error occurs.
	 */
	@Override
	public Serializable doAction(TSPerspectivesViewService service,
		TSServiceCommand command) throws TSServiceException
	{
		TSCustomCommand customCommand = (TSCustomCommand) command;

		List<String> args = customCommand.getCustomArgs();

		if (args == null || args.isEmpty())
		{
			throw new TSServiceException(
				"No arguments found for New Role");
		}
		else if (args.size() != 2)
		{
			throw new TSServiceException(
				"Wrong number of arguments found for New Role");
		}

		String name = args.get(0);
		String actionName = args.get(1);

		TSProjectSessionInfo projectInfo =
			service.getProjectSessionInfo(customCommand.getProjectID());

		TSModel model = projectInfo.getModel(customCommand.getModelID());

		TSModelDrawingView view =
			(TSModelDrawingView) projectInfo.getView(customCommand.getViewID());

		this.result = Boolean.TRUE;

		if (model != null)
		{
			new FindOrganisationAction(view, actionName)
			{
				@Override
				protected void showNoResults()
				{
					FindOrganisationActionCommandImpl.this.result =
						"No results found.";
				}
			}.onAction(name);
		}
		else
		{
			throw new TSServiceException("No model found with ID = " +
				customCommand.getModelID());
		}

		return this.result;
	}


	/**
	 * This field stores a serializable result of this command.
	 */
	private Serializable result;
}
