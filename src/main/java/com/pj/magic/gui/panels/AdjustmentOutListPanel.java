package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.AdjustmentOutsTable;
import com.pj.magic.model.AdjustmentOut;
import com.pj.magic.service.AdjustmentOutService;
import com.pj.magic.util.ComponentUtil;

@Component
public class AdjustmentOutListPanel extends AbstractMagicPanel implements ActionListener {
	
	private static final String NEW_ADJUSTMENT_OUT_ACTION_NAME = "newAdjustmentOut";
	private static final String NEW_ADJUSTMENT_OUT_ACTION_COMMAND_NAME = "newAdjustmentOut";
	
	@Autowired private AdjustmentOutsTable table;
	@Autowired private AdjustmentOutService AdjustmentOutService;
	
	@Override
	public void initializeComponents() {
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	public void updateDisplay() {
		table.update();
	}

	public void displayAdjustmentOutDetails(AdjustmentOut AdjustmentOut) {
		getMagicFrame().switchToAdjustmentOutPanel(AdjustmentOut);
	}
	
	@Override
	protected void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;
		GridBagConstraints c = new GridBagConstraints();
		
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
		
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, c);
	}
	
	@Override
	protected void registerKeyBindings() {
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), NEW_ADJUSTMENT_OUT_ACTION_NAME);
		getActionMap().put(NEW_ADJUSTMENT_OUT_ACTION_NAME, new AbstractAction() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewAdjustmentOutPanel();
			}
		});		
	}
	
	protected void switchToNewAdjustmentOutPanel() {
		getMagicFrame().switchToAdjustmentOutPanel(new AdjustmentOut());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}
	
	private JToolBar createToolBar() {
		MagicToolBar toolBar = new MagicToolBar();
		addBackButton(toolBar);
		
		JButton postButton = new MagicToolBarButton("plus", "New (F4)");
		postButton.setActionCommand(NEW_ADJUSTMENT_OUT_ACTION_COMMAND_NAME);
		postButton.addActionListener(this);
		
		toolBar.add(postButton);
		return toolBar;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case NEW_ADJUSTMENT_OUT_ACTION_COMMAND_NAME:
			switchToNewAdjustmentOutPanel();
			break;
		}
	}

}
