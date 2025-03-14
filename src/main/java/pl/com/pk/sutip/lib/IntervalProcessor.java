package pl.com.pk.sutip.lib;

import java.util.ArrayList;
import java.util.List;

import pl.com.pk.sutip.domain.Interval;
import pl.com.pk.sutip.domain.Participation;

public class IntervalProcessor
{
	/*
	 *  Oblicza najlepszy możliwy termin spotkania przy uwzględnieniu długości jego trwania
	 */
	public static Interval findOptimalInterval(List<Participation> participations, String duration)
	{
		List<Interval> intervalList = new ArrayList<Interval>();
		for (Participation participation : participations)
		{
			List<Interval> intervals = participation.getIntervals();
			for (Interval interval : intervals)
			{
				intervalList.add(interval);
			}
		} 
		
		return processIntervals(intervalList, duration);
	}
	
	/*
	 *  Przetwarza wszystkie przedziały w poszukiwaniu najlepszego
	 */
	public static Interval processIntervals(List<Interval> intervals, String duration)
	{
		List<Interval> wspolnePrzedzialy = new ArrayList<Interval>();
		List<Integer> ilosc = new ArrayList<Integer>();
		
		// Poszukiwanie wspónych przedziałów
		Object[] objTab = intervals.toArray();
		for (int i = 0; i < objTab.length; i++)
		{
			Interval interval = (Interval) objTab[i];
			if(objTab.length == 1 && Long.parseLong(duration) <= differenceInterval(interval))
			{
				wspolnePrzedzialy.add(interval);
				ilosc.add(0);
			}
			for (int k = i+1; k < objTab.length; k++)
			{
				Interval intervalNext = (Interval) objTab[k];
				if(interval.equals(intervalNext))
				{
					wspolnePrzedzialy.add(intervalNext);
					ilosc.add(0);
					continue;
				}
				
				Interval intervalTemp = compareIntervals(interval, intervalNext);
				
				// Jeśli istnieje część wspólna i jest krótsza w czasie niż parametr duration 
				if(intervalTemp != null && Long.parseLong(duration) <= differenceInterval(intervalTemp) )
				{
					if (!wspolnePrzedzialy.contains(intervalTemp))
					{
						wspolnePrzedzialy.add(intervalTemp);
						ilosc.add(0);
					}
				}
			}
			// System.out.println("------------------------------------------------");
		}
		
		if(wspolnePrzedzialy.size() > 0)
		{
			// Zliczanie ilości wystąpień każdego wspólnego przedziału
			for (int i = 0; i < wspolnePrzedzialy.size(); i++)
			{
				for (Interval interval : intervals)
				{
					if(isInnerInterval(wspolnePrzedzialy.get(i), interval))
					{
						ilosc.set(i, ilosc.get(i)+1);
					}
				}
			}
			
			// Sprawdzam największą ilość wystąpień przedziału i pobieram ten przedział‚
			int max = 0;
			int maxI = -1;
			for (int i = 0; i < ilosc.size(); i++)
			{
				if(ilosc.get(i) > max)
				{
					max = ilosc.get(i);
					maxI = i;
				}
			}
			
			return wspolnePrzedzialy.get(maxI);
		}
		else
		{
			return null;
		}
		
	}
	
	/*
	 *  Porównuje dwa przedziały czasu i zwraca różnicę w minutach między tymi przedziałami
	 */
	public static Long differenceInterval(Interval interval)
	{
		String intervalStart = interval.getStartDate() + "T" + interval.getStartTimestamp();
		String intervalEnd = interval.getEndDate() + "T" + interval.getEndTimestamp();
		
		return DateManagement.differenceTimestamps(intervalStart, intervalEnd);
	}
	
	/*
	 *  Sprawdza czy jeden przedziaĹ‚ zawiera siÄ™ w drugim
	 */
	public static boolean isInnerInterval(Interval source, Interval target)
	{
		String sourceStart = source.getStartDate() + "T" + source.getStartTimestamp();
		String sourceEnd = source.getEndDate() + "T" + source.getEndTimestamp();
		String targetStart = target.getStartDate() + "T" + target.getStartTimestamp();
		String targetEnd = target.getEndDate() + "T" + target.getEndTimestamp();
		
		// jeśli sourceStart jest później lub w tym samym punkcie niż targetStart 
		/*
		 * 	o--...   o---...
		 *  *---...     *---...
		 */
		if( DateManagement.compareTimestamps(sourceStart, targetStart) <= 0 )
		{
			// i sourceStart jest później lub w tym samym czasie niż targetEnd
			/*
			 * 	o--------o
			 * 		     *---...
			 */
			if( DateManagement.compareTimestamps(sourceStart, targetEnd) <= 0 )
			{
				return false;
			}
			
			// ale jeśli sourceStart jest wcześiej niż targetEnd
			/*
			 * 	o------o
			 * 		*----....
			 */
			if( DateManagement.compareTimestamps(sourceStart, targetEnd) == 1 )
			{
				// sprawdź czy sourceEnd jest później niż targetEnd
				/*
				 * 	o------o
				 * 	    *------*
				 */
				if( DateManagement.compareTimestamps(sourceEnd, targetEnd) == -1 )
				{
					return false;
				}
				
				// jeśli natomiast sourceEnd jest wcześniej lub w tym samym czasie niż targetEnd
				/*
				 * 	o----------o
				 * 	   *----*
				 */
				if( DateManagement.compareTimestamps(sourceEnd, targetEnd) >= 0 )
				{
					return true;
				}
			}
		}
		return false;
	}
	
