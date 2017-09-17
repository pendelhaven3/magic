package com.pj.magic.client.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.stereotype.Service;

import com.pj.magic.client.service.SalesRequisitionServiceClient;
import com.pj.magic.exception.SalesRequisitionPostException;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.User;
import com.pj.magic.service.SalesRequisitionService;

@Service
public class SalesRequisitionServiceClientImpl implements SalesRequisitionServiceClient {
    
    private SalesRequisitionService service;
    
    @Autowired
    @Qualifier("salesRequisitionServiceRmiClient")
    public void setSalesRequisitionServiceRmiClient(RmiProxyFactoryBean salesRequisitionServiceRmiClient) {
        service = (SalesRequisitionService)salesRequisitionServiceRmiClient.getObject();
    }
    
    @Override
    public SalesInvoice post(SalesRequisition salesRequisition, User postedBy) throws SalesRequisitionPostException {
        return service.post(salesRequisition, postedBy);
    }

}
