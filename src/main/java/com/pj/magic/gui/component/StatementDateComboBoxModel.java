package com.pj.magic.gui.component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import com.pj.magic.model.CreditCard;
import com.pj.magic.util.FormatterUtil;

public class StatementDateComboBoxModel extends DefaultComboBoxModel<Date> {

	private static final int OPTIONS_RANGE = 6;

	private StatementDateComboBoxModel() {
	}
	
	public StatementDateComboBoxModel(CreditCard creditCard) {
		super(createStatementDateOptions(creditCard));
	}

	private static Date[] createStatementDateOptions(CreditCard creditCard) {
		List<Date> statementDates = new ArrayList<>();
		statementDates.add(null);
		
		Calendar cal = Calendar.getInstance();
		if (cal.get(Calendar.DATE) > creditCard.getCutoffDate()) {
			cal.add(Calendar.MONTH, 1);
		}
		cal.set(Calendar.DATE, creditCard.getCutoffDate());
		cal.add(Calendar.MONTH, OPTIONS_RANGE * -1);
		
		Date firstOption = cal.getTime();
		statementDates.add(firstOption);
		
		for (int i = 0; i < OPTIONS_RANGE - 1; i++) {
			cal.add(Calendar.MONTH, 1);
			statementDates.add(cal.getTime());
		}
		
		cal.add(Calendar.MONTH, 1);
		Date currentStatementDate = cal.getTime();
		statementDates.add(currentStatementDate);
		
		for (int i = 0; i < OPTIONS_RANGE; i++) {
			cal.add(Calendar.MONTH, 1);
			statementDates.add(cal.getTime());
		}
		
		Date[] returnDates = new Date[statementDates.size()];
		StatementDateComboBoxModel dummy = new StatementDateComboBoxModel();
		for (int i = 0; i < statementDates.size(); i++) {
			if (statementDates.get(i) != null) {
				returnDates[i] = dummy.new StatementDate(statementDates.get(i));
			} else {
				returnDates[i] = null;
			}
		}
		return returnDates;
	}
	
	private class StatementDate extends Date {
		
		public StatementDate(Date date) {
			super(date.getTime());
		}
		
		@Override
		public String toString() {
			return FormatterUtil.formatDate(this);
		}
		
	}
	
}
