package ftb.lib.notification;

import latmod.lib.*;

import java.util.HashMap;

public class ClickActionRegistry
{
	private static final HashMap<String, ClickActionType> map = new HashMap<>();
	
	static
	{
		add(ClickActionType.CMD);
		add(ClickActionType.SHOW_CMD);
		add(ClickActionType.URL);
		add(ClickActionType.FILE);
		add(ClickActionType.GUI);
	}
	
	public static String[] getKeys()
	{ return LMListUtils.toStringArray(map.keySet()); }
	
	public static void add(ClickActionType a)
	{
		if(a != null && LMStringUtils.isValid(a.ID) && !map.containsKey(a.ID)) map.put(a.ID, a);
	}
	
	public static ClickActionType get(String s)
	{ return map.get(s); }
}