package com.pj.magic.model.util;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Workbook;

import com.pj.magic.Constants;

public class CellStyleBuilder {
	
	private Workbook workbook;
	private Short align;
	private Short borderBottom;
	private Short borderRight;
	private Boolean amountFormat;
	
	public static CellStyleBuilder createStyle(Workbook workbook) {
		return new CellStyleBuilder(workbook);
	}
	
	private CellStyleBuilder(Workbook workbook) {
		this.workbook = workbook;
	}
	
	public CellStyleBuilder setAlignment(short align) {
		this.align = align;
		return this;
	}
	
	public CellStyle build() {
		CellStyle style = workbook.createCellStyle();
		CreationHelper creationHelper = workbook.getCreationHelper();
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