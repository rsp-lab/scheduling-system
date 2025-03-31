package pl.radek.ss.domain;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "intervals")
public class Interval
{
	public Interval() { }
	
	public Interval(String startDate, String startTimestamp, String endDate, String endTimestamp) {
		this.startDate = startDate;
		this.startTimestamp = startTimestamp;
		this.endDate = endDate;
		this.endTimestamp = endTimestamp;
	}
	
	public Interval(String startDate, String startTimestamp, String endDate, String endTimestamp, Participation participation) {
		this.startDate = startDate;
		this.startTimestamp = startTimestamp;
		this.endDate = endDate;
		this.endTimestamp = endTimestamp;
		this.participation = participation;
	}

	@Id
	@GeneratedValue(generator = "interval_id", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "interval_id", sequenceName = "interval_id_seq", allocationSize = 1)
	Integer id;
	public Integer getId()
	{
		return id;
	}
	public void setId(Integer id)
	{
		this.id = id;
	}
	
	@Basic
	@Pattern(regexp="^(\\d{4})\\D?(0[1-9]|1[0-2])\\D?([12]\\d|0[1-9]|3[01])$")
	@Size(max = 10)
	String startDate;
	public String getStartDate()
	{
		return startDate;
	}
	public void setStartDate(String startDate)
	{
		this.startDate = startDate;
	}
	
	@Basic
	@Pattern(regexp="([01]?[0-9]|2[0-3]):[0-5][0-9]")
	@Size(max = 5)
	String startTimestamp;
	public String getStartTimestamp()
	{
		return startTimestamp;
	}
	public void setStartTimestamp(String startTimestamp)
	{
		this.startTimestamp = startTimestamp;
	}
	
	@Basic
	@Pattern(regexp="^(\\d{4})\\D?(0[1-9]|1[0-2])\\D?([12]\\d|0[1-9]|3[01])$")
	@Size(max = 10)
	String endDate;
	public String getEndDate()
	{
		return endDate;
	}
	public void setEndDate(String endDate)
	{
		this.endDate = endDate;
	}
	
	@Basic
	@Pattern(regexp="([01]?[0-9]|2[0-3]):[0-5][0-9]")
	@Size(max = 5)
	String endTimestamp;
	public String getEndTimestamp()
	{
		return endTimestamp;
	}
	public void setEndTimestamp(String endTimestamp)
	{
		this.endTimestamp = endTimestamp;
	}
	
	@ManyToOne(targetEntity = Participation.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "participation_id", nullable = true)
	Participation participation;
	public Participation getParticipation()
	{
		return participation;
	}
	public void setParticipation(Participation participation)
	{
		this.participation = participation;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof Interval) {
			if(((Interval) obj).getStartDate().equals(this.getStartDate())
				&& ((Interval) obj).getStartTimestamp().equals(this.getStartTimestamp())
				&& ((Interval) obj).getEndDate().equals(this.getEndDate())
				&& ((Interval) obj).getEndTimestamp().equals(this.getEndTimestamp()))
                return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Interval: " + startDate + " " + startTimestamp + " | " + endDate + " " + endTimestamp;
	}
}
