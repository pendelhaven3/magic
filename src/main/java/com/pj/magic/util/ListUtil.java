package com.pj.magic.util;

import java.util.Collections;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

public class ListUtil {

	public static <T extends Comparable<T>> List<T> asSortedList(List<T> list) {
		Collections.sort(list);
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <T> DefaultComboBoxModel<T> toDefaultComboBoxModel(List<T> list) {
		DefaultComboBoxModel<T> model = new DefaultComboBoxModel<>((T[])list.toArray());
		return model;
	}
	
}