package com.pj.magic.model.util;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pj.magic.Constants;

public class CellStyleBuilder {
	
	private XSSFWorkbook workbook;
	private Short align;
	private Short borderBottom;
	private Short borderRight;
	private Boolean amountFormat;
	
	public static CellStyleBuilder createStyle(XSSFWorkbook workbook) {
		return new CellStyleBuilder(workbook);
	}
	
	private CellStyleBuilder(XSSFWorkbook workbook) {
		this.workbook = workbook;
	}
	
	public CellStyleBuilder setAlignment(short align) {
		this.align = align;
		return this;
	}
	
	public CellStyle build() {
		XSSFCellStyle style = workbook.createCellStyle();
		XSSFCreationHelper creationHelper = workbook.getCreationHelper();
		if (align != null) {
			style.setAlignment(align);
		}
		if (borderBottom != null) {
			style.setBorderBottom(borderBottom);
		}
		if (borderRight != null) {
			style.setBorderRight(borderRight);
		}
		if (amountFormat != null && amountFormat) {
			style.setDataFormat(creationHelper.createDataFormat().getFormat(Constants.AMOUNT_FORMAT));
		}
		return style;
	}

	public CellStyleBuilder setBorderBottom(short borderBottom) {
		this.borderBottom = borderBottom;
		return this;
	}

	public CellStyleBuilder setBorderRight(short borderRight) {
		this.borderRight = borderRight;
		return this;
	}

	public CellStyleBuilder setAmountFormat(boolean amountFormat) {
		this.amountFormat = amountFormat;
		return this;
	}
	
}