package com.pj.magic.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.pj.magic.model.search.InventoryCheckSearchCriteria;

public class InventoryCheckTest {
	
	@Test
	public void getTotalBeginningValue() {
		InventoryCheck inventoryCheck = new InventoryCheck();
		inventoryCheck.setSummaryItems(Arrays.asList(
				createSummaryItemWithBeginningValue("10"),
				createSummaryItemWithBeginningValue("20")));
		
		assertEquals(new BigDecimal("30.00"), inventoryCheck.getTotalBeginningValue());
	}

	private InventoryCheckSummaryItem createSummaryItemWithBeginningValue(String value) {
		InventoryCheckSummaryItem item = mock(InventoryCheckSummaryItem.class);
		when(item.getBeginningValue()).thenReturn(new BigDecimal(value));
		return item;
	}
	
	@Test
	public void getTotalActualValue() {
		InventoryCheck inventoryCheck = new InventoryCheck();
		inventoryCheck.setSummaryItems(Arrays.asList(
				createSummaryItemWithActualValue("10"),
				createSummaryItemWithActualValue("20")));
		
		assertEquals(new BigDecimal("30.00"), inventoryCheck.getTotalActualValue());
	}

	private InventoryCheckSummaryItem createSummaryItemWithActualValue(String value) {
		InventoryCheckSummaryItem item = mock(InventoryCheckSummaryItem.class);
		when(item.getActualValue()).thenReturn(new BigDecimal(value));
		return item;
	}
	
	@Test
	public void getSummaryItemsWithActualCountOnly() {
		InventoryCheckSummaryItem itemWithActualCount = createSummaryItemWithActualCount(1);
		
		InventoryCheck inventoryCheck = new InventoryCheck();
		inventoryCheck.setSummaryItems(Arrays.asList(
				itemWithActualCount,
				createSummaryItemWithActualCount(0)));
		
		List<InventoryCheckSummaryItem> result = inventoryCheck.getSummaryItemsWithActualCountOnly();
		assertEquals(1, result.size());
		assertSame(itemWithActualCount, result.get(0));
	}
	
	private InventoryCheckSummaryItem createSummaryItemWithActualCount(int count) {
		InventoryCheckSummaryItem item = mock(InventoryCheckSummaryItem.class);
		when(item.getQuantity()).thenReturn(count);
		return item;
	}
	
	@Test
	public void getSummaryItemsWithBeginningInventoriesOnly() {
		InventoryCheckSummaryItem itemWithBeginningInventory = 
				createSummaryItemWithBeginningInventory(1);
		
		InventoryCheck inventoryCheck = new InventoryCheck();
		inventoryCheck.setSummaryItems(Arrays.asList(
				itemWithBeginningInventory,
				createSummaryItemWithBeginningInventory(0)));
		
		List<InventoryCheckSummaryItem> result = inventoryCheck.getSummaryItemsWithBeginningInventoriesOnly();
		assertEquals(1, result.size());
		assertSame(itemWithBeginningInventory, result.get(0));
	}
	
	private InventoryCheckSummaryItem createSummaryItemWithBeginningInventory(int beginningInventory) {
		InventoryCheckSummaryItem item = mock(InventoryCheckSummaryItem.class);
		when(item.getBeginningInventory()).thenReturn(beginningInventory);
		return item;
	}
	
	@Test
	public void getSummaryItemsWithDiscrepancies() {
		InventoryCheckSummaryItem itemWithDiscrepancy = 
				createSummaryItemWithDiscrepancy();
		
		InventoryCheck inventoryCheck = new InventoryCheck();
		inventoryCheck.setSummaryItems(Arrays.asList(
				itemWithDiscrepancy,
				createSummaryItemWithNoDiscrepancy()));
		
		List<InventoryCheckSummaryItem> result = inventoryCheck.getSummaryItemsWithDiscrepancies();
		assertEquals(1, result.size());
		assertSame(itemWithDiscrepancy, result.get(0));
	}
	
	private InventoryCheckSummaryItem createSummaryItemWithDiscrepancy() {
		InventoryCheckSummaryItem item = mock(InventoryCheckSummaryItem.class);
		when(item.getBeginningInventory()).thenReturn(1);
		when(item.getQuantity()).thenReturn(2);
		return item;
	}

	private InventoryCheckSummaryItem createSummaryItemWithNoDiscrepancy() {
		InventoryCheckSummaryItem item = mock(InventoryCheckSummaryItem.class);
		when(item.getBeginningInventory()).thenReturn(1);
		when(item.getQuantity()).thenReturn(1);
		return item;
	}
	
	@Test
	public void searchSummaryItems_emptyCriteria() {
		InventoryCheck inventoryCheck = new InventoryCheck();
		inventoryCheck.setSummaryItems(Arrays.asList(
				new InventoryCheckSummaryItem(),
				new InventoryCheckSummaryItem()));
		
		assertSame(inventoryCheck.getSummaryItems(), 
				inventoryCheck.searchSummaryItems(new InventoryCheckSearchCriteria()));
	}

	@Test
	public void searchSummaryItems_withCodeOrDescriptionCriteria_matchingCode() {
		InventoryCheckSummaryItem item1 = createSummaryItemWithProductCode("ABCDEF");
		InventoryCheckSummaryItem item2 = createSummaryItemWithProductCode("QWERTY");
		
		InventoryCheck inventoryCheck = new InventoryCheck();
		inventoryCheck.setSummaryItems(Arrays.asList(item1, item2));
		
		InventoryCheckSearchCriteria criteria = new InventoryCheckSearchCriteria();
		criteria.setCodeOrDescriptionLike("QWE");
		
		List<InventoryCheckSummaryItem> result = inventoryCheck.searchSummaryItems(criteria);
		assertEquals(1, result.size());
		assertSame(item2, result.get(0));
	}

