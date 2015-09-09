package com.pj.magic.dao.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class CreditCardPaymentDaoImpl extends MagicDao implements CreditCardPaymentDao {

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

	private static final String GET_SURPLUS_PAYMENT_SQL =
			" select sum(AMOUNT)"
			+ " from ("
			+ "   select sum(AMOUNT) as AMOUNT"
			+ "   from CREDIT_CARD_PAYMENT a"
			+ "   where a.CREDIT_CARD_ID = :creditCardId"
			+ "   union"
			+ "   select ifnull(sum(AMOUNT), 0) * -1 as AMOUNT"
			+ "   from CREDIT_CARD_STATEMENT a"
			+ "   join CREDIT_CARD_STATEMENT_ITEM b"
			+ "     on b.CREDIT_CARD_STATEMENT_ID = a.ID"
			+ "   join PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT c"
			+ "     on c.ID = b.PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT_ID"
			+ "   where a.CREDIT_CARD_ID = :creditCardId"
			+ "   and a.POST_IND = 'Y'"
			+ "   and b.PAID_IND = 'Y'"
			+ " ) main";
	
	@Override
	public BigDecimal getSurplusPayment(CreditCard creditCard) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("creditCardId", creditCard.getId());
		
		return getNamedParameterJdbcTemplate()
				.queryForObject(GET_SURPLUS_PAYMENT_SQL, paramMap, BigDecimal.class);
	}

	private static final String DELETE_SQL = "delete from CREDIT_CARD_PAYMENT where ID = ?";
	
	@Override
	public void delete(CreditCardPayment payment) {
		getJdbcTemplate().update(DELETE_SQL, payment.getId());
	}

}