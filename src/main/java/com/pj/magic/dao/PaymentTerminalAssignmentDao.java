package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PaymentTerminalAssignment;

public interface PaymentTerminalAssignmentDao {
	
	List<PaymentTerminalAssignment> getAll();

	void save(PaymentTerminalAssignment paymentTerminalAssignment);

	void delete(PaymentTerminalAssignment paymentTerminalAssignment);

}
