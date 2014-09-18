package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.pj.magic.gui.component.MagicToolBar;

public abstract class StandardMagicPanel extends AbstractMagicPanel {

	@PostConstruct
	@Override
	public void initialize() {
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		initializeComponents();
		registerKeyBindings();
		registerBackKeyBinding();
		createStandardLayout();
	}
	
	private void createStandardLayout() {
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		add(createToolBar(), c);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		add(createMainPanel(), c);
	}

	private JPanel createMainPanel() {
		JPanel panel = new JPanel();
		layoutMainPanel(panel);
		return panel;
	}

	protected abstract void layoutMainPanel(JPanel panel);

	private MagicToolBar createToolBar() {
		MagicToolBar toolBar = new MagicToolBar();
		addBackButton(toolBar);
		addToolBarButtons(toolBar);
		addUsernameFieldAndLogoutButton(toolBar);
		return toolBar;
	}

	/**
	 * Add panel-specific toolbar content here
	 * 
	 * @param toolBar
	 */
	protected abstract void addToolBarButtons(MagicToolBar toolBar);

	// TODO: Remove this
	@Override
	protected final void layoutComponents() {
		// use layoutMainPanel instead
	}
	
}
