package com.pj.magic.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.pj.magic.dao.CreditCardPaymentDao;
import com.pj.magic.model.CreditCard;
import com.pj.magic.model.CreditCardPayment;

@Repository
public class CreditCardPaymentDaoImpl implements CreditCardPaymentDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public void save(CreditCardPayment payment) {
		if (payment.getId() == null) {
			entityManager.persist(payment);
		} else {
			entityManager.merge(payment);
		}
	}

	@Override
	public CreditCardPayment get(long id) {
		return entityManager.find(CreditCardPayment.class, id);
	}

	@Override
	public List<CreditCardPayment> findAllByCreditCard(CreditCard creditCard) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<CreditCardPayment> criteria = builder.createQuery(CreditCardPayment.class);
		Root<CreditCardPayment> payment = criteria.from(CreditCardPayment.class);
		criteria.where(payment.get("creditCard").in(creditCard));
		criteria.orderBy(builder.desc(payment.get("paymentDate")));
		
		return entityManager.createQuery(criteria).getResultList();
	}

}