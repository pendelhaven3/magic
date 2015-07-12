package com.pj.magic.dao;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.Supplier;

@Ignore
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class SupplierDaoTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	@Autowired private SupplierDao supplierDao;
	@Autowired private PaymentTermDao paymentTermDao;
	
	private PaymentTerm paymentTerm;
	private PaymentTerm paymentTerm2;
	
	@Before
	public void setUp() {
		paymentTerm = new PaymentTerm();
		paymentTerm.setName("COD");
		paymentTerm.setNumberOfDays(0);
		paymentTermDao.save(paymentTerm);
		
		paymentTerm2 = new PaymentTerm();
		paymentTerm2.setName("3 DAYS");
		paymentTerm2.setNumberOfDays(3);
		paymentTermDao.save(paymentTerm2);
	}
	
	@Test
	public void insertWithPaymentTerm() {
		String name = "TEST SUPPLIER";
		String address = "TEST SUPPLIER ADDRESS";
		String contactNumber = "09163441423";
		String contactPerson = "PJ MIRANDA";
		String faxNumber = "3613988";
		String emailAddress = "pendelhaven3@yahoo.com";
		String tin = "TIN";
		
		Supplier supplier = new Supplier();
		supplier.setName(name);
		supplier.setAddress(address);
		supplier.setContactNumber(contactNumber);
		supplier.setContactPerson(contactPerson);
		supplier.setFaxNumber(faxNumber);
		supplier.setEmailAddress(emailAddress);
		supplier.setPaymentTerm(paymentTerm);
		supplier.setTin(tin);
		supplierDao.save(supplier);
		
		Supplier fromDb = supplierDao.get(supplier.getId());
		assertEquals(name, fromDb.getName());
		assertEquals(address, fromDb.getAddress());
		assertEquals(contactNumber, fromDb.getContactNumber());
		assertEquals(contactPerson, fromDb.getContactPerson());
		assertEquals(faxNumber, fromDb.getFaxNumber());
		assertEquals(emailAddress, fromDb.getEmailAddress());
		assertEquals(paymentTerm, fromDb.getPaymentTerm());
		assertEquals(tin, fromDb.getTin());
	}

	@Test
	public void insertWithoutPaymentTerm() {
		String name = "TEST SUPPLIER";
		String address = "TEST SUPPLIER ADDRESS";
		String contactNumber = "09163441423";
		String contactPerson = "PJ MIRANDA";
		String faxNumber = "3613988";
		String emailAddress = "pendelhaven3@yahoo.com";
		String tin = "TIN"; // TODO: Put proper dummy TIN
		
		Supplier supplier = new Supplier();
		supplier.setName(name);
		supplier.setAddress(address);
		supplier.setContactNumber(contactNumber);
		supplier.setContactPerson(contactPerson);
		supplier.setFaxNumber(faxNumber);
		supplier.setEmailAddress(emailAddress);
		supplier.setTin(tin);
		supplierDao.save(supplier);
		
		Supplier fromDb = supplierDao.get(supplier.getId());
		assertEquals(name, fromDb.getName());
		assertEquals(address, fromDb.getAddress());
		assertEquals(contactNumber, fromDb.getContactNumber());
		assertEquals(contactPerson, fromDb.getContactPerson());
		assertEquals(faxNumber, fromDb.getFaxNumber());
		assertEquals(emailAddress, fromDb.getEmailAddress());
		assertNull(fromDb.getPaymentTerm());
		assertEquals(tin, fromDb.getTin());
	}

	@Test
	public void updateWithPaymentTerm() {
		Supplier supplier = createSupplier();
		
		String name = "RAMEN HOUSE";
		String address = "KONOHA";
		String contactNumber = "911-1111";
		String contactPerson = "UZUMAKI NARUTO";
		String faxNumber = "3639181";
		String emailAddress = "uzumaki_naruto@yahoo.com";
		String tin = "TINTIN";
		
		supplier.setName(name);
		supplier.setAddress(address);
		supplier.setContactNumber(contactNumber);
		supplier.setContactPerson(contactPerson);
		supplier.setFaxNumber(faxNumber);
		supplier.setEmailAddress(emailAddress);
		supplier.setPaymentTerm(paymentTerm2);
		supplier.setTin(tin);
		supplierDao.save(supplier);
		
		Supplier fromDb = supplierDao.get(supplier.getId());
		assertEquals(name, fromDb.getName());
		assertEquals(address, fromDb.getAddress());
		assertEquals(contactNumber, fromDb.getContactNumber());
		assertEquals(contactPerson, fromDb.getContactPerson());
		assertEquals(faxNumber, fromDb.getFaxNumber());
		assertEquals(emailAddress, fromDb.getEmailAddress());
		assertEquals(paymentTerm2, fromDb.getPaymentTerm());
		assertEquals(tin, fromDb.getTin());
	}

	@Test
	public void updateWithoutPaymentTerm() {
		Supplier supplier = createSupplier();
		
		String name = "RAMEN HOUSE";
		String address = "KONOHA";
		String contactNumber = "911-1111";
		String contactPerson = "UZUMAKI NARUTO";
		String faxNumber = "3639181";
		String emailAddress = "uzumaki_naruto@yahoo.com";
		String tin = "TINTIN";
		
		supplier.setName(name);
		supplier.setAddress(address);
		supplier.setContactNumber(contactNumber);
		supplier.setContactPerson(contactPerson);
		supplier.setFaxNumber(faxNumber);
		supplier.setEmailAddress(emailAddress);
		supplier.setPaymentTerm(null);
		supplier.setTin(tin);
		supplierDao.save(supplier);
		
		Supplier fromDb = supplierDao.get(supplier.getId());
		assertEquals(name, fromDb.getName());
		assertEquals(address, fromDb.getAddress());
		assertEquals(contactNumber, fromDb.getContactNumber());
		assertEquals(contactPerson, fromDb.getContactPerson());
		assertEquals(faxNumber, fromDb.getFaxNumber());
		assertEquals(emailAddress, fromDb.getEmailAddress());
		assertNull(fromDb.getPaymentTerm());
		assertEquals(tin, fromDb.getTin());
	}

	private Supplier createSupplier() {
		Supplier supplier = new Supplier();
		supplier.setName("TEST SUPPLIER NAME");
		supplier.setAddress("TEST ADDRESS");
		supplier.setContactNumber("09163441423");
		supplier.setContactPerson("PJ MIRANDA");
		supplier.setFaxNumber("FAX NUMBER");
		supplier.setEmailAddress("pendelhaven3@yahoo.com");
		supplier.setPaymentTerm(paymentTerm);
		supplier.setTin("TIN");
		supplierDao.save(supplier);
		
		return supplier;
	}
	
	@Test
	public void getWithInvalidId() {
		assertNull(supplierDao.get(999L));
	}
	
	@Test
	public void getAll() {
		fail();
	}
	
	@Test
	public void findAllByProduct() {
		fail();
	}

	@Test
	public void deleteSupplierProduct() {
		fail();
	}
	
}
