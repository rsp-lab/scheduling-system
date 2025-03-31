package pl.radek.ss.lib.schedule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class Schedule
{
	// ------------------------------------------------------------------------------------------------------
	// Number of segments for crossover between parents during reproduction
	private int numberOfCrossoverPoints;			
	
	// Number of events (EventSchedule) that will be randomly shifted during a single mutation
	private int mutationSize;		
	
	// Crossover probability
	private int crossoverProbability;		
	
	// Mutation probability
	private int mutationProbability;	
	
	// Chromosome evaluation
	private float fitness;				
	
	// List of requirements met by the chromosome
	private Vector<Boolean> criteria = new Vector<>();
	
	// Stores classes over time (EventSchedule), with each element corresponding by default to one hour
	private Vector<List<EventSchedule>> slots = new Vector<>();
	
	// List indicating the positions of classes (EventSchedule) in the above list (slots)
	private List<Pair> events = new ArrayList<>();
	
	// Number of elements per day in the meeting schedule
	public static int HOURS = 12;
	public static int DAYS = 5;
	public static int MIN_HOUR = 9;
	// ------------------------------------------------------------------------------------------------------
	public Schedule(int numberOfCrossoverPoints, int mutationSize, int crossoverProbability, int mutationProbability) {
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
	
	public Schedule(Schedule schedule, boolean copy) {
		if(copy) {
			slots = schedule.slots;
			events = schedule.events;
			criteria = schedule.criteria;
			fitness = schedule.fitness;
		}
		else {
			slots.setSize(DAYS * HOURS * Configuration.getInstance().getNumberOfRooms());
			initializeSlots();
			
			criteria.setSize( Configuration.getInstance().getNumberOfEvents() * 6);
			initializeCriteria();
		}

		numberOfCrossoverPoints = schedule.numberOfCrossoverPoints;
		mutationSize = schedule.mutationSize;
		crossoverProbability = schedule.crossoverProbability;
		mutationProbability = schedule.mutationProbability;
	}

	public Schedule makeNewChromosome() {
		Schedule newChromosome = new Schedule( this, false );

		// Set classes in random positions
		List<EventSchedule> EventsSchedule = Configuration.getInstance().getEventSchedule();
		for (Iterator<EventSchedule> iterator = EventsSchedule.iterator(); iterator.hasNext();) {
			EventSchedule eventSchedule = iterator.next();
			int nr = Configuration.getInstance().getNumberOfRooms();
			int dur = eventSchedule.getDuration();
			int day = Algorithm.random(0, DAYS-1);
			int room = Algorithm.random(0, nr-1);
			int time = Algorithm.random(0, (HOURS + 1 - dur)-1);
			int pos = day * nr * HOURS + room * HOURS + time;
			
			// Fill the time vector
			for( int i = dur - 1; i >= 0; i-- )
                newChromosome.getSlots().get(pos + i).add(eventSchedule);
            
			newChromosome.events.add(new Pair(eventSchedule, pos));
		}

		newChromosome.calculateFitness();
		return newChromosome;
	}

	public Schedule crossover(Schedule parent) {
		Schedule offspring = null;
		
		// Check the probability of crossover occurrence
		if( Algorithm.random(0, 100-1) > crossoverProbability)
            return new Schedule(this, true);
		else
            offspring = new Schedule(this, false);

		int eventsSize = events.size();
		
		// Auxiliary vector for determining parent fragments (chromosomes) used for crossover
		Vector<Boolean> cp = new Vector<>(eventsSize);
		for (int i = 0; i < eventsSize; i++)
            cp.add(false);
		
		// Crossover segment is determined randomly
		for(int i = numberOfCrossoverPoints; i > 0; i--) {
			while(true) {
				int p = Algorithm.random(0, eventsSize-1);
				if( !cp.get(p) ) {
					cp.set(p, true);
					break;
				}
			}
		}

		Iterator<Pair> it1 = events.iterator();
		Iterator<Pair> it2 = parent.events.iterator();
		
		// Select a parent randomly
		boolean firstParent = Algorithm.random(0, 1) == 0;
		for(int i = 0; i < eventsSize; i++) {
			Pair pair1 = it1.next();
			EventSchedule first1 = pair1.getKey();
			int second1 = pair1.getValue();
			Pair pair2 = it2.next();
			EventSchedule first2 = pair2.getKey();
			int second2 = pair2.getValue();
			
            // Fill in data for the newly created chromosome
			if(firstParent)	{
				offspring.events.add(new Pair(first1, second1));
				for( int k = first1.getDuration() - 1; k >= 0; k-- )
					offspring.slots.get(second1+k).add(first1);
			}
			else {
				offspring.events.add(new Pair(first2, second2));
				for( int k = first2.getDuration() - 1; k >= 0; k-- )
					offspring.slots.get(second2+k).add( first2 );
			}
			
			if(cp.get(i))
                firstParent = !firstParent;
		}
		
		offspring.calculateFitness();

		return offspring;
	}

	public void mutation()
	{
		if(Algorithm.random(0, 100-1) > mutationProbability)
            return;

		int classesSize = events.size();
		
		// Randomly shift the appropriate number of classes (EventSchedule)
		for(int i = mutationSize; i > 0; i--)
		{
			// Select a chromosome randomly
			int randomPos = Algorithm.random(0, classesSize-1);
			int chromosomPosition = 0;
			
			Iterator<Pair> iterator = events.iterator(); 
			Pair pair = iterator.next();
			EventSchedule first = pair.getKey();
			int second =  pair.getValue();
			for(; randomPos > 0; randomPos--) {
				pair = iterator.next();
				first = pair.getKey();
				second = pair.getValue();
			}
			
			chromosomPosition = second;
			EventSchedule randomCourseClass = first;

			// Determine the position randomly
			int nr = Configuration.getInstance().getNumberOfRooms();
			int dur = randomCourseClass.getDuration();
			int day = Algorithm.random(0, DAYS-1);
			int room = Algorithm.random(0, nr-1);
			int time = Algorithm.random(0, (HOURS + 1 - dur)-1);
			int pos2 = day * nr * HOURS + room * HOURS + time;
			
			for(int k = dur - 1; k >= 0; k--) {
				// Remove an entry from the time vector
				List<EventSchedule> cl = slots.get(chromosomPosition+k);
				for (Iterator<EventSchedule> it = cl.iterator(); it.hasNext();) {
					EventSchedule eventSchedule = it.next();
					if(eventSchedule.equals(randomCourseClass)) {
						cl.remove(eventSchedule);
						break;
					}
				}

				// Move a class to a new position in the time vector
				slots.get(pos2+k).add(randomCourseClass);
			}

			// Modify the list indicating positions in the time vector
			Pair.setEventScheduleValueById(events, randomCourseClass.getId(), pos2);
		}

		calculateFitness();
	}

	// Responsible for calculating the chromosome evaluation (whether it meets all criteria)
	public void calculateFitness() {
		int score = 0;
		int numberOfRooms = Configuration.getInstance().getNumberOfRooms();
		int daySize = HOURS * numberOfRooms;
		int criteriaPosition = 0;

		// Check requirements and evaluate each class (EventSchedule) in the chromosome
		for (Iterator<Pair> iterator = events.iterator(); iterator.hasNext(); criteriaPosition += 6) {
			Pair pair = iterator.next();
			EventSchedule first = pair.getKey();
			int second = pair.getValue();
			
			int p = second;
			int day = p / daySize;
			int time = p % daySize;
			int roomId = time / HOURS;
			time = time % HOURS;
			
			int duration = first.getDuration();
			
			// Check for overlapping classes at the same time
			boolean roomOverlap = false;
			for(int i = duration - 1; i >= 0; i--)
                if (slots.get(p + i).size() > 1) {
                    roomOverlap = true;
                    break;
                }

			if(!roomOverlap)
				score++;

			criteria.set(criteriaPosition+0, !roomOverlap);
			
			EventSchedule cc = first;
			Room room = Configuration.getInstance().getRoomById( roomId );
			
			// Check if the room has a sufficient number of seats
			criteria.set(criteriaPosition+1, room.getNumberOfSeats() >= cc.getNumberOfSeats());
			if(criteria.get(criteriaPosition+1))
				score++;

			// Check if the room has the required equipment
			criteria.set(criteriaPosition+2, !cc.isLabRequired() || ( cc.isLabRequired() && room.isLab()));
			if(criteria.get(criteriaPosition+2))
				score++;
			
			boolean profOverlap = false;
			boolean groupOverlap = false;
			
			// Check if the organizer is teaching another subject at the same time
			// Check if the group must attend another meeting at the same time
			boolean flag = false;
			for(int i = numberOfRooms, t = day * daySize + time; i > 0; i--, t += HOURS) {
				if(flag)
                    break;
				for(int k = duration - 1; k >= 0; k--) {
					if(flag)
                        break;
                    
					List<EventSchedule> cl = slots.get(t+1);
					for (Iterator<EventSchedule> it = cl.iterator(); it.hasNext();) {
						if(flag)
                            break;
                        
						EventSchedule eventSchedule = it.next();
						if(!cc.equals(eventSchedule))
						{
							// Check for overlapping professors
							if(!profOverlap && cc.promoterOverlaps(eventSchedule))
								profOverlap = true;

							// Check for overlapping groups
							if(!groupOverlap && cc.groupsOverlap(eventSchedule))
								groupOverlap = true;

							if( profOverlap && groupOverlap ) {
								flag = true;
								break;
							}
						}
					}
				}
			}
			
			if(!profOverlap)
				score++;
			criteria.set(criteriaPosition+3, !profOverlap);

			if(!groupOverlap)
				score++;
			criteria.set(criteriaPosition+4, !groupOverlap); 
			
			// Compacting meetings
			boolean nearEvent = false;
   
			if((p+duration < DAYS * HOURS * Configuration.getInstance().getNumberOfRooms()) && (slots.get(p+duration).size() > 1)) {
				int schP = p+duration;
				int schDay = schP / daySize;
				int schTime = schP % daySize;
				schTime = schTime % HOURS;
				if(day == schDay)
                    nearEvent = true;
			}
			else if((p != 0) && (slots.get(p-1).size() > 1)) {
				int schP = p-1;
				int schDay = schP / daySize;
				int schTime = schP % daySize;
				schTime = schTime % HOURS;
				if(day == schDay)
                    nearEvent = true;
			}
   
			if( nearEvent )
				score++;
			criteria.set(criteriaPosition+5, nearEvent); 
		}
		
		// Calculate the chromosome evaluation
		fitness = (float) score / ( Configuration.getInstance().getNumberOfEvents() * 6 );
	}

	private void initializeSlots() {
		for (int i = 0; i < slots.size(); i++)
            slots.set(i, new ArrayList<>());
	}
	
	private void initializeCriteria() {
		for (int i = 0; i < criteria.size(); i++)
            criteria.set(i, false);
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
