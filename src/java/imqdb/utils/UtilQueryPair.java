package imqdb.utils;

public record UtilQueryPair(String name, String id) {

	public static UtilQueryPair ANY = new UtilQueryPair("Any", "*");

	public String toString()
	{
		return name;
	}


	public boolean isAny()
	{
		return id.equals("*");
	}

}