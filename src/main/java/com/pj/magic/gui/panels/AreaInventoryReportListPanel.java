package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.AreaInventoryReportsTable;
import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.service.AreaInventoryReportService;
import com.pj.magic.util.ComponentUtil;

@Component
public class AreaInventoryReportListPanel extends StandardMagicPanel {
	
	@Autowired private AreaInventoryReportsTable table;
	@Autowired private AreaInventoryReportService AreaInventoryReportService;
	
	@Override
	public void initializeComponents() {
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	public void updateDisplay() {
		table.update();
	}

	public void displayAreaInventoryReportDetails(AreaInventoryReport AreaInventoryReport) {
		getMagicFrame().switchToAreaInventoryReportPanel(AreaInventoryReport);
	}
	
	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		int currentRow = 0;
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 5), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		
		JScrollPane scrollPane = new JScrollPane(table);
		mainPanel.add(scrollPane, c);
	}
	
	@Override
	protected void registerKeyBindings() {
		// none
	}
	
	protected void switchToNewAreaInventoryReportPanel() {
		getMagicFrame().switchToAreaInventoryReportPanel(new AreaInventoryReport());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton addButton = new MagicToolBarButton("plus", "New");
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewAreaInventoryReportPanel();
			}
		});
		toolBar.add(addButton);
	}

}
