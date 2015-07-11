package com.pj.magic.util;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.pj.magic.util.report.ReportUtil;

@Ignore
public class ReportUtilTest {

	@Test
	public void multiline() {
		String text = "BLK. 61, LOT 11, PHASE IV-A. STO. NI�O VILLAGE, MEYCAUAYAN, BULACAN";
		
		assertEquals("BLK. 61, LOT 11, PHASE IV-A. STO. NI�O  ", ReportUtil.multiline(text, 40, 1));
		assertEquals("VILLAGE, MEYCAUAYAN, BULACAN            ", ReportUtil.multiline(text, 40, 2));
	}

	@Test
	public void center() {
		String text = "JC HARMONY SELLING INC.";
		assertEquals("                         JC HARMONY SELLING INC.", ReportUtil.center(text));
		
		text = "251 GEN.P.ALVAREZ ST.CALOOCAN CITY";
		assertEquals("                   251 GEN.P.ALVAREZ ST.CALOOCAN CITY", ReportUtil.center(text));
		
		text = "TEL.NO.3621785 3235946";
		assertEquals("                         TEL.NO.3621785 3235946", ReportUtil.center(text));
		
		text = "PURCHASE ORDER";
		assertEquals("                             PURCHASE ORDER", ReportUtil.center(text));
	}
	
}
