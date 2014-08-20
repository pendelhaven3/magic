package com.pj.magic.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PaymentTermDao;
import com.pj.magic.model.PaymentTerm;

@Repository
public class PaymentTermDaoImpl implements PaymentTermDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public void save(PaymentTerm paymentTerm) {
		entityManager.persist(paymentTerm);
	}

	@Override
	public PaymentTerm get(long id) {
		return entityManager.find(PaymentTerm.class, id);
	}

	@Override
	public List<PaymentTerm> getAll() {
        return entityManager.createQuery("SELECT p FROM PaymentTerm p", PaymentTerm.class).getResultList();
	}

}
