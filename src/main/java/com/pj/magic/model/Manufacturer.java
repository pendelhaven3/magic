package com.pj.magic.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Manufacturer {

	private Long id;
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof Manufacturer)) {
            return false;
        }
        Manufacturer other = (Manufacturer)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
