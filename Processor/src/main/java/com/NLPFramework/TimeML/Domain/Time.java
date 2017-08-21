package com.NLPFramework.TimeML.Domain;

import java.util.OptionalInt;

public class Time implements Comparable<Time> 
{
	public static String noData = "XXXX";
	public OptionalInt year = null;
	public OptionalInt month = null;
	public OptionalInt day = null;
	@Override
	public int compareTo(Time time2)
	{
		int thisDate = 100000000;
		if(year.isPresent())
			thisDate = thisDate + (year.getAsInt() * 100000);
		if(month.isPresent())
			thisDate = thisDate + (month.getAsInt() * 1000);
		if(day.isPresent())
			thisDate = thisDate + day.getAsInt();
		
		int compareDate = 100000000;
		if(time2.year.isPresent())
			compareDate = compareDate + (year.getAsInt() * 100000);
		if(time2.month.isPresent())
			compareDate = compareDate + (month.getAsInt() * 1000);
		if(time2.day.isPresent())
			compareDate = compareDate + day.getAsInt();
		
		/*if(!year.isPresent() && !time2.year.isPresent()
				&& !month.isPresent() && !time2.month.isPresent()
				&& !day.isPresent() && !time2.day.isPresent())
			return 0;

		if(year.isPresent() && time2.year.isPresent())
		{
			int yearComparison = Integer.compare(this.year.getAsInt(), time2.year.getAsInt());
			if(yearComparison != 0)
				return yearComparison;
		}else if(year.isPresent()) return -1;
		else return 1;
		
		if(month.isPresent() && time2.month.isPresent())
		{
			int monthComparison = Integer.compare(this.month.getAsInt(), time2.month.getAsInt());
			if(monthComparison != 0)
				return monthComparison;
		}else if(month.isPresent()) return -1;
		else return 1;

		if(day.isPresent() && time2.day.isPresent())
		{
			int dayComparison = Integer.compare(this.day.getAsInt(), time2.day.getAsInt());
			if(dayComparison != 0)
				return dayComparison;
		}else if(day.isPresent()) return -1;
		else return 1;*/

		return Integer.compare(thisDate, compareDate);
	}

}
