package com.pj.magic.util;

import static org.junit.Assert.*;
import java.math.BigDecimal;

import org.junit.Test;

public class NumberUtilTest {

	@Test
	public void roundToNearestFiveCents() {
		assertEquals(new BigDecimal("10.00"), NumberUtil.roundUpToNearestFiveCents(new BigDecimal("10.00")));
		assertEquals(new BigDecimal("10.05"), NumberUtil.roundUpToNearestFiveCents(new BigDecimal("10.01")));
		assertEquals(new BigDecimal("10.05"), NumberUtil.roundUpToNearestFiveCents(new BigDecimal("10.02")));
		assertEquals(new BigDecimal("10.05"), NumberUtil.roundUpToNearestFiveCents(new BigDecimal("10.03")));
		assertEquals(new BigDecimal("10.05"), NumberUtil.roundUpToNearestFiveCents(new BigDecimal("10.04")));
		assertEquals(new BigDecimal("10.05"), NumberUtil.roundUpToNearestFiveCents(new BigDecimal("10.05")));
		assertEquals(new BigDecimal("10.10"), NumberUtil.roundUpToNearestFiveCents(new BigDecimal("10.06")));
		assertEquals(new BigDecimal("10.10"), NumberUtil.roundUpToNearestFiveCents(new BigDecimal("10.07")));
		assertEquals(new BigDecimal("10.10"), NumberUtil.roundUpToNearestFiveCents(new BigDecimal("10.08")));
		assertEquals(new BigDecimal("10.10"), NumberUtil.roundUpToNearestFiveCents(new BigDecimal("10.09")));
	}
	
}
