package com.pj.magic;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;

public class MagicDialog extends JDialog {

	private static final long serialVersionUID = -8193620345831459422L;
	
	private Map<String, String> returnValuesMap = new HashMap<>();
	
	public MagicDialog() {
		setModal(true);
	}
	
	public void setReturnValue(String name, String value) {
		returnValuesMap.put(name, value);
	}
	
	public String getReturnValue(String name) {
		return returnValuesMap.get(name);
	}
	
}
