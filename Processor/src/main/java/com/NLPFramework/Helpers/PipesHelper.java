package com.NLPFramework.Helpers;

public class PipesHelper {

	public static String AppendPipes(String s)
	 {
		if(s == null || s.isEmpty())
			s = "-";
		 StringBuilder sb = new StringBuilder();
		 sb.append("|");
		 sb.append(s);
		 return sb.toString();
	 }
	
	public static String AppendPipes(Object s)
	 {
		
		 StringBuilder sb = new StringBuilder();
		 sb.append("|");
		 if(s != null)
			 sb.append(s.toString());
		 else
			 sb.append("-");
		 return sb.toString();
	 }
}
