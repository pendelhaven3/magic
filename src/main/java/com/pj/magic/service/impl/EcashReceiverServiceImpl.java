package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.EcashReceiverDao;
import com.pj.magic.dao.PaymentEcashPaymentDao;
import com.pj.magic.model.EcashReceiver;
import com.pj.magic.service.EcashReceiverService;

@Service
public class EcashReceiverServiceImpl implements EcashReceiverService {

	@Autowired private EcashReceiverDao ecashReceiverDao;
	@Autowired private PaymentEcashPaymentDao paymentEcashPaymentDao;
	
	@Override
	public void save(EcashReceiver ecashReceiver) {
		ecashReceiverDao.save(ecashReceiver);
	}

	@Override
	public List<EcashReceiver> getAllEcashReceivers() {
		return ecashReceiverDao.getAll();
	}

	@Override
	public EcashReceiver getEcashReceiver(long id) {
		return ecashReceiverDao.get(id);
	}

	@Override
	public boolean isBeingUsed(EcashReceiver ecashReceiver) {
		return paymentEcashPaymentDao.findOneByEcashReceiver(ecashReceiver) != null;
	}

	@Override
	public void delete(EcashReceiver ecashReceiver) {
		ecashReceiverDao.delete(ecashReceiver);
	}

	@Override
	public EcashReceiver getEcashReceiverByName(String name) {
		return ecashReceiverDao.findByName(name);
	}

}
