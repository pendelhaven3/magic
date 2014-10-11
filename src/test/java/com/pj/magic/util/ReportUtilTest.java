package com.pj.magic.util;

import static org.junit.Assert.*;
import org.junit.Test;

public class ReportUtilTest {

	@Test
	public void multiline() {
		String text = "BLK. 61, LOT 11, PHASE IV-A. STO. NI�O VILLAGE, MEYCAUAYAN, BULACAN";
		
		assertEquals("BLK. 61, LOT 11, PHASE IV-A. STO. NI�O  ", ReportUtil.multiline(text, 40, 1));
		assertEquals("VILLAGE, MEYCAUAYAN, BULACAN            ", ReportUtil.multiline(text, 40, 2));
	}
	
}
