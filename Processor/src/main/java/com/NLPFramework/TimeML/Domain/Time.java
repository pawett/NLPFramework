package com.NLPFramework.TimeML.Domain;

import java.util.OptionalInt;

import com.NLPFramework.Crosscutting.Logger;

public class Time implements Comparable<Time> 
{
	public OptionalInt year = null;
	public OptionalInt month = null;
	public OptionalInt day = null;
	
	public Time(Timex3 timex)
	{
		if(timex != null && timex.value != null)
		{
			try
			{
				String[] dateValues = timex.value.split("-");

				if(dateValues.length > 0 && !dateValues[0].equalsIgnoreCase("XXXX"))
				{
					year = OptionalInt.of(Integer.valueOf(dateValues[0]));
					if(dateValues.length > 1 && !dateValues[1].equalsIgnoreCase("XX"))
					{
						month = OptionalInt.of(Integer.valueOf(dateValues[1]));

						if(dateValues.length > 2 && !dateValues[2].equalsIgnoreCase("XXXX"))
							day = OptionalInt.of(Integer.valueOf(dateValues[2]));
					}
				}
			}catch(Exception ex){Logger.WriteDebug("Wrong values for date " + timex.value);}
		}
	}
	
	public String toString()
	{
		if(year == null)
			return "XXXX-XX-XX";
		StringBuilder sb = new StringBuilder();
		
		sb.append(year.getAsInt());
		
		if(month == null)
		{
			sb.append("-XX-XX");
			return sb.toString();
		}
		
		sb.append("-" + ((month.getAsInt() < 10) ? "0"+month.getAsInt() : month.getAsInt()));
		
		if(day == null)
			sb.append("-XX");
		else
			sb.append("-" + ((day.getAsInt() < 10) ? "0"+day.getAsInt() : day.getAsInt()));
		
		return sb.toString();
		
		
	}
	@Override
	public int compareTo(Time time2)
	{
		int thisDate = 100000000;
		if(year != null)
			thisDate = thisDate + (year.getAsInt() * 10000);
		if(month != null)
			thisDate = thisDate + (month.getAsInt() * 100);
		if(day != null)
			thisDate = thisDate + day.getAsInt();
		
		int compareDate = 100000000;
		if(time2.year != null)
			compareDate = compareDate + (time2.year.getAsInt() * 10000);
		if(time2.month != null)
			compareDate = compareDate + (time2.month.getAsInt() * 100);
		if(time2.day != null)
			compareDate = compareDate + time2.day.getAsInt();
		
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
	
	public boolean equals(Object obj)
	{
		return compareTo((Time)obj) == 0;
	}

}
