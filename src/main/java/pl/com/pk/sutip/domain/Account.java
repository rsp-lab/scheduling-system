package pl.com.pk.sutip.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@Entity
@XStreamAlias("account")
@SuppressWarnings("serial")
public class Account extends User implements UserDetails 
{
	public Account()
	{
		super("NONE", "NONE", false, false, false, false, new ArrayList<GrantedAuthority>());
	}
	
	public Account(String username, String password, Date registerDate, Integer createdBy, List<Role> roles)
	{
		this();
		this.username = username;
		this.password = password;
		this.registerDate = registerDate;
		this.roles = roles;
	}

	@Id
	@GeneratedValue(generator = "account_id", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "account_id", sequenceName = "account_id_seq", initialValue = 1)
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
//	@NotNull
    @Size(min = 4, max = 20)
	String username;
	public String getUsername()
	{
		return username;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}

	@Basic
	@NotNull
    @Size(min = 4, max = 64)
	String password;
	public String getPassword()
	{
		return password;
	}
	public void setPassword(String password)
	{
		this.password = password;
	}

	@Transient
	public String repeatedPassword;
	public String getRepeatedPassword()
	{
		return repeatedPassword;
	}
	public void setRepeatedPassword(String repeatedPassword)
	{
		this.repeatedPassword = repeatedPassword;
	}
	
	@Transient
	@Size(min = 4, max = 64)
	public String newPassword;
	public String getNewPassword()
	{
		return newPassword;
	}
	public void setNewPassword(String newPassword)
	{
		this.newPassword = newPassword;
	}

	@Basic
	@Column(name="register_date")
	@DateTimeFormat(iso = ISO.DATE_TIME)
	Date registerDate = new Date();
	public Date getRegisterDate()
	{
		return registerDate;
	}
	public void setRegisterDate(Date registerDate)
	{
		this.registerDate = registerDate;
	}

	@Basic
	@Column(name="last_login")
	@DateTimeFormat(iso=ISO.DATE_TIME)
	Date lastLogin;
	public Date getLastLogin()
	{
		return lastLogin;
	}
	public void setLastLogin(Date lastLogin)
	{
		this.lastLogin = lastLogin;
	}
	
	@Basic
	@Column(name="created_counter")
	Integer createdCounter = 0;
	public Integer getCreatedCounter()
	{
		return createdCounter;
	}
	public void setCreatedCounter(Integer createdCounter)
	{
		this.createdCounter = createdCounter;
	}
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "account", targetEntity = Role.class, fetch = FetchType.EAGER)
	List<Role> roles = new ArrayList<Role>();
	public List<Role> getRoles()
	{
		return roles;
	}
	public void setRoles(List<Role> roles) 
	{
		for (Role ar : roles)
		{
			this.addRole(ar);
		}
	}
	public void addRole(Role role)
	{
		if (!this.roles.contains(role))
		{
			role.setAccount(this);
			this.roles.add(role);
		}
	}
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "account", targetEntity = Event.class, fetch = FetchType.LAZY)
	List<Event> events = new ArrayList<Event>();
	public List<Event> getEvents()
	{
		return events;
	}
	public void setEvents(List<Event> events) 
	{
		this.events = events;
	}
	public void addEvents(List<Event> events)
	{
		for (Event e : events)
		{
			this.events.add(e);
		}
	}
	
	@Override
	public Collection<GrantedAuthority> getAuthorities()
	{
		Set<GrantedAuthority> ga = new HashSet<GrantedAuthority>();
		for (Role ar : this.roles)
		{
			ga.add(new GrantedAuthorityImpl(ar.getName()));
		}
		return ga;
	}

	@Override
	public boolean isAccountNonExpired() 
	{
		return this.isEnabled();
	}

	@Override
	public boolean isAccountNonLocked()
	{
		return this.isEnabled();
	}

	@Override
	public boolean isCredentialsNonExpired()
	{
		return this.isEnabled();
	}

	@Override
	public boolean isEnabled() 
	{
		if (this.roles.size() > 0)
		{
			return true;
		}
		return false;
	}
}
