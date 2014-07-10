package com.pj.magic.component;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class MagicTextField extends JTextField {

	private int maximumLength;
	
	public MagicTextField() {
		setDocument(new PlainDocument() {
			
			@Override
			public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
				if (str == null) {
					return;
				}
				if (maximumLength > 0) {
					if ((getLength() + str.length()) <= maximumLength) {
						super.insertString(offset, str.toUpperCase(), attr);
					}
				} else {
					super.insertString(offset, str.toUpperCase(), attr);
				}
			}
		});
	}
	
	public void setMaximumLength(int maximumLength) {
		this.maximumLength = maximumLength;
	}
	
}
