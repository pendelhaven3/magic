package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;

import org.springframework.util.StringUtils;

import com.pj.magic.gui.component.MagicButton;
import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.model.search.BadStockReportSearchCriteria;
import com.pj.magic.util.ComponentUtil;

import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

public class SearchBadStockReportsDialog extends MagicDialog {

	private MagicTextField badStockReportNumberField = new MagicTextField();
	private MagicTextField locationField = new MagicTextField();
	private MagicComboBox<String> statusComboBox;
	private UtilCalendarModel postDateFromModel;
    private UtilCalendarModel postDateToModel;
	private MagicButton searchButton;
	private BadStockReportSearchCriteria searchCriteria;
	
	public SearchBadStockReportsDialog() {
		setSize(450, 260);
		setLocationRelativeTo(null);
		setTitle("Search Bad Stock Reports");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));
		
        initializeComponents();
        registerKeyBindings();
        layoutComponents();
	}

	private void initializeComponents() {
		badStockReportNumberField.setNumbersOnly(true);
		badStockReportNumberField.setMaximumLength(10);
		
		statusComboBox = new MagicComboBox<>();
		statusComboBox.setModel(new DefaultComboBoxModel<>(new String[] {"All", "Not Posted", "Posted"}));
		
		postDateFromModel = new UtilCalendarModel();
        postDateToModel = new UtilCalendarModel();
		
		searchButton = new MagicButton("Search");
		searchButton.addActionListener(e -> saveBadStockReportCriteria());
		
		focusOnComponentWhenThisPanelIsDisplayed(badStockReportNumberField);
	}

	private void saveBadStockReportCriteria() {
		searchCriteria = new BadStockReportSearchCriteria();
		
		if (!StringUtils.isEmpty(locationField.getText())) {
			searchCriteria.setLocation(locationField.getText());
		}
		
		if (!StringUtils.isEmpty(badStockReportNumberField.getText())) {
			searchCriteria.setBadStockReportNumber(Long.valueOf(badStockReportNumberField.getText()));
		}
		
		if (statusComboBox.getSelectedIndex() != 0) {
			switch (statusComboBox.getSelectedIndex()) {
			case 1:
				searchCriteria.setPosted(false);
				break;
			case 2:
				searchCriteria.setPosted(true);
				break;
			}
		}
		
		if (postDateFromModel.getValue() != null) {
			searchCriteria.setPostDateFrom(postDateFromModel.getValue().getTime());
		}
		
        if (postDateToModel.getValue() != null) {
            searchCriteria.setPostDateTo(postDateToModel.getValue().getTime());
        }
        
		setVisible(false);
	}

	private void registerKeyBindings() {
		badStockReportNumberField.onEnterKey(() -> statusComboBox.requestFocusInWindow());
		statusComboBox.onEnterKey(() -> searchButton.requestFocusInWindow());
		searchButton.onEnterKey(() -> saveBadStockReportCriteria());
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		// nothing
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(200, "Bad Stock Report No.:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		badStockReportNumberField.setPreferredSize(new Dimension(100, 25));
		add(badStockReportNumberField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(80, "Location:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		locationField.setPreferredSize(new Dimension(150, 25));
		add(locationField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(80, "Status:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusComboBox.setPreferredSize(new Dimension(150, 25));
		add(statusComboBox, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(140, "Post Date From:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createDatePicker(postDateFromModel), c);
		
        currentRow++;
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = currentRow;
        c.anchor = GridBagConstraints.WEST;
        add(ComponentUtil.createLabel(140, "Post Date To:"), c);

        c = new GridBagConstraints();
        c.weightx = 1.0;
        c.gridx = 1;
        c.gridy = currentRow;
        c.anchor = GridBagConstraints.WEST;
        add(ComponentUtil.createDatePicker(postDateToModel), c);
        
		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.top = 10;
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		searchButton.setPreferredSize(new Dimension(100, 25));
		add(searchButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createGlue(), c);
	}
	
	public BadStockReportSearchCriteria getSearchCriteria() {
	    try {
	        return searchCriteria;
	    } finally {
	        searchCriteria = null;
	    }
	}
	
	public void resetDisplay() {
		searchCriteria = null;
		badStockReportNumberField.setText(null);
		locationField.setText(null);
		statusComboBox.setSelectedIndex(0);
		postDateFromModel.setValue(null);
        postDateToModel.setValue(null);
	}
	
}