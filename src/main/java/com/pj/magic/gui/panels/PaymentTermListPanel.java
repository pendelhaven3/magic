package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import com.pj.magic.gui.tables.PaymentTermsTableModel;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.service.PaymentTermService;
import com.pj.magic.util.ComponentUtil;

@Component
public class PaymentTermListPanel extends AbstractMagicPanel implements ActionListener {

	private static final String EDIT_PAYMENT_TERM_ACTION_NAME = "editPaymentTerm";
	private static final String NEW_PAYMENT_TERM_ACTION_NAME = "newPaymentTerm";
	
	@Autowired private PaymentTermService paymentTermService;
	
	private JTable table;
	private PaymentTermsTableModel tableModel = new PaymentTermsTableModel();
	
	public void updateDisplay() {
		List<PaymentTerm> paymentTerms = paymentTermService.getAllPaymentTerms();
		tableModel.setPaymentTerms(paymentTerms);
		if (!paymentTerms.isEmpty()) {
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

		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(1, 5), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(new JScrollPane(table), c);
	}

	private JToolBar createToolBar() {
		JToolBar toolBar = new MagicToolBar();
		
		JButton backButton = new MagicToolBarButton("back", "Back");
		backButton.setActionCommand(BACK_ACTION_COMMAND_NAME);
		backButton.addActionListener(this);
		toolBar.add(backButton);
		
		JButton postButton = new MagicToolBarButton("plus", "New");
		postButton.setActionCommand(NEW_PAYMENT_TERM_ACTION_NAME);
		postButton.addActionListener(this);
		toolBar.add(postButton);
		
		return toolBar;
	}

	@Override
	protected void registerKeyBindings() {
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), EDIT_PAYMENT_TERM_ACTION_NAME);
		table.getActionMap().put(EDIT_PAYMENT_TERM_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectPaymentTerm();
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					selectPaymentTerm();
				}
			}
		});
	}

	protected void selectPaymentTerm() {
		PaymentTerm paymentTerm = tableModel.getPaymentTerm(table.getSelectedRow());
		getMagicFrame().switchToEditPaymentTermPanel(paymentTerm);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case NEW_PAYMENT_TERM_ACTION_NAME:
			switchToNewPaymentTermPanel();
			break;
		case BACK_ACTION_COMMAND_NAME:
			doOnBack();
			break;
		}
	}

	private void switchToNewPaymentTermPanel() {
		getMagicFrame().switchToAddNewPaymentTermPanel();
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}
	
}