	private InventoryCheckSummaryItem createSummaryItemWithProductCode(String code) {
		InventoryCheckSummaryItem item = new InventoryCheckSummaryItem();
		item.setProduct(new Product());
		item.getProduct().setCode(code);
		item.getProduct().setDescription(StringUtils.EMPTY);
		return item;
	}
	
	@Test
	public void searchSummaryItems_withCodeOrDescriptionCriteria_matchingDescription() {
		InventoryCheckSummaryItem item1 = createSummaryItemWithProductDescription("abcdef");
		InventoryCheckSummaryItem item2 = createSummaryItemWithProductDescription("qwerty");
		
		InventoryCheck inventoryCheck = new InventoryCheck();
		inventoryCheck.setSummaryItems(Arrays.asList(item1, item2));
		
		InventoryCheckSearchCriteria criteria = new InventoryCheckSearchCriteria();
		criteria.setCodeOrDescriptionLike("CDE");
		
		List<InventoryCheckSummaryItem> result = inventoryCheck.searchSummaryItems(criteria);
		assertEquals(1, result.size());
		assertSame(item1, result.get(0));
	}

	private InventoryCheckSummaryItem createSummaryItemWithProductDescription(String description) {
		InventoryCheckSummaryItem item = new InventoryCheckSummaryItem();
		item.setProduct(new Product());
		item.getProduct().setCode(StringUtils.EMPTY);
		item.getProduct().setDescription(description);
		return item;
	}
	
	@Test
	public void searchSummaryItems_withDiscrepancyCriteria() {
		InventoryCheckSummaryItem item1 = createSummaryItemWithDiscrepancy();
		InventoryCheckSummaryItem item2 = createSummaryItemWithNoDiscrepancy();
		
		InventoryCheck inventoryCheck = new InventoryCheck();
		inventoryCheck.setSummaryItems(Arrays.asList(item1, item2));
		
		InventoryCheckSearchCriteria criteria = new InventoryCheckSearchCriteria();
		criteria.setWithDiscrepancy(true);
		
		List<InventoryCheckSummaryItem> result = inventoryCheck.searchSummaryItems(criteria);
		assertEquals(1, result.size());
		assertSame(item1, result.get(0));
	}
	
	@Test
	public void searchSummaryItems_withNoDiscrepancyCriteria() {
		InventoryCheckSummaryItem item1 = createSummaryItemWithDiscrepancy();
		InventoryCheckSummaryItem item2 = createSummaryItemWithNoDiscrepancy();
		
		InventoryCheck inventoryCheck = new InventoryCheck();
		inventoryCheck.setSummaryItems(Arrays.asList(item1, item2));
		
		InventoryCheckSearchCriteria criteria = new InventoryCheckSearchCriteria();
		criteria.setWithDiscrepancy(false);
		
		List<InventoryCheckSummaryItem> result = inventoryCheck.searchSummaryItems(criteria);
		assertEquals(1, result.size());
		assertSame(item2, result.get(0));
	}
	
	@Test
	public void searchSummaryItems_withCodeOrDescriptionAndDiscrepancyCriteria() {
		Product product1 = new Product();
		product1.setCode("ABCDEF");
		product1.setDescription(StringUtils.EMPTY);
		
		Product product2 = new Product();
		product2.setCode("ABCDEFGHI");
		product2.setDescription(StringUtils.EMPTY);
		
		InventoryCheckSummaryItem item1 = createSummaryItemWithDiscrepancy();
		when(item1.getProduct()).thenReturn(product1);
		
		InventoryCheckSummaryItem item2 = createSummaryItemWithNoDiscrepancy();
		when(item2.getProduct()).thenReturn(product2);
		
		InventoryCheck inventoryCheck = new InventoryCheck();
		inventoryCheck.setSummaryItems(Arrays.asList(item1, item2));
		
		InventoryCheckSearchCriteria criteria = new InventoryCheckSearchCriteria();
		criteria.setCodeOrDescriptionLike("ABCDE");
		criteria.setWithDiscrepancy(false);
		
		List<InventoryCheckSummaryItem> result = inventoryCheck.searchSummaryItems(criteria);
		assertEquals(1, result.size());
		assertSame(item2, result.get(0));
	}
	
	@Test
	public void getNonEmptySummaryItems() {
		InventoryCheckSummaryItem itemWithBeginningInventory = 
				createSummaryItemWithBeginningInventory(1);
		InventoryCheckSummaryItem itemWithActualCount = 
				createSummaryItemWithActualCount(1);
		
		InventoryCheck inventoryCheck = new InventoryCheck();
		inventoryCheck.setSummaryItems(Arrays.asList(
				itemWithBeginningInventory,
				itemWithActualCount,
				createEmptySummaryItem()));
		
		List<InventoryCheckSummaryItem> result = inventoryCheck.getNonEmptySummaryItems();
		assertEquals(2, result.size());
		assertTrue(result.contains(itemWithBeginningInventory));
		assertTrue(result.contains(itemWithActualCount));
	}

	private InventoryCheckSummaryItem createEmptySummaryItem() {
		InventoryCheckSummaryItem item = mock(InventoryCheckSummaryItem.class);
		when(item.getBeginningInventory()).thenReturn(0);
		when(item.getQuantity()).thenReturn(0);
		return item;
	}
	
}