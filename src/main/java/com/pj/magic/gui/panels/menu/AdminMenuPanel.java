package com.pj.magic.gui.panels.menu;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.MagicSubmenuTable;
import com.pj.magic.model.User;
import com.pj.magic.service.LoginService;

@Component
public class AdminMenuPanel extends MenuPanel {

    private static final String USER_LIST = "User List";
    private static final String RESET_PASSWORD = "Reset Password";
	private static final String PAYMENT_TERMINAL_ASSIGNMENTS = "Payment Terminal Assignments";
    private static final String CHANGE_PASSWORD = "Change Password";
    private static final String SALES_COMPLIANCE = "Sales Compliance";
    
    @Autowired
    private LoginService loginService;
    
    private MagicListTable table;
	private MainMenuTableModel tableModel;
	
	@Override
	protected void initializeComponents() {
		tableModel = new MainMenuTableModel();
		table = new MagicSubmenuTable(tableModel);
		
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(JLabel.CENTER);
		table.setDefaultRenderer(Object.class, renderer);

		table.changeSelection(0, 0, false, false);
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		
		table.setTableHeader(null);
		table.setShowGrid(false);
		
		mainPanel.add(new JScrollPane(table), c);
	}

	@Override
	protected void registerKeyBindings() {
		table.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectMenuItem();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectMenuItem();
			}
		});
	}

	public void updateDisplay() {
	    tableModel.setUser(loginService.getLoggedInUser());
		table.changeSelection(0, 0, false, false);
	}
	
	private void selectMenuItem() {
		switch ((String)table.getValueAt(table.getSelectedRow(), 0)) {
		case USER_LIST:
			getMagicFrame().switchToUserListPanel();
			break;
		case RESET_PASSWORD:
			getMagicFrame().switchToResetPasswordPanel();
			break;
		case PAYMENT_TERMINAL_ASSIGNMENTS:
			getMagicFrame().switchToPaymentTerminalAssignmentListPanel();
			break;
        case CHANGE_PASSWORD:
            getMagicFrame().switchToChangePasswordPanel();
            break;
        case SALES_COMPLIANCE:
            getMagicFrame().switchToSalesComplianceProjectsListPanel();
            break;
		}
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}

	private class MainMenuTableModel extends AbstractTableModel {

        private final List<String> allMenuItems = Arrays.asList(
                USER_LIST,
                RESET_PASSWORD,
                PAYMENT_TERMINAL_ASSIGNMENTS,
                CHANGE_PASSWORD,
                SALES_COMPLIANCE
        );
        
		private final List<String> menuItems = new ArrayList<>();
		
		@Override
		public int getRowCount() {
			return menuItems.size();
		}

		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return menuItems.get(rowIndex);
		}
		
        public void setUser(User user) {
            menuItems.clear();
            menuItems.addAll(allMenuItems);
            if (!user.isSupervisor()) {
                menuItems.remove(USER_LIST);
                menuItems.remove(RESET_PASSWORD);
                menuItems.remove(PAYMENT_TERMINAL_ASSIGNMENTS);
                menuItems.remove(SALES_COMPLIANCE);
            }
            fireTableDataChanged();
        }
		
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

}