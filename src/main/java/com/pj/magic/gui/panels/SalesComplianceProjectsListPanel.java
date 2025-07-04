package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.SalesComplianceProject;
import com.pj.magic.service.SalesComplianceService;
import com.pj.magic.util.FormatterUtil;

@Component
public class SalesComplianceProjectsListPanel extends StandardMagicPanel {
	
	private static final int NAME_COLUMN_INDEX = 0;
	private static final int START_DATE_COLUMN_INDEX = 1;
	private static final int END_DATE_COLUMN_INDEX = 2;
	
	@Autowired
	private SalesComplianceService salesComplianceService;
	
	private MagicListTable table;
	private SalesComplianceProjectsTableModel tableModel = new SalesComplianceProjectsTableModel();
	
	public void updateDisplay() {
		tableModel.setItems(salesComplianceService.getAllProjects());
		if (tableModel.hasItems()) {
			table.selectFirstRow();
		}
	}

	@Override
	protected void initializeComponents() {
		table = new MagicListTable(tableModel);
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(5), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(new JScrollPane(table), c);
	}

	@Override
	protected void registerKeyBindings() {
		table.onEnterKeyAndDoubleClick(() -> selectProject());
		
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
	}

	protected void selectProject() {
		SalesComplianceProject project = tableModel.getItem(table.getSelectedRow());
		getMagicFrame().switchToSalesComplianceProjectPanel(project);
	}

	private void switchToNewSalesComplianceProjectPanel() {
		getMagicFrame().switchToCreateNewSalesComplianceProjectPanel();
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToAdminMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton postButton = new MagicToolBarButton("plus", "New", e -> switchToNewSalesComplianceProjectPanel());
		toolBar.add(postButton);
	}

	private class SalesComplianceProjectsTableModel extends ListBackedTableModel<SalesComplianceProject>{

		private final String[] columnNames = {"Name", "Start Date", "End Date"};
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			SalesComplianceProject salesComplianceProject = getItem(rowIndex);
			switch (columnIndex) {
			case NAME_COLUMN_INDEX:
				return salesComplianceProject.getName();
			case START_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(salesComplianceProject.getStartDate());
			case END_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(salesComplianceProject.getEndDate());
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}

		@Override
		protected String[] getColumnNames() {
			return columnNames;
		}

	}
	
}
