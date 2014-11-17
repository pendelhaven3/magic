package com.pj.magic.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PaymentTerminalDao;
import com.pj.magic.model.PaymentTerminal;

@Repository
public class PaymentTerminalDaoImpl implements PaymentTerminalDao {

	@PersistenceContext
	private EntityManager entityManager;
	
//	@Override
//	public Area get(long id) {
//		return entityManager.find(Area.class, id);
//	}

	@Override
	public List<PaymentTerminal> getAll() {
        return entityManager.createQuery("SELECT pt FROM PaymentTerminal pt ORDER BY pt.name", 
        		PaymentTerminal.class).getResultList();
	}

//	@Override
//	public Area findByName(String name) {
//        TypedQuery<Area> query = entityManager.createQuery("SELECT a FROM Area a WHERE a.name = :name", Area.class);
//        query.setParameter("name", name);
//		try {
//			return query.getSingleResult();
//		} catch (NoResultException e) {
//			return null;
//		}
//	}

}