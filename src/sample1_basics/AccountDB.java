package sample1_basics;

public class AccountDB {

	
	final static int MaxNum = 7;
	private static String[] username = {"uchiha_1","uchiha_2","uchiha_3","uchiha_4","uchiha_6","uchiha_7","uchiha_8"};
	private static String[] password = {"test1234","test1234","test1234","test1234","test1234","test1234","test1234"};
	private static AccType [] data = new AccType[MaxNum];
	
	public AccType getAcc(int i)
	{
		if (i <= MaxNum)
			return data[i];
		else
			return data[0];
	}
	
	public AccountDB()
	{
		for (int i = 0; i < MaxNum; i++)
			data[i] = new AccType(username[i],password[i]);
	}
}
