package com.pj.magic.util;

public class HtmlUtil {

	public static final String html(String text) {
		String body = text.replaceAll("\n", "<br>");
		return new StringBuilder().append("<html>").append(body).append("</html>").toString();
	}
	
	public static final String blueUnderline(String text) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><u><font color=\"blue\">").append(text).append("</font></u><html>");
		return sb.toString();
	}
	
}