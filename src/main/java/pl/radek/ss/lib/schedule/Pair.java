package pl.radek.ss.lib.schedule;
import java.util.Iterator;
import java.util.List;

public class Pair
{
	// --------------------------------------------------------------------------------
    private EventSchedule key;
    private Integer value;
	// --------------------------------------------------------------------------------
    public static void setEventScheduleValueById(List<Pair> list, Integer id, Integer value) {
	    for (Iterator<Pair> iterator = list.iterator(); iterator.hasNext();) {
		    Pair pair = iterator.next();
		    if(pair.getKey().getId() == id) {
			    pair.value = value;
			    break;
		    }
	    }
    }
   
	public Pair(EventSchedule key, Integer value) {
		this.key = key;
		this.value = value;
	}
	// --------------------------------------------------------------------------------
	public EventSchedule getKey()
	{
		return key;
	}
	
	public void setKey(EventSchedule key)
	{
		this.key = key;
	}
	
	public Integer getValue()
	{
		return value;
	}
	
	public void setValue(Integer value)
	{
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "Pair: " + key + " " + value;
	}
}
