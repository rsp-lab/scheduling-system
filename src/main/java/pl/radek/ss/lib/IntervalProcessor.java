package pl.radek.ss.lib;

import pl.radek.ss.domain.Interval;
import pl.radek.ss.domain.Participation;

import java.util.ArrayList;
import java.util.List;

public class IntervalProcessor
{
	public static Interval findOptimalInterval(List<Participation> participations, String duration)
	{
		List<Interval> intervalList = new ArrayList<>();
		for (Participation participation : participations) {
			List<Interval> intervals = participation.getIntervals();
            intervalList.addAll(intervals);
		} 
		
		return processIntervals(intervalList, duration);
	}
	
	public static Interval processIntervals(List<Interval> intervals, String duration)
	{
		List<Interval> wspolnePrzedzialy = new ArrayList<>();
		List<Integer> ilosc = new ArrayList<>();
		
		Object[] objTab = intervals.toArray();
		for (int i = 0; i < objTab.length; i++) {
			Interval interval = (Interval) objTab[i];
   
			if(objTab.length == 1 && Long.parseLong(duration) <= differenceIntervalInMinutes(interval)) {
				wspolnePrzedzialy.add(interval);
				ilosc.add(0);
			}
   
			for (int k = i+1; k < objTab.length; k++)
			{
				Interval intervalNext = (Interval) objTab[k];
				if(interval.equals(intervalNext)) {
					wspolnePrzedzialy.add(intervalNext);
					ilosc.add(0);
					continue;
				}
				
				Interval intervalTemp = compareIntervalsReturnCommon(interval, intervalNext);
				
                // There's common part and it's shorter than duration
				if(intervalTemp != null && Long.parseLong(duration) <= differenceIntervalInMinutes(intervalTemp) )
                    if (!wspolnePrzedzialy.contains(intervalTemp)) {
                        wspolnePrzedzialy.add(intervalTemp);
                        ilosc.add(0);
                    }
			}
		}
		
		if(!wspolnePrzedzialy.isEmpty())
		{
			// Count common intervals
			for (int i = 0; i < wspolnePrzedzialy.size(); i++)
                for (Interval interval : intervals)
                    if (isInnerInterval(wspolnePrzedzialy.get(i), interval))
                        ilosc.set(i, ilosc.get(i) + 1);
			
			int max = 0;
			int maxI = -1;
			for (int i = 0; i < ilosc.size(); i++)
                if (ilosc.get(i) > max) {
                    max = ilosc.get(i);
                    maxI = i;
                }
			
			return wspolnePrzedzialy.get(maxI);
		}
		else
            return null;
	}
	
	public static Long differenceIntervalInMinutes(Interval interval)
	{
		String intervalStart = interval.getStartDate() + "T" + interval.getStartTimestamp();
		String intervalEnd = interval.getEndDate() + "T" + interval.getEndTimestamp();
		
		return DateManagement.differenceTimestampsInMinutes(intervalStart, intervalEnd);
	}
	
	public static boolean isInnerInterval(Interval source, Interval target)
	{
		String sourceStart = source.getStartDate() + "T" + source.getStartTimestamp();
		String sourceEnd = source.getEndDate() + "T" + source.getEndTimestamp();
		String targetStart = target.getStartDate() + "T" + target.getStartTimestamp();
		String targetEnd = target.getEndDate() + "T" + target.getEndTimestamp();
		
		if( DateManagement.compareTimestamps(sourceStart, targetStart) <= 0 ) {
   
			if( DateManagement.compareTimestamps(sourceStart, targetEnd) <= 0 )
                return false;
			
			if( DateManagement.compareTimestamps(sourceStart, targetEnd) == 1 ) {
				if( DateManagement.compareTimestamps(sourceEnd, targetEnd) == -1 )
                    return false;
                
                return DateManagement.compareTimestamps(sourceEnd, targetEnd) >= 0;
			}
		}
		return false;
	}
	
	public static Interval compareIntervalsReturnCommon(Interval source, Interval target)
	{
		String sourceStart = source.getStartDate() + "T" + source.getStartTimestamp();
		String sourceEnd = source.getEndDate() + "T" + source.getEndTimestamp();
		String targetStart = target.getStartDate() + "T" + target.getStartTimestamp();
		String targetEnd = target.getEndDate() + "T" + target.getEndTimestamp();
		
		if( DateManagement.compareTimestamps(sourceStart, targetStart) == -1 ) {
			if( DateManagement.compareTimestamps(sourceStart, targetEnd) <= 0 )
                return null;
			
			if( DateManagement.compareTimestamps(sourceStart, targetEnd) == 1 ) {
				if( DateManagement.compareTimestamps(sourceEnd, targetEnd) <= 0 )
                    return new Interval(source.getStartDate(), source.getStartTimestamp(),
                            target.getEndDate(), target.getEndTimestamp());
				
				if( DateManagement.compareTimestamps(sourceEnd, targetEnd) >= 0 )
                    return new Interval(source.getStartDate(), source.getStartTimestamp(),
                            source.getEndDate(), source.getEndTimestamp());
			}
		}
		
		if( DateManagement.compareTimestamps(sourceStart, targetStart) == 1 )
		{
			if( DateManagement.compareTimestamps(sourceEnd, targetStart) >= 0)
                return null;
			
			if( DateManagement.compareTimestamps(sourceEnd, targetEnd) >= 0 )
                return new Interval(target.getStartDate(), target.getStartTimestamp(), source.getEndDate(),
                        source.getEndTimestamp());
            
            if( DateManagement.compareTimestamps(sourceEnd, targetEnd) <= 0 )
                return new Interval(target.getStartDate(), target.getStartTimestamp(), target.getEndDate(),
                        target.getEndTimestamp());
		}
		
		if( DateManagement.compareTimestamps(sourceStart, targetStart) == 0 )
		{
			if( DateManagement.compareTimestamps(sourceEnd, targetEnd) == 1 )
                return new Interval(target.getStartDate(), target.getStartTimestamp(), source.getEndDate(),
                        source.getEndTimestamp());
            
            if( DateManagement.compareTimestamps(sourceEnd, targetEnd) == -1 )
                return new Interval(target.getStartDate(), target.getStartTimestamp(), target.getEndDate(),
                        target.getEndTimestamp());
		}
		
		return null;
	}
}
