package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;

import org.springframework.stereotype.Component;

import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class AuditTrailStatusDialog extends MagicDialog {

	private JLabel postDateLabel = new JLabel();
	private JLabel postedByLabel = new JLabel();
	private JLabel cancelDateLabel = new JLabel();
	private JLabel cancelledByLabel = new JLabel();
	
	public AuditTrailStatusDialog() {
		setSize(320, 180);
		setLocationRelativeTo(null);
		getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		initializeComponents();
		layoutMainPanel();
	}

	private void initializeComponents() {
		// always get focus within the dialog so escape key binding can be used
		focusOnComponentWhenThisPanelIsDisplayed(getRootPane()); 
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
	}
	
	private void layoutMainPanel() {
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
	
	public void updateDisplay(ReceivingReceipt receivingReceipt) {
		setTitle("Receiving Receipt Status");
		postDateLabel.setText(receivingReceipt.isPosted() ? FormatterUtil.formatDate(receivingReceipt.getPostDate()) : "-");
		postedByLabel.setText(receivingReceipt.isPosted() ? receivingReceipt.getPostedBy().getUsername() : "-");
		cancelDateLabel.setText(receivingReceipt.isCancelled() ? FormatterUtil.formatDate(receivingReceipt.getCancelDate()) : "-");
		cancelledByLabel.setText(receivingReceipt.isCancelled() ? receivingReceipt.getCancelledBy().getUsername() : "-");
	}

}