package com.pj.magic.util;

import static org.junit.Assert.*;
import org.junit.Test;

public class ReportUtilTest {

	@Test
	public void multiline() {
		String text = "BLK. 61, LOT 11, PHASE IV-A. STO. NIÑO VILLAGE, MEYCAUAYAN, BULACAN";
		
		assertEquals("BLK. 61, LOT 11, PHASE IV-A. STO. NIÑO  ", ReportUtil.multiline(text, 40, 1));
		assertEquals("VILLAGE, MEYCAUAYAN, BULACAN            ", ReportUtil.multiline(text, 40, 2));
	}
	
}
