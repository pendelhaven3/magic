package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.SuppliersTableModel;
import com.pj.magic.model.Supplier;
import com.pj.magic.service.SupplierService;
import com.pj.magic.util.ComponentUtil;

@Component
public class SupplierListPanel extends AbstractMagicPanel implements ActionListener {

	private static final String EDIT_SUPPLIER_ACTION_NAME = "editSupplier";
	private static final String NEW_SUPPLIER_ACTION_COMMAND = "newSupplier";
	
	@Autowired private SupplierService supplierService;
	
	private JTable table;
	private SuppliersTableModel tableModel = new SuppliersTableModel();
	
	public void updateDisplay() {
		List<Supplier> suppliers = supplierService.getAllSuppliers();
		tableModel.setSuppliers(suppliers);
		if (!suppliers.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
	}

	@Override
	protected void initializeComponents() {
		table = new JTable(tableModel);
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	@Override
	protected void layoutComponents() {
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(createToolBar(), c);

		currentRow++; // first row
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(1, 5), c);
		
		currentRow++; // first row
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(new JScrollPane(table), c);
	}

	private JToolBar createToolBar() {
		JToolBar toolBar = new MagicToolBar();
		
		JButton postButton = new MagicToolBarButton("plus", "New");
		postButton.setActionCommand(NEW_SUPPLIER_ACTION_COMMAND);
		postButton.addActionListener(this);
		
		toolBar.add(postButton);
		return toolBar;
	}

	@Override
	protected void registerKeyBindings() {
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), EDIT_SUPPLIER_ACTION_NAME);
		table.getActionMap().put(EDIT_SUPPLIER_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Supplier supplier = tableModel.getSupplier(table.getSelectedRow());
				getMagicFrame().switchToEditSupplierPanel(supplier);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case NEW_SUPPLIER_ACTION_COMMAND:
			switchToNewSupplierPanel();
			break;
		}
	}

	private void switchToNewSupplierPanel() {
		getMagicFrame().switchToAddNewSupplierPanel();
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}
	
}
