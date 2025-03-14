package pl.com.pk.sutip.lib;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateManagement
{
	// Porównuje czas typu string w formacie "rrrr-mm-ddThh:mm"
	public static int compareTimestamps(String start, String end)
	{
		// System.out.println("compareTimestamps (" + start + ", " + end + ")");
		
		SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
		
		try
		{
			String[] startTimestamp = start.split("T");
			String[] startDate = startTimestamp[0].split("-");
			Date startTime = parser.parse(startTimestamp[1]);

			String[] endTimestamp = end.split("T");
			String[] endDate = endTimestamp[0].split("-");
			Date endTime = parser.parse(endTimestamp[1]);

			Calendar startCal = Calendar.getInstance();
			startCal.set(Integer.parseInt(startDate[0]), Integer.parseInt(startDate[1]), Integer.parseInt(startDate[2]));
			
			Calendar endCal = Calendar.getInstance();
			endCal.set(Integer.parseInt(endDate[0]), Integer.parseInt(endDate[1]), Integer.parseInt(endDate[2]));
			
			// System.out.println("StartCalendar: " + startCal.get(Calendar.YEAR) + "-" + startCal.get(Calendar.MONTH) + "-" + startCal.get(Calendar.DAY_OF_MONTH));
			// System.out.println("EndCalendar: " + endCal.get(Calendar.YEAR) + "-" + endCal.get(Calendar.MONTH) + "-" + endCal.get(Calendar.DAY_OF_MONTH));
			
			// Porównanie dat
			if(startCal.before(endCal))
			{
				return 1;
			}
			if(startCal.after(endCal))
			{
				return -1;
			}
			// Porównanie czasów
			if(startTime.before(endTime))
			{
				return 1;
			}
			if(startTime.after(endTime))
			{
				return -1;
			}
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	// Zwraca różnice w czasie (minuty) obiektów typu string w formacie "rrrr-mm-ddThh:mm"
	public static Long differenceTimestamps(String start, String end)
	{
		String[] startTimestamp = start.split("T");
		String[] startDate = startTimestamp[0].split("-");
		String[] startTime = startTimestamp[1].split(":");
		
		String[] endTimestamp = end.split("T");
		String[] endDate = endTimestamp[0].split("-");
		String[] endTime = endTimestamp[1].split(":");
		
		Calendar startCal = Calendar.getInstance();
		startCal.set(Integer.parseInt(startDate[0]), Integer.parseInt(startDate[1]), Integer.parseInt(startDate[2]), Integer.parseInt(startTime[0]), Integer.parseInt(startTime[1]));
		
		Calendar endCal = Calendar.getInstance();
		endCal.set(Integer.parseInt(endDate[0]), Integer.parseInt(endDate[1]), Integer.parseInt(endDate[2]), Integer.parseInt(endTime[0]), Integer.parseInt(endTime[1]));
		
        long diff = endCal.getTimeInMillis() - startCal.getTimeInMillis();
		return diff / (60 * 1000);
	}
	
	// Porównuje daty typu string w formacie "rrrr-mm-dd"
	public static int compareDates(String start, String end)
	{
		// System.out.println("compareDates(String start, String end)");
		
		String[] startDate = start.split("-");
		String[] endDate = end.split("-");
		
		Calendar startCal = Calendar.getInstance();
		startCal.set(Integer.parseInt(startDate[0]), Integer.parseInt(startDate[1]), Integer.parseInt(startDate[2]));

		Calendar endCal = Calendar.getInstance();
		endCal.set(Integer.parseInt(endDate[0]), Integer.parseInt(endDate[1]), Integer.parseInt(endDate[2]));
		
		// System.out.println("StartCalendar: " + startCal.get(Calendar.YEAR) + "-" + startCal.get(Calendar.MONTH) + "-" + startCal.get(Calendar.DAY_OF_MONTH));
		// System.out.println("EndCalendar: " + endCal.get(Calendar.YEAR) + "-" + endCal.get(Calendar.MONTH) + "-" + endCal.get(Calendar.DAY_OF_MONTH));
	
		// PorĂłwnanie dat
		if(startCal.before(endCal))
		{
			return 1;
		}
		if(startCal.after(endCal))
		{
			return -1;
		}
		return 0;
	}
	
	// Porównuje daty typu string w formacie "rrrr-mm-dd"
	public static int compareDates(Calendar startCal, Calendar endCal)
	{
		// System.out.println("compareDates(Calendar start, Calendar end)");
		// System.out.println("StartCalendar: " + startCal.get(Calendar.YEAR) + "-" + startCal.get(Calendar.MONTH) + "-" + startCal.get(Calendar.DAY_OF_MONTH));
		// System.out.println("EndCalendar: " + endCal.get(Calendar.YEAR) + "-" + endCal.get(Calendar.MONTH) + "-" + endCal.get(Calendar.DAY_OF_MONTH));
	
		// PorĂłwnanie dat
		if(startCal.after(endCal))
		{
			return 1;
		}
		if(startCal.before(endCal))
		{
			return -1;
		}
		return 0;
	}
	
	// Sprawdza czy data typu string w formacie "rrrr-mm-dd" jest w przyszłości
	public static boolean isFuture(String date)
	{
		// System.out.println("isFuture(String start, String end)");
		
		String[] tab = date.split("-");
		
		Calendar dateCal = Calendar.getInstance();
		dateCal.set(Integer.parseInt(tab[0]), Integer.parseInt(tab[1]), Integer.parseInt(tab[2]));

		Calendar currentCal = Calendar.getInstance();
		
		// System.out.println("StartCalendar: " + dateCal.get(Calendar.YEAR) + "-" + dateCal.get(Calendar.MONTH) + "-" + dateCal.get(Calendar.DAY_OF_MONTH));
		// System.out.println("CurrentCalendar: " + currentCal.get(Calendar.YEAR) + "-" + currentCal.get(Calendar.MONTH) + "-" + currentCal.get(Calendar.DAY_OF_MONTH));
	
		// PorĂłwnanie dat
		if(dateCal.after(currentCal))
		{
			return true;
		}
		return false;
	}
	
	// Sprawdza czy data typu string w formacie "rrrr-mm-dd" jest w przeszłości
	public static boolean isPast(String date)
	{
		// System.out.println("isFuture(String start, String end)");
		
		String[] tab = date.split("-");
		
		Calendar dateCal = Calendar.getInstance();
		dateCal.set(Integer.parseInt(tab[0]), Integer.parseInt(tab[1]), Integer.parseInt(tab[2]));

		Calendar currentCal = Calendar.getInstance();
		
		// System.out.println("StartCalendar: " + dateCal.get(Calendar.YEAR) + "-" + dateCal.get(Calendar.MONTH) + "-" + dateCal.get(Calendar.DAY_OF_MONTH));
		// System.out.println("CurrentCalendar: " + currentCal.get(Calendar.YEAR) + "-" + currentCal.get(Calendar.MONTH) + "-" + currentCal.get(Calendar.DAY_OF_MONTH));
	
		// Porównanie dat
		if(dateCal.before(currentCal))
		{
			return true;
		}
		return false;
	}
	
	// Zwraca aktualną datę w formacie "rrrr-mm-dd"
	public static String getCurrentDate()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar currentCal = Calendar.getInstance();
		// currentCal.set(Calendar.MONTH, currentCal.get(Calendar.MONTH)-1);
		return sdf.format(currentCal.getTime());
	}
	
	// Zwraca aktualny czas w formacie "hh:mm"
	public static String getCurrentTime()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		Calendar currentCal = Calendar.getInstance();
		return sdf.format(currentCal.getTime());
	}
	
	// Zwraca aktualny czas w formacie "rrrr-mm-ddThh:mm"
	public static String getCurrentTimestamp()
	{
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
		Calendar currentCal = Calendar.getInstance();
		
		return sdfDate.format(currentCal.getTime()) + "T" + sdfTime.format(currentCal.getTime());
	}
}
