package imqdb.utils;

public class UtilQueryPair {

	private String name;
	private String id;

	public static UtilQueryPair ANY = new UtilQueryPair("Any", "*");

	public UtilQueryPair(String name, String id)

	{
		this.name = name;
		this.id = id;
	}

	public String toString()
	{
		return name;
	}

	public String getName()
	{
		return name;
	}

	public String getId()
	{
		return id;
	}

}