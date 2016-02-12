package com.pj.magic.util;

import java.io.File;
import java.nio.file.Paths;

public class FileUtil {

	public static String getDesktopFolderPath() {
		return Paths.get(System.getProperty("user.home"), "Desktop").toAbsolutePath().toString();
	}
	
	public static File getDesktopFolderPathAsFile() {
		return new File(getDesktopFolderPath());
	}
	
}