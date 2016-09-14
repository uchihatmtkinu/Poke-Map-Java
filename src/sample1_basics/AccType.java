package sample1_basics;

public class AccType {

	private String username;
	private String password;
	
	public AccType(String u, String p)
	{
		this.username = u;
		this.password = p;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getPassword()
	{
		return password;
	}
}
