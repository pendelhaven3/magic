package com.pj.magic.model.search;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerSearchCriteria {

	private String nameLike;
	private String addressLike;
	private Boolean active;

}