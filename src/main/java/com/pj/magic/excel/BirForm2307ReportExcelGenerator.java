package com.pj.magic.excel;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.hssf.record.TextObjectRecord;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFSimpleShape;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.test.util.ReflectionTestUtils;

import com.pj.magic.model.report.EwtReport;
import com.pj.magic.model.report.EwtReportItem;
import com.pj.magic.model.util.CellStyleBuilder;

public class BirForm2307ReportExcelGenerator {

    private final int SHAPE_INDEX_PERIOD_FROM_MONTH = 93;
    private final int SHAPE_INDEX_PERIOD_FROM_DATE = 270;
    private final int SHAPE_INDEX_PERIOD_FROM_YEAR = 14;
    private final int SHAPE_INDEX_PERIOD_TO_YEAR = 188;
    private final int SHAPE_INDEX_PERIOD_TO_DATE = 268;
    private final int SHAPE_INDEX_PERIOD_TO_MONTH = 193;

    private final int SHAPE_INDEX_PAYEE_TIN_1 = 22;
    private final int SHAPE_INDEX_PAYEE_TIN_2 = 20;
    private final int SHAPE_INDEX_PAYEE_TIN_3 = 19;
    private final int SHAPE_INDEX_PAYEE_TIN_4 = 17;
    private final int SHAPE_INDEX_PAYEE_NAME = 267;
    private final int SHAPE_INDEX_PAYEE_ADDRESS = 208;
    private final int SHAPE_INDEX_PAYEE_ZIP_CODE = 209;
    
    private final int SHAPE_INDEX_PAYOR_TIN_1 = 228;
    private final int SHAPE_INDEX_PAYOR_TIN_2 = 226;
    private final int SHAPE_INDEX_PAYOR_TIN_3 = 225;
    private final int SHAPE_INDEX_PAYOR_TIN_4 = 223;
    private final int SHAPE_INDEX_PAYOR_NAME = 258;
    private final int SHAPE_INDEX_PAYOR_ADDRESS = 259;
    private final int SHAPE_INDEX_PAYOR_ZIP_CODE = 260;
    
    private final Pattern TIN_PATTERN = Pattern.compile("^(\\d{3}-\\d{3}-\\d{3}-\\d{3})|\\d{12}$");
    
    private List<HSSFShape> shapes;
    
    public Workbook generate(EwtReport report) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook(getClass().getResourceAsStream("/excel/form2307.xls"));
        
        HSSFSheet sheet = workbook.getSheetAt(0);
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        shapes = patriarch.getChildren();
        
        Calendar fromDate = DateUtils.toCalendar(report.getFromDate());
        Calendar toDate = DateUtils.toCalendar(report.getToDate());
        
        setText(SHAPE_INDEX_PERIOD_FROM_MONTH, StringUtils.leftPad(String.valueOf(fromDate.get(Calendar.MONTH) + 1), 2, '0'));
        setText(SHAPE_INDEX_PERIOD_FROM_DATE, StringUtils.leftPad(String.valueOf(fromDate.get(Calendar.DATE)), 2, '0'));
        setText(SHAPE_INDEX_PERIOD_FROM_YEAR, String.valueOf(fromDate.get(Calendar.YEAR)).substring(2));
        
        setText(SHAPE_INDEX_PERIOD_TO_MONTH, StringUtils.leftPad(String.valueOf(toDate.get(Calendar.MONTH) + 1), 2, '0'));
        setText(SHAPE_INDEX_PERIOD_TO_DATE, StringUtils.leftPad(String.valueOf(toDate.get(Calendar.DATE)), 2, '0'));
        setText(SHAPE_INDEX_PERIOD_TO_YEAR, String.valueOf(toDate.get(Calendar.YEAR)).substring(2));
        
        if (isValidTin(report.getSupplier().getTin())) {
            String[] tinParts = getTinParts(report.getSupplier().getTin());
            
            setText(SHAPE_INDEX_PAYOR_TIN_1, tinParts[0]);
            setText(SHAPE_INDEX_PAYOR_TIN_2, tinParts[1]);
            setText(SHAPE_INDEX_PAYOR_TIN_3, tinParts[2]);
            setText(SHAPE_INDEX_PAYOR_TIN_4, tinParts[3]);
        }
        
        setLeftAlignText(SHAPE_INDEX_PAYOR_NAME, report.getSupplier().getName().toUpperCase());
        setLeftAlignText(SHAPE_INDEX_PAYOR_ADDRESS, report.getSupplier().getAddress().toUpperCase());
        
        int currentRow = 31;
        
        BigDecimal month1Total = BigDecimal.ZERO;
        BigDecimal month2Total = BigDecimal.ZERO;
        BigDecimal month3Total = BigDecimal.ZERO;
        for (EwtReportItem item : report.getItems()) {
            switch (getQuarterPosition(item.getReceivingReceipt().getReceivedDate())) {
            case 1:
                month1Total = month1Total.add(item.getInvoiceAmount());
                break;
            case 2:
                month2Total = month2Total.add(item.getInvoiceAmount());
                break;
            case 3:
                month3Total = month3Total.add(item.getInvoiceAmount());
                break;
            }
        }
        
        CellStyle amountStyle = CellStyleBuilder.createStyle(workbook)
                .setAlignment(CellStyle.ALIGN_RIGHT).setAmountFormat(true).setFullBorderThin(true).build();
        
        Row row = sheet.getRow(32);
        Cell cell;
        
        if (!month1Total.equals(BigDecimal.ZERO)) {
            cell = row.getCell(16);
            cell.setCellValue(month1Total.doubleValue());
            cell.setCellStyle(amountStyle);
        }
        
        if (!month2Total.equals(BigDecimal.ZERO)) {
            cell = row.getCell(21);
            cell.setCellValue(month2Total.doubleValue());
            cell.setCellStyle(amountStyle);
        }
        
        if (!month3Total.equals(BigDecimal.ZERO)) {
            cell = row.getCell(26);
            cell.setCellValue(month3Total.doubleValue());
            cell.setCellStyle(amountStyle);
        }

        HSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
        
        return workbook;
    }

    private void setText(int index, String text) {
        HSSFRichTextString string = new HSSFRichTextString(text);
        HSSFSimpleShape shape = (HSSFSimpleShape)shapes.get(index);
        shape.setString(string);
    }
    
    private void setLeftAlignText(int index, String text) {
        HSSFRichTextString string = new HSSFRichTextString("  " + text);
        HSSFSimpleShape shape = (HSSFSimpleShape)shapes.get(index);
        shape.setString(string);
        
        TextObjectRecord textObjRecord = (TextObjectRecord)ReflectionTestUtils.invokeGetterMethod(shape, "getTextObjectRecord");
        textObjRecord.setHorizontalTextAlignment(TextObjectRecord.HORIZONTAL_TEXT_ALIGNMENT_LEFT_ALIGNED);
    }
    
    private boolean isValidTin(String tin) {
        if (!StringUtils.isEmpty(tin)) {
            return TIN_PATTERN.matcher(tin).matches();
        }
        return false;
    }
    
    private String[] getTinParts(String tin) {
        String regex = tin.length() == 12 ? "(?<=\\\\G.{4})" : "-";
        return tin.split(regex);
    }
    
    private int getQuarterPosition(Date date) {
        Calendar calendar = DateUtils.toCalendar(date);
        switch (calendar.get(Calendar.MONTH)) {
        case 0:
        case 3:
        case 6:
        case 9:
            return 1;
        case 1:
        case 4:
        case 7:
        case 10:
            return 2;
        default:
            return 3;
        }
    }
    
}
