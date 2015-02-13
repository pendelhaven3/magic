package com.pj.magic.util;

public class HtmlUtil {

	public static final String blueUnderline(String text) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><u><font color=\"blue\">").append(text).append("</font></u><html>");
		return sb.toString();
	}
	
}