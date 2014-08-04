package com.pj.magic.util;

import java.io.ByteArrayInputStream;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

public class PrinterUtil {

	private static DocFlavor DOC_FLAVOR = new DocFlavor("application/octet-stream", "java.io.InputStream");
	private static String CARRIAGE_RETURN = "\r";
	private static String FORM_FEED = "\f";
	private static String EPSON_PRINTER = "EPSON LX-300+ /II";

	public static void print(String data) throws PrintException {
		print(data.getBytes());
	}

	public static void print(byte[] data) throws PrintException {
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(DOC_FLAVOR, null);
		PrintService epsonPrintService = null;
		for (PrintService printService : printServices) {
			if (EPSON_PRINTER.equals(printService.getName())) {
				epsonPrintService = printService;
				break;
			}
		}

		if (epsonPrintService == null) {
			throw new RuntimeException(EPSON_PRINTER + "  printer not connected");
		}

		DocPrintJob printJob = epsonPrintService.createPrintJob();
		Doc doc = new SimpleDoc(new ByteArrayInputStream(CARRIAGE_RETURN.getBytes()), DOC_FLAVOR, null);
		printJob.print(doc, null);

		printJob = epsonPrintService.createPrintJob();
		doc = new SimpleDoc(new ByteArrayInputStream(data), DOC_FLAVOR, null);
		printJob.print(doc, null);

		printJob = epsonPrintService.createPrintJob();
		doc = new SimpleDoc(new ByteArrayInputStream(FORM_FEED.getBytes()), DOC_FLAVOR, null);
		printJob.print(doc, null);
	}

}