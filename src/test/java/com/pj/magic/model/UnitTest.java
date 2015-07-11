package com.pj.magic.model;

import static org.junit.Assert.*;
import org.junit.Test;

public class UnitTest {

	@Test
	public void compare_otherUnitLess() {
		assertEquals(1, Unit.compare(Unit.CASE, Unit.CARTON));
		assertEquals(1, Unit.compare(Unit.CASE, Unit.DOZEN));
		assertEquals(1, Unit.compare(Unit.CASE, Unit.PIECES));
		assertEquals(1, Unit.compare(Unit.CARTON, Unit.PIECES));
	}
	
	@Test
	public void compare_otherUnitGreater() {
		assertEquals(-1, Unit.compare(Unit.PIECES, Unit.CARTON));
		assertEquals(-1, Unit.compare(Unit.PIECES, Unit.CASE));
		assertEquals(-1, Unit.compare(Unit.DOZEN, Unit.CASE));
		assertEquals(-1, Unit.compare(Unit.CARTON, Unit.CASE));
	}
	
	@Test
	public void compare_otherUnitEqual() {
		assertEquals(0, Unit.compare(Unit.PIECES, Unit.PIECES));
		assertEquals(0, Unit.compare(Unit.DOZEN, Unit.DOZEN));
		assertEquals(0, Unit.compare(Unit.CARTON, Unit.CARTON));
		assertEquals(0, Unit.compare(Unit.CASE, Unit.CASE));
	}
	
}