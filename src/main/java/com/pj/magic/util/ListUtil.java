package com.pj.magic.util;

import java.util.Collections;
import java.util.List;

public class ListUtil {

	public static <T extends Comparable<T>> List<T> asSortedList(List<T> list) {
		Collections.sort(list);
		return list;
	}
	
}