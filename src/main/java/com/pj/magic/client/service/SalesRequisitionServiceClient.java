package com.pj.magic.client.service;

import com.pj.magic.exception.SalesRequisitionPostException;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.User;

public interface SalesRequisitionServiceClient {

    SalesInvoice post(SalesRequisition salesRequisition, User postedBy) throws SalesRequisitionPostException;
    
}
