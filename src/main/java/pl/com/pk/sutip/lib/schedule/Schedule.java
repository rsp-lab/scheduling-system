package pl.com.pk.sutip.lib.schedule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class Schedule
{
	// ------------------------------------------------------------------------------------------------------
	// ilo�� fragment�w do krzy�owania mi�dzy rodzicami przy reprodukcji
	private int numberOfCrossoverPoints;			
	
	// ilość spotkań (EventSchedule), kt�re zostan� losowo przesuni�te podczas pojedynczej mutacji
	private int mutationSize;		
	
	// prawdopodobie�stwo krzy�owania
	private int crossoverProbability;		
	
	// prawdopodobie�stwo wyst�pienia mutacji
	private int mutationProbability;	
	
	// ocena chromosomu
	private float fitness;				
	
	// lista wymaga� spe�nionych przez chromosom
	private Vector<Boolean> criteria = new Vector<Boolean>();	
	
	// wektor przechowywyj�cy klasy w czasie (EventSchedule), ka�dy element odpowiada domy�lnie 1 godzinie
	private Vector<List<EventSchedule>> slots = new Vector<List<EventSchedule>>();
	
	// lista wskazuj�ca na pozycje klasy (EventSchedule) w powy�szym wektorze (slots)
	private List<Pair> events = new ArrayList<Pair>();		
	
	// Ilo�� godzin na dzie� w planie zaj��
	public static int HOURS = 12;
	// Ilo�� dni
	public static int DAYS = 5;
	// Minimalna godzina pracy
	public static int MIN_HOUR = 9;
	// ------------------------------------------------------------------------------------------------------
	public Schedule(int numberOfCrossoverPoints, int mutationSize, int crossoverProbability, int mutationProbability)
	{
		this.numberOfCrossoverPoints = numberOfCrossoverPoints;
		this.mutationSize = mutationSize;
		this.crossoverProbability = crossoverProbability;
		this.mutationProbability = mutationProbability;
		this.fitness = 0;

		slots.setSize( DAYS * HOURS * Configuration.getInstance().getNumberOfRooms() );
		initializeSlots();
		
		criteria.setSize( Configuration.getInstance().getNumberOfEvents() * 6 );
		initializeCriteria();
	}
	
	public Schedule(Schedule schedule, boolean copy)
	{
		if( copy )
		{
			slots = schedule.slots;
			events = schedule.events;
			criteria = schedule.criteria;
			fitness = schedule.fitness;
		}
		else
		{
			slots.setSize( DAYS * HOURS * Configuration.getInstance().getNumberOfRooms() );
			initializeSlots();
			
			criteria.setSize( Configuration.getInstance().getNumberOfEvents() * 6 );
			initializeCriteria();
		}

		numberOfCrossoverPoints = schedule.numberOfCrossoverPoints;
		mutationSize = schedule.mutationSize;
		crossoverProbability = schedule.crossoverProbability;
		mutationProbability = schedule.mutationProbability;
	}

	public Schedule makeNewChromosome()
	{
		Schedule newChromosome = new Schedule( this, false );

		// ustawia klasy na losowych pozycjach
		List<EventSchedule> EventsSchedule = Configuration.getInstance().getEventSchedule();
		for (Iterator<EventSchedule> iterator = EventsSchedule.iterator(); iterator.hasNext();)
		{
			EventSchedule eventSchedule = (EventSchedule) iterator.next();
			int nr = Configuration.getInstance().getNumberOfRooms();
			int dur = eventSchedule.getDuration();
			int day = Algorithm.random(0, DAYS-1);
			int room = Algorithm.random(0, nr-1);
			int time = Algorithm.random(0, (HOURS + 1 - dur)-1);
			int pos = day * nr * HOURS + room * HOURS + time;
			
			// wype�nia wektor czasu
			for( int i = dur - 1; i >= 0; i-- )
			{
				newChromosome.getSlots().get(pos+i).add(eventSchedule);
			}
			newChromosome.events.add(new Pair(eventSchedule, pos));
		}

		newChromosome.calculateFitness();
		return newChromosome;
	}

	public Schedule crossover(Schedule parent)
	{
		Schedule offspring = null;
		
		// sprawdza prawdopodobie�stwo wyst�pienia krzy�owania
		if( Algorithm.random(0, 100-1) > crossoverProbability)
		{
			return new Schedule( this, true );
		}
		else
		{
			offspring = new Schedule( this, false );
		}

		int eventsSize = events.size();
		
		// Wektor pomocniczy do okre�lenia fragment�w rodzic�w (chromosomy) s�u��cych do krzy�owania
		Vector<Boolean> cp = new Vector<Boolean>(eventsSize);
		for (int i = 0; i < eventsSize; i++)
		{
			cp.add(false);
		}
		
		// fragment do krzy�owania wyznaczany jest losowo
		for( int i = numberOfCrossoverPoints; i > 0; i-- )
		{
			while( true )
			{
				int p = Algorithm.random(0, eventsSize-1);
				if( !cp.get(p) )
				{
					cp.set(p, true);
					break;
				}
			}
		}

		Iterator<Pair> it1 = events.iterator();
		Iterator<Pair> it2 = parent.events.iterator();
		
		// wyznacza losowo rodzica
		boolean firstParent = Algorithm.random(0, 1) == 0;
		for( int i = 0; i < eventsSize; i++ )
		{
			Pair pair1 = it1.next();
			EventSchedule first1 = pair1.getKey();
			int second1 = pair1.getValue();
			Pair pair2 = it2.next();
			EventSchedule first2 = pair2.getKey();
			int second2 = pair2.getValue();
			
			if( firstParent )
			{
				// uzupe�nia dane nowopowsta�emu chromosomowi
				offspring.events.add(new Pair(first1, second1));
				for( int k = first1.getDuration() - 1; k >= 0; k-- )
					offspring.slots.get(second1+k).add(first1);
			}
			else
			{
				// uzupe�nia dane nowopowsta�emu chromosomowi
				offspring.events.add(new Pair(first2, second2));
				for( int k = first2.getDuration() - 1; k >= 0; k-- )
					offspring.slots.get(second2+k).add( first2 );
			}
			
			if( cp.get(i) )
			{
				firstParent = !firstParent;
			}
		}
		
		offspring.calculateFitness();

		return offspring;
	}

	public void mutation()
	{
		// sprawdza prawdopodobie�stwo wyst�pienia mutacji
		if( Algorithm.random(0, 100-1) > mutationProbability)
		{
			return;
		}

		int classesSize = events.size();
		
		// przesuwa losowo odpowiedni� ilo�� klas (EventSchedule) 
		for( int i = mutationSize; i > 0; i-- )
		{
			// pobiera losowo chromosom
			int randomPos = Algorithm.random(0, classesSize-1);
			int chromosomPosition = 0;
			
			Iterator<Pair> iterator = events.iterator(); 
			Pair pair = iterator.next();
			EventSchedule first = pair.getKey();
			int second =  pair.getValue();
			for( ; randomPos > 0; randomPos-- )
			{
				pair = iterator.next();
				first = pair.getKey();
				second = pair.getValue();
			}
			
			chromosomPosition = second;
			EventSchedule randomCourseClass = first;

			// wyznacza pozycj� losowo
			int nr = Configuration.getInstance().getNumberOfRooms();
			int dur = randomCourseClass.getDuration();
			int day = Algorithm.random(0, DAYS-1);
			int room = Algorithm.random(0, nr-1);
			int time = Algorithm.random(0, (HOURS + 1 - dur)-1);
			int pos2 = day * nr * HOURS + room * HOURS + time;
			
			for( int k = dur - 1; k >= 0; k-- )
			{
				// usuwa wpis z wektora czasu
				List<EventSchedule> cl = slots.get(chromosomPosition+k);
				for (Iterator<EventSchedule> it = cl.iterator(); it.hasNext();)
				{
					EventSchedule eventSchedule = (EventSchedule) it.next();
					if( eventSchedule.equals(randomCourseClass) )
					{
						cl.remove(eventSchedule);
						break;
					}
				}

				// przesuwa klas� na now� pozycj� w wektorze czasu
				slots.get(pos2+k).add( randomCourseClass );
			}

			// modyfikuje list� wskazuj�c� na pozycj� w wektorze czasu
			Pair.setEventScheduleValueById(events, randomCourseClass.getId(), pos2);
		}

		calculateFitness();
	}

	// odpowiada za obliczenie oceny chromosomu (czy spe�nia wszystkie kryteria)
	public void calculateFitness()
	{
		int score = 0;
		int numberOfRooms = Configuration.getInstance().getNumberOfRooms();
		int daySize = HOURS * numberOfRooms;
		int criteriaPosition = 0;

		// sprawdza wymagania i dokonuje oceny ka�dej klasy (EventSchedule) w chromosomie
		for (Iterator<Pair> iterator = events.iterator(); iterator.hasNext(); criteriaPosition += 6)
		{
			Pair pair = iterator.next();
			EventSchedule first = pair.getKey();
			int second = pair.getValue();
			
			int p = second;
			int day = p / daySize;
			int time = p % daySize;
			int roomId = time / HOURS;
			time = time % HOURS;
			
			int duration = first.getDuration();
			
			// sprawdza nak�adanie si� klas w jednym momencie
			boolean roomOverlap = false;
			for( int i = duration - 1; i >= 0; i-- )
			{
				if( slots.get(p+i).size() > 1 )
				{
					roomOverlap = true;
					break;
				}
			}

			if( !roomOverlap )
				score++;

			criteria.set(criteriaPosition+0, !roomOverlap);
			
			EventSchedule cc = first;
			Room room = Configuration.getInstance().getRoomById( roomId );
			
			// sprawdza czy pok�j posiada odpowiedni� ilo�� miejsc
			criteria.set(criteriaPosition+1, room.getNumberOfSeats() >= cc.getNumberOfSeats());
			if( criteria.get(criteriaPosition+1) )
				score++;

			// sprawdza czy pok�j posiada wymagany sprz�t
			criteria.set(criteriaPosition+2, !cc.isLabRequired() || ( cc.isLabRequired() && room.isLab() ));
			if( criteria.get(criteriaPosition+2) )
				score++;
			
			boolean profOverlap = false;
			boolean groupOverlap = false;
			
			// sprawdzenie czy ogranizator prowadzi w tym samym czasie inny przedmiot
			// sprawdzenie czy grupa musi ucz�szcza� na inne spotkanie w tym samym czasie
			boolean flag = false;
			for( int i = numberOfRooms, t = day * daySize + time; i > 0; i--, t += HOURS )
			{
				if(flag)
				{
					break;
				}
				for( int k = duration - 1; k >= 0; k-- )
				{
					if(flag)
					{
						break;
					}
					List<EventSchedule> cl = slots.get(t+1);
					for (Iterator<EventSchedule> it = cl.iterator(); it.hasNext();)
					{
						if(flag)
						{
							break;
						}
						EventSchedule eventSchedule = (EventSchedule) it.next();
						if( !cc.equals(eventSchedule) )
						{
							// sprawdza nak�adanaie si� profesor�w
							if( !profOverlap && cc.promoterOverlaps(eventSchedule) )
								profOverlap = true;

							// sprawdza nak�adanie si� grup
							if( !groupOverlap && cc.groupsOverlap(eventSchedule) )
								groupOverlap = true;

							if( profOverlap && groupOverlap )
							{
								flag = true;
								break;
							}
						}
						
					}
				}
			}
			
			if( !profOverlap )
				score++;
			criteria.set(criteriaPosition+3, !profOverlap);

			if( !groupOverlap )
				score++;
			criteria.set(criteriaPosition+4, !groupOverlap); 
			
			// �ciskanie spotka�
			boolean nearEvent = false;
			
//			System.out.println("EVENT: " + first);
//			System.out.println("DURATION: " + first.getDuration());
//			System.out.println("P: " + p);
//			System.out.println("DAY: " + day); // 0 - 4 prawdopodobnie
//			System.out.println("TIME:" + time); // 0 - 11 prawdopodonie
			
//			System.out.println("if slots get (" + (p + first.getDuration()) + ")");
//			System.out.println("if slots get (" + (p-1) + ")");
			if( (p+duration < DAYS * HOURS * Configuration.getInstance().getNumberOfRooms()) && (slots.get(p+duration).size() > 1))
			{
//				System.out.println("near event po !");
//				EventSchedule sch = slots.get(p+duration).get(0);
//				System.out.println("schedule: " + sch);
				int schP = p+duration;
				int schDay = schP / daySize;
				int schTime = schP % daySize;
				schTime = schTime % HOURS;
//				System.out.println("schedule p:" + schP);
//				System.out.println("schedule day:" + schDay);
//				System.out.println("schedule time:" + schTime);
//				System.out.println("if (day " + day + " == schDay " + schDay + ")");
				if(day == schDay)
				{
					nearEvent = true;
				}
			}
			else if((p != 0) && (slots.get(p-1).size() > 1))
			{
//				System.out.println("near event przed !");
//				EventSchedule sch = slots.get(p-1).get(0);
//				System.out.println("schedule: " + sch);
				int schP = p-1;
				int schDay = schP / daySize;
				int schTime = schP % daySize;
				schTime = schTime % HOURS;
//				System.out.println("schedule p:" + schP);
//				System.out.println("schedule day:" + schDay);
//				System.out.println("schedule time:" + schTime);
				
//				System.out.println("if (day " + day + " == schDay " + schDay + ")");
				if(day == schDay)
				{
					nearEvent = true;
				}
			}
			
			
//			for( int i = duration - 1; i >= 0; i-- )
//			{
//				if( slots.get(p+i).size() > 1 )
//				{
//					roomOverlap = true;
//					break;
//				}
//			}
			
			if( nearEvent )
				score++;
			criteria.set(criteriaPosition+5, nearEvent); 
		}
		
		// obliczanie oceny chromosomu
		fitness = (float) score / ( Configuration.getInstance().getNumberOfEvents() * 6 );
	}

	private void initializeSlots()
	{
		for (int i = 0; i < slots.size(); i++)
		{
			slots.set(i, new ArrayList<EventSchedule>());
		}
	}
	
	private void initializeCriteria()
	{
		for (int i = 0; i < criteria.size(); i++)
		{
			criteria.set(i, false);
		}
	}
	
	// ------------------------------------------------------------------------------------------------------
	public float getFitness()
	{ 
		return fitness;
	}
	
	public void setFitness(float fitness)
	{
		this.fitness = fitness;
	}
	
	public List<Pair> getEvents()
	{
		return events;
	}

	public void setEvents(List<Pair> events)
	{
		this.events = events;
	}
	
	public Vector<Boolean> getCriteria()
	{ 
		return criteria; 
	}
	
	public void setCriteria(Vector<Boolean> criteria)
	{
		this.criteria = criteria;
	}

	public Vector<List<EventSchedule>> getSlots()
	{ 
		return slots;
	}
	
	public void setSlots(Vector<List<EventSchedule>> slots)
	{ 
		this.slots = slots;
	}
}
