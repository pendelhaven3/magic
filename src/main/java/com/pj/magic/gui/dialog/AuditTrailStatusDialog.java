package com.pj.magic.gui.dialog;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class AuditTrailStatusDialog extends MagicDialog {

	private JLabel postDateLabel = new JLabel();
	private JLabel postedByLabel = new JLabel();
	private JLabel cancelDateLabel = new JLabel();
	private JLabel cancelledByLabel = new JLabel();
	private JLabel paidDateLabel = new JLabel();
	private JLabel paidByLabel = new JLabel();
	private boolean showPaidFields;
	private boolean showCancelFields;
	
	public AuditTrailStatusDialog() {
		setSize(320, 180);
		setLocationRelativeTo(null);
		getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		initializeComponents();
	}

	private void initializeComponents() {
		// always get focus within the dialog so escape key binding can be used
		focusOnComponentWhenThisPanelIsDisplayed(getRootPane()); 
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
	}
	
	private void layoutMainPanel() {
		Container mainPanel = getContentPane();
		mainPanel.removeAll();
		
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(120, "Post Date:"), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		postDateLabel.setPreferredSize(new Dimension(150, 20));
		add(postDateLabel, c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		add(Box.createGlue(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(120, "Posted By:"), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		postedByLabel.setPreferredSize(new Dimension(150, 20));
		add(postedByLabel, c);
		
		if (showPaidFields) {
			currentRow++;
			
			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(ComponentUtil.createLabel(120, "Paid Date:"), c);

			c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(paidDateLabel, c);
			
			currentRow++;
			
			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(ComponentUtil.createLabel(120, "Paid By:"), c);

			c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(paidByLabel, c);
		}
		
		if (showCancelFields) {
			currentRow++;
			
			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(ComponentUtil.createLabel(120, "Cancel Date:"), c);

			c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			cancelDateLabel.setPreferredSize(new Dimension(150, 20));
			add(cancelDateLabel, c);
			
			currentRow++;
			
			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			add(ComponentUtil.createLabel(120, "Cancelled By:"), c);

			c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = currentRow;
			c.anchor = GridBagConstraints.WEST;
			cancelledByLabel.setPreferredSize(new Dimension(150, 20));
			add(cancelledByLabel, c);
		}
		
		mainPanel.repaint();
		mainPanel.revalidate();
	}
	
	public void updateDisplay(ReceivingReceipt receivingReceipt) {
		setTitle("Receiving Receipt Status");
		showPaidFields = false;
		showCancelFields = true;
		layoutMainPanel();
		
		postDateLabel.setText(receivingReceipt.isPosted() ? 
				FormatterUtil.formatDateTime(receivingReceipt.getPostDate()) : "-");
		postedByLabel.setText(receivingReceipt.isPosted() ? 
				receivingReceipt.getPostedBy().getUsername() : "-");
		cancelDateLabel.setText(receivingReceipt.isCancelled() ? 
				FormatterUtil.formatDate(receivingReceipt.getCancelDate()) : "-");
		cancelledByLabel.setText(receivingReceipt.isCancelled() ? 
				receivingReceipt.getCancelledBy().getUsername() : "-");
	}

	public void updateDisplay(SalesReturn salesReturn) {
		setTitle("Sales Return Status");
		showPaidFields = true;
		showCancelFields = false;
		layoutMainPanel();
		
		postDateLabel.setText(salesReturn.isPosted() ? 
				FormatterUtil.formatDateTime(salesReturn.getPostDate()) : "-");
		postedByLabel.setText(salesReturn.isPosted() ? 
				salesReturn.getPostedBy().getUsername() : "-");
		paidDateLabel.setText(salesReturn.isPaid() ? 
				FormatterUtil.formatDateTime(salesReturn.getPaidDate()) : "-");
		paidByLabel.setText(salesReturn.isPaid() ? 
				salesReturn.getPaidBy().getUsername() : "-");
	}

}