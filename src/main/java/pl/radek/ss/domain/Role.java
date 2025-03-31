package pl.radek.ss.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "roles")
public class Role
{
	public Role()
	{
		this.setName("ROLE_ANONYMOUS");
	}

	public Role(String role)
	{
		this.setName(role);
	}

	@Id
	@GeneratedValue(generator = "role_id", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "role_id", sequenceName = "role_id_seq", allocationSize = 1)
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
	@NotNull
	@Size(min = 4, max = 20)
	String name;
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}

	@ManyToOne(targetEntity = Account.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = true)
	Account account;
	public Account getAccount()
	{
		return account;
	}
	public void setAccount(Account account)
	{
		this.account = account;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Role)
            return ((Role) obj).getName().equals(this.getName());
        
		return false;
	}

	@Override
	public String toString() 
	{
		return "Role: " + this.name;
	}
}
