package pl.com.pk.sutip.lib.schedule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class Algorithm
{
	// ------------------------------------------------------------------------------------------------------
	// Populacja chromosom�w
	private Vector<Schedule> chromosomes = new Vector<Schedule>();
	
	// Wektor flag wskazuj�cy na przynole�no�� do grupy najlepszych chromosom�w
	private Vector<Boolean> bestFlags = new Vector<Boolean>();

	// Wektor wskazuj�cy na pozycj� najlepszych chromosomy
	private Vector<Integer> bestChromosomes = new Vector<Integer>();

	// Ilo�� najlepszych chromosom�w
	private int currentBestSize = 0;

	// Ilo�� chromosom�w, kt�re musz� zosta� zast�pione z ka�d� reprodukcj� (generacj� potomk�w)
	private int replaceByGeneration = 8;

	// Prototyp chromosomu
	private Schedule prototype = null;

	// Licznik generacji
	private int currentGeneration = 0;

	private static Algorithm instance = null;
	private boolean endFlag = true;
	
	// Ustawienia algorytmu
	private static int max_generations 			= 30000;
	private static int log_generations 			= 100;
	private static int num_crossoverPoints 		= 2;
	private static int num_mutationSize 		= 2;
	private static int num_crossoverProbability = 90; // 80
	private static int num_mutationProbability 	= 1; // 3
	private static int num_chromosomes 			= 100;
	private static int num_replaceByGeneration 	= 8;
	private static int num_trackBest 			= 5;
	// ------------------------------------------------------------------------------------------------------
	public static Algorithm getInstance()
	{
		if( instance == null )
		{
			// Tworzy prototyp chromosom�w
			Schedule prototype = new Schedule( num_crossoverPoints, num_mutationSize, num_crossoverProbability, num_mutationProbability );
			
			// Inicjalizuje instancj�
			instance = new Algorithm( num_chromosomes, num_replaceByGeneration, num_trackBest, prototype);
		}
		
		return instance;
	}
	
	public Algorithm(int numberOfChromosomes, int replaceByGeneration, int trackBest, Schedule prototype)
	{
		this.replaceByGeneration = replaceByGeneration;
		this.prototype = prototype;
		this.currentBestSize = 0;
		this.currentGeneration = 0;
		
		// algorytm powinien �ledzi� przynajmniej jesten najlepszy chromosom
		if( trackBest < 1 )
		{
			trackBest = 1;
		}

		// wymagane s� przynajmniej 2 chromosomy w populacji
		if( numberOfChromosomes < 2 ) 
		{
			numberOfChromosomes = 2;
		}
		
		// algorytm powinien zast�pi� przynajmniej jeden chromosom
		if( replaceByGeneration < 1 )
		{
			replaceByGeneration = 1;
		}
		else if( replaceByGeneration > numberOfChromosomes - trackBest )
		{
			replaceByGeneration = numberOfChromosomes - trackBest;
		}
		
		chromosomes.setSize( numberOfChromosomes );
		bestFlags.setSize( numberOfChromosomes );
		bestChromosomes.setSize( trackBest );
		
		// inicjalizuje wektory
		for( int i = (int) chromosomes.size() - 1; i >= 0; --i )
		{
			chromosomes.set(i, null);
			bestFlags.set(i, false);
		}
	}
	
	public String start()
	{
		String xml = null;
		endFlag = true;
		do
		{
			if( prototype == null )
				return xml;
			
			// ustawia flagi (false) dla wszystkich chromosom�w i zeruje licznik najlepszych chromosom�w
			clearBest(); 
	
			// inicjalizuje populacj� losowo utworzonych chromosom�w na podstawie prototypu
			for (int i = 0; i < chromosomes.size(); ++i)
			{
				chromosomes.set(i, prototype.makeNewChromosome());
				addToBest(i);
			}
			
			currentGeneration = 0;
	
			while(true)
			{
				Schedule bestChromosome = getBestChromosome();
	
				// sprawdza czy algorytm spe�ni� wymagania
				if( bestChromosome.getFitness() >= 1 )
				{
					endFlag = false;
					
					List<Pair> listEvents = bestChromosome.getEvents();
					Vector<List<EventSchedule>> slots = bestChromosome.getSlots();
					
					xml = toXml(slots, listEvents);
					break;
				}
	
				// reprodukcja (potomstwo)
				Vector<Schedule> offspring = new Vector<Schedule>();
				offspring.setSize( replaceByGeneration );
				for( int i = 0; i < replaceByGeneration; i++ )
				{
					// losuje rodzic�w
					Schedule p1 = chromosomes.get(random(0, chromosomes.size()-1));
					Schedule p2 = chromosomes.get(random(0, chromosomes.size()-1));
					offspring.set(i, p1.crossover(p2));
					offspring.get(i).mutation();
				}
	
				// zast�puje bie��ce chromosomy potomkami
				for( int i = 0; i < replaceByGeneration; i++ )
				{
					int chromosomId = 0;
					do
					{
						// wybiera losowo chromosom do podmiany
						chromosomId = random(0, chromosomes.size()-1);
					} 
					// chroni najlepsze chromosomy przed podmian�
					while( isInBest( chromosomId ) );
	
					// zast�puje chromosomy
					chromosomes.set(chromosomId, offspring.get(i));
					
					// pr�buje doda� nowy chromosom do grupy najlepszych
					addToBest( chromosomId );
				}
	
				if(log_generations != 0 && (getCurrentGeneration() % log_generations == 0))
				{
					System.out.println("fitness: " + chromosomes.get(bestChromosomes.get(0)).getFitness() + " | " + getCurrentGeneration());
				}
				currentGeneration++;
				if(getCurrentGeneration() == max_generations)
				{
					endFlag = false;
					
					List<Pair> listEvents = bestChromosome.getEvents();
					Vector<List<EventSchedule>> slots = bestChromosome.getSlots();
					
					xml = toXml(slots, listEvents);
					break;
				}
			}
		}
		while(endFlag);
		return xml;
	}

	private void addToBest(int chromosomeIndex)
	{
		// Nie dodawaj je�li jako�� nowego chromosomu nie spe�nia oczekiwa� (funkcja oceny) ca�ej grupy najlpeszych lub jest ju� w grupie
		if( (currentBestSize == bestChromosomes.size() && 
			chromosomes.get(bestChromosomes.get(currentBestSize-1)).getFitness() >= chromosomes.get(chromosomeIndex).getFitness()) ||
			bestFlags.get(chromosomeIndex) )
		{
			return;
		}
		
		// Znajduje miejsce dla nowego chromosomu
		int i = currentBestSize;
		for( ; i > 0; i-- )
		{
			// grupa nie jest pe�na?
			if( i < bestChromosomes.size() )
			{
				// pozycja nowego chromosomu zosta�a znaleziona?
				if( chromosomes.get(bestChromosomes.get(i-1)).getFitness() > chromosomes.get(chromosomeIndex).getFitness() )
					break;

				// przesu� chromosom,a by zrobi� miejsce dla nowego
				bestChromosomes.set(i, bestChromosomes.get(i-1));
			}
			// grupa jest pe�na, usuwa najgorsze chromosomy w grupie
			else
				bestFlags.set(bestChromosomes.get(i-1), false);
		}
		
		// Utrzymuje chromosom w grupie najlepszych
		bestChromosomes.set(i, chromosomeIndex);
		bestFlags.set(chromosomeIndex, true);

		if( currentBestSize < bestChromosomes.size() )
		{
			currentBestSize++;
		}
	}

	private Boolean isInBest(int chromosomeIndex)
	{
		return bestFlags.get(chromosomeIndex);
	}

	private void clearBest()
	{
		for(int i = (int) bestFlags.size() - 1; i >= 0; --i )
		{
			bestFlags.set(i, false);
		}

		currentBestSize = 0;
	}
	
	public Schedule getBestChromosome()
	{
		return chromosomes.get(bestChromosomes.get(0));
	}

	public int getCurrentGeneration() 
	{ 
		return currentGeneration;
	}
	
	// generuje losow� liczb� z przedzia�u <lo, hi>
	public static int random(int lo, int hi)
    {
		Random random = new Random();
        int n = hi - lo + 1;
        int i = random.nextInt() % n;
        if (i < 0)
                i = -i;
        return lo + i;
    }
	
	// zwraca plan zaj�c do formatu xml
	public String toXml(Vector<List<EventSchedule>> slots, List<Pair> listClasses)
	{
		String tab = "   ";
		StringBuilder builder = new StringBuilder();
		builder.append("<schedule>").append("\n");
		List<Group> studenci = new ArrayList<Group>();
		
		int count = 0;
		for (Iterator<Pair> iterator = listClasses.iterator(); iterator.hasNext();)
		{
			Pair pair = (Pair) iterator.next();
			EventSchedule cc = pair.getKey();
			for (Iterator<Group> it = cc.getGroups().iterator(); it.hasNext();)
			{
				Group st = (Group) it.next();
				if(!studenci.contains(st))
				{
					studenci.add(st);
				}
			}
			count++;
		}
		
		builder.append(tab).append("<groups>").append("\n");
		
		count = 0;
		for (Iterator<Group> iterator = studenci.iterator(); iterator.hasNext();)
		{
			count = 0;
			Group studentsGroup = (Group) iterator.next();
			builder.append(tab).append(tab).append("<group>").append("\n");
			builder.append(tab).append(tab).append(tab).append("<id>").append(studentsGroup.getId()).append("</id>").append("\n");
			builder.append(tab).append(tab).append(tab).append("<name>").append(studentsGroup.getName()).append("</name>").append("\n");
			
			for (Iterator<List<EventSchedule>> iteratorSlot = slots.iterator(); iteratorSlot.hasNext();)
			{
				List<EventSchedule> slot = (List<EventSchedule>) iteratorSlot.next();
				for (Iterator<EventSchedule> it = slot.iterator(); it.hasNext();)
				{
					EventSchedule eventSchedule = (EventSchedule) it.next();
					if(eventSchedule != null)
					{
						if(eventSchedule.getGroups().contains(studentsGroup))
						{
							builder.append(tab).append(tab).append(tab).append("<event>").append("\n");
							
							// Minialna godzina pracy (dolna granica)
							int minHour = Schedule.MIN_HOUR;
							int nrRooms = Configuration.getInstance().getNumberOfRooms(); 
							int daySlot = Schedule.HOURS * nrRooms; 
							
							// Poniedzia�ek
							if((0 <= count && count < daySlot))
							{
								// Godzina?
								int hour = (count % Schedule.HOURS) + minHour;
								builder.append(tab).append(tab).append(tab).append(tab).append("<timeId>").append(hour-minHour).append("</timeId>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<timeDay>").append("0").append("</timeDay>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<time>").append(hour + "-" + (hour+1)).append("</time>").append("\n");
								// Pok�j?
								int roomId = (count / Schedule.HOURS) % nrRooms;
								Room room = Configuration.getInstance().getRoomById( roomId );
								builder.append(tab).append(tab).append(tab).append(tab).append("<room>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<id>").append(room.getId()).append("</id>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<name>").append(room.getName()).append("</name>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<nrSeats>").append(room.getNumberOfSeats()).append("</nrSeats>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("</room>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<course>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<prof>").append(eventSchedule.getPromoter().getName()).append("</prof>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<name>").append(eventSchedule.getCourse().getName()).append("</name>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<duration>").append(eventSchedule.getDuration()).append("</duration>").append("\n");	
								builder.append(tab).append(tab).append(tab).append(tab).append("</course>").append("\n");
							}
							
							// Wtorek
							if((daySlot <= count && count < daySlot*2))
							{
								// Godzina?
								int hour = (count % Schedule.HOURS)+minHour;
								builder.append(tab).append(tab).append(tab).append(tab).append("<timeId>").append(hour-minHour).append("</timeId>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<timeDay>").append("1").append("</timeDay>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<time>").append(hour + "-" + (hour+1)).append("</time>").append("\n");
								// Pok�j?
								int roomId = (count / Schedule.HOURS) % nrRooms;
								Room room = Configuration.getInstance().getRoomById( roomId );
								builder.append(tab).append(tab).append(tab).append(tab).append("<room>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<id>").append(room.getId()).append("</id>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<name>").append(room.getName()).append("</name>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<nrSeats>").append(room.getNumberOfSeats()).append("</nrSeats>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("</room>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<course>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<prof>").append(eventSchedule.getPromoter().getName()).append("</prof>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<name>").append(eventSchedule.getCourse().getName()).append("</name>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<duration>").append(eventSchedule.getDuration()).append("</duration>").append("\n");	
								builder.append(tab).append(tab).append(tab).append(tab).append("</course>").append("\n");
							}
							
							// Sroda
							if((daySlot*2 <= count && count < daySlot*3))
							{
								// Godzina?
								int hour = (count % Schedule.HOURS)+minHour;
								builder.append(tab).append(tab).append(tab).append(tab).append("<timeId>").append(hour-minHour).append("</timeId>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<timeDay>").append("2").append("</timeDay>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<time>").append(hour + "-" + (hour+1)).append("</time>").append("\n");
								// Pok�j?
								int roomId = (count / Schedule.HOURS) % nrRooms;
								Room room = Configuration.getInstance().getRoomById( roomId );
								builder.append(tab).append(tab).append(tab).append(tab).append("<room>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<id>").append(room.getId()).append("</id>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<name>").append(room.getName()).append("</name>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<nrSeats>").append(room.getNumberOfSeats()).append("</nrSeats>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("</room>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<course>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<prof>").append(eventSchedule.getPromoter().getName()).append("</prof>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<name>").append(eventSchedule.getCourse().getName()).append("</name>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<duration>").append(eventSchedule.getDuration()).append("</duration>").append("\n");	
								builder.append(tab).append(tab).append(tab).append(tab).append("</course>").append("\n");
							}
							
							// Czwartek
							if((daySlot*3 <= count && count < daySlot*4))
							{
								// Godzina?
								int hour = (count % Schedule.HOURS)+minHour;
								builder.append(tab).append(tab).append(tab).append(tab).append("<timeId>").append(hour-minHour).append("</timeId>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<timeDay>").append("3").append("</timeDay>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<time>").append(hour + "-" + (hour+1)).append("</time>").append("\n");
								
								// Pok�j?
								int roomId = (count / Schedule.HOURS) % nrRooms;
								Room room = Configuration.getInstance().getRoomById( roomId );
								builder.append(tab).append(tab).append(tab).append(tab).append("<room>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<id>").append(room.getId()).append("</id>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<name>").append(room.getName()).append("</name>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<nrSeats>").append(room.getNumberOfSeats()).append("</nrSeats>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("</room>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<course>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<prof>").append(eventSchedule.getPromoter().getName()).append("</prof>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<name>").append(eventSchedule.getCourse().getName()).append("</name>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<duration>").append(eventSchedule.getDuration()).append("</duration>").append("\n");	
								builder.append(tab).append(tab).append(tab).append(tab).append("</course>").append("\n");
							}
							
							// Piatek
							if((daySlot*4 <= count && count < daySlot*5))
							{
								// Godzina?
								int hour = (count % Schedule.HOURS)+minHour;
								builder.append(tab).append(tab).append(tab).append(tab).append("<timeId>").append(hour-minHour).append("</timeId>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<timeDay>").append("4").append("</timeDay>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<time>").append(hour + "-" + (hour+1)).append("</time>").append("\n");
								
								// Pok�j?
								int roomId = (count / Schedule.HOURS) % nrRooms;
								Room room = Configuration.getInstance().getRoomById( roomId );
								builder.append(tab).append(tab).append(tab).append(tab).append("<room>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<id>").append(room.getId()).append("</id>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<name>").append(room.getName()).append("</name>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<nrSeats>").append(room.getNumberOfSeats()).append("</nrSeats>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("</room>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<course>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<prof>").append(eventSchedule.getPromoter().getName()).append("</prof>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<name>").append(eventSchedule.getCourse().getName()).append("</name>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append(tab).append("<duration>").append(eventSchedule.getDuration()).append("</duration>").append("\n");	
								builder.append(tab).append(tab).append(tab).append(tab).append("</course>").append("\n");
							}
							builder.append(tab).append(tab).append(tab).append("</event>").append("\n");
						}
					}
				}
				count++;
			}
			builder.append(tab).append(tab).append("</group>").append("\n");
		}
		System.out.println();
		builder.append(tab).append("</groups>").append("\n");
		builder.append("</schedule>");
		return builder.toString();
	}
}
