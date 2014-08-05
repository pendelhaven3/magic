package com.pj.magic.model;

import java.util.HashMap;
import java.util.Map;

public class Unit {

	// TODO: Find occurences and replace them
	
	public static final String CASE = "CSE";
	public static final String CARTON = "CTN";
	public static final String DOZEN = "DOZ";
	public static final String PIECES = "PCS";

	private static final Map<String, Integer> compareMap = new HashMap<>();
	
	static {
		compareMap.put(CASE, 4);
		compareMap.put(CARTON, 3);
		compareMap.put(DOZEN, 2);
		compareMap.put(PIECES, 1);
	}
	
	private Unit() {
		// Can never be instantiated. This class is only for the constants.
	}
	
	public static int compare(String unit1, String unit2) {
		int value1 = compareMap.get(unit1);
		int value2 = compareMap.get(unit2);
		
		if (value1 < value2) {
			return -1;
		} else if (value1 > value2) {
			return 1;
		} else {
			return 0;
		}
	}
	
}
