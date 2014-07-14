package com.pj.magic.gui;

import java.awt.CardLayout;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.panels.SalesRequisitionPanel;
import com.pj.magic.gui.panels.SalesRequisitionsListPanel;

@Component
public class MagicFrame extends JFrame {
	
	private static final String SALES_REQUISITIONS_LIST_PANEL = "SALES_REQUISITIONS_LIST_PANEL";
	private static final String SALES_REQUISITION_PANEL = "SALES_REQUISITION_PANEL";
	
	@Autowired private SalesRequisitionsListPanel salesRequisitionsListPanel;
	@Autowired private SalesRequisitionPanel salesRequisitionPanel;
	
	private JPanel panelHolder;

	public MagicFrame() {
		this.setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle(ResourceBundle.getBundle("application").getString("application.title"));
	}
	
	@PostConstruct
	private void addContents() {
		panelHolder = new JPanel(new CardLayout());
		panelHolder.add(salesRequisitionPanel, SALES_REQUISITION_PANEL);
		panelHolder.add(salesRequisitionsListPanel, SALES_REQUISITIONS_LIST_PANEL);
        getContentPane().add(panelHolder);
	}
	
	public void switchToSalesRequisitionsListPanel() {
		((CardLayout)panelHolder.getLayout()).show(panelHolder, SALES_REQUISITIONS_LIST_PANEL);
	}
	
}
