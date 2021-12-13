package imqdb;

public class QueryBus {

	private static QueryController activeController;

	public static QueryController getController()
	{
		return activeController;
	}

	public static void setController(QueryController controller)
	{
		activeController = controller;
		System.out.println("NEW CONTROLLER: " + controller);
	}

	public static boolean hasController()
	{
		return activeController != null;
	}

}
