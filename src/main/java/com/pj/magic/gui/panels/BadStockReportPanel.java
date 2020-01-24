package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.exception.NoItemException;
import com.pj.magic.gui.MagicFrame;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.BadStockReportItemsTable;
import com.pj.magic.model.BadStockReport;
import com.pj.magic.service.BadStockReportService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
@Slf4j
public class BadStockReportPanel extends StandardMagicPanel {

	@Autowired
	private BadStockReportService badStockReportService;
	
	@Autowired
	private BadStockReportItemsTable itemsTable;
	
	private BadStockReport badStockReport;
	
	private JLabel badStockReportNumberLabel = new JLabel();
	private MagicTextField locationField = new MagicTextField();
	private MagicTextField remarksField = new MagicTextField();
	private JLabel statusLabel = new JLabel();
	private JLabel postDateLabel = new JLabel();
	private JLabel postedByLabel = new JLabel();
	
	private JButton postButton;
	private MagicToolBarButton addItemButton;
	private MagicToolBarButton deleteItemButton;
	
	private UtilCalendarModel receivedDateModel = new UtilCalendarModel();
	private JDatePickerImpl datePicker;
	
	public BadStockReportPanel() {
		setTitle("Inventory Bad Stock Report");
	}
	
	@Override
	protected void initializeComponents() {
		locationField.setMaximumLength(100);
		locationField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveLocation();
			}
		});
		
		remarksField.setMaximumLength(100);
		remarksField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveRemarks();
			}
		});
		
		receivedDateModel.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("value".equals(evt.getPropertyName()) && evt.getOldValue() != null 
						&& evt.getNewValue() != null) {
					badStockReport.setReceivedDate(receivedDateModel.getValue().getTime());
					badStockReportService.save(badStockReport);
				}
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(locationField);
	}

	private void saveLocation() {
		if (StringUtils.isEmpty(locationField.getText())) {
			showErrorMessage("Location must be specified");
			locationField.requestFocusInWindow();
			return;
		}
		
		if (!locationField.getText().equals(badStockReport.getLocation())) {
			badStockReport.setLocation(locationField.getText());
			
			try {
				badStockReportService.save(badStockReport);
			} catch (Exception e) {
				log.error("Unable to save Inventory Bad Stock Report Service", e);
				showMessageForUnexpectedError();
				return;
			}
			
			updateDisplay(badStockReport);
			remarksField.requestFocusInWindow();
		}
	}
	
	private void saveRemarks() {
		if (!remarksField.getText().equals(badStockReport.getRemarks())) {
			badStockReport.setRemarks(remarksField.getText());
			badStockReportService.save(badStockReport);
		}
	}
	
	@Override
	protected void registerKeyBindings() {
		locationField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveLocation();
				remarksField.requestFocusInWindow();
			}
		});
		
		remarksField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveRemarks();
				itemsTable.highlight();
			}
		});
	}

	@Override
	protected void doOnBack() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().back(MagicFrame.BAD_STOCK_REPORT_LIST_PANEL);
	}
	
	public void updateDisplay(BadStockReport badStockReport) {
		if (badStockReport.isNew()) {
			this.badStockReport = badStockReport;
			clearDisplay();
			return;
		}
		
		this.badStockReport = badStockReport = badStockReportService.getBadStockReport(badStockReport.getId());
		
		badStockReportNumberLabel.setText(badStockReport.getBadStockReportNumber().toString());
		locationField.setText(badStockReport.getLocation());
		locationField.setEnabled(!badStockReport.isPosted());
		updateReceivedDateField();
		remarksField.setText(badStockReport.getRemarks());
		remarksField.setEnabled(!badStockReport.isPosted());
		statusLabel.setText(badStockReport.isPosted() ? "Yes" : "No");
		postButton.setEnabled(!badStockReport.isPosted());
		addItemButton.setEnabled(!badStockReport.isPosted());
		deleteItemButton.setEnabled(!badStockReport.isPosted());
		datePicker.getComponents()[1].setVisible(!badStockReport.isPosted());
		
		if (badStockReport.isPosted()) {
			postDateLabel.setText(FormatterUtil.formatDate(badStockReport.getPostDate()));
			postedByLabel.setText(badStockReport.getPostedBy().getUsername());
		}
		
		itemsTable.setBadStockReport(badStockReport);
	}

	private void updateReceivedDateField() {
		if (badStockReport.getReceivedDate() != null) {
			receivedDateModel.setValue(null); // set to null first to prevent property change listener from triggering
			receivedDateModel.setValue(DateUtils.toCalendar(badStockReport.getReceivedDate()));
		}
	}
	
	private void clearDisplay() {
		badStockReportNumberLabel.setText(null);
		statusLabel.setText(null);
		locationField.setText(null);
		locationField.setEnabled(true);
		remarksField.setText(null);
		remarksField.setEnabled(false);
		receivedDateModel.setValue(null); // set to null first to prevent property change listener from triggering
		datePicker.getComponents()[1].setVisible(true);
		postDateLabel.setText(null);
		postedByLabel.setText(null);
		
		itemsTable.setBadStockReport(badStockReport);
		postButton.setEnabled(false);
		addItemButton.setEnabled(false);
		deleteItemButton.setEnabled(false);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createHorizontalStrut(50), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(180, "Inventory BSR No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		badStockReportNumberLabel = ComponentUtil.createLabel(200, "");
		mainPanel.add(badStockReportNumberLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Status:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(statusLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Location:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		locationField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(locationField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Post Date:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(postDateLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Remarks:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(remarksField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Posted By:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(postedByLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Received Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		JDatePanelImpl datePanel = new JDatePanelImpl(receivedDateModel);
		datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(datePicker, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createVerticalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createItemsTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(itemsTable);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);
	}
	
	private JPanel createItemsTableToolBar() {
		JPanel panel = new JPanel();
		
		addItemButton = new MagicToolBarButton("plus_small", "Add Item", true);
		addItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.switchToAddMode();
			}
		});
		panel.add(addItemButton, BorderLayout.WEST);
		
		deleteItemButton = new MagicToolBarButton("minus_small", "Delete Item", true);
		deleteItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteItemButton, BorderLayout.WEST);
		
		return panel;
	}

	private void postBadStockReport() {
		if (itemsTable.isAdding()) {
			itemsTable.switchToEditMode();
		}
		
		if (confirm("Do you want to post this Inventory Bad Stock Report?")) { 
			if (!badStockReport.hasItems()) {
				showErrorMessage("Cannot post an Inventory Bad Stock Report with no items");
				itemsTable.requestFocusInWindow();
				return;
			}
			
			try {
				badStockReportService.post(badStockReport);
			} catch (NoItemException | AlreadyPostedException e) {
				showErrorMessage(e.getMessage());
				updateDisplay(badStockReport);
				return;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during posting!");
				return;
			}
			
			showMessage("Post successful!");
			updateDisplay(badStockReport);
		}
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postBadStockReport();
			}
		});
		toolBar.add(postButton);
	}

}