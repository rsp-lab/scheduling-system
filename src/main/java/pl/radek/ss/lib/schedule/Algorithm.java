package pl.radek.ss.lib.schedule;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Algorithm
{
	// ------------------------------------------------------------------------------------------------------
	private Vector<Schedule> chromosomes = new Vector<>();
	
	// List of flags indicating membership in the group of best chromosomes
	private Vector<Boolean> bestFlags = new Vector<>();

	// List indicating the position of the best chromosomes
	private Vector<Integer> bestChromosomes = new Vector<>();

	// Number of the best chromosomes
	private int currentBestSize = 0;

	// Number of chromosomes that must be replaced with each reproduction (offspring generation)
	private int replaceByGeneration = 8;

	private Schedule prototype;

	// Generation counter
	private int currentGeneration = 0;

	private static Algorithm instance = null;
	private boolean endFlag = true;
	
	// Algorithm's settings
	private static int max_generations 			= 40000;
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
		if( instance == null ) {
			Schedule prototype = new Schedule( num_crossoverPoints, num_mutationSize, num_crossoverProbability, num_mutationProbability );
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
		
        // Track at least one best chromosome
		if( trackBest < 1 )
            trackBest = 1;

		// At least 2 chromosomes are required in the population
		if( numberOfChromosomes < 2 )
            numberOfChromosomes = 2;
		
		// Replace at least one chromosome
        /*
            if( replaceByGeneration < 1 )
                replaceByGeneration = 1;
            else if( replaceByGeneration > numberOfChromosomes - trackBest )
                replaceByGeneration = numberOfChromosomes - trackBest;
		*/
  
		chromosomes.setSize( numberOfChromosomes );
		bestFlags.setSize( numberOfChromosomes );
		bestChromosomes.setSize( trackBest );
  
		for(int i = chromosomes.size() - 1; i >= 0; --i ) {
			chromosomes.set(i, null);
			bestFlags.set(i, false);
		}
	}
	
	public String start() {
		String xml = null;
		endFlag = true;
		do {
			if( prototype == null )
				return xml;
			
			// Set flags (false) for all chromosomes and reset the best chromosomes counter
			clearBest();
	
			// Initialize the population of randomly created chromosomes based on a prototype
			for (int i = 0; i < chromosomes.size(); ++i) {
				chromosomes.set(i, prototype.makeNewChromosome());
				addToBest(i);
			}
			
			currentGeneration = 0;
	
			while(true) {
				Schedule bestChromosome = getBestChromosome();
	
				// Check if the algorithm meets the requirements
				if( bestChromosome.getFitness() >= 1 ) {
					endFlag = false;
					
					List<Pair> listEvents = bestChromosome.getEvents();
					Vector<List<EventSchedule>> slots = bestChromosome.getSlots();
					
					xml = toXml(slots, listEvents);
					break;
				}
	
				// Reproduction (offspring)
				Vector<Schedule> offspring = new Vector<>();
				offspring.setSize( replaceByGeneration );
				for( int i = 0; i < replaceByGeneration; i++ ) {
					// Select parents randomly
					Schedule p1 = chromosomes.get(random(0, chromosomes.size()-1));
					Schedule p2 = chromosomes.get(random(0, chromosomes.size()-1));
					offspring.set(i, p1.crossover(p2));
					offspring.get(i).mutation();
				}
	
				// Replace current chromosomes with offspring
				for( int i = 0; i < replaceByGeneration; i++ ) {
					int chromosomId = 0;
					do {
						// Randomly select a chromosome for replacement
						chromosomId = random(0, chromosomes.size()-1);
					}
					// Protect the best chromosomes from replacement
					while( isInBest( chromosomId ) );
	
					// Replace chromosomes
					chromosomes.set(chromosomId, offspring.get(i));
					
					// Try to add a new chromosome to the group of best ones
					addToBest( chromosomId );
				}
	
				if(log_generations != 0 && (getCurrentGeneration() % log_generations == 0))
                    System.out.println("fitness: " + chromosomes.get(bestChromosomes.get(0))
                            .getFitness() + " | " + getCurrentGeneration());
                
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
		// Do not add if the quality of the new chromosome doesn't meet the expectations (evaluation function) of the entire best group or is already in the group
		if( (currentBestSize == bestChromosomes.size() &&
			chromosomes.get(bestChromosomes.get(currentBestSize-1)).getFitness() >= chromosomes.get(chromosomeIndex).getFitness()) ||
			bestFlags.get(chromosomeIndex) )
            return;
		
		// Find a place for the new chromosome
		int i = currentBestSize;
		for( ; i > 0; i-- ) {
			// Is the group not full?
			if( i < bestChromosomes.size() ) {
				// Has the position for the new chromosome been found?
				if( chromosomes.get(bestChromosomes.get(i-1)).getFitness() > chromosomes.get(chromosomeIndex).getFitness() )
					break;

				// Shift a chromosome to make room for the new one
				bestChromosomes.set(i, bestChromosomes.get(i-1));
			}
			// The group is full, remove the worst chromosomes in the group
			else
				bestFlags.set(bestChromosomes.get(i-1), false);
		}
		
		// Keep the chromosome in the group of best ones
		bestChromosomes.set(i, chromosomeIndex);
		bestFlags.set(chromosomeIndex, true);

		if( currentBestSize < bestChromosomes.size() )
            currentBestSize++;
	}

	private Boolean isInBest(int chromosomeIndex)
	{
		return bestFlags.get(chromosomeIndex);
	}

	private void clearBest()
	{
		for(int i = bestFlags.size() - 1; i >= 0; --i )
            bestFlags.set(i, false);

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
	
	public static int random(int lo, int hi)
    {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int n = hi - lo + 1;
        int i = random.nextInt() % n;
        if (i < 0)
            i = -i;
        return lo + i;
    }
	
	// Returns a meeting schedule in XML format
	public String toXml(Vector<List<EventSchedule>> slots, List<Pair> listClasses)
	{
		String tab = "   ";
		StringBuilder builder = new StringBuilder();
		builder.append("<schedule>").append("\n");
		List<Group> studenci = new ArrayList<>();
		
		int count = 0;
		for (Iterator<Pair> iterator = listClasses.iterator(); iterator.hasNext();) {
			Pair pair = iterator.next();
			EventSchedule cc = pair.getKey();
			for (Iterator<Group> it = cc.getGroups().iterator(); it.hasNext();)
			{
				Group st = it.next();
				if(!studenci.contains(st))
                    studenci.add(st);
			}
			count++;
		}
		
		builder.append(tab).append("<groups>").append("\n");
        
        for (Iterator<Group> iterator = studenci.iterator(); iterator.hasNext();) {
			count = 0;
			Group studentsGroup = iterator.next();
			builder.append(tab).append(tab).append("<group>").append("\n");
			builder.append(tab).append(tab).append(tab).append("<id>").append(studentsGroup.getId()).append("</id>").append("\n");
			builder.append(tab).append(tab).append(tab).append("<name>").append(studentsGroup.getName()).append("</name>").append("\n");
			
			for (Iterator<List<EventSchedule>> iteratorSlot = slots.iterator(); iteratorSlot.hasNext();) {
				List<EventSchedule> slot = iteratorSlot.next();
    
				for (Iterator<EventSchedule> it = slot.iterator(); it.hasNext();) {
					EventSchedule eventSchedule = it.next();
					if(eventSchedule != null) {
						if(eventSchedule.getGroups().contains(studentsGroup)) {
							builder.append(tab).append(tab).append(tab).append("<event>").append("\n");
							
							int minHour = Schedule.MIN_HOUR;
							int nrRooms = Configuration.getInstance().getNumberOfRooms(); 
							int daySlot = Schedule.HOURS * nrRooms; 
							
							// Monday
							if((0 <= count && count < daySlot))
							{
								int hour = (count % Schedule.HOURS) + minHour;
								builder.append(tab).append(tab).append(tab).append(tab).append("<timeId>").append(hour-minHour).append("</timeId>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<timeDay>").append("0").append("</timeDay>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<time>").append(hour + "-" + (hour+1)).append("</time>").append("\n");
								
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
							
							// Tuesday
							if((daySlot <= count && count < daySlot*2))
							{
								int hour = (count % Schedule.HOURS)+minHour;
								builder.append(tab).append(tab).append(tab).append(tab).append("<timeId>").append(hour-minHour).append("</timeId>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<timeDay>").append("1").append("</timeDay>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<time>").append(hour + "-" + (hour+1)).append("</time>").append("\n");
								
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
							
							// Wednesday
							if((daySlot*2 <= count && count < daySlot*3))
							{
								int hour = (count % Schedule.HOURS)+minHour;
								builder.append(tab).append(tab).append(tab).append(tab).append("<timeId>").append(hour-minHour).append("</timeId>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<timeDay>").append("2").append("</timeDay>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<time>").append(hour + "-" + (hour+1)).append("</time>").append("\n");
								
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
							
							// Thursday
							if((daySlot*3 <= count && count < daySlot*4))
							{
								int hour = (count % Schedule.HOURS)+minHour;
								builder.append(tab).append(tab).append(tab).append(tab).append("<timeId>").append(hour-minHour).append("</timeId>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<timeDay>").append("3").append("</timeDay>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<time>").append(hour + "-" + (hour+1)).append("</time>").append("\n");
        
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
							
							// Friday
							if((daySlot*4 <= count && count < daySlot*5))
							{
								int hour = (count % Schedule.HOURS)+minHour;
								builder.append(tab).append(tab).append(tab).append(tab).append("<timeId>").append(hour-minHour).append("</timeId>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<timeDay>").append("4").append("</timeDay>").append("\n");
								builder.append(tab).append(tab).append(tab).append(tab).append("<time>").append(hour + "-" + (hour+1)).append("</time>").append("\n");
								
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
