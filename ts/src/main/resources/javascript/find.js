var currentProjectID;
var currentModuleName;
var currentModelID;
var currentViewID;
var currentViewName;
var currentCustomArgs;


function FindAction(projectID, moduleName, modelID, viewID, viewName, customArgs)
{
	currentProjectID = projectID;
	currentModuleName = moduleName;
	currentModelID = modelID;
	currentViewID = viewID;
	currentViewName = viewName;
	currentCustomArgs = customArgs;

	tsPrompt("Name: ", "", "onAnswer");
}


function onAnswer(answer)
{
	if (answer)
	{
		startAction(currentProjectID,
			currentModuleName,
			currentModelID,
			currentViewID,
			currentViewName,
			"action.FindActionCommandImpl",
			[answer,currentCustomArgs[0]]);
	}
}

function FindOrganisationAction(projectID, moduleName, modelID, viewID, viewName, customArgs)
{
	currentProjectID = projectID;
	currentModuleName = moduleName;
	currentModelID = modelID;
	currentViewID = viewID;
	currentViewName = viewName;
	currentCustomArgs = customArgs;

	tsPrompt("Organisation Name: ", "", "onAnswerOrganisation");
}


function onAnswerOrganisation(answer)
{
	if (answer)
	{
		startAction(currentProjectID,
			currentModuleName,
			currentModelID,
			currentViewID,
			currentViewName,
			"action.FindOrganisationActionCommandImpl",
			[answer,currentCustomArgs[0]]);
	}
}

function GetNeighboursAction(projectID, moduleName, modelID, viewID, viewName, customArgs)
{
		startAction(projectID,
			moduleName,
			modelID,
			viewID,
			viewName,
			"action.GetNeighborsActionCommandImpl",
			[customArgs[0]]);
}

function GetALTPersonAction(projectID, moduleName, modelID, viewID, viewName, customArgs)
{
		startAction(projectID,
			moduleName,
			modelID,
			viewID,
			viewName,
			"action.GetALTPersonActionCommandImpl",
			[customArgs[0]]);
}
function GetOrgParSubAction(projectID, moduleName, modelID, viewID, viewName, customArgs)
{
		startAction(projectID,
			moduleName,
			modelID,
			viewID,
			viewName,
			"action.GetOrgParSubActionCommandImpl",
			[customArgs[0]]);
}

function startAction(projectID, moduleName, modelID, viewID, viewName, serverClassName, args)
{
	var command =
	{
		"command":"Custom",
		"data":
		{
			"project":projectID,
			"module":moduleName,
			"modelID":modelID,
			"viewID":viewID,
			"viewName":viewName,
			"serverClassName":serverClassName,
			"args":args
		},
		"onsuccess":"OnActionSuccess"
	};

	invokePerspectivesCommand(command);
}

function OnActionSuccess(successfulCommand, result)
{
	if (result != "true")
	{
		// No results were found.
		tsAlert(result);
	}
}
