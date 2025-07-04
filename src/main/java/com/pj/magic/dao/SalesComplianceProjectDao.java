package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.SalesComplianceProject;

public interface SalesComplianceProjectDao {

	List<SalesComplianceProject> getAll();

	SalesComplianceProject get(Long id);

	void save(SalesComplianceProject project);

}
