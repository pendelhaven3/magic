package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SearchSalesReturnsDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.SalesReturnsTableModel;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.search.SalesReturnSearchCriteria;
import com.pj.magic.service.SalesReturnService;
import com.pj.magic.util.ComponentUtil;

@Component
public class SalesReturnListPanel extends StandardMagicPanel {

	@Autowired private SalesReturnService salesReturnService;
	@Autowired private SearchSalesReturnsDialog searchSalesReturnsDialog;
	
	private MagicListTable table;
	private SalesReturnsTableModel tableModel = new SalesReturnsTableModel();
	
	public void updateDisplay() {
		List<SalesReturn> salesReturns = salesReturnService.getUnpaidSalesReturns();
		tableModel.setSalesReturns(salesReturns);
		if (!salesReturns.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
		searchSalesReturnsDialog.updateDisplay();
	}

	@Override
	protected void initializeComponents() {
		table = new MagicListTable(tableModel);
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;
		
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
		mainPanel.add(new JScrollPane(table), c);
	}

	@Override
	protected void registerKeyBindings() {
		table.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectSalesReturn();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectSalesReturn();
			}
			
		});
	}

	protected void selectSalesReturn() {
		SalesReturn salesReturn = tableModel.getSalesReturn(table.getSelectedRow());
		getMagicFrame().switchToSalesReturnPanel(salesReturn);
	}

	private void switchToNewSalesReturnPanel() {
		getMagicFrame().switchToSalesReturnPanel(new SalesReturn());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToSalesMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton postButton = new MagicToolBarButton("plus", "New");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewSalesReturnPanel();
			}
		});
		toolBar.add(postButton);
		
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchSalesReturns();
			}
		});
		
		toolBar.add(searchButton);
	}
	
	private void searchSalesReturns() {
		searchSalesReturnsDialog.setVisible(true);
		
		SalesReturnSearchCriteria criteria = searchSalesReturnsDialog.getSearchCriteria();
		if (criteria != null) {
			List<SalesReturn> salesReturns = salesReturnService.search(criteria);
			tableModel.setSalesReturns(salesReturns);
			if (!salesReturns.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}
	
}