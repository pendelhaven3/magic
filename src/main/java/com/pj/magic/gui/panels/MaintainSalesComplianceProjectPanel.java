package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.model.SalesComplianceProject;
import com.pj.magic.service.SalesComplianceService;
import com.pj.magic.util.ComponentUtil;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
@Slf4j
public class MaintainSalesComplianceProjectPanel extends StandardMagicPanel {

	@Autowired private SalesComplianceService salesComplianceService;
	
	private SalesComplianceProject salesComplianceProject;
	
	private MagicTextField nameField;
	private UtilCalendarModel startDateModel;
	private UtilCalendarModel endDateModel;
	private MagicTextField targetAmountField;
	
	private JButton saveButton;
	
	@Override
	protected void initializeComponents() {
		nameField = new MagicTextField();
		nameField.setMaximumLength(50);
		
		startDateModel = new UtilCalendarModel();
		endDateModel = new UtilCalendarModel();
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(e -> saveSalesComplianceProject());
		
		targetAmountField = new MagicTextField();
		targetAmountField.setNumbersOnly(true);
		
		focusOnComponentWhenThisPanelIsDisplayed(nameField);
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(nameField);
		focusOrder.add(targetAmountField);
	}
	
	private void saveSalesComplianceProject() {
		if (!validateSalesComplianceProject()) {
			return;
		}
		
		if (confirm("Save?")) {
			salesComplianceProject.setName(nameField.getText());
			salesComplianceProject.setStartDate(startDateModel.getValue().getTime());
			salesComplianceProject.setEndDate(endDateModel.getValue().getTime());
			salesComplianceProject.setTargetAmount(targetAmountField.getTextAsBigDecimal());
			
			try {
				salesComplianceService.save(salesComplianceProject);
				showMessage("Saved!");
				getMagicFrame().switchToSalesComplianceProjectPanel(salesComplianceProject);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
		}
	}

	private boolean validateSalesComplianceProject() {
		if (StringUtils.isEmpty(nameField.getText())) {
			showErrorMessage("Name must be specified");
			nameField.requestFocusInWindow();
			return false;
		}
		
		if (startDateModel.getValue() == null) {
			showErrorMessage("Start Date From must be specified");
			return false;
		}
		
		if (endDateModel.getValue() == null) {
			showErrorMessage("End Date From must be specified");
			return false;
		}
		
		if (StringUtils.isEmpty(targetAmountField.getText())) {
			showErrorMessage("Target Amount must be specified");
			targetAmountField.requestFocusInWindow();
			return false;
		}
		
		return true;
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.insets.top = 5;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Name: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		nameField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(nameField, c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalGlue(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.top = 5;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "Start Date: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl datePanel = new JDatePanelImpl(startDateModel);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(datePicker, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.top = 5;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "End Date: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		datePanel = new JDatePanelImpl(endDateModel);
		datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(datePicker, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.top = 5;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "Target Amount: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		targetAmountField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(targetAmountField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.top = 25;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.EAST;
		saveButton.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(saveButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createGlue(), c);
	}

	@Override
	protected void registerKeyBindings() {
		nameField.onEnterKey(() -> focusNextField());
	}

	public void updateDisplay(SalesComplianceProject salesComplianceProject) {
		this.salesComplianceProject = salesComplianceProject;
		
		if (salesComplianceProject.getId() == null) {
			clearDisplay();
			return;
		}
		
		nameField.setText(salesComplianceProject.getName());
		
		startDateModel.setValue(null); // set to null first to prevent property change listener from triggering
		startDateModel.setValue(DateUtils.toCalendar(salesComplianceProject.getStartDate()));
		
		endDateModel.setValue(null); // set to null first to prevent property change listener from triggering
		endDateModel.setValue(DateUtils.toCalendar(salesComplianceProject.getEndDate()));
		
		targetAmountField.setText(String.valueOf(salesComplianceProject.getTargetAmount().longValue()));
	}

	private void clearDisplay() {
		nameField.setText(null);
		startDateModel.setValue(null);
		endDateModel.setValue(null);
		targetAmountField.setText(null);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToSalesComplianceProjectsListPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) { }

}
