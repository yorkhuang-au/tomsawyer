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
 * This class defines the load data action web view service command.
 */
public class GetNeighborsActionCommandImpl implements TSServiceCommandImpl
{
	@Override
	public Serializable doAction(TSPerspectivesViewService service,
		TSServiceCommand command) throws TSServiceException
	{
		TSCustomCommand customCommand = (TSCustomCommand) command;

		List<String> args = customCommand.getCustomArgs();

		if (args == null || args.isEmpty())
		{
			throw new TSServiceException(
				"No arguments found for Load Data Action");
		}
		else if (args.size() != 1)
		{
			throw new TSServiceException(
				"Wrong number of arguments for Load Data Action ");
		}

		String actionName = args.get(0);

		TSProjectSessionInfo projectInfo =
			service.getProjectSessionInfo(customCommand.getProjectID());

		TSModel model = projectInfo.getModel(customCommand.getModelID());

		TSModelDrawingView drawingView =
			(TSModelDrawingView) projectInfo.getView(customCommand.getViewID());

		this.result = Boolean.TRUE;

		if (model != null)
		{
			new GetNeighborsAction(drawingView, actionName)
			{
				@Override
				protected void showNoResults()
				{
					GetNeighborsActionCommandImpl.this.result =
						"No results found.";
				}
			}.onAction();
		}
		else
		{
			throw new TSServiceException("No model found with ID = " +
				customCommand.getModelID());
		}

		return this.result;
	}



	private Serializable result;
}
