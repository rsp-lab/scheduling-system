package pl.radek.ss.lib;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// Uses format "rrrr-mm-ddThh:mm"
public class DateManagement
{
	public static int compareTimestamps(String start, String end) {
        String[] startTimestamp = start.split("T");
        String[] startDate = startTimestamp[0].split("-");
        String startHour = startTimestamp[1].split(":")[0];
        String startMinute = startTimestamp[1].split(":")[1];
        
        String[] endTimestamp = end.split("T");
        String[] endDate = endTimestamp[0].split("-");
        String endHour = endTimestamp[1].split(":")[0];
        String endMinute = endTimestamp[1].split(":")[1];
        
        Calendar startCal = Calendar.getInstance();
        startCal.set(Integer.parseInt(startDate[0]), Integer.parseInt(startDate[1]) - 1, Integer.parseInt(startDate[2]), Integer.parseInt(startHour), Integer.parseInt(startMinute));
        
        Calendar endCal = Calendar.getInstance();
        endCal.set(Integer.parseInt(endDate[0]), Integer.parseInt(endDate[1]) - 1, Integer.parseInt(endDate[2]), Integer.parseInt(endHour), Integer.parseInt(endMinute));
        
        if(startCal.before(endCal))
            return 1;
        else if(startCal.after(endCal))
            return -1;
        else
		    return 0;
	}
    
	public static Long differenceTimestampsInMinutes(String start, String end) {
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
    
	public static int compareDates(String start, String end) {
		String[] startDate = start.split("-");
		String[] endDate = end.split("-");
		
		Calendar startCal = Calendar.getInstance();
		startCal.set(Integer.parseInt(startDate[0]), Integer.parseInt(startDate[1]), Integer.parseInt(startDate[2]));

		Calendar endCal = Calendar.getInstance();
		endCal.set(Integer.parseInt(endDate[0]), Integer.parseInt(endDate[1]), Integer.parseInt(endDate[2]));
		
		if(startCal.before(endCal))
            return 1;
		if(startCal.after(endCal))
            return -1;
        
		return 0;
	}
	
	public static int compareDates(Calendar startCal, Calendar endCal) {
		if(startCal.after(endCal))
            return 1;
		if(startCal.before(endCal))
            return -1;
        
		return 0;
	}
	
	public static boolean isFutureDate(String date)	{
		String[] tab = date.split("-");
		
		Calendar dateCal = Calendar.getInstance();
		dateCal.set(Integer.parseInt(tab[0]), Integer.parseInt(tab[1]), Integer.parseInt(tab[2]));

		Calendar currentCal = Calendar.getInstance();
		
		if(dateCal.after(currentCal))
            return true;
        
		return false;
	}
	
	public static boolean isPastDate(String date) {
		String[] tab = date.split("-");
		
		Calendar dateCal = Calendar.getInstance();
		dateCal.set(Integer.parseInt(tab[0]), Integer.parseInt(tab[1]), Integer.parseInt(tab[2]));

		Calendar currentCal = Calendar.getInstance();
  
		if(dateCal.before(currentCal))
            return true;
        
		return false;
	}
	
	public static String getCurrentDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar currentCal = Calendar.getInstance();
		currentCal.set(Calendar.DAY_OF_MONTH, currentCal.get(Calendar.DAY_OF_MONTH)+1);
		return sdf.format(currentCal.getTime());
	}
	
	public static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		Calendar currentCal = Calendar.getInstance();
        currentCal.set(Calendar.HOUR, currentCal.get(Calendar.HOUR) + 1);
		return sdf.format(currentCal.getTime());
	}
    
    public static String getFutureDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar currentCal = Calendar.getInstance();
        currentCal.set(Calendar.MONTH, currentCal.get(Calendar.MONTH) + 1);
        return sdf.format(currentCal.getTime());
    }
    
    public static String getFutureTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar currentCal = Calendar.getInstance();
        currentCal.set(Calendar.HOUR, currentCal.get(Calendar.HOUR) + 1);
        return sdf.format(currentCal.getTime());
    }
	
	public static String getCurrentTimestamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
		Calendar currentCal = Calendar.getInstance();
		
		return sdfDate.format(currentCal.getTime()) + "T" + sdfTime.format(currentCal.getTime());
	}
}