	/*
	 *  PorĂłwnuje dwa przedziaĹ‚y czasu i zwraca wspĂłlny (null oznacza brak przedziaĹ‚u)
	 */
	public static Interval compareIntervals(Interval source, Interval target)
	{
		String sourceStart = source.getStartDate() + "T" + source.getStartTimestamp();
		String sourceEnd = source.getEndDate() + "T" + source.getEndTimestamp();
		String targetStart = target.getStartDate() + "T" + target.getStartTimestamp();
		String targetEnd = target.getEndDate() + "T" + target.getEndTimestamp();
		
		// String text = "cI: " + sourceStart + "  " + sourceEnd + "   " + targetStart + "  " + targetEnd + "  --->  ";
		// System.out.print(text.replaceAll("T", "  "));
		
		// jeĹ›li sourceStart jest pĂłĹşniej niĹĽ targetStart 
		/*
		 * 	o--...
		 * 		    *---...
		 */
		if( DateManagement.compareTimestamps(sourceStart, targetStart) == -1 )
		{
			// i sourceStart jest pĂłĹĽniej lub w tym samym czasie niĹĽ targetEnd
			/*
			 * 	o--------o
			 * 		     *---...
			 */
			if( DateManagement.compareTimestamps(sourceStart, targetEnd) <= 0 )
			{
				// przedziaĹ‚y nie majÄ… czÄ™Ĺ›ci wspĂłlnej
				return null;
			}
			
			// ale jeĹ›li sourceStart jest wczeĹ›niej niĹĽ targetEnd
			/*
			 * 	o------o
			 * 		*----....
			 */
			if( DateManagement.compareTimestamps(sourceStart, targetEnd) == 1 )
			{
				// sprawdĹş czy sourceEnd jest pĂłĹşniej lub w tym samym czasie niĹĽ targetEnd
				/*
				 * 	o------o
				 * 	    *------*
				 */
				if( DateManagement.compareTimestamps(sourceEnd, targetEnd) <= 0 )
				{
					// zwraca czÄ™Ĺ›c wspĂłlnÄ… -> sourceStart oraz targetEnd
					return new Interval(source.getStartDate(), source.getStartTimestamp(), target.getEndDate(), target.getEndTimestamp());
				}
				
				// jeĹ›li natomiast sourceEnd jest wczeĹ›niej lub w tym samym czasie niĹĽ targetEnd
				/*
				 * 	o----------o
				 * 	   *----*
				 */
				if( DateManagement.compareTimestamps(sourceEnd, targetEnd) >= 0 )
				{
					// zwraca czÄ™Ĺ›c wspĂłlnÄ… -> sourceStart oraz sourceEnd
					return new Interval(source.getStartDate(), source.getStartTimestamp(), source.getEndDate(), source.getEndTimestamp());
				}
			}
		}
		
		// jeĹ›li sourceStart jest wczeĹ›niej niĹĽ targetStart 
		/*
		 * 	 		o---....
		 *    *---....
		 */
		if( DateManagement.compareTimestamps(sourceStart, targetStart) == 1 )
		{
			// i sourceEnd jest wczeĹ›niej lub w tym samym czasie niĹĽ targetStart, ewentualnie wszystko jest takie same
			/*
			 * 			 o-----o      o------o
			 * 	 *----*               *------*
			 */
			if( DateManagement.compareTimestamps(sourceEnd, targetStart) >= 0)
			{
				// przedziaĹ‚y nie majÄ… czÄ™Ĺ›ci wspĂłlnej
				return null;
			}
			
			// jeĹ›li jednak sourceEnd jest wczeĹ›niej lub w tym samym czasie niĹĽ targetEnd
			/*
			 * 	   o-----o
			 * 	*-----*
			 */
			if( DateManagement.compareTimestamps(sourceEnd, targetEnd) >= 0 )
			{
				// zwraca czÄ™Ĺ›c wspĂłlnÄ… -> targetStart oraz sourceEnd
				return new Interval(target.getStartDate(), target.getStartTimestamp(), source.getEndDate(), source.getEndTimestamp());
			}
			
			// jeĹ›li jednak sourceEnd jest pĂłĹşniej lub w tym samym czasie niĹĽ targetEnd
			/*
			 * 	   o----o
			 * 	*----------*
			 */
			if( DateManagement.compareTimestamps(sourceEnd, targetEnd) <= 0 )
			{
				// zwraca czÄ™Ĺ›c wspĂłlnÄ… -> targetStart oraz targetEnd
				return new Interval(target.getStartDate(), target.getStartTimestamp(), target.getEndDate(), target.getEndTimestamp());
			}
		}
		
		
		// jeĹ›li sourceStart jest taki sam jak targetStart 
		/*
		 * 	  o---....
		 *    *---....
		 */
		if( DateManagement.compareTimestamps(sourceStart, targetStart) == 0 )
		{
			// jeĹ›li jednak sourceEnd jest wczeĹ›niej lub w tym samym czasie niĹĽ targetEnd
			/*
			 * 	o--------o
			 * 	*-----*
			 */
			if( DateManagement.compareTimestamps(sourceEnd, targetEnd) == 1 )
			{
				// zwraca czÄ™Ĺ›c wspĂłlnÄ… -> targetStart oraz sourceEnd
				return new Interval(target.getStartDate(), target.getStartTimestamp(), source.getEndDate(), source.getEndTimestamp());
			}
			
			// jeĹ›li jednak sourceEnd jest pĂłĹşniej lub w tym samym czasie niĹĽ targetEnd
			/*
			 * 	o----o
			 * 	*-------*
			 */
			if( DateManagement.compareTimestamps(sourceEnd, targetEnd) == -1 )
			{
				// zwraca czÄ™Ĺ›c wspĂłlnÄ… -> targetStart oraz targetEnd
				return new Interval(target.getStartDate(), target.getStartTimestamp(), target.getEndDate(), target.getEndTimestamp());
			}
		}
		
		// Są takie same
		return null;
	}
}
